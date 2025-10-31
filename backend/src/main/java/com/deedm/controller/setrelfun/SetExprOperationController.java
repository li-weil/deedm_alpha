package com.deedm.controller.setrelfun;

import com.deedm.model.setrelfun.SetExprOperationRequest;
import com.deedm.model.setrelfun.SetExprOperationResponse;
import com.deedm.service.setrelfun.SetExprOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/set-expression-operation")
@CrossOrigin(origins = "*")
public class SetExprOperationController {

    @Autowired
    private SetExprOperationService setExprOperationService;

    @PostMapping("/calculate")
    public SetExprOperationResponse calculateSetExpression(@RequestBody SetExprOperationRequest request) {
        try {
            return setExprOperationService.executeSetExpression(request);
        } catch (Exception e) {
            SetExprOperationResponse errorResponse = new SetExprOperationResponse();
            errorResponse.setSuccess(false);
            errorResponse.setErrorMessage("Internal server error: " + e.getMessage());
            return errorResponse;
        }
    }

    @PostMapping("/validate")
    public Map<String, Object> validateSetExpression(@RequestBody SetExprOperationRequest request) {
        try {
            return setExprOperationService.validateSetExpression(request);
        } catch (Exception e) {
            return Map.of(
                "valid", false,
                "message", "Internal server error: " + e.getMessage()
            );
        }
    }

    @GetMapping("/generate")
    public Map<String, Object> generateRandomSets() {
        try {
            return setExprOperationService.generateRandomSets();
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "message", "Internal server error: " + e.getMessage()
            );
        }
    }
}