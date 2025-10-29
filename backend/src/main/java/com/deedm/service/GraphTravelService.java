package com.deedm.service;

import com.deedm.model.GraphTravelRequest;
import com.deedm.model.GraphTravelResponse;
import com.deedm.legacy.graph.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GraphTravelService {

    public GraphTravelResponse analyzeGraph(GraphTravelRequest request) {
        GraphTravelResponse response = new GraphTravelResponse();

        try {
            // 创建图
            DefaultGraph graph = GraphUtil.createGraphUsingFormatedString(
                "InputGraph",
                request.getNodesString(),
                request.getEdgesString(),
                request.isDirected()
            );

            if (graph == null) {
                response.setErrorMessage("无法创建图：" + GraphUtil.getErrorMessage());
                return response;
            }

            // 设置基本信息
            response.setNodesString(request.getNodesString());
            response.setEdgesString(request.getEdgesString());
            response.setDirected(request.isDirected());
            response.setFormula("G = (V, E), 其中 V = " + GraphUtil.getNodeLaTeXString(graph.getNodes()) +
                              ", E = " + GraphUtil.getEdgeLaTeXString(graph.getEdges()));

            // 生成图形可视化
            if (request.isShowGraphVisualization()) {
                try {
                    String graphImageUrl = generateGraphImage(graph);
                    response.setGraphImageUrl(graphImageUrl);
                } catch (Exception e) {
                    // 图形生成失败不影响其他功能
                    System.err.println("图形生成失败: " + e.getMessage());
                }
            }

            // 计算度数
            if (request.isCalculateDegree()) {
                List<GraphTravelResponse.NodeDegree> nodeDegrees = calculateNodeDegrees(graph);
                response.setNodeDegrees(nodeDegrees);
            }

            // 生成邻接矩阵和关联矩阵
            if (request.isShowAdjacencyMatrix()) {
                Matrix adjacencyMatrix = graph.getAdjacencyMatrix();
                response.setAdjacencyMatrix(adjacencyMatrix.toLaTeXString());
            }

            if (request.isShowIncidenceMatrix()) {
                Matrix incidenceMatrix = graph.getIncidenceMatrix();
                response.setIncidenceMatrix(incidenceMatrix.toLaTeXString());
            }

            // 计算路径矩阵
            if (request.isCalculatePath()) {
                List<String> pathMatrices = calculatePathMatrices(graph);
                response.setPathMatrices(pathMatrices);
            }

            // DFS遍历
            if (request.isPerformDFS()) {
                GraphTravelResponse.DFSResult dfsResult = performDFS(graph, request.isShowDetails());
                response.setDfsResult(dfsResult);
            }

            // BFS遍历
            if (request.isPerformBFS()) {
                GraphTravelResponse.BFSResult bfsResult = performBFS(graph, request.isShowDetails());
                response.setBfsResult(bfsResult);
            }

            return response;

        } catch (Exception e) {
            response.setErrorMessage("图分析过程中发生错误: " + e.getMessage());
            return response;
        }
    }

    public boolean validateGraphInput(String nodesString, String edgesString, boolean directed) {
        try {
            DefaultGraph graph = GraphUtil.createGraphUsingFormatedString("TestGraph", nodesString, edgesString, directed);
            return graph != null;
        } catch (Exception e) {
            return false;
        }
    }

    public Map<String, Object> generateRandomGraph() {
        Map<String, Object> result = new HashMap<>();

        try {
            Random random = new Random();
            int nodeNumber = random.nextInt(8) + 1; // 1-8个节点
            int edgeNumber = random.nextInt(10) + 1; // 1-10条边
            boolean directed = random.nextBoolean();

            DefaultGraph graph = GraphUtil.randomGenerateGraph(nodeNumber, edgeNumber, directed, true);

            String nodesString = GraphUtil.getFormatedNodeString(graph.getNodes());
            String edgesString = GraphUtil.getFormatedEdgeString(graph.getEdges());

            result.put("success", true);
            result.put("nodesString", nodesString);
            result.put("edgesString", edgesString);
            result.put("directed", directed);

            // 设置默认选项
            Map<String, Boolean> options = new HashMap<>();
            options.put("calculateDegree", false);
            options.put("performDFS", true);
            options.put("performBFS", true);
            options.put("showDetails", true);
            options.put("showAdjacencyMatrix", false);
            options.put("showIncidenceMatrix", false);
            options.put("showGraphVisualization", true);
            options.put("calculatePath", false);

            result.put("options", options);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "生成随机图失败: " + e.getMessage());
        }

        return result;
    }

    private List<GraphTravelResponse.NodeDegree> calculateNodeDegrees(DefaultGraph graph) {
        List<GraphTravelResponse.NodeDegree> nodeDegrees = new ArrayList<>();
        List<GraphNode> nodes = graph.getNodes();

        for (GraphNode node : nodes) {
            GraphTravelResponse.NodeDegree nodeDegree = new GraphTravelResponse.NodeDegree();
            nodeDegree.setNodeId(node.getId());
            nodeDegree.setNodeLabel(node.getLabel());
            nodeDegree.setDegree(graph.getDegree(node));

            if (graph.getEdges().stream().anyMatch(GraphEdge::isDirected)) {
                nodeDegree.setInDegree(graph.getInDegree(node));
                nodeDegree.setOutDegree(graph.getOutDegree(node));
            }

            nodeDegrees.add(nodeDegree);
        }

        return nodeDegrees;
    }

    private List<String> calculatePathMatrices(DefaultGraph graph) {
        List<String> pathMatrices = new ArrayList<>();
        Matrix[] matrices = graph.calculatePathNumberByUsingAdjacencyMatrix();

        for (int i = 0; i < matrices.length; i++) {
            String latex = "M_R^{[" + (i+1) + "]} = " + matrices[i].toLaTeXString();
            pathMatrices.add(latex);
        }

        return pathMatrices;
    }

    private GraphTravelResponse.DFSResult performDFS(DefaultGraph graph, boolean showDetails) {
        GraphTravelResponse.DFSResult dfsResult = new GraphTravelResponse.DFSResult();

        List<GraphNode> dfsOrder = graph.getDFSNodeList();
        String traversalOrder = GraphUtil.createLaTeXStringForNodeList(dfsOrder);
        dfsResult.setTraversalOrder(traversalOrder);

        if (showDetails) {
            List<DefaultGraph.TravelStepRecord> records = DefaultGraph.getTravelStepRecords();
            List<GraphTravelResponse.TravelStep> steps = new ArrayList<>();

            for (DefaultGraph.TravelStepRecord record : records) {
                GraphTravelResponse.TravelStep step = new GraphTravelResponse.TravelStep();
                step.setStep(record.getStep());
                step.setVisitedNodes(GraphUtil.createLaTeXStringForNodeList(record.getVisitedNodeList()));
                step.setAuxNodes(GraphUtil.createLaTeXStringForNodeList(record.getAuxNodeList()));
                steps.add(step);
            }

            dfsResult.setSteps(steps);
        }

        return dfsResult;
    }

    private GraphTravelResponse.BFSResult performBFS(DefaultGraph graph, boolean showDetails) {
        GraphTravelResponse.BFSResult bfsResult = new GraphTravelResponse.BFSResult();

        List<GraphNode> bfsOrder = graph.getBFSNodeList();
        String traversalOrder = GraphUtil.createLaTeXStringForNodeList(bfsOrder);
        bfsResult.setTraversalOrder(traversalOrder);

        if (showDetails) {
            List<DefaultGraph.TravelStepRecord> records = DefaultGraph.getTravelStepRecords();
            List<GraphTravelResponse.TravelStep> steps = new ArrayList<>();

            for (DefaultGraph.TravelStepRecord record : records) {
                GraphTravelResponse.TravelStep step = new GraphTravelResponse.TravelStep();
                step.setStep(record.getStep());
                step.setVisitedNodes(GraphUtil.createLaTeXStringForNodeList(record.getVisitedNodeList()));
                step.setAuxNodes(GraphUtil.createLaTeXStringForNodeList(record.getAuxNodeList()));
                steps.add(step);
            }

            bfsResult.setSteps(steps);
        }

        return bfsResult;
    }

    private String generateGraphImage(DefaultGraph graph) {
        // 这里应该实现图形生成逻辑，类似于legacy代码中的GraphViz部分
        // 暂时返回null，后续可以实现
        return null;
    }
}