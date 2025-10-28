package com.deedm.model;

public class ReasonArgumentCheckRequest {
    private String premises;
    private String consequent;
    private String content;
    private int stepNumber;

    public ReasonArgumentCheckRequest() {}

    public String getPremises() {
        return premises;
    }

    public void setPremises(String premises) {
        this.premises = premises;
    }

    public String getConsequent() {
        return consequent;
    }

    public void setConsequent(String consequent) {
        this.consequent = consequent;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }
}