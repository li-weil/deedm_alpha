package com.deedm.controller.algebra;

import com.deedm.model.algebra.OperationPropertyRequest;
import com.deedm.model.algebra.OperationPropertyResponse;
import com.deedm.service.algebra.OperationPropertyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/operation-property")
@CrossOrigin(origins = "*")
public class OperationPropertyController {

    @Autowired
    private OperationPropertyService operationPropertyService;

    @PostMapping("/analyze")
    public ResponseEntity<OperationPropertyResponse> analyzeOperationProperties(@RequestBody OperationPropertyRequest request) {
        try {
            OperationPropertyResponse response = operationPropertyService.analyzeOperationProperties(request);
            if (response.getErrorMessage() != null) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            OperationPropertyResponse errorResponse = new OperationPropertyResponse();
            errorResponse.setErrorMessage("Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateOperationInput(@RequestBody Map<String, String> input) {
        try {
            String setAString = input.get("setAString");
            String operator1String = input.get("operator1String");
            String operator2String = input.get("operator2String");
            boolean intTypeElement = Boolean.parseBoolean(input.get("intTypeElement"));

            String errorMessage = operationPropertyService.validateOperationInput(
                setAString, operator1String, operator2String, intTypeElement);
            boolean isValid = errorMessage == null;
            Map<String, Object> response = Map.of(
                "valid", isValid,
                "message", isValid ? "运算输入格式正确" : errorMessage
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
    public ResponseEntity<Map<String, Object>> generateRandomOperation() {
        try {
            OperationPropertyResponse result = operationPropertyService.generateRandomOperation();

            Map<String, Object> response = Map.of(
                "success", result.isSuccess(),
                "setAString", result.getSetAString(),
                "operator1String", result.getOperator1String(),
                "operator2String", result.getOperator2String(),
                "intTypeElement", result.isIntTypeElement(),
                "message", result.isSuccess() ? "生成随机运算成功" : result.getErrorMessage()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                "success", false,
                "message", "Internal server error: " + e.getMessage()
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}