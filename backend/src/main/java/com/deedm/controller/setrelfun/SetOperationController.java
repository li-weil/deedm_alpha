package com.deedm.controller.setrelfun;

import com.deedm.model.setrelfun.SetOperationRequest;
import com.deedm.model.setrelfun.SetOperationResponse;
import com.deedm.service.setrelfun.SetOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/set-operation")
@CrossOrigin(origins = "*")
public class SetOperationController {

    @Autowired
    private SetOperationService setOperationService;

    @PostMapping("/calculate")
    public SetOperationResponse calculateSetOperations(@RequestBody SetOperationRequest request) {
        try {
            SetOperationResponse response = setOperationService.performSetOperations(request);
            if (response.getErrorMessage() != null) {
                return response;
            }
            return response;
        } catch (Exception e) {
            SetOperationResponse errorResponse = new SetOperationResponse();
            errorResponse.setErrorMessage("Internal server error: " + e.getMessage());
            return errorResponse;
        }
    }

    @PostMapping("/validate")
    public Map<String, Object> validateSetInput(@RequestBody Map<String, Object> input) {
        try {
            String setUString = (String) input.get("setU");
            String setAString = (String) input.get("setA");
            String setBString = (String) input.get("setB");
            boolean intTypeElement = Boolean.parseBoolean((String) input.getOrDefault("intTypeElement", "false"));

            Map<String, Object> response = setOperationService.validateSetInput(setUString, setAString, setBString, intTypeElement);
            return response;
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                "valid", false,
                "message", "Internal server error: " + e.getMessage()
            );
            return errorResponse;
        }
    }

    @GetMapping("/generate")
    public Map<String, Object> generateRandomSets() {
        try {
            Map<String, Object> result = setOperationService.generateRandomSets();
            return result;
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                "success", false,
                "message", "Internal server error: " + e.getMessage()
            );
            return errorResponse;
        }
    }
}