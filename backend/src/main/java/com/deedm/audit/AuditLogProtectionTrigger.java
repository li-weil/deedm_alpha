package com.deedm.audit;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.SQLException;

public class AuditLogProtectionTrigger implements Trigger {

    @Override
    public void init(Connection conn,
                     String schemaName,
                     String triggerName,
                     String tableName,
                     boolean before,
                     int type) {
        // No-op: protection is enforced in fire().
    }

    @Override
    public void fire(Connection conn, Object[] oldRow, Object[] newRow) throws SQLException {
        if (AuditMaintenanceContext.isMaintenanceAccessAllowed()) {
            return;
        }

        String operation = newRow == null ? "DELETE" : "UPDATE";
        throw new SQLException(
            "audit_log is append-only; " + operation + " requires controlled maintenance access"
        );
    }

    @Override
    public void close() {
        // No-op
    }

    @Override
    public void remove() {
        // No-op
    }
}
