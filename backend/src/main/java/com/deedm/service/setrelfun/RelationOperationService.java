package com.deedm.service.setrelfun;

import com.deedm.model.setrelfun.RelationOperationRequest;
import com.deedm.model.setrelfun.RelationOperationResponse;
import com.deedm.legacy.setrelfun.Set;
import com.deedm.legacy.setrelfun.Relation;
import com.deedm.legacy.setrelfun.Matrix;
import com.deedm.legacy.setrelfun.SetrelfunUtil;
import com.deedm.legacy.util.GraphvizUtil;
import com.deedm.legacy.graph.AbstractGraph;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.io.PrintWriter;
import java.io.File;

@Service
public class RelationOperationService {

    private int counter = 0;

    public RelationOperationResponse executeRelationOperation(RelationOperationRequest request) {
        RelationOperationResponse response = new RelationOperationResponse();

        try {
            // 验证输入
            String validationError = validateInput(request);
            if (validationError != null) {
                response.setSuccess(false);
                response.setErrorMessage(validationError);
                return response;
            }

            // 解析集合
            Set setA = parseSet(request.getSetA(), request.isIntTypeElement());
            Set setB = parseSet(request.getSetB(), request.isIntTypeElement());
            Set setC = parseSet(request.getSetC(), request.isIntTypeElement());

            // 确定关系R的源集合和目标集合
            Set rFromSet = getSetByIndex(setA, setB, setC, request.getRelationRFromSet());
            Set rToSet = getSetByIndex(setA, setB, setC, request.getRelationRToSet());

            if (rFromSet == null || rToSet == null) {
                response.setSuccess(false);
                response.setErrorMessage("关系R的源集合或目标集合未定义");
                return response;
            }

            // 确定关系S的源集合和目标集合
            Set sFromSet = getSetByIndex(setA, setB, setC, request.getRelationSFromSet());
            Set sToSet = getSetByIndex(setA, setB, setC, request.getRelationSToSet());

            if (sFromSet == null || sToSet == null) {
                response.setSuccess(false);
                response.setErrorMessage("关系S的源集合或目标集合未定义");
                return response;
            }

            // 解析关系
            Relation relationR = SetrelfunUtil.extractRelation(rFromSet, rToSet, request.getRelationR(), request.isIntTypeElement());
            Relation relationS = SetrelfunUtil.extractRelation(sFromSet, sToSet, request.getRelationS(), request.isIntTypeElement());

            if (relationR == null || relationS == null) {
                response.setSuccess(false);
                response.setErrorMessage("关系格式不正确");
                return response;
            }

            // 执行关系运算
            counter++;
            response.setSuccess(true);
            response.setResultType("relationOperation");
            response.setType("relationOperation");
            response.setMessage("关系运算完成");
            response.setIndex(counter);

            // 设置集合和关系的LaTeX表示
            if (setA != null) {
                response.setSetA(setA.toLaTeXString());
            }
            if (setB != null) {
                response.setSetB(setB.toLaTeXString());
            }
            if (setC != null) {
                response.setSetC(setC.toLaTeXString());
            }

            response.setRelationR(relationR.toString());
            response.setRelationS(relationS.toString());
            response.setRelationRLatex(relationR.toLaTeXString(request.isIntTypeElement()));
            response.setRelationSLatex(relationS.toLaTeXString(request.isIntTypeElement()));

            // 生成关系矩阵
            if (request.isUseMatrix()) {
                Matrix matrixR = relationR.getMatrix();
                Matrix matrixS = relationS.getMatrix();
                response.setRelationRMatrix(matrixR.toLaTeXString());
                response.setRelationSMatrix(matrixS.toLaTeXString());
            }

            // 生成关系图
            if (request.isUseGraph()) {
                try {
                    String relationRGraphUrl = generateRelationGraph(relationR, "RelationR_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8), request.isIntTypeElement());
                    String relationSGraphUrl = generateRelationGraph(relationS, "RelationS_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8), request.isIntTypeElement());
                    response.setRelationRGraphImageUrl(relationRGraphUrl);
                    response.setRelationSGraphImageUrl(relationSGraphUrl);
                } catch (Exception e) {
                    System.err.println("关系图生成失败: " + e.getMessage());
                    // 图形生成失败不影响其他功能
                }
            }

            // 执行各种运算
            performRelationOperations(relationR, relationS, rFromSet, rToSet, sFromSet, sToSet, request, response);

            // 构建公式字符串
            StringBuilder formulaBuilder = new StringBuilder();
            formulaBuilder.append("R = ").append(relationR.toLaTeXString(request.isIntTypeElement()));
            formulaBuilder.append(", S = ").append(relationS.toLaTeXString(request.isIntTypeElement()));
            response.setFormula(formulaBuilder.toString());

        } catch (Exception e) {
            response.setSuccess(false);
            response.setErrorMessage("运算过程中发生错误: " + e.getMessage());
        }

        return response;
    }

    public Map<String, Object> validateRelationOperation(RelationOperationRequest request) {
        Map<String, Object> result = new HashMap<>();

        try {
            String validationError = validateInput(request);
            if (validationError != null) {
                result.put("valid", false);
                result.put("message", validationError);
                return result;
            }

            // 验证集合格式
            Set setA = parseSet(request.getSetA(), request.isIntTypeElement());
            Set setB = parseSet(request.getSetB(), request.isIntTypeElement());
            Set setC = parseSet(request.getSetC(), request.isIntTypeElement());

            // 验证关系格式
            Set rFromSet = getSetByIndex(setA, setB, setC, request.getRelationRFromSet());
            Set rToSet = getSetByIndex(setA, setB, setC, request.getRelationRToSet());
            Set sFromSet = getSetByIndex(setA, setB, setC, request.getRelationSFromSet());
            Set sToSet = getSetByIndex(setA, setB, setC, request.getRelationSToSet());

            if (rFromSet == null || rToSet == null || sFromSet == null || sToSet == null) {
                result.put("valid", false);
                result.put("message", "关系的源集合或目标集合未定义");
                return result;
            }

            Relation relationR = SetrelfunUtil.extractRelation(rFromSet, rToSet, request.getRelationR(), request.isIntTypeElement());
            Relation relationS = SetrelfunUtil.extractRelation(sFromSet, sToSet, request.getRelationS(), request.isIntTypeElement());

            if (relationR == null || relationS == null) {
                result.put("valid", false);
                result.put("message", "关系格式不正确");
                return result;
            }

            result.put("valid", true);
            result.put("message", "输入格式正确，可以进行运算");

        } catch (Exception e) {
            result.put("valid", false);
            result.put("message", "验证过程中发生错误: " + e.getMessage());
        }

        return result;
    }

    public Map<String, Object> generateRandomRelations() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 生成默认集合
            char[] elements = {'0', '1', '2', '3', '4', '5', '6'};
            Set setA = new Set(elements);
            Set setB = Set.randomGenerateSubset(setA);
            Set setC = Set.randomGenerateSubset(setA);

            // 生成默认关系 - 确保两个关系有相同的源集合和目标集合，以便进行交、并、差运算
            Relation relationR = Relation.randomGenerate(setA, setB, 8);
            Relation relationS = Relation.randomGenerate(setA, setB, 8);

            result.put("success", true);
            result.put("message", "随机关系生成完成");
            result.put("setA", setA.toString());
            result.put("setB", setB.toString());
            result.put("setC", setC.toString());
            result.put("relationR", relationR.toString());
            result.put("relationS", relationS.toString());
            result.put("relationRFromSet", 0); // A
            result.put("relationRToSet", 1);   // B
            result.put("relationSFromSet", 0); // A (改为相同源集合)
            result.put("relationSToSet", 1);   // B (改为相同目标集合)
            result.put("intTypeElement", false);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "生成随机关系失败: " + e.getMessage());
        }

