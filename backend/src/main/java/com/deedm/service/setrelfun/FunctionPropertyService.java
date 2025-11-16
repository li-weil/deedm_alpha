package com.deedm.service.setrelfun;

import com.deedm.model.setrelfun.FunctionPropertyRequest;
import com.deedm.model.setrelfun.FunctionPropertyResponse;
import com.deedm.legacy.setrelfun.*;
import com.deedm.legacy.graph.*;
import com.deedm.legacy.util.GraphvizUtil;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.File;
import java.util.UUID;

@Service
public class FunctionPropertyService {

    public FunctionPropertyResponse analyzeFunction(FunctionPropertyRequest request) {
        FunctionPropertyResponse response = new FunctionPropertyResponse();

        try {
            // 提取集合A
            Set setA = null;
            if (request.getSetAString() != null && !request.getSetAString().trim().isEmpty()) {
                char[] setAElements = SetrelfunUtil.extractSetElements(request.getSetAString(), request.isIntTypeElement());
                if (setAElements == null) {
                    response.setErrorMessage("输入集合A的格式不正确: " + SetrelfunUtil.getExtractErrorMessage());
                    return response;
                }
                setA = new Set(setAElements);
                response.setSetALaTeX(setA.toLaTeXString());
            }

            // 提取集合B
            Set setB = null;
            if (request.getSetBString() != null && !request.getSetBString().trim().isEmpty()) {
                char[] setBElements = SetrelfunUtil.extractSetElements(request.getSetBString(), request.isIntTypeElement());
                if (setBElements == null) {
                    response.setErrorMessage("输入集合B的格式不正确: " + SetrelfunUtil.getExtractErrorMessage());
                    return response;
                }
                setB = new Set(setBElements);
                response.setSetBLaTeX(setB.toLaTeXString());
            }

            // 提取函数关系F
            Relation functionRelation = null;
            if (request.getFunctionString() != null && !request.getFunctionString().trim().isEmpty()) {
                functionRelation = SetrelfunUtil.extractRelation(setA, setB, request.getFunctionString(), request.isIntTypeElement());
                if (functionRelation == null) {
                    response.setErrorMessage("输入函数F的格式不正确: " + SetrelfunUtil.getExtractErrorMessage());
                    return response;
                }
                response.setFunctionLaTeX(functionRelation.toLaTeXString(request.isIntTypeElement()));
            }

            // 生成LaTeX公式
            StringBuilder formulaBuilder = new StringBuilder();
            if (setA != null) {
                formulaBuilder.append("A = ").append(setA.toLaTeXString()).append(", ");
            }
            if (setB != null) {
                formulaBuilder.append("B = ").append(setB.toLaTeXString()).append(", ");
            }
            if (functionRelation != null) {
                formulaBuilder.append("F = ").append(functionRelation.toLaTeXString(request.isIntTypeElement()))
                           .append(" \\subseteq A\\times B");
            }
            response.setFormula(formulaBuilder.toString());

            // 判断是否为函数
            boolean isFunction = functionRelation != null && functionRelation.isFunction();
            response.setFunction(isFunction);

            if (!isFunction) {
                // 如果不是函数，返回
                return response;
            }

            // 创建Function对象进行性质分析
            Function function = new Function(functionRelation);

            // 分析单射性
            if (request.isCheckInjection()) {
                boolean isInjection = function.isInjection();
                FunctionPropertyResponse.FunctionProperty injectionResult = new FunctionPropertyResponse.FunctionProperty(isInjection);

                if (isInjection) {
                    injectionResult.setDescription("函数F是单射（入射）");
                } else {
                    char counterExample = Function.getExample();
                    String exampleLabel = Set.getElementLabel(counterExample, request.isIntTypeElement());
                    injectionResult.setCounterExample(exampleLabel);
                    injectionResult.setDescription("函数F不是单射（入射），元素 " + exampleLabel + " 有多个原像");
                }

                response.setInjectionResult(injectionResult);
            }

            // 分析满射性
            if (request.isCheckSurjection()) {
                boolean isSurjection = function.isSurjection();
                FunctionPropertyResponse.FunctionProperty surjectionResult = new FunctionPropertyResponse.FunctionProperty(isSurjection);

                if (isSurjection) {
                    surjectionResult.setDescription("函数F是满射");
                } else {
                    char counterExample = Function.getExample();
                    String exampleLabel = Set.getElementLabel(counterExample, request.isIntTypeElement());
                    surjectionResult.setCounterExample(exampleLabel);
                    surjectionResult.setDescription("函数F不是满射，元素 " + exampleLabel + " 没有原像");
                }

                response.setSurjectionResult(surjectionResult);
            }

            // 分析双射性
            if (request.isCheckBijection()) {
                boolean isBijection = function.isBijection();
                FunctionPropertyResponse.FunctionProperty bijectionResult = new FunctionPropertyResponse.FunctionProperty(isBijection);

                if (isBijection) {
                    bijectionResult.setDescription("函数F是双射");
                } else {
                    char counterExample = Function.getExample();
                    String exampleLabel = Set.getElementLabel(counterExample, request.isIntTypeElement());
                    bijectionResult.setCounterExample(exampleLabel);
                    bijectionResult.setDescription("函数F不是双射，元素 " + exampleLabel + " 没有唯一的原像");
                }

                response.setBijectionResult(bijectionResult);
            }

            // 生成关系矩阵
            if (request.isShowRelationMatrix() && functionRelation != null) {
                Matrix matrix = functionRelation.getMatrix();
                response.setRelationMatrix(matrix.toLaTeXString());
            }

            // 生成关系图
            if (request.isShowRelationGraph() && functionRelation != null) {
                try {
                    String graphUrl = generateRelationGraph(functionRelation, request.isIntTypeElement());
                    response.setRelationGraphUrl(graphUrl);
                } catch (Exception e) {
                    // 图形生成失败不影响其他功能
                    System.err.println("关系图生成失败: " + e.getMessage());
                }
            }

            return response;

        } catch (Exception e) {
            response.setErrorMessage("函数性质分析过程中发生错误: " + e.getMessage());
            return response;
        }
    }

