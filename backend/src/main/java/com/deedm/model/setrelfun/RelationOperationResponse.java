package com.deedm.model.setrelfun;

public class RelationOperationResponse {
    private boolean success;
    private String message;
    private String errorMessage;
    private String resultType;
    private String type; // 用于前端判断结果类型
    private String setA;
    private String setB;
    private String setC;
    private String relationR;
    private String relationS;
    private String relationRLatex;
    private String relationSLatex;
    private String intersectionResult;
    private String unionResult;
    private String subtractResult;
    private String inverseRResult;
    private String inverseSResult;
    private String compositeResult;
    private String invcompResult;
    private String relationRMatrix;
    private String relationSMatrix;
    private String relationRGraphImageUrl;
    private String relationSGraphImageUrl;
    private String formula;
    private int index;

    public RelationOperationResponse() {}

    public RelationOperationResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSetA() {
        return setA;
    }

    public void setSetA(String setA) {
        this.setA = setA;
    }

    public String getSetB() {
        return setB;
    }

    public void setSetB(String setB) {
        this.setB = setB;
    }

    public String getSetC() {
        return setC;
    }

    public void setSetC(String setC) {
        this.setC = setC;
    }

    public String getRelationR() {
        return relationR;
    }

    public void setRelationR(String relationR) {
        this.relationR = relationR;
    }

    public String getRelationS() {
        return relationS;
    }

    public void setRelationS(String relationS) {
        this.relationS = relationS;
    }

    public String getRelationRLatex() {
        return relationRLatex;
    }

    public void setRelationRLatex(String relationRLatex) {
        this.relationRLatex = relationRLatex;
    }

    public String getRelationSLatex() {
        return relationSLatex;
    }

    public void setRelationSLatex(String relationSLatex) {
        this.relationSLatex = relationSLatex;
    }

    public String getIntersectionResult() {
        return intersectionResult;
    }

    public void setIntersectionResult(String intersectionResult) {
        this.intersectionResult = intersectionResult;
    }

    public String getUnionResult() {
        return unionResult;
    }

    public void setUnionResult(String unionResult) {
        this.unionResult = unionResult;
    }

    public String getSubtractResult() {
        return subtractResult;
    }

    public void setSubtractResult(String subtractResult) {
        this.subtractResult = subtractResult;
    }

    public String getInverseRResult() {
        return inverseRResult;
    }

    public void setInverseRResult(String inverseRResult) {
        this.inverseRResult = inverseRResult;
    }

    public String getInverseSResult() {
        return inverseSResult;
    }

    public void setInverseSResult(String inverseSResult) {
        this.inverseSResult = inverseSResult;
    }

    public String getCompositeResult() {
        return compositeResult;
    }

    public void setCompositeResult(String compositeResult) {
        this.compositeResult = compositeResult;
    }

    public String getInvcompResult() {
        return invcompResult;
    }

    public void setInvcompResult(String invcompResult) {
        this.invcompResult = invcompResult;
    }

    public String getRelationRMatrix() {
        return relationRMatrix;
    }

    public void setRelationRMatrix(String relationRMatrix) {
        this.relationRMatrix = relationRMatrix;
    }

    public String getRelationSMatrix() {
        return relationSMatrix;
    }

    public void setRelationSMatrix(String relationSMatrix) {
        this.relationSMatrix = relationSMatrix;
    }

    public String getRelationRGraphImageUrl() {
        return relationRGraphImageUrl;
    }

    public void setRelationRGraphImageUrl(String relationRGraphImageUrl) {
        this.relationRGraphImageUrl = relationRGraphImageUrl;
    }

    public String getRelationSGraphImageUrl() {
        return relationSGraphImageUrl;
    }

    public void setRelationSGraphImageUrl(String relationSGraphImageUrl) {
        this.relationSGraphImageUrl = relationSGraphImageUrl;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "RelationOperationResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", resultType='" + resultType + '\'' +
                ", type='" + type + '\'' +
                ", setA='" + setA + '\'' +
                ", setB='" + setB + '\'' +
                ", setC='" + setC + '\'' +
                ", relationR='" + relationR + '\'' +
                ", relationS='" + relationS + '\'' +
                ", relationRLatex='" + relationRLatex + '\'' +
                ", relationSLatex='" + relationSLatex + '\'' +
                ", intersectionResult='" + intersectionResult + '\'' +
                ", unionResult='" + unionResult + '\'' +
                ", subtractResult='" + subtractResult + '\'' +
                ", inverseRResult='" + inverseRResult + '\'' +
                ", inverseSResult='" + inverseSResult + '\'' +
                ", compositeResult='" + compositeResult + '\'' +
                ", invcompResult='" + invcompResult + '\'' +
                ", relationRMatrix='" + relationRMatrix + '\'' +
                ", relationSMatrix='" + relationSMatrix + '\'' +
                ", formula='" + formula + '\'' +
                ", index=" + index +
                '}';
    }
}