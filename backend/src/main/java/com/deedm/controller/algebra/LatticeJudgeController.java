package com.deedm.controller.algebra;

import com.deedm.model.algebra.LatticeJudgeRequest;
import com.deedm.model.algebra.LatticeJudgeResponse;
import com.deedm.service.algebra.LatticeJudgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/algebra")
@CrossOrigin(origins = "*")
public class LatticeJudgeController {

    @Autowired
    private LatticeJudgeService latticeJudgeService;

    @PostMapping("/lattice-judge")
    public ResponseEntity<LatticeJudgeResponse> judgeLattice(@RequestBody LatticeJudgeRequest request) {
        try {
            LatticeJudgeResponse response = latticeJudgeService.judgeLattice(request);

            // 添加调试信息 - 检查最终响应
            System.out.println("=== Controller最终响应调试信息 ===");
            System.out.println("response.success: " + response.isSuccess());
            System.out.println("response.isPartialOrder: " + response.isPartialOrder());
            System.out.println("response.partialOrderReason: " + response.getPartialOrderReason());
            System.out.println("=== Controller最终响应调试信息结束 ===");

            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            LatticeJudgeResponse errorResponse = new LatticeJudgeResponse();
            errorResponse.setErrorMessage("格判断服务内部错误: " + e.getMessage());
            errorResponse.setType("lattice-judge");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/lattice-judge/generate")
    public ResponseEntity<Map<String, Object>> generateLatticeExample() {
        try {
            Map<String, Object> response = new HashMap<>();

            // 生成示例数据（模拟原Java应用的随机生成功能）
            response.put("setA", "{a, b, c, d, e}");
            response.put("relationR", "{<a, b>, <a, c>, <b, d>, <c, d>, <d, e>}");
            response.put("intTypeElement", false);
            response.put("useHasseDiagram", true);
            response.put("checkDistributive", false);
            response.put("checkBounded", false);
            response.put("checkComplemented", false);
            response.put("checkBoolean", false);

            response.put("success", true);
            response.put("message", "已生成格判断示例");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "生成示例失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/lattice-judge/validate")
    public ResponseEntity<Map<String, Object>> validateLatticeInput(@RequestBody LatticeJudgeRequest request) {
        try {
            Map<String, Object> response = new HashMap<>();
            boolean valid = true;
            String message = "输入格式正确";

            // 这里可以添加输入验证逻辑
            if (request.getSetAString() == null || request.getSetAString().trim().isEmpty()) {
                valid = false;
                message = "集合A不能为空";
            } else if (request.getRelationRString() == null || request.getRelationRString().trim().isEmpty()) {
                valid = false;
                message = "关系R不能为空";
            }

            response.put("valid", valid);
            response.put("message", message);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("valid", false);
            errorResponse.put("message", "验证失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 提供哈斯图图片的Web访问
     */
    @GetMapping("/lattice-judge/hasse-diagram/{filename}")
    public ResponseEntity<Resource> getHasseDiagramImage(@PathVariable String filename) {
        try {
            // 安全检查：只允许HASSE_开头的哈斯图文件
            if (!filename.matches("HASSE_[a-f0-9]+\\.png")) {
                System.out.println("LatticeJudgeController: 文件名格式不匹配: " + filename);
                return ResponseEntity.badRequest().build();
            }

            File imageFile = new File("./data/" + filename);
            if (!imageFile.exists() || !imageFile.isFile()) {
                System.out.println("LatticeJudgeController: 文件不存在: " + imageFile.getAbsolutePath());
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(imageFile);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (Exception e) {
            System.err.println("LatticeJudgeController: 获取哈斯图图片失败: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}