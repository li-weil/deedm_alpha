package com.deedm.service.graph;

import com.deedm.model.graph.ShortestPathRequest;
import com.deedm.model.graph.ShortestPathResponse;
import com.deedm.legacy.graph.*;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
public class ShortestPathService {

    public ShortestPathResponse calculateShortestPath(ShortestPathRequest request) {
        ShortestPathResponse response = new ShortestPathResponse();

        try {
            // 解析节点
            List<GraphNode> nodeList = new ArrayList<>();
            boolean success = GraphUtil.extractNodesFromFormatedString(request.getNodesString(), nodeList);
            if (!success) {
                response.setSuccess(false);
                response.setErrorMessage("节点集格式错误: " + GraphUtil.getErrorMessage());
                return response;
            }

            // 查找起始节点
            GraphNode startNode = null;
            for (GraphNode node : nodeList) {
                if (node.getId().equals(request.getStartNode())) {
                    startNode = node;
                    break;
                }
            }

            if (startNode == null) {
                response.setSuccess(false);
                response.setErrorMessage("起始节点不在节点集中");
                return response;
            }

            // 解析边
            List<GraphEdge> edgeList = new ArrayList<>();
            success = GraphUtil.extractWeightedEdgesFromFormatedString(nodeList, request.getEdgesString(), edgeList, request.isDirected());
            if (!success) {
                response.setSuccess(false);
                response.setErrorMessage("边集格式错误: " + GraphUtil.getErrorMessage());
                return response;
            }

            // 创建带权图
            WeightedGraph graph = new WeightedGraph("ShortestPath");
            graph.setNodes(nodeList);
            graph.setEdges(edgeList);

            // 设置基本信息
            response.setSuccess(true);
            response.setType("shortest-path");
            response.setStartNode(startNode.getLabel());
            response.setDirected(request.isDirected());
            response.setFormula("G = (V, E), V = " + GraphUtil.getNodeLaTeXString(nodeList) + ", E = " + GraphUtil.getWeightedEdgeLaTeXString(edgeList));

            // 添加节点集和边集字符串（用于前端判断）
            response.setNodesString(request.getNodesString());
            response.setEdgesString(request.getEdgesString());

            // 显示邻接矩阵
            if (request.isShowMatrix()) {
                DoubleMatrix matrix = graph.getWeightAdjacencyMatrix();
                response.setAdjacencyMatrix(matrix.toLaTeXString());
            }

            // 生成原图可视化（独立于Dijkstra算法）
            if (request.isShowGraphVisualization()) {
                WeightedGraph graphForVisualization = new WeightedGraph("GraphVisualization");
                graphForVisualization.setNodes(nodeList);
                graphForVisualization.setEdges(edgeList);

                String graphImageName = generateGraphImage(graphForVisualization, "GraphVisualization");
                if (graphImageName != null) {
                    response.setGraphImageUrl("/api/shortest-path/graph-image/" + graphImageName);
                }
            }

            // 执行Dijkstra算法
            if (request.isExecuteDijkstra()) {
                List<WeightedGraphPath> pathList = graph.dijkstra(startNode, null);

                // 显示算法步骤
                if (request.isShowDetails()) {
                    String details = WeightedGraph.getDijkstraRecorder().toLaTeXString();
                    response.setDijkstraDetails(details);
                }

                // 显示最短路径
                if (request.isShowPaths() && pathList != null) {
                    List<Map<String, Object>> paths = new ArrayList<>();
                    for (WeightedGraphPath path : pathList) {
                        Map<String, Object> pathInfo = new HashMap<>();
                        pathInfo.put("formula", path.toLaTeXString(startNode));
                        List<GraphNode> nodes = path.getNodes();
                        if (!nodes.isEmpty()) {
                            pathInfo.put("target", nodes.get(nodes.size() - 1).getLabel());
                        } else {
                            pathInfo.put("target", "");
                        }
                        pathInfo.put("distance", path.getWeight());
                        paths.add(pathInfo);
                    }
                    response.setShortestPaths(paths);
                }

                // 生成路径图可视化（需要先有最短路径结果）
                if (request.isShowPathGraph() && pathList != null && !pathList.isEmpty()) {
                    WeightedGraph pathGraph = createPathGraph(nodeList, pathList);
                    String pathImageName = generateGraphImage(pathGraph, "ShortestPath");
                    if (pathImageName != null) {
                        response.setPathGraphImageUrl("/api/shortest-path/graph-image/" + pathImageName);
                    }
                }
            }

            // 如果没有执行Dijkstra算法但用户选择了显示最短路径图，需要单独执行
            if (!request.isExecuteDijkstra() && request.isShowPathGraph()) {
                // 执行Dijkstra算法获取最短路径
                List<WeightedGraphPath> pathList = graph.dijkstra(startNode, null);

                if (pathList != null && !pathList.isEmpty()) {
                    WeightedGraph pathGraph = createPathGraph(nodeList, pathList);
                    String pathImageName = generateGraphImage(pathGraph, "ShortestPath");
                    if (pathImageName != null) {
                        response.setPathGraphImageUrl("/api/shortest-path/graph-image/" + pathImageName);
                        response.setShortestPaths(new ArrayList<>()); // 确保有shortestPaths字段
                    }
                }
            }

        } catch (Exception e) {
            response.setSuccess(false);
            response.setErrorMessage("计算最短路径时发生错误: " + e.getMessage());
        }

        return response;
    }

