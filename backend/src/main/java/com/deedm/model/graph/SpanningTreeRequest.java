package com.deedm.model.graph;

public class SpanningTreeRequest {
    private String nodesString;
    private String edgesString;
    private boolean directed;
    private boolean performKruskal;
    private boolean performPrim;
    private boolean showDetails;
    private boolean showMatrix;
    private boolean showGraph;
    private boolean showSpanningTreeGraph;

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

    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public boolean isPerformKruskal() {
        return performKruskal;
    }

    public void setPerformKruskal(boolean performKruskal) {
        this.performKruskal = performKruskal;
    }

    public boolean isPerformPrim() {
        return performPrim;
    }

    public void setPerformPrim(boolean performPrim) {
        this.performPrim = performPrim;
    }

    public boolean isShowDetails() {
        return showDetails;
    }

    public void setShowDetails(boolean showDetails) {
        this.showDetails = showDetails;
    }

    public boolean isShowMatrix() {
        return showMatrix;
    }

    public void setShowMatrix(boolean showMatrix) {
        this.showMatrix = showMatrix;
    }

    public boolean isShowGraph() {
        return showGraph;
    }

    public void setShowGraph(boolean showGraph) {
        this.showGraph = showGraph;
    }
}