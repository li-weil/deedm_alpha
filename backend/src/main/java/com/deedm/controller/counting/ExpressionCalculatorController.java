package com.deedm.controller.counting;

import com.deedm.model.counting.ExpressionCalculatorRequest;
import com.deedm.model.counting.ExpressionCalculatorResponse;
import com.deedm.service.counting.ExpressionCalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 组合表达式计算控制器
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/counting/expression-calculator")
public class ExpressionCalculatorController {

    @Autowired
    private ExpressionCalculatorService expressionCalculatorService;

    /**
     * 计算组合表达式
     * @param request 计算请求
     * @return 计算响应
     */
    @PostMapping("/calculate")
    public ExpressionCalculatorResponse calculateExpression(@RequestBody ExpressionCalculatorRequest request) {
        try {
            return expressionCalculatorService.calculateExpression(request);
        } catch (Exception e) {
            return new ExpressionCalculatorResponse(false, "服务异常: " + e.getMessage());
        }
    }

    /**
     * 验证表达式合法性
     * @param request 验证请求
     * @return 验证响应
     */
    @PostMapping("/validate")
    public ExpressionCalculatorResponse validateExpression(@RequestBody ExpressionCalculatorRequest request) {
        try {
            boolean isValid = expressionCalculatorService.validateExpression(request.getExpression());
            if (isValid) {
                return new ExpressionCalculatorResponse(true, "表达式格式正确");
            } else {
                return new ExpressionCalculatorResponse(false, "表达式格式不正确");
            }
        } catch (Exception e) {
            return new ExpressionCalculatorResponse(false, "验证异常: " + e.getMessage());
        }
    }
}