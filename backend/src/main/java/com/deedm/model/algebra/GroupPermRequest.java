package com.deedm.model.algebra;

public class GroupPermRequest {
    private int m; // 置换群的度数
    private boolean showCycleGroup;     // 是否循环群
    private boolean showElementPower;   // 群元素的幂
    private boolean showElementOrder;   // 群元素的阶
    private boolean showSubgroups;      // 所有非平凡子群
    private boolean showCosets;         // 非平凡子群的陪集
    private boolean showNormalSubgroups; // 正规子群

    // Getters and Setters
    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public boolean isShowCycleGroup() {
        return showCycleGroup;
    }

    public void setShowCycleGroup(boolean showCycleGroup) {
        this.showCycleGroup = showCycleGroup;
    }

    public boolean isShowElementPower() {
        return showElementPower;
    }

    public void setShowElementPower(boolean showElementPower) {
        this.showElementPower = showElementPower;
    }

    public boolean isShowElementOrder() {
        return showElementOrder;
    }

    public void setShowElementOrder(boolean showElementOrder) {
        this.showElementOrder = showElementOrder;
    }

    public boolean isShowSubgroups() {
        return showSubgroups;
    }

    public void setShowSubgroups(boolean showSubgroups) {
        this.showSubgroups = showSubgroups;
    }

    public boolean isShowCosets() {
        return showCosets;
    }

    public void setShowCosets(boolean showCosets) {
        this.showCosets = showCosets;
    }

    public boolean isShowNormalSubgroups() {
        return showNormalSubgroups;
    }

    public void setShowNormalSubgroups(boolean showNormalSubgroups) {
        this.showNormalSubgroups = showNormalSubgroups;
    }
}