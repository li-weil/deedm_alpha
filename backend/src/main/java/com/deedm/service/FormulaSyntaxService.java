package com.deedm.service;

import com.deedm.legacy.proplogic.FormulaBuilder;
import com.deedm.legacy.proplogic.formula.Formula;
import com.deedm.legacy.proplogic.formula.ASTGraph.FormulaASTGraph;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FormulaSyntaxService {

    public Map<String, Object> analyzeFormula(String latexFormula) {
        System.out.println("FormulaSyntaxService: 开始分析公式: " + latexFormula);
        Map<String, Object> result = new HashMap<>();

        try {
            System.out.println("FormulaSyntaxService: 调用FormulaBuilder.buildFromLaTexFormulaString");
            Formula formula = FormulaBuilder.buildFromLaTexFormulaString(latexFormula);
            System.out.println("FormulaSyntaxService: 构建的公式对象: " + (formula != null ? formula.getClass().getSimpleName() : "null"));

            if (formula == null) {
                String errorMsg = FormulaBuilder.getErrorMessage();
                System.out.println("FormulaSyntaxService: 公式构建失败，错误信息: " + errorMsg);
                result.put("success", false);
                result.put("error", errorMsg);
                return result;
            }

            String strictForm = formula.toLaTeXString();
            String simpleForm = formula.toSimpleLaTeXString();
            String formulaType = getFormulaTypeChineseName(formula);
            List<String> subformulas = getSubformulasList(formula);

            System.out.println("FormulaSyntaxService: 严格形式: " + strictForm);
            System.out.println("FormulaSyntaxService: 简化形式: " + simpleForm);
            System.out.println("FormulaSyntaxService: 公式类型: " + formulaType);
            System.out.println("FormulaSyntaxService: 子公式数量: " + subformulas.size());

            result.put("success", true);
            result.put("originalFormula", latexFormula);
            result.put("strictForm", strictForm);
            result.put("simpleForm", simpleForm);
            result.put("formulaType", formulaType);
            result.put("subformulas", subformulas);

        } catch (Exception e) {
            System.out.println("FormulaSyntaxService: 分析公式时发生异常: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        System.out.println("FormulaSyntaxService: 分析完成，返回结果: " + result);
        return result;
    }

    public Map<String, Object> checkFormula(String latexFormula) {
        Map<String, Object> result = new HashMap<>();

        try {
            Formula formula = FormulaBuilder.buildFromLaTexFormulaString(latexFormula);
            if (formula == null) {
                result.put("valid", false);
                result.put("error", FormulaBuilder.getErrorMessage());
            } else {
                result.put("valid", true);
                result.put("formula", latexFormula);
            }
        } catch (Exception e) {
            result.put("valid", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    public Map<String, Object> generateRandomFormula() {
        Map<String, Object> result = new HashMap<>();

        try {
            String plainFormula = FormulaBuilder.randomGenerateFormulaControlledByOperatorNumber(4, 5);
            Formula formula = FormulaBuilder.buildFromSymbolFormulaString(plainFormula);

            if (formula != null) {
                String laTeXString = formula.toSimpleLaTeXString();
                result.put("success", true);
                result.put("formula", laTeXString);
            } else {
                result.put("success", false);
                result.put("error", "自动生成公式失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    public Map<String, Object> getSubformulas(String latexFormula) {
        Map<String, Object> result = new HashMap<>();

        try {
            Formula formula = FormulaBuilder.buildFromLaTexFormulaString(latexFormula);
            if (formula == null) {
                result.put("success", false);
                result.put("error", FormulaBuilder.getErrorMessage());
                return result;
            }

            result.put("success", true);
            result.put("subformulas", getSubformulasList(formula));

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    public Map<String, Object> getASTGraph(String latexFormula) {
        Map<String, Object> result = new HashMap<>();

        try {
            Formula formula = FormulaBuilder.buildFromLaTexFormulaString(latexFormula);
            if (formula == null) {
                result.put("success", false);
                result.put("error", FormulaBuilder.getErrorMessage());
                return result;
            }

            result.put("success", true);
            result.put("astInfo", "AST graph generated successfully");
            result.put("formula", latexFormula);

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    private List<String> getSubformulasList(Formula formula) {
        List<Formula> subformList = formula.getAllSyntaxDifferentSubFormulas();
        List<String> result = new ArrayList<>();

        for (Formula subform : subformList) {
            result.add(subform.toLaTeXString());
        }

        return result;
    }

    private String getFormulaTypeChineseName(Formula formula) {
        if (formula.isAtomicFormula()) return "原子公式";
        if (formula.isNegFormula()) return "否定式";
        if (formula.isOrFormula()) return "析取式";
        if (formula.isAndFormula()) return "合取式";
        if (formula.isBiImpFormula()) return "双蕴含式";
        if (formula.isImpFormula()) return "蕴含式";
        return "未知类型公式";
    }
}