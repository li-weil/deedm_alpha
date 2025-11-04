package com.deedm.service.setrelfun;

import com.deedm.model.setrelfun.RelationClosureRequest;
import com.deedm.model.setrelfun.RelationClosureResponse;
import com.deedm.legacy.setrelfun.*;
import com.deedm.legacy.util.GraphvizUtil;

import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.util.*;

@Service
public class RelationClosureService {

    public RelationClosureResponse calculateRelationClosures(RelationClosureRequest request) {
        RelationClosureResponse response = new RelationClosureResponse();

        try {
            // 解析集合A
            char[] elements = SetrelfunUtil.extractSetElements(request.getSetAString(), request.isIntTypeElement());
            if (elements == null) {
                response.setErrorMessage("集合A格式错误: " + SetrelfunUtil.getExtractErrorMessage());
                return response;
            }
            com.deedm.legacy.setrelfun.Set setA = new com.deedm.legacy.setrelfun.Set(elements);

            // 解析关系R
            Relation relationR = SetrelfunUtil.extractRelation(setA, setA, request.getRelationRString(), request.isIntTypeElement());
            if (relationR == null) {
                response.setErrorMessage("关系R格式错误: " + SetrelfunUtil.getExtractErrorMessage());
                return response;
            }

            // 设置基本信息
            response.setSetAString(request.getSetAString());
            response.setRelationRString(request.getRelationRString());
            response.setIntTypeElement(request.isIntTypeElement());
            response.setFormula("A = " + setA.toLaTeXString() + ", R = " + relationR.toLaTeXString(request.isIntTypeElement()) + " \\subseteq A\\times A");

            // 生成关系矩阵
            if (request.isShowRelationMatrix()) {
                Matrix matrix = relationR.getMatrix();
                response.setRelationMatrix(matrix.toLaTeXString());
            }

            // 生成关系图
            if (request.isShowRelationGraph()) {
                try {
                    String graphUrl = generateRelationGraph(relationR, "R", request.isIntTypeElement());
                    response.setRelationGraphUrl(graphUrl);
                } catch (Exception e) {
                    System.err.println("生成关系图失败: " + e.getMessage());
                }
            }

            // 计算自反闭包
            if (request.isCalculateReflexiveClosure()) {
                RelationClosureResponse.ClosureResult reflexiveResult = calculateReflexiveClosure(
                    relationR, request.isIntTypeElement(), request.isShowRelationMatrix(), request.isShowRelationGraph());
                response.setReflexiveClosureResult(reflexiveResult);
            }

            // 计算对称闭包
            if (request.isCalculateSymmetricClosure()) {
                RelationClosureResponse.ClosureResult symmetricResult = calculateSymmetricClosure(
                    relationR, request.isIntTypeElement(), request.isShowRelationMatrix(), request.isShowRelationGraph());
                response.setSymmetricClosureResult(symmetricResult);
            }

            // 计算传递闭包
            if (request.isCalculateTransitiveClosure()) {
                RelationClosureResponse.TransitiveClosureResult transitiveResult = calculateTransitiveClosure(
                    relationR, request.isIntTypeElement(), request.isUseWarshallAlgorithm(),
                    request.isShowTransitiveDetails(), request.isShowRelationMatrix(), request.isShowRelationGraph());
                response.setTransitiveClosureResult(transitiveResult);
            }

            // 计算等价闭包
            if (request.isCalculateEquivalenceClosure()) {
                RelationClosureResponse.ClosureResult equivalenceResult = calculateEquivalenceClosure(
                    relationR, request.isIntTypeElement(), request.isShowRelationMatrix(), request.isShowRelationGraph());
                response.setEquivalenceClosureResult(equivalenceResult);
            }

            return response;

        } catch (Exception e) {
            response.setErrorMessage("关系闭包计算过程中发生错误: " + e.getMessage());
            return response;
        }
    }

    public String validateRelationInput(String setAString, String relationRString, boolean intTypeElement) {
        try {
            // 解析集合A
            char[] elements = SetrelfunUtil.extractSetElements(setAString, intTypeElement);
            if (elements == null) {
                return "集合A格式错误: " + SetrelfunUtil.getExtractErrorMessage();
            }
            com.deedm.legacy.setrelfun.Set setA = new com.deedm.legacy.setrelfun.Set(elements);

            // 解析关系R
            Relation relationR = SetrelfunUtil.extractRelation(setA, setA, relationRString, intTypeElement);
            if (relationR == null) {
                return "关系R格式错误: " + SetrelfunUtil.getExtractErrorMessage();
            }

            return null; // null表示验证通过
        } catch (Exception e) {
            return "输入格式错误: " + e.getMessage();
        }
    }

