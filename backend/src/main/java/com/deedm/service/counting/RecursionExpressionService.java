package com.deedm.service.counting;

import com.deedm.legacy.counting.RecuExpressionCalculator;
import com.deedm.model.counting.RecursionExpressionRequest;
import com.deedm.model.counting.RecursionExpressionResponse;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 递归表达式计算服务
 */
@Service
public class RecursionExpressionService {

    // 使用正则表达式规范表达式字符串的合法性，进行静态检查，每次使用到都要
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("[0-9\\.+-/*^!PCabn,() ]+");

    /**
     * 执行递归表达式计算
     * @param request 计算请求
     * @return 计算响应
     */
    public RecursionExpressionResponse calculateRecursionExpression(RecursionExpressionRequest request) {
        String expression = request.getExpression();
        String n = request.getN();
        String a = request.getA();
        String b = request.getB();

        // 表达式合法性验证
        String validationError = validateRecursionExpression(expression, n, a, b);
        if (validationError != null) {
            return new RecursionExpressionResponse(false, validationError);
        }

        try {
            // 调用legacy代码执行递归表达式计算
            String result = RecuExpressionCalculator.executeRecuExpression(expression, a, b, n);

            if ("error".equals(result)) {
                return new RecursionExpressionResponse(false, "递归表达式计算错误");
            }

            // 添加花括号以改善LaTeX显示效果
            String processedExpression = addCurlyBraces(expression);

            // 生成LaTeX公式
            String formula = String.format("\\text{要进行计算的递归表达式是：} %s \\quad\\quad \\text{计算结果：} a_%s = %s",
                    processedExpression, n, result);

            return new RecursionExpressionResponse(true, "计算成功", result, expression, formula, n, a, b);

        } catch (Exception e) {
            return new RecursionExpressionResponse(false, "计算异常: " + e.getMessage());
        }
    }

    /**
     * 验证递归表达式输入的合法性
     * @param expression 表达式
     * @param n 递推次数n
     * @param a 初始值a
     * @param b 初始值b
     * @return 错误信息，如果验证通过则返回null
     */
    public String validateRecursionExpression(String expression, String n, String a, String b) {
        // 表达式非空校验
        if (expression == null || "".equals(expression.trim())) {
            return "递归表达式不能为空";
        }

        // 表达式字符串合法性校验
        Matcher matcher = EXPRESSION_PATTERN.matcher(expression);
        if (!matcher.matches()) {
            return "递归表达式中有非法字符";
        }

        // n必须是合法整数
        try {
            Integer.parseInt(n);
        } catch (Exception exc) {
            return "递归次数n不是合法的整数";
        }

        // a和b必须是合法数字，如果为空则默认使用1
        try {
            if (a != null && !"".equals(a.trim())) {
                Float.parseFloat(a);
            }
            if (b != null && !"".equals(b.trim())) {
                Float.parseFloat(b);
            }
        } catch (Exception exc) {
            return "初始值a或b不是合法的数字或小数";
        }

        return null; // 验证通过
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