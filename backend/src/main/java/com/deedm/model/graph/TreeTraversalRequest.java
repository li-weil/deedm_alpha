package com.deedm.model.graph;

public class TreeTraversalRequest {
    private String nodesString;
    private String edgesString;
    private String rootString;
    private boolean directed;

    // 遍历选项
    private boolean preorder;
    private boolean inorder;
    private boolean postorder;

    // 显示选项
    private boolean showAdjacencyMatrix;
    private boolean showIncidenceMatrix;
    private boolean showGraphVisualization;

    // Getters and Setters
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

    public boolean isPreorder() {
        return preorder;
    }

    public void setPreorder(boolean preorder) {
        this.preorder = preorder;
    }

    public boolean isInorder() {
        return inorder;
    }

    public void setInorder(boolean inorder) {
        this.inorder = inorder;
    }

    public boolean isPostorder() {
        return postorder;
    }

    public void setPostorder(boolean postorder) {
        this.postorder = postorder;
    }

    public boolean isShowAdjacencyMatrix() {
        return showAdjacencyMatrix;
    }

    public void setShowAdjacencyMatrix(boolean showAdjacencyMatrix) {
        this.showAdjacencyMatrix = showAdjacencyMatrix;
    }

    public boolean isShowIncidenceMatrix() {
        return showIncidenceMatrix;
    }

    public void setShowIncidenceMatrix(boolean showIncidenceMatrix) {
        this.showIncidenceMatrix = showIncidenceMatrix;
    }

    public boolean isShowGraphVisualization() {
        return showGraphVisualization;
    }

    public void setShowGraphVisualization(boolean showGraphVisualization) {
        this.showGraphVisualization = showGraphVisualization;
    }
}