    public Map<String, Object> generateRandomRelation() {
        Map<String, Object> result = new HashMap<>();

        try {
            Random random = new Random();

            // 生成随机集合A
            int setSize = random.nextInt(6) + 3; // 3-8个元素
            char[] elements = new char[setSize];
            for (int i = 0; i < setSize; i++) {
                elements[i] = (char) ('a' + i);
            }
            com.deedm.legacy.setrelfun.Set setA = new com.deedm.legacy.setrelfun.Set(elements);
            String setAString = setA.toString();

            // 生成随机关系R
            Relation relationR = Relation.randomGenerate(setA, setA, random.nextInt(8) + 3); // 3-10个关系对
            String relationRString = relationR.toString();

            boolean intTypeElement = false;

            result.put("success", true);
            result.put("setA", setAString);
            result.put("relationR", relationRString);
            result.put("intTypeElement", intTypeElement);

            // 设置默认选项
            Map<String, Boolean> options = new HashMap<>();
            options.put("calculateReflexiveClosure", true);
            options.put("calculateSymmetricClosure", true);
            options.put("calculateTransitiveClosure", false);
            options.put("useWarshallAlgorithm", false);
            options.put("showTransitiveDetails", false);
            options.put("calculateEquivalenceClosure", false);
            options.put("showRelationMatrix", false);
            options.put("showRelationGraph", false);

            result.put("options", options);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "生成随机关系失败: " + e.getMessage());
        }

