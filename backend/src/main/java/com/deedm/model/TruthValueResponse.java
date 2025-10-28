package com.deedm.model;

import java.util.List;
import java.util.Map;

public class TruthValueResponse {
    private Boolean truthValue;
    private String formulaType;
    private String latexTable;
    private List<Map<String, Object>> detailedSteps;
    private Map<String, Object> syntaxData;
    private String errorMessage;

    public TruthValueResponse() {}

    public Boolean getTruthValue() {
        return truthValue;
    }

    public void setTruthValue(Boolean truthValue) {
        this.truthValue = truthValue;
    }

    public String getFormulaType() {
        return formulaType;
    }

    public void setFormulaType(String formulaType) {
        this.formulaType = formulaType;
    }

    public String getLatexTable() {
        return latexTable;
    }

    public void setLatexTable(String latexTable) {
        this.latexTable = latexTable;
    }

    public List<Map<String, Object>> getDetailedSteps() {
        return detailedSteps;
    }

    public void setDetailedSteps(List<Map<String, Object>> detailedSteps) {
        this.detailedSteps = detailedSteps;
    }

    public Map<String, Object> getSyntaxData() {
        return syntaxData;
    }

    public void setSyntaxData(Map<String, Object> syntaxData) {
        this.syntaxData = syntaxData;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}