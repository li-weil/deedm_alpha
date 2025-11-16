package com.deedm.service.counting;

import com.deedm.model.counting.CountFunctionRequest;
import com.deedm.model.counting.CountFunctionResponse;
import com.deedm.legacy.setrelfun.Set;
import com.deedm.legacy.setrelfun.Function;
import com.deedm.legacy.setrelfun.SetrelfunUtil;
import com.deedm.legacy.counting.generator.FunctionGenerator;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CountFunctionService {

    public CountFunctionResponse countFunctions(CountFunctionRequest request) {
        CountFunctionResponse response = new CountFunctionResponse();

        try {
            // 解析集合A
            Set setA = parseSet(request.getSetAString(), request.isIntType());
            if (setA == null) {
                response.setSuccess(false);
                response.setErrorMessage("集合A的字符串 " + request.getSetAString() + " 不是合法的表示集合的字符串！");
                return response;
            }

            // 解析集合B
            Set setB = parseSet(request.getSetBString(), request.isIntType());
            if (setB == null) {
                response.setSuccess(false);
                response.setErrorMessage("集合B的字符串 " + request.getSetBString() + " 不是合法的表示集合的字符串！");
                return response;
            }

            // 解析最大显示数量
            int maxDisplay = parseMaxDisplay(request.getMaxDisplayString());

            // 设置LaTeX集合字符串
            response.setSetALaTeX(setA.toLaTeXString());
            response.setSetBLaTeX(setB.toLaTeXString());

            // 生成函数并计数
            List<Map<String, Object>> functionList = new ArrayList<>();
            int totalCounter = 0;
            int injectionCounter = 0;
            int surjectionCounter = 0;
            int bijectionCounter = 0;
            int displayCounter = 0;

            FunctionGenerator generator = new FunctionGenerator(setA, setB);
            generator.first();

            while (true) {
                Function function = generator.current();
                totalCounter++;

                boolean display = request.isDetailed();
                if (display) {
                    if (displayCounter >= maxDisplay) {
                        display = false;
                    }
                }

                // 检查函数性质
                boolean isBijection = function.isBijection();
                boolean isInjection = function.isInjection();
                boolean isSurjection = function.isSurjection();

                if (isBijection) {
                    bijectionCounter++;
                    if (request.isCountBijection() && display) {
                        Map<String, Object> funcInfo = new HashMap<>();
                        funcInfo.put("totalNumber", totalCounter);
                        funcInfo.put("countNumber", bijectionCounter);
                        funcInfo.put("type", "bijection");
                        funcInfo.put("laTeX", function.toLaTeXString(request.isIntType()));
                        functionList.add(funcInfo);
                        display = false;
                        displayCounter++;
                    }
                }

                if (isInjection) {
                    injectionCounter++;
                    if (request.isCountInjection() && display) {
                        Map<String, Object> funcInfo = new HashMap<>();
                        funcInfo.put("totalNumber", totalCounter);
                        funcInfo.put("countNumber", injectionCounter);
                        funcInfo.put("type", "injection");
                        funcInfo.put("laTeX", function.toLaTeXString(request.isIntType()));
                        functionList.add(funcInfo);
                        display = false;
                        displayCounter++;
                    }
                }

                if (isSurjection) {
                    surjectionCounter++;
                    if (request.isCountSurjection() && display) {
                        Map<String, Object> funcInfo = new HashMap<>();
                        funcInfo.put("totalNumber", totalCounter);
                        funcInfo.put("countNumber", surjectionCounter);
                        funcInfo.put("type", "surjection");
                        funcInfo.put("laTeX", function.toLaTeXString(request.isIntType()));
                        functionList.add(funcInfo);
                        display = false;
                        displayCounter++;
                    }
                }

                // 如果没有选择任何特定性质，显示所有函数
                if (display && !request.isCountBijection() && !request.isCountInjection() && !request.isCountSurjection()) {
                    Map<String, Object> funcInfo = new HashMap<>();
                    funcInfo.put("totalNumber", totalCounter);
                    funcInfo.put("type", "function");
                    funcInfo.put("laTeX", function.toLaTeXString(request.isIntType()));
                    functionList.add(funcInfo);
                    display = false;
                    displayCounter++;
                }

                if (generator.isLast()) {
                    break;
                }
                generator.next();
            }

            // 设置统计结果
            response.setTotalCount(totalCounter);
            response.setInjectionCount(injectionCounter);
            response.setSurjectionCount(surjectionCounter);
            response.setBijectionCount(bijectionCounter);
            response.setFunctionList(functionList);
            response.setSuccess(true);

        } catch (Exception e) {
            response.setSuccess(false);
            response.setErrorMessage("处理过程中发生错误: " + e.getMessage());
        }

        return response;
    }

    public String validateInput(CountFunctionRequest request) {
        // 验证集合A
        Set setA = parseSet(request.getSetAString(), request.isIntType());
        if (setA == null) {
            return "集合A的字符串 " + request.getSetAString() + " 不是合法的表示集合的字符串！\n    错误信息： " + SetrelfunUtil.getExtractErrorMessage();
        }

        // 验证集合B
        Set setB = parseSet(request.getSetBString(), request.isIntType());
        if (setB == null) {
            return "集合B的字符串 " + request.getSetBString() + " 不是合法的表示集合的字符串！\n    错误信息： " + SetrelfunUtil.getExtractErrorMessage();
        }

        // 验证最大显示数量
        try {
            Integer.parseInt(request.getMaxDisplayString());
        } catch (Exception exc) {
            return "最大显示数量的字符串 " + request.getMaxDisplayString() + " 不是合法的整数字符串！";
        }

        return null; // 验证通过
    }

    private Set parseSet(String setString, boolean isIntType) {
        if (setString == null || setString.trim().isEmpty()) {
            return null;
        }

        char[] elements = SetrelfunUtil.extractSetElements(setString, isIntType);
        if (elements == null) {
            return null;
        }

        return new Set(elements);
    }

    private int parseMaxDisplay(String maxString) {
        try {
            int maxDisplay = Integer.parseInt(maxString);
            return Math.min(maxDisplay, 100); // 限制最大显示数量为100
        } catch (Exception exc) {
            return 100; // 默认值
        }
    }
}