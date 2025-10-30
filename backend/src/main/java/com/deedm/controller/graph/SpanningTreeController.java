package com.deedm.controller.graph;

import com.deedm.model.graph.SpanningTreeRequest;
import com.deedm.model.graph.SpanningTreeResponse;
import com.deedm.service.graph.SpanningTreeService;

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
@RequestMapping("/spanning-tree")
@CrossOrigin(origins = "*")
public class SpanningTreeController {

    @Autowired
    private SpanningTreeService spanningTreeService;

    @PostMapping("/calculate")
    public ResponseEntity<SpanningTreeResponse> calculateMinimalSpanningTree(@RequestBody SpanningTreeRequest request) {
        try {
            SpanningTreeResponse response = spanningTreeService.calculateMinimalSpanningTree(request);
            if (response.getErrorMessage() != null) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            SpanningTreeResponse errorResponse = new SpanningTreeResponse();
            errorResponse.setErrorMessage("Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateSpanningTreeInput(@RequestBody Map<String, String> input) {
        try {
            String nodesString = input.get("nodesString");
            String edgesString = input.get("edgesString");
            boolean directed = Boolean.parseBoolean(input.get("directed"));

            String errorMessage = spanningTreeService.validateInput(nodesString, edgesString, directed);
            boolean isValid = errorMessage == null;
            Map<String, Object> response = Map.of(
                "valid", isValid,
                "message", isValid ? "带权图的输入格式正确" : errorMessage
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
    public ResponseEntity<Map<String, Object>> generateRandomWeightedGraph() {
        try {
            Map<String, Object> result = spanningTreeService.generateRandomWeightedGraph();
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
     * 提供最小生成树图片的Web访问
     */
    @GetMapping("/tree-image/{filename}")
    public ResponseEntity<Resource> getSpanningTreeImage(@PathVariable String filename) {
        try {
            // 安全检查：允许WeightedGraph_、KruskalTree_、PrimTree_开头的文件
            if (!filename.matches("(WeightedGraph|KruskalTree|PrimTree)_[0-9]+\\.png")) {
                System.out.println("SpanningTreeController: 文件名格式不匹配: " + filename);
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