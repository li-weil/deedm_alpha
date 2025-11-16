package com.deedm.service.counting;

import com.deedm.model.counting.CountIntegerRequest;
import com.deedm.model.counting.CountIntegerResponse;
import com.deedm.legacy.counting.filter.*;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CountIntegerService {

    public CountIntegerResponse countIntegers(CountIntegerRequest request) {
        CountIntegerResponse response = new CountIntegerResponse();

        try {
            // 验证输入参数
            String validationError = validateInput(request.getStart(), request.getEnd());
            if (validationError != null) {
                response.setErrorMessage(validationError);
                return response;
            }

            // 设置基本信息
            response.setStart(request.getStart());
            response.setEnd(request.getEnd());
            response.setFormula("S = [" + request.getStart() + ", " + request.getEnd() + "]");
            response.setType("countInteger");

            // 构建过滤条件
            IntegerFilter filter = buildIntegerFilter(request.getFilterGroups(), request.getLogicRelation());
            if (filter != null) {
                response.setFilterDescription(filter.toLaTeXString());
            } else {
                response.setFilterDescription("\\text{无过滤条件}");
            }

            // 统计整数
            countAndAnalyzeIntegers(response, request.getStart(), request.getEnd(), filter, request.getOutputOption());

            return response;

        } catch (Exception e) {
            response.setErrorMessage("整数计数过程中发生错误: " + e.getMessage());
            return response;
        }
    }

    public String validateInput(int start, int end) {
        if (start < 0) {
            return "起始整数不能为负数";
        }
        if (end < 0) {
            return "终止整数不能为负数";
        }
        if (start > end) {
            return "起始整数不能大于终止整数";
        }
        return null; // null表示验证通过
    }

    public CountIntegerResponse getExample(String exampleType) {
        CountIntegerResponse response = new CountIntegerResponse();

        try {
            switch (exampleType) {
                case "example8_11":
                    response.setStart(1);
                    response.setEnd(1000);
                    response.setFilterDescription("\\text{能被3或5整除}");
                    response.setFormula("S = [1, 1000]");
                    break;
                case "example8_12_1":
                    response.setStart(100);
                    response.setEnd(999);
                    response.setFilterDescription("\\text{不能被3整除}");
                    response.setFormula("S = [100, 999]");
                    break;
                case "example8_12_2":
                    response.setStart(100);
                    response.setEnd(999);
                    response.setFilterDescription("\\text{不能被3或5整除}");
                    response.setFormula("S = [100, 999]");
                    break;
                case "example8_12_3":
                    response.setStart(100);
                    response.setEnd(999);
                    response.setFilterDescription("\\text{能被3整除且不能被5整除}");
                    response.setFormula("S = [100, 999]");
                    break;
                default:
                    response.setErrorMessage("未知的示例类型: " + exampleType);
                    return response;
            }

            response.setType("countInteger");
            response.setSuccess(true);
            return response;

        } catch (Exception e) {
            response.setErrorMessage("获取示例时发生错误: " + e.getMessage());
            return response;
        }
    }

    private IntegerFilter buildIntegerFilter(List<CountIntegerRequest.IntegerFilterGroup> filterGroups, String logicRelation) {
        if (filterGroups == null || filterGroups.isEmpty()) {
            return null;
        }

        List<IntegerFilter> groupFilters = new ArrayList<>();

        // 构建每个过滤组
        for (CountIntegerRequest.IntegerFilterGroup group : filterGroups) {
            IntegerFilter groupFilter = buildGroupFilter(group);
            if (groupFilter != null) {
                groupFilters.add(groupFilter);
            }
        }

        if (groupFilters.isEmpty()) {
            return null;
        }

        // 组合过滤器
        if (groupFilters.size() == 1) {
            return groupFilters.get(0);
        }

        IntegerFilter combinedFilter = groupFilters.get(0);
        for (int i = 1; i < groupFilters.size(); i++) {
            if ("OR".equalsIgnoreCase(logicRelation)) {
                OrGroupIntegerFilter orFilter = new OrGroupIntegerFilter(combinedFilter);
                orFilter.addFilter(groupFilters.get(i));
                combinedFilter = orFilter;
            } else {
                AndGroupIntegerFilter andFilter = new AndGroupIntegerFilter(combinedFilter);
                andFilter.addFilter(groupFilters.get(i));
                combinedFilter = andFilter;
            }
        }

        return combinedFilter;
    }

    private IntegerFilter buildGroupFilter(CountIntegerRequest.IntegerFilterGroup group) {
        if (group.getDivisionConditions() == null || group.getDivisionConditions().isEmpty()) {
            return null;
        }

        List<IntegerFilter> conditionFilters = new ArrayList<>();

        // 构建每个整除条件
        for (CountIntegerRequest.IntegerDivisionCondition condition : group.getDivisionConditions()) {
            IntegerFilter conditionFilter = buildDivisionFilter(condition);
            if (conditionFilter != null) {
                conditionFilters.add(conditionFilter);
            }
        }

        if (conditionFilters.isEmpty()) {
            return null;
        }

        // 同一组内的条件使用AND组合
        if (conditionFilters.size() == 1) {
            return conditionFilters.get(0);
        }

        IntegerFilter groupFilter = conditionFilters.get(0);
        for (int i = 1; i < conditionFilters.size(); i++) {
            AndGroupIntegerFilter andFilter = new AndGroupIntegerFilter(groupFilter);
            andFilter.addFilter(conditionFilters.get(i));
            groupFilter = andFilter;
        }

        return groupFilter;
    }

    private IntegerFilter buildDivisionFilter(CountIntegerRequest.IntegerDivisionCondition condition) {
        if (condition.getIntegers() == null || condition.getIntegers().trim().isEmpty()) {
            return null;
        }

        int[] integers = parseIntegers(condition.getIntegers());
        if (integers == null || integers.length == 0) {
            return null;
        }

        int flag = (condition.getDivisionType() == CountIntegerRequest.IntegerDivisionCondition.DivisionType.DIVIDED)
                   ? IntegerDivisionFilter.DIVIDED
                   : IntegerDivisionFilter.NOT_DIVIDED;

        return new IntegerDivisionFilter(flag, integers);
    }

    private int[] parseIntegers(String integerString) {
        try {
            String[] integerArray = integerString.split(",");
            int[] integers = new int[integerArray.length];

            for (int i = 0; i < integerArray.length; i++) {
                String trimmed = integerArray[i].trim();
                if (trimmed.isEmpty()) {
                    return null;
                }
                int value = Integer.parseInt(trimmed);
                if (value == 0) {
                    return null; // 不允许0作为除数
                }
                integers[i] = value;
            }
            return integers;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void countAndAnalyzeIntegers(CountIntegerResponse response, int start, int end, IntegerFilter filter, CountIntegerRequest.OutputOption outputOption) {
        int totalCount = 0;
        int acceptedCount = 0;
        List<CountIntegerResponse.IntegerResult> details = new ArrayList<>();

        boolean detailed = false;
        boolean onlyAccept = false;
        boolean accept50 = false;
        boolean part100 = false;

        if (outputOption != null) {
            switch (outputOption) {
                case ONLY_RESULT:
                    // 不显示详情
                    break;
                case ACCEPT_50:
                    accept50 = true;
                    break;
                case PARTIAL_100:
                    part100 = true;
                    break;
                case ONLY_ACCEPTED:
                    onlyAccept = true;
                    break;
                case ALL_INTEGERS:
                    detailed = true;
                    break;
            }
        }

        for (int integer = start; integer <= end; integer++) {
            totalCount++;
            String acceptMessage = "NOT accepted";
            boolean accepted = true;
            if (filter != null) accepted = filter.accept(integer);
            if (accepted) {
                acceptedCount++;
                acceptMessage = "accepted " + acceptedCount;
            }

            // 严格按照原Java应用逻辑决定是否记录详情
            boolean shouldRecord = false;
            if (detailed || onlyAccept) {
                shouldRecord = true;
            } else if ((part100 && totalCount <= 100) || (accept50 && acceptedCount <= 50)) {
                shouldRecord = true;
            }

            if (shouldRecord) {
                details.add(new CountIntegerResponse.IntegerResult(
                    totalCount,
                    integer,
                    accepted,
                    acceptMessage
                ));
            }
        }

        // 设置结果
        response.setTotalCount(totalCount);
        response.setAcceptedCount(acceptedCount);
        response.setDetailedResults(details);
    }
}