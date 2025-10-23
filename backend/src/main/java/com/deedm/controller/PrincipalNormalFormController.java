package com.deedm.controller;

import com.deedm.model.NormalFormCheckRequest;
import com.deedm.model.NormalFormGenerateRequest;
import com.deedm.model.PrincipalNormalFormRequest;
import com.deedm.model.PrincipalNormalFormResponse;
import com.deedm.service.PrincipalNormalFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/principal-normal-form")
@CrossOrigin(origins = "*")
public class PrincipalNormalFormController {

    @Autowired
    private PrincipalNormalFormService principalNormalFormService;

    /**
     * Calculate principal normal forms
     */
    @PostMapping("/calculate")
    public ResponseEntity<PrincipalNormalFormResponse> calculatePrincipalNormalForm(
            @RequestBody PrincipalNormalFormRequest request) {
        try {
            PrincipalNormalFormResponse response = principalNormalFormService.calculatePrincipalNormalForm(request);

            if (!response.isSuccess()) {
                return ResponseEntity.badRequest().body(response);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            PrincipalNormalFormResponse errorResponse = new PrincipalNormalFormResponse();
            errorResponse.setSuccess(false);
            errorResponse.setErrorMessage("Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Calculate principal normal forms with detailed expansion steps (like original application)
     */
    @PostMapping("/calculate-with-expansion")
    public ResponseEntity<PrincipalNormalFormResponse> calculateWithExpansionSteps(
            @RequestBody PrincipalNormalFormRequest request) {
        try {
            // Ensure PNF and detailed methods are included
            if (!request.getNormalFormTypes().contains("pnf")) {
                request.getNormalFormTypes().add("pnf");
            }
            if (!request.getCalculationMethods().contains("detailed")) {
                request.getCalculationMethods().add("detailed");
            }

            PrincipalNormalFormResponse response = principalNormalFormService.calculatePrincipalNormalForm(request);

            if (!response.isSuccess()) {
                return ResponseEntity.badRequest().body(response);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            PrincipalNormalFormResponse errorResponse = new PrincipalNormalFormResponse();
            errorResponse.setSuccess(false);
            errorResponse.setErrorMessage("Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Generate random formulas for normal form practice
     */
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateFormulas(@RequestBody NormalFormGenerateRequest request) {
        try {
            Map<String, Object> response = principalNormalFormService.generateFormulas(request);

            if (response.containsKey("error")) {
                return ResponseEntity.badRequest().body(response);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                "success", false,
                "error", "Internal server error: " + e.getMessage()
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Check if formulas are valid for normal form calculation
     */
    @PostMapping("/check")
    public ResponseEntity<Map<String, Object>> checkFormulas(@RequestBody NormalFormCheckRequest request) {
        try {
            Map<String, Object> response = principalNormalFormService.checkFormulas(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                "valid", false,
                "errors", List.of("Internal server error: " + e.getMessage())
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "healthy"));
    }
}