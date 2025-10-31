package com.deedm.service.setrelfun;

import com.deedm.model.setrelfun.SetOperationRequest;
import com.deedm.model.setrelfun.SetOperationResponse;
import com.deedm.legacy.setrelfun.Set;
import com.deedm.legacy.setrelfun.SetrelfunUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class SetOperationService {

    public SetOperationResponse performSetOperations(SetOperationRequest request) {
        SetOperationResponse response = new SetOperationResponse();

        try {
            // 解析集合
            Set setU = parseSet(request.getSetU(), request.isIntTypeElement());
            if (setU == null) {
                response.setErrorMessage("全集U格式不正确: " + request.getSetU());
                return response;
            }

            Set setA = parseSet(request.getSetA(), request.isIntTypeElement());
            if (setA == null) {
                response.setErrorMessage("集合A格式不正确: " + request.getSetA());
                return response;
            }

            Set setB = parseSet(request.getSetB(), request.isIntTypeElement());
            if (setB == null) {
                response.setErrorMessage("集合B格式不正确: " + request.getSetB());
                return response;
            }

            // 验证A和B都是U的子集
            if (!setU.isSubset(setA)) {
                response.setErrorMessage("集合A不是全集U的子集");
                return response;
            }

            if (!setU.isSubset(setB)) {
                response.setErrorMessage("集合B不是全集U的子集");
                return response;
            }

            // 设置基本信息
            response.setType("set-operation");
            response.setSetU(setU.toString());
            response.setSetA(setA.toString());
            response.setSetB(setB.toString());
            response.setFormula("U = " + setU.toLaTeXString() + ", A = " + setA.toLaTeXString() + ", B = " + setB.toLaTeXString());

            // 执行各种集合运算
            if (request.isCalculateIntersection()) {
                Set result = setA.intersection(setB);
                result = setU.getSubsetForCharFunction(setU.getCharFunctionForSubset(result));
                response.setIntersectionResult("A \\cap B = " + result.toLaTeXString());
            }

            if (request.isCalculateUnion()) {
                Set result = setA.union(setB);
                result = setU.getSubsetForCharFunction(setU.getCharFunctionForSubset(result));
                response.setUnionResult("A \\cup B = " + result.toLaTeXString());
            }

            if (request.isCalculateSubtract()) {
                Set result = setA.subtract(setB);
                result = setU.getSubsetForCharFunction(setU.getCharFunctionForSubset(result));
                response.setSubtractResult("A - B = " + result.toLaTeXString());
            }

            if (request.isCalculateComplement()) {
                Set resultA = setU.subtract(setA);
                resultA = setU.getSubsetForCharFunction(setU.getCharFunctionForSubset(resultA));
                response.setComplementAResult("\\overline{A} = " + resultA.toLaTeXString());

                Set resultB = setU.subtract(setB);
                resultB = setU.getSubsetForCharFunction(setU.getCharFunctionForSubset(resultB));
                response.setComplementBResult("\\overline{B} = " + resultB.toLaTeXString());
            }

            if (request.isCalculateDifference()) {
                Set result = setA.symmetricDifference(setB);
                result = setU.getSubsetForCharFunction(setU.getCharFunctionForSubset(result));
                response.setDifferenceResult("A \\oplus B = " + result.toLaTeXString());
            }

            if (request.isCalculatePower()) {
                Set[] powerSetA = setA.powerSet();
                List<String> powerSetAList = new ArrayList<>();
                for (Set subset : powerSetA) {
                    powerSetAList.add(subset.toLaTeXString());
                }
                response.setPowerSetAResult(powerSetAList);

                Set[] powerSetB = setB.powerSet();
                List<String> powerSetBList = new ArrayList<>();
                for (Set subset : powerSetB) {
                    powerSetBList.add(subset.toLaTeXString());
                }
                response.setPowerSetBResult(powerSetBList);
            }

            return response;

        } catch (Exception e) {
            response.setErrorMessage("集合运算过程中发生错误: " + e.getMessage());
            return response;
        }
    }

    public Map<String, Object> generateRandomSets() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 生成全集U，包含0-9的数字
            char[] elements = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
            Set setU = new Set(elements);

            // 生成A和B作为U的随机子集
            Set setA = Set.randomGenerateSubset(setU);
            Set setB = Set.randomGenerateSubset(setU);

            result.put("success", true);
            result.put("setU", setU.toString());
            result.put("setA", setA.toString());
            result.put("setB", setB.toString());
            result.put("intTypeElement", true);

            // 设置默认选项
            Map<String, Boolean> options = new HashMap<>();
            options.put("calculateIntersection", true);
            options.put("calculateUnion", true);
            options.put("calculateSubtract", true);
            options.put("calculateComplement", true);
            options.put("calculateDifference", true);
            options.put("calculatePower", false);

            result.put("options", options);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "生成随机集合失败: " + e.getMessage());
        }

        return result;
    }

    public Map<String, Object> validateSetInput(String setUString, String setAString, String setBString, boolean intTypeElement) {
        Map<String, Object> result = new HashMap<>();

        try {
            Set setU = parseSet(setUString, intTypeElement);
            if (setU == null) {
                result.put("valid", false);
                result.put("message", "全集U格式不正确: " + setUString);
                return result;
            }

            Set setA = parseSet(setAString, intTypeElement);
            if (setA == null) {
                result.put("valid", false);
                result.put("message", "集合A格式不正确: " + setAString);
                return result;
            }

            Set setB = parseSet(setBString, intTypeElement);
            if (setB == null) {
                result.put("valid", false);
                result.put("message", "集合B格式不正确: " + setBString);
                return result;
            }

            // 验证子集关系
            if (!setU.isSubset(setA)) {
                result.put("valid", false);
                result.put("message", "集合A不是全集U的子集");
                return result;
            }

            if (!setU.isSubset(setB)) {
                result.put("valid", false);
                result.put("message", "集合B不是全集U的子集");
                return result;
            }

            result.put("valid", true);
            result.put("message", "集合输入格式正确，可以进行运算");

        } catch (Exception e) {
            result.put("valid", false);
            result.put("message", "验证过程中发生错误: " + e.getMessage());
        }

        return result;
    }

    private Set parseSet(String setString, boolean intTypeElement) {
        if (setString == null || setString.trim().isEmpty()) {
            return null;
        }

        char[] elements = SetrelfunUtil.extractSetElements(setString.trim(), intTypeElement);
        return elements != null ? new Set(elements) : null;
    }
}