package com.deedm.service.logic;

import com.deedm.model.logic.EquivCalculusRequest;
import com.deedm.model.logic.EquivCalculusResponse;
import com.deedm.model.logic.EquivCalculusStepInfo;
import com.deedm.legacy.proplogic.FormulaBuilder;
import com.deedm.legacy.proplogic.TruthAssignmentFunction;
import com.deedm.legacy.proplogic.equiv.EquivCalculusChecker;
import com.deedm.legacy.proplogic.equiv.EquivCalculusRecorder;
import com.deedm.legacy.proplogic.equiv.EquivCalculusStep;
import com.deedm.legacy.proplogic.formula.Formula;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EquivCalculusService {

    public EquivCalculusResponse checkEquivCalculus(EquivCalculusRequest request) {
        EquivCalculusResponse response = new EquivCalculusResponse();

        try {
            String content = request.getContent();
            if (content == null || content.trim().isEmpty()) {
                response.setSuccess(false);
                response.setValid(false);
                response.setMessage("输入内容不能为空");
                return response;
            }

            // 解析输入内容，检查格式是否正确
            List<EquivCalculusStepInfo> steps = parseCalculusSteps(content);

            response.setSuccess(true);
            response.setValid(true);
            response.setMessage("输入格式正确");
            response.setSteps(steps);

        } catch (Exception e) {
            response.setSuccess(false);
            response.setValid(false);
            response.setMessage("输入格式错误: " + e.getMessage());
        }

        return response;
    }

    public EquivCalculusResponse performEquivCalculus(EquivCalculusRequest request) {
        EquivCalculusResponse response = new EquivCalculusResponse();
        response.setStepNumber(request.getStepNumber());

        try {
            String content = request.getContent();
            if (content == null || content.trim().isEmpty()) {
                response.setSuccess(false);
                response.setValid(false);
                response.setMessage("输入内容不能为空");
                return response;
            }

            // 解析所有演算步骤用于前端显示
            List<EquivCalculusStepInfo> allSteps;
            try {
                allSteps = parseCalculusSteps(content);
            } catch (Exception e) {
                response.setSuccess(false);
                response.setValid(false);
                response.setMessage("无法解析等值演算过程: " + e.getMessage());
                return response;
            }

            response.setSteps(allSteps);

            // 解析等值演算步骤用于真值表验证
            EquivCalculusRecorder recorder = parseEquivCalculusRecorder(content);
            if (recorder == null || recorder.getStepList().isEmpty()) {
                // 如果只有一个公式，无需验证等值演算
                response.setSuccess(true);
                response.setValid(true);
                response.setMessage("输入包含单个公式，无需验证等值演算");
                return response;
            }

            // 使用真值表检查等值演算
            EquivCalculusChecker checker = new EquivCalculusChecker(recorder);
            boolean isValid = checker.checkByUsingTruthTable();

            response.setSuccess(true);
            response.setValid(isValid);

            if (isValid) {
                response.setMessage("通过真值表检验，上述等值演算过程没有错误");
            } else {
                response.setMessage("等值演算过程存在错误");

                // 获取错误信息
                Formula checkingFormula = checker.getCheckingFormula();
                TruthAssignmentFunction counterExample = checker.getCounterExample();

                if (checkingFormula != null) {
                    response.setCheckingFormula(checkingFormula.toSimpleLaTeXString());
                }

                if (counterExample != null) {
                    response.setCounterExample(counterExample.toString());
                    response.setErrorMessage("在真值赋值 " + counterExample + " 下，公式不成立");
                } else {
                    response.setErrorMessage("等值演算步骤中存在逻辑错误");
                }
            }

        } catch (Exception e) {
            response.setSuccess(false);
            response.setValid(false);
            response.setMessage("演算检查时发生错误: " + e.getMessage());
        }

        return response;
    }

    private List<EquivCalculusStepInfo> parseCalculusSteps(String content) throws Exception {
        List<EquivCalculusStepInfo> steps = new ArrayList<>();
        String[] lines = content.trim().split("[\n\r;]");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("&");
            if (parts.length == 0) continue;

            String firstPart = parts[0].trim();
            String formula;
            String comment = "";

            if (firstPart.isEmpty() && parts.length > 1) {
                // 格式: & 公式 & 注释
                formula = parts[1].trim();
                if (parts.length > 2) {
                    comment = parts[2].trim();
                }
            } else if (!firstPart.isEmpty() && !firstPart.equals("\\equiv")) {
                // 格式: 公式 & 注释
                formula = firstPart;
                if (parts.length > 1) {
                    comment = parts[1].trim();
                }
            } else if (firstPart.equals("\\equiv") && parts.length > 1) {
                // 格式: \equiv & 公式 & 注释
                formula = parts[1].trim();
                if (parts.length > 2) {
                    comment = parts[2].trim();
                }
            } else {
                continue; // 跳过无效行
            }

            // 验证公式
            if (!formula.isEmpty()) {
                Formula parsedFormula = FormulaBuilder.buildFromLaTexFormulaString(formula);
                if (parsedFormula == null) {
                    throw new Exception("第 " + (i + 1) + " 行 [" + line + "] 中包含非法公式: " + formula);
                }
                steps.add(new EquivCalculusStepInfo(formula, comment));
            }
        }

        return steps;
    }

    private EquivCalculusRecorder parseEquivCalculusRecorder(String content) {
        try {
            EquivCalculusRecorder recorder = new EquivCalculusRecorder();
            String[] lines = content.trim().split("[\n\r;]");
            Formula firstFormula = null;

            for (int i = 0; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("&");
                if (parts.length == 0) continue;

                String firstPart = parts[0].trim();
                String formula = null;
                String comment = "";

                if (firstPart.isEmpty() && parts.length > 1) {
                    // 格式: & 公式 & 注释 (第一个公式)
                    formula = parts[1].trim();
                    if (parts.length > 2) {
                        comment = parts[2].trim();
                    }
                } else if (!firstPart.isEmpty() && !firstPart.equals("\\equiv")) {
                    // 格式: 公式 & 注释 (第一个公式)
                    formula = firstPart;
                    if (parts.length > 1) {
                        comment = parts[1].trim();
                    }
                } else if (firstPart.equals("\\equiv") && parts.length > 1) {
                    // 格式: \equiv & 公式 & 注释 (后续公式)
                    formula = parts[1].trim();
                    if (parts.length > 2) {
                        comment = parts[2].trim();
                    }
                }

                // 处理解析到的公式
                if (formula != null && !formula.isEmpty()) {
                    Formula parsedFormula = FormulaBuilder.buildFromLaTexFormulaString(formula);
                    if (parsedFormula == null) {
                        return null; // 解析失败
                    }

                    if (firstFormula == null) {
                        firstFormula = parsedFormula; // 保存第一个公式
                    } else {
                        // 后续公式添加到recorder
                        recorder.addStep(new EquivCalculusStep(parsedFormula, comment));
                    }
                }
            }

            // 如果只找到了一个公式，也添加到recorder中
            if (firstFormula != null && recorder.getStepList().isEmpty()) {
                recorder.addStep(new EquivCalculusStep(firstFormula, ""));
                return recorder;
            }

            // 将第一个公式添加到recorder的开头
            if (firstFormula != null) {
                EquivCalculusRecorder newRecorder = new EquivCalculusRecorder();
                newRecorder.addStep(new EquivCalculusStep(firstFormula, ""));

                // 将原有步骤添加到新recorder中
                for (EquivCalculusStep step : recorder.getStepList()) {
                    newRecorder.addStep(step);
                }

                return newRecorder;
            }

            return recorder;
        } catch (Exception e) {
            return null;
        }
    }
}