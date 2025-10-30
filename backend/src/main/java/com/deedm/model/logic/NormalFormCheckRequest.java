package com.deedm.model.logic;

import java.util.List;

public class NormalFormCheckRequest {
    private List<String> formulas;
    private String variableSet;

    public NormalFormCheckRequest() {}

    public NormalFormCheckRequest(List<String> formulas, String variableSet) {
        this.formulas = formulas;
        this.variableSet = variableSet;
    }

    // Getters and Setters
    public List<String> getFormulas() {
        return formulas;
    }

    public void setFormulas(List<String> formulas) {
        this.formulas = formulas;
    }

    public String getVariableSet() {
        return variableSet;
    }

    public void setVariableSet(String variableSet) {
        this.variableSet = variableSet;
    }

    @Override
    public String toString() {
        return "NormalFormCheckRequest{" +
                "formulas=" + formulas +
                ", variableSet='" + variableSet + '\'' +
                '}';
    }
}