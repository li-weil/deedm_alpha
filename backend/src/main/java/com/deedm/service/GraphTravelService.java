package com.deedm.service;

import com.deedm.model.GraphTravelRequest;
import com.deedm.model.GraphTravelResponse;
import com.deedm.legacy.graph.*;
import com.deedm.legacy.setrelfun.Matrix;
import com.deedm.legacy.util.GraphvizUtil;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
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

    public String validateGraphInput(String nodesString, String edgesString, boolean directed) {
        try {
            DefaultGraph graph = GraphUtil.createGraphUsingFormatedString("TestGraph", nodesString, edgesString, directed);
            if (graph != null) {
                return null; // null表示验证通过
            } else {
                return GraphUtil.getErrorMessage();
            }
        } catch (Exception e) {
            return "输入格式错误: " + e.getMessage();
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
        String traversalOrder = createLaTeXStringForNodeList(dfsOrder);
        dfsResult.setTraversalOrder(traversalOrder);

        if (showDetails) {
            List<DefaultGraph.TravelStepRecord> records = DefaultGraph.getTravelStepRecords();
            List<GraphTravelResponse.TravelStep> steps = new ArrayList<>();

            for (DefaultGraph.TravelStepRecord record : records) {
                GraphTravelResponse.TravelStep step = new GraphTravelResponse.TravelStep();
                step.setStep(record.getStep());
                step.setVisitedNodes(createLaTeXStringForNodeList(record.getVisitedNodeList()));
                step.setAuxNodes(createLaTeXStringForNodeList(record.getAuxNodeList()));
                steps.add(step);
            }

            dfsResult.setSteps(steps);
        }

        return dfsResult;
    }

    private GraphTravelResponse.BFSResult performBFS(DefaultGraph graph, boolean showDetails) {
        GraphTravelResponse.BFSResult bfsResult = new GraphTravelResponse.BFSResult();

        List<GraphNode> bfsOrder = graph.getBFSNodeList();
        String traversalOrder = createLaTeXStringForNodeList(bfsOrder);
        bfsResult.setTraversalOrder(traversalOrder);

        if (showDetails) {
            List<DefaultGraph.TravelStepRecord> records = DefaultGraph.getTravelStepRecords();
            List<GraphTravelResponse.TravelStep> steps = new ArrayList<>();

            for (DefaultGraph.TravelStepRecord record : records) {
                GraphTravelResponse.TravelStep step = new GraphTravelResponse.TravelStep();
                step.setStep(record.getStep());
                step.setVisitedNodes(createLaTeXStringForNodeList(record.getVisitedNodeList()));
                step.setAuxNodes(createLaTeXStringForNodeList(record.getAuxNodeList()));
                steps.add(step);
            }

            bfsResult.setSteps(steps);
        }

        return bfsResult;
    }

    private String generateGraphImage(DefaultGraph graph) {
        try {
            // 检查Graphviz是否可用
            if (!GraphvizUtil.isGraphvizAvailable()) {
                System.err.println("Graphviz不可用，无法生成图形可视化");
                return null;
            }

            // 生成唯一文件名
            String uniqueId = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            String dotFileName = "./data/GRAPH_" + uniqueId + ".dot";
            String pngFileName = "./data/GRAPH_" + uniqueId + ".png";

            // 写入DOT文件
            try (PrintWriter writer = new PrintWriter(dotFileName)) {
                writeGraphToDotFile(graph, writer);
            }

            // 生成PNG文件
            boolean success = GraphvizUtil.generatePNGFile(dotFileName, pngFileName, false);

            if (success && new java.io.File(pngFileName).exists()) {
                // 删除DOT文件，保留PNG文件
                new java.io.File(dotFileName).delete();
                return "/api/graph-travel/graph-image/GRAPH_" + uniqueId + ".png";
            } else {
                // 清理失败的文件
                new java.io.File(dotFileName).delete();
                new java.io.File(pngFileName).delete();
                System.err.println("Graphviz生成失败: " + GraphvizUtil.errorMessage);
                return null;
            }

        } catch (Exception e) {
            System.err.println("生成图形可视化失败: " + e.getMessage());
            return null;
        }
    }

    /**
     * 创建节点列表的LaTeX字符串表示，用于遍历结果显示
     */
    private String createLaTeXStringForNodeList(List<GraphNode> nodeList) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("\\langle ");
        boolean isFirst = true;
        for (GraphNode node : nodeList) {
            if (isFirst) {
                buffer.append(node.getLabel());
                isFirst = false;
            } else buffer.append(", " + node.getLabel());
        }
        buffer.append("\\rangle ");

        return buffer.toString();
    }

    /**
     * 将图写入DOT文件格式
     */
    private void writeGraphToDotFile(DefaultGraph graph, PrintWriter writer) {
        // 检查是否有有向边来决定图的类型
        boolean hasDirectedEdges = graph.getEdges().stream().anyMatch(GraphEdge::isDirected);

        if (hasDirectedEdges) {
            writer.println("digraph G {");
            writer.println("  rankdir=TB;");
            writer.println("  node [shape=circle, style=filled, fillcolor=lightblue];");

            // 写入节点
            for (GraphNode node : graph.getNodes()) {
                writer.println("  \"" + node.getId() + "\" [label=\"" + node.getLabel() + "\"];");
            }

            writer.println();

            // 写入边
            for (GraphEdge edge : graph.getEdges()) {
                String startId = edge.getStartNode().getId();
                String endId = edge.getEndNode().getId();

                if (edge.isDirected()) {
                    writer.println("  \"" + startId + "\" -> \"" + endId + "\";");
                } else {
                    // 在digraph中使用有向边表示无向边（双向）
                    writer.println("  \"" + startId + "\" -> \"" + endId + "\" [dir=none];");
                }
            }

            writer.println("}");
        } else {
            // 无向图使用graph模式
            writer.println("graph G {");
            writer.println("  rankdir=TB;");
            writer.println("  node [shape=circle, style=filled, fillcolor=lightblue];");

            // 写入节点
            for (GraphNode node : graph.getNodes()) {
                writer.println("  \"" + node.getId() + "\" [label=\"" + node.getLabel() + "\"];");
            }

            writer.println();

            // 写入边
            for (GraphEdge edge : graph.getEdges()) {
                String startId = edge.getStartNode().getId();
                String endId = edge.getEndNode().getId();
                writer.println("  \"" + startId + "\" -- \"" + endId + "\";");
            }

            writer.println("}");
        }
    }
}