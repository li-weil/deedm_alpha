package com.deedm.controller;

import com.deedm.service.FormulaSyntaxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Map;

@RestController
@RequestMapping("/formula-syntax")
public class FormulaSyntaxController {

    @Autowired
    private FormulaSyntaxService formulaSyntaxService;

    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeFormula(@RequestBody Map<String, String> request) {
        System.out.println("FormulaSyntaxController: 收到分析请求");
        System.out.println("FormulaSyntaxController: 请求参数: " + request);

        String latexFormula = request.get("formula");
        System.out.println("FormulaSyntaxController: 提取的公式: " + latexFormula);

        if (latexFormula == null || latexFormula.trim().isEmpty()) {
            System.out.println("FormulaSyntaxController: 公式为空，返回错误");
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", "Formula cannot be empty"));
        }

        System.out.println("FormulaSyntaxController: 调用服务分析公式");
        Map<String, Object> result = formulaSyntaxService.analyzeFormula(latexFormula);
        System.out.println("FormulaSyntaxController: 服务返回结果: " + result);

        ResponseEntity<Map<String, Object>> response = ResponseEntity.ok(result);
        System.out.println("FormulaSyntaxController: 返回响应");
        return response;
    }

    @PostMapping("/check")
    public ResponseEntity<Map<String, Object>> checkFormula(@RequestBody Map<String, String> request) {
        String latexFormula = request.get("formula");
        if (latexFormula == null || latexFormula.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("valid", false, "error", "Formula cannot be empty"));
        }

        Map<String, Object> result = formulaSyntaxService.checkFormula(latexFormula);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateRandomFormula() {
        Map<String, Object> result = formulaSyntaxService.generateRandomFormula();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/subformulas")
    public ResponseEntity<Map<String, Object>> getSubformulas(@RequestBody Map<String, String> request) {
        String latexFormula = request.get("formula");
        if (latexFormula == null || latexFormula.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", "Formula cannot be empty"));
        }

        Map<String, Object> result = formulaSyntaxService.getSubformulas(latexFormula);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/ast")
    public ResponseEntity<Map<String, Object>> getASTGraph(@RequestBody Map<String, String> request) {
        String latexFormula = request.get("formula");
        if (latexFormula == null || latexFormula.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", "Formula cannot be empty"));
        }

        Map<String, Object> result = formulaSyntaxService.getASTGraph(latexFormula);
        return ResponseEntity.ok(result);
    }

    /**
     * 提供AST图片的Web访问
     */
    @GetMapping("/ast-image/{filename}")
    public ResponseEntity<Resource> getASTImage(@PathVariable String filename) {
        try {
            // 安全检查：只允许特定的文件名格式
            if (!filename.matches("AST_[a-f0-9-]+\\.png")) {
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