package com.deedm.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class ApiAuditAspect {

    private static final Logger logger = LoggerFactory.getLogger(ApiAuditAspect.class);
    private static final int MAX_ERROR_LENGTH = 500;
    private static final Set<String> WRITE_METHODS = Set.of("POST", "PUT", "DELETE");
    private static final Set<String> FILTERED_ARG_TYPES = Set.of(
        "org.springframework.validation.BindingResult",
        "org.springframework.ui.Model",
        "org.springframework.web.servlet.mvc.support.RedirectAttributes",
        "jakarta.servlet.http.HttpServletRequest",
        "jakarta.servlet.http.HttpServletResponse"
    );

    private final AuditPersistenceService auditPersistenceService;
    private final ClientIdentityResolver clientIdentityResolver;
    private final RequestAbuseDetector requestAbuseDetector;
    private final ObjectMapper objectMapper;

    @Value("${audit.enabled:true}")
    private boolean auditEnabled;

    @Value("${audit.abuse.enabled:true}")
    private boolean abuseDetectionEnabled;

    @Value("${spring.datasource.username:unknown}")
    private String dbUser;

    public ApiAuditAspect(AuditPersistenceService auditPersistenceService,
                          ClientIdentityResolver clientIdentityResolver,
                          RequestAbuseDetector requestAbuseDetector,
                          ObjectMapper objectMapper) {
        this.auditPersistenceService = auditPersistenceService;
        this.clientIdentityResolver = clientIdentityResolver;
        this.requestAbuseDetector = requestAbuseDetector;
        this.objectMapper = objectMapper;
    }

    @Around("execution(public * com.deedm.controller..*(..))")
    public Object auditControllerApi(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = getCurrentRequest();
        if (!auditEnabled || request == null || !shouldAudit(request)) {
            return joinPoint.proceed();
        }

        LocalDateTime createdAt = LocalDateTime.now();
        long startNano = System.nanoTime();
        String action = joinPoint.getSignature().toShortString();
        String requestPath = buildRequestPath(request);
        String method = request.getMethod();
        String visitorId = clientIdentityResolver.resolveVisitorId(request);
        String clientIp = clientIdentityResolver.resolveClientIp(request);
        String userAgent = clientIdentityResolver.resolveUserAgent(request);
        String resourceKey = clientIp + "|" + request.getRequestURI();
        PayloadSummary payloadSummary = buildPayloadSummary(joinPoint.getArgs());

        Integer statusCode = 500;
        boolean success = false;
        String errorMessage = null;
        String eventType = resolveEventType(method, request.getRequestURI(), 200, false);
        String severity = "INFO";

        try {
            Object result = joinPoint.proceed();
            statusCode = resolveStatusCode(result);
            success = statusCode < 400;
            eventType = resolveEventType(method, request.getRequestURI(), statusCode, false);
            severity = resolveSeverity(eventType, statusCode, false);
            return result;
        } catch (Throwable throwable) {
            statusCode = resolveStatusCode(throwable);
            errorMessage = buildErrorMessage(throwable);
            success = false;
            eventType = resolveEventType(method, request.getRequestURI(), statusCode, true);
            severity = resolveSeverity(eventType, statusCode, true);
            throw throwable;
        } finally {
            long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNano);
            AuditRecord record = new AuditRecord(
                createdAt,
                eventType,
                severity,
                action,
                requestPath,
                method,
                visitorId,
                clientIp,
                userAgent,
                dbUser,
                statusCode,
                durationMs,
                payloadSummary.digest(),
                payloadSummary.size(),
                resourceKey,
                null,
                null,
                success,
                errorMessage
            );
            auditPersistenceService.persist(record);
            logAuditRecord(record);
            recordAbuseEventIfNeeded(record);
        }
    }

    private void recordAbuseEventIfNeeded(AuditRecord sourceRecord) {
        if (!abuseDetectionEnabled || !isHighRiskPath(sourceRecord.getRequestPath())) {
            return;
        }

        RequestAbuseDetector.AbuseResult abuseResult = requestAbuseDetector.recordAndEvaluate(sourceRecord.getResourceKey());
        if (!abuseResult.triggered()) {
            return;
        }

        AuditRecord abuseRecord = new AuditRecord(
            LocalDateTime.now(),
            "RESOURCE_ABUSE",
            "WARN",
            "ApiAuditAspect.recordAbuseEventIfNeeded",
            sourceRecord.getRequestPath(),
            sourceRecord.getHttpMethod(),
            sourceRecord.getVisitorId(),
            sourceRecord.getClientIp(),
            sourceRecord.getUserAgent(),
            sourceRecord.getDbUser(),
            sourceRecord.getStatusCode(),
            sourceRecord.getDurationMs(),
            sourceRecord.getPayloadDigest(),
            sourceRecord.getPayloadSize(),
            sourceRecord.getResourceKey(),
            abuseResult.threshold(),
            null,
            false,
            "request-count=" + abuseResult.count()
        );
        auditPersistenceService.persist(abuseRecord);
        logAuditRecord(abuseRecord);
    }

    private void logAuditRecord(AuditRecord record) {
        logger.info(
            "AUDIT eventType={} severity={} action={} method={} path={} status={} durationMs={} visitorId={} ip={} success={} threshold={} error={}",
            record.getEventType(),
            record.getSeverity(),
            record.getAction(),
            record.getHttpMethod(),
            record.getRequestPath(),
            record.getStatusCode(),
            record.getDurationMs(),
            record.getVisitorId(),
            record.getClientIp(),
            record.isSuccess(),
            record.getThreshold(),
            record.getErrorMessage() == null ? "-" : record.getErrorMessage()
        );
    }

    private HttpServletRequest getCurrentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest();
        }
        return null;
    }

    private boolean shouldAudit(HttpServletRequest request) {
        String method = request.getMethod();
        if (method == null) {
            return false;
        }
        if (WRITE_METHODS.contains(method.toUpperCase())) {
            return true;
        }
        return "GET".equalsIgnoreCase(method) && isHighRiskPath(request.getRequestURI());
    }

    private boolean isHighRiskPath(String path) {
        if (path == null) {
            return false;
        }
        return path.contains("/hasse-diagram/")
            || path.contains("/graph-image/")
            || path.contains("/tree-image/")
            || path.contains("/relation-image/")
            || path.contains("/function-image/")
            || path.contains("/ast-image/")
            || path.contains("/cleanup/status");
    }

    private String resolveEventType(String method, String path, int statusCode, boolean hasException) {
        if (hasException) {
            return "APP_ERROR";
        }
        if (statusCode >= 500) {
            return "APP_ERROR";
        }
        if ("GET".equalsIgnoreCase(method) && isHighRiskPath(path) && statusCode >= 400) {
            return "FILE_READ_DENY";
        }
        if ("POST".equalsIgnoreCase(method) && path != null && path.endsWith("/cleanup/data")) {
            return "FILE_DELETE";
        }
        if ("GET".equalsIgnoreCase(method) && isHighRiskPath(path)) {
            return "API_READ_HIGH_RISK";
        }
        return "API_WRITE";
    }

    private String resolveSeverity(String eventType, int statusCode, boolean hasException) {
        if (hasException || "APP_ERROR".equals(eventType)) {
            return "ERROR";
        }
        if ("FILE_READ_DENY".equals(eventType) || "RESOURCE_ABUSE".equals(eventType)) {
            return "WARN";
        }
        if (statusCode >= 500) {
            return "ERROR";
        }
        if (statusCode >= 400) {
            return "WARN";
        }
        return "INFO";
    }

    private PayloadSummary buildPayloadSummary(Object[] args) {
        if (args == null || args.length == 0) {
            return new PayloadSummary(null, 0);
        }
        Object[] filtered = Arrays.stream(args)
            .filter(arg -> arg != null && !shouldFilterArg(arg))
            .toArray();
        if (filtered.length == 0) {
            return new PayloadSummary(null, 0);
        }

        String payload = safeSerialize(filtered.length == 1 ? filtered[0] : filtered);
        if (payload == null || payload.isBlank()) {
            return new PayloadSummary(null, 0);
        }

        byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);
        return new PayloadSummary(sha256Hex(payloadBytes), payloadBytes.length);
    }

    private boolean shouldFilterArg(Object arg) {
        String type = arg.getClass().getName();
        if (FILTERED_ARG_TYPES.contains(type)) {
            return true;
        }
        return type.startsWith("org.springframework.web.multipart");
    }

    private String safeSerialize(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ignored) {
            return value.toString();
        }
    }

    private String sha256Hex(byte[] payload) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(payload));
        } catch (Exception e) {
            return null;
        }
    }

    private Integer resolveStatusCode(Object result) {
        if (result instanceof ResponseEntity<?> responseEntity) {
            return responseEntity.getStatusCode().value();
        }
        return 200;
    }

    private Integer resolveStatusCode(Throwable throwable) {
        if (throwable instanceof ResponseStatusException responseStatusException) {
            return responseStatusException.getStatusCode().value();
        }
        return 500;
    }

    private String buildRequestPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        String query = request.getQueryString();
        if (query == null || query.isBlank()) {
            return path;
        }
        return path + "?" + query;
    }

    private String buildErrorMessage(Throwable throwable) {
        String message = throwable.getClass().getSimpleName() + ": " + (throwable.getMessage() == null ? "" : throwable.getMessage());
        if (message.length() > MAX_ERROR_LENGTH) {
            return message.substring(0, MAX_ERROR_LENGTH);
        }
        return message;
    }

    private record PayloadSummary(String digest, Integer size) {
    }
}
