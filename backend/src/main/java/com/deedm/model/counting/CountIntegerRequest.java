package com.deedm.model.counting;

import java.util.List;

public class CountIntegerRequest {
    private int start;
    private int end;
    private String logicRelation; // "AND", "OR"

    // 三组过滤条件
    private List<IntegerFilterGroup> filterGroups;

    // 输出选项
    private OutputOption outputOption;

    // 输出选项枚举
    public enum OutputOption {
        ONLY_RESULT,           // 只显示结果
        ACCEPT_50,            // 显示前50个接受的整数
        PARTIAL_100,          // 显示前100个整数详情
        ONLY_ACCEPTED,        // 只显示接受的整数
        ALL_INTEGERS          // 显示所有整数
    }

    // 整数过滤条件组内部类
    public static class IntegerFilterGroup {
        private List<IntegerDivisionCondition> divisionConditions;

        public List<IntegerDivisionCondition> getDivisionConditions() {
            return divisionConditions;
        }

        public void setDivisionConditions(List<IntegerDivisionCondition> divisionConditions) {
            this.divisionConditions = divisionConditions;
        }
    }

    // 整除条件内部类
    public static class IntegerDivisionCondition {
        private DivisionType divisionType;
        private String integers; // 用逗号分隔的整数

        public enum DivisionType {
            DIVIDED,       // 能被整除
            NOT_DIVIDED    // 不能被整除
        }

        public DivisionType getDivisionType() {
            return divisionType;
        }

        public void setDivisionType(DivisionType divisionType) {
            this.divisionType = divisionType;
        }

        public String getIntegers() {
            return integers;
        }

        public void setIntegers(String integers) {
            this.integers = integers;
        }
    }

    // Getters and Setters
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

    public String getLogicRelation() {
        return logicRelation;
    }

    public void setLogicRelation(String logicRelation) {
        this.logicRelation = logicRelation;
    }

    public List<IntegerFilterGroup> getFilterGroups() {
        return filterGroups;
    }

    public void setFilterGroups(List<IntegerFilterGroup> filterGroups) {
        this.filterGroups = filterGroups;
    }

    public OutputOption getOutputOption() {
        return outputOption;
    }

    public void setOutputOption(OutputOption outputOption) {
        this.outputOption = outputOption;
    }
}