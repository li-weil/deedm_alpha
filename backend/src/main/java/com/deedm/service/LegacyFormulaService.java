package com.deedm.service;

import com.deedm.legacy.proplogic.FormulaBuilder;
import com.deedm.legacy.proplogic.FormulaTruthTable;
import com.deedm.legacy.proplogic.TruthAssignment;
import com.deedm.legacy.proplogic.formula.Formula;
import com.deedm.model.TruthTableRequest;
import com.deedm.model.TruthTableResponse;
import com.deedm.model.TruthTableRow;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service that wraps legacy formula logic for the TruthTableController
 */
@Service
public class LegacyFormulaService {

    public TruthTableResponse generateTruthTable(TruthTableRequest request) {
        try {
            // Build formula from the input string using legacy FormulaBuilder
            Formula formula = FormulaBuilder.buildFromSymbolFormulaString(request.getFormula());

            // Generate truth table using legacy FormulaTruthTable
            FormulaTruthTable truthTable = new FormulaTruthTable(formula);

            // Convert legacy truth table to our response format
            TruthTableResponse response = new TruthTableResponse();
            response.setFormula(request.getFormula());
            response.setVariables(new ArrayList<>(truthTable.getVariables()));

            List<TruthTableRow> rows = new ArrayList<>();
            for (TruthAssignment assignment : truthTable.getAssignments()) {
                List<String> variableValues = new ArrayList<>();
                for (String variable : truthTable.getVariables()) {
                    variableValues.add(String.valueOf(assignment.getValue(variable)));
                }
                String resultValue = String.valueOf(assignment.evaluate(formula));
                TruthTableRow row = new TruthTableRow(variableValues, resultValue);
                rows.add(row);
            }
            response.setRows(rows);

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate truth table: " + e.getMessage(), e);
        }
    }

    public List<String> validateFormulas(List<String> formulas) {
        List<String> errors = new ArrayList<>();
        for (String formula : formulas) {
            try {
                FormulaBuilder.buildFromSymbolFormulaString(formula);
            } catch (Exception e) {
                errors.add("Invalid formula '" + formula + "': " + e.getMessage());
            }
        }
        return errors;
    }
}