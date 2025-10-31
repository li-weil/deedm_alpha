package com.deedm.model.setrelfun;

public class SetExprOperationResponse {
    private boolean success;
    private String message;
    private String errorMessage;
    private String resultType;
    private String type; // 用于前端判断结果类型
    private String result;
    private String latexResult;
    private String formula;
    private int index;

    public SetExprOperationResponse() {}

    public SetExprOperationResponse(boolean success, String message) {
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getLatexResult() {
        return latexResult;
    }

    public void setLatexResult(String latexResult) {
        this.latexResult = latexResult;
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
        return "SetExprOperationResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", resultType='" + resultType + '\'' +
                ", type='" + type + '\'' +
                ", result='" + result + '\'' +
                ", latexResult='" + latexResult + '\'' +
                ", formula='" + formula + '\'' +
                ", index=" + index +
                '}';
    }
}