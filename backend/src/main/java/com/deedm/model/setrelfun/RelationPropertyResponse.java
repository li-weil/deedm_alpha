package com.deedm.model.setrelfun;


public class RelationPropertyResponse {
    private boolean success;
    private String errorMessage;
    private String type; // 添加类型标记
    private String formula;
    private String setAString;
    private String relationRString;
    private boolean intTypeElement;

    // 关系矩阵
    private String matrixString;

    // 关系图
    private String graphImageUrl;

    // 关系性质结果
    private ReflexiveResult reflexiveResult;
    private ReflexiveResult irreflexiveResult;
    private SymmetricResult symmetricResult;
    private SymmetricResult antisymmetricResult;
    private TransitiveResult transitiveResult;

    // 构造函数
    public RelationPropertyResponse() {
        this.success = true;
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

    public String getSetAString() {
        return setAString;
    }

    public void setSetAString(String setAString) {
        this.setAString = setAString;
    }

    public String getRelationRString() {
        return relationRString;
    }

    public void setRelationRString(String relationRString) {
        this.relationRString = relationRString;
    }

    public boolean isIntTypeElement() {
        return intTypeElement;
    }

    public void setIntTypeElement(boolean intTypeElement) {
        this.intTypeElement = intTypeElement;
    }

    public String getMatrixString() {
        return matrixString;
    }

    public void setMatrixString(String matrixString) {
        this.matrixString = matrixString;
    }

    public String getGraphImageUrl() {
        return graphImageUrl;
    }

    public void setGraphImageUrl(String graphImageUrl) {
        this.graphImageUrl = graphImageUrl;
    }

    public ReflexiveResult getReflexiveResult() {
        return reflexiveResult;
    }

    public void setReflexiveResult(ReflexiveResult reflexiveResult) {
        this.reflexiveResult = reflexiveResult;
    }

    public ReflexiveResult getIrreflexiveResult() {
        return irreflexiveResult;
    }

    public void setIrreflexiveResult(ReflexiveResult irreflexiveResult) {
        this.irreflexiveResult = irreflexiveResult;
    }

    public SymmetricResult getSymmetricResult() {
        return symmetricResult;
    }

    public void setSymmetricResult(SymmetricResult symmetricResult) {
        this.symmetricResult = symmetricResult;
    }

    public SymmetricResult getAntisymmetricResult() {
        return antisymmetricResult;
    }

    public void setAntisymmetricResult(SymmetricResult antisymmetricResult) {
        this.antisymmetricResult = antisymmetricResult;
    }

    public TransitiveResult getTransitiveResult() {
        return transitiveResult;
    }

    public void setTransitiveResult(TransitiveResult transitiveResult) {
        this.transitiveResult = transitiveResult;
    }

    // 内部类：自反性/反自反性结果
    public static class ReflexiveResult {
        private boolean isReflexive;
        private String explanation;
        private String counterExample;

        public boolean isReflexive() {
            return isReflexive;
        }

        public void setReflexive(boolean reflexive) {
            isReflexive = reflexive;
        }

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }

        public String getCounterExample() {
            return counterExample;
        }

        public void setCounterExample(String counterExample) {
            this.counterExample = counterExample;
        }
    }

    // 内部类：对称性/反对称性结果
    public static class SymmetricResult {
        private boolean isSymmetric;
        private String explanation;
        private String counterExample1;
        private String counterExample2;

        public boolean isSymmetric() {
            return isSymmetric;
        }

        public void setSymmetric(boolean symmetric) {
            isSymmetric = symmetric;
        }

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }

        public String getCounterExample1() {
            return counterExample1;
        }

        public void setCounterExample1(String counterExample1) {
            this.counterExample1 = counterExample1;
        }

        public String getCounterExample2() {
            return counterExample2;
        }

        public void setCounterExample2(String counterExample2) {
            this.counterExample2 = counterExample2;
        }
    }

    // 内部类：传递性结果
    public static class TransitiveResult {
        private boolean isTransitive;
        private String explanation;
        private String counterExample1;
        private String counterExample2;
        private String counterExample3;

        public boolean isTransitive() {
            return isTransitive;
        }

        public void setTransitive(boolean transitive) {
            isTransitive = transitive;
        }

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }

        public String getCounterExample1() {
            return counterExample1;
        }

        public void setCounterExample1(String counterExample1) {
            this.counterExample1 = counterExample1;
        }

        public String getCounterExample2() {
            return counterExample2;
        }

        public void setCounterExample2(String counterExample2) {
            this.counterExample2 = counterExample2;
        }

        public String getCounterExample3() {
            return counterExample3;
        }

        public void setCounterExample3(String counterExample3) {
            this.counterExample3 = counterExample3;
        }
    }
}