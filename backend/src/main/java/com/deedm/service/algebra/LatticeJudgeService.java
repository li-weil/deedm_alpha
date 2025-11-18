package com.deedm.service.algebra;

import com.deedm.model.algebra.LatticeJudgeRequest;
import com.deedm.model.algebra.LatticeJudgeResponse;
import com.deedm.legacy.setrelfun.SetrelfunUtil;
import com.deedm.legacy.setrelfun.Set;
import com.deedm.legacy.setrelfun.Relation;
import com.deedm.legacy.setrelfun.PartialOrder;
import com.deedm.legacy.setrelfun.OrderedPair;
import com.deedm.legacy.algebra.Lattice;
import com.deedm.legacy.graph.AbstractGraph;
import com.deedm.legacy.util.GraphvizUtil;

import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class LatticeJudgeService {

    public LatticeJudgeResponse judgeLattice(LatticeJudgeRequest request) {
        LatticeJudgeResponse response = new LatticeJudgeResponse();
        response.setType("lattice-judge");

        try {
            // 解析集合A
            char[] setAElements = SetrelfunUtil.extractSetElements(request.getSetAString(), request.isIntTypeElement());
            if (setAElements == null) {
                response.setErrorMessage("集合A的格式不正确: " + SetrelfunUtil.getExtractErrorMessage());
                return response;
            }
            Set setA = new Set(setAElements);

            // 解析关系R
            Relation relationR = SetrelfunUtil.extractRelation(setA, setA, request.getRelationRString(), request.isIntTypeElement());
            if (relationR == null) {
                response.setErrorMessage("关系R的格式不正确: " + SetrelfunUtil.getExtractErrorMessage());
                return response;
            }

            // 设置基本信息
            response.setSetAString(setA.toString());
            response.setRelationRString(relationR.toString());
            response.setIntTypeElement(request.isIntTypeElement());
            response.setUseHasseDiagram(request.isUseHasseDiagram());

            // 构建LaTeX公式
            String formula = String.format("A = %s \\quad R = %s",
                setA.toLaTeXString(),
                relationR.toLaTeXString(request.isIntTypeElement()));
            response.setFormula(formula);

            // 如果使用哈斯图输入，计算传递和自反闭包
            if (request.isUseHasseDiagram()) {
                System.out.println("=== 哈斯图处理前 ===");
                System.out.println("原始关系: " + relationR.toString());
                relationR = relationR.transitiveClosureByWarshallAlgorithm();
                System.out.println("传递闭包后: " + relationR.toString());
                relationR = relationR.reflexiveClosure();
                System.out.println("自反闭包后: " + relationR.toString());
                System.out.println("=== 哈斯图处理完成 ===");
            }

            // 判断是否为偏序关系
            boolean isPartialOrder = checkPartialOrder(relationR, request.isUseHasseDiagram(), request.isIntTypeElement(), response);
            response.setPartialOrder(isPartialOrder);

            // 添加调试信息 - 检查响应设置
            System.out.println("=== 响应设置调试信息 ===");
            System.out.println("isPartialOrder设置值: " + isPartialOrder);
            System.out.println("response.isPartialOrder()返回值: " + response.isPartialOrder());
            System.out.println("=== 响应设置调试信息结束 ===");

            if (!isPartialOrder) {
                response.setLattice(false);
                response.setLatticeReason("不是偏序关系，因此不是格");
                return response;
            }

            // 生成哈斯图
            String hasseDiagramUrl = generateHasseDiagram(setA, relationR, request.isIntTypeElement());
            response.setHasseDiagramUrl(hasseDiagramUrl);

            // 判断是否为格
            PartialOrder partialOrder = new PartialOrder(relationR);
            boolean isLattice = partialOrder.isLattice();
            response.setLattice(isLattice);

            if (!isLattice) {
                char elementOne = PartialOrder.getElementOne();
                char elementTwo = PartialOrder.getElementTwo();
                int reasonType = PartialOrder.getReasonType();

                String elementOneString = Set.getElementLabel(elementOne, request.isIntTypeElement());
                String elementTwoString = Set.getElementLabel(elementTwo, request.isIntTypeElement());

                if (reasonType == PartialOrder.WITHOUT_GLB) {
                    response.setLatticeReason(String.format("元素 %s 和 %s 没有最大下界", elementOneString, elementTwoString));
                } else {
                    response.setLatticeReason(String.format("元素 %s 和 %s 没有最小上界", elementOneString, elementTwoString));
                }
                return response;
            }

            // 如果是格，生成运算表
            Lattice lattice = Lattice.createLatticeFromPoset(partialOrder);
            response.setSupOperatorTable(lattice.getSupOperator().toLaTeXString());
            response.setSubOperatorTable(lattice.getSubOperator().toLaTeXString());
            response.setSupOperatorName(lattice.getSupOperator().getName());
            response.setSubOperatorName(lattice.getSubOperator().getName());

            // 判断分配格
            if (request.isCheckDistributive()) {
                boolean isDistributive = lattice.isDistributive();
                response.setIsDistributive(isDistributive);
                if (!isDistributive) {
                    response.setDistributiveReason(Lattice.getNonDistributiveReason());
                }
            }

            // 判断有界格
            if (request.isCheckBounded()) {
                boolean isBounded = lattice.isBounded();
                response.setIsBounded(isBounded);
                if (isBounded) {
                    char greatest = lattice.getGreatestElement();
                    char least = lattice.getLeastElement();
                    response.setGreatestElement(Set.getElementLabel(greatest, request.isIntTypeElement()));
                    response.setLeastElement(Set.getElementLabel(least, request.isIntTypeElement()));
                } else {
                    int reasonType = Lattice.getReasonType();
                    if (reasonType == Lattice.WITHOUT_GREATEST) {
                        response.setBoundedReason("没有最大元");
                    } else {
                        response.setBoundedReason("没有最小元");
                    }
                }
            }

            // 判断有补格
            if (request.isCheckComplemented()) {
                boolean isBounded = lattice.isBounded();
                if (isBounded) {
                    boolean hasComplement = lattice.isComplemented();
                    response.setIsComplemented(hasComplement);
                    if (hasComplement) {
                        // 计算每个元素的补元
                        List<LatticeJudgeResponse.ElementComplement> complements = new ArrayList<>();
                        for (int i = 0; i < setA.length(); i++) {
                            char element = setA.get(i);
                            String elementString = Set.getElementLabel(element, request.isIntTypeElement());
                            char[] complementArray = new char[setA.length()];
                            int complementNumber = lattice.getComplement(element, complementArray);
                            String complementString = Set.getElementsLabel(complementArray, request.isIntTypeElement(), complementNumber);
                            complements.add(new LatticeJudgeResponse.ElementComplement(elementString, complementString));
                        }
                        response.setComplements(complements);
                    } else {
                        char elementOne = Lattice.getElementOne();
                        String elementOneString = Set.getElementLabel(elementOne, request.isIntTypeElement());
                        response.setComplementedReason(String.format("元素 %s 没有补元", elementOneString));
                    }
                } else {
                    int reasonType = Lattice.getReasonType();
                    if (reasonType == Lattice.WITHOUT_GREATEST) {
                        response.setComplementedReason("不是有界格，没有最大元");
                    } else {
                        response.setComplementedReason("不是有界格，没有最小元");
                    }
                }
            }

            // 判断布尔代数
            if (request.isCheckBoolean()) {
                boolean isDistributive = lattice.isDistributive();
                if (isDistributive) {
                    boolean isBounded = lattice.isBounded();
                    if (isBounded) {
                        boolean hasComplement = lattice.isComplemented();
                        response.setIsBooleanAlgebra(hasComplement);
                        if (!hasComplement) {
                            char elementOne = Lattice.getElementOne();
                            String elementOneString = Set.getElementLabel(elementOne, request.isIntTypeElement());
                            response.setBooleanAlgebraReason(String.format("不是有补格，元素 %s 没有补元", elementOneString));
                        }
                    } else {
                        int reasonType = Lattice.getReasonType();
                        if (reasonType == Lattice.WITHOUT_GREATEST) {
                            response.setBooleanAlgebraReason("不是有界格，没有最大元");
                        } else {
                            response.setBooleanAlgebraReason("不是有界格，没有最小元");
                        }
                    }
                } else {
                    response.setIsBooleanAlgebra(false);
                    response.setBooleanAlgebraReason("不是分配格");
                }
            }

        } catch (Exception e) {
            response.setErrorMessage("格判断过程中发生错误: " + e.getMessage());
        }

        return response;
    }

    private boolean checkPartialOrder(Relation relation, boolean useHasse, boolean isIntElement, LatticeJudgeResponse response) {
        // 添加调试信息
        System.out.println("=== 偏序关系检查调试信息 ===");
        System.out.println("使用哈斯图: " + useHasse);
        System.out.println("关系内容: " + relation.toString());
        System.out.println("关系元素数量: " + (relation.getPairs() != null ? relation.getPairs().length : 0));

        // 检查自反性（如果不是哈斯图输入）
        if (!useHasse) {
            boolean isReflexive = relation.isReflexive();
            System.out.println("自反性检查结果: " + isReflexive);
            if (!isReflexive) {
                OrderedPair exampleOne = Relation.getExampleOne();
                response.setPartialOrderReason("关系不是自反的");
                response.setPartialOrderCounterexample(exampleOne.toLaTeXString() + " \\notin R");
                return false;
            }
        }

        // 检查反对称性
        boolean isAntisymmetric = relation.isAntisymmetric();
        System.out.println("反对称性检查结果: " + isAntisymmetric);
        if (!isAntisymmetric) {
            OrderedPair exampleOne = Relation.getExampleOne();
            OrderedPair exampleTwo = Relation.getExampleTwo();
            String relationName = useHasse ? "tr(R)" : "R";
            response.setPartialOrderReason("关系不是反对称的");
            response.setPartialOrderCounterexample(String.format("%s \\in %s \\land %s \\in %s",
                exampleOne.toLaTeXString(), relationName, exampleTwo.toLaTeXString(), relationName));
            return false;
        }

        // 检查传递性（如果不是哈斯图输入）
        if (!useHasse) {
            boolean isTransitive = relation.isTransitive();
            System.out.println("传递性检查结果: " + isTransitive);
            if (!isTransitive) {
                OrderedPair exampleOne = Relation.getExampleOne();
                OrderedPair exampleTwo = Relation.getExampleTwo();
                OrderedPair pair = new OrderedPair(exampleOne.getFirst(), exampleTwo.getSecond());
                response.setPartialOrderReason("关系不是传递的");
                response.setPartialOrderCounterexample(String.format("%s \\in R \\land %s \\in R \\land %s \\notin R",
                    exampleOne.toLaTeXString(), exampleTwo.toLaTeXString(), pair.toLaTeXString()));
                return false;
            }
        }

        System.out.println("偏序关系检查通过: 是偏序关系");
        response.setPartialOrderReason("满足自反性、反对称性和传递性");
        return true;
    }

    private String generateHasseDiagram(Set setA, Relation relation, boolean isIntElement) {
        try {
            System.out.println("=== 哈斯图生成调试信息 ===");

            // 检查Graphviz是否可用
            if (!GraphvizUtil.isGraphvizAvailable()) {
                System.out.println("Graphviz不可用，无法生成哈斯图");
                return null;
            }

            PartialOrder partialOrder = new PartialOrder(relation);

            // 生成唯一文件名避免冲突
            String uniqueId = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            String dataPath = System.getProperty("user.dir") + "/data/";
            String dotFileName = dataPath + "PartialOrderR.dot";
            String pngFileName = dataPath + "HASSE_" + uniqueId + ".png";
            String imageUrl = "/api/algebra/lattice-judge/hasse-diagram/HASSE_" + uniqueId + ".png";

            // 确保目录存在
            File dataDir = new File(dataPath);
            if (!dataDir.exists()) {
                boolean created = dataDir.mkdirs();
                System.out.println("创建数据目录: " + dataPath + ", 结果: " + created);
            }

            System.out.println("数据路径: " + dataPath);
            System.out.println("DOT文件名: " + dotFileName);
            System.out.println("PNG文件名: " + pngFileName);
            System.out.println("图片URL: " + imageUrl);

            // 写入DOT文件
            try (PrintWriter writer = new PrintWriter(dotFileName, "UTF-8")) {
                AbstractGraph graph = partialOrder.getHasseDigram(isIntElement);
                graph.simplyWriteToDotFile(writer);
            }

            System.out.println("DOT文件写入完成");

            // 使用GraphvizUtil生成PNG文件
            boolean success = GraphvizUtil.generatePNGFile(dotFileName, pngFileName, false);
            System.out.println("PNG生成结果: " + success);
            System.out.println("错误信息: " + GraphvizUtil.errorMessage);

            File pngFile = new File(pngFileName);
            System.out.println("PNG文件是否存在: " + pngFile.exists());
            System.out.println("PNG文件绝对路径: " + pngFile.getAbsolutePath());

            if (success && pngFile.exists()) {
                // 删除DOT文件，保留PNG文件
                new File(dotFileName).delete();
                System.out.println("返回图片URL: " + imageUrl);
                System.out.println("=== 哈斯图生成调试信息结束 ===");
                return imageUrl;
            } else {
                // 清理失败的文件
                new File(dotFileName).delete();
                new File(pngFileName).delete();
                System.out.println("哈斯图生成失败，已清理临时文件");
                return null;
            }
        } catch (Exception e) {
            // 记录错误但不影响主要功能
            System.err.println("生成哈斯图失败: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("=== 哈斯图生成调试信息结束 ===");
        return null;
    }
}