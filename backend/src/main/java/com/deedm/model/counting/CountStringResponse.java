package com.deedm.model.counting;

import java.util.List;

public class CountStringResponse {
    private boolean success;
    private String errorMessage;

    // 基本信息和类型标记
    private String formula;
    private String type; // "countString"

    // 输入参数
    private String baseSet;
    private int length;
    private boolean allowRepetition;
    private String filterDescription;

    // 结果统计
    private int totalCount;       // 总字符串数
    private int acceptedCount;    // 满足条件的字符串数
    private String resultFormula; // 结果公式

    // 详细结果列表
    private List<StringResultDetail> stringDetails;

    // 构造函数
    public CountStringResponse() {
        this.success = true;
        this.type = "countString";
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

    public boolean isAllowRepetition() {
        return allowRepetition;
    }

    public void setAllowRepetition(boolean allowRepetition) {
        this.allowRepetition = allowRepetition;
    }

    public String getFilterDescription() {
        return filterDescription;
    }

    public void setFilterDescription(String filterDescription) {
        this.filterDescription = filterDescription;
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

    public String getResultFormula() {
        return resultFormula;
    }

    public void setResultFormula(String resultFormula) {
        this.resultFormula = resultFormula;
    }

    public List<StringResultDetail> getStringDetails() {
        return stringDetails;
    }

    public void setStringDetails(List<StringResultDetail> stringDetails) {
        this.stringDetails = stringDetails;
    }

    // 字符串详情内部类
    public static class StringResultDetail {
        private int index;
        private String content;
        private boolean accepted;
        private int acceptedCount; // 当前接受的计数

        public StringResultDetail(int index, String content, boolean accepted, int acceptedCount) {
            this.index = index;
            this.content = content;
            this.accepted = accepted;
            this.acceptedCount = acceptedCount;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public boolean isAccepted() {
            return accepted;
        }

        public void setAccepted(boolean accepted) {
            this.accepted = accepted;
        }

        public int getAcceptedCount() {
            return acceptedCount;
        }

        public void setAcceptedCount(int acceptedCount) {
            this.acceptedCount = acceptedCount;
        }
    }
}