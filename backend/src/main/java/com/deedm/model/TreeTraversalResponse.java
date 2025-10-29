package com.deedm.model;

public class TreeTraversalResponse {
    private boolean success;
    private String errorMessage;

    // 树的基本信息
    private String formula;
    private String nodesString;
    private String edgesString;
    private String rootString;
    private boolean directed;

    // 根节点信息
    private String rootNode;

    // 邻接矩阵和关联矩阵
    private String adjacencyMatrix;
    private String incidenceMatrix;

    // 树遍历结果
    private TraversalResult preorderResult;
    private TraversalResult inorderResult;
    private TraversalResult postorderResult;

    // 树形可视化
    private String graphImageUrl;

    // 构造函数
    public TreeTraversalResponse() {
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

    public String getRootString() {
        return rootString;
    }

    public void setRootString(String rootString) {
        this.rootString = rootString;
    }

    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public String getRootNode() {
        return rootNode;
    }

    public void setRootNode(String rootNode) {
        this.rootNode = rootNode;
    }

    public String getAdjacencyMatrix() {
        return adjacencyMatrix;
    }

    public void setAdjacencyMatrix(String adjacencyMatrix) {
        this.adjacencyMatrix = adjacencyMatrix;
    }

    public String getIncidenceMatrix() {
        return incidenceMatrix;
    }

    public void setIncidenceMatrix(String incidenceMatrix) {
        this.incidenceMatrix = incidenceMatrix;
    }

    public TraversalResult getPreorderResult() {
        return preorderResult;
    }

    public void setPreorderResult(TraversalResult preorderResult) {
        this.preorderResult = preorderResult;
    }

    public TraversalResult getInorderResult() {
        return inorderResult;
    }

    public void setInorderResult(TraversalResult inorderResult) {
        this.inorderResult = inorderResult;
    }

    public TraversalResult getPostorderResult() {
        return postorderResult;
    }

    public void setPostorderResult(TraversalResult postorderResult) {
        this.postorderResult = postorderResult;
    }

    public String getGraphImageUrl() {
        return graphImageUrl;
    }

    public void setGraphImageUrl(String graphImageUrl) {
        this.graphImageUrl = graphImageUrl;
    }

    // 内部类：遍历结果
    public static class TraversalResult {
        private String traversalOrder;

        public String getTraversalOrder() {
            return traversalOrder;
        }

        public void setTraversalOrder(String traversalOrder) {
            this.traversalOrder = traversalOrder;
        }
    }
}