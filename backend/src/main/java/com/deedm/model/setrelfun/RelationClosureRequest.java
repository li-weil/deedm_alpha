package com.deedm.model.setrelfun;

public class RelationClosureRequest {
    private String setAString;
    private String relationRString;
    private boolean intTypeElement;
    private boolean calculateReflexiveClosure;
    private boolean calculateSymmetricClosure;
    private boolean calculateTransitiveClosure;
    private boolean useWarshallAlgorithm;
    private boolean showTransitiveDetails;
    private boolean calculateEquivalenceClosure;
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

    public boolean isCalculateReflexiveClosure() {
        return calculateReflexiveClosure;
    }

    public void setCalculateReflexiveClosure(boolean calculateReflexiveClosure) {
        this.calculateReflexiveClosure = calculateReflexiveClosure;
    }

    public boolean isCalculateSymmetricClosure() {
        return calculateSymmetricClosure;
    }

    public void setCalculateSymmetricClosure(boolean calculateSymmetricClosure) {
        this.calculateSymmetricClosure = calculateSymmetricClosure;
    }

    public boolean isCalculateTransitiveClosure() {
        return calculateTransitiveClosure;
    }

    public void setCalculateTransitiveClosure(boolean calculateTransitiveClosure) {
        this.calculateTransitiveClosure = calculateTransitiveClosure;
    }

    public boolean isUseWarshallAlgorithm() {
        return useWarshallAlgorithm;
    }

    public void setUseWarshallAlgorithm(boolean useWarshallAlgorithm) {
        this.useWarshallAlgorithm = useWarshallAlgorithm;
    }

    public boolean isShowTransitiveDetails() {
        return showTransitiveDetails;
    }

    public void setShowTransitiveDetails(boolean showTransitiveDetails) {
        this.showTransitiveDetails = showTransitiveDetails;
    }

    public boolean isCalculateEquivalenceClosure() {
        return calculateEquivalenceClosure;
    }

    public void setCalculateEquivalenceClosure(boolean calculateEquivalenceClosure) {
        this.calculateEquivalenceClosure = calculateEquivalenceClosure;
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