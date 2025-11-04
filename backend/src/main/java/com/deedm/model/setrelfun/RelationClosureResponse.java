package com.deedm.model.setrelfun;

import java.util.List;

public class RelationClosureResponse {
    private boolean success;
    private String errorMessage;
    private String type; // 标记结果类型

    // 基本信息
    private String formula;
    private String setAString;
    private String relationRString;
    private boolean intTypeElement;

    // 关系矩阵
    private String relationMatrix;
    private String relationGraphUrl;

    // 自反闭包结果
    private ClosureResult reflexiveClosureResult;

    // 对称闭包结果
    private ClosureResult symmetricClosureResult;

    // 传递闭包结果
    private TransitiveClosureResult transitiveClosureResult;

    // 等价闭包结果
    private ClosureResult equivalenceClosureResult;

    // 构造函数
    public RelationClosureResponse() {
        this.success = true;
        this.type = "relation-closure";
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

    public String getRelationMatrix() {
        return relationMatrix;
    }

    public void setRelationMatrix(String relationMatrix) {
        this.relationMatrix = relationMatrix;
    }

    public String getRelationGraphUrl() {
        return relationGraphUrl;
    }

    public void setRelationGraphUrl(String relationGraphUrl) {
        this.relationGraphUrl = relationGraphUrl;
    }

    public ClosureResult getReflexiveClosureResult() {
        return reflexiveClosureResult;
    }

    public void setReflexiveClosureResult(ClosureResult reflexiveClosureResult) {
        this.reflexiveClosureResult = reflexiveClosureResult;
    }

    public ClosureResult getSymmetricClosureResult() {
        return symmetricClosureResult;
    }

    public void setSymmetricClosureResult(ClosureResult symmetricClosureResult) {
        this.symmetricClosureResult = symmetricClosureResult;
    }

    public TransitiveClosureResult getTransitiveClosureResult() {
        return transitiveClosureResult;
    }

    public void setTransitiveClosureResult(TransitiveClosureResult transitiveClosureResult) {
        this.transitiveClosureResult = transitiveClosureResult;
    }

    public ClosureResult getEquivalenceClosureResult() {
        return equivalenceClosureResult;
    }

    public void setEquivalenceClosureResult(ClosureResult equivalenceClosureResult) {
        this.equivalenceClosureResult = equivalenceClosureResult;
    }

    // 内部类：一般闭包结果
    public static class ClosureResult {
        private String closureLaTeX;
        private String closureMatrix;
        private String closureGraphUrl;

        public String getClosureLaTeX() {
            return closureLaTeX;
        }

        public void setClosureLaTeX(String closureLaTeX) {
            this.closureLaTeX = closureLaTeX;
        }

        public String getClosureMatrix() {
            return closureMatrix;
        }

        public void setClosureMatrix(String closureMatrix) {
            this.closureMatrix = closureMatrix;
        }

        public String getClosureGraphUrl() {
            return closureGraphUrl;
        }

        public void setClosureGraphUrl(String closureGraphUrl) {
            this.closureGraphUrl = closureGraphUrl;
        }
    }

    // 内部类：传递闭包结果（包含详细信息）
    public static class TransitiveClosureResult {
        private String closureLaTeX;
        private String closureMatrix;
        private String closureGraphUrl;
        private List<String> detailMatrices;
        private List<String> detailDescriptions;
        private String algorithmUsed;

        public String getClosureLaTeX() {
            return closureLaTeX;
        }

        public void setClosureLaTeX(String closureLaTeX) {
            this.closureLaTeX = closureLaTeX;
        }

        public String getClosureMatrix() {
            return closureMatrix;
        }

        public void setClosureMatrix(String closureMatrix) {
            this.closureMatrix = closureMatrix;
        }

        public String getClosureGraphUrl() {
            return closureGraphUrl;
        }

        public void setClosureGraphUrl(String closureGraphUrl) {
            this.closureGraphUrl = closureGraphUrl;
        }

        public List<String> getDetailMatrices() {
            return detailMatrices;
        }

        public void setDetailMatrices(List<String> detailMatrices) {
            this.detailMatrices = detailMatrices;
        }

        public List<String> getDetailDescriptions() {
            return detailDescriptions;
        }

        public void setDetailDescriptions(List<String> detailDescriptions) {
            this.detailDescriptions = detailDescriptions;
        }

        public String getAlgorithmUsed() {
            return algorithmUsed;
        }

        public void setAlgorithmUsed(String algorithmUsed) {
            this.algorithmUsed = algorithmUsed;
        }
    }
}