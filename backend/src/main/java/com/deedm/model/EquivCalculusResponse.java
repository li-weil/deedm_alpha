package com.deedm.model;

import java.util.List;

public class EquivCalculusResponse {
    private boolean success;
    private String message;
    private int stepNumber;
    private List<EquivCalculusStepInfo> steps;
    private boolean valid;
    private String errorMessage;
    private String counterExample;
    private String checkingFormula;

    public EquivCalculusResponse() {}

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

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public List<EquivCalculusStepInfo> getSteps() {
        return steps;
    }

    public void setSteps(List<EquivCalculusStepInfo> steps) {
        this.steps = steps;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getCounterExample() {
        return counterExample;
    }

    public void setCounterExample(String counterExample) {
        this.counterExample = counterExample;
    }

    public String getCheckingFormula() {
        return checkingFormula;
    }

    public void setCheckingFormula(String checkingFormula) {
        this.checkingFormula = checkingFormula;
    }
}