package com.deedm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class FileRetentionCleanupTask {

    private static final Logger logger = LoggerFactory.getLogger(FileRetentionCleanupTask.class);

    private final FileCleanupUtil fileCleanupUtil;

    @Value("${file.cleanup.enabled:true}")
    private boolean cleanupEnabled;

    @Value("${file.cleanup.max-age-hours:24}")
    private long maxAgeHours;

    public FileRetentionCleanupTask(FileCleanupUtil fileCleanupUtil) {
        this.fileCleanupUtil = fileCleanupUtil;
    }

    @Scheduled(cron = "${file.cleanup.cron:0 0 * * * *}")
    public void cleanupExpiredFiles() {
        if (!cleanupEnabled) {
            return;
        }
        if (maxAgeHours <= 0) {
            logger.warn("Skip file retention cleanup because maxAgeHours={} is invalid", maxAgeHours);
            return;
        }

        Duration maxAge = Duration.ofHours(maxAgeHours);
        Instant cutoff = fileCleanupUtil.calculateCutoff(maxAge);
        int deletedCount;
        try {
            deletedCount = fileCleanupUtil.cleanupExpiredFiles(cutoff);
            logger.info("File retention cleanup completed: deleted={} maxAgeHours={}", deletedCount, maxAgeHours);
        } catch (Exception e) {
            logger.error("File retention cleanup failed", e);
        }
    }
}
