package com.deedm.model.algebra;

public class GroupUmRequest {
    private int m;
    private boolean showCycleGroup;
    private boolean showPower;
    private boolean showOrder;
    private boolean showSubgroups;
    private boolean showCosets;

    public GroupUmRequest() {}

    public GroupUmRequest(int m, boolean showCycleGroup, boolean showPower, boolean showOrder, boolean showSubgroups, boolean showCosets) {
        this.m = m;
        this.showCycleGroup = showCycleGroup;
        this.showPower = showPower;
        this.showOrder = showOrder;
        this.showSubgroups = showSubgroups;
        this.showCosets = showCosets;
    }

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

    public boolean isShowPower() {
        return showPower;
    }

    public void setShowPower(boolean showPower) {
        this.showPower = showPower;
    }

    public boolean isShowOrder() {
        return showOrder;
    }

    public void setShowOrder(boolean showOrder) {
        this.showOrder = showOrder;
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
}