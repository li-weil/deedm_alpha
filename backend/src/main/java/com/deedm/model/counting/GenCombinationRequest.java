package com.deedm.model.counting;

public class GenCombinationRequest {
    private String baseSet;        // 基础字符集
    private int length;            // 组合长度
    private String startString;    // 起始字符串
    private int number;            // 生成数量

    // 构造函数
    public GenCombinationRequest() {}

    // Getters and Setters
    public String getBaseSet() {
        return baseSet;
    }

    public void setBaseSet(String baseSet) {
        this.baseSet = baseSet;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getStartString() {
        return startString;
    }

    public void setStartString(String startString) {
        this.startString = startString;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}