package com.deedm.model.setrelfun;

public class EquivalenceRequest {
    private String setAString;
    private String relationRString;
    private boolean intTypeElement;
    private boolean calculateEquivClosure;
    private boolean calculateEquivalenceClasses;
    private boolean calculateQuotientSet;
    private boolean showRelationMatrix;
    private boolean showRelationGraph;

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

    public boolean isCalculateEquivClosure() {
        return calculateEquivClosure;
    }

    public void setCalculateEquivClosure(boolean calculateEquivClosure) {
        this.calculateEquivClosure = calculateEquivClosure;
    }

    public boolean isCalculateEquivalenceClasses() {
        return calculateEquivalenceClasses;
    }

    public void setCalculateEquivalenceClasses(boolean calculateEquivalenceClasses) {
        this.calculateEquivalenceClasses = calculateEquivalenceClasses;
    }

    public boolean isCalculateQuotientSet() {
        return calculateQuotientSet;
    }

    public void setCalculateQuotientSet(boolean calculateQuotientSet) {
        this.calculateQuotientSet = calculateQuotientSet;
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