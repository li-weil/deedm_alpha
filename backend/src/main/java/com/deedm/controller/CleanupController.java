package com.deedm.controller;

import com.deedm.model.CleanupRequest;
import com.deedm.util.FileCleanupUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 清理控制器
 * 提供清理数据目录的API接口
 */
@RestController
@RequestMapping("/cleanup")
@CrossOrigin(origins = "*")
public class CleanupController {

    @Autowired
    private FileCleanupUtil fileCleanupUtil;

    /**
     * 按白名单清理data目录中的文件
     * @return 清理结果
     */
    @PostMapping("/data")
    public ResponseEntity<Map<String, Object>> cleanupDataDirectory(@RequestBody(required = false) CleanupRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (request == null || request.getFilenames() == null || request.getFilenames().isEmpty()) {
                response.put("success", false);
                response.put("message", "请求体必须包含 filenames 列表，禁止全目录清理");
                return ResponseEntity.badRequest().body(response);
            }

            int requestedCount = request.getFilenames().size();
            Set<String> sanitizedFileNames = fileCleanupUtil.sanitizeFileNames(request.getFilenames());

            if (sanitizedFileNames.isEmpty()) {
                response.put("success", false);
                response.put("message", "没有可清理的合法文件名");
                response.put("requestedCount", requestedCount);
                response.put("validCount", 0);
                response.put("invalidCount", requestedCount);
                return ResponseEntity.badRequest().body(response);
            }

            // 获取清理前的文件数量
            long fileCountBefore = fileCleanupUtil.getFileCount();

            // 执行清理
            int deletedCount = fileCleanupUtil.cleanupFiles(sanitizedFileNames);

            // 构建响应
            response.put("success", true);
            response.put("message", "数据目录白名单清理成功");
            response.put("deletedCount", deletedCount);
            response.put("requestedCount", requestedCount);
            response.put("validCount", sanitizedFileNames.size());
            response.put("invalidCount", requestedCount - sanitizedFileNames.size());
            response.put("fileCountBefore", fileCountBefore);
            response.put("fileCountAfter", fileCleanupUtil.getFileCount());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "数据目录清理失败: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取data目录的状态信息
     * @return 目录状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getDataDirectoryStatus() {
        Map<String, Object> response = new HashMap<>();

        try {
            long fileCount = fileCleanupUtil.getFileCount();

            response.put("success", true);
            response.put("fileCount", fileCount);
            response.put("message", fileCount > 0 ?
                "数据目录中有 " + fileCount + " 个文件" :
                "数据目录为空");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取数据目录状态失败: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());

            return ResponseEntity.status(500).body(response);
        }
    }
}
