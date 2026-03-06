package com.deedm.util;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileCleanupUtilTest {

    @Test
    void sanitizeAndCleanupShouldOnlyDeleteWhitelistedFiles() throws Exception {
        Path tempDir = Files.createTempDirectory("cleanup-test-");
        Path pngFile = tempDir.resolve("demo.png");
        Path dotFile = tempDir.resolve("graph.dot");
        Path txtFile = tempDir.resolve("keep.txt");

        Files.writeString(pngFile, "png");
        Files.writeString(dotFile, "dot");
        Files.writeString(txtFile, "txt");

        FileCleanupUtil fileCleanupUtil = new FileCleanupUtil();
        ReflectionTestUtils.setField(fileCleanupUtil, "dataDirectory", tempDir.toString());

        Set<String> sanitized = fileCleanupUtil.sanitizeFileNames(List.of(
            "demo.png",
            "graph.dot",
            "../evil.png",
            "keep.txt",
            ""
        ));

        assertEquals(2, sanitized.size());
        assertTrue(sanitized.contains("demo.png"));
        assertTrue(sanitized.contains("graph.dot"));

        int deleted = fileCleanupUtil.cleanupFiles(sanitized);

        assertEquals(2, deleted);
        assertFalse(Files.exists(pngFile));
        assertFalse(Files.exists(dotFile));
        assertTrue(Files.exists(txtFile));
    }
}