        return result;
    }

    private RelationClosureResponse.ClosureResult calculateReflexiveClosure(Relation relation, boolean intTypeElement,
            boolean showMatrix, boolean showGraph) {
        RelationClosureResponse.ClosureResult result = new RelationClosureResponse.ClosureResult();

        try {
            Relation reflexiveRelation = relation.reflexiveClosure();
            result.setClosureLaTeX(reflexiveRelation.toLaTeXString(intTypeElement));

            if (showMatrix) {
                Matrix matrix = reflexiveRelation.getMatrix();
                result.setClosureMatrix(matrix.toLaTeXString());
            }

            if (showGraph) {
                try {
                    String graphUrl = generateRelationGraph(reflexiveRelation, "r(R)", intTypeElement);
                    result.setClosureGraphUrl(graphUrl);
                } catch (Exception e) {
                    System.err.println("生成自反闭包关系图失败: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("计算自反闭包失败: " + e.getMessage());
        }

        return result;
    }

    private RelationClosureResponse.ClosureResult calculateSymmetricClosure(Relation relation, boolean intTypeElement,
            boolean showMatrix, boolean showGraph) {
        RelationClosureResponse.ClosureResult result = new RelationClosureResponse.ClosureResult();

        try {
            Relation symmetricRelation = relation.symmetricClosure();
            result.setClosureLaTeX(symmetricRelation.toLaTeXString(intTypeElement));

            if (showMatrix) {
                Matrix matrix = symmetricRelation.getMatrix();
                result.setClosureMatrix(matrix.toLaTeXString());
            }

            if (showGraph) {
                try {
                    String graphUrl = generateRelationGraph(symmetricRelation, "s(R)", intTypeElement);
                    result.setClosureGraphUrl(graphUrl);
                } catch (Exception e) {
                    System.err.println("生成对称闭包关系图失败: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("计算对称闭包失败: " + e.getMessage());
        }

        return result;
    }

    private RelationClosureResponse.TransitiveClosureResult calculateTransitiveClosure(Relation relation, boolean intTypeElement,
            boolean useWarshallAlgorithm, boolean showDetails, boolean showMatrix, boolean showGraph) {
        RelationClosureResponse.TransitiveClosureResult result = new RelationClosureResponse.TransitiveClosureResult();

        try {
            Relation transitiveRelation;
            String algorithm;

            if (useWarshallAlgorithm) {
                transitiveRelation = relation.transitiveClosureByWarshallAlgorithm();
                algorithm = "Warshall算法";
            } else {
                transitiveRelation = relation.transitiveClosureByComposition();
                algorithm = "矩阵逻辑乘法";
            }

            result.setAlgorithmUsed(algorithm);
            result.setClosureLaTeX(transitiveRelation.toLaTeXString(intTypeElement));

            if (showMatrix) {
                Matrix matrix = transitiveRelation.getMatrix();
                result.setClosureMatrix(matrix.toLaTeXString());
            }

            if (showDetails) {
                List<String> detailMatrices = new ArrayList<>();
                List<String> detailDescriptions = new ArrayList<>();

                Matrix[] matrices = Relation.getTransitiveClosureMatrixes();
                for (int i = 0; i < matrices.length; i++) {
                    if (useWarshallAlgorithm) {
                        detailMatrices.add("W_{" + i + "} = " + matrices[i].toLaTeXString());
                        detailDescriptions.add("Warshall算法第" + i + "步");
                    } else {
                        detailMatrices.add("M_R^{[" + (i+1) + "]} = " + matrices[i].toLaTeXString());
                        detailDescriptions.add("关系的" + (i+1) + "次幂");
                    }
                }

                result.setDetailMatrices(detailMatrices);
                result.setDetailDescriptions(detailDescriptions);
            }

            if (showGraph) {
                try {
                    String graphUrl = generateRelationGraph(transitiveRelation, "t(R)", intTypeElement);
                    result.setClosureGraphUrl(graphUrl);
                } catch (Exception e) {
                    System.err.println("生成传递闭包关系图失败: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("计算传递闭包失败: " + e.getMessage());
        }

        return result;
    }

    private RelationClosureResponse.ClosureResult calculateEquivalenceClosure(Relation relation, boolean intTypeElement,
            boolean showMatrix, boolean showGraph) {
        RelationClosureResponse.ClosureResult result = new RelationClosureResponse.ClosureResult();

        try {
            // 等价闭包 = 自反闭包 + 对称闭包 + 传递闭包(Warshall算法)
            Relation equivalenceRelation = relation.reflexiveClosure();
            equivalenceRelation = equivalenceRelation.symmetricClosure();
            equivalenceRelation = equivalenceRelation.transitiveClosureByWarshallAlgorithm();

            result.setClosureLaTeX(equivalenceRelation.toLaTeXString(intTypeElement));

            if (showMatrix) {
                Matrix matrix = equivalenceRelation.getMatrix();
                result.setClosureMatrix(matrix.toLaTeXString());
            }

            if (showGraph) {
                try {
                    String graphUrl = generateRelationGraph(equivalenceRelation, "tsr(R)", intTypeElement);
                    result.setClosureGraphUrl(graphUrl);
                } catch (Exception e) {
                    System.err.println("生成等价闭包关系图失败: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("计算等价闭包失败: " + e.getMessage());
        }

        return result;
    }

    private String generateRelationGraph(Relation relation, String relationName, boolean intTypeElement) {
        try {
            // 检查Graphviz是否可用
            if (!GraphvizUtil.isGraphvizAvailable()) {
                System.err.println("Graphviz不可用，无法生成关系图");
                return null;
            }

            // 生成唯一文件名
            String uniqueId = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            String dotFileName = "./data/Relation_" + uniqueId + ".dot";
            String pngFileName = "./data/Relation_" + uniqueId + ".png";

            // 写入DOT文件
            try (PrintWriter writer = new PrintWriter(dotFileName)) {
                writeRelationToDotFile(relation, relationName, intTypeElement, writer);
            }

            // 生成PNG文件
            boolean success = GraphvizUtil.generatePNGFile(dotFileName, pngFileName, false);

            if (success && new java.io.File(pngFileName).exists()) {
                // 删除DOT文件，保留PNG文件
                new java.io.File(dotFileName).delete();
                return "/api/relation-closure/relation-image/Relation_" + uniqueId + ".png";
            } else {
                // 清理失败的文件
                new java.io.File(dotFileName).delete();
                new java.io.File(pngFileName).delete();
                System.err.println("Graphviz生成失败: " + GraphvizUtil.errorMessage);
                return null;
            }

        } catch (Exception e) {
            System.err.println("生成关系图失败: " + e.getMessage());
            return null;
        }
    }

    private void writeRelationToDotFile(Relation relation, String relationName, boolean intTypeElement, PrintWriter writer) {
        writer.println("digraph Relation {");
        writer.println("  rankdir=TB;");
        writer.println("  node [shape=circle, style=filled, fillcolor=lightblue];");
        writer.println("  label=\"" + relationName + "\";");
        writer.println("  labelloc=t;");

        com.deedm.legacy.setrelfun.Set setA = relation.getFromSet();
        char[] elements = setA.toCharArray();

        // 写入节点
        for (char element : elements) {
            if (intTypeElement) {
                writer.println("  \"" + element + "\" [label=\"" + element + "\"];");
            } else {
                writer.println("  \"" + element + "\" [label=\"" + element + "\"];");
            }
        }

        writer.println();

        // 写入关系边
        OrderedPair[] pairs = relation.getPairs();
        for (OrderedPair pair : pairs) {
            char first = pair.getFirst();
            char second = pair.getSecond();
            String start = intTypeElement ? String.valueOf((int)first) : String.valueOf(first);
            String end = intTypeElement ? String.valueOf((int)second) : String.valueOf(second);
            writer.println("  \"" + start + "\" -> \"" + end + "\";");
        }

        writer.println("}");
    }
}