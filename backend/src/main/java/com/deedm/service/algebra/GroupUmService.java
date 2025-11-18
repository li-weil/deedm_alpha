package com.deedm.service.algebra;

import com.deedm.model.algebra.GroupUmRequest;
import com.deedm.model.algebra.GroupUmResponse;
import com.deedm.legacy.algebra.GroupUnitModulo;
import com.deedm.legacy.algebra.BinaryOperator;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GroupUmService {

    public GroupUmResponse analyzeGroupUm(GroupUmRequest request) {
        GroupUmResponse response = new GroupUmResponse();

        try {
            int m = request.getM();

            // 验证输入参数
            if (m < 1) {
                response.setSuccess(false);
                response.setErrorMessage("参数m必须是正整数");
                return response;
            }

            if (m > 30) {
                response.setSuccess(false);
                response.setErrorMessage("为了确保性能，参数m不应大于30");
                return response;
            }

            // 创建群U(m)
            GroupUnitModulo UGroup = new GroupUnitModulo(m);
            Integer[] elements = UGroup.getElements();
            String elementString = GroupUnitModulo.integerArrayToString(elements);
            String operatorString = UGroup.getLaTeXStringOfOperatorTable();

            // 设置基本信息
            response.setSuccess(true);
            response.setM(m);
            response.setGroupElements(elementString);
            response.setOperatorTable(operatorString);
            response.setFormula("U(" + m + ") = \\{" + elementString + "\\}");

            // 分析是否为循环群
            if (request.isShowCycleGroup()) {
                if (UGroup.isCycleGroup()) {
                    response.setCycleGroup(true);
                    int[] generator = GroupUnitModulo.integerListToArray(UGroup.getGenerator());
                    String generatorString = GroupUnitModulo.integerArrayToString(generator);
                    response.setGenerators(generatorString);
                } else {
                    response.setCycleGroup(false);
                    response.setGenerators("");
                }
            }

            // 计算元素的幂
            if (request.isShowPower()) {
                List<Map<String, Object>> elementPowers = new ArrayList<>();
                for (int element : elements) {
                    Map<String, Object> powerInfo = new HashMap<>();
                    List<String> powerList = new ArrayList<>();

                    // 计算逆元和各次幂，整合到一个序列中
                    int inverse = UGroup.getInverse(element);

                    // 先添加逆元
                    powerList.add(element + "^{-1}=" + inverse);

                    // 然后添加各次幂（跳过j=1因为已经显示了逆元）
                    for (int j = 1; j <= elements.length; j++) {
                        int power = UGroup.power(element, j);
                        powerList.add(element + "^{" + j + "}=" + power);
                        if (power == UGroup.getIdentity()) break;
                    }

                    powerInfo.put("element", element);
                    powerInfo.put("powers", powerList);
                    elementPowers.add(powerInfo);
                }
                response.setElementPowers(elementPowers);
            }

            // 计算元素的阶
            if (request.isShowOrder()) {
                List<Map<String, Object>> elementOrders = new ArrayList<>();
                for (int element : elements) {
                    Map<String, Object> orderInfo = new HashMap<>();
                    int order = UGroup.getOrder(element);
                    orderInfo.put("element", element);
                    orderInfo.put("order", order);
                    orderInfo.put("formula", "|" + element + "| = " + order);
                    elementOrders.add(orderInfo);
                }
                response.setElementOrders(elementOrders);
            }

            // 分析子群和陪集
            if (request.isShowSubgroups() || request.isShowCosets()) {
                List<GroupUmResponse.SubgroupInfo> subgroups = new ArrayList<>();
                List<List<Integer>> allSubgroups = UGroup.getAllSubgroup();

                for (List<Integer> subgroup : allSubgroups) {
                    // 跳过平凡子群
                    if (subgroup.size() == 1 || subgroup.size() == elements.length) {
                        continue;
                    }

                    String subgroupString = GroupUnitModulo.integerListToString(subgroup);
                    Integer[] subgroupArray = new Integer[subgroup.size()];
                    for (int i = 0; i < subgroup.size(); i++) {
                        subgroupArray[i] = subgroup.get(i);
                    }

                    // 获取子群的生成元
                    List<Integer> generatorList = UGroup.getGenerator(subgroupArray);
                    boolean isCycleSubgroup = generatorList.size() > 0;
                    String generatorString = "";
                    if (isCycleSubgroup) {
                        generatorString = GroupUnitModulo.integerListToString(generatorList);
                    }

                    
                    // 获取子群运算表
                    BinaryOperator<Integer> subgroupOperatorTable = UGroup.getSubgroupOperatorTable(subgroupArray);
                    String subgroupOpString = subgroupOperatorTable.toLaTeXString();

                    // 获取陪集（如果需要）
                    List<String> cosets = new ArrayList<>();
                    if (request.isShowCosets()) {
                        List<int[]> allCosets = UGroup.getAllCoset(subgroup);
                        for (int[] coset : allCosets) {
                            cosets.add("\\{" + GroupUnitModulo.integerArrayToString(coset) + "\\}");
                        }
                    }

                    subgroups.add(new GroupUmResponse.SubgroupInfo(
                        subgroupString, subgroupOpString, isCycleSubgroup, generatorString, cosets
                    ));
                }

                response.setSubgroups(subgroups);
            }

            return response;

        } catch (Exception e) {
            response.setSuccess(false);
            response.setErrorMessage("群U(m)分析过程中发生错误: " + e.getMessage());
            return response;
        }
    }

    public Map<String, Object> validateGroupUmInput(int m) {
        Map<String, Object> result = new HashMap<>();

        if (m < 1) {
            result.put("valid", false);
            result.put("message", "参数m必须是正整数");
            return result;
        }

        if (m > 30) {
            result.put("valid", false);
            result.put("message", "为了确保性能，参数m不应大于30");
            return result;
        }

        result.put("valid", true);
        result.put("message", "输入有效");
        result.put("recommendations", getRecommendations(m));

        return result;
    }

    public Map<String, Object> generateRandomGroupUm() {
        Map<String, Object> result = new HashMap<>();

        try {
            Random random = new Random();
            // 生成1-29之间的随机数
            int m = random.nextInt(29) + 1;

            result.put("success", true);
            result.put("m", m);

            // 设置默认选项
            Map<String, Boolean> options = new HashMap<>();
            options.put("showCycleGroup", true);
            options.put("showPower", true);
            options.put("showOrder", true);
            options.put("showSubgroups", true);
            options.put("showCosets", true);

            result.put("options", options);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "生成随机输入失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 根据输入值m提供建议的显示选项
     */
    private Map<String, Boolean> getRecommendations(int m) {
        Map<String, Boolean> recommendations = new HashMap<>();

        // 对于较小的值，可以显示所有信息
        if (m <= 10) {
            recommendations.put("showCycleGroup", true);
            recommendations.put("showPower", true);
            recommendations.put("showOrder", true);
            recommendations.put("showSubgroups", true);
            recommendations.put("showCosets", true);
        }
        // 对于中等大小的值，建议减少显示
        else if (m <= 20) {
            recommendations.put("showCycleGroup", true);
            recommendations.put("showPower", true);
            recommendations.put("showOrder", false); // 元素的阶可能较多
            recommendations.put("showSubgroups", false); // 子群分析可能耗时
            recommendations.put("showCosets", false);
        }
        // 对于较大的值，只显示基本信息
        else {
            recommendations.put("showCycleGroup", true);
            recommendations.put("showPower", false);
            recommendations.put("showOrder", false);
            recommendations.put("showSubgroups", false);
            recommendations.put("showCosets", false);
        }

        return recommendations;
    }
}