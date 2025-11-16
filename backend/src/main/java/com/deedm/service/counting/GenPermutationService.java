package com.deedm.service.counting;

import com.deedm.model.counting.GenPermutationRequest;
import com.deedm.model.counting.GenPermutationResponse;
import com.deedm.legacy.counting.generator.PermutationGenerator;
import com.deedm.legacy.counting.generator.StringGenerator;
import com.deedm.legacy.setrelfun.SetrelfunUtil;
import com.deedm.legacy.setrelfun.Set;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GenPermutationService {

    public GenPermutationResponse generatePermutations(GenPermutationRequest request) {
        GenPermutationResponse response = new GenPermutationResponse();

        try {
            // 验证并提取集合元素
            char[] elements = SetrelfunUtil.extractSetElements(request.getBaseSet(), false);
            if (elements == null) {
                String errorMessage = SetrelfunUtil.getExtractErrorMessage();
                response.setErrorMessage("给定的集合或字符串 " + request.getBaseSet() + " 不合法: " + errorMessage);
                return response;
            }

            // 处理长度参数
            int length = request.getLength();
            if (length > elements.length) {
                length = elements.length;
            }

            // 处理起始字符串
            String startString = request.getStartString();
            if (startString != null && !startString.isEmpty() && startString.length() != length) {
                response.setErrorMessage("起始字符串 " + startString + " 的长度不等于字符串长度 " + length);
                return response;
            }

            // 处理生成数量
            int number = request.getNumber();
            if (number <= 0) {
                number = 20; // 默认值
            }

            // 创建集合对象
            Set baseSet = new Set(elements);
            String formula = "P(" + baseSet.length() + ", " + length + ")";

            // 设置响应基本信息
            response.setBaseSet(baseSet.toLaTeXString());
            response.setLength(length);
            response.setStartString(startString);
            response.setNumber(number);
            response.setFormula(formula);

            // 创建排列生成器
            PermutationGenerator generator = new PermutationGenerator(elements, length);
            generator.first();

            // 如果没有提供起始字符串，使用第一个排列
            if (startString == null || startString.isEmpty()) {
                char[] charArray = generator.current();
                startString = StringGenerator.convertToString(charArray);
                response.setStartString(startString);
            }

            // 生成排列
            List<String> permutations = new ArrayList<>();
            int totalCounter = 0;
            boolean display = false;
            int displayCounter = 0;
            boolean startStringFound = false;

            while (true) {
                char[] charArray = generator.current();
                String string = StringGenerator.convertToString(charArray);
                totalCounter++;

                if (!display) {
                    if (startString.equals(string)) {
                        display = true;
                        startStringFound = true;
                    }
                } else {
                    if (displayCounter >= number) {
                        display = false;
                    }
                }

                if (display) {
                    permutations.add(string);
                    displayCounter++;
                }

                if (generator.isLast()) {
                    break;
                }
                generator.next();
            }

            // 设置结果
            response.setPermutations(permutations);
            response.setTotalCount(totalCounter);
            response.setStartStringFound(startStringFound);

            // 生成消息
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("从集合 B = ").append(baseSet.toLaTeXString())
                        .append(" 的长度为 ").append(length)
                        .append(" 的排列中，从起始排列 ").append(startString)
                        .append(" 开始生成 ").append(number)
                        .append(" 个排列，共 ").append(formula).append(" = ").append(totalCounter).append(" 个排列");

            if (!startStringFound) {
                messageBuilder.append("。注意：起始字符串 ").append(startString).append(" 不在生成的排列中！");
            }

            response.setMessage(messageBuilder.toString());

            return response;

        } catch (Exception e) {
            response.setErrorMessage("生成排列时发生错误: " + e.getMessage());
            return response;
        }
    }

    public String validateInput(String baseSet, String lengthString, String startString, String numberString) {
        try {
            // 验证集合
            char[] elements = SetrelfunUtil.extractSetElements(baseSet, false);
            if (elements == null) {
                return "给定的集合或字符串 " + baseSet + " 不合法: " + SetrelfunUtil.getExtractErrorMessage();
            }

            // 验证长度
            try {
                int length = Integer.parseInt(lengthString);
                if (length <= 0) {
                    return "字符串长度必须是正整数";
                }
                if (length > elements.length) {
                    return "字符串长度 " + length + " 超过了集合元素数量 " + elements.length;
                }
            } catch (NumberFormatException e) {
                return "字符串长度 " + lengthString + " 不是合法的整数";
            }

            // 验证起始字符串长度
            if (startString != null && !startString.isEmpty()) {
                int length = Integer.parseInt(lengthString);
                if (startString.length() != length) {
                    return "起始字符串 " + startString + " 的长度不等于字符串长度 " + length;
                }
            }

            // 验证生成数量
            try {
                if (numberString != null && !numberString.isEmpty()) {
                    int number = Integer.parseInt(numberString);
                    if (number <= 0) {
                        return "生成个数 " + number + " 必须是正整数";
                    }
                }
            } catch (NumberFormatException e) {
                return "生成个数 " + numberString + " 不是合法的整数，将使用缺省值(20)";
            }

            return null; // null表示验证通过

        } catch (Exception e) {
            return "输入验证时发生错误: " + e.getMessage();
        }
    }
}