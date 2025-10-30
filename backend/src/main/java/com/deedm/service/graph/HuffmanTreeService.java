package com.deedm.service.graph;

import com.deedm.model.graph.HuffmanTreeRequest;
import com.deedm.model.graph.HuffmanTreeResponse;
import com.deedm.legacy.graph.*;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

@Service
public class HuffmanTreeService {

    public HuffmanTreeResponse constructHuffmanTree(HuffmanTreeRequest request) {
        HuffmanTreeResponse response = new HuffmanTreeResponse();

        try {
            String leafString = request.getLeafString();
            if (leafString == null || leafString.trim().isEmpty()) {
                response.setSuccess(false);
                response.setErrorMessage("带权叶节点集合不能为空");
                return response;
            }

            // 解析带权叶节点
            List<GraphNode> nodeList = new ArrayList<>();
            boolean success = HuffmanTree.extractWeightedLeafsFromFormatedString(leafString, nodeList);

            if (!success) {
                response.setSuccess(false);
                response.setErrorMessage("带权叶节点集合格式错误: " + GraphUtil.getErrorMessage());
                return response;
            }

            // 生成LaTeX字符串
            String leafSetLaTeX = HuffmanTree.getWeightedLeafLaTeXString(nodeList);
            response.setLeafSetLaTeX(leafSetLaTeX);
            response.setFormula("L = " + leafSetLaTeX);

            if (!request.isExecuteHuffmanAlgorithm()) {
                response.setSuccess(true);
                return response;
            }

            // 创建Huffman树
            WeightedTreeNode[] leafs = new WeightedTreeNode[nodeList.size()];
            for (int i = 0; i < nodeList.size(); i++) {
                leafs[i] = (WeightedTreeNode) nodeList.get(i);
            }

            HuffmanTree huffmanTree = HuffmanTree.createHuffmanTree(leafs);

            // 处理算法步骤
            if (request.isShowAlgorithmDetails()) {
                List<HuffmanTreeResponse.HuffmanStep> steps = processAlgorithmSteps();
                response.setSteps(steps);
            } else {
                // 生成最终树图片
                String treeImageUrl = generateTreeImage(huffmanTree, false);
                response.setTreeImageUrl(treeImageUrl);
            }

            // 计算总权值
            double totalWeight = huffmanTree.getTotalWeight();
            response.setTotalWeight(totalWeight);
            String totalWeightLaTeX = huffmanTree.getTotalWeightCalculateLaTeXString();
            response.setTotalWeightLaTeX(totalWeightLaTeX);

            // 处理叶节点编码
            if (request.isShowLeafCodes()) {
                Map<String, String> leafCodes = processLeafCodes(leafs, huffmanTree);
                response.setLeafCodes(leafCodes);
            }

            response.setSuccess(true);

        } catch (Exception e) {
            response.setSuccess(false);
            response.setErrorMessage("构造Huffman树时发生错误: " + e.getMessage());
        }

        return response;
    }

    public String validateInput(String leafString) {
        if (leafString == null || leafString.trim().isEmpty()) {
            return "带权叶节点集合不能为空";
        }

        List<GraphNode> nodeList = new ArrayList<>();
        boolean success = HuffmanTree.extractWeightedLeafsFromFormatedString(leafString, nodeList);

        if (!success) {
            return "带权叶节点集合格式错误: " + GraphUtil.getErrorMessage();
        }

        return null; // null 表示验证通过
    }