        return result;
    }

    private String validateInput(RelationOperationRequest request) {
        if (request.getRelationR() == null || request.getRelationR().trim().isEmpty()) {
            return "关系R不能为空";
        }

        if (request.getRelationS() == null || request.getRelationS().trim().isEmpty()) {
            return "关系S不能为空";
        }

        return null;
    }

    private Set parseSet(String setString, boolean isIntType) {
        try {
            if (setString == null || setString.trim().isEmpty()) {
                return null;
            }
            char[] elements = SetrelfunUtil.extractSetElements(setString, isIntType);
            if (elements == null) {
                return null;
            }
            return new Set(elements);
        } catch (Exception e) {
            return null;
        }
    }

    private Set getSetByIndex(Set setA, Set setB, Set setC, int index) {
        switch (index) {
            case 0:
                return setA;
            case 1:
                return setB;
            case 2:
                return setC;
            default:
                return null;
        }
    }

    private void performRelationOperations(Relation relationR, Relation relationS,
                                        Set rFromSet, Set rToSet, Set sFromSet, Set sToSet,
                                        RelationOperationRequest request, RelationOperationResponse response) {
        // 关系交运算
        if (request.isIntersection()) {
            if (rFromSet.equals(sFromSet) && rToSet.equals(sToSet)) {
                Relation result = relationR.intersection(relationS);
                response.setIntersectionResult(result.toLaTeXString(request.isIntTypeElement()));
            }
        }

        // 关系并运算
        if (request.isUnion()) {
            if (rFromSet.equals(sFromSet) && rToSet.equals(sToSet)) {
                Relation result = relationR.union(relationS);
                response.setUnionResult(result.toLaTeXString(request.isIntTypeElement()));
            }
        }

        // 关系差运算
        if (request.isSubtract()) {
            if (rFromSet.equals(sFromSet) && rToSet.equals(sToSet)) {
                Relation result = relationR.subtract(relationS);
                response.setSubtractResult(result.toLaTeXString(request.isIntTypeElement()));
            }
        }

        // 关系逆运算
        if (request.isInverse()) {
            Relation invR = relationR.inverse();
            Relation invS = relationS.inverse();
            response.setInverseRResult(invR.toLaTeXString(request.isIntTypeElement()));
            response.setInverseSResult(invS.toLaTeXString(request.isIntTypeElement()));
        }

        // 关系复合运算
        if (request.isComposite()) {
            StringBuilder compositeResult = new StringBuilder();

            if (rToSet.equals(sFromSet)) {
                Relation result = relationR.composite(relationS);
                compositeResult.append("S∘R = ").append(result.toLaTeXString(request.isIntTypeElement()));
            }

            if (sToSet.equals(rFromSet)) {
                if (compositeResult.length() > 0) {
                    compositeResult.append(", ");
                }
                Relation result = relationS.composite(relationR);
                compositeResult.append("R∘S = ").append(result.toLaTeXString(request.isIntTypeElement()));
            }

            if (compositeResult.length() > 0) {
                response.setCompositeResult(compositeResult.toString());
            }
        }

        // 逆的复合运算
        if (request.isInvcomp()) {
            StringBuilder invcompResult = new StringBuilder();

            if (rToSet.equals(sFromSet)) {
                Relation invR = relationR.inverse();
                Relation invS = relationS.inverse();
                Relation result = invS.composite(invR);
                invcompResult.append("R⁻¹∘S⁻¹ = ").append(result.toLaTeXString(request.isIntTypeElement()));
            }

            if (sToSet.equals(rFromSet)) {
                if (invcompResult.length() > 0) {
                    invcompResult.append(", ");
                }
                Relation invR = relationR.inverse();
                Relation invS = relationS.inverse();
                Relation result = invR.composite(invS);
                invcompResult.append("S⁻¹∘R⁻¹ = ").append(result.toLaTeXString(request.isIntTypeElement()));
            }

            if (invcompResult.length() > 0) {
                response.setInvcompResult(invcompResult.toString());
            }
        }
    }

    private String generateRelationGraph(Relation relation, String uniqueId, boolean intTypeElement) {
        try {
            // 检查Graphviz是否可用
            if (!GraphvizUtil.isGraphvizAvailable()) {
                System.err.println("Graphviz不可用，无法生成关系图");
                return null;
            }

            // 生成DOT文件名和PNG文件名
            String dotFileName = "./data/" + uniqueId + ".dot";
            String pngFileName = "./data/" + uniqueId + ".png";

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
                return "/api/setrelfun/relation-operation/relation-image/" + uniqueId + ".png";
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