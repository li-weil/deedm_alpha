package com.deedm.controller;

import com.deedm.model.TreeTraversalRequest;
import com.deedm.model.TreeTraversalResponse;
import com.deedm.service.TreeTraversalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Map;

@RestController
@RequestMapping("/tree-traversal")
@CrossOrigin(origins = "*")
public class TreeTraversalController {

    @Autowired
    private TreeTraversalService treeTraversalService;

    @PostMapping("/analyze")
    public ResponseEntity<TreeTraversalResponse> analyzeTree(@RequestBody TreeTraversalRequest request) {
        try {
            TreeTraversalResponse response = treeTraversalService.analyzeTree(request);
            if (response.getErrorMessage() != null) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            TreeTraversalResponse errorResponse = new TreeTraversalResponse();
            errorResponse.setErrorMessage("Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateTreeInput(@RequestBody Map<String, String> input) {
        try {
            String nodesString = input.get("nodesString");
            String edgesString = input.get("edgesString");
            String rootString = input.get("rootString");

            String errorMessage = treeTraversalService.validateTreeInput(nodesString, edgesString, rootString);
            boolean isValid = errorMessage == null;
            Map<String, Object> response = Map.of(
                "valid", isValid,
                "message", isValid ? "树的输入格式正确且为有效树结构" : errorMessage
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                "valid", false,
                "message", "Internal server error: " + e.getMessage()
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateRandomTree() {
        try {
            Map<String, Object> result = treeTraversalService.generateRandomTree();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                "success", false,
                "message", "Internal server error: " + e.getMessage()
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 提供树可视化图片的Web访问
     */
    @GetMapping("/tree-image/{filename}")
    public ResponseEntity<Resource> getTreeImage(@PathVariable String filename) {
        try {
            // 安全检查：只允许特定的文件名格式
            if (!filename.matches("TREE_[a-f0-9]+\\.png")) {
                System.out.println("TreeTraversalController: 文件名格式不匹配: " + filename);
                return ResponseEntity.badRequest().build();
            }

            File imageFile = new File("./data/" + filename);
            if (!imageFile.exists() || !imageFile.isFile()) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(imageFile);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}