package com.deedm.model;

import java.util.List;

public class NormalFormulaExpansionResponse {
    private boolean success;
    private String message;
    private String originalFormula;
    private String variableSet;
    private String targetType;
    private List<ExpansionStep> expansionSteps;
    private String pdnfResult;
    private String pcnfResult;

    public NormalFormulaExpansionResponse() {}

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOriginalFormula() {
        return originalFormula;
    }

    public void setOriginalFormula(String originalFormula) {
        this.originalFormula = originalFormula;
    }

    public String getVariableSet() {
        return variableSet;
    }

    public void setVariableSet(String variableSet) {
        this.variableSet = variableSet;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public List<ExpansionStep> getExpansionSteps() {
        return expansionSteps;
    }

    public void setExpansionSteps(List<ExpansionStep> expansionSteps) {
        this.expansionSteps = expansionSteps;
    }

    public String getPdnfResult() {
        return pdnfResult;
    }

    public void setPdnfResult(String pdnfResult) {
        this.pdnfResult = pdnfResult;
    }

    public String getPcnfResult() {
        return pcnfResult;
    }

    public void setPcnfResult(String pcnfResult) {
        this.pcnfResult = pcnfResult;
    }
}