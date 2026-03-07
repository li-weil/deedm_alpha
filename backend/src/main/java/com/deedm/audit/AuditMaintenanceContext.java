package com.deedm.audit;

import java.util.function.Supplier;

public final class AuditMaintenanceContext {

    private static final ThreadLocal<Boolean> MAINTENANCE_ACCESS =
        ThreadLocal.withInitial(() -> Boolean.FALSE);

    private AuditMaintenanceContext() {
    }

    public static boolean isMaintenanceAccessAllowed() {
        return Boolean.TRUE.equals(MAINTENANCE_ACCESS.get());
    }

    public static <T> T runWithMaintenanceAccess(Supplier<T> supplier) {
        boolean previous = isMaintenanceAccessAllowed();
        MAINTENANCE_ACCESS.set(Boolean.TRUE);
        try {
            return supplier.get();
        } finally {
            MAINTENANCE_ACCESS.set(previous);
        }
    }
}
