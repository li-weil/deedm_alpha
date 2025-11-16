package com.deedm.model.counting;

public class CombCalculatorRequest {
    private int n;
    private int m;
    private boolean calculatePower;
    private boolean calculateCombination;
    private boolean calculatePermutation;
    private boolean calculateFactorial;

    // Getters and Setters
    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public boolean isCalculatePower() {
        return calculatePower;
    }

    public void setCalculatePower(boolean calculatePower) {
        this.calculatePower = calculatePower;
    }

    public boolean isCalculateCombination() {
        return calculateCombination;
    }

    public void setCalculateCombination(boolean calculateCombination) {
        this.calculateCombination = calculateCombination;
    }

    public boolean isCalculatePermutation() {
        return calculatePermutation;
    }

    public void setCalculatePermutation(boolean calculatePermutation) {
        this.calculatePermutation = calculatePermutation;
    }

    public boolean isCalculateFactorial() {
        return calculateFactorial;
    }

    public void setCalculateFactorial(boolean calculateFactorial) {
        this.calculateFactorial = calculateFactorial;
    }
}