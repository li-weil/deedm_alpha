package com.deedm.controller.setrelfun;

import com.deedm.model.setrelfun.FunctionPropertyRequest;
import com.deedm.model.setrelfun.FunctionPropertyResponse;
import com.deedm.service.setrelfun.FunctionPropertyService;

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
@RequestMapping("/function-property")
@CrossOrigin(origins = "*")
public class FunctionPropertyController {

    @Autowired
    private FunctionPropertyService functionPropertyService;

    @PostMapping("/analyze")
    public ResponseEntity<FunctionPropertyResponse> analyzeFunction(@RequestBody FunctionPropertyRequest request) {
        try {
            FunctionPropertyResponse response = functionPropertyService.analyzeFunction(request);
            if (response.getErrorMessage() != null) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            FunctionPropertyResponse errorResponse = new FunctionPropertyResponse();
            errorResponse.setErrorMessage("Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateFunctionInput(@RequestBody Map<String, String> input) {
        try {
            String setAString = input.get("setAString");
            String setBString = input.get("setBString");
            String functionString = input.get("functionString");
            boolean intTypeElement = Boolean.parseBoolean(input.get("intTypeElement"));

            String errorMessage = functionPropertyService.validateFunctionInput(setAString, setBString, functionString, intTypeElement);
            boolean isValid = errorMessage == null;
            Map<String, Object> response = Map.of(
                "valid", isValid,
                "message", isValid ? "函数输入格式正确" : errorMessage
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
    public ResponseEntity<Map<String, Object>> generateRandomFunction() {
        try {
            // 这里可以添加随机生成函数的逻辑
            // 暂时返回一些预设的示例
            Map<String, Object> result = Map.of(
                "success", true,
                "setAString", "{1, 2, 3, 4, 5}",
                "setBString", "{a, b, c, d}",
                "functionString", "{<1, a>, <2, b>, <3, c>, <4, d>}",
                "intTypeElement", false,
                "options", Map.of(
                    "checkInjection", true,
                    "checkSurjection", true,
                    "checkBijection", true,
                    "showRelationMatrix", false,
                    "showRelationGraph", true
                )
            );
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
     * 提供函数关系图图片的Web访问
     */
    @GetMapping("/function-image/{filename}")
    public ResponseEntity<Resource> getFunctionImage(@PathVariable String filename) {
        try {
            // 安全检查：只允许FunctionF_开头的文件
            if (!filename.matches("FunctionF_[a-f0-9]+\\.png")) {
                System.out.println("FunctionPropertyController: 文件名格式不匹配: " + filename);
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