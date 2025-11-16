package com.deedm.service.counting;

import com.deedm.model.counting.GenCombinationRequest;
import com.deedm.model.counting.GenCombinationResponse;
import com.deedm.legacy.counting.generator.CombinationGenerator;
import com.deedm.legacy.counting.generator.StringGenerator;
import com.deedm.legacy.setrelfun.Set;
import com.deedm.legacy.setrelfun.SetrelfunUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GenCombinationService {

    public GenCombinationResponse generateCombinations(GenCombinationRequest request) {
        GenCombinationResponse response = new GenCombinationResponse();

        try {
            // 验证并提取基础字符集
            char[] elements = SetrelfunUtil.extractSetElements(request.getBaseSet(), false);
            if (elements == null) {
                response.setErrorMessage("输入的基础字符集 " + request.getBaseSet() + " 不合法: " + SetrelfunUtil.getExtractErrorMessage());
                return response;
            }

            // 验证长度参数
            int length = request.getLength();
            if (length <= 0 || length > elements.length) {
                response.setErrorMessage("组合长度 " + length + " 不合法，必须在1到" + elements.length + "之间");
                return response;
            }

            // 验证起始字符串长度
            String startString = request.getStartString();
            if (startString != null && !startString.isEmpty() && startString.length() != length) {
                response.setErrorMessage("起始字符串 " + startString + " 的长度不等于组合长度 " + length);
                return response;
            }

            // 验证生成数量
            int number = request.getNumber();
            if (number <= 0) {
                number = 20; // 使用默认值
            }

            // 创建基础集
            Set baseSet = new Set(elements);

            // 创建组合生成器
            CombinationGenerator generator = new CombinationGenerator(elements, length);
            generator.first();

            // 如果没有起始字符串，使用第一个组合
            if (startString == null || startString.isEmpty()) {
                char[] charArray = generator.current();
                startString = StringGenerator.convertToString(charArray);
            }

            // 生成组合
            List<String> generatedCombinations = new ArrayList<>();
            boolean foundStart = false;
            int generatedCount = 0;

            while (true) {
                char[] charArray = generator.current();
                String currentString = StringGenerator.convertToString(charArray);

                // 找到起始字符串后开始生成
                if (!foundStart) {
                    if (startString.equals(currentString)) {
                        foundStart = true;
                    }
                } else {
                    if (generatedCount < number) {
                        generatedCombinations.add(currentString);
                        generatedCount++;
                    } else {
                        break;
                    }
                }

                if (generator.isLast()) {
                    break;
                }
                generator.next();
            }

            // 计算总组合数 C(n, k)
            int totalCombinations = calculateCombinationCount(baseSet.length(), length);
            String formula = "C(" + baseSet.length() + ", " + length + ")";

            // 设置响应信息
            response.setBaseSet(baseSet.toString());
            response.setLength(length);
            response.setStartString(startString);
            response.setNumber(number);
            response.setGeneratedCombinations(generatedCombinations);
            response.setTotalCombinations(totalCombinations);
            response.setFormula(formula);

            // 生成LaTeX格式信息
            response.setLatexBaseSet(baseSet.toLaTeXString());
            response.setLatexFormula(formula + "=" + totalCombinations);

            // 生成描述信息
            StringBuilder description = new StringBuilder();
            description.append("从基础集 B = ").append(baseSet.toString())
                      .append(" 中生成长度为 ").append(length)
                      .append(" 的组合，从 ").append(startString)
                      .append(" 开始生成 ").append(number).append(" 个组合。");
            response.setDescription(description.toString());

            // 生成LaTeX描述
            StringBuilder latexDescription = new StringBuilder();
            latexDescription.append("字符串集合： B = ").append(baseSet.toLaTeXString())
                           .append("，长度： n = ").append(length)
                           .append("，从： ").append(startString)
                           .append(" 开始生成 ").append(formula)
                           .append(" 的 ").append(number).append(" 个组合。");
            response.setLatexDescription(latexDescription.toString());

            // 如果没有找到起始字符串，给出警告
            if (!foundStart) {
                response.setErrorMessage("警告：起始字符串 " + startString + " 不在生成的组合序列中");
            }

        } catch (Exception e) {
            response.setErrorMessage("生成组合时发生错误: " + e.getMessage());
        }

        return response;
    }

    /**
     * 计算组合数 C(n, k)
     */
    private int calculateCombinationCount(int n, int k) {
        if (k > n || k < 0) {
            return 0;
        }
        if (k == 0 || k == n) {
            return 1;
        }
        if (k > n - k) {
            k = n - k; // 利用对称性
        }

        int result = 1;
        for (int i = 1; i <= k; i++) {
            result = result * (n - k + i) / i;
        }
        return result;
    }

    /**
     * 验证输入参数
     */
    public String validateInput(String baseSet, String lengthString, String startString, String numberString) {
        // 验证基础字符集
        char[] elements = SetrelfunUtil.extractSetElements(baseSet, false);
        if (elements == null) {
            return "输入的基础字符集 " + baseSet + " 不合法: " + SetrelfunUtil.getExtractErrorMessage();
        }

        // 验证长度
        try {
            int length = Integer.parseInt(lengthString);
            if (length <= 0 || length > elements.length) {
                return "组合长度 " + length + " 不合法，必须在1到" + elements.length + "之间";
            }
        } catch (Exception e) {
            return "组合长度 " + lengthString + " 不是合法的正整数";
        }

        // 验证起始字符串长度
        if (startString != null && !startString.isEmpty()) {
            try {
                int length = Integer.parseInt(lengthString);
                if (startString.length() != length) {
                    return "起始字符串 " + startString + " 的长度不等于组合长度 " + length;
                }
            } catch (Exception e) {
                return "无法验证起始字符串长度";
            }
        }

        // 验证生成数量
        try {
            int number = Integer.parseInt(numberString);
            if (number <= 0) {
                return "生成数量 " + number + " 不是合法的正整数";
            }
        } catch (Exception e) {
            return "生成数量 " + numberString + " 不是合法的正整数";
        }

        return null; // 验证通过
    }
}