package com.deedm.controller.counting;

import com.deedm.model.counting.CountEquationSolverRequest;
import com.deedm.model.counting.CountEquationSolverResponse;
import com.deedm.service.counting.CountEquationSolverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/count-equation-solver")
@CrossOrigin(origins = "*")
public class CountEquationSolverController {

    @Autowired
    private CountEquationSolverService countEquationSolverService;

    @PostMapping("/solve")
    public ResponseEntity<CountEquationSolverResponse> solveEquation(@RequestBody CountEquationSolverRequest request) {
        try {
            // 验证输入
            String validationError = countEquationSolverService.validateInput(request);
            if (validationError != null) {
                CountEquationSolverResponse errorResponse = new CountEquationSolverResponse();
                errorResponse.setErrorMessage(validationError);
                return ResponseEntity.badRequest().body(errorResponse);
            }

            CountEquationSolverResponse response = countEquationSolverService.solveEquation(request);
            if (response.getErrorMessage() != null) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            CountEquationSolverResponse errorResponse = new CountEquationSolverResponse();
            errorResponse.setErrorMessage("Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateInput(@RequestBody CountEquationSolverRequest request) {
        try {
            String errorMessage = countEquationSolverService.validateInput(request);
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

    @GetMapping("/examples")
    public ResponseEntity<Map<String, Object>> getExamples() {
        try {
            Map<String, Object> examples = new java.util.HashMap<>();

            Map<String, Object> example8_30 = new java.util.HashMap<>();
            example8_30.put("varNumber", 3);
            example8_30.put("varSum", 2);
            example8_30.put("description", "例题8.30：x₁ + x₂ + x₃ = 2");
            examples.put("example8_30", example8_30);

            Map<String, Object> example8_31 = new java.util.HashMap<>();
            example8_31.put("varNumber", 3);
            example8_31.put("varSum", 6);
            example8_31.put("description", "例题8.31：x₁ + x₂ + x₃ = 6");
            examples.put("example8_31", example8_31);

            Map<String, Object> example8_32 = new java.util.HashMap<>();
            example8_32.put("varNumber", 4);
            example8_32.put("varSum", 15);
            example8_32.put("index11", 1);
            example8_32.put("min11", 3);
            example8_32.put("index12", 2);
            example8_32.put("min12", 4);
            example8_32.put("description", "例题8.32：x₁ + x₂ + x₃ + x₄ = 15, x₁ ≥ 3, x₂ ≥ 4");
            examples.put("example8_32", example8_32);

            Map<String, Object> example8_34 = new java.util.HashMap<>();
            example8_34.put("varNumber", 3);
            example8_34.put("varSum", 6);
            example8_34.put("index11", 1);
            example8_34.put("max11", 3);
            example8_34.put("index12", 2);
            example8_34.put("max12", 2);
            example8_34.put("index21", 3);
            example8_34.put("max21", 4);
            example8_34.put("logicAnd", true);
            example8_34.put("description", "例题8.34：x₁ + x₂ + x₃ = 6, x₁ ≤ 3 ∧ x₂ ≤ 2 ∧ x₃ ≤ 4");
            examples.put("example8_34", example8_34);

            Map<String, Object> example8_35 = new java.util.HashMap<>();
            example8_35.put("varNumber", 5);
            example8_35.put("varSum", 15);
            example8_35.put("index11", 1);
            example8_35.put("min11", 2);
            example8_35.put("max11", 5);
            example8_35.put("index12", 2);
            example8_35.put("max12", 2);
            example8_35.put("index21", 3);
            example8_35.put("min21", 1);
            example8_35.put("max21", 4);
            example8_35.put("logicAnd", true);
            example8_35.put("description", "例题8.35：x₁ + x₂ + x₃ + x₄ + x₅ = 15, 2 ≤ x₁ ≤ 5 ∧ x₂ ≤ 2 ∧ 1 ≤ x₃ ≤ 4");
            examples.put("example8_35", example8_35);

            Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", true);
            response.put("examples", examples);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}