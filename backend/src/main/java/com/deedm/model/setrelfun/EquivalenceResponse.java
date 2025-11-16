package com.deedm.model.setrelfun;

import java.util.List;

public class EquivalenceResponse {
    private boolean success;
    private String errorMessage;

    // 基本信息
    private String formula;
    private String type; // 添加类型标记
    private String setAString;
    private String relationRString;
    private boolean intTypeElement;

    // 关系性质分析结果
    private boolean isReflexive;
    private boolean isSymmetric;
    private boolean isTransitive;
    private boolean isEquivalenceRelation;
    private String reflexiveResult;
    private String symmetricResult;
    private String transitiveResult;

    // 等价关系闭包
    private String equivalenceClosure;
    private String equivalenceClosureMatrix;
    private String equivalenceClosureGraphUrl;

    // 关系矩阵和关系图
    private String relationMatrix;
    private String relationGraphUrl;

    // 等价类
    private List<EquivalenceClass> equivalenceClasses;

    // 商集
    private String quotientSet;

    // 构造函数
    public EquivalenceResponse() {
        this.success = true;
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

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    public boolean isReflexive() {
        return isReflexive;
    }

    public void setReflexive(boolean reflexive) {
        isReflexive = reflexive;
    }

    public boolean isSymmetric() {
        return isSymmetric;
    }

    public void setSymmetric(boolean symmetric) {
        isSymmetric = symmetric;
    }

    public boolean isTransitive() {
        return isTransitive;
    }

    public void setTransitive(boolean transitive) {
        isTransitive = transitive;
    }

    public boolean isEquivalenceRelation() {
        return isEquivalenceRelation;
    }

    public void setEquivalenceRelation(boolean equivalenceRelation) {
        isEquivalenceRelation = equivalenceRelation;
    }

    public String getReflexiveResult() {
        return reflexiveResult;
    }

    public void setReflexiveResult(String reflexiveResult) {
        this.reflexiveResult = reflexiveResult;
    }

    public String getSymmetricResult() {
        return symmetricResult;
    }

    public void setSymmetricResult(String symmetricResult) {
        this.symmetricResult = symmetricResult;
    }

    public String getTransitiveResult() {
        return transitiveResult;
    }

    public void setTransitiveResult(String transitiveResult) {
        this.transitiveResult = transitiveResult;
    }

    public String getEquivalenceClosure() {
        return equivalenceClosure;
    }

    public void setEquivalenceClosure(String equivalenceClosure) {
        this.equivalenceClosure = equivalenceClosure;
    }

    public String getEquivalenceClosureMatrix() {
        return equivalenceClosureMatrix;
    }

    public void setEquivalenceClosureMatrix(String equivalenceClosureMatrix) {
        this.equivalenceClosureMatrix = equivalenceClosureMatrix;
    }

    public String getEquivalenceClosureGraphUrl() {
        return equivalenceClosureGraphUrl;
    }

    public void setEquivalenceClosureGraphUrl(String equivalenceClosureGraphUrl) {
        this.equivalenceClosureGraphUrl = equivalenceClosureGraphUrl;
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

    public List<EquivalenceClass> getEquivalenceClasses() {
        return equivalenceClasses;
    }

    public void setEquivalenceClasses(List<EquivalenceClass> equivalenceClasses) {
        this.equivalenceClasses = equivalenceClasses;
    }

    public String getQuotientSet() {
        return quotientSet;
    }

    public void setQuotientSet(String quotientSet) {
        this.quotientSet = quotientSet;
    }

    // 内部类：等价类
    public static class EquivalenceClass {
        private String element;
        private String equivalenceClassLaTeX;

        public String getElement() {
            return element;
        }

        public void setElement(String element) {
            this.element = element;
        }

        public String getEquivalenceClassLaTeX() {
            return equivalenceClassLaTeX;
        }

        public void setEquivalenceClassLaTeX(String equivalenceClassLaTeX) {
            this.equivalenceClassLaTeX = equivalenceClassLaTeX;
        }
    }
}