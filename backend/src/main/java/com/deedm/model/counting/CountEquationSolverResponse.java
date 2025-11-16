package com.deedm.model.counting;

import java.util.List;

public class CountEquationSolverResponse {
    private boolean success;
    private String errorMessage;

    // 基本信息和公式
    private String type;  // 类型标记，用于前端识别
    private String formula;  // 方程的LaTeX表示
    private String filterLaTeX;  // 约束条件的LaTeX表示

    // 计算结果
    private int totalCount;  // 总解数（无约束条件下的所有解）
    private int acceptedCount;  // 符合条件的解数
    private String combinationFormula;  // 组合数学公式的LaTeX表示，如C(n-1+s, s)

    // 详细的解列表（根据输出模式决定是否包含）
    private List<EquationSolution> solutions;

    // 构造函数
    public CountEquationSolverResponse() {
        this.success = true;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        this.success = false;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getFilterLaTeX() {
        return filterLaTeX;
    }

    public void setFilterLaTeX(String filterLaTeX) {
        this.filterLaTeX = filterLaTeX;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getAcceptedCount() {
        return acceptedCount;
    }

    public void setAcceptedCount(int acceptedCount) {
        this.acceptedCount = acceptedCount;
    }

    public String getCombinationFormula() {
        return combinationFormula;
    }

    public void setCombinationFormula(String combinationFormula) {
        this.combinationFormula = combinationFormula;
    }

    public List<EquationSolution> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<EquationSolution> solutions) {
        this.solutions = solutions;
    }

    // 内部类：单个解
    public static class EquationSolution {
        private int number;  // 解的序号
        private String solutionLaTeX;  // 解的LaTeX表示
        private boolean accepted;  // 是否符合条件

        public EquationSolution() {}

        public EquationSolution(int number, String solutionLaTeX, boolean accepted) {
            this.number = number;
            this.solutionLaTeX = solutionLaTeX;
            this.accepted = accepted;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getSolutionLaTeX() {
            return solutionLaTeX;
        }

        public void setSolutionLaTeX(String solutionLaTeX) {
            this.solutionLaTeX = solutionLaTeX;
        }

        public boolean isAccepted() {
            return accepted;
        }

        public void setAccepted(boolean accepted) {
            this.accepted = accepted;
        }
    }
}