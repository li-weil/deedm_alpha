package com.deedm.service.graph;

import com.deedm.model.graph.SpanningTreeRequest;
import com.deedm.model.graph.SpanningTreeResponse;
import com.deedm.legacy.graph.*;
import com.deedm.legacy.util.GraphvizUtil;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.util.*;

@Service
public class SpanningTreeService {

    public SpanningTreeResponse calculateMinimalSpanningTree(SpanningTreeRequest request) {
        SpanningTreeResponse response = new SpanningTreeResponse();

        try {
            // 解析节点和边
            List<GraphNode> nodeList = new ArrayList<>();
            boolean success = GraphUtil.extractNodesFromFormatedString(request.getNodesString(), nodeList);

            if (!success) {
                response.setErrorMessage("给出顶点集的字符串 " + request.getNodesString() + " 不是合法的表示顶点集的字符串！错误信息：" + GraphUtil.getErrorMessage());
                return response;
            }

            List<GraphEdge> edgeList = new ArrayList<>();
            success = GraphUtil.extractWeightedEdgesFromFormatedString(nodeList, request.getEdgesString(), edgeList, request.isDirected());

            if (!success) {
                response.setErrorMessage("字符串 " + request.getEdgesString() + " 不是合法的表示边集字符串！错误信息：" + GraphUtil.getErrorMessage());
                return response;
            }

            // 创建带权图
            WeightedGraph graph = new WeightedGraph("WeightedGraph");
            graph.setNodes(nodeList);
            graph.setEdges(edgeList);

            // 设置基本信息
            response.setType("spanning-tree");
            response.setNodesString(request.getNodesString());
            response.setEdgesString(request.getEdgesString());
            response.setDirected(request.isDirected());
            response.setFormula("G = (V, E), 其中 V = " + GraphUtil.getNodeLaTeXString(nodeList) +
                              ", E = " + GraphUtil.getWeightedEdgeLaTeXString(edgeList));

            // 生成原图的可视化
            if (request.isShowGraph()) {
                try {
                    String graphImageUrl = generateGraphImage(graph, "WeightedGraph");
                    response.setGraphImageUrl(graphImageUrl);
                } catch (Exception e) {
                    System.err.println("原图生成失败: " + e.getMessage());
                }
            }

            // 生成距离矩阵
            if (request.isShowMatrix()) {
                try {
                    String weightMatrix = graph.getWeightAdjacencyMatrix().toLaTeXString();
                    response.setWeightMatrix(weightMatrix);
                } catch (Exception e) {
                    System.err.println("距离矩阵生成失败: " + e.getMessage());
                }
            }

            // Kruskal算法
            if (request.isPerformKruskal()) {
                try {
                    WeightedGraph kruskalTree = graph.kruskal();
                    double totalWeight = kruskalTree.getTotalWeight();

                    SpanningTreeResponse.KruskalResult kruskalResult = new SpanningTreeResponse.KruskalResult();
                    kruskalResult.setTotalWeight(totalWeight);

                    // 获取算法步骤
                    if (request.isShowDetails()) {
                        String steps = WeightedGraph.getKruskalRecorder().toLaTeXString();
                        kruskalResult.setSteps(steps);
                    }

                    // 获取边集
                    String edges = GraphUtil.getWeightedEdgeLaTeXString(kruskalTree.getEdges());
                    kruskalResult.setEdges(edges);

                    response.setKruskalResult(kruskalResult);

                    // 生成Kruskal最小生成树图片
                    if (request.isShowGraph()) {
                        try {
                            String treeImageUrl = generateGraphImage(kruskalTree, "KruskalTree");
                            response.setKruskalTreeImageUrl(treeImageUrl);
                        } catch (Exception e) {
                            System.err.println("Kruskal生成树图片生成失败: " + e.getMessage());
                        }
                    }

                } catch (Exception e) {
                    System.err.println("Kruskal算法执行失败: " + e.getMessage());
                }
            }

            // Prim算法
            if (request.isPerformPrim()) {
                try {
                    WeightedGraph primTree = graph.prim();
                    double totalWeight = primTree.getTotalWeight();

                    SpanningTreeResponse.PrimResult primResult = new SpanningTreeResponse.PrimResult();
                    primResult.setTotalWeight(totalWeight);

                    // 获取算法步骤
                    if (request.isShowDetails()) {
                        String steps = WeightedGraph.getPrimRecorder().toLaTeXString();
                        primResult.setSteps(steps);
                    }

                    // 获取边集
                    String edges = GraphUtil.getWeightedEdgeLaTeXString(primTree.getEdges());
                    primResult.setEdges(edges);

                    response.setPrimResult(primResult);

                    // 生成Prim最小生成树图片
                    if (request.isShowGraph()) {
                        try {
                            String treeImageUrl = generateGraphImage(primTree, "PrimTree");
                            response.setPrimTreeImageUrl(treeImageUrl);
                        } catch (Exception e) {
                            System.err.println("Prim生成树图片生成失败: " + e.getMessage());
                        }
                    }

                } catch (Exception e) {
                    System.err.println("Prim算法执行失败: " + e.getMessage());
                }
            }

            return response;

        } catch (Exception e) {
            response.setErrorMessage("最小生成树计算过程中发生错误: " + e.getMessage());
            return response;
        }
    }

