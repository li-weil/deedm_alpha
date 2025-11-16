package com.deedm.model.counting;

public class GenPermutationRequest {
    private String baseSet;
    private int length;
    private String startString;
    private int number;

    // 构造函数
    public GenPermutationRequest() {}

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