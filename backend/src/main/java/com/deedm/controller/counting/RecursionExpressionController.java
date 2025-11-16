package com.deedm.controller.counting;

import com.deedm.model.counting.RecursionExpressionRequest;
import com.deedm.model.counting.RecursionExpressionResponse;
import com.deedm.service.counting.RecursionExpressionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 递归表达式计算控制器
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/counting/recursion-expression")
public class RecursionExpressionController {

    @Autowired
    private RecursionExpressionService recursionExpressionService;

    /**
     * 计算递归表达式
     * @param request 计算请求
     * @return 计算响应
     */
    @PostMapping("/calculate")
    public RecursionExpressionResponse calculateRecursionExpression(@RequestBody RecursionExpressionRequest request) {
        try {
            return recursionExpressionService.calculateRecursionExpression(request);
        } catch (Exception e) {
            return new RecursionExpressionResponse(false, "服务异常: " + e.getMessage());
        }
    }

    /**
     * 验证递归表达式合法性
     * @param request 验证请求
     * @return 验证响应
     */
    @PostMapping("/validate")
    public Map<String, Object> validateRecursionExpression(@RequestBody RecursionExpressionRequest request) {
        try {
            String validationError = recursionExpressionService.validateRecursionExpression(
                request.getExpression(),
                request.getN(),
                request.getA(),
                request.getB()
            );

            boolean isValid = validationError == null;
            Map<String, Object> response = Map.of(
                "valid", isValid,
                "message", isValid ? "递归表达式格式正确" : validationError
            );

            return response;
        } catch (Exception e) {
            return Map.of(
                "valid", false,
                "message", "验证异常: " + e.getMessage()
            );
        }
    }

    /**
     * 生成随机递归表达式示例
     * @return 示例请求
     */
    @GetMapping("/generate")
    public Map<String, Object> generateRandomRecursionExpression() {
        try {
            // 提供一些典型的递归表达式示例
            String[] examples = {
                "2*a + 1",        // 线性递归
                "a + b",          // 二阶线性递归
                "2*a + 3*b",      // 常系数线性递归
                "a^2 + 1",        // 非线性递归
                "a + b + n"       // 带非齐次项
            };

            int randomIndex = (int)(Math.random() * examples.length);
            String expression = examples[randomIndex];

            return Map.of(
                "success", true,
                "expression", expression,
                "n", "5",
                "a", "1",
                "b", "1",
                "message", "已生成随机递归表达式示例"
            );
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "message", "生成随机示例异常: " + e.getMessage()
            );
        }
    }
}