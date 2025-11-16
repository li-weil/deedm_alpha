package com.deedm.controller.counting;

import com.deedm.model.counting.CountStringRequest;
import com.deedm.model.counting.CountStringResponse;
import com.deedm.service.counting.CountStringService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/count-string")
@CrossOrigin(origins = "*")
public class CountStringController {

    @Autowired
    private CountStringService countStringService;

    @PostMapping("/count")
    public CountStringResponse countStrings(@RequestBody CountStringRequest request) {
        try {
            CountStringResponse response = countStringService.countStrings(request);
            return response;
        } catch (Exception e) {
            CountStringResponse errorResponse = new CountStringResponse();
            errorResponse.setErrorMessage("Internal server error: " + e.getMessage());
            return errorResponse;
        }
    }

    @PostMapping("/validate")
    public Map<String, Object> validateInput(@RequestBody Map<String, String> input) {
        try {
            String baseSet = input.get("baseSet");
            String lengthString = input.get("lengthString");

            String errorMessage = countStringService.validateInput(baseSet, lengthString);
            boolean isValid = errorMessage == null;

            return Map.of(
                "valid", isValid,
                "message", isValid ? "输入格式正确" : errorMessage
            );

        } catch (Exception e) {
            return Map.of(
                "valid", false,
                "message", "Internal server error: " + e.getMessage()
            );
        }
    }

    @PostMapping("/example")
    public CountStringResponse getExample(@RequestBody Map<String, String> request) {
        try {
            String exampleType = request.get("exampleType");
            CountStringResponse response = countStringService.getExample(exampleType);
            return response;
        } catch (Exception e) {
            CountStringResponse errorResponse = new CountStringResponse();
            errorResponse.setErrorMessage("Internal server error: " + e.getMessage());
            return errorResponse;
        }
    }
}