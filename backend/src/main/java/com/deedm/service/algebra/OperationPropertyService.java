package com.deedm.service.algebra;

import com.deedm.model.algebra.OperationPropertyRequest;
import com.deedm.model.algebra.OperationPropertyResponse;
import com.deedm.legacy.algebra.BinaryOperator;
import com.deedm.legacy.setrelfun.Set;
import com.deedm.legacy.setrelfun.SetrelfunUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OperationPropertyService {

    public OperationPropertyResponse analyzeOperationProperties(OperationPropertyRequest request) {
        OperationPropertyResponse response = new OperationPropertyResponse();

        try {
            // 解析集合A
            Set setA = parseSet(request.getSetAString(), request.isIntTypeElement());
            if (setA == null) {
                response.setErrorMessage("无法解析集合A: " + SetrelfunUtil.getExtractErrorMessage());
                return response;
            }

            // 设置基本信息
            response.setType("operation-property");
            response.setSetAString(request.getSetAString());
            response.setOperator1String(request.getOperator1String());
            response.setOperator2String(request.getOperator2String());
            response.setIntTypeElement(request.isIntTypeElement());
            response.setFormula("A = " + setA.toLaTeXString());

            // 创建基础集合数组
            Character[] baseSet = new Character[setA.length()];
            for (int i = 0; i < setA.length(); i++) {
                baseSet[i] = new Character(setA.get(i));
            }

            // 解析第一个运算符
            BinaryOperator<Character> operator1 = BinaryOperator.extractBinaryOperator(
                baseSet, request.getOperator1String(), request.isIntTypeElement());
            if (operator1 == null) {
                response.setErrorMessage("无法解析运算符一: " + BinaryOperator.getReasonMessage());
                return response;
            }
            operator1.setName("\\circ");
            response.setOperator1Table(operator1.toLaTeXString());

            // 解析第二个运算符（可选）
            BinaryOperator<Character> operator2 = null;
            if (request.getOperator2String() != null && !request.getOperator2String().trim().isEmpty()) {
                operator2 = BinaryOperator.extractBinaryOperator(
                    baseSet, request.getOperator2String(), request.isIntTypeElement());
                if (operator2 == null) {
                    response.setErrorMessage("无法解析运算符二: " + BinaryOperator.getReasonMessage());
                    return response;
                }
                operator2.setName("*");
                response.setOperator2Table(operator2.toLaTeXString());
            }

            // 分析运算符1的性质
            List<OperationPropertyResponse.PropertyResult> operator1Properties = analyzeOperatorProperties(
                operator1, request, setA, request.isIntTypeElement());
            response.setOperator1Properties(operator1Properties);

            // 分析运算符2的性质（如果存在）
            if (operator2 != null) {
                List<OperationPropertyResponse.PropertyResult> operator2Properties = analyzeOperatorProperties(
                    operator2, request, setA, request.isIntTypeElement());
                response.setOperator2Properties(operator2Properties);

                // 分析运算符之间的关系性质
                List<OperationPropertyResponse.RelationProperty> relationProperties = analyzeRelationProperties(
                    operator1, operator2, request);
                response.setRelationProperties(relationProperties);
            }

            return response;

        } catch (Exception e) {
            response.setErrorMessage("运算性质分析过程中发生错误: " + e.getMessage());
            return response;
        }
    }

    public String validateOperationInput(String setAString, String operator1String, String operator2String, boolean intTypeElement) {
        try {
            Set setA = parseSet(setAString, intTypeElement);
            if (setA == null) {
                return "集合A格式错误: " + SetrelfunUtil.getExtractErrorMessage();
            }

            Character[] baseSet = new Character[setA.length()];
            for (int i = 0; i < setA.length(); i++) {
                baseSet[i] = new Character(setA.get(i));
            }

            BinaryOperator<Character> operator1 = BinaryOperator.extractBinaryOperator(
                baseSet, operator1String, intTypeElement);
            if (operator1 == null) {
                return "运算符一格式错误: " + BinaryOperator.getReasonMessage();
            }

            if (operator2String != null && !operator2String.trim().isEmpty()) {
                BinaryOperator<Character> operator2 = BinaryOperator.extractBinaryOperator(
                    baseSet, operator2String, intTypeElement);
                if (operator2 == null) {
                    return "运算符二格式错误: " + BinaryOperator.getReasonMessage();
                }
            }

            return null; // null表示验证通过
        } catch (Exception e) {
            return "输入验证错误: " + e.getMessage();
        }
    }

    public OperationPropertyResponse generateRandomOperation() {
        OperationPropertyResponse response = new OperationPropertyResponse();

        try {
            // 生成随机集合
            char[] elements = {'1', '2', '3', '4', '5'};
            Set setA = new Set(elements);
            String setAString = setA.toString();

            // 生成随机运算符
            Character[] baseSet = new Character[setA.length()];
            for (int i = 0; i < setA.length(); i++) {
                baseSet[i] = new Character(setA.get(i));
            }

            String operator1String = generateRandomOperatorString(setA);
            String operator2String = generateRandomOperatorString(setA);

            // 设置返回结果
            response.setSuccess(true);
            response.setSetAString(setAString);
            response.setOperator1String(operator1String);
            response.setOperator2String(operator2String);
            response.setIntTypeElement(false);

            // 设置默认选项
            response.setOperator1Properties(new ArrayList<>());
            response.setOperator2Properties(new ArrayList<>());

        } catch (Exception e) {
            response.setSuccess(false);
            response.setErrorMessage("生成随机运算失败: " + e.getMessage());
        }

        return response;
    }

    private Set parseSet(String setString, boolean intTypeElement) {
        char[] elements = SetrelfunUtil.extractSetElements(setString, intTypeElement);
        return elements != null ? new Set(elements) : null;
    }

    private List<OperationPropertyResponse.PropertyResult> analyzeOperatorProperties(
            BinaryOperator<Character> operator, OperationPropertyRequest request, Set setA, boolean intTypeElement) {

        List<OperationPropertyResponse.PropertyResult> properties = new ArrayList<>();

        if (request.isCheckCommutative()) {
            OperationPropertyResponse.PropertyResult result = new OperationPropertyResponse.PropertyResult();
            result.setPropertyType("commutative");
            boolean isCommutative = operator.isCommutative();
            result.setHasProperty(isCommutative);
            result.setReason(isCommutative ? "运算符满足交换律" : "运算符不满足交换律: " + BinaryOperator.getReasonMessage());
            properties.add(result);
        }

        if (request.isCheckAssociative()) {
            OperationPropertyResponse.PropertyResult result = new OperationPropertyResponse.PropertyResult();
            result.setPropertyType("associative");
            boolean isAssociative = operator.isAssociative();
            result.setHasProperty(isAssociative);
            result.setReason(isAssociative ? "运算符满足结合律" : "运算符不满足结合律: " + BinaryOperator.getReasonMessage());
            properties.add(result);
        }

        if (request.isCheckIdempotent()) {
            OperationPropertyResponse.PropertyResult result = new OperationPropertyResponse.PropertyResult();
            result.setPropertyType("idempotent");
            boolean isIdempotent = operator.isIdempotent();
            result.setHasProperty(isIdempotent);
            result.setReason(isIdempotent ? "运算符满足幂等律" : "运算符不满足幂等律: " + BinaryOperator.getReasonMessage());
            properties.add(result);
        }

        if (request.isCheckCancellation()) {
            OperationPropertyResponse.PropertyResult result = new OperationPropertyResponse.PropertyResult();
            result.setPropertyType("cancellation");
            boolean isCancellative = operator.isCancellative();
            result.setHasProperty(isCancellative);
            result.setReason(isCancellative ? "运算符满足消去律" : "运算符不满足消去律: " + BinaryOperator.getReasonMessage());
            properties.add(result);
        }

        if (request.isCheckIdentity()) {
            OperationPropertyResponse.PropertyResult result = new OperationPropertyResponse.PropertyResult();
            result.setPropertyType("identity");
            boolean hasIdentity = operator.hasIdentity();
            result.setHasProperty(hasIdentity);
            if (hasIdentity) {
                char identity = operator.getIdentity();
                result.setReason("运算符有单位元");
                result.setDetails("单位元为: " + Set.getElementLabel(identity, intTypeElement));
            } else {
                result.setReason("运算符没有单位元");
            }
            properties.add(result);
        }

        if (request.isCheckZero()) {
            OperationPropertyResponse.PropertyResult result = new OperationPropertyResponse.PropertyResult();
            result.setPropertyType("zero");
            boolean hasZero = operator.hasZeroElement();
            result.setHasProperty(hasZero);
            if (hasZero) {
                char zero = operator.getZeroElement();
                result.setReason("运算符有零元");
                result.setDetails("零元为: " + Set.getElementLabel(zero, intTypeElement));
            } else {
                result.setReason("运算符没有零元");
            }
            properties.add(result);
        }

        if (request.isCheckInverse()) {
            OperationPropertyResponse.PropertyResult result = new OperationPropertyResponse.PropertyResult();
            result.setPropertyType("inverse");
            boolean hasIdentity = operator.hasIdentity();
            result.setHasProperty(hasIdentity); // 只有有单位元才可能有逆元
            if (hasIdentity) {
                result.setReason("运算符有单位元，可以讨论逆元");
                StringBuilder inverseDetails = new StringBuilder();
                for (int i = 0; i < setA.length(); i++) {
                    char element = setA.get(i);
                    Character[] inverses = new Character[setA.length()];
                    int number = operator.getInverse(element, inverses);
                    if (number > 0) {
                        char[] inversElements = new char[number];
                        for (int j = 0; j < number; j++) {
                            inversElements[j] = inverses[j];
                        }
                        inverseDetails.append(Set.getElementLabel(element, intTypeElement))
                                    .append("的逆元: ")
                                    .append(Set.getElementsLabel(inversElements, intTypeElement, number))
                                    .append("; ");
                    } else {
                        inverseDetails.append(Set.getElementLabel(element, intTypeElement))
                                    .append("没有逆元; ");
                    }
                }
                result.setDetails(inverseDetails.toString());
            } else {
                result.setReason("运算符没有单位元，所有元素都没有逆元");
            }
            properties.add(result);
        }

        return properties;
    }

    private List<OperationPropertyResponse.RelationProperty> analyzeRelationProperties(
            BinaryOperator<Character> operator1, BinaryOperator<Character> operator2, OperationPropertyRequest request) {

        List<OperationPropertyResponse.RelationProperty> relations = new ArrayList<>();

        if (request.isCheckDistributive()) {
            // 检查operator2对operator1的分配律
            OperationPropertyResponse.RelationProperty result1 = new OperationPropertyResponse.RelationProperty();
            result1.setRelationType("distributive");
            result1.setOperator1Name(operator2.getName());
            result1.setOperator2Name(operator1.getName());
            boolean isDistributive1 = operator2.isDistributiveWith(operator1);
            result1.setHasRelation(isDistributive1);
            result1.setDirection(operator2.getName() + "对" + operator1.getName() + "分配");
            result1.setReason(isDistributive1 ? "满足分配律" : "不满足分配律: " + BinaryOperator.getReasonMessage());
            relations.add(result1);

            // 检查operator1对operator2的分配律
            OperationPropertyResponse.RelationProperty result2 = new OperationPropertyResponse.RelationProperty();
            result2.setRelationType("distributive");
            result2.setOperator1Name(operator1.getName());
            result2.setOperator2Name(operator2.getName());
            boolean isDistributive2 = operator1.isDistributiveWith(operator2);
            result2.setHasRelation(isDistributive2);
            result2.setDirection(operator1.getName() + "对" + operator2.getName() + "分配");
            result2.setReason(isDistributive2 ? "满足分配律" : "不满足分配律: " + BinaryOperator.getReasonMessage());
            relations.add(result2);
        }

        if (request.isCheckAbsorption()) {
            OperationPropertyResponse.RelationProperty result = new OperationPropertyResponse.RelationProperty();
            result.setRelationType("absorption");
            result.setOperator1Name(operator1.getName());
            result.setOperator2Name(operator2.getName());
            boolean isAbsorptive = operator1.isAbsorptiveWith(operator2);
            result.setHasRelation(isAbsorptive);
            result.setDirection("相互吸收");
            result.setReason(isAbsorptive ? "满足吸收律" : "不满足吸收律: " + BinaryOperator.getReasonMessage());
            relations.add(result);
        }

        return relations;
    }

    private String generateRandomOperatorString(Set setA) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("{");
        boolean isFirst = true;

        for (int i = 0; i < setA.length(); i++) {
            char first = setA.get(i);
            for (int j = 0; j < setA.length(); j++) {
                char second = setA.get(j);
                int index = (int)(Math.random() * setA.length());
                char result = setA.get(index);

                if (isFirst) {
                    isFirst = false;
                } else {
                    buffer.append(", ");
                }
                buffer.append("<").append(first).append(", ").append(second).append(", ").append(result).append(">");
            }
        }
        buffer.append("}");

        return buffer.toString();
    }
}