package com.deedm.controller.graph;

import com.deedm.model.graph.ShortestPathRequest;
import com.deedm.model.graph.ShortestPathResponse;
import com.deedm.service.graph.ShortestPathService;

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

    /**
     * 提供最短路径图片的Web访问
     */
    @GetMapping("/graph-image/{filename}")
    public ResponseEntity<Resource> getShortestPathImage(@PathVariable String filename) {
        try {
            // 安全检查：允许GraphVisualization_和ShortestPath_开头的文件
            if (!filename.matches("(GraphVisualization|ShortestPath)_[0-9]+\\.png")) {
                System.out.println("ShortestPathController: 文件名格式不匹配: " + filename);
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