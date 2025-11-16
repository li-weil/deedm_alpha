package com.deedm.model.counting;

public class GenRepCombRequest {
    private String baseSetString;
    private int length;
    private String startString;
    private int number;

    // 构造函数
    public GenRepCombRequest() {}

    // Getters and Setters
    public String getBaseSetString() {
        return baseSetString;
    }

    public void setBaseSetString(String baseSetString) {
        this.baseSetString = baseSetString;
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