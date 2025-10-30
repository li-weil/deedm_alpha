package com.deedm.model.logic;

public class EquivCalculusStepInfo {
    private String formula;
    private String comment;

    public EquivCalculusStepInfo() {}

    public EquivCalculusStepInfo(String formula, String comment) {
        this.formula = formula;
        this.comment = comment;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}