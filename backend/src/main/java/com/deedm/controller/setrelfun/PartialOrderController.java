package com.deedm.controller.setrelfun;

import com.deedm.model.setrelfun.PartialOrderRequest;
import com.deedm.model.setrelfun.PartialOrderResponse;
import com.deedm.service.setrelfun.PartialOrderService;

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
@RequestMapping("/partial-order")
@CrossOrigin(origins = "*")
public class PartialOrderController {

    @Autowired
    private PartialOrderService partialOrderService;

    @PostMapping("/calculate")
    public ResponseEntity<PartialOrderResponse> calculatePartialOrder(@RequestBody PartialOrderRequest request) {
        try {
            PartialOrderResponse response = partialOrderService.analyzePartialOrder(request);
            if (response.getErrorMessage() != null) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            PartialOrderResponse errorResponse = new PartialOrderResponse();
            errorResponse.setErrorMessage("Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validatePartialOrderInput(@RequestBody Map<String, Object> input) {
        try {
            String setAString = (String) input.get("setAString");
            String setSString = (String) input.get("setSString");
            String relationRString = (String) input.get("relationRString");
            Boolean intTypeElement = (Boolean) input.get("intTypeElement");
            Boolean useDivideRelation = (Boolean) input.get("useDivideRelation");

            String errorMessage = partialOrderService.validatePartialOrderInput(
                setAString, setSString, relationRString, intTypeElement, useDivideRelation);

            boolean isValid = errorMessage == null;
            Map<String, Object> response = Map.of(
                "valid", isValid,
                "message", isValid ? "偏序关系的输入格式正确" : errorMessage
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

    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateRandomPartialOrder() {
        try {
            Map<String, Object> result = partialOrderService.generateRandomPartialOrder();
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
     * 提供偏序关系图片的Web访问
     */
    @GetMapping("/partial-order-image/{filename}")
    public ResponseEntity<Resource> getPartialOrderImage(@PathVariable String filename) {
        try {
            // 安全检查：允许PartialOrder_和RelationR_开头的文件
            if (!filename.matches("(PartialOrder|RelationR)_[a-f0-9]+\\.png")) {
                System.out.println("PartialOrderController: 文件名格式不匹配: " + filename);
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