package com.deedm.model.logic;

import java.util.List;

public class TruthTableResponse {
    private List<String> columnHeaders;
    private List<TruthTableRow> rows;
    private String formulaType; // "tautology", "contradiction", "contingency"
    private String errorMessage;
    private String latexTable; // Legacy LaTeX table output

    public TruthTableResponse() {}

    public TruthTableResponse(List<String> columnHeaders, List<TruthTableRow> rows, String formulaType) {
        this.columnHeaders = columnHeaders;
        this.rows = rows;
        this.formulaType = formulaType;
    }

    public List<String> getColumnHeaders() {
        return columnHeaders;
    }

    public void setColumnHeaders(List<String> columnHeaders) {
        this.columnHeaders = columnHeaders;
    }

    public List<TruthTableRow> getRows() {
        return rows;
    }

    public void setRows(List<TruthTableRow> rows) {
        this.rows = rows;
    }

    public String getFormulaType() {
        return formulaType;
    }

    public void setFormulaType(String formulaType) {
        this.formulaType = formulaType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getLatexTable() {
        return latexTable;
    }

    public void setLatexTable(String latexTable) {
        this.latexTable = latexTable;
    }
}