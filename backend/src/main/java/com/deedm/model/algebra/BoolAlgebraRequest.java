package com.deedm.model.algebra;

public class BoolAlgebraRequest {
    private int m;
    private boolean showHasseDiagram;
    private boolean showOperationTables;
    private boolean showComplements;

    // Getters and Setters
    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public boolean isShowHasseDiagram() {
        return showHasseDiagram;
    }

    public void setShowHasseDiagram(boolean showHasseDiagram) {
        this.showHasseDiagram = showHasseDiagram;
    }

    public boolean isShowOperationTables() {
        return showOperationTables;
    }

    public void setShowOperationTables(boolean showOperationTables) {
        this.showOperationTables = showOperationTables;
    }

    public boolean isShowComplements() {
        return showComplements;
    }

    public void setShowComplements(boolean showComplements) {
        this.showComplements = showComplements;
    }
}