package com.deedm.service;

import com.deedm.model.TruthValueRequest;
import com.deedm.model.TruthValueResponse;
import com.deedm.legacy.proplogic.FormulaBuilder;
import com.deedm.legacy.proplogic.formula.Formula;
import com.deedm.legacy.proplogic.TruthAssignment;
import com.deedm.legacy.proplogic.TruthAssignmentFunction;
import com.deedm.legacy.proplogic.FormulaTruthTable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TruthValueService {

    public TruthValueResponse calculateTruthValue(TruthValueRequest request) {
        try {
            String formulaStr = request.getFormula();
            String assignmentStr = request.getAssignment();
            String variablesStr = request.getVariables();

            // 解析公式
            Formula formula = FormulaBuilder.buildFromLaTexFormulaString(formulaStr);
            if (formula == null) {
                TruthValueResponse response = new TruthValueResponse();
                response.setErrorMessage("无法解析公式: " + formulaStr);
                return response;
            }

            TruthValueResponse response = new TruthValueResponse();

            // 解析变量和赋值
            List<TruthAssignment> assignments = parseAssignments(formula, variablesStr, assignmentStr);

            // 计算真值
            boolean truthValue = formula.getTruthValue(assignments);
            response.setTruthValue(truthValue);

            // 如果需要显示详细过程
            if (request.isShowDetailedProcess()) {
                List<Map<String, Object>> detailedSteps = generateDetailedSteps(formula, assignments);
                response.setDetailedSteps(detailedSteps);
            }

            // 如果需要检查公式类型
            if (request.isCheckType()) {
                String formulaType = determineFormulaType(formula);
                response.setFormulaType(formulaType);
            }

            // 如果需要显示严格形式公式
            if (request.isShowStrictForm()) {
                Map<String, Object> syntaxData = generateSyntaxData(formula);
                response.setSyntaxData(syntaxData);
            }

            return response;

        } catch (Exception e) {
            TruthValueResponse response = new TruthValueResponse();
            response.setErrorMessage("计算真值时发生错误: " + e.getMessage());
            return response;
        }
    }

    // 解析变量和赋值
    private List<TruthAssignment> parseAssignments(Formula formula, String variablesStr, String assignmentStr) {
        List<TruthAssignment> assignments = new ArrayList<>();

        // 如果提供了变量和赋值，使用提供的值
        if (variablesStr != null && !variablesStr.trim().isEmpty() &&
            assignmentStr != null && !assignmentStr.trim().isEmpty()) {

            char[] variables = variablesStr.replaceAll("[^a-zA-Z]", "").toCharArray();
            String assignment = assignmentStr.replaceAll("[^01]", "");

            for (int i = 0; i < Math.min(variables.length, assignment.length()); i++) {
                boolean value = assignment.charAt(i) == '1';
                assignments.add(new TruthAssignment(variables[i], value));
            }
        } else {
            // 否则使用公式中的变量，默认赋值为true
            char[] formulaVariables = formula.getAllVariables();
            for (char variable : formulaVariables) {
                assignments.add(new TruthAssignment(variable, true));
            }
        }

        return assignments;
    }

    // 生成详细计算步骤
    private List<Map<String, Object>> generateDetailedSteps(Formula formula, List<TruthAssignment> assignments) {
        List<Map<String, Object>> steps = new ArrayList<>();

        try {
            // 创建TruthAssignmentFunction列表
            List<TruthAssignmentFunction> functionList = new ArrayList<>();
            if (assignments != null && !assignments.isEmpty()) {
                // 如果提供了具体的赋值，使用这些赋值
                TruthAssignmentFunction function = new TruthAssignmentFunction(assignments);
                functionList.add(function);
            }

            // 创建FormulaTruthTable对象
            FormulaTruthTable truthTable;
            if (functionList.size() > 0) {
                // 使用提供的赋值
                truthTable = new FormulaTruthTable(formula, functionList);
            } else {
                // 使用所有可能的赋值（生成完整真值表）
                truthTable = new FormulaTruthTable(formula);
            }

            // 生成详细真值表LaTeX字符串
            String detailedTable = truthTable.createDetailedTruthTable();

            Map<String, Object> step1 = new HashMap<>();
            step1.put("formula", detailedTable);
            step1.put("explanation", "详细真值表显示了每个子公式的计算过程");
            steps.add(step1);

        } catch (Exception e) {
            // 如果生成详细真值表失败，生成简单的变量赋值和结果
            if (assignments != null && !assignments.isEmpty()) {
                Map<String, Object> step1 = new HashMap<>();
                StringBuilder assignmentStr = new StringBuilder("变量赋值: ");
                for (TruthAssignment assignment : assignments) {
                    assignmentStr.append(assignment.getVariable()).append("=")
                                 .append(assignment.getValue() ? "\\mathbf{1}" : "\\mathbf{0}").append(", ");
                }
                if (assignmentStr.length() > 2) {
                    assignmentStr.setLength(assignmentStr.length() - 2);
                }
                step1.put("formula", assignmentStr.toString());
                step1.put("explanation", "各变量的真值赋值");
                steps.add(step1);
            }

            // 计算最终结果
            boolean result = formula.getTruthValue(assignments);
            Map<String, Object> step2 = new HashMap<>();
            step2.put("formula", formula.toSimpleLaTeXString() + " = " + (result ? "\\mathbf{1}" : "\\mathbf{0}"));
            step2.put("explanation", "计算得到公式的最终真值");
            steps.add(step2);
        }

        return steps;
    }

    // 生成严格形式数据
    private Map<String, Object> generateSyntaxData(Formula formula) {
        Map<String, Object> syntaxData = new HashMap<>();
        syntaxData.put("strictForm", formula.toString());
        syntaxData.put("simpleForm", formula.toSimpleLaTeXString());
        syntaxData.put("formulaType", getFormulaTypeName(formula));
        return syntaxData;
    }

    private String getFormulaTypeName(Formula formula) {
        if (formula.isAtomicFormula()) return "原子公式";
        if (formula.isNegFormula()) return "否定公式";
        if (formula.isAndFormula()) return "合取公式";
        if (formula.isOrFormula()) return "析取公式";
        if (formula.isImpFormula()) return "蕴含公式";
        if (formula.isBiImpFormula()) return "双蕴含公式";
        return "复合公式";
    }

    private String determineFormulaType(Formula formula) {
        char[] variables = formula.getAllVariables();
        int variableCount = variables.length;

        if (variableCount == 0) {
            // 没有变量的公式，直接返回其真值
            List<TruthAssignment> emptyAssignments = new ArrayList<>();
            boolean value = formula.getTruthValue(emptyAssignments);
            return value ? "tautology" : "contradiction";
        }

        // 生成所有可能的真值赋值
        List<List<TruthAssignment>> allAssignments = generateAllAssignments(variables);

        boolean allTrue = true;
        boolean allFalse = true;

        for (List<TruthAssignment> assignment : allAssignments) {
            boolean value = formula.getTruthValue(assignment);
            if (value) {
                allFalse = false;
            } else {
                allTrue = false;
            }

            // 如果既不是重言式也不是矛盾式，提前退出
            if (!allTrue && !allFalse) {
                break;
            }
        }

        if (allTrue) {
            return "tautology";
        } else if (allFalse) {
            return "contradiction";
        } else {
            return "contingency";
        }
    }

    private List<List<TruthAssignment>> generateAllAssignments(char[] variables) {
        List<List<TruthAssignment>> result = new ArrayList<>();
        int n = variables.length;
        int totalAssignments = 1 << n; // 2^n

        for (int i = 0; i < totalAssignments; i++) {
            List<TruthAssignment> assignment = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                boolean value = ((i >> (n - 1 - j)) & 1) == 1;
                assignment.add(new TruthAssignment(variables[j], value));
            }
            result.add(assignment);
        }

        return result;
    }

    private String generateTruthTableLatex(Formula formula) {
        try {
            // FormulaTruthTable没有toLaTeXString方法，直接使用简单表格生成
            return generateSimpleLatexTable(formula);
        } catch (Exception e) {
            // 如果生成LaTeX表格失败，返回错误信息
            return "\\text{生成真值表失败: " + e.getMessage() + "}";
        }
    }

    private String generateSimpleLatexTable(Formula formula) {
        char[] variables = formula.getAllVariables();
        int variableCount = variables.length;

        StringBuilder latex = new StringBuilder();
        latex.append("\\begin{array}{");
        for (int i = 0; i < variableCount; i++) {
            latex.append("c");
        }
        latex.append("c}\n");

        // 表头
        for (int i = 0; i < variableCount; i++) {
            latex.append(variables[i]);
            if (i < variableCount - 1) {
                latex.append(" & ");
            }
        }
        latex.append(" & ");
        latex.append(formula.toSimpleLaTeXString());
        latex.append(" \\\\\n\\hline\n");

        // 表格内容
        List<List<TruthAssignment>> allAssignments = generateAllAssignments(variables);
        for (List<TruthAssignment> assignment : allAssignments) {
            for (int i = 0; i < variableCount; i++) {
                boolean value = assignment.get(i).getValue();
                latex.append(value ? "T" : "F");
                if (i < variableCount - 1) {
                    latex.append(" & ");
                }
            }
            latex.append(" & ");
            boolean formulaValue = formula.getTruthValue(assignment);
            latex.append(formulaValue ? "T" : "F");
            latex.append(" \\\\\n");
        }

        latex.append("\\end{array}");
        return latex.toString();
    }
}