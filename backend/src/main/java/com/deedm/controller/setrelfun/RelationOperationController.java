package com.deedm.controller.setrelfun;

import com.deedm.model.setrelfun.RelationOperationRequest;
import com.deedm.model.setrelfun.RelationOperationResponse;
import com.deedm.service.setrelfun.RelationOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.io.File;

@RestController
@RequestMapping("/setrelfun/relation-operation")
@CrossOrigin(origins = "*")
public class RelationOperationController {

    @Autowired
    private RelationOperationService relationOperationService;

    @PostMapping("/calculate")
    public RelationOperationResponse calculateRelationOperation(@RequestBody RelationOperationRequest request) {
        return relationOperationService.executeRelationOperation(request);
    }

    @PostMapping("/validate")
    public Map<String, Object> validateRelationOperation(@RequestBody RelationOperationRequest request) {
        return relationOperationService.validateRelationOperation(request);
    }

    @PostMapping("/generate")
    public Map<String, Object> generateRandomRelations() {
        return relationOperationService.generateRandomRelations();
    }

    /**
     * 提供关系图可视化图片的Web访问
     */
    @GetMapping("/relation-image/{filename}")
    public ResponseEntity<Resource> getRelationImage(@PathVariable String filename) {
        try {
            // 安全检查：允许RelationR_或RelationS_开头的文件
            if (!filename.matches("(RelationR_|RelationS_)[a-zA-Z0-9]+\\.png")) {
                System.out.println("RelationOperationController: 文件名格式不匹配: " + filename);
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