    public String validateFunctionInput(String setAString, String setBString, String functionString, boolean intTypeElement) {
        try {
            // 验证集合A
            Set setA = null;
            if (setAString != null && !setAString.trim().isEmpty()) {
                char[] setAElements = SetrelfunUtil.extractSetElements(setAString, intTypeElement);
                if (setAElements == null) {
                    return "集合A格式错误: " + SetrelfunUtil.getExtractErrorMessage();
                }
                setA = new Set(setAElements);
            }

            // 验证集合B
            Set setB = null;
            if (setBString != null && !setBString.trim().isEmpty()) {
                char[] setBElements = SetrelfunUtil.extractSetElements(setBString, intTypeElement);
                if (setBElements == null) {
                    return "集合B格式错误: " + SetrelfunUtil.getExtractErrorMessage();
                }
                setB = new Set(setBElements);
            }

            // 验证函数关系
            if (functionString != null && !functionString.trim().isEmpty()) {
                Relation functionRelation = SetrelfunUtil.extractRelation(setA, setB, functionString, intTypeElement);
                if (functionRelation == null) {
                    return "函数F格式错误: " + SetrelfunUtil.getExtractErrorMessage();
                }
            }

            return null; // null表示验证通过

        } catch (Exception e) {
            return "输入格式错误: " + e.getMessage();
        }
    }

    private String generateRelationGraph(Relation relation, boolean intTypeElement) {
        try {
            // 检查Graphviz是否可用
            if (!GraphvizUtil.isGraphvizAvailable()) {
                System.err.println("Graphviz不可用，无法生成关系图");
                return null;
            }

            // 生成唯一文件名
            String uniqueId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            String dotFileName = "./data/FunctionF_" + uniqueId + ".dot";
            String pngFileName = "./data/FunctionF_" + uniqueId + ".png";

            // 写入DOT文件
            try (PrintWriter writer = new PrintWriter(dotFileName)) {
                AbstractGraph graph = relation.getRelationGraph(intTypeElement);
                graph.simplyWriteToDotFile(writer);
            }

            // 生成PNG文件
            boolean success = GraphvizUtil.generatePNGFile(dotFileName, pngFileName, false);

            if (success && new File(pngFileName).exists()) {
                // 删除DOT文件，保留PNG文件
                new File(dotFileName).delete();
                return "/api/function-property/function-image/FunctionF_" + uniqueId + ".png";
            } else {
                // 清理失败的文件
                new File(dotFileName).delete();
                new File(pngFileName).delete();
                System.err.println("Graphviz生成失败: " + GraphvizUtil.errorMessage);
                return null;
            }

        } catch (Exception e) {
            System.err.println("生成关系图失败: " + e.getMessage());
            return null;
        }
    }
}