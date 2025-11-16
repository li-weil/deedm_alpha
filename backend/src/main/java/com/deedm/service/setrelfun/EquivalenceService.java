package com.deedm.service.setrelfun;

import com.deedm.model.setrelfun.EquivalenceRequest;
import com.deedm.model.setrelfun.EquivalenceResponse;
import com.deedm.legacy.setrelfun.*;
import com.deedm.legacy.util.GraphvizUtil;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.util.*;

@Service
public class EquivalenceService {

    public EquivalenceResponse analyzeEquivalenceRelation(EquivalenceRequest request) {
        EquivalenceResponse response = new EquivalenceResponse();

        try {
            // 解析集合A
            com.deedm.legacy.setrelfun.Set setA = parseSet(request.getSetAString(), request.isIntTypeElement());
            if (setA == null) {
                response.setErrorMessage("无法解析集合A: " + SetrelfunUtil.getExtractErrorMessage());
                return response;
            }

            // 解析关系R
            Relation relationR = SetrelfunUtil.extractRelation(setA, setA, request.getRelationRString(), request.isIntTypeElement());
            if (relationR == null) {
                response.setErrorMessage("无法解析关系R: " + SetrelfunUtil.getExtractErrorMessage());
                return response;
            }

            // 设置基本信息
            response.setType("equivalence-relation");
            response.setSetAString(request.getSetAString());
            response.setRelationRString(request.getRelationRString());
            response.setIntTypeElement(request.isIntTypeElement());
            response.setFormula("A = " + setA.toLaTeXString() + ", \\; R = " + relationR.toLaTeXString(request.isIntTypeElement()) + " \\subseteq A\\times A");

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
                    // 图形生成失败不影响其他功能
                    System.err.println("关系图生成失败: " + e.getMessage());
                }
            }

            // 分析关系性质
            analyzeRelationProperties(relationR, setA, request.isIntTypeElement(), response);

            // 计算等价关系闭包
            if (request.isCalculateEquivClosure()) {
                calculateEquivalenceClosure(relationR, request.isIntTypeElement(), response);
            }

            // 如果不是等价关系，跳过等价类和商集计算
            if (!response.isEquivalenceRelation()) {
                return response;
            }

            // 创建等价关系对象
            EquivalenceRelation equivalence = new EquivalenceRelation(relationR);

            // 计算等价类
            if (request.isCalculateEquivalenceClasses()) {
                calculateEquivalenceClasses(equivalence, setA, request.isIntTypeElement(), response);
            }

            // 计算商集
            if (request.isCalculateQuotientSet()) {
                calculateQuotientSet(equivalence, response);
            }

