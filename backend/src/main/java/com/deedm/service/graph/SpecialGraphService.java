package com.deedm.service.graph;

import com.deedm.model.graph.SpecialGraphRequest;
import com.deedm.model.graph.SpecialGraphResponse;
import com.deedm.legacy.graph.*;
import com.deedm.legacy.util.GraphvizUtil;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Service
public class SpecialGraphService {

    public SpecialGraphResponse generateSpecialGraphs(SpecialGraphRequest request) {
        SpecialGraphResponse response = new SpecialGraphResponse();

        try {
            // 设置基本信息
            response.setType("special-graph");
            response.setN(request.getN());
            response.setM(request.getM());

            List<SpecialGraphResponse.GraphResult> graphResults = new ArrayList<>();

            // 生成完全图 K_n
            if (request.isShowComplete()) {
                SpecialGraphResponse.GraphResult result = generateCompleteGraph(request.getN());
                graphResults.add(result);
            }

            // 生成圈图 C_n
            if (request.isShowCycle()) {
                SpecialGraphResponse.GraphResult result = generateCycleGraph(request.getN());
                graphResults.add(result);
            }

            // 生成轮图 W_n
            if (request.isShowWheel()) {
                SpecialGraphResponse.GraphResult result = generateWheelGraph(request.getN());
                graphResults.add(result);
            }

            // 生成超立方体 Q_n
            if (request.isShowHypercube()) {
                SpecialGraphResponse.GraphResult result = generateHyperCubeGraph(request.getN());
                graphResults.add(result);
            }

            // 生成完全二分图 K_{n,m}
            if (request.isShowBigraph()) {
                SpecialGraphResponse.GraphResult result = generateCompleteBiGraph(request.getN(), request.getM());
                graphResults.add(result);
            }

            response.setGraphResults(graphResults);
            return response;

        } catch (Exception e) {
            response.setErrorMessage("生成特殊图时发生错误: " + e.getMessage());
            return response;
        }
    }

    private SpecialGraphResponse.GraphResult generateCompleteGraph(int n) {
        SpecialGraphResponse.GraphResult result = new SpecialGraphResponse.GraphResult(
            "complete", "K_" + n, "完全图"
        );

        try {
            if (n < 1) {
                result.setGenerated(false);
                return result;
            }

            AbstractGraph graph = GraphUtil.createCompleteGraph(n);
            String imageUrl = generateGraphImage(graph, "Complete");
            result.setImageUrl(imageUrl);

        } catch (Exception e) {
            System.err.println("生成完全图失败: " + e.getMessage());
            result.setGenerated(false);
        }

        return result;
    }

    private SpecialGraphResponse.GraphResult generateCycleGraph(int n) {
        SpecialGraphResponse.GraphResult result = new SpecialGraphResponse.GraphResult(
            "cycle", "C_" + n, "圈图"
        );

        try {
            if (n < 3) {
                result.setGenerated(false);
                return result;
            }

            AbstractGraph graph = GraphUtil.createCycleGraph(n);
            String imageUrl = generateGraphImage(graph, "Cycle");
            result.setImageUrl(imageUrl);

        } catch (Exception e) {
            System.err.println("生成圈图失败: " + e.getMessage());
            result.setGenerated(false);
        }

        return result;
    }

    private SpecialGraphResponse.GraphResult generateWheelGraph(int n) {
        SpecialGraphResponse.GraphResult result = new SpecialGraphResponse.GraphResult(
            "wheel", "W_" + n, "轮图"
        );

        try {
            if (n < 4) {
                result.setGenerated(false);
                return result;
            }

            AbstractGraph graph = GraphUtil.createWheelGraph(n);
            String imageUrl = generateGraphImage(graph, "Wheel");
            result.setImageUrl(imageUrl);

        } catch (Exception e) {
            System.err.println("生成轮图失败: " + e.getMessage());
            result.setGenerated(false);
        }

        return result;
    }

    private SpecialGraphResponse.GraphResult generateHyperCubeGraph(int n) {
        SpecialGraphResponse.GraphResult result = new SpecialGraphResponse.GraphResult(
            "hypercube", "Q_" + n, "超立方体图"
        );

        try {
            if (n < 1) {
                result.setGenerated(false);
                return result;
            }

            AbstractGraph graph = GraphUtil.createHyperCubeGraph(n);
            String imageUrl = generateGraphImage(graph, "Hypercube");
            result.setImageUrl(imageUrl);

        } catch (Exception e) {
            System.err.println("生成超立方体图失败: " + e.getMessage());
            result.setGenerated(false);
        }

        return result;
    }

    private SpecialGraphResponse.GraphResult generateCompleteBiGraph(int n, int m) {
        SpecialGraphResponse.GraphResult result = new SpecialGraphResponse.GraphResult(
            "bigraph", "K_{" + n + "," + m + "}", "完全二分图"
        );

        try {
            if (n < 1 || m < 1) {
                result.setGenerated(false);
                return result;
            }

            AbstractGraph graph = GraphUtil.createCompleteBiGraph(n, m);
            String imageUrl = generateGraphImage(graph, "Bigraph");
            result.setImageUrl(imageUrl);

        } catch (Exception e) {
            System.err.println("生成完全二分图失败: " + e.getMessage());
            result.setGenerated(false);
        }

        return result;
    }

    private String generateGraphImage(AbstractGraph graph, String prefix) {
        try {
            // 检查Graphviz是否可用
            if (!GraphvizUtil.isGraphvizAvailable()) {
                System.err.println("Graphviz不可用，无法生成图形可视化");
                return null;
            }

            // 生成唯一文件名
            String uniqueId = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            String dotFileName = "./data/Special_" + prefix + "_" + uniqueId + ".dot";
            String pngFileName = "./data/Special_" + prefix + "_" + uniqueId + ".png";

            // 写入DOT文件
            try (PrintWriter writer = new PrintWriter(dotFileName)) {
                graph.simplyWriteToDotFile(writer);
            }

            // 生成PNG文件
            boolean success = GraphvizUtil.generatePNGFile(dotFileName, pngFileName, false);

            if (success && new java.io.File(pngFileName).exists()) {
                // 删除DOT文件，保留PNG文件
                new java.io.File(dotFileName).delete();
                return "/api/special-graph/graph-image/Special_" + prefix + "_" + uniqueId + ".png";
            } else {
                // 清理失败的文件
                new java.io.File(dotFileName).delete();
                new java.io.File(pngFileName).delete();
                System.err.println("Graphviz生成失败: " + GraphvizUtil.errorMessage);
                return null;
            }

        } catch (Exception e) {
            System.err.println("生成特殊图图形可视化失败: " + e.getMessage());
            return null;
        }
    }
}