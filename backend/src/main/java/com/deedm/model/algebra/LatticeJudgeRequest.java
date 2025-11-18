package com.deedm.model.algebra;

public class LatticeJudgeRequest {
    private String setAString;
    private String relationRString;
    private boolean intTypeElement;
    private boolean useHasseDiagram;
    private boolean checkDistributive;
    private boolean checkBounded;
    private boolean checkComplemented;
    private boolean checkBoolean;

    // Getters and Setters
    public String getSetAString() {
        return setAString;
    }

    public void setSetAString(String setAString) {
        this.setAString = setAString;
    }

    public String getRelationRString() {
        return relationRString;
    }

    public void setRelationRString(String relationRString) {
        this.relationRString = relationRString;
    }

    public boolean isIntTypeElement() {
        return intTypeElement;
    }

    public void setIntTypeElement(boolean intTypeElement) {
        this.intTypeElement = intTypeElement;
    }

    public boolean isUseHasseDiagram() {
        return useHasseDiagram;
    }

    public void setUseHasseDiagram(boolean useHasseDiagram) {
        this.useHasseDiagram = useHasseDiagram;
    }

    public boolean isCheckDistributive() {
        return checkDistributive;
    }

    public void setCheckDistributive(boolean checkDistributive) {
        this.checkDistributive = checkDistributive;
    }

    public boolean isCheckBounded() {
        return checkBounded;
    }

    public void setCheckBounded(boolean checkBounded) {
        this.checkBounded = checkBounded;
    }

    public boolean isCheckComplemented() {
        return checkComplemented;
    }

    public void setCheckComplemented(boolean checkComplemented) {
        this.checkComplemented = checkComplemented;
    }

    public boolean isCheckBoolean() {
        return checkBoolean;
    }

    public void setCheckBoolean(boolean checkBoolean) {
        this.checkBoolean = checkBoolean;
    }
}