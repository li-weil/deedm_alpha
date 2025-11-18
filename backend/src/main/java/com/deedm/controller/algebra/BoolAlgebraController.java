package com.deedm.controller.algebra;

import com.deedm.model.algebra.BoolAlgebraRequest;
import com.deedm.model.algebra.BoolAlgebraResponse;
import com.deedm.service.algebra.BoolAlgebraService;

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
@RequestMapping("/bool-algebra")
@CrossOrigin(origins = "*")
public class BoolAlgebraController {

    @Autowired
    private BoolAlgebraService boolAlgebraService;

    @PostMapping("/analyze")
    public ResponseEntity<BoolAlgebraResponse> analyzeBooleanAlgebra(@RequestBody BoolAlgebraRequest request) {
        try {
            BoolAlgebraResponse response = boolAlgebraService.analyzeBooleanAlgebra(request);
            if (response.getErrorMessage() != null) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            BoolAlgebraResponse errorResponse = new BoolAlgebraResponse();
            errorResponse.setErrorMessage("Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateInput(@RequestBody Map<String, String> input) {
        try {
            String mStr = input.get("m");
            String errorMessage = boolAlgebraService.validateInput(mStr);
            boolean isValid = errorMessage == null;
            Map<String, Object> response = Map.of(
                "valid", isValid,
                "message", isValid ? "输入格式正确" : errorMessage
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
    public ResponseEntity<Map<String, Object>> generateRandomInput() {
        try {
            Map<String, Object> result = boolAlgebraService.generateRandomInput();
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
     * 提供哈斯图图片的Web访问
     */
    @GetMapping("/hasse-diagram/{filename}")
    public ResponseEntity<Resource> getHasseDiagramImage(@PathVariable String filename) {
        try {
            // 安全检查：只允许HASSE_开头的哈斯图文件
            if (!filename.matches("HASSE_[a-f0-9]+\\.png")) {
                System.out.println("BoolAlgebraController: 文件名格式不匹配: " + filename);
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