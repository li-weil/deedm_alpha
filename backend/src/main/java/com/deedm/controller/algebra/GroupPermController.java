package com.deedm.controller.algebra;

import com.deedm.model.algebra.GroupPermRequest;
import com.deedm.model.algebra.GroupPermResponse;
import com.deedm.service.algebra.GroupPermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/group-perm")
@CrossOrigin(origins = "*")
public class GroupPermController {

    @Autowired
    private GroupPermService groupPermService;

    @PostMapping("/analyze")
    public ResponseEntity<GroupPermResponse> analyzePermutationGroup(@RequestBody GroupPermRequest request) {
        try {
            GroupPermResponse response = groupPermService.analyzePermutationGroup(request);
            if (response.getErrorMessage() != null) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            GroupPermResponse errorResponse = new GroupPermResponse();
            errorResponse.setErrorMessage("Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateInput(@RequestBody Map<String, String> input) {
        try {
            int m = Integer.parseInt(input.get("m"));
            String errorMessage = groupPermService.validateInput(m);
            boolean isValid = errorMessage == null;
            Map<String, Object> response = Map.of(
                "valid", isValid,
                "message", isValid ? "输入格式正确" : errorMessage
            );
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            Map<String, Object> errorResponse = Map.of(
                "valid", false,
                "message", "度数n必须是整数"
            );
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                "valid", false,
                "message", "Internal server error: " + e.getMessage()
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateExample() {
        try {
            // 提供两个示例配置
            Map<String, Object> result = Map.of(
                "success", true,
                "examples", Map.of(
                    "example10_36", Map.of(
                        "m", 3,
                        "showCycleGroup", true,
                        "showElementPower", true,
                        "showElementOrder", true,
                        "showSubgroups", true,
                        "showCosets", true,
                        "showNormalSubgroups", false
                    ),
                    "example10_40", Map.of(
                        "m", 3,
                        "showCycleGroup", false,
                        "showElementPower", false,
                        "showElementOrder", false,
                        "showSubgroups", false,
                        "showCosets", true,
                        "showNormalSubgroups", true
                    )
                )
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                "success", false,
                "message", "Internal server error: " + e.getMessage()
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}