package com.deedm.service.setrelfun;

import com.deedm.model.setrelfun.PartialOrderRequest;
import com.deedm.model.setrelfun.PartialOrderResponse;
import com.deedm.legacy.setrelfun.*;
import com.deedm.legacy.graph.*;
import com.deedm.legacy.util.GraphvizUtil;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.util.*;

@Service
public class PartialOrderService {

    public PartialOrderResponse analyzePartialOrder(PartialOrderRequest request) {
        PartialOrderResponse response = new PartialOrderResponse();

        try {
            // 验证和创建集合A
            com.deedm.legacy.setrelfun.Set setA = createSetFromString(request.getSetAString(), request.isIntTypeElement());
            if (setA == null) {
                response.setErrorMessage("无法解析集合A: " + SetrelfunUtil.getExtractErrorMessage());
                return response;
            }

            // 验证和创建子集S（可选）
            com.deedm.legacy.setrelfun.Set setS = null;
            if (request.getSetSString() != null && !request.getSetSString().trim().isEmpty()) {
                setS = createSetFromString(request.getSetSString(), request.isIntTypeElement());
                if (setS == null) {
                    response.setErrorMessage("无法解析子集S: " + SetrelfunUtil.getExtractErrorMessage());
                    return response;
                }
                // 验证S是否是A的子集
                if (!setA.isSubset(setS)) {
                    response.setErrorMessage("子集S不是集合A的子集");
                    return response;
                }
            }

            // 创建关系R
            Relation relationR = createRelation(setA, request);
            if (relationR == null) {
                response.setErrorMessage("无法解析关系R: " + SetrelfunUtil.getExtractErrorMessage());
                return response;
            }

            // 设置基本信息
            response.setSetAString(request.getSetAString());
            response.setSetSString(request.getSetSString());
            response.setRelationRString(request.getRelationRString());
            response.setIntTypeElement(request.isIntTypeElement());
            response.setUseDivideRelation(request.isUseDivideRelation());
            response.setFormula("A = " + setA.toLaTeXString() + ", R = " + relationR.toLaTeXString(request.isIntTypeElement()));

            // 分析关系性质
            analyzeRelationProperties(relationR, response);

            // 如果不是偏序关系，返回
            if (!response.isPartialOrder()) {
                return response;
            }

            // 创建偏序关系对象
            PartialOrder partialOrder = new PartialOrder(relationR);

            // 生成关系矩阵
            if (request.isShowRelationMatrix()) {
                Matrix matrix = relationR.getMatrix();
                response.setRelationMatrix(matrix.toLaTeXString());
            }

            // 生成关系图
            if (request.isShowRelationGraph()) {
                try {
                    String graphImageUrl = generateRelationGraph(relationR, request.isIntTypeElement(), "RelationR");
                    response.setRelationGraphUrl(graphImageUrl);
                } catch (Exception e) {
                    System.err.println("生成关系图失败: " + e.getMessage());
                }
            }

            // 生成哈斯图
            if (request.isShowHasseDiagram()) {
                try {
                    String hasseImageUrl = generateHasseDiagram(partialOrder, request.isIntTypeElement(), "PartialOrder");
                    response.setHasseDiagramUrl(hasseImageUrl);
                } catch (Exception e) {
                    System.err.println("生成哈斯图失败: " + e.getMessage());
                }
            }

            // 计算极值元素（针对集合A）
            calculateExtremalElements(partialOrder, setA, request, response, request.isIntTypeElement());

            // 如果有子集S，计算界相关结果
            if (setS != null) {
                calculateBounds(partialOrder, setS, setA, request, response, request.isIntTypeElement());
            }

            return response;

        } catch (Exception e) {
            response.setErrorMessage("偏序关系分析过程中发生错误: " + e.getMessage());
            return response;
        }
    }

    public String validatePartialOrderInput(String setAString, String setSString, String relationRString,
                                          Boolean intTypeElement, Boolean useDivideRelation) {
        try {
            // 验证集合A
            com.deedm.legacy.setrelfun.Set setA = createSetFromString(setAString, intTypeElement);
            if (setA == null) {
                return "集合A格式不正确: " + SetrelfunUtil.getExtractErrorMessage();
            }

            // 验证子集S（如果存在）
            if (setSString != null && !setSString.trim().isEmpty()) {
                com.deedm.legacy.setrelfun.Set setS = createSetFromString(setSString, intTypeElement);
                if (setS == null) {
                    return "子集S格式不正确: " + SetrelfunUtil.getExtractErrorMessage();
                }
                if (!setA.isSubset(setS)) {
                    return "子集S不是集合A的子集";
                }
            }

            // 验证关系R（如果不使用整除关系）
            if (intTypeElement && useDivideRelation) {
                // 使用整除关系，无需验证relationRString
                return null;
            } else {
                if (relationRString == null || relationRString.trim().isEmpty()) {
                    return "关系R不能为空";
                }
                Relation relation = SetrelfunUtil.extractRelation(setA, setA, relationRString, intTypeElement);
                if (relation == null) {
                    return "关系R格式不正确: " + SetrelfunUtil.getExtractErrorMessage();
                }
            }

            return null; // null表示验证通过

        } catch (Exception e) {
            return "输入验证过程中发生错误: " + e.getMessage();
        }
    }