    public String validateInput(String nodesString, String edgesString, boolean directed) {
        try {
            // 验证节点集
            List<GraphNode> nodeList = new ArrayList<>();
            boolean success = GraphUtil.extractNodesFromFormatedString(nodesString, nodeList);
            if (!success) {
                return "给出顶点集的字符串 " + nodesString + " 不是合法的表示顶点集的字符串！错误信息：" + GraphUtil.getErrorMessage();
            }

            // 验证边集
            List<GraphEdge> edgeList = new ArrayList<>();
            success = GraphUtil.extractWeightedEdgesFromFormatedString(nodeList, edgesString, edgeList, directed);
            if (!success) {
                return "字符串 " + edgesString + " 不是合法的表示边集字符串！错误信息：" + GraphUtil.getErrorMessage();
            }

            return null; // null表示验证通过
        } catch (Exception e) {
            return "输入验证过程中发生错误: " + e.getMessage();
        }
    }

    public Map<String, Object> generateRandomWeightedGraph() {
        try {
            boolean directed = false;
            int nodeNumber = (int)(Math.random() * 5) + 5; // 5-9个节点
            int edgeNumber = (int)(Math.random() * 20) + 1; // 1-20条边
            int maxWeight = 20; // 最大权重20

            DefaultGraph graph = GraphUtil.randomGenerateWeightedGraph(nodeNumber, edgeNumber, directed, true, maxWeight);

            String nodesString = GraphUtil.getFormatedNodeString(graph.getNodes());
            String edgesString = GraphUtil.getFormatedWeightedEdgeString(graph.getEdges());

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("nodesString", nodesString);
            result.put("edgesString", edgesString);
            result.put("directed", directed);
            result.put("options", Map.of(
                "performKruskal", true,
                "performPrim", true,
                "showDetails", true,
                "showMatrix", true,
                "showGraph", true
            ));

            return result;

        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "生成随机带权图失败: " + e.getMessage());
            return errorResult;
        }
    }

    private String generateGraphImage(DefaultGraph graph, String prefix) throws Exception {
        // 确保data目录存在
        java.io.File dataDir = new java.io.File("./data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        // 使用现有的GraphvizUtil生成图片
        String timestamp = String.valueOf(System.currentTimeMillis());
        String dotFileName = "./data/" + prefix + "_" + timestamp + ".dot";
        String pngFileName = "./data/" + prefix + "_" + timestamp + ".png";

        try (PrintWriter writer = new PrintWriter(dotFileName)) {
            graph.simplyWriteToDotFile(writer);
            writer.flush();
        }

        boolean success = GraphvizUtil.generatePNGFile(dotFileName, pngFileName, false);
        if (!success) {
            throw new RuntimeException("无法生成图片文件: " + GraphvizUtil.errorMessage);
        }

        return "/api/spanning-tree/tree-image/" + prefix + "_" + timestamp + ".png";
    }
}