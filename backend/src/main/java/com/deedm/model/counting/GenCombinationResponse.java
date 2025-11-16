package com.deedm.model.counting;

import java.util.List;

public class GenCombinationResponse {
    private boolean success;
    private String errorMessage;
    private String type; // 类型标记，用于前端识别

    // 输入参数信息
    private String baseSet;
    private int length;
    private String startString;
    private int number;

    // 结果信息
    private List<String> generatedCombinations; // 生成的组合列表
    private int totalCombinations;              // 总组合数
    private String formula;                     // 数学公式，如 "C(8, 5)"
    private String description;                 // 描述信息

    // LaTeX格式信息
    private String latexBaseSet;               // 基础集的LaTeX表示
    private String latexFormula;               // 公式的LaTeX表示
    private String latexDescription;           // 描述的LaTeX表示

    // 构造函数
    public GenCombinationResponse() {
        this.success = true;
        this.type = "genCombination"; // 设置类型标识
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

    public List<String> getGeneratedCombinations() {
        return generatedCombinations;
    }

    public void setGeneratedCombinations(List<String> generatedCombinations) {
        this.generatedCombinations = generatedCombinations;
    }

    public int getTotalCombinations() {
        return totalCombinations;
    }

    public void setTotalCombinations(int totalCombinations) {
        this.totalCombinations = totalCombinations;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLatexBaseSet() {
        return latexBaseSet;
    }

    public void setLatexBaseSet(String latexBaseSet) {
        this.latexBaseSet = latexBaseSet;
    }

    public String getLatexFormula() {
        return latexFormula;
    }

    public void setLatexFormula(String latexFormula) {
        this.latexFormula = latexFormula;
    }

    public String getLatexDescription() {
        return latexDescription;
    }

    public void setLatexDescription(String latexDescription) {
        this.latexDescription = latexDescription;
    }
}