    public Map<String, Object> generateRandomPartialOrder() {
        Map<String, Object> result = new HashMap<>();

        try {
            Random random = new Random();
            boolean useIntElements = random.nextBoolean();
            boolean useDivideRelation = false;

            com.deedm.legacy.setrelfun.Set setA;
            Relation relationR;
            String setAString, relationRString;

            if (useIntElements) {
                // 整数元素
                int elementCount = random.nextInt(8) + 3; // 3-10个元素
                int[] elements = new int[elementCount];
                for (int i = 0; i < elementCount; i++) {
                    elements[i] = random.nextInt(20) + 1; // 1-20
                }

                // 使用集合去重
                java.util.Set<Integer> elementSet = new TreeSet<>();
                for (int element : elements) {
                    elementSet.add(element);
                }

                int[] intElements = new int[elementSet.size()];
                char[] charElements = new char[elementSet.size()];
                int index = 0;
                for (int element : elementSet) {
                    intElements[index] = element;
                    charElements[index] = (char) element;
                    index++;
                }

                setA = new com.deedm.legacy.setrelfun.Set(charElements);
                setAString = setA.toString();

                // 随机决定是否使用整除关系
                useDivideRelation = random.nextBoolean();

                if (useDivideRelation) {
                    relationR = PartialOrder.createDivisionOrder(intElements);
                    relationRString = relationR.toString();
                } else {
                    // 生成随机偏序关系
                    double probability = random.nextDouble();
                    if (probability <= 0.6) {
                        relationR = PartialOrder.randomGeneratePartialOrder(setA);
                    } else {
                        relationR = Relation.randomGenerate(setA, setA, 15);
                    }
                    relationRString = relationR.toString();
                }
            } else {
                // 字符元素
                char[] elements = new char[random.nextInt(6) + 3]; // 3-8个字符
                for (int i = 0; i < elements.length; i++) {
                    elements[i] = (char) ('a' + random.nextInt(26)); // a-z
                }

                setA = new com.deedm.legacy.setrelfun.Set(elements);
                setAString = setA.toString();

                // 生成随机偏序关系
                double probability = random.nextDouble();
                if (probability <= 0.6) {
                    relationR = PartialOrder.randomGeneratePartialOrder(setA);
                } else {
                    relationR = Relation.randomGenerate(setA, setA, 10);
                }
                relationRString = relationR.toString();
            }

            result.put("success", true);
            result.put("setA", setAString);
            result.put("setS", ""); // 不自动生成子集S
            result.put("relationR", relationRString);
            result.put("intTypeElement", useIntElements);
            result.put("useDivideRelation", useDivideRelation);

            // 设置默认选项
            Map<String, Boolean> options = new HashMap<>();
            options.put("showRelationMatrix", false);
            options.put("showRelationGraph", true);
            options.put("showHasseDiagram", true);
            options.put("calculateMinimum", true);
            options.put("calculateMaximum", true);
            options.put("calculateLeast", true);
            options.put("calculateGreatest", true);
            options.put("calculateLower", false);
            options.put("calculateUpper", false);
            options.put("calculateGLB", false);
            options.put("calculateLUB", false);

            result.put("options", options);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "生成随机偏序关系失败: " + e.getMessage());
        }

