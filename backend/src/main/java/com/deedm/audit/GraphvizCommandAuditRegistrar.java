package com.deedm.audit;

import com.deedm.legacy.util.GraphvizUtil;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Component
public class GraphvizCommandAuditRegistrar {

    private static final Logger logger = LoggerFactory.getLogger(GraphvizCommandAuditRegistrar.class);
    private static final int MAX_ERROR_LENGTH = 500;

    private final AuditPersistenceService auditPersistenceService;
    private final ClientIdentityResolver clientIdentityResolver;

    @Value("${audit.enabled:true}")
    private boolean auditEnabled;

    @Value("${spring.datasource.username:unknown}")
    private String dbUser;

    public GraphvizCommandAuditRegistrar(AuditPersistenceService auditPersistenceService,
                                         ClientIdentityResolver clientIdentityResolver) {
        this.auditPersistenceService = auditPersistenceService;
        this.clientIdentityResolver = clientIdentityResolver;
    }

    @PostConstruct
    public void registerHook() {
        GraphvizUtil.setCommandAuditHook(this::recordCommandExecution);
    }

    private void recordCommandExecution(String command, String args, int exitCode, long durationMs, String error) {
        if (!auditEnabled) {
            return;
        }
        HttpServletRequest request = getCurrentRequest();
        String requestPath = request == null ? "/internal/graphviz" : request.getRequestURI();
        String method = request == null ? "INTERNAL" : request.getMethod();
        String visitorId = request == null ? "system" : clientIdentityResolver.resolveVisitorId(request);
        String clientIp = request == null ? "127.0.0.1" : clientIdentityResolver.resolveClientIp(request);
        String userAgent = request == null ? "scheduler" : clientIdentityResolver.resolveUserAgent(request);
        boolean success = exitCode == 0;
        String commandArgs = args == null ? "" : args;
        Integer payloadSize = commandArgs.getBytes(StandardCharsets.UTF_8).length;

        AuditRecord record = new AuditRecord(
            LocalDateTime.now(),
            "CMD_EXEC",
            success ? "INFO" : "WARN",
            command,
            requestPath,
            method,
            visitorId,
            clientIp,
            userAgent,
            dbUser,
            null,
            durationMs,
            sha256Hex(commandArgs),
            payloadSize,
            command,
            null,
            null,
            success,
            truncateError(error == null ? null : "exitCode=" + exitCode + "; " + error)
        );

        auditPersistenceService.persist(record);
        logger.info(
            "AUDIT eventType=CMD_EXEC severity={} command={} requestPath={} method={} ip={} success={} durationMs={} error={}",
            record.getSeverity(),
            command,
            requestPath,
            method,
            clientIp,
            success,
            durationMs,
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

    private String truncateError(String rawError) {
        if (rawError == null) {
            return null;
        }
        if (rawError.length() <= MAX_ERROR_LENGTH) {
            return rawError;
        }
        return rawError.substring(0, MAX_ERROR_LENGTH);
    }

    private String sha256Hex(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(raw.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            return null;
        }
    }
}
