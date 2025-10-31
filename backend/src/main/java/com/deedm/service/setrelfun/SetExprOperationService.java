package com.deedm.service.setrelfun;

import com.deedm.model.setrelfun.SetExprOperationRequest;
import com.deedm.model.setrelfun.SetExprOperationResponse;
import com.deedm.legacy.setrelfun.Set;
import com.deedm.legacy.setrelfun.SetExpr;
import com.deedm.legacy.setrelfun.SetrelfunUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SetExprOperationService {

    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("(\\\\cap|\\\\cup|\\\\oplus|\\\\overline|-|A|B|\\}|\\{|\\)|\\(| )+");

    private int counter = 0;

    public SetExprOperationResponse executeSetExpression(SetExprOperationRequest request) {
        SetExprOperationResponse response = new SetExprOperationResponse();

        try {
            // 验证输入
            String validationError = validateInput(request);
            if (validationError != null) {
                response.setSuccess(false);
                response.setErrorMessage(validationError);
                return response;
            }

            // 解析集合
            Set setU = parseSet(request.getSetU(), request.isIntTypeElement());
            Set setA = parseSet(request.getSetA(), request.isIntTypeElement());
            Set setB = parseSet(request.getSetB(), request.isIntTypeElement());

            if (setU == null || setA == null || setB == null) {
                response.setSuccess(false);
                response.setErrorMessage("集合格式不正确");
                return response;
            }

            // 验证子集关系
            if (!setU.isSubset(setA)) {
                response.setSuccess(false);
                response.setErrorMessage("集合A不是全集U的子集");
                return response;
            }

            if (!setU.isSubset(setB)) {
                response.setSuccess(false);
                response.setErrorMessage("集合B不是全集U的子集");
                return response;
            }

            // 执行表达式计算
            String expression = request.getExpression().replaceAll(" ", "");
            char[] setErrorElements = SetrelfunUtil.extractSetElements("e,r,r,o,r", false);
            Set errorMessageSet = new Set(setErrorElements);

            Set resultSet = SetExpr.executeExpression(expression, setU, setA, setB, true);

            if (resultSet.equalsTo(errorMessageSet)) {
                response.setSuccess(false);
                response.setErrorMessage("表达式计算失败，请检查表达式格式");
                return response;
            }

            // 转换为LaTeX格式
            String latexExpression = convertToLatexString(expression);
            String latexResult = resultSet.toLaTeXString();

            // 构建响应 - 重要：包含resultType和type参数用于前端LaTeX生成
            counter++;
            response.setSuccess(true);
            response.setResultType("setExpressionOperation"); // 关键参数：用于latexGenerator.js判断
            response.setType("setExpressionOperation"); // 关键参数：用于前端判断类型
            response.setMessage("集合表达式运算完成");
            response.setResult(resultSet.toString());
            response.setLatexResult(latexResult);
            response.setFormula(String.format("U = %s, A = %s, B = %s, expression = %s",
                setU.toLaTeXString(), setA.toLaTeXString(), setB.toLaTeXString(), latexExpression));
            response.setIndex(counter);

        } catch (Exception e) {
            response.setSuccess(false);
            response.setErrorMessage("运算过程中发生错误: " + e.getMessage());
        }

        return response;
    }

    public Map<String, Object> validateSetExpression(SetExprOperationRequest request) {
        Map<String, Object> result = new HashMap<>();

        try {
            String validationError = validateInput(request);
            if (validationError != null) {
                result.put("valid", false);
                result.put("message", validationError);
                return result;
            }

            // 解析集合并验证
            Set setU = parseSet(request.getSetU(), request.isIntTypeElement());
            Set setA = parseSet(request.getSetA(), request.isIntTypeElement());
            Set setB = parseSet(request.getSetB(), request.isIntTypeElement());

            if (setU == null || setA == null || setB == null) {
                result.put("valid", false);
                result.put("message", "集合格式不正确");
                return result;
            }

            if (!setU.isSubset(setA)) {
                result.put("valid", false);
                result.put("message", "集合A不是全集U的子集");
                return result;
            }

            if (!setU.isSubset(setB)) {
                result.put("valid", false);
                result.put("message", "集合B不是全集U的子集");
                return result;
            }

            // 测试表达式
            String expression = request.getExpression().replaceAll(" ", "");
            char[] setErrorElements = SetrelfunUtil.extractSetElements("e,r,r,o,r", false);
            Set errorMessageSet = new Set(setErrorElements);

            Set testResult = SetExpr.executeExpression(expression, setU, setA, setB, true);

            if (testResult.equalsTo(errorMessageSet)) {
                result.put("valid", false);
                result.put("message", "表达式格式不正确或计算失败");
                return result;
            }

            result.put("valid", true);
            result.put("message", "输入格式正确，可以进行运算");

        } catch (Exception e) {
            result.put("valid", false);
            result.put("message", "验证过程中发生错误: " + e.getMessage());
        }

        return result;
    }

    public Map<String, Object> generateRandomSets() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 默认生成整数集合
            char[] elements = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
            Set setU = new Set(elements);
            Set setA = Set.randomGenerateSubset(setU);
            Set setB = Set.randomGenerateSubset(setU);

            // 默认表达式
            String expression = "\\overline{A\\cap B \\oplus A}";

            result.put("success", true);
            result.put("message", "随机集合生成完成");
            result.put("setU", setU.toString());
            result.put("setA", setA.toString());
            result.put("setB", setB.toString());
            result.put("expression", expression);
            result.put("intTypeElement", true);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "生成随机集合失败: " + e.getMessage());
        }

        return result;
    }

    private String validateInput(SetExprOperationRequest request) {
        if (request.getSetU() == null || request.getSetU().trim().isEmpty()) {
            return "全集U不能为空";
        }

        if (request.getSetA() == null || request.getSetA().trim().isEmpty()) {
            return "集合A不能为空";
        }

        if (request.getSetB() == null || request.getSetB().trim().isEmpty()) {
            return "集合B不能为空";
        }

        if (request.getExpression() == null || request.getExpression().trim().isEmpty()) {
            return "集合表达式不能为空";
        }

        // 验证表达式格式
        String expression = request.getExpression().replaceAll(" ", "");
        Matcher matcher = EXPRESSION_PATTERN.matcher(expression);

        if (!matcher.matches()) {
            return "表达式包含非法字符或格式不正确";
        }

        return null;
    }

    private Set parseSet(String setString, boolean isIntType) {
        try {
            char[] elements = SetrelfunUtil.extractSetElements(setString, isIntType);
            if (elements == null) {
                return null;
            }
            return new Set(elements);
        } catch (Exception e) {
            return null;
        }
    }

    private String convertToLatexString(String expression) {
        StringBuilder result = new StringBuilder(expression);
        for (int i = 0; i < result.length(); i++) {
            if ((result.charAt(i) == 'p' && result.charAt(i - 1) != 'o') || result.charAt(i) == 's') {
                result.insert(i + 1, " ");
                i++; // 跳过刚插入的空格
            }
        }
        return result.toString();
    }
}