package com.deedm.controller.counting;

import com.deedm.model.counting.CombCalculatorRequest;
import com.deedm.model.counting.CombCalculatorResponse;
import com.deedm.service.counting.CombCalculatorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/comb-calculator")
@CrossOrigin(origins = "*")
public class CombCalculatorController {

    @Autowired
    private CombCalculatorService combCalculatorService;

    @PostMapping("/calculate")
    public ResponseEntity<CombCalculatorResponse> calculateCombinations(@RequestBody CombCalculatorRequest request) {
        try {
            CombCalculatorResponse response = combCalculatorService.calculateCombinations(request);
            if (response.getErrorMessage() != null) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            CombCalculatorResponse errorResponse = new CombCalculatorResponse();
            errorResponse.setErrorMessage("Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateInput(@RequestBody Map<String, String> input) {
        try {
            String nStr = input.get("n");
            String mStr = input.get("m");

            String errorMessage = combCalculatorService.validateInput(nStr, mStr);
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
    public ResponseEntity<Map<String, Object>> generateRandomValues() {
        try {
            // 生成随机n和m值
            java.util.Random random = new java.util.Random();
            int n = random.nextInt(20) + 1; // 1-20
            int m = random.nextInt(n + 1);   // 0-n

            Map<String, Object> result = Map.of(
                "success", true,
                "n", n,
                "m", m,
                "calculatePower", true,
                "calculateCombination", true,
                "calculatePermutation", true,
                "calculateFactorial", true
            );

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                "success", false,
                "message", "生成随机值失败: " + e.getMessage()
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}