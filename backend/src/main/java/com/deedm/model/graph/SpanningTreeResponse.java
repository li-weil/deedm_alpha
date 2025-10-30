package com.deedm.model.graph;

public class SpanningTreeResponse {
    private boolean success;
    private String errorMessage;

    // 图的基本信息
    private String formula;
    private String type; // 添加类型标记
    private String nodesString;
    private String edgesString;
    private boolean directed;

    // 带权图的距离矩阵
    private String weightMatrix;

    // Kruskal算法结果
    private KruskalResult kruskalResult;

    // Prim算法结果
    private PrimResult primResult;

    // 图形可视化
    private String graphImageUrl;
    private String kruskalTreeImageUrl;
    private String primTreeImageUrl;

    // 构造函数
    public SpanningTreeResponse() {
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

    public String getNodesString() {
        return nodesString;
    }

    public void setNodesString(String nodesString) {
        this.nodesString = nodesString;
    }

    public String getEdgesString() {
        return edgesString;
    }

    public void setEdgesString(String edgesString) {
        this.edgesString = edgesString;
    }

    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public String getWeightMatrix() {
        return weightMatrix;
    }

    public void setWeightMatrix(String weightMatrix) {
        this.weightMatrix = weightMatrix;
    }

    public KruskalResult getKruskalResult() {
        return kruskalResult;
    }

    public void setKruskalResult(KruskalResult kruskalResult) {
        this.kruskalResult = kruskalResult;
    }

    public PrimResult getPrimResult() {
        return primResult;
    }

    public void setPrimResult(PrimResult primResult) {
        this.primResult = primResult;
    }

    public String getGraphImageUrl() {
        return graphImageUrl;
    }

    public void setGraphImageUrl(String graphImageUrl) {
        this.graphImageUrl = graphImageUrl;
    }

    public String getKruskalTreeImageUrl() {
        return kruskalTreeImageUrl;
    }

    public void setKruskalTreeImageUrl(String kruskalTreeImageUrl) {
        this.kruskalTreeImageUrl = kruskalTreeImageUrl;
    }

    public String getPrimTreeImageUrl() {
        return primTreeImageUrl;
    }

    public void setPrimTreeImageUrl(String primTreeImageUrl) {
        this.primTreeImageUrl = primTreeImageUrl;
    }

    // Kruskal算法结果内部类
    public static class KruskalResult {
        private double totalWeight;
        private String steps;
        private String edges;

        public KruskalResult() {}

        public KruskalResult(double totalWeight, String steps, String edges) {
            this.totalWeight = totalWeight;
            this.steps = steps;
            this.edges = edges;
        }

        public double getTotalWeight() {
            return totalWeight;
        }

        public void setTotalWeight(double totalWeight) {
            this.totalWeight = totalWeight;
        }

        public String getSteps() {
            return steps;
        }

        public void setSteps(String steps) {
            this.steps = steps;
        }

        public String getEdges() {
            return edges;
        }

        public void setEdges(String edges) {
            this.edges = edges;
        }
    }

    // Prim算法结果内部类
    public static class PrimResult {
        private double totalWeight;
        private String steps;
        private String edges;

        public PrimResult() {}

        public PrimResult(double totalWeight, String steps, String edges) {
            this.totalWeight = totalWeight;
            this.steps = steps;
            this.edges = edges;
        }

        public double getTotalWeight() {
            return totalWeight;
        }

        public void setTotalWeight(double totalWeight) {
            this.totalWeight = totalWeight;
        }

        public String getSteps() {
            return steps;
        }

        public void setSteps(String steps) {
            this.steps = steps;
        }

        public String getEdges() {
            return edges;
        }

        public void setEdges(String edges) {
            this.edges = edges;
        }
    }
}