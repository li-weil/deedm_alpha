package com.deedm.model.setrelfun;

public class SetExprOperationRequest {
    private String setU;
    private String setA;
    private String setB;
    private String expression;
    private boolean intTypeElement;

    public SetExprOperationRequest() {}

    public SetExprOperationRequest(String setU, String setA, String setB, String expression, boolean intTypeElement) {
        this.setU = setU;
        this.setA = setA;
        this.setB = setB;
        this.expression = expression;
        this.intTypeElement = intTypeElement;
    }

    public String getSetU() {
        return setU;
    }

    public void setSetU(String setU) {
        this.setU = setU;
    }

    public String getSetA() {
        return setA;
    }

    public void setSetA(String setA) {
        this.setA = setA;
    }

    public String getSetB() {
        return setB;
    }

    public void setSetB(String setB) {
        this.setB = setB;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public boolean isIntTypeElement() {
        return intTypeElement;
    }

    public void setIntTypeElement(boolean intTypeElement) {
        this.intTypeElement = intTypeElement;
    }

    @Override
    public String toString() {
        return "SetExprOperationRequest{" +
                "setU='" + setU + '\'' +
                ", setA='" + setA + '\'' +
                ", setB='" + setB + '\'' +
                ", expression='" + expression + '\'' +
                ", intTypeElement=" + intTypeElement +
                '}';
    }
}