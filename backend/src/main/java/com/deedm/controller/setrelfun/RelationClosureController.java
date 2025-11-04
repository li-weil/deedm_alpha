package com.deedm.controller.setrelfun;

import com.deedm.model.setrelfun.RelationClosureRequest;
import com.deedm.model.setrelfun.RelationClosureResponse;
import com.deedm.service.setrelfun.RelationClosureService;

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
@RequestMapping("/relation-closure")
@CrossOrigin(origins = "*")
public class RelationClosureController {

    @Autowired
    private RelationClosureService relationClosureService;

    @PostMapping("/calculate")
    public ResponseEntity<RelationClosureResponse> calculateRelationClosures(@RequestBody RelationClosureRequest request) {
        try {
            RelationClosureResponse response = relationClosureService.calculateRelationClosures(request);
            if (response.getErrorMessage() != null) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            RelationClosureResponse errorResponse = new RelationClosureResponse();
            errorResponse.setErrorMessage("Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateRelationInput(@RequestBody Map<String, String> input) {
        try {
            String setAString = input.get("setAString");
            String relationRString = input.get("relationRString");
            boolean intTypeElement = Boolean.parseBoolean(input.get("intTypeElement"));

            String errorMessage = relationClosureService.validateRelationInput(setAString, relationRString, intTypeElement);
            boolean isValid = errorMessage == null;
            Map<String, Object> response = Map.of(
                "valid", isValid,
                "message", isValid ? "关系的输入格式正确" : errorMessage
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
    public ResponseEntity<Map<String, Object>> generateRandomRelation() {
        try {
            Map<String, Object> result = relationClosureService.generateRandomRelation();
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
     * 提供关系图可视化图片的Web访问
     */
    @GetMapping("/relation-image/{filename}")
    public ResponseEntity<Resource> getRelationImage(@PathVariable String filename) {
        try {
            // 安全检查：允许Relation_开头的关系图文件
            if (!filename.matches("Relation_[a-f0-9]+\\.png")) {
                System.out.println("RelationClosureController: 文件名格式不匹配: " + filename);
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