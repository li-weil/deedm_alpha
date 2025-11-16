package com.deedm.controller.counting;

import com.deedm.model.counting.GenCombinationRequest;
import com.deedm.model.counting.GenCombinationResponse;
import com.deedm.service.counting.GenCombinationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/gen-combination")
@CrossOrigin(origins = "*")
public class GenCombinationController {

    @Autowired
    private GenCombinationService genCombinationService;

    @PostMapping("/generate")
    public ResponseEntity<GenCombinationResponse> generateCombinations(@RequestBody GenCombinationRequest request) {
        try {
            GenCombinationResponse response = genCombinationService.generateCombinations(request);
            if (response.getErrorMessage() != null && !response.isSuccess()) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            GenCombinationResponse errorResponse = new GenCombinationResponse();
            errorResponse.setErrorMessage("内部服务器错误: " + e.getMessage());
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

            String errorMessage = genCombinationService.validateInput(baseSet, lengthString, startString, numberString);
            boolean isValid = errorMessage == null;

            Map<String, Object> response = Map.of(
                "valid", isValid,
                "message", isValid ? "输入格式正确" : errorMessage
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                "valid", false,
                "message", "内部服务器错误: " + e.getMessage()
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/example")
    public ResponseEntity<Map<String, Object>> getExample(@RequestBody Map<String, String> input) {
        try {
            String exampleType = input.get("exampleType");

            if ("example8_40".equals(exampleType)) {
                Map<String, Object> example = Map.of(
                    "baseSet", "1, 2, 3, 4, 5, 6, 7, 8",
                    "length", 5,
                    "startString", "13458",
                    "number", 20
                );
                return ResponseEntity.ok(example);
            } else if ("example8_41".equals(exampleType)) {
                Map<String, Object> example = Map.of(
                    "baseSet", "1, 2, 3, 4, 5, 6, 7, 8",
                    "length", 5,
                    "startString", "13478",
                    "number", 20
                );
                return ResponseEntity.ok(example);
            } else {
                Map<String, Object> defaultExample = Map.of(
                    "baseSet", "1, 2, 3, 4, 5",
                    "length", 3,
                    "startString", "123",
                    "number", 10
                );
                return ResponseEntity.ok(defaultExample);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                "success", false,
                "message", "内部服务器错误: " + e.getMessage()
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}