package com.deedm.model.algebra;

public class OperationPropertyRequest {
    private String setAString;
    private String operator1String;
    private String operator2String;
    private boolean intTypeElement;
    private boolean checkCommutative;
    private boolean checkAssociative;
    private boolean checkIdempotent;
    private boolean checkCancellation;
    private boolean checkDistributive;
    private boolean checkAbsorption;
    private boolean checkIdentity;
    private boolean checkZero;
    private boolean checkInverse;

    // Getters and Setters
    public String getSetAString() {
        return setAString;
    }

    public void setSetAString(String setAString) {
        this.setAString = setAString;
    }

    public String getOperator1String() {
        return operator1String;
    }

    public void setOperator1String(String operator1String) {
        this.operator1String = operator1String;
    }

    public String getOperator2String() {
        return operator2String;
    }

    public void setOperator2String(String operator2String) {
        this.operator2String = operator2String;
    }

    public boolean isIntTypeElement() {
        return intTypeElement;
    }

    public void setIntTypeElement(boolean intTypeElement) {
        this.intTypeElement = intTypeElement;
    }

    public boolean isCheckCommutative() {
        return checkCommutative;
    }

    public void setCheckCommutative(boolean checkCommutative) {
        this.checkCommutative = checkCommutative;
    }

    public boolean isCheckAssociative() {
        return checkAssociative;
    }

    public void setCheckAssociative(boolean checkAssociative) {
        this.checkAssociative = checkAssociative;
    }

    public boolean isCheckIdempotent() {
        return checkIdempotent;
    }

    public void setCheckIdempotent(boolean checkIdempotent) {
        this.checkIdempotent = checkIdempotent;
    }

    public boolean isCheckCancellation() {
        return checkCancellation;
    }

    public void setCheckCancellation(boolean checkCancellation) {
        this.checkCancellation = checkCancellation;
    }

    public boolean isCheckDistributive() {
        return checkDistributive;
    }

    public void setCheckDistributive(boolean checkDistributive) {
        this.checkDistributive = checkDistributive;
    }

    public boolean isCheckAbsorption() {
        return checkAbsorption;
    }

    public void setCheckAbsorption(boolean checkAbsorption) {
        this.checkAbsorption = checkAbsorption;
    }

    public boolean isCheckIdentity() {
        return checkIdentity;
    }

    public void setCheckIdentity(boolean checkIdentity) {
        this.checkIdentity = checkIdentity;
    }

    public boolean isCheckZero() {
        return checkZero;
    }

    public void setCheckZero(boolean checkZero) {
        this.checkZero = checkZero;
    }

    public boolean isCheckInverse() {
        return checkInverse;
    }

    public void setCheckInverse(boolean checkInverse) {
        this.checkInverse = checkInverse;
    }
}