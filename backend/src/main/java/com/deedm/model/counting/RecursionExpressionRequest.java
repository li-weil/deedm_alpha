package com.deedm.model.counting;

/**
 * 递归表达式计算请求模型
 */
public class RecursionExpressionRequest {
    private String expression;
    private String n;
    private String a;
    private String b;

    public RecursionExpressionRequest() {}

    public RecursionExpressionRequest(String expression, String n, String a, String b) {
        this.expression = expression;
        this.n = n;
        this.a = a;
        this.b = b;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
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
        return "RecursionExpressionRequest{" +
                "expression='" + expression + '\'' +
                ", n='" + n + '\'' +
                ", a='" + a + '\'' +
                ", b='" + b + '\'' +
                '}';
    }
}