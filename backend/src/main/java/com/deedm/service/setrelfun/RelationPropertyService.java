package com.deedm.service.setrelfun;

import com.deedm.model.setrelfun.RelationPropertyRequest;
import com.deedm.model.setrelfun.RelationPropertyResponse;
import com.deedm.legacy.setrelfun.*;
import com.deedm.legacy.util.GraphvizUtil;
import com.deedm.legacy.graph.AbstractGraph;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.util.UUID;

@Service
public class RelationPropertyService {

    public RelationPropertyResponse analyzeRelationProperties(RelationPropertyRequest request) {
        RelationPropertyResponse response = new RelationPropertyResponse();

        try {
            // 解析集合A
            char[] elements = SetrelfunUtil.extractSetElements(request.getSetAString(), request.isIntTypeElement());
            if (elements == null) {
                response.setErrorMessage("字符串 " + request.getSetAString() + " 不是合法的表示集合的字符串！错误信息：" + SetrelfunUtil.getExtractErrorMessage());
                return response;
            }
            Set setA = new Set(elements);

            // 解析关系R
            Relation relationR = SetrelfunUtil.extractRelation(setA, setA, request.getRelationRString(), request.isIntTypeElement());
            if (relationR == null) {
                response.setErrorMessage("字符串 " + request.getRelationRString() + " 不是合法的表示关系的字符串！错误信息：" + SetrelfunUtil.getExtractErrorMessage());
                return response;
            }

            // 设置基本信息
            response.setType("relation-property");
            response.setSetAString(request.getSetAString());
            response.setRelationRString(request.getRelationRString());
            response.setIntTypeElement(request.isIntTypeElement());
            response.setFormula("A = " + setA.toLaTeXString() + ", R = " + relationR.toLaTeXString(request.isIntTypeElement()) + " \\subseteq A\\times A");

            // 生成关系矩阵
            if (request.isUseMatrix()) {
                Matrix matrix = relationR.getMatrix();
                response.setMatrixString("M_R = " + matrix.toLaTeXString());
            }

            // 生成关系图
            if (request.isUseGraph()) {
                try {
                    String graphImageUrl = generateRelationGraph(relationR, request.isIntTypeElement());
                    response.setGraphImageUrl(graphImageUrl);
                } catch (Exception e) {
                    // 图形生成失败不影响其他功能
                    System.err.println("关系图生成失败: " + e.getMessage());
                }
            }

            // 检查自反性
            if (request.isCheckReflexive()) {
                RelationPropertyResponse.ReflexiveResult reflexiveResult = checkReflexive(relationR, setA);
                response.setReflexiveResult(reflexiveResult);
            }

            // 检查反自反性
            if (request.isCheckIrreflexive()) {
                RelationPropertyResponse.ReflexiveResult irreflexiveResult = checkIrreflexive(relationR, setA);
                response.setIrreflexiveResult(irreflexiveResult);
            }

            // 检查对称性
            if (request.isCheckSymmetric()) {
                RelationPropertyResponse.SymmetricResult symmetricResult = checkSymmetric(relationR, request.isIntTypeElement());
                response.setSymmetricResult(symmetricResult);
            }

            // 检查反对称性
            if (request.isCheckAntisymmetric()) {
                RelationPropertyResponse.SymmetricResult antisymmetricResult = checkAntisymmetric(relationR, setA, request.isIntTypeElement());
                response.setAntisymmetricResult(antisymmetricResult);
            }

            // 检查传递性
            if (request.isCheckTransitive()) {
                RelationPropertyResponse.TransitiveResult transitiveResult = checkTransitive(relationR, request.isIntTypeElement());
                response.setTransitiveResult(transitiveResult);
            }

            return response;

        } catch (Exception e) {
            response.setErrorMessage("关系性质分析过程中发生错误: " + e.getMessage());
            return response;
        }
    }

    public String validateRelationInput(String setAString, String relationRString, boolean intTypeElement) {
        try {
            // 验证集合A
            char[] elements = SetrelfunUtil.extractSetElements(setAString, intTypeElement);
            if (elements == null) {
                return "字符串 " + setAString + " 不是合法的表示集合的字符串！错误信息：" + SetrelfunUtil.getExtractErrorMessage();
            }
            Set setA = new Set(elements);

            // 验证关系R
            Relation relationR = SetrelfunUtil.extractRelation(setA, setA, relationRString, intTypeElement);
            if (relationR == null) {
                return "字符串 " + relationRString + " 不是合法的表示关系的字符串！错误信息：" + SetrelfunUtil.getExtractErrorMessage();
            }

            return null; // null表示验证通过
        } catch (Exception e) {
            return "输入格式错误: " + e.getMessage();
        }
    }

    public RelationPropertyResponse generateRandomRelation() {
        RelationPropertyResponse response = new RelationPropertyResponse();

        try {
            // 生成随机集合
            char[] elements = {'0', '1', '2', '3', '4', '5', '6'};
            Set setA = new Set(elements);

            // 生成随机关系
            Relation relationR = Relation.randomGenerate(setA, setA, 10);

            response.setType("relation-property");
            response.setSetAString(setA.toString());
            response.setRelationRString(relationR.toString());
            response.setIntTypeElement(false);
            response.setSuccess(true);

            // 设置默认选项
            response.setSuccess(true);

        } catch (Exception e) {
            response.setErrorMessage("生成随机关系失败: " + e.getMessage());
        }

        return response;
    }

