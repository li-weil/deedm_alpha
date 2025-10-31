package com.deedm.model.setrelfun;

public class SetOperationRequest {
    private String setU;
    private String setA;
    private String setB;
    private boolean intTypeElement;
    private boolean calculateIntersection;
    private boolean calculateUnion;
    private boolean calculateSubtract;
    private boolean calculateComplement;
    private boolean calculateDifference;
    private boolean calculatePower;

    // Getters and Setters
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

    public boolean isIntTypeElement() {
        return intTypeElement;
    }

    public void setIntTypeElement(boolean intTypeElement) {
        this.intTypeElement = intTypeElement;
    }

    public boolean isCalculateIntersection() {
        return calculateIntersection;
    }

    public void setCalculateIntersection(boolean calculateIntersection) {
        this.calculateIntersection = calculateIntersection;
    }

    public boolean isCalculateUnion() {
        return calculateUnion;
    }

    public void setCalculateUnion(boolean calculateUnion) {
        this.calculateUnion = calculateUnion;
    }

    public boolean isCalculateSubtract() {
        return calculateSubtract;
    }

    public void setCalculateSubtract(boolean calculateSubtract) {
        this.calculateSubtract = calculateSubtract;
    }

    public boolean isCalculateComplement() {
        return calculateComplement;
    }

    public void setCalculateComplement(boolean calculateComplement) {
        this.calculateComplement = calculateComplement;
    }

    public boolean isCalculateDifference() {
        return calculateDifference;
    }

    public void setCalculateDifference(boolean calculateDifference) {
        this.calculateDifference = calculateDifference;
    }

    public boolean isCalculatePower() {
        return calculatePower;
    }

    public void setCalculatePower(boolean calculatePower) {
        this.calculatePower = calculatePower;
    }
}