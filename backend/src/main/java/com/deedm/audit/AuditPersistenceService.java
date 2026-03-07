package com.deedm.audit;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

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
            record_hash VARCHAR(128) NOT NULL,
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
        "ALTER TABLE audit_log ADD COLUMN IF NOT EXISTS threshold INT",
        "ALTER TABLE audit_log ADD COLUMN IF NOT EXISTS record_hash VARCHAR(128)"
    };
    private static final String DROP_UPDATE_TRIGGER_SQL = "DROP TRIGGER IF EXISTS audit_log_block_update";
    private static final String DROP_DELETE_TRIGGER_SQL = "DROP TRIGGER IF EXISTS audit_log_block_delete";
    private static final String CREATE_UPDATE_TRIGGER_SQL = """
        CREATE TRIGGER audit_log_block_update
        BEFORE UPDATE ON audit_log
        FOR EACH ROW
        CALL 'com.deedm.audit.AuditLogProtectionTrigger'
        """;
    private static final String CREATE_DELETE_TRIGGER_SQL = """
        CREATE TRIGGER audit_log_block_delete
        BEFORE DELETE ON audit_log
        FOR EACH ROW
        CALL 'com.deedm.audit.AuditLogProtectionTrigger'
        """;

    private final JdbcTemplate jdbcTemplate;
    private final AuditIntegrityService auditIntegrityService;

    public AuditPersistenceService(JdbcTemplate jdbcTemplate,
                                   AuditIntegrityService auditIntegrityService) {
        this.jdbcTemplate = jdbcTemplate;
        this.auditIntegrityService = auditIntegrityService;
    }

    @PostConstruct
    public void initAuditTable() {
        try {
            jdbcTemplate.execute(CREATE_AUDIT_TABLE_SQL);
            for (String sql : UPGRADE_SQLS) {
                jdbcTemplate.execute(sql);
            }
            backfillMissingRecordHashes();
            jdbcTemplate.execute(DROP_UPDATE_TRIGGER_SQL);
            jdbcTemplate.execute(DROP_DELETE_TRIGGER_SQL);
            jdbcTemplate.execute(CREATE_UPDATE_TRIGGER_SQL);
            jdbcTemplate.execute(CREATE_DELETE_TRIGGER_SQL);
        } catch (Exception e) {
            logger.error("Failed to initialize audit_log table", e);
        }
    }

    private void backfillMissingRecordHashes() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
            "SELECT id, created_at, event_type, severity, action, request_path, http_method, visitor_id, client_ip, user_agent, db_user, status_code, duration_ms, payload_digest, payload_size, resource_key, threshold, success, error_message FROM audit_log WHERE record_hash IS NULL OR record_hash = ''"
        );
        if (rows.isEmpty()) {
            return;
        }

        AuditMaintenanceContext.runWithMaintenanceAccess(() -> {
            for (Map<String, Object> row : rows) {
                AuditRecord record = new AuditRecord(
                    ((Timestamp) row.get("created_at")).toLocalDateTime(),
                    (String) row.get("event_type"),
                    (String) row.get("severity"),
                    (String) row.get("action"),
                    (String) row.get("request_path"),
                    (String) row.get("http_method"),
                    (String) row.get("visitor_id"),
                    (String) row.get("client_ip"),
                    (String) row.get("user_agent"),
                    (String) row.get("db_user"),
                    (Integer) row.get("status_code"),
                    toLong(row.get("duration_ms")),
                    (String) row.get("payload_digest"),
                    (Integer) row.get("payload_size"),
                    (String) row.get("resource_key"),
                    (Integer) row.get("threshold"),
                    null,
                    Boolean.TRUE.equals(row.get("success")),
                    (String) row.get("error_message")
                );
                jdbcTemplate.update(
                    "UPDATE audit_log SET record_hash = ? WHERE id = ?",
                    auditIntegrityService.computeRecordHash(record),
                    row.get("id")
                );
            }
            return null;
        });
    }

    public void persist(AuditRecord record) {
        try {
            String recordHash = auditIntegrityService.computeRecordHash(record);
            jdbcTemplate.update(
                "INSERT INTO audit_log (created_at, event_type, severity, action, request_path, http_method, visitor_id, client_ip, user_agent, db_user, status_code, duration_ms, payload_digest, payload_size, resource_key, threshold, record_hash, success, error_message) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
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
                recordHash,
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
        return AuditMaintenanceContext.runWithMaintenanceAccess(() ->
            jdbcTemplate.update(
                "DELETE FROM audit_log WHERE created_at < ?",
                Timestamp.valueOf(cutoff)
            )
        );
    }

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(value.toString());
    }
}
