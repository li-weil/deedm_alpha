package com.deedm.controller.algebra;

import com.deedm.model.algebra.GroupUmRequest;
import com.deedm.model.algebra.GroupUmResponse;
import com.deedm.service.algebra.GroupUmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/group-um")
@CrossOrigin(origins = "*")
public class GroupUmController {

    @Autowired
    private GroupUmService groupUmService;

    @PostMapping("/analyze")
    public ResponseEntity<GroupUmResponse> analyzeGroupUm(@RequestBody GroupUmRequest request) {
        try {
            GroupUmResponse response = groupUmService.analyzeGroupUm(request);
            if (!response.isSuccess()) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            GroupUmResponse errorResponse = new GroupUmResponse();
            errorResponse.setSuccess(false);
            errorResponse.setErrorMessage("Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateGroupUmInput(@RequestBody Map<String, String> input) {
        try {
            int m;
            try {
                m = Integer.parseInt(input.get("m"));
            } catch (NumberFormatException e) {
                return ResponseEntity.ok(Map.of(
                    "valid", false,
                    "message", "参数m必须是整数"
                ));
            }

            Map<String, Object> result = groupUmService.validateGroupUmInput(m);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                "valid", false,
                "message", "Internal server error: " + e.getMessage()
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateRandomGroupUm() {
        try {
            Map<String, Object> result = groupUmService.generateRandomGroupUm();
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