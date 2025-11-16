package com.deedm.model.counting;

/**
 * 递归表达式计算响应模型
 */
public class RecursionExpressionResponse {
    private boolean success;
    private String message;
    private String result;
    private String originalExpression;
    private String formula;
    private String resultType;
    private String n;
    private String a;
    private String b;

    public RecursionExpressionResponse() {}

    public RecursionExpressionResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public RecursionExpressionResponse(boolean success, String message, String result,
                                      String originalExpression, String formula, String n, String a, String b) {
        this.success = success;
        this.message = message;
        this.result = result;
        this.originalExpression = originalExpression;
        this.formula = formula;
        this.resultType = "recursion_expression";
        this.n = n;
        this.a = a;
        this.b = b;
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

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    @Override
    public String toString() {
        return "RecursionExpressionResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", result='" + result + '\'' +
                ", originalExpression='" + originalExpression + '\'' +
                ", formula='" + formula + '\'' +
                ", resultType='" + resultType + '\'' +
                ", n='" + n + '\'' +
                ", a='" + a + '\'' +
                ", b='" + b + '\'' +
                '}';
    }
}