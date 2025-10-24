package com.deedm.service;

import com.deedm.model.NormalFormCheckRequest;
import com.deedm.model.NormalFormGenerateRequest;
import com.deedm.model.PrincipalNormalFormRequest;
import com.deedm.model.PrincipalNormalFormResponse;
import com.deedm.legacy.proplogic.formula.Formula;
import com.deedm.legacy.proplogic.FormulaBuilder;
import com.deedm.legacy.proplogic.normalFormula.NormalFormulaCalculator;
import com.deedm.legacy.proplogic.normalFormula.ConjunctiveNormalFormula;
import com.deedm.legacy.proplogic.normalFormula.DisjunctiveNormalFormula;
import com.deedm.legacy.proplogic.normalFormula.PCNFormula;
import com.deedm.legacy.proplogic.normalFormula.PDNFormula;
import com.deedm.legacy.proplogic.equiv.EquivCalculusRecorder;
import com.deedm.legacy.proplogic.equiv.EquivCalculusStep;
import com.deedm.legacy.proplogic.normalFormula.ExpandNFRecorder;
import com.deedm.legacy.proplogic.normalFormula.ExpandNFStep;
import com.deedm.legacy.setrelfun.SetrelfunUtil;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PrincipalNormalFormService {

    /**
     * Calculate principal normal forms based on the request
     */
    public PrincipalNormalFormResponse calculatePrincipalNormalForm(PrincipalNormalFormRequest request) {
        try {
            PrincipalNormalFormResponse response = new PrincipalNormalFormResponse();
            response.setSuccess(true);
            response.setMessage("Principal normal form calculation completed");

            // Combine formulas based on logical relation
            String combinedFormula = combineFormulas(request.getFormulas(), request.getLogicalRelation());
            response.setOriginalFormula(request.getFormulas().get(0)); // First formula as main
            response.setCombinedFormula(combinedFormula);

            // Calculate CNF if requested
            if (request.getNormalFormTypes().contains("cnf")) {
                PrincipalNormalFormResponse.CNFResult cnfResult = calculateCNF(combinedFormula, request);
                response.setCnfResult(cnfResult);

                // Calculate detailed steps if requested
                if (request.getCalculationMethods().contains("detailed")) {
                    List<PrincipalNormalFormResponse.CalculationStep> cnfSteps = getCNFCalculationSteps(combinedFormula);
                    response.setCnfSteps(cnfSteps);
                }

                // Calculate expansion steps if PNF is requested
                if (request.getNormalFormTypes().contains("pnf")) {
                    List<PrincipalNormalFormResponse.ExpansionStep> cnfExpansionSteps = getCNFExpansionSteps(combinedFormula, request.getVariableSet());
                    response.setCnfExpansionSteps(cnfExpansionSteps);
                }
            }

            // Calculate DNF if requested
            if (request.getNormalFormTypes().contains("dnf")) {
                PrincipalNormalFormResponse.DNFResult dnfResult = calculateDNF(combinedFormula, request);
                response.setDnfResult(dnfResult);

                // Calculate detailed steps if requested
                if (request.getCalculationMethods().contains("detailed")) {
                    List<PrincipalNormalFormResponse.CalculationStep> dnfSteps = getDNFCalculationSteps(combinedFormula);
                    response.setDnfSteps(dnfSteps);
                }

                // Calculate expansion steps if PNF is requested
                if (request.getNormalFormTypes().contains("pnf")) {
                    List<PrincipalNormalFormResponse.ExpansionStep> dnfExpansionSteps = getDNFExpansionSteps(combinedFormula, request.getVariableSet());
                    response.setDnfExpansionSteps(dnfExpansionSteps);
                }
            }

            // Generate truth table if requested
            if (request.getCalculationMethods().contains("table")) {
                String truthTable = generateTruthTable(combinedFormula, request.getVariableSet());
                response.setTruthTable(truthTable);
            }

            return response;

        } catch (Exception e) {
            PrincipalNormalFormResponse errorResponse = new PrincipalNormalFormResponse();
            errorResponse.setSuccess(false);
            errorResponse.setErrorMessage("Failed to calculate principal normal form: " + e.getMessage());
            return errorResponse;
        }
    }

    /**
     * Generate random formulas for practice
     */
    public Map<String, Object> generateFormulas(NormalFormGenerateRequest request) {
        try {
            // Validate variable set
            if (request.getVariableSet() == null || request.getVariableSet().trim().isEmpty()) {
                return Map.of("success", false, "error", "Variable set is required");
            }

            // Extract variable set using legacy utility
            char[] varSet = SetrelfunUtil.extractSetElements(request.getVariableSet(), false);
            if (varSet == null) {
                return Map.of("success", false, "error", "Invalid variable set: " + request.getVariableSet());
            }

            // Generate formulas using legacy code
            boolean isDNF = "or".equals(request.getLogicalRelation());
            List<String> generatedFormulas = new ArrayList<>();

            for (int i = 0; i < request.getCount(); i++) {
                String symbolString;
                if (isDNF) {
                    symbolString = NormalFormulaCalculator.randomGenerateSimpleConjunctiveFormula(varSet);
                } else {
                    symbolString = NormalFormulaCalculator.randomGenerateSimpleDisjunctiveFormula(varSet);
                }

                Formula formula = FormulaBuilder.buildFromSymbolFormulaString(symbolString);
                if (formula != null) {
                    generatedFormulas.add("(" + formula.toSimpleLaTeXString() + ")");
                }
            }

            if (generatedFormulas.isEmpty()) {
                return Map.of("success", false, "error", "Failed to generate any valid formulas");
            }

            // Join formulas with newlines
            String formulaList = String.join("\n", generatedFormulas);

            return Map.of(
                "success", true,
                "formula", formulaList,
                "formulas", generatedFormulas,
                "message", "Generated " + generatedFormulas.size() + " random formulas"
            );

        } catch (Exception e) {
            return Map.of(
                "success", false,
                "error", "Failed to generate formulas: " + e.getMessage()
            );
        }
    }

    /**
     * Check if formulas are valid for normal form calculation
     */
    public Map<String, Object> checkFormulas(NormalFormCheckRequest request) {
        try {
            List<String> errors = new ArrayList<>();
            List<Map<String, Object>> formulaResults = new ArrayList<>();

            // Check if formulas list is empty
            if (request.getFormulas() == null || request.getFormulas().isEmpty()) {
                errors.add("No formulas provided");
                return Map.of("valid", false, "errors", errors);
            }

            // Check if variable set is provided
            if (request.getVariableSet() == null || request.getVariableSet().trim().isEmpty()) {
                errors.add("Variable set is required");
                return Map.of("valid", false, "errors", errors);
            }

            // Validate variable set using legacy utility
            char[] varSet = SetrelfunUtil.extractSetElements(request.getVariableSet(), false);
            if (varSet == null) {
                errors.add("Invalid variable set: " + request.getVariableSet());
                return Map.of("valid", false, "errors", errors);
            }

            // Validate each formula using legacy code
            for (String formulaStr : request.getFormulas()) {
                Map<String, Object> result = new HashMap<>();
                result.put("formula", formulaStr);

                try {
                    Formula formula = FormulaBuilder.buildFromLaTexFormulaString(formulaStr);
                    if (formula == null) {
                        String errorMsg = "Invalid formula: " + formulaStr;
                        if (FormulaBuilder.getErrorMessage() != null) {
                            errorMsg += " (" + FormulaBuilder.getErrorMessage() + ")";
                        }
                        errors.add(errorMsg);
                        result.put("valid", false);
                        result.put("error", errorMsg);
                    } else {
                        result.put("valid", true);
                        result.put("message", "Formula is valid");
                    }
                } catch (Exception e) {
                    String errorMsg = "Error parsing formula: " + formulaStr + " - " + e.getMessage();
                    errors.add(errorMsg);
                    result.put("valid", false);
                    result.put("error", errorMsg);
                }

                formulaResults.add(result);
            }

            return Map.of(
                "valid", errors.isEmpty(),
                "errors", errors,
                "formulaResults", formulaResults,
                "message", errors.isEmpty() ? "All formulas are valid" : "Some formulas have errors"
            );

        } catch (Exception e) {
            return Map.of(
                "valid", false,
                "errors", List.of("Validation failed: " + e.getMessage())
            );
        }
    }

    // Helper methods

    private String combineFormulas(List<String> formulas, String logicalRelation) {
        if (formulas == null || formulas.isEmpty()) {
            return "";
        }
        if (formulas.size() == 1) {
            return formulas.get(0);
        }

        String operator = "or".equals(logicalRelation) ? "\\vee" : "\\wedge";
        StringBuilder combined = new StringBuilder("(" + formulas.get(0) + ")");

        for (int i = 1; i < formulas.size(); i++) {
            combined.append(" ").append(operator).append(" (").append(formulas.get(i)).append(")");
        }

        return combined.toString();
    }

    private PrincipalNormalFormResponse.CNFResult calculateCNF(String formula, PrincipalNormalFormRequest request) {
        try {
            // Parse formula using legacy code
            Formula parsedFormula = FormulaBuilder.buildFromLaTexFormulaString(formula);
            if (parsedFormula == null) {
                return new PrincipalNormalFormResponse.CNFResult(null, null, null);
            }

            // Calculate CNF using legacy code
            Formula cnfFormula = NormalFormulaCalculator.calculateCNF(parsedFormula);
            ConjunctiveNormalFormula conjNormForm = ConjunctiveNormalFormula.convertAndSimplify(cnfFormula);
            String finalCNF = conjNormForm.toFormula().toSimpleLaTeXString();

            String pcnf = null;
            String pdnf = null;

            // Calculate PCNF if requested
            if (request.getNormalFormTypes().contains("pnf")) {
                char[] varSet = SetrelfunUtil.extractSetElements(request.getVariableSet(), false);
                if (varSet != null) {
                    PCNFormula lastPCNF = NormalFormulaCalculator.expandToPCNF(conjNormForm, varSet);
                    pcnf = lastPCNF.toNamedLaTeXString();
                    pdnf = lastPCNF.toNamedPDNFLaTeXString();
                }
            }

            return new PrincipalNormalFormResponse.CNFResult(finalCNF, pcnf, pdnf);

        } catch (Exception e) {
            System.err.println("Error calculating CNF: " + e.getMessage());
            return new PrincipalNormalFormResponse.CNFResult(null, null, null);
        }
    }

    private PrincipalNormalFormResponse.DNFResult calculateDNF(String formula, PrincipalNormalFormRequest request) {
        try {
            // Parse formula using legacy code
            Formula parsedFormula = FormulaBuilder.buildFromLaTexFormulaString(formula);
            if (parsedFormula == null) {
                return new PrincipalNormalFormResponse.DNFResult(null, null, null);
            }

            // Calculate DNF using legacy code
            Formula dnfFormula = NormalFormulaCalculator.calculateDNF(parsedFormula);
            DisjunctiveNormalFormula disjNormForm = DisjunctiveNormalFormula.convertAndSimplify(dnfFormula);
            String finalDNF = disjNormForm.toFormula().toSimpleLaTeXString();

            String pdnf = null;
            String pcnf = null;

            // Calculate PDNF if requested
            if (request.getNormalFormTypes().contains("pnf")) {
                char[] varSet = SetrelfunUtil.extractSetElements(request.getVariableSet(), false);
                if (varSet != null) {
                    PDNFormula lastPDNF = NormalFormulaCalculator.expandToPDNF(disjNormForm, varSet);
                    pdnf = lastPDNF.toNamedLaTeXString();
                    pcnf = lastPDNF.toNamedPCNFLaTeXString();
                }
            }

            return new PrincipalNormalFormResponse.DNFResult(finalDNF, pdnf, pcnf);

        } catch (Exception e) {
            System.err.println("Error calculating DNF: " + e.getMessage());
            return new PrincipalNormalFormResponse.DNFResult(null, null, null);
        }
    }

    private List<PrincipalNormalFormResponse.CalculationStep> getCNFCalculationSteps(String formula) {
        try {
            List<PrincipalNormalFormResponse.CalculationStep> steps = new ArrayList<>();

            // Get calculation steps from legacy code
            EquivCalculusRecorder calculusRecorder = NormalFormulaCalculator.getCalculusRecorder();
            List<EquivCalculusStep> calculusStepList = calculusRecorder.getStepList();

            for (EquivCalculusStep step : calculusStepList) {
                Formula stepFormula = step.getFormula();
                String comments = step.getComments();
                steps.add(new PrincipalNormalFormResponse.CalculationStep(
                    stepFormula.toSimpleLaTeXString(), comments
                ));
            }

            return steps;

        } catch (Exception e) {
            System.err.println("Error getting CNF calculation steps: " + e.getMessage());
            // Return basic steps if error occurs
            List<PrincipalNormalFormResponse.CalculationStep> steps = new ArrayList<>();
            steps.add(new PrincipalNormalFormResponse.CalculationStep(formula, "原始公式"));
            steps.add(new PrincipalNormalFormResponse.CalculationStep(formula, "计算完成"));
            return steps;
        }
    }

    /**
     * Get detailed CNF expansion steps like the original application
     */
    private List<PrincipalNormalFormResponse.ExpansionStep> getCNFExpansionSteps(String formula, String variableSet) {
        try {
            List<PrincipalNormalFormResponse.ExpansionStep> steps = new ArrayList<>();

            // Parse formula and calculate CNF
            Formula parsedFormula = FormulaBuilder.buildFromLaTexFormulaString(formula);
            if (parsedFormula == null) {
                return steps;
            }

            Formula cnfFormula = NormalFormulaCalculator.calculateCNF(parsedFormula);
            ConjunctiveNormalFormula conjNormForm = ConjunctiveNormalFormula.convertAndSimplify(cnfFormula);

            // Get expansion recorder for detailed steps
            ExpandNFRecorder expandRecorder = NormalFormulaCalculator.getExpandRecorder();
            List<ExpandNFStep> stepList = expandRecorder.getStepList();

            for (ExpandNFStep step : stepList) {
                Formula stepFormula = step.getFormula();
                String formulaCode = step.getFormulaBinaryCodeString();

                // 获取原始结果代码，然后修正连接符
                String originalResultCodes = step.getResultCodesNamedLaTeXString(ExpandNFRecorder.TYPE_CNF);
                String correctedResultCodes = originalResultCodes.replaceAll("\\\\vee", "\\\\wedge");

                // 恢复原来的下标格式，使用新的KaTeX字体配置
                // correctedResultCodes = correctedResultCodes
                //     .replaceAll("M_\\{(\\d+)\\}", "M($1)")
                //     .replaceAll("m_\\{(\\d+)\\}", "m($1)");

                
                steps.add(new PrincipalNormalFormResponse.ExpansionStep(
                    stepFormula.toSimpleLaTeXString(),
                    formulaCode,
                    correctedResultCodes,
                    "扩展简单合取式",
                    "得到极大项"
                ));
            }

            return steps;

        } catch (Exception e) {
            System.err.println("Error getting CNF expansion steps: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get detailed DNF expansion steps like the original application
     */
    private List<PrincipalNormalFormResponse.ExpansionStep> getDNFExpansionSteps(String formula, String variableSet) {
        try {
            List<PrincipalNormalFormResponse.ExpansionStep> steps = new ArrayList<>();

            // Parse formula and calculate DNF
            Formula parsedFormula = FormulaBuilder.buildFromLaTexFormulaString(formula);
            if (parsedFormula == null) {
                return steps;
            }

            Formula dnfFormula = NormalFormulaCalculator.calculateDNF(parsedFormula);
            DisjunctiveNormalFormula disjNormForm = DisjunctiveNormalFormula.convertAndSimplify(dnfFormula);

            // Get expansion recorder for detailed steps
            ExpandNFRecorder expandRecorder = NormalFormulaCalculator.getExpandRecorder();
            List<ExpandNFStep> stepList = expandRecorder.getStepList();

            for (ExpandNFStep step : stepList) {
                Formula stepFormula = step.getFormula();
                String formulaCode = step.getFormulaBinaryCodeString();

                // 对于DNF，极小项之间应该使用\vee连接（这本身就是正确的，所以不需要修正）
                String resultCodes = step.getResultCodesNamedLaTeXString(ExpandNFRecorder.TYPE_DNF);

                // 恢复原来的下标格式，使用新的KaTeX字体配置
                // resultCodes = resultCodes
                //     .replaceAll("M_\\{(\\d+)\\}", "M($1)")
                //     .replaceAll("m_\\{(\\d+)\\}", "m($1)");

                
                steps.add(new PrincipalNormalFormResponse.ExpansionStep(
                    stepFormula.toSimpleLaTeXString(),
                    formulaCode,
                    resultCodes,
                    "扩展简单析取式",
                    "得到极小项"
                ));
            }

            return steps;

        } catch (Exception e) {
            System.err.println("Error getting DNF expansion steps: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<PrincipalNormalFormResponse.CalculationStep> getDNFCalculationSteps(String formula) {
        try {
            List<PrincipalNormalFormResponse.CalculationStep> steps = new ArrayList<>();

            // Get calculation steps from legacy code
            EquivCalculusRecorder calculusRecorder = NormalFormulaCalculator.getCalculusRecorder();
            List<EquivCalculusStep> calculusStepList = calculusRecorder.getStepList();

            for (EquivCalculusStep step : calculusStepList) {
                Formula stepFormula = step.getFormula();
                String comments = step.getComments();
                steps.add(new PrincipalNormalFormResponse.CalculationStep(
                    stepFormula.toSimpleLaTeXString(), comments
                ));
            }

            return steps;

        } catch (Exception e) {
            System.err.println("Error getting DNF calculation steps: " + e.getMessage());
            // Return basic steps if error occurs
            List<PrincipalNormalFormResponse.CalculationStep> steps = new ArrayList<>();
            steps.add(new PrincipalNormalFormResponse.CalculationStep(formula, "原始公式"));
            steps.add(new PrincipalNormalFormResponse.CalculationStep(formula, "计算完成"));
            return steps;
        }
    }

    private String generateTruthTable(String formula, String variableSet) {
        try {
            // Parse formula using legacy code
            Formula parsedFormula = FormulaBuilder.buildFromLaTexFormulaString(formula);
            if (parsedFormula == null) {
                return "Error: Unable to parse formula for truth table generation";
            }

            // Generate truth table using legacy code
            com.deedm.legacy.proplogic.FormulaTruthTable truthTable = new com.deedm.legacy.proplogic.FormulaTruthTable(parsedFormula);
            return truthTable.createDetailedTruthTable();

        } catch (Exception e) {
            System.err.println("Error generating truth table: " + e.getMessage());
            return "Error: Unable to generate truth table - " + e.getMessage();
        }
    }
}