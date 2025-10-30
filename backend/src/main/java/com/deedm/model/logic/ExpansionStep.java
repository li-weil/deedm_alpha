package com.deedm.model.logic;

public class ExpansionStep {
    private String formula;
    private String binaryCode;
    private String resultCodes;
    private String description;

    public ExpansionStep() {}

    public ExpansionStep(String formula, String binaryCode, String resultCodes, String description) {
        this.formula = formula;
        this.binaryCode = binaryCode;
        this.resultCodes = resultCodes;
        this.description = description;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getBinaryCode() {
        return binaryCode;
    }

    public void setBinaryCode(String binaryCode) {
        this.binaryCode = binaryCode;
    }

    public String getResultCodes() {
        return resultCodes;
    }

    public void setResultCodes(String resultCodes) {
        this.resultCodes = resultCodes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}