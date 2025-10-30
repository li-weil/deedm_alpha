package com.deedm.controller.graph;

import com.deedm.model.graph.SpecialGraphRequest;
import com.deedm.model.graph.SpecialGraphResponse;
import com.deedm.service.graph.SpecialGraphService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping("/special-graph")
@CrossOrigin(origins = "*")
public class SpecialGraphController {

    @Autowired
    private SpecialGraphService specialGraphService;

    @PostMapping("/generate")
    public ResponseEntity<SpecialGraphResponse> generateSpecialGraphs(@RequestBody SpecialGraphRequest request) {
        try {
            SpecialGraphResponse response = specialGraphService.generateSpecialGraphs(request);
            if (response.getErrorMessage() != null) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            SpecialGraphResponse errorResponse = new SpecialGraphResponse();
            errorResponse.setErrorMessage("Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 提供特殊图可视化图片的Web访问
     */
    @GetMapping("/graph-image/{filename}")
    public ResponseEntity<Resource> getSpecialGraphImage(@PathVariable String filename) {
        try {
            // 安全检查：只允许Special_开头的特殊图文件
            if (!filename.matches("Special_[a-zA-Z]+_[a-f0-9]+\\.png")) {
                System.out.println("SpecialGraphController: 文件名格式不匹配: " + filename);
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