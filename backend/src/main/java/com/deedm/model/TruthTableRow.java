package com.deedm.model;

import java.util.List;

public class TruthTableRow {
    private List<String> variableValues;
    private String resultValue;

    public TruthTableRow() {}

    public TruthTableRow(List<String> variableValues, String resultValue) {
        this.variableValues = variableValues;
        this.resultValue = resultValue;
    }

    public List<String> getVariableValues() {
        return variableValues;
    }

    public void setVariableValues(List<String> variableValues) {
        this.variableValues = variableValues;
    }

    public String getResultValue() {
        return resultValue;
    }

    public void setResultValue(String resultValue) {
        this.resultValue = resultValue;
    }
}