package com.deedm.model.setrelfun;

import java.util.List;

public class FunctionPropertyResponse {
    private boolean success;
    private String errorMessage;

    // 基本信息和类型标识
    private String type; // 用于标识这是函数性质判断的结果
    private String formula; // LaTeX格式的集合和函数描述
    private int index; // 运算序号

    // 集合和函数信息
    private String setALaTeX;
    private String setBLaTeX;
    private String functionLaTeX;

    // 函数性质判断结果
    private boolean isFunction; // 是否为函数
    private FunctionProperty injectionResult;
    private FunctionProperty surjectionResult;
    private FunctionProperty bijectionResult;

    // 矩阵和图形表示
    private String relationMatrix; // 关系矩阵
    private String relationGraphUrl; // 关系图URL

    // 构造函数
    public FunctionPropertyResponse() {
        this.success = true;
        this.type = "function-property";
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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getSetALaTeX() {
        return setALaTeX;
    }

    public void setSetALaTeX(String setALaTeX) {
        this.setALaTeX = setALaTeX;
    }

    public String getSetBLaTeX() {
        return setBLaTeX;
    }

    public void setSetBLaTeX(String setBLaTeX) {
        this.setBLaTeX = setBLaTeX;
    }

    public String getFunctionLaTeX() {
        return functionLaTeX;
    }

    public void setFunctionLaTeX(String functionLaTeX) {
        this.functionLaTeX = functionLaTeX;
    }

    public boolean isFunction() {
        return isFunction;
    }

    public void setFunction(boolean isFunction) {
        this.isFunction = isFunction;
    }

    public FunctionProperty getInjectionResult() {
        return injectionResult;
    }

    public void setInjectionResult(FunctionProperty injectionResult) {
        this.injectionResult = injectionResult;
    }

    public FunctionProperty getSurjectionResult() {
        return surjectionResult;
    }

    public void setSurjectionResult(FunctionProperty surjectionResult) {
        this.surjectionResult = surjectionResult;
    }

    public FunctionProperty getBijectionResult() {
        return bijectionResult;
    }

    public void setBijectionResult(FunctionProperty bijectionResult) {
        this.bijectionResult = bijectionResult;
    }

    public String getRelationMatrix() {
        return relationMatrix;
    }

    public void setRelationMatrix(String relationMatrix) {
        this.relationMatrix = relationMatrix;
    }

    public String getRelationGraphUrl() {
        return relationGraphUrl;
    }

    public void setRelationGraphUrl(String relationGraphUrl) {
        this.relationGraphUrl = relationGraphUrl;
    }

    // 内部类：函数性质结果
    public static class FunctionProperty {
        private boolean isProperty; // 是否具有该性质
        private String counterExample; // 反例，如果有的话
        private String description; // 性质描述

        public FunctionProperty(boolean isProperty) {
            this.isProperty = isProperty;
        }

        // Getters and Setters
        public boolean isProperty() {
            return isProperty;
        }

        public void setProperty(boolean property) {
            isProperty = property;
        }

        public String getCounterExample() {
            return counterExample;
        }

        public void setCounterExample(String counterExample) {
            this.counterExample = counterExample;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}