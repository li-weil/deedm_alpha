package com.deedm.model.setrelfun;


public class PartialOrderResponse {
    private boolean success;
    private String errorMessage;

    // 基本信息和类型标记
    private String type;
    private String formula;
    private String setAString;
    private String setSString;
    private String relationRString;
    private boolean intTypeElement;
    private boolean useDivideRelation;

    // 关系性质
    private boolean isReflexive;
    private boolean isAntisymmetric;
    private boolean isTransitive;
    private boolean isPartialOrder;

    // 矩阵和图
    private String relationMatrix;
    private String relationGraphUrl;
    private String hasseDiagramUrl;

    // 极值计算结果
    private String minimumElements;
    private String maximumElements;
    private String leastElement;
    private String greatestElement;

    // 界计算结果（针对子集S）
    private String lowerBounds;
    private String upperBounds;
    private String greatestLowerBound;
    private String leastUpperBound;

    // 构造函数
    public PartialOrderResponse() {
        this.success = true;
        this.type = "partial-order";
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        this.success = false;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

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

    public boolean isReflexive() {
        return isReflexive;
    }

    public void setReflexive(boolean reflexive) {
        isReflexive = reflexive;
    }

    public boolean isAntisymmetric() {
        return isAntisymmetric;
    }

    public void setAntisymmetric(boolean antisymmetric) {
        isAntisymmetric = antisymmetric;
    }

    public boolean isTransitive() {
        return isTransitive;
    }

    public void setTransitive(boolean transitive) {
        isTransitive = transitive;
    }

    public boolean isPartialOrder() {
        return isPartialOrder;
    }

    public void setPartialOrder(boolean partialOrder) {
        isPartialOrder = partialOrder;
    }

    public String getRelationMatrix() {
        return relationMatrix;
    }

    public void setRelationMatrix(String relationMatrix) {
        this.relationMatrix = relationMatrix;
    }

    public String getRelationGraphUrl() {
        return relationGraphUrl;
    }

    public void setRelationGraphUrl(String relationGraphUrl) {
        this.relationGraphUrl = relationGraphUrl;
    }

    public String getHasseDiagramUrl() {
        return hasseDiagramUrl;
    }

    public void setHasseDiagramUrl(String hasseDiagramUrl) {
        this.hasseDiagramUrl = hasseDiagramUrl;
    }

    public String getMinimumElements() {
        return minimumElements;
    }

    public void setMinimumElements(String minimumElements) {
        this.minimumElements = minimumElements;
    }

    public String getMaximumElements() {
        return maximumElements;
    }

    public void setMaximumElements(String maximumElements) {
        this.maximumElements = maximumElements;
    }

    public String getLeastElement() {
        return leastElement;
    }

    public void setLeastElement(String leastElement) {
        this.leastElement = leastElement;
    }

    public String getGreatestElement() {
        return greatestElement;
    }

    public void setGreatestElement(String greatestElement) {
        this.greatestElement = greatestElement;
    }

    public String getLowerBounds() {
        return lowerBounds;
    }

    public void setLowerBounds(String lowerBounds) {
        this.lowerBounds = lowerBounds;
    }

    public String getUpperBounds() {
        return upperBounds;
    }

    public void setUpperBounds(String upperBounds) {
        this.upperBounds = upperBounds;
    }

    public String getGreatestLowerBound() {
        return greatestLowerBound;
    }

    public void setGreatestLowerBound(String greatestLowerBound) {
        this.greatestLowerBound = greatestLowerBound;
    }

    public String getLeastUpperBound() {
        return leastUpperBound;
    }

    public void setLeastUpperBound(String leastUpperBound) {
        this.leastUpperBound = leastUpperBound;
    }
}