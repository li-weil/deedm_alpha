package com.deedm.audit;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Set;

@Aspect
@Component
public class ApiAuditAspect {

    private static final Logger logger = LoggerFactory.getLogger(ApiAuditAspect.class);
    private static final int MAX_ERROR_LENGTH = 500;
    private static final Set<String> WRITE_METHODS = Set.of("POST", "PUT", "DELETE");

    private final AuditPersistenceService auditPersistenceService;
    private final ClientIdentityResolver clientIdentityResolver;

    @Value("${audit.enabled:true}")
    private boolean auditEnabled;

    @Value("${spring.datasource.username:unknown}")
    private String dbUser;

    public ApiAuditAspect(AuditPersistenceService auditPersistenceService,
                          ClientIdentityResolver clientIdentityResolver) {
        this.auditPersistenceService = auditPersistenceService;
        this.clientIdentityResolver = clientIdentityResolver;
    }

    @Around("execution(public * com.deedm.controller..*(..))")
    public Object auditWriteApi(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = getCurrentRequest();
        if (!auditEnabled || request == null || !isWriteMethod(request.getMethod())) {
            return joinPoint.proceed();
        }

        LocalDateTime createdAt = LocalDateTime.now();
        String action = joinPoint.getSignature().toShortString();
        String requestPath = buildRequestPath(request);
        String method = request.getMethod();
        String visitorId = clientIdentityResolver.resolveVisitorId(request);
        String clientIp = clientIdentityResolver.resolveClientIp(request);
        String userAgent = clientIdentityResolver.resolveUserAgent(request);

        boolean success = false;
        String errorMessage = null;

        try {
            Object result = joinPoint.proceed();
            success = true;
            return result;
        } catch (Throwable throwable) {
            errorMessage = buildErrorMessage(throwable);
            throw throwable;
        } finally {
            AuditRecord record = new AuditRecord(
                createdAt,
                action,
                requestPath,
                method,
                visitorId,
                clientIp,
                userAgent,
                dbUser,
                success,
                errorMessage
            );

            auditPersistenceService.persist(record);
            logger.info(
                "AUDIT action={} method={} path={} visitorId={} ip={} dbUser={} success={} error={}",
                action,
                method,
                requestPath,
                visitorId,
                clientIp,
                dbUser,
                success,
                errorMessage == null ? "-" : errorMessage
            );
        }
    }

    private HttpServletRequest getCurrentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest();
        }
        return null;
    }

    private boolean isWriteMethod(String method) {
        return method != null && WRITE_METHODS.contains(method.toUpperCase());
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
}
