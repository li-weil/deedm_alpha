package com.deedm.model.algebra;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class LatticeJudgeResponse {
    private boolean success;
    private String errorMessage;
    private String type; // 结果类型标记
    private String formula; // LaTeX公式表示
    private String setAString;
    private String relationRString;
    private boolean intTypeElement;
    private boolean useHasseDiagram;

    // 偏序关系判断结果
    @JsonProperty("isPartialOrder")
    private boolean isPartialOrder;
    private String partialOrderReason;
    private String partialOrderCounterexample;

    // 格判断结果
    @JsonProperty("isLattice")
    private boolean isLattice;
    private String latticeReason;
    private String latticeCounterexample;

    // 哈斯图
    private String hasseDiagramUrl;

    // 格运算表
    private String supOperatorTable;
    private String subOperatorTable;
    private String supOperatorName;
    private String subOperatorName;

    // 分配格判断
    @JsonProperty("isDistributive")
    private Boolean isDistributive;
    private String distributiveReason;

    // 有界格判断
    @JsonProperty("isBounded")
    private Boolean isBounded;
    private String boundedReason;
    private String greatestElement;
    private String leastElement;

    // 有补格判断
    @JsonProperty("isComplemented")
    private Boolean isComplemented;
    private String complementedReason;
    private List<ElementComplement> complements;

    // 布尔代数判断
    @JsonProperty("isBooleanAlgebra")
    private Boolean isBooleanAlgebra;
    private String booleanAlgebraReason;

    // 构造函数
    public LatticeJudgeResponse() {
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

    public boolean isPartialOrder() {
        return isPartialOrder;
    }

    public void setPartialOrder(boolean partialOrder) {
        isPartialOrder = partialOrder;
    }

    public String getPartialOrderReason() {
        return partialOrderReason;
    }

    public void setPartialOrderReason(String partialOrderReason) {
        this.partialOrderReason = partialOrderReason;
    }

    public String getPartialOrderCounterexample() {
        return partialOrderCounterexample;
    }

    public void setPartialOrderCounterexample(String partialOrderCounterexample) {
        this.partialOrderCounterexample = partialOrderCounterexample;
    }

    public boolean isLattice() {
        return isLattice;
    }

    public void setLattice(boolean lattice) {
        isLattice = lattice;
    }

    public String getLatticeReason() {
        return latticeReason;
    }

    public void setLatticeReason(String latticeReason) {
        this.latticeReason = latticeReason;
    }

    public String getLatticeCounterexample() {
        return latticeCounterexample;
    }

    public void setLatticeCounterexample(String latticeCounterexample) {
        this.latticeCounterexample = latticeCounterexample;
    }

    public String getHasseDiagramUrl() {
        return hasseDiagramUrl;
    }

    public void setHasseDiagramUrl(String hasseDiagramUrl) {
        this.hasseDiagramUrl = hasseDiagramUrl;
    }

    public String getSupOperatorTable() {
        return supOperatorTable;
    }

    public void setSupOperatorTable(String supOperatorTable) {
        this.supOperatorTable = supOperatorTable;
    }

    public String getSubOperatorTable() {
        return subOperatorTable;
    }

    public void setSubOperatorTable(String subOperatorTable) {
        this.subOperatorTable = subOperatorTable;
    }

    public String getSupOperatorName() {
        return supOperatorName;
    }

    public void setSupOperatorName(String supOperatorName) {
        this.supOperatorName = supOperatorName;
    }

    public String getSubOperatorName() {
        return subOperatorName;
    }

    public void setSubOperatorName(String subOperatorName) {
        this.subOperatorName = subOperatorName;
    }

    public Boolean isDistributive() {
        return isDistributive;
    }

    public void setIsDistributive(Boolean isDistributive) {
        this.isDistributive = isDistributive;
    }

    public String getDistributiveReason() {
        return distributiveReason;
    }

    public void setDistributiveReason(String distributiveReason) {
        this.distributiveReason = distributiveReason;
    }

    public Boolean isBounded() {
        return isBounded;
    }

    public void setIsBounded(Boolean isBounded) {
        this.isBounded = isBounded;
    }

    public String getBoundedReason() {
        return boundedReason;
    }

    public void setBoundedReason(String boundedReason) {
        this.boundedReason = boundedReason;
    }

    public String getGreatestElement() {
        return greatestElement;
    }

    public void setGreatestElement(String greatestElement) {
        this.greatestElement = greatestElement;
    }

    public String getLeastElement() {
        return leastElement;
    }

    public void setLeastElement(String leastElement) {
        this.leastElement = leastElement;
    }

    public Boolean isComplemented() {
        return isComplemented;
    }

    public void setIsComplemented(Boolean isComplemented) {
        this.isComplemented = isComplemented;
    }

    public String getComplementedReason() {
        return complementedReason;
    }

    public void setComplementedReason(String complementedReason) {
        this.complementedReason = complementedReason;
    }

    public List<ElementComplement> getComplements() {
        return complements;
    }

    public void setComplements(List<ElementComplement> complements) {
        this.complements = complements;
    }

    public Boolean isBooleanAlgebra() {
        return isBooleanAlgebra;
    }

    public void setIsBooleanAlgebra(Boolean isBooleanAlgebra) {
        this.isBooleanAlgebra = isBooleanAlgebra;
    }

    public String getBooleanAlgebraReason() {
        return booleanAlgebraReason;
    }

    public void setBooleanAlgebraReason(String booleanAlgebraReason) {
        this.booleanAlgebraReason = booleanAlgebraReason;
    }

    // 内部类：元素补
    public static class ElementComplement {
        private String element;
        private String complementElements;

        public ElementComplement() {}

        public ElementComplement(String element, String complementElements) {
            this.element = element;
            this.complementElements = complementElements;
        }

        public String getElement() {
            return element;
        }

        public void setElement(String element) {
            this.element = element;
        }

        public String getComplementElements() {
            return complementElements;
        }

        public void setComplementElements(String complementElements) {
            this.complementElements = complementElements;
        }
    }
}