package com.deedm.audit;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class AuditPersistenceService {

    private static final Logger logger = LoggerFactory.getLogger(AuditPersistenceService.class);

    private static final String CREATE_AUDIT_TABLE_SQL = """
        CREATE TABLE IF NOT EXISTS audit_log (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,
            created_at TIMESTAMP NOT NULL,
            action VARCHAR(255) NOT NULL,
            request_path VARCHAR(512),
            http_method VARCHAR(16),
            visitor_id VARCHAR(128),
            client_ip VARCHAR(128),
            user_agent VARCHAR(512),
            db_user VARCHAR(64),
            success BOOLEAN,
            error_message VARCHAR(1000)
        )
        """;

    private final JdbcTemplate jdbcTemplate;

    public AuditPersistenceService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void initAuditTable() {
        try {
            jdbcTemplate.execute(CREATE_AUDIT_TABLE_SQL);
        } catch (Exception e) {
            logger.error("Failed to initialize audit_log table", e);
        }
    }

    public void persist(AuditRecord record) {
        try {
            jdbcTemplate.update(
                "INSERT INTO audit_log (created_at, action, request_path, http_method, visitor_id, client_ip, user_agent, db_user, success, error_message) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                Timestamp.valueOf(record.getCreatedAt()),
                record.getAction(),
                record.getRequestPath(),
                record.getHttpMethod(),
                record.getVisitorId(),
                record.getClientIp(),
                record.getUserAgent(),
                record.getDbUser(),
                record.isSuccess(),
                record.getErrorMessage()
            );
        } catch (Exception e) {
            logger.error("Failed to persist audit record", e);
        }
    }
}
