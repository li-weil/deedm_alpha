package com.deedm.model.setrelfun;

public class RelationPropertyRequest {
    private String setAString;
    private String relationRString;
    private boolean intTypeElement;
    private boolean checkReflexive;
    private boolean checkIrreflexive;
    private boolean checkSymmetric;
    private boolean checkAntisymmetric;
    private boolean checkTransitive;
    private boolean useMatrix;
    private boolean useGraph;

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

    public boolean isCheckReflexive() {
        return checkReflexive;
    }

    public void setCheckReflexive(boolean checkReflexive) {
        this.checkReflexive = checkReflexive;
    }

    public boolean isCheckIrreflexive() {
        return checkIrreflexive;
    }

    public void setCheckIrreflexive(boolean checkIrreflexive) {
        this.checkIrreflexive = checkIrreflexive;
    }

    public boolean isCheckSymmetric() {
        return checkSymmetric;
    }

    public void setCheckSymmetric(boolean checkSymmetric) {
        this.checkSymmetric = checkSymmetric;
    }

    public boolean isCheckAntisymmetric() {
        return checkAntisymmetric;
    }

    public void setCheckAntisymmetric(boolean checkAntisymmetric) {
        this.checkAntisymmetric = checkAntisymmetric;
    }

    public boolean isCheckTransitive() {
        return checkTransitive;
    }

    public void setCheckTransitive(boolean checkTransitive) {
        this.checkTransitive = checkTransitive;
    }

    public boolean isUseMatrix() {
        return useMatrix;
    }

    public void setUseMatrix(boolean useMatrix) {
        this.useMatrix = useMatrix;
    }

    public boolean isUseGraph() {
        return useGraph;
    }

    public void setUseGraph(boolean useGraph) {
        this.useGraph = useGraph;
    }
}