    private RelationPropertyResponse.ReflexiveResult checkReflexive(Relation relationR, Set setA) {
        RelationPropertyResponse.ReflexiveResult result = new RelationPropertyResponse.ReflexiveResult();

        boolean hasProperty = relationR.isReflexive();
        result.setReflexive(hasProperty);

        String identityLaTeXString = Relation.getIdentity(setA).toLaTeXString();

        if (hasProperty) {
            result.setExplanation("关系R是自反关系，R\\subseteq \\Delta_A = " + identityLaTeXString);
        } else {
            OrderedPair exampleOne = Relation.getExampleOne();
            result.setExplanation("关系R不是自反关系");
            result.setCounterExample(exampleOne.toLaTeXString() + "\\not\\in R");
        }

        return result;
    }

    private RelationPropertyResponse.ReflexiveResult checkIrreflexive(Relation relationR, Set setA) {
        RelationPropertyResponse.ReflexiveResult result = new RelationPropertyResponse.ReflexiveResult();

        boolean hasProperty = relationR.isIrreflexive();
        result.setReflexive(hasProperty); // 复用字段表示是否具有该性质

        if (hasProperty) {
            result.setExplanation("关系R是反自反关系，R\\cap \\Delta_A = \\varnothing");
        } else {
            OrderedPair exampleOne = Relation.getExampleOne();
            result.setExplanation("关系R不是反自反关系");
            result.setCounterExample(exampleOne.toLaTeXString() + "\\in R");
        }

        return result;
    }

    private RelationPropertyResponse.SymmetricResult checkSymmetric(Relation relationR, boolean intTypeElement) {
        RelationPropertyResponse.SymmetricResult result = new RelationPropertyResponse.SymmetricResult();

        boolean hasProperty = relationR.isSymmetric();
        result.setSymmetric(hasProperty);

        if (hasProperty) {
            Relation Rinv = relationR.inverse();
            result.setExplanation("关系R是对称关系，R = R^{-1} = " + Rinv.toLaTeXString(intTypeElement));
        } else {
            OrderedPair exampleOne = Relation.getExampleOne();
            OrderedPair exampleTwo = Relation.getExampleTwo();
            result.setExplanation("关系R不是对称关系");
            result.setCounterExample1(exampleOne.toLaTeXString() + "\\in R");
            result.setCounterExample2(exampleTwo.toLaTeXString() + "\\not\\in R");
        }

        return result;
    }

    private RelationPropertyResponse.SymmetricResult checkAntisymmetric(Relation relationR, Set setA, boolean intTypeElement) {
        RelationPropertyResponse.SymmetricResult result = new RelationPropertyResponse.SymmetricResult();

        boolean hasProperty = relationR.isAntisymmetric();
        result.setSymmetric(hasProperty); // 复用字段表示是否具有该性质

        String identityLaTeXString = Relation.getIdentity(setA).toLaTeXString();

        if (hasProperty) {
            Relation Rinv = relationR.inverse();
            Relation resultRelation = relationR.intersection(Rinv);
            result.setExplanation("关系R是反对称关系，R \\cap R^{-1} = " + resultRelation.toLaTeXString(intTypeElement) + "\\subseteq \\Delta_A = " + identityLaTeXString);
        } else {
            OrderedPair exampleOne = Relation.getExampleOne();
            OrderedPair exampleTwo = Relation.getExampleTwo();
            result.setExplanation("关系R不是反对称关系");
            result.setCounterExample1(exampleOne.toLaTeXString() + "\\in R");
            result.setCounterExample2(exampleTwo.toLaTeXString() + "\\in R");
        }

        return result;
    }

    private RelationPropertyResponse.TransitiveResult checkTransitive(Relation relationR, boolean intTypeElement) {
        RelationPropertyResponse.TransitiveResult result = new RelationPropertyResponse.TransitiveResult();

        boolean hasProperty = relationR.isTransitive();
        result.setTransitive(hasProperty);

        if (hasProperty) {
            Relation resultRelation = relationR.composite(relationR);
            result.setExplanation("关系R是传递关系，R \\circ R = " + resultRelation.toLaTeXString(intTypeElement) + "\\subseteq R");
        } else {
            OrderedPair exampleOne = Relation.getExampleOne();
            OrderedPair exampleTwo = Relation.getExampleTwo();
            OrderedPair pair = new OrderedPair(exampleOne.getFirst(), exampleTwo.getSecond());
            result.setExplanation("关系R不是传递关系");
            result.setCounterExample1(exampleOne.toLaTeXString() + "\\in R");
            result.setCounterExample2(exampleTwo.toLaTeXString() + "\\in R");
            result.setCounterExample3(pair.toLaTeXString() + "\\not\\in R");
        }

        return result;
    }

    private String generateRelationGraph(Relation relationR, boolean intTypeElement) {
        try {
            // 检查Graphviz是否可用
            if (!GraphvizUtil.isGraphvizAvailable()) {
                System.err.println("Graphviz不可用，无法生成关系图");
                return null;
            }

            // 生成唯一文件名
            String uniqueId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            String dotFileName = "./data/RelationR_" + uniqueId + ".dot";
            String pngFileName = "./data/RelationR_" + uniqueId + ".png";

            // 写入DOT文件
            try (PrintWriter writer = new PrintWriter(dotFileName)) {
                AbstractGraph graph = relationR.getRelationGraph(intTypeElement);
                graph.simplyWriteToDotFile(writer);
            }

            // 生成PNG文件
            boolean success = GraphvizUtil.generatePNGFile(dotFileName, pngFileName, false);

            if (success && new java.io.File(pngFileName).exists()) {
                // 删除DOT文件，保留PNG文件
                new java.io.File(dotFileName).delete();
                return "/api/relation-property/relation-image/RelationR_" + uniqueId + ".png";
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