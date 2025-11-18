package com.deedm.model.algebra;

import java.util.List;

public class BoolAlgebraResponse {
    private boolean success;
    private String errorMessage;
    private String type; // 结果类型标记
    private String formula; // LaTeX公式表示

    // 基本信息输出
    private int m; // 输入的数值m
    private String latticeDescription; // 格F(m)的LaTeX表示
    private boolean booleanAlgebra; // 是否为布尔代数

    // 最大元和最小元
    private String greatestElement; // 最大元的LaTeX表示
    private String leastElement; // 最小元的LaTeX表示

    // 哈斯图
    private String hasseDiagramUrl; // 哈斯图图片URL

    // 运算表
    private String supOperatorTable; // 上确界运算表的LaTeX表示
    private String subOperatorTable; // 下确界运算表的LaTeX表示

    // 补元信息
    private List<ComplementInfo> complementInfos; // 各元素的补元信息

    // 构造函数
    public BoolAlgebraResponse() {
        this.success = true;
        this.type = "bool-algebra";
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        this.success = false;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public String getLatticeDescription() {
        return latticeDescription;
    }

    public void setLatticeDescription(String latticeDescription) {
        this.latticeDescription = latticeDescription;
    }

    public boolean isBooleanAlgebra() {
        return booleanAlgebra;
    }

    public void setBooleanAlgebra(boolean booleanAlgebra) {
        this.booleanAlgebra = booleanAlgebra;
    }

    public String getGreatestElement() {
        return greatestElement;
    }

    public void setGreatestElement(String greatestElement) {
        this.greatestElement = greatestElement;
    }

    public String getLeastElement() {
        return leastElement;
    }

    public void setLeastElement(String leastElement) {
        this.leastElement = leastElement;
    }

    public String getHasseDiagramUrl() {
        return hasseDiagramUrl;
    }

    public void setHasseDiagramUrl(String hasseDiagramUrl) {
        this.hasseDiagramUrl = hasseDiagramUrl;
    }

    public String getSupOperatorTable() {
        return supOperatorTable;
    }

    public void setSupOperatorTable(String supOperatorTable) {
        this.supOperatorTable = supOperatorTable;
    }

    public String getSubOperatorTable() {
        return subOperatorTable;
    }

    public void setSubOperatorTable(String subOperatorTable) {
        this.subOperatorTable = subOperatorTable;
    }

    public List<ComplementInfo> getComplementInfos() {
        return complementInfos;
    }

    public void setComplementInfos(List<ComplementInfo> complementInfos) {
        this.complementInfos = complementInfos;
    }

    // 内部类：补元信息
    public static class ComplementInfo {
        private String element; // 元素的LaTeX表示
        private boolean hasComplement; // 是否有补元
        private String complements; // 补元的LaTeX表示（如果有）

        public ComplementInfo() {}

        public ComplementInfo(String element, boolean hasComplement, String complements) {
            this.element = element;
            this.hasComplement = hasComplement;
            this.complements = complements;
        }

        public String getElement() {
            return element;
        }

        public void setElement(String element) {
            this.element = element;
        }

        public boolean isHasComplement() {
            return hasComplement;
        }

        public void setHasComplement(boolean hasComplement) {
            this.hasComplement = hasComplement;
        }

        public String getComplements() {
            return complements;
        }

        public void setComplements(String complements) {
            this.complements = complements;
        }
    }
}