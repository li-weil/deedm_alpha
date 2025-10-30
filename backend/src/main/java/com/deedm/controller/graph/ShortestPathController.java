package com.deedm.controller.graph;

import com.deedm.model.graph.ShortestPathRequest;
import com.deedm.model.graph.ShortestPathResponse;
import com.deedm.service.graph.ShortestPathService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/shortest-path")
@CrossOrigin(origins = "*")
public class ShortestPathController {

    @Autowired
    private ShortestPathService shortestPathService;

    @PostMapping("/calculate")
    public ResponseEntity<ShortestPathResponse> calculateShortestPath(@RequestBody ShortestPathRequest request) {
        try {
            ShortestPathResponse response = shortestPathService.calculateShortestPath(request);
            if (!response.isSuccess()) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ShortestPathResponse errorResponse = new ShortestPathResponse();
            errorResponse.setSuccess(false);
            errorResponse.setErrorMessage("Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateInput(@RequestBody Map<String, String> input) {
        try {
            String nodesString = input.get("nodesString");
            String edgesString = input.get("edgesString");
            boolean directed = Boolean.parseBoolean(input.get("directed"));

            String errorMessage = shortestPathService.validateInput(nodesString, edgesString, directed);
            boolean isValid = errorMessage == null;

            Map<String, Object> response = Map.of(
                "valid", isValid,
                "message", isValid ? "最短路径图的输入格式正确" : errorMessage
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
            Map<String, Object> result = shortestPathService.generateRandomGraph();
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