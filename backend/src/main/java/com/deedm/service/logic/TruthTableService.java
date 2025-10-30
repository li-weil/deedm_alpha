package com.deedm.service.logic;

import com.deedm.model.logic.TruthTableRequest;
import com.deedm.model.logic.TruthTableResponse;
import com.deedm.legacy.proplogic.formula.Formula;
import com.deedm.legacy.proplogic.FormulaBuilder;
import com.deedm.legacy.proplogic.FormulaTruthTable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import java.util.List;


  @Service
public class TruthTableService {

    public TruthTableResponse generateTruthTable(TruthTableRequest request) {
        try {
            List<String> formulas = request.getFormulas();

            // For now, handle only the first formula (can be extended for multiple formulas)
            String formula = formulas.get(0);

            // Use legacy FormulaBuilder to parse the LaTeX formula
            Formula parsedFormula = FormulaBuilder.buildFromLaTexFormulaString(formula);

            if (parsedFormula == null) {
                TruthTableResponse errorResponse = new TruthTableResponse();
                errorResponse.setErrorMessage("Invalid formula: " + FormulaBuilder.getErrorMessage());
                return errorResponse;
            }

            // Use legacy FormulaTruthTable to generate truth table
            FormulaTruthTable truthTable = new FormulaTruthTable(parsedFormula);

            // 直接调用legacy方法生成LaTeX格式的真值表
            String truthTableLaTeX;
            if (request.isDetailed()) {
                truthTableLaTeX = truthTable.createDetailedTruthTable();
            } else {
                truthTableLaTeX = truthTable.createSimpleTruthTable();
            }

            // Determine formula type
            String formulaType;
            if (truthTable.isAllTruthAssignment()) {
                formulaType = "tautology";
            } else if (truthTable.hasTruthAssignment()) {
                formulaType = "contingency";
            } else {
                formulaType = "contradiction";
            }

            // 返回包含legacy LaTeX输出的响应
            TruthTableResponse response = new TruthTableResponse();
            response.setLatexTable(truthTableLaTeX);
            response.setFormulaType(formulaType);

            return response;

        } catch (Exception e) {
            TruthTableResponse response = new TruthTableResponse();
            response.setErrorMessage("Error generating truth table: " + e.getMessage());
            return response;
        }
    }

  
    public List<String> validateFormulas(List<String> formulas) {
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < formulas.size(); i++) {
            String formula = formulas.get(i).trim();
            if (formula.isEmpty()) {
                errors.add("Formula " + (i + 1) + " is empty");
                continue;
            }

            // Use legacy FormulaBuilder to validate the formula
            Formula parsedFormula = FormulaBuilder.buildFromLaTexFormulaString(formula);
            if (parsedFormula == null) {
                errors.add("Formula " + (i + 1) + " '" + formula + "' is not valid: " + FormulaBuilder.getErrorMessage());
            }
        }

        return errors;
    }
}