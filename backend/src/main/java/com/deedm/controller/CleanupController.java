package com.deedm.controller;

import com.deedm.util.FileCleanupUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
     * 清理data目录下的所有文件
     * @return 清理结果
     */
    @PostMapping("/data")
    public ResponseEntity<Map<String, Object>> cleanupDataDirectory() {
        Map<String, Object> response = new HashMap<>();

        try {
            // 获取清理前的文件数量
            long fileCountBefore = fileCleanupUtil.getFileCount();

            // 执行清理
            int deletedCount = fileCleanupUtil.cleanupDataDirectory();

            // 构建响应
            response.put("success", true);
            response.put("message", "数据目录清理成功");
            response.put("deletedCount", deletedCount);
            response.put("fileCountBefore", fileCountBefore);
            response.put("fileCountAfter", 0);

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