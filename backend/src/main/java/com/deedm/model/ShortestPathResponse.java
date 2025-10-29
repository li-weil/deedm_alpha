package com.deedm.model;

import java.util.List;
import java.util.Map;

public class ShortestPathResponse {
    private boolean success;
    private String errorMessage;
    private String formula;
    private String type; // 添加类型标记
    private String startNode;
    private boolean directed;
    private String nodesString;
    private String edgesString;
    private List<Map<String, Object>> nodeDegrees;
    private String adjacencyMatrix;
    private String dijkstraDetails;
    private List<Map<String, Object>> shortestPaths;
    private String graphImageUrl;
    private String pathGraphImageUrl;

    public ShortestPathResponse() {}

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

    public List<Map<String, Object>> getNodeDegrees() {
        return nodeDegrees;
    }

    public void setNodeDegrees(List<Map<String, Object>> nodeDegrees) {
        this.nodeDegrees = nodeDegrees;
    }

    public String getAdjacencyMatrix() {
        return adjacencyMatrix;
    }

    public void setAdjacencyMatrix(String adjacencyMatrix) {
        this.adjacencyMatrix = adjacencyMatrix;
    }

    public String getDijkstraDetails() {
        return dijkstraDetails;
    }

    public void setDijkstraDetails(String dijkstraDetails) {
        this.dijkstraDetails = dijkstraDetails;
    }

    public List<Map<String, Object>> getShortestPaths() {
        return shortestPaths;
    }

    public void setShortestPaths(List<Map<String, Object>> shortestPaths) {
        this.shortestPaths = shortestPaths;
    }

    public String getGraphImageUrl() {
        return graphImageUrl;
    }

    public void setGraphImageUrl(String graphImageUrl) {
        this.graphImageUrl = graphImageUrl;
    }

    public String getPathGraphImageUrl() {
        return pathGraphImageUrl;
    }

    public void setPathGraphImageUrl(String pathGraphImageUrl) {
        this.pathGraphImageUrl = pathGraphImageUrl;
    }
}