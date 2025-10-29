package com.deedm.controller;

import com.deedm.model.GraphTravelRequest;
import com.deedm.model.GraphTravelResponse;
import com.deedm.service.GraphTravelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
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

            String errorMessage = graphTravelService.validateGraphInput(nodesString, edgesString, directed);
            boolean isValid = errorMessage == null;
            Map<String, Object> response = Map.of(
                "valid", isValid,
                "message", isValid ? "图的输入格式正确" : errorMessage
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

    /**
     * 提供图可视化图片的Web访问
     */
    @GetMapping("/graph-image/{filename}")
    public ResponseEntity<Resource> getGraphImage(@PathVariable String filename) {
        try {
            // 安全检查：只允许特定的文件名格式
            if (!filename.matches("GRAPH_[a-f0-9]+\\.png")) {
                System.out.println("GraphTravelController: 文件名格式不匹配: " + filename);
                return ResponseEntity.badRequest().build();
            }

            File imageFile = new File("./data/" + filename);
            if (!imageFile.exists() || !imageFile.isFile()) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(imageFile);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}