package com.deedm.model;

public class ShortestPathRequest {
    private String nodesString;
    private String edgesString;
    private String startNode;
    private boolean directed;
    private boolean executeDijkstra = true;
    private boolean showDetails = false;
    private boolean showPaths = true;
    private boolean showPathGraph = false;
    private boolean showMatrix = false;
    private boolean showGraphVisualization = false;

    public ShortestPathRequest() {}

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

    public String getStartNode() {
        return startNode;
    }

    public void setStartNode(String startNode) {
        this.startNode = startNode;
    }

    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public boolean isExecuteDijkstra() {
        return executeDijkstra;
    }

    public void setExecuteDijkstra(boolean executeDijkstra) {
        this.executeDijkstra = executeDijkstra;
    }

    public boolean isShowDetails() {
        return showDetails;
    }

    public void setShowDetails(boolean showDetails) {
        this.showDetails = showDetails;
    }

    public boolean isShowPaths() {
        return showPaths;
    }

    public void setShowPaths(boolean showPaths) {
        this.showPaths = showPaths;
    }

    public boolean isShowPathGraph() {
        return showPathGraph;
    }

    public void setShowPathGraph(boolean showPathGraph) {
        this.showPathGraph = showPathGraph;
    }

    public boolean isShowMatrix() {
        return showMatrix;
    }

    public void setShowMatrix(boolean showMatrix) {
        this.showMatrix = showMatrix;
    }

    public boolean isShowGraphVisualization() {
        return showGraphVisualization;
    }

    public void setShowGraphVisualization(boolean showGraphVisualization) {
        this.showGraphVisualization = showGraphVisualization;
    }
}