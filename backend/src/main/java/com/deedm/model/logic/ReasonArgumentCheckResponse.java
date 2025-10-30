package com.deedm.model.logic;

import java.util.List;

public class ReasonArgumentCheckResponse {
    private boolean success;
    private String message;
    private boolean valid;
    private String errorMessage;
    private String checkingFormula;
    private String counterExample;
    private List<ReasoningStep> steps;
    private List<CheckingStep> checkSteps;
    private int errorType;
    private String latexString;

    public ReasonArgumentCheckResponse() {}

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

    public String getCheckingFormula() {
        return checkingFormula;
    }

    public void setCheckingFormula(String checkingFormula) {
        this.checkingFormula = checkingFormula;
    }

    public String getCounterExample() {
        return counterExample;
    }

    public void setCounterExample(String counterExample) {
        this.counterExample = counterExample;
    }

    public List<ReasoningStep> getSteps() {
        return steps;
    }

    public void setSteps(List<ReasoningStep> steps) {
        this.steps = steps;
    }

    public List<CheckingStep> getCheckSteps() {
        return checkSteps;
    }

    public void setCheckSteps(List<CheckingStep> checkSteps) {
        this.checkSteps = checkSteps;
    }

    public int getErrorType() {
        return errorType;
    }

    public void setErrorType(int errorType) {
        this.errorType = errorType;
    }

    public String getLatexString() {
        return latexString;
    }

    public void setLatexString(String latexString) {
        this.latexString = latexString;
    }

    // Inner class for reasoning steps
    public static class ReasoningStep {
        private int serialNo;
        private String formula;
        private String ruleName;
        private String prevSN;

        public ReasoningStep() {}

        public int getSerialNo() {
            return serialNo;
        }

        public void setSerialNo(int serialNo) {
            this.serialNo = serialNo;
        }

        public String getFormula() {
            return formula;
        }

        public void setFormula(String formula) {
            this.formula = formula;
        }

        public String getRuleName() {
            return ruleName;
        }

        public void setRuleName(String ruleName) {
            this.ruleName = ruleName;
        }

        public String getPrevSN() {
            return prevSN;
        }

        public void setPrevSN(String prevSN) {
            this.prevSN = prevSN;
        }
    }

    // Inner class for checking steps
    public static class CheckingStep {
        private int serialNo;
        private String formula;
        private String checkType;

        public CheckingStep() {}

        public int getSerialNo() {
            return serialNo;
        }

        public void setSerialNo(int serialNo) {
            this.serialNo = serialNo;
        }

        public String getFormula() {
            return formula;
        }

        public void setFormula(String formula) {
            this.formula = formula;
        }

        public String getCheckType() {
            return checkType;
        }

        public void setCheckType(String checkType) {
            this.checkType = checkType;
        }
    }
}