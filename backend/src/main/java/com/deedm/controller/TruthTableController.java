package com.deedm.controller;

import com.deedm.model.TruthTableRequest;
import com.deedm.model.TruthTableResponse;
import com.deedm.service.TruthTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/truth-table")
@CrossOrigin(origins = "*")
public class TruthTableController {

    @Autowired
    private TruthTableService truthTableService;

    @PostMapping("/generate")
    public ResponseEntity<TruthTableResponse> generateTruthTable(@RequestBody TruthTableRequest request) {
        try {
            TruthTableResponse response = truthTableService.generateTruthTable(request);
            if (response.getErrorMessage() != null) {
                return ResponseEntity.badRequest().body(response);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            TruthTableResponse errorResponse = new TruthTableResponse();
            errorResponse.setErrorMessage("Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateFormulas(@RequestBody List<String> formulas) {
        try {
            List<String> errors = truthTableService.validateFormulas(formulas);
            Map<String, Object> response = Map.of(
                "valid", errors.isEmpty(),
                "errors", errors
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                "valid", false,
                "errors", List.of("Internal server error: " + e.getMessage())
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "healthy"));
    }
}