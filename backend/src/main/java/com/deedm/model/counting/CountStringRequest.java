package com.deedm.model.counting;

import java.util.List;

public class CountStringRequest {
    private String baseSet;
    private int length;
    private boolean allowRepetition;

    // 过滤条件
    private List<StringFilterCondition> filterConditions;
    private List<String> logicOperators; // "AND", "OR"

    // 输出选项
    private OutputOption outputOption;

    // 输出选项枚举
    public enum OutputOption {
        ONLY_RESULT,           // 只显示结果
        ACCEPT_50,            // 显示前50个接受的字符串
        PARTIAL_100,          // 显示前100个字符串详情
        ONLY_ACCEPTED,        // 只显示接受的字符串
        ALL_STRINGS           // 显示所有字符串
    }

    // 字符串过滤条件内部类
    public static class StringFilterCondition {
        private LocationFilter locationFilter;
        private SubstringFilter substringFilter;

        public LocationFilter getLocationFilter() {
            return locationFilter;
        }

        public void setLocationFilter(LocationFilter locationFilter) {
            this.locationFilter = locationFilter;
        }

        public SubstringFilter getSubstringFilter() {
            return substringFilter;
        }

        public void setSubstringFilter(SubstringFilter substringFilter) {
            this.substringFilter = substringFilter;
        }
    }

    // 位置过滤条件
    public static class LocationFilter {
        private int position;
        private boolean appear; // true: 出现, false: 不出现
        private String chars;   // 用逗号分隔的字符

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public boolean isAppear() {
            return appear;
        }

        public void setAppear(boolean appear) {
            this.appear = appear;
        }

        public String getChars() {
            return chars;
        }

        public void setChars(String chars) {
            this.chars = chars;
        }
    }

    // 子串过滤条件
    public static class SubstringFilter {
        private SubstringType type;
        private int number;
        private String substrings; // 用逗号分隔的子串

        public enum SubstringType {
            EXACTLY,      // 恰好
            AT_LEAST,     // 至少
            NO_MORE_THAN, // 不超过
            NOT_CONTAIN   // 不包含
        }

        public SubstringType getType() {
            return type;
        }

        public void setType(SubstringType type) {
            this.type = type;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getSubstrings() {
            return substrings;
        }

        public void setSubstrings(String substrings) {
            this.substrings = substrings;
        }
    }

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

    public boolean isAllowRepetition() {
        return allowRepetition;
    }

    public void setAllowRepetition(boolean allowRepetition) {
        this.allowRepetition = allowRepetition;
    }

    public List<StringFilterCondition> getFilterConditions() {
        return filterConditions;
    }

    public void setFilterConditions(List<StringFilterCondition> filterConditions) {
        this.filterConditions = filterConditions;
    }

    public List<String> getLogicOperators() {
        return logicOperators;
    }

    public void setLogicOperators(List<String> logicOperators) {
        this.logicOperators = logicOperators;
    }

    public OutputOption getOutputOption() {
        return outputOption;
    }

    public void setOutputOption(OutputOption outputOption) {
        this.outputOption = outputOption;
    }
}