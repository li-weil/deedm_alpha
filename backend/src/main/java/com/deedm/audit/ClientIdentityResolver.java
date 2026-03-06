package com.deedm.audit;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class ClientIdentityResolver {

    private static final int MAX_VISITOR_ID_LENGTH = 128;
    private static final int MAX_USER_AGENT_LENGTH = 512;

    public String resolveVisitorId(HttpServletRequest request) {
        String visitorId = normalizeToken(request.getHeader("X-Visitor-Id"), MAX_VISITOR_ID_LENGTH);
        if (!visitorId.isBlank()) {
            return visitorId;
        }

        String fallback = resolveClientIp(request) + "|" + normalizeToken(request.getHeader("User-Agent"), 128);
        return "anon-" + Integer.toHexString(fallback.hashCode());
    }

    public String resolveClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            String firstIp = xForwardedFor.split(",")[0].trim();
            String normalized = normalizeToken(firstIp, 128);
            if (!normalized.isBlank()) {
                return normalized;
            }
        }

        String remoteAddr = normalizeToken(request.getRemoteAddr(), 128);
        return remoteAddr.isBlank() ? "unknown" : remoteAddr;
    }

    public String resolveUserAgent(HttpServletRequest request) {
        String userAgent = normalizeToken(request.getHeader("User-Agent"), MAX_USER_AGENT_LENGTH);
        return userAgent.isBlank() ? "unknown" : userAgent;
    }

    private String normalizeToken(String raw, int maxLength) {
        if (raw == null) {
            return "";
        }
        String normalized = raw.replaceAll("[^A-Za-z0-9._:\\- /]", "_").trim();
        if (normalized.length() > maxLength) {
            return normalized.substring(0, maxLength);
        }
        return normalized;
    }
}
