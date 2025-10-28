package com.deedm.service;

import com.deedm.legacy.proplogic.FormulaBuilder;
import com.deedm.legacy.proplogic.formula.Formula;
import com.deedm.legacy.proplogic.normalFormula.*;
import com.deedm.legacy.setrelfun.SetrelfunUtil;
import com.deedm.model.NormalFormulaExpansionRequest;
import com.deedm.model.NormalFormulaExpansionResponse;
import com.deedm.model.ExpansionStep;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NormalFormulaExpansionService {

    public NormalFormulaExpansionResponse expandNormalFormula(NormalFormulaExpansionRequest request) {
        NormalFormulaExpansionResponse response = new NormalFormulaExpansionResponse();

        try {
            // Extract variable set
            char[] varSet = SetrelfunUtil.extractSetElements(request.getVariableSet(), false);
            if (varSet == null) {
                response.setSuccess(false);
                response.setMessage("变量集 " + request.getVariableSet() + " 不合法!");
                return response;
            }

            // Combine formulas based on logical relation
            String wholeString = combineFormulas(request.getFormulas(), request.isDNF());
            Formula formula = FormulaBuilder.buildFromLaTexFormulaString(wholeString);

            if (formula == null) {
                // Check individual formulas
                for (String laTeXString : request.getFormulas()) {
                    formula = FormulaBuilder.buildFromLaTexFormulaString(laTeXString);
                    if (formula == null) {
                        response.setSuccess(false);
                        response.setMessage("公式字符串中有非法公式的字符串: " + laTeXString);
                        return response;
                    }
                }
                response.setSuccess(false);
                response.setMessage("公式字符串 " + wholeString + " 合起来不是合法的公式!");
                return response;
            }

            response.setOriginalFormula(formula.toSimpleLaTeXString());
            response.setVariableSet(request.getVariableSet());

            if (request.isDNF()) {
                // Expand DNF to PDNF
                DisjunctiveNormalFormula disjnormform = DisjunctiveNormalFormula.convert(formula);
                if (disjnormform == null) {
                    response.setSuccess(false);
                    response.setMessage("公式 " + formula.toSimpleLaTeXString() + " 不是析取范式，无法扩展为主析取范式!");
                    return response;
                }

                PDNFormula lastPDNF = NormalFormulaCalculator.expandToPDNF(disjnormform, varSet);

                // Get expansion steps
                ExpandNFRecorder expandRecorder = NormalFormulaCalculator.getExpandRecorder();
                int type = expandRecorder.getType();
                List<ExpandNFStep> stepList = expandRecorder.getStepList();

                List<ExpansionStep> expansionSteps = new ArrayList<>();
                for (ExpandNFStep step : stepList) {
                    ExpansionStep expansionStep = new ExpansionStep();
                    Formula stepFormula = step.getFormula();
                    expansionStep.setFormula(stepFormula.toSimpleLaTeXString());
                    expansionStep.setBinaryCode(step.getFormulaBinaryCodeString());
                    expansionStep.setResultCodes(step.getResultCodesNamedLaTeXString(type));
                    expansionStep.setDescription("扩展简单合取式");
                    expansionSteps.add(expansionStep);
                }

                response.setExpansionSteps(expansionSteps);
                response.setPdnfResult(lastPDNF.toNamedLaTeXString());
                response.setPcnfResult(lastPDNF.toNamedPCNFLaTeXString());
                response.setTargetType("主析取范式(PDNF)");

            } else {
                // Expand CNF to PCNF
                ConjunctiveNormalFormula conjnormform = ConjunctiveNormalFormula.convert(formula);
                if (conjnormform == null) {
                    response.setSuccess(false);
                    response.setMessage("公式 " + formula.toSimpleLaTeXString() + " 不是合取范式，无法扩展为主合取范式!");
                    return response;
                }

                PCNFormula lastPCNF = NormalFormulaCalculator.expandToPCNF(conjnormform, varSet);

                // Get expansion steps
                ExpandNFRecorder expandRecorder = NormalFormulaCalculator.getExpandRecorder();
                int type = expandRecorder.getType();
                List<ExpandNFStep> stepList = expandRecorder.getStepList();

                List<ExpansionStep> expansionSteps = new ArrayList<>();
                for (ExpandNFStep step : stepList) {
                    ExpansionStep expansionStep = new ExpansionStep();
                    Formula stepFormula = step.getFormula();
                    expansionStep.setFormula(stepFormula.toSimpleLaTeXString());
                    expansionStep.setBinaryCode(step.getFormulaBinaryCodeString());
                    expansionStep.setResultCodes(step.getResultCodesNamedLaTeXString(type));
                    expansionStep.setDescription("扩展简单析取式");
                    expansionSteps.add(expansionStep);
                }

                response.setExpansionSteps(expansionSteps);
                response.setPcnfResult(lastPCNF.toNamedLaTeXString());
                response.setPdnfResult(lastPCNF.toNamedPDNFLaTeXString());
                response.setTargetType("主合取范式(PCNF)");
            }

            response.setSuccess(true);
            response.setMessage("范式扩展完成");

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("范式扩展过程中发生错误: " + e.getMessage());
        }

        return response;
    }

    public NormalFormulaExpansionResponse checkNormalFormula(NormalFormulaExpansionRequest request) {
        NormalFormulaExpansionResponse response = new NormalFormulaExpansionResponse();

        try {
            char[] varSet = SetrelfunUtil.extractSetElements(request.getVariableSet(), false);
            if (varSet == null) {
                response.setSuccess(false);
                response.setMessage("变量集 " + request.getVariableSet() + " 不合法!");
                return response;
            }

            String wholeString = combineFormulas(request.getFormulas(), request.isDNF());
            Formula formula = FormulaBuilder.buildFromLaTexFormulaString(wholeString);

            if (formula == null) {
                response.setSuccess(false);
                response.setMessage("公式字符串 " + wholeString + " 合起来不是合法的公式!");
                return response;
            }

            if (request.isDNF()) {
                // Check DNF
                for (String laTeXString : request.getFormulas()) {
                    formula = FormulaBuilder.buildFromLaTexFormulaString(laTeXString);
                    SimpleConjunctiveFormula simpleform = SimpleConjunctiveFormula.convert(formula);
                    if (simpleform == null) {
                        response.setSuccess(false);
                        response.setMessage("公式字符串 " + formula.toSimpleLaTeXString() + " 不是简单合取式，无法扩展为主析取范式!");
                        return response;
                    }
                }

                DisjunctiveNormalFormula disjnormform = DisjunctiveNormalFormula.convert(formula);
                if (disjnormform == null) {
                    response.setSuccess(false);
                    response.setMessage("公式字符串 " + formula.toSimpleLaTeXString() + " 合起来不是析取范式，无法扩展!");
                    return response;
                }

                response.setMessage("您的公式是合法的简单合取式，可以扩展为主析取范式！");

            } else {
                // Check CNF
                for (String laTeXString : request.getFormulas()) {
                    formula = FormulaBuilder.buildFromLaTexFormulaString(laTeXString);
                    SimpleDisjunctiveFormula simpleform = SimpleDisjunctiveFormula.convert(formula);
                    if (simpleform == null) {
                        response.setSuccess(false);
                        response.setMessage("公式字符串 " + formula.toSimpleLaTeXString() + " 不是简单析取式，无法扩展为主合取范式!");
                        return response;
                    }
                }

                ConjunctiveNormalFormula conjnormform = ConjunctiveNormalFormula.convert(formula);
                if (conjnormform == null) {
                    response.setSuccess(false);
                    response.setMessage("公式字符串 " + formula.toSimpleLaTeXString() + " 合起来不是合取范式，无法扩展!");
                    return response;
                }

                response.setMessage("您的公式是合法的简单析取式，可以扩展为主合取范式！");
            }

            response.setSuccess(true);

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("范式检查过程中发生错误: " + e.getMessage());
        }

        return response;
    }

    private String combineFormulas(List<String> formulas, boolean isDNF) {
        if (formulas.isEmpty()) return "";

        StringBuilder buffer = new StringBuilder();
        buffer.append("(").append(formulas.get(0)).append(")");

        for (int i = 1; i < formulas.size(); i++) {
            if (isDNF) {
                buffer.append(" \\vee (").append(formulas.get(i)).append(")");
            } else {
                buffer.append(" \\wedge (").append(formulas.get(i)).append(")");
            }
        }

        return buffer.toString();
    }
}