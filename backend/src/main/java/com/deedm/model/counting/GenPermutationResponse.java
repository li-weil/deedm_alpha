package com.deedm.model.counting;

import java.util.List;

public class GenPermutationResponse {
    private boolean success;
    private String errorMessage;
    private String type;
    private String formula;

    // 输入参数
    private String baseSet;
    private int length;
    private String startString;
    private int number;

    // 结果数据
    private List<String> permutations;
    private int totalCount;
    private String message;
    private boolean startStringFound;

    // 构造函数
    public GenPermutationResponse() {
        this.success = true;
        this.type = "gen-permutation";
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

    public String getBaseSet() {
        return baseSet;
    }

    public void setBaseSet(String baseSet) {
        this.baseSet = baseSet;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getStartString() {
        return startString;
    }

    public void setStartString(String startString) {
        this.startString = startString;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<String> getPermutations() {
        return permutations;
    }

    public void setPermutations(List<String> permutations) {
        this.permutations = permutations;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isStartStringFound() {
        return startStringFound;
    }

    public void setStartStringFound(boolean startStringFound) {
        this.startStringFound = startStringFound;
    }
}