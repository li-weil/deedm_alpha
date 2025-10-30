package com.deedm.model.graph;

public class SpecialGraphRequest {
    private int n; // 顶点数
    private int m; // 用于完全二分图的第二部分顶点数
    private boolean showComplete; // 显示完全图
    private boolean showCycle; // 显示圈图
    private boolean showWheel; // 显示轮图
    private boolean showHypercube; // 显示超立方体
    private boolean showBigraph; // 显示完全二分图

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

    public boolean isShowComplete() {
        return showComplete;
    }

    public void setShowComplete(boolean showComplete) {
        this.showComplete = showComplete;
    }

    public boolean isShowCycle() {
        return showCycle;
    }

    public void setShowCycle(boolean showCycle) {
        this.showCycle = showCycle;
    }

    public boolean isShowWheel() {
        return showWheel;
    }

    public void setShowWheel(boolean showWheel) {
        this.showWheel = showWheel;
    }

    public boolean isShowHypercube() {
        return showHypercube;
    }

    public void setShowHypercube(boolean showHypercube) {
        this.showHypercube = showHypercube;
    }

    public boolean isShowBigraph() {
        return showBigraph;
    }

    public void setShowBigraph(boolean showBigraph) {
        this.showBigraph = showBigraph;
    }
}