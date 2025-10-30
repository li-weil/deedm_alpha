package com.deedm.model.logic;

public class TruthValueRequest {
    private String formula;
    private String assignment;
    private String variables;
    private boolean showDetailedProcess;
    private boolean checkType;
    private boolean showStrictForm;

    public TruthValueRequest() {}

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getAssignment() {
        return assignment;
    }

    public void setAssignment(String assignment) {
        this.assignment = assignment;
    }

    public String getVariables() {
        return variables;
    }

    public void setVariables(String variables) {
        this.variables = variables;
    }

    public boolean isShowDetailedProcess() {
        return showDetailedProcess;
    }

    public void setShowDetailedProcess(boolean showDetailedProcess) {
        this.showDetailedProcess = showDetailedProcess;
    }

    public boolean isCheckType() {
        return checkType;
    }

    public void setCheckType(boolean checkType) {
        this.checkType = checkType;
    }

    public boolean isShowStrictForm() {
        return showStrictForm;
    }

    public void setShowStrictForm(boolean showStrictForm) {
        this.showStrictForm = showStrictForm;
    }
}