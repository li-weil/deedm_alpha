package com.deedm.service.algebra;

import com.deedm.model.algebra.BoolAlgebraRequest;
import com.deedm.model.algebra.BoolAlgebraResponse;
import com.deedm.legacy.algebra.BinaryOperator;
import com.deedm.legacy.algebra.Lattice;
import com.deedm.legacy.graph.AbstractGraph;
import com.deedm.legacy.setrelfun.PartialOrder;
import com.deedm.legacy.setrelfun.Set;
import com.deedm.legacy.util.GraphvizUtil;

import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class BoolAlgebraService {

    public BoolAlgebraResponse analyzeBooleanAlgebra(BoolAlgebraRequest request) {
        BoolAlgebraResponse response = new BoolAlgebraResponse();

        try {
            int m = request.getM();

            // 验证输入参数
            if (m < 6) {
                response.setErrorMessage("输入值 m 必须大于等于 6");
                return response;
            }

            boolean isIntElement = true;

            // 创建偏序关系：整除关系
            PartialOrder partialOrder = PartialOrder.createFactorDivisionOrder(m);
            Set setA = partialOrder.getFromSet();
            String setAString = setA.toLaTeXString(true);

            // 设置基本信息
            response.setM(m);
            response.setLatticeDescription(setAString);
            response.setFormula("F(" + m + ") = " + setAString);

            // 生成哈斯图
            if (request.isShowHasseDiagram()) {
                try {
                    String hasseDiagramUrl = generateHasseDiagram(partialOrder, isIntElement);
                    if (hasseDiagramUrl != null) {
                        response.setHasseDiagramUrl(hasseDiagramUrl);
                    }
                } catch (Exception e) {
                    System.err.println("哈斯图生成失败: " + e.getMessage());
                    // 哈斯图生成失败不影响其他功能
                }
            }

            // 创建格并分析运算表
            Lattice lattice = Lattice.createLatticeFromPoset(partialOrder);
            if (request.isShowOperationTables()) {
                BinaryOperator<Character> supOperator = lattice.getSupOperator();
                String supOptableString = supOperator.toLaTeXString(true);
                BinaryOperator<Character> subOperator = lattice.getSubOperator();
                String subOptableString = subOperator.toLaTeXString(true);

                response.setSupOperatorTable(supOptableString);
                response.setSubOperatorTable(subOptableString);
            }

            // 判断是否为布尔代数
            boolean isBoolAlgebra = lattice.isBooleanAlgebra();
            response.setBooleanAlgebra(isBoolAlgebra);

            // 获取最大元和最小元
            char greatest = lattice.getGreatestElement();
            String greatestString = Set.getElementLabel(greatest, isIntElement);
            char least = lattice.getLeastElement();
            String leastString = Set.getElementLabel(least, isIntElement);
            response.setGreatestElement(greatestString);
            response.setLeastElement(leastString);

            // 计算补元信息
            if (request.isShowComplements()) {
                List<BoolAlgebraResponse.ComplementInfo> complementInfos = new ArrayList<>();

                for (int i = 0; i < setA.length(); i++) {
                    char element = setA.get(i);
                    String elementString = Set.getElementLabel(element, isIntElement);

                    char[] complements = new char[setA.length()];
                    int complementNumber = lattice.getComplement(element, complements);

                    if (complementNumber > 0) {
                        String complementString = Set.getElementsLabel(complements, isIntElement, complementNumber);
                        complementInfos.add(new BoolAlgebraResponse.ComplementInfo(
                            elementString, true, complementString));
                    } else {
                        complementInfos.add(new BoolAlgebraResponse.ComplementInfo(
                            elementString, false, ""));
                    }
                }

                response.setComplementInfos(complementInfos);
            }

            return response;

        } catch (Exception e) {
            response.setErrorMessage("布尔代数分析过程中发生错误: " + e.getMessage());
            return response;
        }
    }

    public String validateInput(String mStr) {
        try {
            if (mStr == null || mStr.trim().isEmpty()) {
                return "请输入正整数 m";
            }

            int m = Integer.parseInt(mStr.trim());
            if (m < 6) {
                return "输入值 m 必须大于等于 6";
            }

            if (m > 1000) {
                return "为了确保性能，输入值 m 不应大于 1000";
            }

            return null; // null表示验证通过

        } catch (NumberFormatException e) {
            return "请输入有效的正整数";
        }
    }

    public Map<String, Object> generateRandomInput() {
        Map<String, Object> result = new HashMap<>();

        try {
            Random random = new Random();
            // 生成6-1000之间的随机数
            int m = random.nextInt(995) + 6;

            result.put("success", true);
            result.put("m", m);

            // 设置默认选项
            Map<String, Boolean> options = new HashMap<>();
            options.put("showHasseDiagram", true);
            options.put("showOperationTables", true);
            options.put("showComplements", true);

            result.put("options", options);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "生成随机输入失败: " + e.getMessage());
        }

        return result;
    }

    private String generateHasseDiagram(PartialOrder partialOrder, boolean isIntElement) {
        try {
            // 检查Graphviz是否可用
            if (!GraphvizUtil.isGraphvizAvailable()) {
                System.err.println("Graphviz不可用，无法生成哈斯图");
                return null;
            }

            // 生成唯一文件名
            String uniqueId = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            String dotFileName = "./data/PartialOrderR.dot";
            String pngFileName = "./data/HASSE_" + uniqueId + ".png";

            // 写入DOT文件
            try (PrintWriter writer = new PrintWriter(dotFileName)) {
                var graph = partialOrder.getHasseDigram(isIntElement);
                graph.simplyWriteToDotFile(writer);
            }

            // 生成PNG文件
            boolean success = GraphvizUtil.generatePNGFile(dotFileName, pngFileName, false);

            if (success && new java.io.File(pngFileName).exists()) {
                // 删除DOT文件，保留PNG文件
                new java.io.File(dotFileName).delete();
                return "/api/bool-algebra/hasse-diagram/HASSE_" + uniqueId + ".png";
            } else {
                // 清理失败的文件
                new java.io.File(dotFileName).delete();
                new java.io.File(pngFileName).delete();
                System.err.println("Graphviz生成失败: " + GraphvizUtil.errorMessage);
                return null;
            }

        } catch (Exception e) {
            System.err.println("生成哈斯图失败: " + e.getMessage());
            return null;
        }
    }
}