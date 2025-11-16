package com.deedm.controller.setrelfun;

import com.deedm.model.setrelfun.EquivalenceRequest;
import com.deedm.model.setrelfun.EquivalenceResponse;
import com.deedm.service.setrelfun.EquivalenceService;

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
@RequestMapping("/equivalence")
@CrossOrigin(origins = "*")
public class EquivalenceController {

    @Autowired
    private EquivalenceService equivalenceService;

    @PostMapping("/calculate")
    public ResponseEntity<EquivalenceResponse> calculateEquivalenceRelation(@RequestBody EquivalenceRequest request) {
        try {
            EquivalenceResponse response = equivalenceService.analyzeEquivalenceRelation(request);
            if (response.getErrorMessage() != null) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            EquivalenceResponse errorResponse = new EquivalenceResponse();
            errorResponse.setErrorMessage("Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateEquivalenceInput(@RequestBody Map<String, String> input) {
        try {
            String setAString = input.get("setAString");
            String relationRString = input.get("relationRString");
            boolean intTypeElement = Boolean.parseBoolean(input.get("intTypeElement"));

            String errorMessage = equivalenceService.validateEquivalenceInput(setAString, relationRString, intTypeElement);
            boolean isValid = errorMessage == null;
            Map<String, Object> response = Map.of(
                "valid", isValid,
                "message", isValid ? "输入格式正确" : errorMessage
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
    public ResponseEntity<Map<String, Object>> generateRandomEquivalenceRelation() {
        try {
            Map<String, Object> result = equivalenceService.generateRandomEquivalenceRelation();
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
            // 安全检查：允许RelationR_和RelationRequivclo_开头的文件
            if (!filename.matches("(RelationR|RelationRequivclo)_[a-f0-9]+\\.png")) {
                System.out.println("EquivalenceController: 文件名格式不匹配: " + filename);
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