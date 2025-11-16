package com.deedm.model.counting;

/**
 * 组合表达式计算响应模型
 */
public class ExpressionCalculatorResponse {
    private boolean success;
    private String message;
    private String result;
    private String originalExpression;
    private String formula;
    private String resultType;

    public ExpressionCalculatorResponse() {}

    public ExpressionCalculatorResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ExpressionCalculatorResponse(boolean success, String message, String result,
                                      String originalExpression, String formula) {
        this.success = success;
        this.message = message;
        this.result = result;
        this.originalExpression = originalExpression;
        this.formula = formula;
        this.resultType = "expression_calculator";
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getOriginalExpression() {
        return originalExpression;
    }

    public void setOriginalExpression(String originalExpression) {
        this.originalExpression = originalExpression;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    @Override
    public String toString() {
        return "ExpressionCalculatorResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", result='" + result + '\'' +
                ", originalExpression='" + originalExpression + '\'' +
                ", formula='" + formula + '\'' +
                ", resultType='" + resultType + '\'' +
                '}';
    }
}