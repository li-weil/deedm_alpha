package com.deedm.model;

public class GraphTravelRequest {
    private String nodesString;
    private String edgesString;
    private boolean directed;
    private boolean calculateDegree;
    private boolean calculatePath;
    private boolean performDFS;
    private boolean performBFS;
    private boolean showDetails;
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

    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public boolean isCalculateDegree() {
        return calculateDegree;
    }

    public void setCalculateDegree(boolean calculateDegree) {
        this.calculateDegree = calculateDegree;
    }

    public boolean isCalculatePath() {
        return calculatePath;
    }

    public void setCalculatePath(boolean calculatePath) {
        this.calculatePath = calculatePath;
    }

    public boolean isPerformDFS() {
        return performDFS;
    }

    public void setPerformDFS(boolean performDFS) {
        this.performDFS = performDFS;
    }

    public boolean isPerformBFS() {
        return performBFS;
    }

    public void setPerformBFS(boolean performBFS) {
        this.performBFS = performBFS;
    }

    public boolean isShowDetails() {
        return showDetails;
    }

    public void setShowDetails(boolean showDetails) {
        this.showDetails = showDetails;
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