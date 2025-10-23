package com.deedm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * 文件清理工具类
 * 用于清理指定目录下的文件
 */
@Component
public class FileCleanupUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileCleanupUtil.class);

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
}