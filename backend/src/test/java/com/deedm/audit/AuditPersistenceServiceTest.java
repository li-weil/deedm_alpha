package com.deedm.audit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = {
    "spring.main.web-application-type=none",
    "spring.datasource.url=jdbc:h2:mem:audit-persistence-test;DB_CLOSE_DELAY=-1",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "audit.enabled=false",
    "audit.retention.days=0",
    "audit.integrity.secret=test-audit-secret"
})
class AuditPersistenceServiceTest {

    @Autowired
    private AuditPersistenceService auditPersistenceService;

    @Autowired
    private AuditIntegrityService auditIntegrityService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void persistStoresIntegrityHash() {
        AuditRecord record = buildRecord("API_WRITE", "INFO");

        auditPersistenceService.persist(record);

        String storedHash = jdbcTemplate.queryForObject(
            "SELECT record_hash FROM audit_log LIMIT 1",
            String.class
        );

        assertNotNull(storedHash);
        assertEquals(auditIntegrityService.computeRecordHash(record), storedHash);
    }

    @Test
    void updateIsBlockedWithoutMaintenanceAccess() {
        auditPersistenceService.persist(buildRecord("API_WRITE", "INFO"));

        assertThrows(Exception.class, () ->
            jdbcTemplate.update("UPDATE audit_log SET severity = ? WHERE id = 1", "WARN")
        );
    }

    @Test
    void deleteIsBlockedWithoutMaintenanceAccess() {
        auditPersistenceService.persist(buildRecord("API_WRITE", "INFO"));

        assertThrows(Exception.class, () ->
            jdbcTemplate.update("DELETE FROM audit_log WHERE id = 1")
        );
    }

    @Test
    void deleteIsAllowedWithMaintenanceAccess() {
        auditPersistenceService.persist(buildRecord("AUDIT_MAINTENANCE", "INFO"));

        Integer deleted = AuditMaintenanceContext.runWithMaintenanceAccess(() ->
            jdbcTemplate.update("DELETE FROM audit_log WHERE id = 1")
        );

        Integer remaining = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM audit_log",
            Integer.class
        );

        assertEquals(1, deleted);
        assertEquals(0, remaining);
    }

    private AuditRecord buildRecord(String eventType, String severity) {
        return new AuditRecord(
            LocalDateTime.of(2026, 3, 7, 12, 0, 0),
            eventType,
            severity,
            "AuditPersistenceServiceTest.buildRecord",
            "/api/test",
            "POST",
            "visitor-1",
            "127.0.0.1",
            "JUnit",
            "sa",
            200,
            12L,
            "digest",
            32,
            "127.0.0.1|/api/test",
            null,
            null,
            true,
            null
        );
    }
}
