package com.deedm.model.algebra;

import java.util.List;

public class GroupPermResponse {
    private boolean success;
    private String errorMessage;
    private String type; // 结果类型标记
    private String formula;

    // 基本信息
    private int m;
    private String groupElements;
    private String elementTable;
    private String operatorTable;

    // 循环群结果
    private boolean isCycleGroup;
    private String generators;

    // 群元素的幂
    private List<String> elementPowers;

    // 群元素的阶
    private List<String> elementOrders;

    // 子群结果
    private List<SubgroupResult> subgroups;

    // 陪集结果
    private List<CosetResult> cosets;

    // 正规子群结果
    private List<NormalSubgroupResult> normalSubgroups;

    // 构造函数
    public GroupPermResponse() {
        this.success = true;
        this.type = "group-perm";
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

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public String getGroupElements() {
        return groupElements;
    }

    public void setGroupElements(String groupElements) {
        this.groupElements = groupElements;
    }

    public String getElementTable() {
        return elementTable;
    }

    public void setElementTable(String elementTable) {
        this.elementTable = elementTable;
    }

    public String getOperatorTable() {
        return operatorTable;
    }

    public void setOperatorTable(String operatorTable) {
        this.operatorTable = operatorTable;
    }

    public boolean isCycleGroup() {
        return isCycleGroup;
    }

    public void setCycleGroup(boolean cycleGroup) {
        isCycleGroup = cycleGroup;
    }

    public String getGenerators() {
        return generators;
    }

    public void setGenerators(String generators) {
        this.generators = generators;
    }

    public List<String> getElementPowers() {
        return elementPowers;
    }

    public void setElementPowers(List<String> elementPowers) {
        this.elementPowers = elementPowers;
    }

    public List<String> getElementOrders() {
        return elementOrders;
    }

    public void setElementOrders(List<String> elementOrders) {
        this.elementOrders = elementOrders;
    }

    public List<SubgroupResult> getSubgroups() {
        return subgroups;
    }

    public void setSubgroups(List<SubgroupResult> subgroups) {
        this.subgroups = subgroups;
    }

    public List<CosetResult> getCosets() {
        return cosets;
    }

    public void setCosets(List<CosetResult> cosets) {
        this.cosets = cosets;
    }

    public List<NormalSubgroupResult> getNormalSubgroups() {
        return normalSubgroups;
    }

    public void setNormalSubgroups(List<NormalSubgroupResult> normalSubgroups) {
        this.normalSubgroups = normalSubgroups;
    }

    // 内部类：子群结果
    public static class SubgroupResult {
        private String subgroupElements;
        private boolean cycleSubgroup;  // 修改字段名，避免isCycleSubgroup的序列化问题
        private String generators;
        private String operatorTable;

        public String getSubgroupElements() {
            return subgroupElements;
        }

        public void setSubgroupElements(String subgroupElements) {
            this.subgroupElements = subgroupElements;
        }

        public boolean isCycleSubgroup() {
            return cycleSubgroup;
        }

        public void setCycleSubgroup(boolean cycleSubgroup) {
            this.cycleSubgroup = cycleSubgroup;
        }

        public String getGenerators() {
            return generators;
        }

        public void setGenerators(String generators) {
            this.generators = generators;
        }

        public String getOperatorTable() {
            return operatorTable;
        }

        public void setOperatorTable(String operatorTable) {
            this.operatorTable = operatorTable;
        }
    }

    // 内部类：陪集结果
    public static class CosetResult {
        private String subgroupElements;
        private List<String> leftCosets;
        private List<String> rightCosets;

        public String getSubgroupElements() {
            return subgroupElements;
        }

        public void setSubgroupElements(String subgroupElements) {
            this.subgroupElements = subgroupElements;
        }

        public List<String> getLeftCosets() {
            return leftCosets;
        }

        public void setLeftCosets(List<String> leftCosets) {
            this.leftCosets = leftCosets;
        }

        public List<String> getRightCosets() {
            return rightCosets;
        }

        public void setRightCosets(List<String> rightCosets) {
            this.rightCosets = rightCosets;
        }
    }

    // 内部类：正规子群结果
    public static class NormalSubgroupResult {
        private String subgroupElements;
        private boolean normal;  // 修改字段名，避免isNormal的序列化问题
        private String quotientGroupTable;

        public String getSubgroupElements() {
            return subgroupElements;
        }

        public void setSubgroupElements(String subgroupElements) {
            this.subgroupElements = subgroupElements;
        }

        public boolean isNormal() {
            return normal;
        }

        public void setNormal(boolean normal) {
            this.normal = normal;
        }

        public String getQuotientGroupTable() {
            return quotientGroupTable;
        }

        public void setQuotientGroupTable(String quotientGroupTable) {
            this.quotientGroupTable = quotientGroupTable;
        }
    }
}