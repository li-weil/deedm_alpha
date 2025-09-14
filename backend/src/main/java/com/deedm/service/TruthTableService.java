package com.deedm.service;

import com.deedm.model.TruthTableRequest;
import com.deedm.model.TruthTableResponse;
import com.deedm.model.TruthTableRow;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TruthTableService {

    private static final char OPERATOR_AND = '&';
    private static final char OPERATOR_NOT = '~';
    private static final char OPERATOR_OR = '|';
    private static final char OPERATOR_IMP = '>';
    private static final char OPERATOR_EQ = '=';
    private static final char LEFT_BRACKET = '(';
    private static final char RIGHT_BRACKET = ')';

    public TruthTableResponse generateTruthTable(TruthTableRequest request) {
        try {
            List<String> formulas = request.getFormulas();
            List<TruthTableResponse> responses = new ArrayList<>();

            for (String formula : formulas) {
                TruthTableResponse response = generateSingleTruthTable(formula, request.isDetailed());
                responses.add(response);
            }

            // For now, return the first formula's truth table
            // TODO: Handle multiple formulas if needed
            return responses.get(0);

        } catch (Exception e) {
            TruthTableResponse response = new TruthTableResponse();
            response.setErrorMessage("Error generating truth table: " + e.getMessage());
            return response;
        }
    }

    private TruthTableResponse generateSingleTruthTable(String formula, boolean detailed) {
        // Parse formula to extract variables
        List<Character> variables = extractVariables(formula);

        // Generate all possible truth assignments
        List<boolean[]> assignments = generateTruthAssignments(variables.size());

        // Create column headers
        List<String> columnHeaders = new ArrayList<>();
        for (char var : variables) {
            columnHeaders.add("$" + var + "$");
        }
        columnHeaders.add("$" + cleanFormulaForDisplay(formula) + "$");

        // Generate truth table rows
        List<TruthTableRow> rows = new ArrayList<>();
        boolean allTrue = true;
        boolean hasTrue = false;

        for (boolean[] assignment : assignments) {
            List<String> variableValues = new ArrayList<>();
            for (boolean value : assignment) {
                variableValues.add(value ? "$\\mathbf{1}$" : "$\\mathbf{0}$");
            }

            boolean result = evaluateFormula(formula, variables, assignment);
            if (!result) allTrue = false;
            if (result) hasTrue = true;

            String resultValue = result ? "$\\mathbf{1}$" : "$\\mathbf{0}$";
            rows.add(new TruthTableRow(variableValues, resultValue));
        }

        // Determine formula type
        String formulaType;
        if (allTrue) {
            formulaType = "tautology";
        } else if (hasTrue) {
            formulaType = "contingency";
        } else {
            formulaType = "contradiction";
        }

        return new TruthTableResponse(columnHeaders, rows, formulaType);
    }

    private List<Character> extractVariables(String formula) {
        List<Character> variables = new ArrayList<>();
        // Extract all lowercase letters that appear as variables in the formula
        // This is a more robust approach than the hardcoded list

        // Clean the formula by removing LaTeX commands and operators, keeping only variable letters
        String cleanedFormula = formula.replaceAll("\\\\[a-zA-Z]+", ""); // Remove LaTeX commands
        cleanedFormula = cleanedFormula.replaceAll("[^a-zA-Z]", ""); // Keep only letters

        // Extract unique lowercase letters (variables are typically lowercase)
        for (char c : cleanedFormula.toCharArray()) {
            if (Character.isLowerCase(c) && !variables.contains(c)) {
                variables.add(c);
            }
        }

        // Sort variables alphabetically for consistent ordering
        variables.sort(Character::compareTo);
        return variables;
    }

    private List<boolean[]> generateTruthAssignments(int variableCount) {
        List<boolean[]> assignments = new ArrayList<>();
        int total = (int) Math.pow(2, variableCount);

        for (int i = 0; i < total; i++) {
            boolean[] assignment = new boolean[variableCount];
            for (int j = 0; j < variableCount; j++) {
                assignment[j] = ((i >> (variableCount - 1 - j)) & 1) == 1;
            }
            assignments.add(assignment);
        }

        return assignments;
    }

    private boolean evaluateFormula(String formula, List<Character> variables, boolean[] assignment) {
        // Convert formula to internal representation and evaluate
        String cleanedFormula = cleanFormulaForEvaluation(formula);

        // Replace variables with their truth values more carefully
        // Use word boundaries to avoid partial replacements
        for (int i = 0; i < variables.size(); i++) {
            char var = variables.get(i);
            boolean value = assignment[i];
            // Replace only standalone variables, not parts of other words
            cleanedFormula = cleanedFormula.replaceAll("\\b" + var + "\\b", String.valueOf(value));
        }

        // Debug output
        System.out.println("Evaluating: " + cleanedFormula);

        boolean result = evaluateExpression(cleanedFormula);
        System.out.println("Result: " + result);

        return result;
    }

    private String cleanFormulaForEvaluation(String formula) {
        String result = formula;

        // Replace LaTeX operators with internal symbols (handle double backslashes)
        result = result.replaceAll("\\\\\\\\wedge", "&");
        result = result.replaceAll("\\\\\\\\vee", "|");
        result = result.replaceAll("\\\\\\\\neg", "~");
        result = result.replaceAll("\\\\\\\\rightarrow", ">");
        result = result.replaceAll("\\\\\\\\leftrightarrow", "=");

        // Also handle single backslashes as fallback
        result = result.replaceAll("\\\\wedge", "&");
        result = result.replaceAll("\\\\vee", "|");
        result = result.replaceAll("\\\\neg", "~");
        result = result.replaceAll("\\\\rightarrow", ">");
        result = result.replaceAll("\\\\leftrightarrow", "=");

        // Remove LaTeX formatting
        result = result.replaceAll("\\\\\\\\mathbf", "");
        result = result.replaceAll("\\\\mathbf", "");
        result = result.replaceAll("[{}]", "");
        result = result.replaceAll("\\$", "");
        result = result.replaceAll("\\\\", "");

        return result;
    }

    private String cleanFormulaForDisplay(String formula) {
        // Fix regex syntax - use replaceAll instead of replace
        // This should convert double backslashes to single backslashes for LaTeX
        return formula.replaceAll("\\\\\\\\([a-zA-Z]+)", "\\\\$1");
    }

    private boolean evaluateExpression(String expr) {
        try {
            // Simple recursive descent evaluator for logical expressions
            // This is a basic implementation that handles the main operators: &, |, ~, >, =
            return evaluateOrExpression(expr);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean evaluateOrExpression(String expr) {
        // Handle OR operator (|)
        int orIndex = findOperatorOutsideBrackets(expr, '|');
        if (orIndex != -1) {
            String left = expr.substring(0, orIndex).trim();
            String right = expr.substring(orIndex + 1).trim();
            return evaluateOrExpression(left) || evaluateAndExpression(right);
        }
        return evaluateAndExpression(expr);
    }

    private boolean evaluateAndExpression(String expr) {
        // Handle AND operator (&)
        int andIndex = findOperatorOutsideBrackets(expr, '&');
        if (andIndex != -1) {
            String left = expr.substring(0, andIndex).trim();
            String right = expr.substring(andIndex + 1).trim();
            return evaluateAndExpression(left) && evaluateImplicationExpression(right);
        }
        return evaluateImplicationExpression(expr);
    }

    private boolean evaluateImplicationExpression(String expr) {
        // Handle implication operator (>)
        int impIndex = findOperatorOutsideBrackets(expr, '>');
        if (impIndex != -1) {
            String left = expr.substring(0, impIndex).trim();
            String right = expr.substring(impIndex + 1).trim();
            boolean leftVal = evaluateImplicationExpression(left);
            boolean rightVal = evaluateEquivalenceExpression(right);
            return !leftVal || rightVal; // p → q ≡ ¬p ∨ q
        }
        return evaluateEquivalenceExpression(expr);
    }

    private boolean evaluateEquivalenceExpression(String expr) {
        // Handle equivalence operator (=)
        int eqIndex = findOperatorOutsideBrackets(expr, '=');
        if (eqIndex != -1) {
            String left = expr.substring(0, eqIndex).trim();
            String right = expr.substring(eqIndex + 1).trim();
            return evaluateEquivalenceExpression(left) == evaluateEquivalenceExpression(right);
        }
        return evaluateNegationExpression(expr);
    }

    private boolean evaluateNegationExpression(String expr) {
        // Handle negation operator (~)
        expr = expr.trim();
        if (expr.startsWith("~")) {
            return !evaluateNegationExpression(expr.substring(1).trim());
        }
        return evaluateAtomicExpression(expr);
    }

    private boolean evaluateAtomicExpression(String expr) {
        expr = expr.trim();
        if (expr.startsWith("(") && expr.endsWith(")")) {
            return evaluateOrExpression(expr.substring(1, expr.length() - 1));
        }
        // Handle boolean values
        if (expr.equals("true") || expr.equals("1")) return true;
        if (expr.equals("false") || expr.equals("0")) return false;
        // Default to false for unrecognized expressions
        return false;
    }

    private int findOperatorOutsideBrackets(String expr, char operator) {
        int bracketCount = 0;
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (c == '(') bracketCount++;
            else if (c == ')') bracketCount--;
            else if (c == operator && bracketCount == 0) {
                return i;
            }
        }
        return -1;
    }

    public List<String> validateFormulas(List<String> formulas) {
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < formulas.size(); i++) {
            String formula = formulas.get(i).trim();
            if (formula.isEmpty()) {
                errors.add("Formula " + (i + 1) + " is empty");
                continue;
            }

            // Basic validation
            if (!isValidFormula(formula)) {
                errors.add("Formula " + (i + 1) + " '" + formula + "' is not valid");
            }
        }

        return errors;
    }

    private boolean isValidFormula(String formula) {
        // Basic validation - check for balanced parentheses and valid characters
        int bracketCount = 0;
        for (char c : formula.toCharArray()) {
            if (c == LEFT_BRACKET) bracketCount++;
            else if (c == RIGHT_BRACKET) bracketCount--;
            else if (!isValidCharacter(c)) return false;
        }
        return bracketCount == 0;
    }

    private boolean isValidCharacter(char c) {
        return (c >= 'a' && c <= 'z') ||
               c == LEFT_BRACKET || c == RIGHT_BRACKET ||
               c == '\\' || c == ' ';
    }
}