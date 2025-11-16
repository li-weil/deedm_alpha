package com.deedm.model.counting;

public class CountFunctionRequest {
    private String setAString;
    private String setBString;
    private String maxDisplayString;
    private boolean intType;
    private boolean countInjection;
    private boolean countSurjection;
    private boolean countBijection;
    private boolean detailed;

    // Getters and Setters
    public String getSetAString() {
        return setAString;
    }

    public void setSetAString(String setAString) {
        this.setAString = setAString;
    }

    public String getSetBString() {
        return setBString;
    }

    public void setSetBString(String setBString) {
        this.setBString = setBString;
    }

    public String getMaxDisplayString() {
        return maxDisplayString;
    }

    public void setMaxDisplayString(String maxDisplayString) {
        this.maxDisplayString = maxDisplayString;
    }

    public boolean isIntType() {
        return intType;
    }

    public void setIntType(boolean intType) {
        this.intType = intType;
    }

    public boolean isCountInjection() {
        return countInjection;
    }

    public void setCountInjection(boolean countInjection) {
        this.countInjection = countInjection;
    }

    public boolean isCountSurjection() {
        return countSurjection;
    }

    public void setCountSurjection(boolean countSurjection) {
        this.countSurjection = countSurjection;
    }

    public boolean isCountBijection() {
        return countBijection;
    }

    public void setCountBijection(boolean countBijection) {
        this.countBijection = countBijection;
    }

    public boolean isDetailed() {
        return detailed;
    }

    public void setDetailed(boolean detailed) {
        this.detailed = detailed;
    }
}