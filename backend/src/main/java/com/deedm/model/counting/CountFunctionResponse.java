package com.deedm.model.counting;

import java.util.List;
import java.util.Map;

public class CountFunctionResponse {
    private boolean success;
    private String errorMessage;
    private String setALaTeX;
    private String setBLaTeX;
    private int totalCount;
    private int injectionCount;
    private int surjectionCount;
    private int bijectionCount;
    private List<Map<String, Object>> functionList;
    private String type;

    public CountFunctionResponse() {
        this.type = "countFunction";
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

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getInjectionCount() {
        return injectionCount;
    }

    public void setInjectionCount(int injectionCount) {
        this.injectionCount = injectionCount;
    }

    public int getSurjectionCount() {
        return surjectionCount;
    }

    public void setSurjectionCount(int surjectionCount) {
        this.surjectionCount = surjectionCount;
    }

    public int getBijectionCount() {
        return bijectionCount;
    }

    public void setBijectionCount(int bijectionCount) {
        this.bijectionCount = bijectionCount;
    }

    public List<Map<String, Object>> getFunctionList() {
        return functionList;
    }

    public void setFunctionList(List<Map<String, Object>> functionList) {
        this.functionList = functionList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}