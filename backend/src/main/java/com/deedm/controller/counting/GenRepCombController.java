package com.deedm.controller.counting;

import com.deedm.model.counting.GenRepCombRequest;
import com.deedm.model.counting.GenRepCombResponse;
import com.deedm.service.counting.GenRepCombService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/gen-rep-comb")
@CrossOrigin(origins = "*")
public class GenRepCombController {

    @Autowired
    private GenRepCombService genRepCombService;

    @PostMapping("/generate")
    public ResponseEntity<GenRepCombResponse> generateRepetitionCombinations(@RequestBody GenRepCombRequest request) {
        try {
            GenRepCombResponse response = genRepCombService.generateRepetitionCombinations(request);
            if (response.getErrorMessage() != null) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            GenRepCombResponse errorResponse = new GenRepCombResponse();
            errorResponse.setErrorMessage("Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateInput(@RequestBody Map<String, String> input) {
        try {
            String baseSetString = input.get("baseSetString");
            String lengthString = input.get("lengthString");
            String startString = input.get("startString");
            String numberString = input.get("numberString");

            String errorMessage = genRepCombService.validateInput(baseSetString, lengthString, startString, numberString);
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
            Map<String, Object> example = Map.of(
                "baseSetString", "1, 2, 3, 4, 5, 6, 7, 8",
                "length", 5,
                "startString", "13458",
                "number", 20
            );
            return ResponseEntity.ok(example);
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                "success", false,
                "message", "Internal server error: " + e.getMessage()
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}