package com.deedm.audit;

import java.time.LocalDateTime;

public class AuditRecord {
    private final LocalDateTime createdAt;
    private final String eventType;
    private final String severity;
    private final String action;
    private final String requestPath;
    private final String httpMethod;
    private final String visitorId;
    private final String clientIp;
    private final String userAgent;
    private final String dbUser;
    private final Integer statusCode;
    private final Long durationMs;
    private final String payloadDigest;
    private final Integer payloadSize;
    private final String resourceKey;
    private final Integer threshold;
    private final boolean success;
    private final String errorMessage;

    public AuditRecord(LocalDateTime createdAt,
                       String eventType,
                       String severity,
                       String action,
                       String requestPath,
                       String httpMethod,
                       String visitorId,
                       String clientIp,
                       String userAgent,
                       String dbUser,
                       Integer statusCode,
                       Long durationMs,
                       String payloadDigest,
                       Integer payloadSize,
                       String resourceKey,
                       Integer threshold,
                       boolean success,
                       String errorMessage) {
        this.createdAt = createdAt;
        this.eventType = eventType;
        this.severity = severity;
        this.action = action;
        this.requestPath = requestPath;
        this.httpMethod = httpMethod;
        this.visitorId = visitorId;
        this.clientIp = clientIp;
        this.userAgent = userAgent;
        this.dbUser = dbUser;
        this.statusCode = statusCode;
        this.durationMs = durationMs;
        this.payloadDigest = payloadDigest;
        this.payloadSize = payloadSize;
        this.resourceKey = resourceKey;
        this.threshold = threshold;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getAction() {
        return action;
    }

    public String getEventType() {
        return eventType;
    }

    public String getSeverity() {
        return severity;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getVisitorId() {
        return visitorId;
    }

    public String getClientIp() {
        return clientIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getDbUser() {
        return dbUser;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public String getPayloadDigest() {
        return payloadDigest;
    }

    public Integer getPayloadSize() {
        return payloadSize;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
