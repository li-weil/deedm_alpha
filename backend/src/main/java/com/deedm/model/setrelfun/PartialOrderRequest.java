package com.deedm.model.setrelfun;

public class PartialOrderRequest {
    private String setAString;
    private String setSString; // 子集S，可选
    private String relationRString;
    private boolean intTypeElement;
    private boolean useDivideRelation; // 使用整除关系
    private boolean showRelationMatrix; // 显示关系矩阵
    private boolean showRelationGraph; // 显示关系图
    private boolean showHasseDiagram; // 显示哈斯图
    private boolean calculateMinimum; // 计算极小元
    private boolean calculateMaximum; // 计算极大元
    private boolean calculateLeast; // 计算最小元
    private boolean calculateGreatest; // 计算最大元
    private boolean calculateLower; // 计算下界
    private boolean calculateUpper; // 计算上界
    private boolean calculateGLB; // 计算最大下界
    private boolean calculateLUB; // 计算最小上界

    // Getters and Setters
    public String getSetAString() {
        return setAString;
    }

    public void setSetAString(String setAString) {
        this.setAString = setAString;
    }

    public String getSetSString() {
        return setSString;
    }

    public void setSetSString(String setSString) {
        this.setSString = setSString;
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

    public boolean isUseDivideRelation() {
        return useDivideRelation;
    }

    public void setUseDivideRelation(boolean useDivideRelation) {
        this.useDivideRelation = useDivideRelation;
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

    public boolean isShowHasseDiagram() {
        return showHasseDiagram;
    }

    public void setShowHasseDiagram(boolean showHasseDiagram) {
        this.showHasseDiagram = showHasseDiagram;
    }

    public boolean isCalculateMinimum() {
        return calculateMinimum;
    }

    public void setCalculateMinimum(boolean calculateMinimum) {
        this.calculateMinimum = calculateMinimum;
    }

    public boolean isCalculateMaximum() {
        return calculateMaximum;
    }

    public void setCalculateMaximum(boolean calculateMaximum) {
        this.calculateMaximum = calculateMaximum;
    }

    public boolean isCalculateLeast() {
        return calculateLeast;
    }

    public void setCalculateLeast(boolean calculateLeast) {
        this.calculateLeast = calculateLeast;
    }

    public boolean isCalculateGreatest() {
        return calculateGreatest;
    }

    public void setCalculateGreatest(boolean calculateGreatest) {
        this.calculateGreatest = calculateGreatest;
    }

    public boolean isCalculateLower() {
        return calculateLower;
    }

    public void setCalculateLower(boolean calculateLower) {
        this.calculateLower = calculateLower;
    }

    public boolean isCalculateUpper() {
        return calculateUpper;
    }

    public void setCalculateUpper(boolean calculateUpper) {
        this.calculateUpper = calculateUpper;
    }

    public boolean isCalculateGLB() {
        return calculateGLB;
    }

    public void setCalculateGLB(boolean calculateGLB) {
        this.calculateGLB = calculateGLB;
    }

    public boolean isCalculateLUB() {
        return calculateLUB;
    }

    public void setCalculateLUB(boolean calculateLUB) {
        this.calculateLUB = calculateLUB;
    }
}