            return response;

        } catch (Exception e) {
            response.setErrorMessage("等价关系分析过程中发生错误: " + e.getMessage());
            return response;
        }
    }

    public String validateEquivalenceInput(String setAString, String relationRString, boolean intTypeElement) {
        try {
            com.deedm.legacy.setrelfun.Set setA = parseSet(setAString, intTypeElement);
            if (setA == null) {
                return "无法解析集合A: " + SetrelfunUtil.getExtractErrorMessage();
            }

            Relation relationR = SetrelfunUtil.extractRelation(setA, setA, relationRString, intTypeElement);
            if (relationR == null) {
                return "无法解析关系R: " + SetrelfunUtil.getExtractErrorMessage();
            }

            return null; // null表示验证通过
        } catch (Exception e) {
            return "输入格式错误: " + e.getMessage();
        }
    }

    public Map<String, Object> generateRandomEquivalenceRelation() {
        Map<String, Object> result = new HashMap<>();

        try {
            Random random = new Random();

            // 生成随机集合A
            char[] elements;
            boolean intTypeElement;

            if (random.nextBoolean()) {
                // 整数集合
                int size = random.nextInt(5) + 3; // 3-7个元素
                elements = new char[size];
                for (int i = 0; i < size; i++) {
                    elements[i] = (char) ('1' + i); // '1'到'7'
                }
                intTypeElement = true;
            } else {
                // 字符集合
                int size = random.nextInt(5) + 3; // 3-7个元素
                elements = new char[size];
                for (int i = 0; i < size; i++) {
                    elements[i] = (char) ('a' + i); // 'a'到'g'
                }
                intTypeElement = false;
            }

  com.deedm.legacy.setrelfun.Set setA = new com.deedm.legacy.setrelfun.Set(elements);
            String setAString = setA.toString();

            // 随机生成等价关系或一般关系
            Relation relationR;
            if (random.nextDouble() <= 0.6) {
                // 60%概率生成等价关系
                relationR = EquivalenceRelation.randomGenerateEquivalence(setA);
            } else {
                // 40%概率生成一般关系
                relationR = Relation.randomGenerate(setA, setA, 10);
            }

            String relationRString = relationR.toString();

            result.put("success", true);
            result.put("setAString", setAString);
            result.put("relationRString", relationRString);
            result.put("intTypeElement", intTypeElement);

            // 设置默认选项
            Map<String, Boolean> options = new HashMap<>();
            options.put("calculateEquivClosure", true);
            options.put("calculateEquivalenceClasses", true);
            options.put("calculateQuotientSet", true);
            options.put("showRelationMatrix", true);
            options.put("showRelationGraph", true);

            result.put("options", options);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "生成随机等价关系失败: " + e.getMessage());
        }

        return result;
    }

    private com.deedm.legacy.setrelfun.Set parseSet(String setString, boolean intTypeElement) {
        char[] elements = SetrelfunUtil.extractSetElements(setString, intTypeElement);
        return elements != null ? new com.deedm.legacy.setrelfun.Set(elements) : null;
    }

    private void analyzeRelationProperties(Relation relationR, com.deedm.legacy.setrelfun.Set setA, boolean intTypeElement, EquivalenceResponse response) {
        // 检查自反性
        boolean isReflexive = relationR.isReflexive();
        response.setReflexive(isReflexive);
        if (isReflexive) {
            String identityLaTeXString = Relation.getIdentity(setA).toLaTeXString();
            response.setReflexiveResult("R是自反关系，R\\subseteq \\Delta_A = " + identityLaTeXString);
        } else {
            OrderedPair exampleOne = Relation.getExampleOne();
            response.setReflexiveResult("R不是自反关系，" + exampleOne.toLaTeXString() + "\\not\\in R");
        }

        // 检查对称性
        boolean isSymmetric = relationR.isSymmetric();
        response.setSymmetric(isSymmetric);
        if (isSymmetric) {
            Relation Rinv = relationR.inverse();
            response.setSymmetricResult("R是对称关系，R = R^{-1} = " + Rinv.toLaTeXString());
        } else {
            OrderedPair exampleOne = Relation.getExampleOne();
            OrderedPair exampleTwo = Relation.getExampleTwo();
            response.setSymmetricResult("R不是对称关系，" + exampleOne.toLaTeXString() + "\\in R, 但" + exampleTwo.toLaTeXString() + "\\not\\in R");
        }

        // 检查传递性
        boolean isTransitive = relationR.isTransitive();
        response.setTransitive(isTransitive);
        if (isTransitive) {
            Relation result = relationR.composite(relationR);
            response.setTransitiveResult("R是传递关系，R \\circ R = " + result.toLaTeXString() + "\\subseteq R");
        } else {
            OrderedPair exampleOne = Relation.getExampleOne();
            OrderedPair exampleTwo = Relation.getExampleTwo();
            OrderedPair pair = new OrderedPair(exampleOne.getFirst(), exampleTwo.getSecond());
            response.setTransitiveResult("R不是传递关系，" + exampleOne.toLaTeXString() + "\\in R, " + exampleTwo.toLaTeXString() + "\\in R, 但" + pair.toLaTeXString() + "\\not\\in R");
        }

        // 判断是否为等价关系
        boolean isEquivalence = isReflexive && isSymmetric && isTransitive;
        response.setEquivalenceRelation(isEquivalence);
    }

    private void calculateEquivalenceClosure(Relation relationR, boolean intTypeElement, EquivalenceResponse response) {
        // 计算等价关系闭包（自反闭包 + 对称闭包 + 传递闭包）
        Relation result = relationR.reflexiveClosure();
        result = result.symmetricClosure();
        result = result.transitiveClosureByWarshallAlgorithm();

        response.setEquivalenceClosure(result.toLaTeXString(intTypeElement));

        // 生成闭包的关系矩阵
        Matrix matrix = result.getMatrix();
        response.setEquivalenceClosureMatrix(matrix.toLaTeXString());

        // 生成闭包的关系图
        try {
            String graphImageUrl = generateRelationGraph(result, intTypeElement, "RelationRequivclo");
            response.setEquivalenceClosureGraphUrl(graphImageUrl);
        } catch (Exception e) {
            System.err.println("等价闭包关系图生成失败: " + e.getMessage());
        }
    }

    private void calculateEquivalenceClasses(EquivalenceRelation equivalence, com.deedm.legacy.setrelfun.Set setA, boolean intTypeElement, EquivalenceResponse response) {
        List<EquivalenceResponse.EquivalenceClass> equivalenceClasses = new ArrayList<>();

        for (int i = 0; i < setA.length(); i++) {
            char element = setA.get(i);
            com.deedm.legacy.setrelfun.Set elementClass = equivalence.getEquivalenceClass(element);

            EquivalenceResponse.EquivalenceClass eqClass = new EquivalenceResponse.EquivalenceClass();
            // 修复字符编码问题：使用更安全的字符转换方法
            String elementString = getElementDisplayString(element, intTypeElement);
            eqClass.setElement(elementString);
            eqClass.setEquivalenceClassLaTeX(elementClass.toLaTeXString(intTypeElement));

            equivalenceClasses.add(eqClass);
        }

        response.setEquivalenceClasses(equivalenceClasses);
    }

    /**
     * 安全地将字符转换为显示字符串，避免编码问题
     */
    private String getElementDisplayString(char element, boolean isIntElement) {
        if (isIntElement) {
            // 对于整数类型元素，直接显示整数值
            return String.valueOf((int) element);
        } else {
            // 对于字符类型元素，使用安全的字符转换
            if (Character.isISOControl(element)) {
                // 控制字符显示为Unicode码点
                return String.format("\\u%04x", (int) element);
            } else {
                // 普通字符直接转换
                return Character.toString(element);
            }
        }
    }

    private void calculateQuotientSet(EquivalenceRelation equivalence, EquivalenceResponse response) {
        List<com.deedm.legacy.setrelfun.Set> quotient = equivalence.getQuotientSet();
        StringBuffer buffer = new StringBuffer();
        buffer.append("\\{ ");
        for (int i = 0; i < quotient.size(); i++) {
            com.deedm.legacy.setrelfun.Set set = quotient.get(i);
            if (i == 0) {
                buffer.append(set.toLaTeXString());
            } else {
                buffer.append(", " + set.toLaTeXString());
            }
        }
        buffer.append(" \\}");
        response.setQuotientSet(buffer.toString());
    }

    private String generateRelationGraph(Relation relationR, boolean intTypeElement, String prefix) {
        try {
            // 检查Graphviz是否可用
            if (!GraphvizUtil.isGraphvizAvailable()) {
                System.err.println("Graphviz不可用，无法生成关系图");
                return null;
            }

            // 生成唯一文件名
            String uniqueId = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            String dotFileName = "./data/" + prefix + "_" + uniqueId + ".dot";
            String pngFileName = "./data/" + prefix + "_" + uniqueId + ".png";

            // 写入DOT文件
            try (PrintWriter writer = new PrintWriter(dotFileName)) {
                com.deedm.legacy.graph.AbstractGraph graph = relationR.getRelationGraph(intTypeElement);
                graph.simplyWriteToDotFile(writer);
            }

            // 生成PNG文件
            boolean success = GraphvizUtil.generatePNGFile(dotFileName, pngFileName, false);

            if (success && new java.io.File(pngFileName).exists()) {
                // 删除DOT文件，保留PNG文件
                new java.io.File(dotFileName).delete();
                return "/api/equivalence/relation-image/" + prefix + "_" + uniqueId + ".png";
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
}