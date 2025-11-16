package com.deedm.model.counting;

import java.math.BigInteger;

public class CombCalculatorResponse {
    private boolean success;
    private String errorMessage;
    private String type;
    private String formula;

    // 计算结果
    private String powerResult;
    private String combinationResult;
    private String permutationResult;
    private String factorialNResult;
    private String factorialMResult;

    // 构造函数
    public CombCalculatorResponse() {
        this.success = true;
        this.type = "comb-calculator";
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        this.success = false;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getPowerResult() {
        return powerResult;
    }

    public void setPowerResult(String powerResult) {
        this.powerResult = powerResult;
    }

    public String getCombinationResult() {
        return combinationResult;
    }

    public void setCombinationResult(String combinationResult) {
        this.combinationResult = combinationResult;
    }

    public String getPermutationResult() {
        return permutationResult;
    }

    public void setPermutationResult(String permutationResult) {
        this.permutationResult = permutationResult;
    }

    public String getFactorialNResult() {
        return factorialNResult;
    }

    public void setFactorialNResult(String factorialNResult) {
        this.factorialNResult = factorialNResult;
    }

    public String getFactorialMResult() {
        return factorialMResult;
    }

    public void setFactorialMResult(String factorialMResult) {
        this.factorialMResult = factorialMResult;
    }
}