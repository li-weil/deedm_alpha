package com.deedm.controller.counting;

import com.deedm.model.counting.GenPermutationRequest;
import com.deedm.model.counting.GenPermutationResponse;
import com.deedm.service.counting.GenPermutationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/gen-permutation")
@CrossOrigin(origins = "*")
public class GenPermutationController {

    @Autowired
    private GenPermutationService genPermutationService;

    @PostMapping("/generate")
    public ResponseEntity<GenPermutationResponse> generatePermutations(@RequestBody GenPermutationRequest request) {
        try {
            GenPermutationResponse response = genPermutationService.generatePermutations(request);
            if (!response.isSuccess()) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            GenPermutationResponse errorResponse = new GenPermutationResponse();
            errorResponse.setErrorMessage("Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateInput(@RequestBody Map<String, String> input) {
        try {
            String baseSet = input.get("baseSet");
            String lengthString = input.get("length");
            String startString = input.get("startString");
            String numberString = input.get("number");

            String errorMessage = genPermutationService.validateInput(baseSet, lengthString, startString, numberString);
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

    @GetMapping("/example")
    public ResponseEntity<Map<String, Object>> getExample() {
        try {
            // 提供例8.38的数据
            Map<String, Object> example8_38 = Map.of(
                "baseSet", "1, 2, 3, 4, 5, 6, 7, 8",
                "length", 8,
                "startString", "63285741",
                "number", 20
            );

            return ResponseEntity.ok(example8_38);
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                "success", false,
                "message", "Internal server error: " + e.getMessage()
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}