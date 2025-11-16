package com.deedm.controller.counting;

import com.deedm.model.counting.CountFunctionRequest;
import com.deedm.model.counting.CountFunctionResponse;
import com.deedm.service.counting.CountFunctionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/count-function")
@CrossOrigin(origins = "*")
public class CountFunctionController {

    @Autowired
    private CountFunctionService countFunctionService;

    @PostMapping("/count")
    public ResponseEntity<CountFunctionResponse> countFunctions(@RequestBody CountFunctionRequest request) {
        try {
            CountFunctionResponse response = countFunctionService.countFunctions(request);
            if (!response.isSuccess()) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            CountFunctionResponse errorResponse = new CountFunctionResponse();
            errorResponse.setSuccess(false);
            errorResponse.setErrorMessage("服务器内部错误: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateInput(@RequestBody CountFunctionRequest request) {
        try {
            String errorMessage = countFunctionService.validateInput(request);
            boolean isValid = errorMessage == null;
            Map<String, Object> response = new HashMap<>();
            response.put("valid", isValid);
            response.put("message", errorMessage);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("message", "验证过程中发生错误: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/generate-example")
    public ResponseEntity<Map<String, Object>> generateExample() {
        try {
            Map<String, Object> example = new HashMap<>();

            // 生成示例集合A: {1, 2, 3, 4, 5, 6}
            example.put("setAString", "{1, 2, 3, 4, 5, 6}");

            // 随机生成集合B: 3-8个元素
            int number = (int)(Math.random() * 6) + 3;
            StringBuilder setB = new StringBuilder("{");
            for (int i = 0; i < number; i++) {
                if (i > 0) {
                    setB.append(", ");
                }
                setB.append((char)('a' + i));
            }
            setB.append("}");
            example.put("setBString", setB.toString());

            example.put("maxDisplayString", "100");
            example.put("intType", false);
            example.put("countInjection", false);
            example.put("countSurjection", true);
            example.put("countBijection", false);
            example.put("detailed", true);

            return ResponseEntity.ok(example);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "生成示例时发生错误: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}