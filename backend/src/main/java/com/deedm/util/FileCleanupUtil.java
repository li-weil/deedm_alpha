package com.deedm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * 文件清理工具类
 * 用于清理指定目录下的文件
 */
@Component
public class FileCleanupUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileCleanupUtil.class);
    private static final Pattern SAFE_FILE_NAME_PATTERN = Pattern.compile("^[A-Za-z0-9._-]+$");
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".png", ".dot");

    @Value("${app.data.directory:./data}")
    private String dataDirectory;

    /**
     * 清理data目录下的所有文件
     * @return 清理的文件数量
     */
    public int cleanupDataDirectory() {
        Path dataPath = Paths.get(dataDirectory);

        // 检查目录是否存在
        if (!Files.exists(dataPath)) {
            logger.warn("Data directory does not exist: {}", dataDirectory);
            return 0;
        }

        // 检查是否为目录
        if (!Files.isDirectory(dataPath)) {
            logger.error("Data path is not a directory: {}", dataDirectory);
            throw new IllegalArgumentException("Data path is not a directory: " + dataDirectory);
        }

        int deletedCount = 0;

        try (Stream<Path> paths = Files.list(dataPath)) {
            deletedCount = paths
                .filter(Files::isRegularFile)
                .filter(path -> isSafeFileName(path.getFileName().toString()))
                .mapToInt(path -> {
                    try {
                        Files.delete(path);
                        logger.info("Deleted file: {}", path.getFileName());
                        return 1;
                    } catch (IOException e) {
                        logger.error("Failed to delete file: {}", path, e);
                        return 0;
                    }
                })
                .sum();
        } catch (IOException e) {
            logger.error("Error while cleaning up data directory: {}", dataDirectory, e);
            throw new RuntimeException("Failed to cleanup data directory", e);
        }

        logger.info("Cleanup completed. Deleted {} files from directory: {}", deletedCount, dataDirectory);
        return deletedCount;
    }

    /**
     * 清理指定文件名列表中的文件
     * @param fileNames 文件名白名单
     * @return 删除成功的文件数量
     */
    public int cleanupFiles(Set<String> fileNames) {
        if (fileNames == null || fileNames.isEmpty()) {
            return 0;
        }

        Path dataPath = Paths.get(dataDirectory).normalize().toAbsolutePath();
        if (!Files.exists(dataPath) || !Files.isDirectory(dataPath)) {
            logger.warn("Data directory does not exist or is not a directory: {}", dataDirectory);
            return 0;
        }

        int deletedCount = 0;
        for (String fileName : fileNames) {
            if (!isSafeFileName(fileName)) {
                logger.warn("Skip unsafe filename: {}", fileName);
                continue;
            }

            Path targetPath = dataPath.resolve(fileName).normalize().toAbsolutePath();
            if (!targetPath.getParent().equals(dataPath)) {
                logger.warn("Skip file outside data directory: {}", fileName);
                continue;
            }

            try {
                if (Files.exists(targetPath) && Files.isRegularFile(targetPath)) {
                    Files.delete(targetPath);
                    deletedCount++;
                    logger.info("Deleted whitelisted file: {}", fileName);
                }
            } catch (IOException e) {
                logger.error("Failed to delete whitelisted file: {}", fileName, e);
            }
        }

        logger.info("Whitelist cleanup completed. Deleted {} files from directory: {}", deletedCount, dataDirectory);
        return deletedCount;
    }

    /**
     * 删除 data 目录中最后修改时间早于截止时刻的安全文件。
     * @param cutoff 最后修改时间截止点
     * @return 删除成功的文件数量
     */
    public int cleanupExpiredFiles(Instant cutoff) {
        Path dataPath = Paths.get(dataDirectory).normalize().toAbsolutePath();
        if (!Files.exists(dataPath) || !Files.isDirectory(dataPath)) {
            logger.warn("Data directory does not exist or is not a directory: {}", dataDirectory);
            return 0;
        }

        int deletedCount = 0;
        try (Stream<Path> paths = Files.list(dataPath)) {
            List<Path> expiredFiles = paths
                .filter(Files::isRegularFile)
                .filter(path -> isSafeFileName(path.getFileName().toString()))
                .filter(path -> isOlderThan(path, cutoff))
                .toList();

            for (Path expiredFile : expiredFiles) {
                try {
                    Files.delete(expiredFile);
                    deletedCount++;
                    logger.info("Deleted expired file: {}", expiredFile.getFileName());
                } catch (IOException e) {
                    logger.error("Failed to delete expired file: {}", expiredFile, e);
                }
            }
        } catch (IOException e) {
            logger.error("Error while cleaning up expired files in directory: {}", dataDirectory, e);
            throw new RuntimeException("Failed to cleanup expired files", e);
        }

        logger.info("Expired file cleanup completed. Deleted {} files from directory: {}", deletedCount, dataDirectory);
        return deletedCount;
    }

    public Instant calculateCutoff(Duration maxAge) {
        return Instant.now().minus(maxAge);
    }

    /**
     * 过滤并标准化文件名列表
     * @param fileNames 原始文件名列表
     * @return 通过校验的文件名集合
     */
    public Set<String> sanitizeFileNames(List<String> fileNames) {
        if (fileNames == null || fileNames.isEmpty()) {
            return Collections.emptySet();
        }

        Set<String> sanitized = new LinkedHashSet<>();
        for (String fileName : fileNames) {
            if (fileName == null) {
                continue;
            }
            String trimmed = fileName.trim();
            if (isSafeFileName(trimmed)) {
                sanitized.add(trimmed);
            }
        }
        return sanitized;
    }

    private boolean isSafeFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return false;
        }
        if (fileName.contains("/") || fileName.contains("\\") || fileName.contains("..")) {
            return false;
        }
        if (!SAFE_FILE_NAME_PATTERN.matcher(fileName).matches()) {
            return false;
        }
        return ALLOWED_EXTENSIONS.stream().anyMatch(fileName::endsWith);
    }

    /**
     * 获取data目录中的文件数量
     * @return 文件数量
     */
    public long getFileCount() {
        Path dataPath = Paths.get(dataDirectory);

        if (!Files.exists(dataPath) || !Files.isDirectory(dataPath)) {
            return 0;
        }

        try (Stream<Path> paths = Files.list(dataPath)) {
            return paths.filter(Files::isRegularFile).count();
        } catch (IOException e) {
            logger.error("Error while counting files in data directory: {}", dataDirectory, e);
            return 0;
        }
    }

    private boolean isOlderThan(Path path, Instant cutoff) {
        try {
            FileTime lastModifiedTime = Files.getLastModifiedTime(path);
            return lastModifiedTime.toInstant().isBefore(cutoff);
        } catch (IOException e) {
            logger.warn("Failed to inspect last modified time for file: {}", path, e);
            return false;
        }
    }
}
