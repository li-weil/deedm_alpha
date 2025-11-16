package com.deedm.service.counting;

import com.deedm.model.counting.GenRepCombRequest;
import com.deedm.model.counting.GenRepCombResponse;
import com.deedm.legacy.counting.generator.RepetitionCombinationGenerator;
import com.deedm.legacy.counting.generator.StringGenerator;
import com.deedm.legacy.setrelfun.Set;
import com.deedm.legacy.setrelfun.SetrelfunUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GenRepCombService {

    public GenRepCombResponse generateRepetitionCombinations(GenRepCombRequest request) {
        GenRepCombResponse response = new GenRepCombResponse();

        try {
            // 提取基础字符集
            char[] elements = SetrelfunUtil.extractSetElements(request.getBaseSetString(), false);
            if (elements == null) {
                String message = SetrelfunUtil.getExtractErrorMessage();
                response.setErrorMessage("提供的基础字符集 " + request.getBaseSetString() + " 不合法: " + message);
                return response;
            }

            // 处理长度
            int length = request.getLength();
            if (length > elements.length) {
                length = elements.length; // 与原Java应用保持一致
            }

            // 处理起始字符串
            String startString = request.getStartString();
            if (startString != null && !startString.isEmpty() && startString.length() != length) {
                // 如果起始字符串长度不匹配，设为空
                startString = "";
            }

            // 处理生成数量，默认值20
            int number = request.getNumber();
            if (number <= 0) {
                number = 20;
            }

            Set baseSet = new Set(elements);

            // 设置基本信息
            response.setBaseSetLaTeX(baseSet.toLaTeXString());
            response.setBaseSetSize(baseSet.length());
            response.setLength(length);
            response.setStartString(startString);
            response.setNumber(number);

            // 生成公式
            String formula = "从 " + baseSet.length() + " 个元素中选取 " + length + " 个可重复的组合";
            response.setFormula(formula);
            response.setMessage("从 " + baseSet.length() + " 个元素中选取 " + length + " 个可重复的组合");

            // 创建生成器
            StringGenerator generator = new RepetitionCombinationGenerator(elements, length);
            generator.first();

            // 如果没有提供起始字符串，使用第一个组合
            if (startString == null || startString.isEmpty()) {
                char[] charArray = generator.current();
                startString = StringGenerator.convertToString(charArray);
                response.setStartString(startString);
            }

            // 生成组合
            List<String> combinations = new ArrayList<>();
            int totalCounter = 0;
            boolean display = false;
            int displayCounter = 0;
            int numberPerLine = 8;

            while (true) {
                char[] charArray = generator.current();
                String string = StringGenerator.convertToString(charArray);
                totalCounter++;

                if (!display) {
                    if (startString.equals(string)) {
                        display = true;
                    }
                } else {
                    if (displayCounter > number) {
                        display = false;
                    }
                }

                if (display) {
                    combinations.add(string);
                    displayCounter++;
                }

                if (generator.isLast()) {
                    break;
                }
                generator.next();
            }

            // 如果起始字符串不在生成序列中
            if (displayCounter <= 0) {
                response.setErrorMessage("要生成的起始字符串 " + startString + " 不在可生成的组合序列中");
                return response;
            }

            // 设置结果
            response.setGeneratedCombinations(combinations);
            response.setTotalCombinations(totalCounter);

            // 计算组合数公式 C(|B|-1+L, L)
            int n = baseSet.length();
            int r = length;
            String combinationFormula = "C(" + (n - 1 + r) + ", " + r + ")";
            String combinationCountLaTeX = "=" + n + "^{" + r + "} \\text{ 或 } C(" + (n - 1 + r) + ", " + r + ") = " + totalCounter;

            response.setCombinationFormula(combinationFormula);
            response.setCombinationCountLaTeX(combinationCountLaTeX);

            return response;

        } catch (Exception e) {
            response.setErrorMessage("生成允许重复组合时发生错误: " + e.getMessage());
            return response;
        }
    }

    public String validateInput(String baseSetString, String lengthString, String startString, String numberString) {
        try {
            // 验证基础字符集
            char[] elements = SetrelfunUtil.extractSetElements(baseSetString, false);
            if (elements == null) {
                String message = SetrelfunUtil.getExtractErrorMessage();
                return "提供的基础字符集 " + baseSetString + " 不合法: " + message;
            }

            // 验证长度
            try {
                int length = Integer.parseInt(lengthString);
                if (length <= 0) {
                    return "组合长度必须大于0";
                }
            } catch (Exception e) {
                return "组合长度 " + lengthString + " 不是合法的整数";
            }

            // 验证起始字符串长度（如果不为空）
            if (startString != null && !startString.isEmpty()) {
                try {
                    int length = Integer.parseInt(lengthString);
                    if (startString.length() != length) {
                        return "起始字符串 " + startString + " 的长度不等于组合长度 " + lengthString;
                    }
                } catch (Exception e) {
                    return "组合长度格式错误，无法验证起始字符串长度";
                }
            }

            // 验证生成数量
            try {
                int number = Integer.parseInt(numberString);
                if (number <= 0) {
                    return "生成数量必须大于0，将使用默认值(20)";
                }
            } catch (Exception e) {
                return "提供的生成数量 " + numberString + " 不是合法的整数，将使用默认值(20)";
            }

            return null; // null表示验证通过

        } catch (Exception e) {
            return "输入验证时发生错误: " + e.getMessage();
        }
    }
}