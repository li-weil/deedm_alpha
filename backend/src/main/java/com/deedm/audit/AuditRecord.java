package com.deedm.audit;

import java.time.LocalDateTime;

public class AuditRecord {
    private final LocalDateTime createdAt;
    private final String action;
    private final String requestPath;
    private final String httpMethod;
    private final String visitorId;
    private final String clientIp;
    private final String userAgent;
    private final String dbUser;
    private final boolean success;
    private final String errorMessage;

    public AuditRecord(LocalDateTime createdAt,
                       String action,
                       String requestPath,
                       String httpMethod,
                       String visitorId,
                       String clientIp,
                       String userAgent,
                       String dbUser,
                       boolean success,
                       String errorMessage) {
        this.createdAt = createdAt;
        this.action = action;
        this.requestPath = requestPath;
        this.httpMethod = httpMethod;
        this.visitorId = visitorId;
        this.clientIp = clientIp;
        this.userAgent = userAgent;
        this.dbUser = dbUser;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getAction() {
        return action;
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

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
