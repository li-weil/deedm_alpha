package com.deedm.model;

import java.util.List;

public class NormalFormulaExpansionRequest {
    private List<String> formulas;
    private String variableSet;
    private boolean isDNF; // true for DNF, false for CNF

    public NormalFormulaExpansionRequest() {}

    public NormalFormulaExpansionRequest(List<String> formulas, String variableSet, boolean isDNF) {
        this.formulas = formulas;
        this.variableSet = variableSet;
        this.isDNF = isDNF;
    }

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

    public boolean isDNF() {
        return isDNF;
    }

    public void setDNF(boolean DNF) {
        isDNF = DNF;
    }
}