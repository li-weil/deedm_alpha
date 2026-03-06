package com.deedm.audit;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.sql.Timestamp;

@Service
public class AuditPersistenceService {

    private static final Logger logger = LoggerFactory.getLogger(AuditPersistenceService.class);

    private static final String CREATE_AUDIT_TABLE_SQL = """
        CREATE TABLE IF NOT EXISTS audit_log (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,
            created_at TIMESTAMP NOT NULL,
            event_type VARCHAR(64),
            severity VARCHAR(16),
            action VARCHAR(255) NOT NULL,
            request_path VARCHAR(512),
            http_method VARCHAR(16),
            visitor_id VARCHAR(128),
            client_ip VARCHAR(128),
            user_agent VARCHAR(512),
            db_user VARCHAR(64),
            status_code INT,
            duration_ms BIGINT,
            payload_digest VARCHAR(128),
            payload_size INT,
            resource_key VARCHAR(512),
            threshold INT,
            success BOOLEAN,
            error_message VARCHAR(1000)
        )
        """;

    private static final String[] UPGRADE_SQLS = {
        "ALTER TABLE audit_log ADD COLUMN IF NOT EXISTS event_type VARCHAR(64)",
        "ALTER TABLE audit_log ADD COLUMN IF NOT EXISTS severity VARCHAR(16)",
        "ALTER TABLE audit_log ADD COLUMN IF NOT EXISTS status_code INT",
        "ALTER TABLE audit_log ADD COLUMN IF NOT EXISTS duration_ms BIGINT",
        "ALTER TABLE audit_log ADD COLUMN IF NOT EXISTS payload_digest VARCHAR(128)",
        "ALTER TABLE audit_log ADD COLUMN IF NOT EXISTS payload_size INT",
        "ALTER TABLE audit_log ADD COLUMN IF NOT EXISTS resource_key VARCHAR(512)",
        "ALTER TABLE audit_log ADD COLUMN IF NOT EXISTS threshold INT"
    };

    private final JdbcTemplate jdbcTemplate;

    public AuditPersistenceService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void initAuditTable() {
        try {
            jdbcTemplate.execute(CREATE_AUDIT_TABLE_SQL);
            for (String sql : UPGRADE_SQLS) {
                jdbcTemplate.execute(sql);
            }
        } catch (Exception e) {
            logger.error("Failed to initialize audit_log table", e);
        }
    }

    public void persist(AuditRecord record) {
        try {
            jdbcTemplate.update(
                "INSERT INTO audit_log (created_at, event_type, severity, action, request_path, http_method, visitor_id, client_ip, user_agent, db_user, status_code, duration_ms, payload_digest, payload_size, resource_key, threshold, success, error_message) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                Timestamp.valueOf(record.getCreatedAt()),
                record.getEventType(),
                record.getSeverity(),
                record.getAction(),
                record.getRequestPath(),
                record.getHttpMethod(),
                record.getVisitorId(),
                record.getClientIp(),
                record.getUserAgent(),
                record.getDbUser(),
                record.getStatusCode(),
                record.getDurationMs(),
                record.getPayloadDigest(),
                record.getPayloadSize(),
                record.getResourceKey(),
                record.getThreshold(),
                record.isSuccess(),
                record.getErrorMessage()
            );
        } catch (Exception e) {
            logger.error("Failed to persist audit record", e);
        }
    }

    public int purgeOlderThanDays(int retentionDays) {
        if (retentionDays <= 0) {
            return 0;
        }
        LocalDateTime cutoff = LocalDateTime.now().minusDays(retentionDays);
        return jdbcTemplate.update(
            "DELETE FROM audit_log WHERE created_at < ?",
            Timestamp.valueOf(cutoff)
        );
    }
}
