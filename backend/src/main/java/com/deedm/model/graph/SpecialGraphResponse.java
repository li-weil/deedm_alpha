package com.deedm.model.graph;

import java.util.List;

public class SpecialGraphResponse {
    private boolean success;
    private String errorMessage;
    private String type; // 类型标记
    private int n;
    private int m;
    private List<GraphResult> graphResults;

    // 构造函数
    public SpecialGraphResponse() {
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

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public List<GraphResult> getGraphResults() {
        return graphResults;
    }

    public void setGraphResults(List<GraphResult> graphResults) {
        this.graphResults = graphResults;
    }

    // 内部类：单个图的结果
    public static class GraphResult {
        private String graphType; // 图类型：complete, cycle, wheel, hypercube, bigraph
        private String name; // 图的名称，如 K_n, C_n, W_n, Q_n, K_{n,m}
        private String description; // 中文描述
        private String imageUrl; // 图片URL
        private boolean generated; // 是否成功生成

        public GraphResult() {}

        public GraphResult(String graphType, String name, String description) {
            this.graphType = graphType;
            this.name = name;
            this.description = description;
            this.generated = false;
        }

        // Getters and Setters
        public String getGraphType() {
            return graphType;
        }

        public void setGraphType(String graphType) {
            this.graphType = graphType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            this.generated = imageUrl != null && !imageUrl.isEmpty();
        }

        public boolean isGenerated() {
            return generated;
        }

        public void setGenerated(boolean generated) {
            this.generated = generated;
        }
    }
}