        return result;
    }

    // 辅助方法
    private com.deedm.legacy.setrelfun.Set createSetFromString(String setString, boolean intTypeElement) {
        if (setString == null || setString.trim().isEmpty()) {
            return null;
        }
        char[] elements = SetrelfunUtil.extractSetElements(setString.trim(), intTypeElement);
        return elements != null ? new com.deedm.legacy.setrelfun.Set(elements) : null;
    }

    private Relation createRelation(com.deedm.legacy.setrelfun.Set setA, PartialOrderRequest request) {
        if (request.isIntTypeElement() && request.isUseDivideRelation()) {
            return PartialOrder.createDivisionOrder(setA);
        } else {
            return SetrelfunUtil.extractRelation(setA, setA, request.getRelationRString(), request.isIntTypeElement());
        }
    }

    private void analyzeRelationProperties(Relation relation, PartialOrderResponse response) {
        boolean reflexive = relation.isReflexive();
        boolean antisymmetric = relation.isAntisymmetric();
        boolean transitive = relation.isTransitive();
        boolean isPartialOrder = reflexive && antisymmetric && transitive;

        response.setReflexive(reflexive);
        response.setAntisymmetric(antisymmetric);
        response.setTransitive(transitive);
        response.setPartialOrder(isPartialOrder);
    }

    private void calculateExtremalElements(PartialOrder partialOrder, com.deedm.legacy.setrelfun.Set setA, PartialOrderRequest request,
                                         PartialOrderResponse response, boolean isIntTypeElement) {
        if (request.isCalculateMinimum()) {
            char[] minElements = new char[setA.length()];
            int number = partialOrder.getMinimalElement(setA, minElements);
            if (number > 0) {
                response.setMinimumElements(com.deedm.legacy.setrelfun.Set.getElementsLabel(minElements, isIntTypeElement, number));
            }
        }

        if (request.isCalculateMaximum()) {
            char[] maxElements = new char[setA.length()];
            int number = partialOrder.getMaximalElement(setA, maxElements);
            if (number > 0) {
                response.setMaximumElements(com.deedm.legacy.setrelfun.Set.getElementsLabel(maxElements, isIntTypeElement, number));
            }
        }

        if (request.isCalculateLeast()) {
            if (partialOrder.hasLeastElement(setA)) {
                char element = partialOrder.getLeastElement(setA);
                response.setLeastElement(com.deedm.legacy.setrelfun.Set.getElementLabel(element, isIntTypeElement));
            }
        }

        if (request.isCalculateGreatest()) {
            if (partialOrder.hasGreatestElement(setA)) {
                char element = partialOrder.getGreatestElement(setA);
                response.setGreatestElement(com.deedm.legacy.setrelfun.Set.getElementLabel(element, isIntTypeElement));
            }
        }
    }

    private void calculateBounds(PartialOrder partialOrder, com.deedm.legacy.setrelfun.Set setS, com.deedm.legacy.setrelfun.Set setA, PartialOrderRequest request,
                               PartialOrderResponse response, boolean isIntTypeElement) {
        if (request.isCalculateLower()) {
            char[] lowerBounds = new char[setA.length()];
            int number = partialOrder.getLowerBound(setS, lowerBounds);
            if (number > 0) {
                response.setLowerBounds(com.deedm.legacy.setrelfun.Set.getElementsLabel(lowerBounds, isIntTypeElement, number));
            }
        }

        if (request.isCalculateUpper()) {
            char[] upperBounds = new char[setA.length()];
            int number = partialOrder.getUpperBound(setS, upperBounds);
            if (number > 0) {
                response.setUpperBounds(com.deedm.legacy.setrelfun.Set.getElementsLabel(upperBounds, isIntTypeElement, number));
            }
        }

        if (request.isCalculateGLB()) {
            if (partialOrder.hasGreatestLowerBound(setS)) {
                char element = partialOrder.getGreatestLowerBound(setS);
                response.setGreatestLowerBound(com.deedm.legacy.setrelfun.Set.getElementLabel(element, isIntTypeElement));
            }
        }

        if (request.isCalculateLUB()) {
            if (partialOrder.hasLeastUpperBound(setS)) {
                char element = partialOrder.getLeastUpperBound(setS);
                response.setLeastUpperBound(com.deedm.legacy.setrelfun.Set.getElementLabel(element, isIntTypeElement));
            }
        }
    }

    private String generateRelationGraph(Relation relation, boolean isIntTypeElement, String prefix) {
        if (!GraphvizUtil.isGraphvizAvailable()) {
            return null;
        }

        try {
            String uniqueId = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            String dotFileName = "./data/" + prefix + "_" + uniqueId + ".dot";
            String pngFileName = "./data/" + prefix + "_" + uniqueId + ".png";

            try (PrintWriter writer = new PrintWriter(dotFileName)) {
                AbstractGraph graph = relation.getRelationGraph(isIntTypeElement);
                graph.simplyWriteToDotFile(writer);
            }

            boolean success = GraphvizUtil.generatePNGFile(dotFileName, pngFileName, false);

            if (success && new java.io.File(pngFileName).exists()) {
                new java.io.File(dotFileName).delete();
                return "/api/partial-order/partial-order-image/" + prefix + "_" + uniqueId + ".png";
            } else {
                new java.io.File(dotFileName).delete();
                new java.io.File(pngFileName).delete();
                return null;
            }

        } catch (Exception e) {
            System.err.println("生成关系图失败: " + e.getMessage());
            return null;
        }
    }

    private String generateHasseDiagram(PartialOrder partialOrder, boolean isIntTypeElement, String prefix) {
        if (!GraphvizUtil.isGraphvizAvailable()) {
            return null;
        }

        try {
            String uniqueId = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            String dotFileName = "./data/" + prefix + "_" + uniqueId + ".dot";
            String pngFileName = "./data/" + prefix + "_" + uniqueId + ".png";

            try (PrintWriter writer = new PrintWriter(dotFileName)) {
                AbstractGraph graph = partialOrder.getHasseDigram(isIntTypeElement);
                graph.simplyWriteToDotFile(writer);
            }

            boolean success = GraphvizUtil.generatePNGFile(dotFileName, pngFileName, true);

            if (success && new java.io.File(pngFileName).exists()) {
                new java.io.File(dotFileName).delete();
                return "/api/partial-order/partial-order-image/" + prefix + "_" + uniqueId + ".png";
            } else {
                new java.io.File(dotFileName).delete();
                new java.io.File(pngFileName).delete();
                return null;
            }

        } catch (Exception e) {
            System.err.println("生成哈斯图失败: " + e.getMessage());
            return null;
        }
    }
}