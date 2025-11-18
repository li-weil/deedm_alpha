package com.deedm.service.algebra;

import com.deedm.model.algebra.GroupPermRequest;
import com.deedm.model.algebra.GroupPermResponse;
import com.deedm.legacy.algebra.GroupPermutation;
import com.deedm.legacy.algebra.BinaryOperator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupPermService {

    /**
     * 分析置换群
     */
    public GroupPermResponse analyzePermutationGroup(GroupPermRequest request) {
        GroupPermResponse response = new GroupPermResponse();

        try {
            int m = request.getM();

            // 验证输入
            String validationError = validateInput(m);
            if (validationError != null) {
                response.setErrorMessage(validationError);
                return response;
            }

            // 创建置换群
            GroupPermutation SGroup = createPermutationGroup(m);
            GroupPermutation.Bijection[] elements = SGroup.getElements();

            // 设置基本信息
            response.setM(m);
            response.setGroupElements(GroupPermutation.FunctionArrayToString(elements));
            response.setElementTable(SGroup.getLaTeXTableStringOfElements());
            response.setOperatorTable(SGroup.getLaTeXStringOfOperatorTable());

            // 设置formula字段（前端显示需要）
            String formula = "S(" + m + ") = \\{" + GroupPermutation.FunctionArrayToString(elements) + "\\}";
            response.setFormula(formula);

            // 循环群分析
            if (request.isShowCycleGroup()) {
                analyzeCycleGroup(SGroup, elements, response);
            }

            // 群元素的幂
            if (request.isShowElementPower()) {
                analyzeElementPowers(SGroup, elements, response);
            }

            // 群元素的阶
            if (request.isShowElementOrder()) {
                analyzeElementOrders(SGroup, elements, response);
            }

            // 子群分析
            if (request.isShowSubgroups()) {
                analyzeSubgroups(SGroup, elements, response);
            }

            // 陪集和正规子群分析
            if (request.isShowCosets() || request.isShowNormalSubgroups()) {
                analyzeCosetsAndNormalSubgroups(SGroup, elements, request, response);
            }

        } catch (Exception e) {
            response.setErrorMessage("分析置换群时发生错误: " + e.getMessage());
        }

        // 最终调试输出
        System.out.println("=== 最终响应调试 ===");
        System.out.println("子群数据数量: " + (response.getSubgroups() != null ? response.getSubgroups().size() : 0));
        System.out.println("正规子群数据数量: " + (response.getNormalSubgroups() != null ? response.getNormalSubgroups().size() : 0));

        return response;
    }

    /**
     * 验证输入参数
     */
    public String validateInput(int m) {
        if (m < 2) {
            return "度数n必须大于等于2";
        }
        if (m > 5) {
            return "度数n必须小于等于5，否则计算时间可能很长";
        }
        return null;
    }

    /**
     * 创建置换群
     */
    private GroupPermutation createPermutationGroup(int m) {
        if (m == 3) {
            // 对于m=3，使用特定的元素（与原Java应用保持一致）
            char[][] functionValue = {
                {'1', '2', '3'},
                {'2', '1', '3'},
                {'3', '2', '1'},
                {'1', '3', '2'},
                {'2', '3', '1'},
                {'3', '1', '2'}
            };
            GroupPermutation.Bijection[] elements = new GroupPermutation.Bijection[functionValue.length];
            for (int i = 1; i <= functionValue.length; i++) {
                elements[i-1] = new GroupPermutation.Bijection("f_"+i, functionValue[i-1]);
            }
            return new GroupPermutation(elements);
        } else {
            // 对于其他值，使用标准构造方法
            return new GroupPermutation(m);
        }
    }

    /**
     * 分析循环群
     */
    private void analyzeCycleGroup(GroupPermutation SGroup, GroupPermutation.Bijection[] elements, GroupPermResponse response) {
        boolean isCycle = SGroup.isCycleGroup();
        response.setCycleGroup(isCycle);

        if (isCycle) {
            GroupPermutation.Bijection[] generator = GroupPermutation.FunctionListToArray(SGroup.getGenerator());
            String generators = GroupPermutation.FunctionArrayToString(generator);
            response.setGenerators(generators);
        }
    }

    /**
     * 分析群元素的幂
     */
    private void analyzeElementPowers(GroupPermutation SGroup, GroupPermutation.Bijection[] elements, GroupPermResponse response) {
        List<String> elementPowers = new ArrayList<>();

        for (GroupPermutation.Bijection element : elements) {
            StringBuilder powerInfo = new StringBuilder();

            // 逆元
            GroupPermutation.Bijection inverse = SGroup.getInverse(element);
            powerInfo.append(element).append("^{-1}=").append(inverse).append("\\quad\\quad\\quad\\quad ");

            // 各次幂
            for (int j = 1; j <= elements.length; j++) {
                GroupPermutation.Bijection power = SGroup.power(element, j);
                powerInfo.append(element).append("^{").append(j).append("}=").append(power).append("\\quad\\quad\\quad\\quad ");
                if (power == SGroup.getIdentity()) break;
            }

            elementPowers.add(powerInfo.toString());
        }

        response.setElementPowers(elementPowers);
    }

    /**
     * 分析群元素的阶
     */
    private void analyzeElementOrders(GroupPermutation SGroup, GroupPermutation.Bijection[] elements, GroupPermResponse response) {
        List<String> elementOrders = new ArrayList<>();

        for (GroupPermutation.Bijection element : elements) {
            int order = SGroup.getOrder(element);
            elementOrders.add("|" + element + "| = " + order + "\\quad\\quad\\quad\\quad ");
        }

        response.setElementOrders(elementOrders);
    }

    /**
     * 分析子群
     * 使用 getAllGeneratedSubgroup() 而不是 getAllSubgroup()，因为我们需要显示循环子群信息
     */
    private void analyzeSubgroups(GroupPermutation SGroup, GroupPermutation.Bijection[] elements, GroupPermResponse response) {
        List<GroupPermResponse.SubgroupResult> subgroupResults = new ArrayList<>();
        List<List<GroupPermutation.Bijection>> allSubgroups = SGroup.getAllGeneratedSubgroup();

        System.out.println("=== 调试：analyzeSubgroups 开始 ===");
        System.out.println("所有元素: " + GroupPermutation.FunctionArrayToString(elements));
        System.out.println("找到的生成子群数量: " + allSubgroups.size());

        for (List<GroupPermutation.Bijection> subgroup : allSubgroups) {
            // 跳过平凡子群
            if (subgroup.size() == 1 || subgroup.size() == elements.length) {
                System.out.println("跳过平凡子群，大小: " + subgroup.size());
                continue;
            }

            GroupPermResponse.SubgroupResult subgroupResult = new GroupPermResponse.SubgroupResult();

            // 子群元素
            String subgroupString = GroupPermutation.FunctionListToString(subgroup);
            subgroupResult.setSubgroupElements(subgroupString);

            System.out.println("处理子群: {" + subgroupString + "}, 大小: " + subgroup.size());

            // 检查是否为循环子群
            GroupPermutation.Bijection[] array = new GroupPermutation.Bijection[subgroup.size()];
            for (int i = 0; i < subgroup.size(); i++) {
                array[i] = subgroup.get(i);
            }

            List<GroupPermutation.Bijection> generatorList = SGroup.getGenerator(array);
            System.out.println("生成元数量: " + generatorList.size());
            if (generatorList.size() > 0) {
                String generators = GroupPermutation.FunctionListToString(generatorList);
                System.out.println("是循环子群，生成元: " + generators);
                subgroupResult.setCycleSubgroup(true);
                subgroupResult.setGenerators(generators);
            } else {
                System.out.println("不是循环子群");
                subgroupResult.setCycleSubgroup(false);
            }

            // 子群运算表
            BinaryOperator<GroupPermutation.Bijection> subgroupOperatorTable = SGroup.getSubgroupOperatorTable(array);
            String operatorTable = subgroupOperatorTable.toLaTeXString();
            subgroupResult.setOperatorTable(operatorTable);

            subgroupResults.add(subgroupResult);
        }

        System.out.println("最终子群结果数量: " + subgroupResults.size());
        System.out.println("=== 调试：analyzeSubgroups 结束 ===");
        response.setSubgroups(subgroupResults);
    }

    /**
     * 分析陪集和正规子群
     * 按照原Java应用逻辑：只要选择了陪集或正规子群，就要分析所有非平凡子群的陪集
     * 如果还选择了正规子群，则额外判断每个子群是否为正规子群并显示商群
     */
    private void analyzeCosetsAndNormalSubgroups(GroupPermutation SGroup, GroupPermutation.Bijection[] elements,
                                                GroupPermRequest request, GroupPermResponse response) {
        List<GroupPermResponse.CosetResult> cosetResults = new ArrayList<>();
        List<GroupPermResponse.NormalSubgroupResult> normalSubgroupResults = new ArrayList<>();

        List<List<GroupPermutation.Bijection>> allSubgroups = SGroup.getAllSubgroup();

        System.out.println("=== 调试：analyzeCosetsAndNormalSubgroups 开始 ===");
        System.out.println("所有子群数量: " + allSubgroups.size());
        System.out.println("显示陪集: " + request.isShowCosets() + ", 显示正规子群: " + request.isShowNormalSubgroups());

        for (List<GroupPermutation.Bijection> subgroup : allSubgroups) {
            // 跳过平凡子群
            if (subgroup.size() == 1 || subgroup.size() == elements.length) {
                System.out.println("跳过平凡子群，大小: " + subgroup.size());
                continue;
            }

            String subgroupString = GroupPermutation.FunctionListToString(subgroup);
            System.out.println("处理陪集和正规性分析子群: {" + subgroupString + "}, 大小: " + subgroup.size());

            // 陪集分析：按照原Java应用逻辑，只要选择了陪集或正规子群就要显示所有子群的陪集
            if (request.isShowCosets() || request.isShowNormalSubgroups()) {
                GroupPermResponse.CosetResult cosetResult = new GroupPermResponse.CosetResult();
                cosetResult.setSubgroupElements(subgroupString);

                // 左陪集
                List<GroupPermutation.Coset> allLeftCosets = SGroup.getAllLeftCoset(subgroup);
                List<String> leftCosetStrings = new ArrayList<>();
                for (int i = 0; i < allLeftCosets.size(); i++) {
                    GroupPermutation.Coset coset = allLeftCosets.get(i);
                    // 按照原Java应用的格式化逻辑，每6个陪集换行
                    if (i % 6 == 0 && i > 0) {
                        leftCosetStrings.add("\n");
                    }
                    leftCosetStrings.add(coset.toString() + "\\quad\\qquad ");
                }
                cosetResult.setLeftCosets(leftCosetStrings);

                // 右陪集
                List<GroupPermutation.Coset> allRightCosets = SGroup.getAllRightCoset(subgroup);
                List<String> rightCosetStrings = new ArrayList<>();
                for (int i = 0; i < allRightCosets.size(); i++) {
                    GroupPermutation.Coset coset = allRightCosets.get(i);
                    // 按照原Java应用的格式化逻辑，每6个陪集换行
                    if (i % 6 == 0 && i > 0) {
                        rightCosetStrings.add("\n");
                    }
                    rightCosetStrings.add(coset.toString() + "\\quad\\qquad ");
                }
                cosetResult.setRightCosets(rightCosetStrings);

                cosetResults.add(cosetResult);
            }

            // 正规子群分析：如果选择了正规子群，则判断每个子群是否为正规子群
            if (request.isShowNormalSubgroups()) {
                GroupPermResponse.NormalSubgroupResult normalResult = new GroupPermResponse.NormalSubgroupResult();
                normalResult.setSubgroupElements(subgroupString);

                boolean isNormal = SGroup.isNormalSubgroup(subgroup);
                normalResult.setNormal(isNormal);

                System.out.println("检查子群 {" + subgroupString + "} 是否为正规子群: " + isNormal);
                if (isNormal) {
                    GroupPermutation.QuotientGroup quotient = SGroup.getQuotientGroup(subgroup);
                    String quotientTable = quotient.getLaTeXStringOfOperatorTable();
                    normalResult.setQuotientGroupTable(quotientTable);
                    System.out.println("是正规子群，商群表已生成");
                }

                normalSubgroupResults.add(normalResult);
            }
        }

        // 只有当选择了陪集或正规子群时才设置陪集数据
        if (request.isShowCosets() || request.isShowNormalSubgroups()) {
            response.setCosets(cosetResults);
            System.out.println("设置陪集结果，数量: " + cosetResults.size());
        }
        // 只有当选择了正规子群时才设置正规子群数据
        if (request.isShowNormalSubgroups()) {
            response.setNormalSubgroups(normalSubgroupResults);
            System.out.println("设置正规子群结果，数量: " + normalSubgroupResults.size());
        }
        System.out.println("=== 调试：analyzeCosetsAndNormalSubgroups 结束 ===");
    }
}