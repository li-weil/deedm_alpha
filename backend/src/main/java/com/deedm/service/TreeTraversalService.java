package com.deedm.service;

import com.deedm.model.TreeTraversalRequest;
import com.deedm.model.TreeTraversalResponse;
import com.deedm.legacy.graph.*;
import com.deedm.legacy.setrelfun.Matrix;
import com.deedm.legacy.util.GraphvizUtil;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TreeTraversalService {

    public TreeTraversalResponse analyzeTree(TreeTraversalRequest request) {
        TreeTraversalResponse response = new TreeTraversalResponse();

        try {
            // 创建图
            DefaultGraph graph = GraphUtil.createGraphUsingFormatedString(
                "InputTree",
                request.getNodesString(),
                request.getEdgesString(),
                request.isDirected()
            );

            if (graph == null) {
                response.setErrorMessage("无法创建树：" + GraphUtil.getErrorMessage());
                return response;
            }

            // 设置基本信息
            response.setNodesString(request.getNodesString());
            response.setEdgesString(request.getEdgesString());
            response.setRootString(request.getRootString());
            response.setDirected(request.isDirected());
            response.setFormula("T = (V, E), 其中 V = " + GraphUtil.getNodeLaTeXString(graph.getNodes()) +
                              ", E = " + GraphUtil.getEdgeLaTeXString(graph.getEdges()));

            // 查找根节点
            DefaultGraphNode rootNode = null;
            for (GraphNode node : graph.getNodes()) {
                if (node.getId().equals(request.getRootString())) {
                    rootNode = (DefaultGraphNode) node;
                    break;
                }
            }

            if (rootNode == null) {
                response.setErrorMessage("指定的根节点不在节点集中");
                return response;
            }

            // 设置根节点信息
            response.setRootNode(rootNode.getLabel());

            // 检查是否为树（无向且连通）
            if (!graph.isUndirectTree()) {
                response.setErrorMessage("给定的图不是一棵树（可能不是无向图或不是连通的）");
                return response;
            }

            // 生成根树
            RootedTree tree = RootedTree.getAnRootedTree(graph, rootNode, false);

            // 生成树形可视化
            if (request.isShowGraphVisualization()) {
                try {
                    String treeImageUrl = generateTreeImage(tree);
                    response.setGraphImageUrl(treeImageUrl);
                } catch (Exception e) {
                    // 图形生成失败不影响其他功能
                    System.err.println("树形图生成失败: " + e.getMessage());
                }
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

            // 执行树遍历
            if (request.isPreorder()) {
                TreeTraversalResponse.TraversalResult preorderResult = performPreorderTraversal(tree);
                response.setPreorderResult(preorderResult);
            }

            if (request.isInorder()) {
                TreeTraversalResponse.TraversalResult inorderResult = performInorderTraversal(tree);
                response.setInorderResult(inorderResult);
            }

            if (request.isPostorder()) {
                TreeTraversalResponse.TraversalResult postorderResult = performPostorderTraversal(tree);
                response.setPostorderResult(postorderResult);
            }

            return response;

        } catch (Exception e) {
            response.setErrorMessage("树遍历分析过程中发生错误: " + e.getMessage());
            return response;
        }
    }

    public String validateTreeInput(String nodesString, String edgesString, String rootString) {
        try {
            DefaultGraph graph = GraphUtil.createGraphUsingFormatedString("TestTree", nodesString, edgesString, false);
            if (graph != null) {
                // 检查根节点是否在节点集中
                for (GraphNode node : graph.getNodes()) {
                    if (node.getId().equals(rootString)) {
                        // 检查是否为树
                        if (graph.isUndirectTree()) {
                            return null; // null表示验证通过
                        } else {
                            return "给定的图不是一棵树（可能不是无向图或不是连通的）";
                        }
                    }
                }
                return "指定的根节点不在节点集中";
            } else {
                return GraphUtil.getErrorMessage();
            }
        } catch (Exception e) {
            return "输入格式错误: " + e.getMessage();
        }
    }

    public Map<String, Object> generateRandomTree() {
        Map<String, Object> result = new HashMap<>();

        try {
            Random random = new Random();
            int nodeNumber = random.nextInt(8) + 3; // 3-10个节点

            DefaultGraph graph = GraphUtil.randomGenerateUndirectedTree(nodeNumber, false);

            String nodesString = GraphUtil.getFormatedNodeString(graph.getNodes());
            String edgesString = GraphUtil.getFormatedEdgeString(graph.getEdges());
            String rootString = graph.getNodes().get(0).getId();

            result.put("success", true);
            result.put("nodesString", nodesString);
            result.put("edgesString", edgesString);
            result.put("rootString", rootString);

            // 设置默认选项
            Map<String, Boolean> options = new HashMap<>();
            options.put("preorder", true);
            options.put("inorder", true);
            options.put("postorder", true);
            options.put("showAdjacencyMatrix", false);
            options.put("showIncidenceMatrix", false);
            options.put("showGraphVisualization", true);

            result.put("options", options);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "生成随机树失败: " + e.getMessage());
        }

        return result;
    }

    private TreeTraversalResponse.TraversalResult performPreorderTraversal(RootedTree tree) {
        TreeTraversalResponse.TraversalResult result = new TreeTraversalResponse.TraversalResult();

        List<RootedTreeNode> preorderNodes = tree.getPreorderNodeList();
        String traversalOrder = createLaTeXStringForTreeNodeList(preorderNodes);
        result.setTraversalOrder(traversalOrder);

        return result;
    }

    private TreeTraversalResponse.TraversalResult performInorderTraversal(RootedTree tree) {
        TreeTraversalResponse.TraversalResult result = new TreeTraversalResponse.TraversalResult();

        List<RootedTreeNode> inorderNodes = tree.getInorderNodeList();
        String traversalOrder = createLaTeXStringForTreeNodeList(inorderNodes);
        result.setTraversalOrder(traversalOrder);

        return result;
    }

    private TreeTraversalResponse.TraversalResult performPostorderTraversal(RootedTree tree) {
        TreeTraversalResponse.TraversalResult result = new TreeTraversalResponse.TraversalResult();

        List<RootedTreeNode> postorderNodes = tree.getPostorderNodeList();
        String traversalOrder = createLaTeXStringForTreeNodeList(postorderNodes);
        result.setTraversalOrder(traversalOrder);

        return result;
    }

    private String generateTreeImage(RootedTree tree) {
        try {
            // 检查Graphviz是否可用
            if (!GraphvizUtil.isGraphvizAvailable()) {
                System.err.println("Graphviz不可用，无法生成树形可视化");
                return null;
            }

            // 确保data目录存在
            java.io.File dataDir = new java.io.File("./data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }

            // 生成唯一文件名
            String uniqueId = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            String dotFileName = "./data/TREE_" + uniqueId + ".dot";
            String pngFileName = "./data/TREE_" + uniqueId + ".png";

            // 写入DOT文件
            try (java.io.PrintWriter writer = new java.io.PrintWriter(dotFileName, "UTF-8")) {
                tree.simplyWriteToDotFile(writer, false, false);
            }

            // 调试：打印DOT文件内容
            System.out.println("Generated DOT file: " + dotFileName);
            try (java.util.Scanner scanner = new java.util.Scanner(new java.io.File(dotFileName), "UTF-8")) {
                System.out.println("DOT file content:");
                int lineNum = 1;
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    System.out.println(lineNum + ": " + line);
                    lineNum++;
                }
            }

            // 生成PNG文件
            boolean success = GraphvizUtil.generatePNGFile(dotFileName, pngFileName, false);

            if (success && new java.io.File(pngFileName).exists()) {
                // 删除DOT文件，保留PNG文件
                new java.io.File(dotFileName).delete();
                System.out.println("Tree image generated successfully: " + pngFileName);
                return "/api/tree-traversal/tree-image/TREE_" + uniqueId + ".png";
            } else {
                // 清理失败的文件
                new java.io.File(dotFileName).delete();
                new java.io.File(pngFileName).delete();
                System.err.println("Graphviz生成失败: " + GraphvizUtil.errorMessage);
                return null;
            }

        } catch (Exception e) {
            System.err.println("生成树形可视化失败: " + e.getMessage());
            return null;
        }
    }

    /**
     * 创建节点列表的LaTeX字符串表示，用于遍历结果显示
     */
    private String createLaTeXStringForTreeNodeList(List<RootedTreeNode> nodeList) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("\\langle ");
        boolean isFirst = true;
        for (RootedTreeNode node : nodeList) {
            if (isFirst) {
                buffer.append(node.getLabel());
                isFirst = false;
            } else buffer.append(", " + node.getLabel());
        }
        buffer.append("\\rangle ");

        return buffer.toString();
    }
}