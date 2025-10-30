package com.deedm.controller.logic;

import com.deedm.model.logic.TruthValueRequest;
import com.deedm.model.logic.TruthValueResponse;
import com.deedm.service.logic.TruthValueService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/truth-value")
@CrossOrigin(origins = "*")
public class TruthValueController {

    @Autowired
    private TruthValueService truthValueService;

    @PostMapping("/calculate")
    public ResponseEntity<TruthValueResponse> calculateTruthValue(@RequestBody TruthValueRequest request) {
        try {
            TruthValueResponse response = truthValueService.calculateTruthValue(request);
            if (response.getErrorMessage() != null) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            TruthValueResponse errorResponse = new TruthValueResponse();
            errorResponse.setErrorMessage("Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Truth value service is healthy");
    }
}