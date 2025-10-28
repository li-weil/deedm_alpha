package com.deedm.model;

public class EquivCalculusRequest {
    private String content;
    private int stepNumber;

    public EquivCalculusRequest() {}

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