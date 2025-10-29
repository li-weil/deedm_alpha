package com.deedm.model;

import java.util.List;
import java.util.Map;

public class GraphTravelResponse {
    private boolean success;
    private String errorMessage;

    // 图的基本信息
    private String formula;
    private String nodesString;
    private String edgesString;
    private boolean directed;

    // 度数计算结果
    private List<NodeDegree> nodeDegrees;

    // 邻接矩阵和关联矩阵
    private String adjacencyMatrix;
    private String incidenceMatrix;

    // 路径矩阵计算结果
    private List<String> pathMatrices;

    // DFS遍历结果
    private DFSResult dfsResult;

    // BFS遍历结果
    private BFSResult bfsResult;

    // 图形可视化
    private String graphImageUrl;

    // 构造函数
    public GraphTravelResponse() {
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

    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public List<NodeDegree> getNodeDegrees() {
        return nodeDegrees;
    }

    public void setNodeDegrees(List<NodeDegree> nodeDegrees) {
        this.nodeDegrees = nodeDegrees;
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

    public List<String> getPathMatrices() {
        return pathMatrices;
    }

    public void setPathMatrices(List<String> pathMatrices) {
        this.pathMatrices = pathMatrices;
    }

    public DFSResult getDfsResult() {
        return dfsResult;
    }

    public void setDfsResult(DFSResult dfsResult) {
        this.dfsResult = dfsResult;
    }

    public BFSResult getBfsResult() {
        return bfsResult;
    }

    public void setBfsResult(BFSResult bfsResult) {
        this.bfsResult = bfsResult;
    }

    public String getGraphImageUrl() {
        return graphImageUrl;
    }

    public void setGraphImageUrl(String graphImageUrl) {
        this.graphImageUrl = graphImageUrl;
    }

    // 内部类：节点度数
    public static class NodeDegree {
        private String nodeId;
        private String nodeLabel;
        private int degree;
        private int inDegree;
        private int outDegree;

        // Getters and Setters
        public String getNodeId() {
            return nodeId;
        }

        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
        }

        public String getNodeLabel() {
            return nodeLabel;
        }

        public void setNodeLabel(String nodeLabel) {
            this.nodeLabel = nodeLabel;
        }

        public int getDegree() {
            return degree;
        }

        public void setDegree(int degree) {
            this.degree = degree;
        }

        public int getInDegree() {
            return inDegree;
        }

        public void setInDegree(int inDegree) {
            this.inDegree = inDegree;
        }

        public int getOutDegree() {
            return outDegree;
        }

        public void setOutDegree(int outDegree) {
            this.outDegree = outDegree;
        }
    }

    // 内部类：DFS结果
    public static class DFSResult {
        private String traversalOrder;
        private List<TravelStep> steps;

        public String getTraversalOrder() {
            return traversalOrder;
        }

        public void setTraversalOrder(String traversalOrder) {
            this.traversalOrder = traversalOrder;
        }

        public List<TravelStep> getSteps() {
            return steps;
        }

        public void setSteps(List<TravelStep> steps) {
            this.steps = steps;
        }
    }

    // 内部类：BFS结果
    public static class BFSResult {
        private String traversalOrder;
        private List<TravelStep> steps;

        public String getTraversalOrder() {
            return traversalOrder;
        }

        public void setTraversalOrder(String traversalOrder) {
            this.traversalOrder = traversalOrder;
        }

        public List<TravelStep> getSteps() {
            return steps;
        }

        public void setSteps(List<TravelStep> steps) {
            this.steps = steps;
        }
    }

    // 内部类：遍历步骤
    public static class TravelStep {
        private int step;
        private String visitedNodes;
        private String auxNodes; // DFS中的栈或BFS中的队列

        public int getStep() {
            return step;
        }

        public void setStep(int step) {
            this.step = step;
        }

        public String getVisitedNodes() {
            return visitedNodes;
        }

        public void setVisitedNodes(String visitedNodes) {
            this.visitedNodes = visitedNodes;
        }

        public String getAuxNodes() {
            return auxNodes;
        }

        public void setAuxNodes(String auxNodes) {
            this.auxNodes = auxNodes;
        }
    }
}