package com.deedm.model;

public class NormalFormGenerateRequest {
    private String variableSet;
    private String logicalRelation; // "or" or "and"
    private int count = 4; // Number of formulas to generate

    public NormalFormGenerateRequest() {}

    public NormalFormGenerateRequest(String variableSet, String logicalRelation) {
        this.variableSet = variableSet;
        this.logicalRelation = logicalRelation;
    }

    // Getters and Setters
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "NormalFormGenerateRequest{" +
                "variableSet='" + variableSet + '\'' +
                ", logicalRelation='" + logicalRelation + '\'' +
                ", count=" + count +
                '}';
    }
}