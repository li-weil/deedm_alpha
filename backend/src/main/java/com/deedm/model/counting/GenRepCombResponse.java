package com.deedm.model.counting;

import java.util.List;

public class GenRepCombResponse {
    private boolean success;
    private String errorMessage;
    private String type; // 用于前端判断是哪种子界面

    // 基本信息
    private String formula;
    private String baseSetLaTeX;
    private int baseSetSize;
    private int length;
    private String startString;
    private int number;

    // 生成结果
    private List<String> generatedCombinations;
    private String message;

    // 统计信息
    private int totalCombinations;
    private String combinationFormula;
    private String combinationCountLaTeX;

    // 构造函数
    public GenRepCombResponse() {
        this.success = true;
        this.type = "gen-rep-comb";
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

    public String getBaseSetLaTeX() {
        return baseSetLaTeX;
    }

    public void setBaseSetLaTeX(String baseSetLaTeX) {
        this.baseSetLaTeX = baseSetLaTeX;
    }

    public int getBaseSetSize() {
        return baseSetSize;
    }

    public void setBaseSetSize(int baseSetSize) {
        this.baseSetSize = baseSetSize;
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

    public List<String> getGeneratedCombinations() {
        return generatedCombinations;
    }

    public void setGeneratedCombinations(List<String> generatedCombinations) {
        this.generatedCombinations = generatedCombinations;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotalCombinations() {
        return totalCombinations;
    }

    public void setTotalCombinations(int totalCombinations) {
        this.totalCombinations = totalCombinations;
    }

    public String getCombinationFormula() {
        return combinationFormula;
    }

    public void setCombinationFormula(String combinationFormula) {
        this.combinationFormula = combinationFormula;
    }

    public String getCombinationCountLaTeX() {
        return combinationCountLaTeX;
    }

    public void setCombinationCountLaTeX(String combinationCountLaTeX) {
        this.combinationCountLaTeX = combinationCountLaTeX;
    }
}