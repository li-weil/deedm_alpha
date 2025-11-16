package com.deedm.model.counting;

public class CountEquationSolverRequest {
    // 基本参数
    private int varNumber;  // 未知数个数
    private int varSum;     // 未知数总和

    // 逻辑关系：true为AND，false为OR
    private boolean logicAnd = true;

    // 输出模式：onlyResult(只显示结果数)、onlyAccept(只显示符合条件的解)、allSolver(显示所有解)
    private String outputMode = "onlyResult";  // onlyResult, onlyAccept, allSolver

    // 约束条件组1 - 最多两个变量
    private Integer index11;  // 第一个变量的下标
    private Integer min11;    // 第一个变量的最小值，-1表示无限制
    private Integer max11;    // 第一个变量的最大值，-1表示无限制

    private Integer index12;  // 第二个变量的下标
    private Integer min12;    // 第二个变量的最小值
    private Integer max12;    // 第二个变量的最大值

    // 约束条件组2 - 最多两个变量
    private Integer index21;
    private Integer min21;
    private Integer max21;

    private Integer index22;
    private Integer min22;
    private Integer max22;

    // 约束条件组3 - 最多两个变量
    private Integer index31;
    private Integer min31;
    private Integer max31;

    private Integer index32;
    private Integer min32;
    private Integer max32;

    // Getters and Setters
    public int getVarNumber() {
        return varNumber;
    }

    public void setVarNumber(int varNumber) {
        this.varNumber = varNumber;
    }

    public int getVarSum() {
        return varSum;
    }

    public void setVarSum(int varSum) {
        this.varSum = varSum;
    }

    public boolean isLogicAnd() {
        return logicAnd;
    }

    public void setLogicAnd(boolean logicAnd) {
        this.logicAnd = logicAnd;
    }

    public String getOutputMode() {
        return outputMode;
    }

    public void setOutputMode(String outputMode) {
        this.outputMode = outputMode;
    }

    public Integer getIndex11() {
        return index11;
    }

    public void setIndex11(Integer index11) {
        this.index11 = index11;
    }

    public Integer getMin11() {
        return min11;
    }

    public void setMin11(Integer min11) {
        this.min11 = min11;
    }

    public Integer getMax11() {
        return max11;
    }

    public void setMax11(Integer max11) {
        this.max11 = max11;
    }

    public Integer getIndex12() {
        return index12;
    }

    public void setIndex12(Integer index12) {
        this.index12 = index12;
    }

    public Integer getMin12() {
        return min12;
    }

    public void setMin12(Integer min12) {
        this.min12 = min12;
    }

    public Integer getMax12() {
        return max12;
    }

    public void setMax12(Integer max12) {
        this.max12 = max12;
    }

    public Integer getIndex21() {
        return index21;
    }

    public void setIndex21(Integer index21) {
        this.index21 = index21;
    }

    public Integer getMin21() {
        return min21;
    }

    public void setMin21(Integer min21) {
        this.min21 = min21;
    }

    public Integer getMax21() {
        return max21;
    }

    public void setMax21(Integer max21) {
        this.max21 = max21;
    }

    public Integer getIndex22() {
        return index22;
    }

    public void setIndex22(Integer index22) {
        this.index22 = index22;
    }

    public Integer getMin22() {
        return min22;
    }

    public void setMin22(Integer min22) {
        this.min22 = min22;
    }

    public Integer getMax22() {
        return max22;
    }

    public void setMax22(Integer max22) {
        this.max22 = max22;
    }

    public Integer getIndex31() {
        return index31;
    }

    public void setIndex31(Integer index31) {
        this.index31 = index31;
    }

    public Integer getMin31() {
        return min31;
    }

    public void setMin31(Integer min31) {
        this.min31 = min31;
    }

    public Integer getMax31() {
        return max31;
    }

    public void setMax31(Integer max31) {
        this.max31 = max31;
    }

    public Integer getIndex32() {
        return index32;
    }

    public void setIndex32(Integer index32) {
        this.index32 = index32;
    }

    public Integer getMin32() {
        return min32;
    }

    public void setMin32(Integer min32) {
        this.min32 = min32;
    }

    public Integer getMax32() {
        return max32;
    }

    public void setMax32(Integer max32) {
        this.max32 = max32;
    }
}