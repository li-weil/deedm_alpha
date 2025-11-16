package com.deedm.service.counting;

import com.deedm.legacy.counting.ExprCalculator;
import com.deedm.model.counting.ExpressionCalculatorRequest;
import com.deedm.model.counting.ExpressionCalculatorResponse;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 组合表达式计算服务
 */
@Service
public class ExpressionCalculatorService {

    // 使用正则表达式规范表达式字符串的合法性，进行静态检查，每次使用到都要
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("[0-9\\.+-/*^!PC,() ]+");

    /**
     * 执行组合表达式计算
     * @param request 计算请求
     * @return 计算响应
     */
    public ExpressionCalculatorResponse calculateExpression(ExpressionCalculatorRequest request) {
        String expression = request.getExpression();

        // 表达式合法性验证
        if (!validateExpression(expression)) {
            return new ExpressionCalculatorResponse(false, "表达式验证失败");
        }

        try {
            // 调用legacy代码执行表达式计算
            String result = ExprCalculator.executeExpression(expression);

            if ("error".equals(result)) {
                return new ExpressionCalculatorResponse(false, "表达式计算错误");
            }

            // 添加花括号以改善LaTeX显示效果
            String processedExpression = addCurlyBraces(expression);

            // 生成LaTeX公式
            String formula = String.format("\\text{要计算的表达式是：} %s \\quad\\quad \\text{计算结果：} %s = %s",
                    processedExpression, processedExpression, result);

            return new ExpressionCalculatorResponse(true, "计算成功", result, expression, formula);

        } catch (Exception e) {
            return new ExpressionCalculatorResponse(false, "计算异常: " + e.getMessage());
        }
    }

    /**
     * 验证表达式输入的合法性
     * @param expression 表达式
     * @return 是否合法
     */
    public boolean validateExpression(String expression) {
        if (expression == null || "".equals(expression.trim())) {
            return false; // 非空校验
        }

        Matcher matcher = EXPRESSION_PATTERN.matcher(expression);
        return matcher.matches(); // 表达式字符串合法性校验
    }

    /**
     * 为了处理2^3!、2^3!!等复杂情况，添加花括号，因为LaTeX下无法正确解析3个及以上阶乘符号，因此给阶乘添加括号
     * 例如2^{3!}
     *
     * 对于阶乘和指数运算，当指数是括号时，也需要添加花括号，例如2^(1+1)变成2^{(1+1)}
     *
     * @param input 输入表达式
     * @return 处理后的表达式
     */
    private String addCurlyBraces(String input) {
        StringBuilder result = new StringBuilder(input);

        for (int i = 1; i < result.length(); i++) {
            if (result.charAt(i) == '!') { // 情况1：阶乘符号
                for (int j = i-2; j >= 0; j--) {
                    // 查找'^'时，在'{'添加并在'!'添加'}'
                    if (result.charAt(j) != '!' && result.charAt(j) != '{' && result.charAt(j) != '}'
                            && (result.charAt(j) < '0' || result.charAt(j) > '9')) {
                        if (result.charAt(j) == '^') {
                            result.insert(j+1, "{");
                            result.insert(i+2, "}");
                            i += 2; // 因为添加了偶数个字符，i加2
                        }
                        break;
                    }
                }
            }
            if (result.charAt(i) == '^' && i+1 < result.length() && result.charAt(i+1) == '(') { // 情况2：指数为括号
                result.insert(i+1, "{");
                java.util.Stack<String> stack = new java.util.Stack<>();
                stack.push("(");
                int j;
                for (j = i+2; j < result.length() && !stack.empty(); j++) { // 查找匹配
                    if (result.charAt(j) == '(') {
                        stack.push("(");
                    } else if (result.charAt(j) == ')') {
                        stack.pop();
                    }
                }
                result.insert(j+1, "}");
                i += 1; // 调整索引
            }
        }
        return result.toString();
    }
}