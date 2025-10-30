package com.deedm.controller.graph;

import com.deedm.model.graph.HuffmanTreeRequest;
import com.deedm.model.graph.HuffmanTreeResponse;
import com.deedm.service.graph.HuffmanTreeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/huffman-tree")
@CrossOrigin(origins = "*")
public class HuffmanTreeController {

    @Autowired
    private HuffmanTreeService huffmanTreeService;

    @PostMapping("/construct")
    public ResponseEntity<HuffmanTreeResponse> constructHuffmanTree(@RequestBody HuffmanTreeRequest request) {
        try {
            HuffmanTreeResponse response = huffmanTreeService.constructHuffmanTree(request);
            if (!response.isSuccess()) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            HuffmanTreeResponse errorResponse = new HuffmanTreeResponse();
            errorResponse.setSuccess(false);
            errorResponse.setErrorMessage("服务器内部错误: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateInput(@RequestBody Map<String, String> input) {
        try {
            String leafString = input.get("leafString");

            String errorMessage = huffmanTreeService.validateInput(leafString);
            boolean isValid = errorMessage == null;
            Map<String, Object> response = Map.of(
                "valid", isValid,
                "message", isValid ? "带权叶节点集合格式正确" : errorMessage
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                "valid", false,
                "message", "服务器内部错误: " + e.getMessage()
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateRandomInputs() {
        try {
            Map<String, Object> result = huffmanTreeService.generateRandomInputs();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                "success", false,
                "message", "服务器内部错误: " + e.getMessage()
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}