package com.deedm.service;

import com.deedm.model.ReasonArgumentCheckRequest;
import com.deedm.model.ReasonArgumentCheckResponse;
import com.deedm.legacy.proplogic.FormulaBuilder;
import com.deedm.legacy.proplogic.TruthAssignmentFunction;
import com.deedm.legacy.proplogic.formula.Formula;
import com.deedm.legacy.proplogic.reason.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReasonArgumentCheckService {

    public ReasonArgumentCheckResponse checkReasonArgument(ReasonArgumentCheckRequest request) {
        ReasonArgumentCheckResponse response = new ReasonArgumentCheckResponse();

        try {
            ReasonArgumentRecorder argumentRecorder = buildReasonArgumentRecorder(request);
            if (argumentRecorder == null) {
                response.setSuccess(false);
                response.setMessage("记录推理前题集和结论公式过程中存在错误");
                response.setValid(false);
                response.setSteps(new ArrayList<>());
                response.setCheckSteps(new ArrayList<>());
                return response;
            }

            // Build the reasoning steps for display
            List<ReasonArgumentCheckResponse.ReasoningStep> reasoningSteps = new ArrayList<>();
            List<ReasonArgumentStep> argumentStepList = argumentRecorder.getStepList();

            for (ReasonArgumentStep step : argumentStepList) {
                ReasonArgumentCheckResponse.ReasoningStep responseStep = new ReasonArgumentCheckResponse.ReasoningStep();
                responseStep.setSerialNo(step.getSerialNo());
                responseStep.setFormula(step.getFormula().toSimpleLaTeXString());
                responseStep.setRuleName(step.getRuleName().getChineseName());

                // Format previous step numbers
                String prevSNString = "";
                int[] prevSN = step.getPreviousSN();
                if (prevSN != null) {
                    boolean isFirstSN = true;
                    for (int i = 0; i < prevSN.length; i++) {
                        if (isFirstSN) {
                            prevSNString = prevSNString + "(" + prevSN[i] + ")";
                            isFirstSN = false;
                        } else {
                            prevSNString = prevSNString + ",(" + prevSN[i]+")";
                        }
                    }
                }
                responseStep.setPrevSN(prevSNString);
                reasoningSteps.add(responseStep);
            }

            // Set the reasoning LaTeX string
            String reasonString = argumentRecorder.getReasoning().toLaTeXString();
            response.setLatexString(reasonString);
            response.setSteps(reasoningSteps);

            // Perform the actual checking
            ReasonArgumentChecker checker = new ReasonArgumentChecker(argumentRecorder);
            boolean success = checker.checkByUsingTruthTable();

            // Build checking steps
            List<ReasonArgumentCheckResponse.CheckingStep> checkingSteps = new ArrayList<>();
            List<ReasonArgumentCheckStep> checkStepList = checker.getCheckStepList();

            for (ReasonArgumentCheckStep step : checkStepList) {
                ReasonArgumentCheckResponse.CheckingStep responseStep = new ReasonArgumentCheckResponse.CheckingStep();
                responseStep.setSerialNo(step.getReasonStep().getSerialNo());
                responseStep.setFormula(step.getCheckFormula().toSimpleLaTeXString());

                LogicReasoningRuleName ruleName = step.getReasonStep().getRuleName();
                if (ruleName == LogicReasoningRuleName.AddPremise) {
                    responseStep.setCheckType("添加前题 ");
                } else if (ruleName == LogicReasoningRuleName.Premise) {
                    responseStep.setCheckType("确认前题 ");
                } else {
                    responseStep.setCheckType("确认推理公式 ");
                }

                checkingSteps.add(responseStep);
            }

            response.setCheckSteps(checkingSteps);
            response.setValid(success);
            response.setSuccess(true);

            if (success) {
                response.setMessage("通过真值表检验，上述推理证明过程没有错误");
            } else {
                // Handle error cases
                String checkingFormula = checker.getCheckingFormula().toSimpleLaTeXString();
                TruthAssignmentFunction example = checker.getCounterExample();
                int errorType = checker.getErrorType();

                response.setCheckingFormula(checkingFormula);
                response.setErrorType(errorType);

                if (example != null) {
                    response.setCounterExample(example.toString());
                }

                switch (errorType) {
                    case ReasonArgumentChecker.ERROR_TYPE_FALSE:
                        response.setMessage("推理存在错误，在真值赋值 " + example + " 下，以下公式不是重言式");
                        break;
                    case ReasonArgumentChecker.ERROR_TYPE_PREMISE:
                        response.setMessage("推理存在错误，公式 " + checkingFormula + " 不在前题公式集合中");
                        break;
                    case ReasonArgumentChecker.ERROR_TYPE_CONSEQUENT:
                        response.setMessage("推理存在错误，最后一个公式 " + checkingFormula + " 不是推理的结论公式");
                        break;
                    case ReasonArgumentChecker.ERROR_TYPE_ADDPREMISE:
                        response.setMessage("推理存在错误，至少一个附加前题 " + checkingFormula + " 在得到结论前没有被消除");
                        break;
                    default:
                        response.setMessage("推理检查发现未知错误");
                        break;
                }
            }

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("推理检查过程中发生错误: " + e.getMessage());
            response.setValid(false);
            // 确保所有字段都有默认值，避免JSON序列化问题
            response.setSteps(new ArrayList<>());
            response.setCheckSteps(new ArrayList<>());
        }

        return response;
    }

    public ReasonArgumentCheckResponse validateInput(ReasonArgumentCheckRequest request) {
        ReasonArgumentCheckResponse response = new ReasonArgumentCheckResponse();

        try {
            // Validate premises
            Formula formula = null;
            String premiseString = request.getPremises() != null ? request.getPremises().trim() : "";
            if (!premiseString.isEmpty()) {
                String[] splitPremise = premiseString.split("[,;]");
                for (int i = 0; i < splitPremise.length; i++) {
                    String cell = splitPremise[i].trim();
                    formula = FormulaBuilder.buildFromLaTexFormulaString(cell);
                    if (formula == null) {
                        response.setSuccess(false);
                        response.setMessage("输入前题集 [" + premiseString + "] 中包含不是合法公式的串 " + cell + " !\\n错误信息：" + FormulaBuilder.getErrorMessage());
                        return response;
                    }
                }
            }

            // Validate consequent
            String consequentString = request.getConsequent() != null ? request.getConsequent().trim() : "";
            formula = FormulaBuilder.buildFromLaTexFormulaString(consequentString);
            if (formula == null) {
                response.setSuccess(false);
                response.setMessage("输入的结论式  [" + consequentString + "] 不是合法的公式串 !\\n错误信息：" + FormulaBuilder.getErrorMessage());
                return response;
            }

            // Validate reasoning steps
            String content = request.getContent();
            String[] splittedSteps = content.trim().split("[\n\r;]");
            for (int i = 0; i < splittedSteps.length; i++) {
                String stepContent = splittedSteps[i].trim();
                String[] stepCells = stepContent.split("&");
                if (stepCells.length <= 0) continue;

                String cell = stepCells[0].trim();
                int serialNo = getSerialNumber(cell);

                if (serialNo != (i+1)) {
                    response.setSuccess(false);
                    response.setMessage("步骤 " + (i+1) + " 行 [" + stepContent + "] 没有正确的序号，应为 " + serialNo + " !");
                    return response;
                }

                if (stepCells.length <= 1) {
                    response.setSuccess(false);
                    response.setMessage("步骤 " + i + " 行 [" + stepContent + "] 没有相应的公式!");
                    return response;
                }

                cell = stepCells[1].trim();
                formula = FormulaBuilder.buildFromLaTexFormulaString(cell);
                if (formula == null) {
                    response.setSuccess(false);
                    response.setMessage("步骤 " + i + " 行 [" + stepContent + "] 中包含不是合法公式的串 " + cell + " !\\n错误信息：" + FormulaBuilder.getErrorMessage());
                    return response;
                }

                if (stepCells.length <= 2) {
                    response.setSuccess(false);
                    response.setMessage("步骤 " + i + " 行 [" + stepContent + "] 没有相应的使用规则名称串，请作为注意!");
                    return response;
                }

                cell = stepCells[2].trim();
                LogicReasoningRuleName ruleName = LogicReasoningRuleName.getRuleName(cell);
                if (ruleName == null) {
                    response.setSuccess(false);
                    response.setMessage("步骤 " + i + " 行 [" + stepContent + "] 无法识别的规则名称 " + cell + " !");
                    return response;
                }
            }

            response.setSuccess(true);
            response.setMessage("输入格式正确");
            response.setValid(true);

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("输入验证过程中发生错误: " + e.getMessage());
            response.setValid(false);
        }

        return response;
    }

    private ReasonArgumentRecorder buildReasonArgumentRecorder(ReasonArgumentCheckRequest request) {
        try {
            List<Formula> premisList = new ArrayList<>();
            Formula formula = null;
            String premiseString = request.getPremises() != null ? request.getPremises().trim() : "";

            if (!premiseString.isEmpty()) {
                String[] splitPremise = premiseString.split("[,;]");
                for (int i = 0; i < splitPremise.length; i++) {
                    String cell = splitPremise[i].trim();
                    formula = FormulaBuilder.buildFromLaTexFormulaString(cell);
                    if (formula == null) {
                        return null;
                    }
                    premisList.add(formula);
                }
            }

            String consequentString = request.getConsequent() != null ? request.getConsequent().trim() : "";
            Formula consequent = FormulaBuilder.buildFromLaTexFormulaString(consequentString);
            if (consequent == null) {
                return null;
            }

            LogicReasoning reasoning = new LogicReasoning(premisList, consequent);
            List<ReasonArgumentStep> stepList = new ArrayList<>();

            String content = request.getContent();
            String[] splittedSteps = content.trim().split("[\n\r;]");
            for (int i = 0; i < splittedSteps.length; i++) {
                String stepContent = splittedSteps[i].trim();
                String[] stepCells = stepContent.split("&");
                if (stepCells.length <= 0) continue;

                String cell = stepCells[0].trim();
                int serialNo = getSerialNumber(cell);

                if (serialNo != (i+1)) {
                    return null;
                }

                if (stepCells.length <= 1) {
                    return null;
                }

                cell = stepCells[1].trim();
                formula = FormulaBuilder.buildFromLaTexFormulaString(cell);
                if (formula == null) {
                    return null;
                }

                if (stepCells.length <= 2) {
                    return null;
                }

                cell = stepCells[2].trim();
                int[] prevSN = getPreviousSN(cell);
                LogicReasoningRuleName ruleName = LogicReasoningRuleName.getRuleName(cell);
                if (ruleName == null) {
                    return null;
                }

                ReasonArgumentStep step = new ReasonArgumentStep(serialNo, formula, prevSN, ruleName);
                stepList.add(step);
            }

            return new ReasonArgumentRecorder(reasoning, stepList);
        } catch (Exception e) {
            return null;
        }
    }

    private int getSerialNumber(String cell) {
        int startIndex = 0;
        int endIndex = cell.length();
        if (cell.charAt(0) == '(') startIndex++;
        if (cell.charAt(cell.length()-1) == ')') endIndex--;
        String valueString = cell.substring(startIndex, endIndex).trim();
        int value = -1;
        try {
            value = Integer.parseInt(valueString);
        } catch (NumberFormatException exc) {
            value = -1;
        }
        return value;
    }

    private int[] getPreviousSN(String cell) {
        final int MAX_NUMBER = 5;
        int[] values = new int[MAX_NUMBER];
        int counter = 0;
        int startIndex = -1;
        for (int i = 0; i < cell.length(); i++) {
            if (cell.charAt(i) == '(') {
                if (startIndex < 0) startIndex = i+1;
            } else if (cell.charAt(i) == ')') {
                if (startIndex >= 0 && startIndex < i) {
                    String valueString = cell.substring(startIndex, i);
                    startIndex = -1;
                    int value = -1;
                    try {
                        value = Integer.parseInt(valueString);
                    } catch (NumberFormatException exc) {
                        value = -1;
                    }
                    if (value >= 1 && counter < MAX_NUMBER) {
                        values[counter] = value;
                        counter++;
                    }
                }
            }
        }
        if (counter > 0) {
            int[] result = new int[counter];
            for (int i = 0; i < counter; i++) result[i] = values[i];
            return result;
        }
        return null;
    }
}