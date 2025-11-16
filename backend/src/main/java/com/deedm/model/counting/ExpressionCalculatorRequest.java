package com.deedm.model.counting;

/**
 * 组合表达式计算请求模型
 */
public class ExpressionCalculatorRequest {
    private String expression;

    public ExpressionCalculatorRequest() {}

    public ExpressionCalculatorRequest(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "ExpressionCalculatorRequest{" +
                "expression='" + expression + '\'' +
                '}';
    }
}