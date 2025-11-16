package com.deedm.controller.counting;

import com.deedm.model.counting.CountIntegerRequest;
import com.deedm.model.counting.CountIntegerResponse;
import com.deedm.service.counting.CountIntegerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/count-integer")
@CrossOrigin(origins = "*")
public class CountIntegerController {

    @Autowired
    private CountIntegerService countIntegerService;

    @PostMapping("/count")
    public CountIntegerResponse countIntegers(@RequestBody CountIntegerRequest request) {
        try {
            CountIntegerResponse response = countIntegerService.countIntegers(request);
            return response;
        } catch (Exception e) {
            CountIntegerResponse errorResponse = new CountIntegerResponse();
            errorResponse.setErrorMessage("Internal server error: " + e.getMessage());
            return errorResponse;
        }
    }

    @PostMapping("/validate")
    public Map<String, Object> validateInput(@RequestBody Map<String, String> input) {
        try {
            int start = Integer.parseInt(input.get("start"));
            int end = Integer.parseInt(input.get("end"));

            String errorMessage = countIntegerService.validateInput(start, end);
            boolean isValid = errorMessage == null;

            return Map.of(
                "valid", isValid,
                "message", isValid ? "输入格式正确" : errorMessage
            );

        } catch (NumberFormatException e) {
            return Map.of(
                "valid", false,
                "message", "起始和终止整数必须是有效的整数"
            );
        } catch (Exception e) {
            return Map.of(
                "valid", false,
                "message", "Internal server error: " + e.getMessage()
            );
        }
    }

    @PostMapping("/example")
    public CountIntegerResponse getExample(@RequestBody Map<String, String> request) {
        try {
            String exampleType = request.get("exampleType");
            CountIntegerResponse response = countIntegerService.getExample(exampleType);
            return response;
        } catch (Exception e) {
            CountIntegerResponse errorResponse = new CountIntegerResponse();
            errorResponse.setErrorMessage("Internal server error: " + e.getMessage());
            return errorResponse;
        }
    }
}