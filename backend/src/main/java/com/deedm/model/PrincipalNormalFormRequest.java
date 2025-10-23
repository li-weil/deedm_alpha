package com.deedm.model;

import java.util.List;

public class PrincipalNormalFormRequest {
    private List<String> formulas;
    private String variableSet;
    private String logicalRelation; // "or" or "and"
    private List<String> normalFormTypes; // "cnf", "dnf", "pnf"
    private List<String> calculationMethods; // "equiv", "table", "detailed"

    public PrincipalNormalFormRequest() {}

    public PrincipalNormalFormRequest(List<String> formulas, String variableSet, String logicalRelation,
                                    List<String> normalFormTypes, List<String> calculationMethods) {
        this.formulas = formulas;
        this.variableSet = variableSet;
        this.logicalRelation = logicalRelation;
        this.normalFormTypes = normalFormTypes;
        this.calculationMethods = calculationMethods;
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

    public String getLogicalRelation() {
        return logicalRelation;
    }

    public void setLogicalRelation(String logicalRelation) {
        this.logicalRelation = logicalRelation;
    }

    public List<String> getNormalFormTypes() {
        return normalFormTypes;
    }

    public void setNormalFormTypes(List<String> normalFormTypes) {
        this.normalFormTypes = normalFormTypes;
    }

    public List<String> getCalculationMethods() {
        return calculationMethods;
    }

    public void setCalculationMethods(List<String> calculationMethods) {
        this.calculationMethods = calculationMethods;
    }

    @Override
    public String toString() {
        return "PrincipalNormalFormRequest{" +
                "formulas=" + formulas +
                ", variableSet='" + variableSet + '\'' +
                ", logicalRelation='" + logicalRelation + '\'' +
                ", normalFormTypes=" + normalFormTypes +
                ", calculationMethods=" + calculationMethods +
                '}';
    }
}