    public String validateInput(String nodesString, String edgesString, boolean directed) {
        try {
            // 验证节点集
            List<GraphNode> nodeList = new ArrayList<>();
            boolean success = GraphUtil.extractNodesFromFormatedString(nodesString, nodeList);
            if (!success) {
                return "节点集格式错误: " + GraphUtil.getErrorMessage();
            }

            // 验证边集
            List<GraphEdge> edgeList = new ArrayList<>();
            success = GraphUtil.extractWeightedEdgesFromFormatedString(nodeList, edgesString, edgeList, directed);
            if (!success) {
                return "边集格式错误: " + GraphUtil.getErrorMessage();
            }

            return null; // 验证通过
        } catch (Exception e) {
            return "验证输入时发生错误: " + e.getMessage();
        }
    }

    public Map<String, Object> generateRandomGraph() {
        Map<String, Object> result = new HashMap<>();

        try {
            boolean directed = Math.random() > 0.5;
            int nodeNumber = (int)(Math.random() * 5) + 5;
            int edgeNumber = (int)(Math.random() * 20) + 1;
            int maxWeight = 20;

            DefaultGraph graph = GraphUtil.randomGenerateWeightedGraph(nodeNumber, edgeNumber, directed, true, maxWeight);

            String nodesString = GraphUtil.getFormatedNodeString(graph.getNodes());
            String edgesString = GraphUtil.getFormatedWeightedEdgeString(graph.getEdges());

            result.put("success", true);
            result.put("nodesString", nodesString);
            result.put("edgesString", edgesString);
            result.put("directed", directed);

            // 设置默认选项
            Map<String, Object> options = new HashMap<>();
            options.put("executeDijkstra", true);
            options.put("showDetails", true);
            options.put("showPaths", true);
            options.put("showPathGraph", true);
            options.put("showMatrix", true);
            options.put("showGraphVisualization", true);
            result.put("options", options);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "生成随机图失败: " + e.getMessage());
        }

        return result;
    }

  private String generateGraphImage(WeightedGraph graph, String prefix) {
        try {
            // 确保data目录存在
            File dataDir = new File("./data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }

            String timestamp = String.valueOf(System.currentTimeMillis());
            String fileName = prefix + "_" + timestamp + ".png";
            String dotFileName = "./data/" + prefix + "_" + timestamp + ".dot";
            String pngFileName = "./data/" + fileName;

            // 检查Graphviz可用性
            if (!com.deedm.legacy.util.GraphvizUtil.isGraphvizAvailable()) {
                System.err.println("Graphviz不可用，无法生成最短路径图片");
                return null;
            }

            // 写入DOT文件
            PrintWriter writer = new PrintWriter(dotFileName);
            graph.simplyWriteToDotFile(writer);
            writer.close();

            // 调用Graphviz生成PNG
            boolean success = com.deedm.legacy.util.GraphvizUtil.generatePNGFile(dotFileName, pngFileName, false);
            if (!success) {
                System.err.println("Graphviz生成失败: " + com.deedm.legacy.util.GraphvizUtil.errorMessage);
                return null;
            }

            // 验证PNG文件
            File imageFile = new File(pngFileName);
            if (imageFile.exists() && imageFile.length() > 0) {
                return fileName;
            } else {
                System.err.println("PNG文件生成失败或文件为空");
                return null;
            }

        } catch (Exception e) {
            System.err.println("生成最短路径图片失败: " + e.getMessage());
            return null;
        }
    }

    private WeightedGraph createPathGraph(List<GraphNode> nodes, List<WeightedGraphPath> paths) {
        WeightedGraph pathGraph = new WeightedGraph("ShortestPath");
        List<GraphEdge> pathEdges = new ArrayList<>();

        if (paths != null) {
            for (WeightedGraphPath path : paths) {
                for (GraphEdge edge : path.getEdges()) {
                    if (!pathEdges.contains(edge)) {
                        pathEdges.add(edge);
                    }
                }
            }
        }

        pathGraph.setNodes(nodes);
        pathGraph.setEdges(pathEdges);
        return pathGraph;
    }
}