package com.deedm.model.algebra;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GroupUmResponse {
    private boolean success;
    private String errorMessage;
    private String type;
    private int m;
    private String groupElements;
    private String operatorTable;
    @JsonProperty("cycleGroup")
    private boolean isCycleGroup;
    private String generators;
    private List<Map<String, Object>> elementPowers;
    private List<Map<String, Object>> elementOrders;
    private List<SubgroupInfo> subgroups;
    private String formula;

    public GroupUmResponse() {
        this.type = "groupUm";
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
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public List<Map<String, Object>> getElementPowers() {
        return elementPowers;
    }

    public void setElementPowers(List<Map<String, Object>> elementPowers) {
        this.elementPowers = elementPowers;
    }

    public List<Map<String, Object>> getElementOrders() {
        return elementOrders;
    }

    public void setElementOrders(List<Map<String, Object>> elementOrders) {
        this.elementOrders = elementOrders;
    }

    public List<SubgroupInfo> getSubgroups() {
        return subgroups;
    }

    public void setSubgroups(List<SubgroupInfo> subgroups) {
        this.subgroups = subgroups;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    // Inner class for subgroup information
    public static class SubgroupInfo {
        private String elements;
        private String operatorTable;
        @JsonProperty("isCycleSubgroup")
        private boolean isCycleSubgroup;
        private String generators;
        private List<String> cosets;

        public SubgroupInfo() {}

        public SubgroupInfo(String elements, String operatorTable, boolean isCycleSubgroup, String generators, List<String> cosets) {
            this.elements = elements;
            this.operatorTable = operatorTable;
            this.isCycleSubgroup = isCycleSubgroup;
            this.generators = generators;
            this.cosets = cosets;
        }

        // Getters and Setters
        public String getElements() {
            return elements;
        }

        public void setElements(String elements) {
            this.elements = elements;
        }

        public String getOperatorTable() {
            return operatorTable;
        }

        public void setOperatorTable(String operatorTable) {
            this.operatorTable = operatorTable;
        }

        public boolean isCycleSubgroup() {
            return isCycleSubgroup;
        }

        public void setCycleSubgroup(boolean cycleSubgroup) {
            isCycleSubgroup = cycleSubgroup;
        }

        public String getGenerators() {
            return generators;
        }

        public void setGenerators(String generators) {
            this.generators = generators;
        }

        public List<String> getCosets() {
            return cosets;
        }

        public void setCosets(List<String> cosets) {
            this.cosets = cosets;
        }
    }
}