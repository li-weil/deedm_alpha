package com.deedm.model;

import java.util.List;

public class TruthTableRequest {
    private List<String> formulas;
    private boolean detailed;
    private boolean checkType;

    // Constructors
    public TruthTableRequest() {}

    public TruthTableRequest(List<String> formulas, boolean detailed, boolean checkType) {
        this.formulas = formulas;
        this.detailed = detailed;
        this.checkType = checkType;
    }

    // Getters and Setters
    public List<String> getFormulas() {
        return formulas;
    }

    public void setFormulas(List<String> formulas) {
        this.formulas = formulas;
    }

    public boolean isDetailed() {
        return detailed;
    }

    public void setDetailed(boolean detailed) {
        this.detailed = detailed;
    }

    public boolean isCheckType() {
        return checkType;
    }

    public void setCheckType(boolean checkType) {
        this.checkType = checkType;
    }
}