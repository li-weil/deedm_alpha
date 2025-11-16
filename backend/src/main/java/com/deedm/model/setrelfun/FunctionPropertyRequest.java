package com.deedm.model.setrelfun;

public class FunctionPropertyRequest {
    private String setAString;
    private String setBString;
    private String functionString;
    private boolean intTypeElement;
    private boolean checkInjection;
    private boolean checkSurjection;
    private boolean checkBijection;
    private boolean showRelationMatrix;
    private boolean showRelationGraph;

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

    public String getFunctionString() {
        return functionString;
    }

    public void setFunctionString(String functionString) {
        this.functionString = functionString;
    }

    public boolean isIntTypeElement() {
        return intTypeElement;
    }

    public void setIntTypeElement(boolean intTypeElement) {
        this.intTypeElement = intTypeElement;
    }

    public boolean isCheckInjection() {
        return checkInjection;
    }

    public void setCheckInjection(boolean checkInjection) {
        this.checkInjection = checkInjection;
    }

    public boolean isCheckSurjection() {
        return checkSurjection;
    }

    public void setCheckSurjection(boolean checkSurjection) {
        this.checkSurjection = checkSurjection;
    }

    public boolean isCheckBijection() {
        return checkBijection;
    }

    public void setCheckBijection(boolean checkBijection) {
        this.checkBijection = checkBijection;
    }

    public boolean isShowRelationMatrix() {
        return showRelationMatrix;
    }

    public void setShowRelationMatrix(boolean showRelationMatrix) {
        this.showRelationMatrix = showRelationMatrix;
    }

    public boolean isShowRelationGraph() {
        return showRelationGraph;
    }

    public void setShowRelationGraph(boolean showRelationGraph) {
        this.showRelationGraph = showRelationGraph;
    }
}