    public Map<String, Object> generateRandomInputs() {
        try {
            Random random = new Random();
            int nodeNumber = random.nextInt(8) + 5; // 5-12个节点
            List<GraphNode> nodeList = HuffmanTree.randomGenerateWeightedLeafNodes(nodeNumber, 100);

            String leafString = HuffmanTree.getWeightedLeafFormatedString(nodeList);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("leafString", leafString);
            result.put("executeHuffmanAlgorithm", true);
            result.put("showAlgorithmDetails", true);
            result.put("showLeafCodes", true);

            return result;
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "message", "生成随机输入时发生错误: " + e.getMessage()
            );
        }
    }

    private List<HuffmanTreeResponse.HuffmanStep> processAlgorithmSteps() {
        List<HuffmanTreeResponse.HuffmanStep> steps = new ArrayList<>();

        try {
            HuffmanTree.HuffmanRecorder recorder = HuffmanTree.getHuffmanRecorder();
            List<HuffmanTree.HuffmanStepRecorder> stepList = recorder.getStepList();

            for (int i = 0; i < stepList.size(); i++) {
                HuffmanTree.HuffmanStepRecorder stepRecorder = stepList.get(i);
                int step = stepRecorder.getStep();
                String nodeString = stepRecorder.getLaTeXStringOfNodeArray();
                RootedForest forest = stepRecorder.getRootedForest();

                boolean labelEdge = (i == stepList.size() - 1);
                String prompt = "当前得到的森林: ";
                if (i == stepList.size() - 1) {
                    prompt = "最终的构造结果: ";
                }

                // 生成森林图片
                String dotFileName = "./data/Huffman" + step + ".dot";
                String pngFileName = "./data/Huffman" + step + ".png";

                try (PrintWriter writer = new PrintWriter(dotFileName)) {
                    forest.simplyWriteToDotFile(writer, false, labelEdge);

                    // 使用GraphViz生成图片（这里简化处理）
                    boolean imageSuccess = generatePNGFile(dotFileName, pngFileName, true);
                    String forestImageUrl = null;
                    if (imageSuccess) {
                        forestImageUrl = "/api/graph-travel/graph-image/Huffman" + step + ".png";
                    }

                    steps.add(new HuffmanTreeResponse.HuffmanStep(step, nodeString, forestImageUrl, prompt));
                } catch (Exception e) {
                    System.err.println("生成步骤图片失败: " + e.getMessage());
                    steps.add(new HuffmanTreeResponse.HuffmanStep(step, nodeString, null, prompt));
                }
            }
        } catch (Exception e) {
            System.err.println("处理算法步骤时发生错误: " + e.getMessage());
        }

        return steps;
    }

    private String generateTreeImage(HuffmanTree huffmanTree, boolean labelEdge) {
        try {
            String dotFileName = "./data/HuffmanTree.dot";
            String pngFileName = "./data/HuffmanTree.png";

            try (PrintWriter writer = new PrintWriter(dotFileName)) {
                huffmanTree.simplyWriteToDotFile(writer, false, labelEdge);

                boolean success = generatePNGFile(dotFileName, pngFileName, true);
                if (success) {
                    return "/api/graph-travel/graph-image/HuffmanTree.png";
                }
            }
        } catch (Exception e) {
            System.err.println("生成Huffman树图片失败: " + e.getMessage());
        }
        return null;
    }

    private Map<String, String> processLeafCodes(WeightedTreeNode[] leafs, HuffmanTree huffmanTree) {
        Map<String, String> leafCodes = new LinkedHashMap<>();
        try {
            String[] codes = huffmanTree.getCodeOfLeafNodes(leafs);
            for (int i = 0; i < codes.length; i++) {
                leafCodes.put(leafs[i].getLabel(), codes[i]);
            }
        } catch (Exception e) {
            System.err.println("处理叶节点编码时发生错误: " + e.getMessage());
        }
        return leafCodes;
    }

    private boolean generatePNGFile(String dotFileName, String pngFileName, boolean removeDotFile) {
        try {
            // 这里需要调用GraphViz的dot命令来生成图片
            ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", dotFileName, "-o", pngFileName);
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (removeDotFile) {
                new File(dotFileName).delete();
            }

            return exitCode == 0;
        } catch (Exception e) {
            System.err.println("生成PNG文件失败: " + e.getMessage());
            return false;
        }
    }
}