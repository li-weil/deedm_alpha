package com.deedm.controller;

import com.deedm.model.GraphTravelRequest;
import com.deedm.model.GraphTravelResponse;
import com.deedm.service.GraphTravelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/graph-travel")
@CrossOrigin(origins = "*")
public class GraphTravelController {

    @Autowired
    private GraphTravelService graphTravelService;

    @PostMapping("/analyze")
    public ResponseEntity<GraphTravelResponse> analyzeGraph(@RequestBody GraphTravelRequest request) {
        try {
            GraphTravelResponse response = graphTravelService.analyzeGraph(request);
            if (response.getErrorMessage() != null) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            GraphTravelResponse errorResponse = new GraphTravelResponse();
            errorResponse.setErrorMessage("Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateGraphInput(@RequestBody Map<String, String> input) {
        try {
            String nodesString = input.get("nodesString");
            String edgesString = input.get("edgesString");
            boolean directed = Boolean.parseBoolean(input.get("directed"));

            boolean isValid = graphTravelService.validateGraphInput(nodesString, edgesString, directed);
            Map<String, Object> response = Map.of(
                "valid", isValid,
                "message", isValid ? "图的输入格式正确" : "图的输入格式不正确"
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
    public ResponseEntity<Map<String, Object>> generateRandomGraph() {
        try {
            Map<String, Object> result = graphTravelService.generateRandomGraph();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                "success", false,
                "message", "Internal server error: " + e.getMessage()
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}