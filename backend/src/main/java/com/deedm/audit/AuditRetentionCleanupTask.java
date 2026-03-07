package com.deedm.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuditRetentionCleanupTask {

    private static final Logger logger = LoggerFactory.getLogger(AuditRetentionCleanupTask.class);

    private final AuditPersistenceService auditPersistenceService;

    @Value("${audit.enabled:true}")
    private boolean auditEnabled;

    @Value("${audit.retention.days:180}")
    private int retentionDays;

    @Value("${spring.datasource.username:unknown}")
    private String dbUser;

    public AuditRetentionCleanupTask(AuditPersistenceService auditPersistenceService) {
        this.auditPersistenceService = auditPersistenceService;
    }

    @Scheduled(cron = "${audit.retention.cron:0 30 2 * * *}")
    public void cleanupExpiredAuditRecords() {
        if (!auditEnabled || retentionDays <= 0) {
            return;
        }

        int deletedCount;
        try {
            deletedCount = auditPersistenceService.purgeOlderThanDays(retentionDays);
            logger.info("Audit retention cleanup completed: deleted={} retentionDays={}", deletedCount, retentionDays);
        } catch (Exception e) {
            logger.error("Audit retention cleanup failed", e);
            return;
        }

        AuditRecord record = new AuditRecord(
            LocalDateTime.now(),
            "AUDIT_MAINTENANCE",
            "INFO",
            "AuditRetentionCleanupTask.cleanupExpiredAuditRecords",
            "/internal/audit/cleanup",
            "INTERNAL",
            "system",
            "127.0.0.1",
            "scheduler",
            dbUser,
            200,
            0L,
            null,
            0,
            "audit_log_retention",
            retentionDays,
            null,
            true,
            "deleted=" + deletedCount
        );
        auditPersistenceService.persist(record);
    }
}
