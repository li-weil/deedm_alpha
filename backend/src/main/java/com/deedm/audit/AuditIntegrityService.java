package com.deedm.audit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.StringJoiner;

@Service
public class AuditIntegrityService {

    private final String integritySecret;

    public AuditIntegrityService(@Value("${audit.integrity.secret:}") String integritySecret) {
        this.integritySecret = integritySecret == null ? "" : integritySecret;
    }

    public String computeRecordHash(AuditRecord record) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(integritySecret.getBytes(StandardCharsets.UTF_8));
            digest.update(buildCanonicalRecord(record).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }

    private String buildCanonicalRecord(AuditRecord record) {
        return new StringJoiner("|")
            .add(value(record.getCreatedAt()))
            .add(value(record.getEventType()))
            .add(value(record.getSeverity()))
            .add(value(record.getAction()))
            .add(value(record.getRequestPath()))
            .add(value(record.getHttpMethod()))
            .add(value(record.getVisitorId()))
            .add(value(record.getClientIp()))
            .add(value(record.getUserAgent()))
            .add(value(record.getDbUser()))
            .add(value(record.getStatusCode()))
            .add(value(record.getDurationMs()))
            .add(value(record.getPayloadDigest()))
            .add(value(record.getPayloadSize()))
            .add(value(record.getResourceKey()))
            .add(value(record.getThreshold()))
            .add(Boolean.toString(record.isSuccess()))
            .add(value(record.getErrorMessage()))
            .toString();
    }

    private String value(Object value) {
        return value == null ? "" : value.toString();
    }
}
