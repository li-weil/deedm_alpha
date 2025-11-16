package com.deedm.model.counting;

import java.util.List;

public class CountIntegerResponse {
    private boolean success;
    private String errorMessage;

    // 基本信息
    private String formula;
    private String type; // 类型标记
    private int start;
    private int end;
    private int totalCount;
    private int acceptedCount;

    // 过滤条件描述
    private String filterDescription;

    // 详细结果
    private List<IntegerResult> detailedResults;

    // 构造函数
    public CountIntegerResponse() {
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

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
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

    public String getFilterDescription() {
        return filterDescription;
    }

    public void setFilterDescription(String filterDescription) {
        this.filterDescription = filterDescription;
    }

    public List<IntegerResult> getDetailedResults() {
        return detailedResults;
    }

    public void setDetailedResults(List<IntegerResult> detailedResults) {
        this.detailedResults = detailedResults;
    }

    // 内部类：整数结果
    public static class IntegerResult {
        private int index;
        private int value;
        private boolean accepted;
        private String acceptMessage;

        public IntegerResult() {}

        public IntegerResult(int index, int value, boolean accepted, String acceptMessage) {
            this.index = index;
            this.value = value;
            this.accepted = accepted;
            this.acceptMessage = acceptMessage;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public boolean isAccepted() {
            return accepted;
        }

        public void setAccepted(boolean accepted) {
            this.accepted = accepted;
        }

        public String getAcceptMessage() {
            return acceptMessage;
        }

        public void setAcceptMessage(String acceptMessage) {
            this.acceptMessage = acceptMessage;
        }
    }
}