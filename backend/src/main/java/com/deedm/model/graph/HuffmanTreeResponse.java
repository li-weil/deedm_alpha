package com.deedm.model.graph;

import java.util.List;
import java.util.Map;

public class HuffmanTreeResponse {
    private boolean success;
    private String errorMessage;
    private String type = "huffmanTree";

    // 输入信息
    private String leafSetLaTeX;
    private String formula;

    // Huffman树结果
    private String totalWeightLaTeX;
    private double totalWeight;
    private String treeImageUrl;

    // 算法步骤（如果显示详细信息）
    private List<HuffmanStep> steps;

    // 叶节点编码（如果显示编码）
    private Map<String, String> leafCodes;

    // 内部类表示算法步骤
    public static class HuffmanStep {
        private int step;
        private String forestLaTeX;
        private String forestImageUrl;
        private String prompt;

        public HuffmanStep() {}

        public HuffmanStep(int step, String forestLaTeX, String forestImageUrl, String prompt) {
            this.step = step;
            this.forestLaTeX = forestLaTeX;
            this.forestImageUrl = forestImageUrl;
            this.prompt = prompt;
        }

        public int getStep() { return step; }
        public void setStep(int step) { this.step = step; }

        public String getForestLaTeX() { return forestLaTeX; }
        public void setForestLaTeX(String forestLaTeX) { this.forestLaTeX = forestLaTeX; }

        public String getForestImageUrl() { return forestImageUrl; }
        public void setForestImageUrl(String forestImageUrl) { this.forestImageUrl = forestImageUrl; }

        public String getPrompt() { return prompt; }
        public void setPrompt(String prompt) { this.prompt = prompt; }
    }

    public HuffmanTreeResponse() {}

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

    public String getLeafSetLaTeX() {
        return leafSetLaTeX;
    }

    public void setLeafSetLaTeX(String leafSetLaTeX) {
        this.leafSetLaTeX = leafSetLaTeX;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getTotalWeightLaTeX() {
        return totalWeightLaTeX;
    }

    public void setTotalWeightLaTeX(String totalWeightLaTeX) {
        this.totalWeightLaTeX = totalWeightLaTeX;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public String getTreeImageUrl() {
        return treeImageUrl;
    }

    public void setTreeImageUrl(String treeImageUrl) {
        this.treeImageUrl = treeImageUrl;
    }

    public List<HuffmanStep> getSteps() {
        return steps;
    }

    public void setSteps(List<HuffmanStep> steps) {
        this.steps = steps;
    }

    public Map<String, String> getLeafCodes() {
        return leafCodes;
    }

    public void setLeafCodes(Map<String, String> leafCodes) {
        this.leafCodes = leafCodes;
    }
}