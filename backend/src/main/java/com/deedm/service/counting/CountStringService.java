package com.deedm.service.counting;

import com.deedm.model.counting.CountStringRequest;
import com.deedm.model.counting.CountStringResponse;
import com.deedm.legacy.counting.*;
import com.deedm.legacy.counting.filter.*;
import com.deedm.legacy.counting.generator.StringGenerator;
import com.deedm.legacy.counting.generator.PermutationGenerator;
import com.deedm.legacy.setrelfun.Set;
import com.deedm.legacy.setrelfun.SetrelfunUtil;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CountStringService {

    public CountStringResponse countStrings(CountStringRequest request) {
        CountStringResponse response = new CountStringResponse();

        try {
            // 验证输入参数
            String validationError = validateInput(request.getBaseSet(), String.valueOf(request.getLength()));
            if (validationError != null) {
                response.setErrorMessage(validationError);
                return response;
            }

            // 提取基础字符集
            char[] elements = SetrelfunUtil.extractSetElements(request.getBaseSet(), false);
            if (elements == null) {
                response.setErrorMessage("基础字符集格式不正确: " + SetrelfunUtil.getExtractErrorMessage());
                return response;
            }

            Set baseSet = new Set(elements);

            // 设置基本信息
            response.setBaseSet(baseSet.toLaTeXString());
            response.setLength(request.getLength());
            response.setAllowRepetition(request.isAllowRepetition());
            response.setFormula("B = " + baseSet.toLaTeXString() + ", \\, n = " + request.getLength());

            // 构建过滤条件
            StringFilter filter = buildStringFilter(request.getFilterConditions(), request.getLogicOperators());
            if (filter != null) {
                response.setFilterDescription(filter.toLaTeXString());
            } else {
                response.setFilterDescription("\\text{无过滤条件}");
            }

            // 生成字符串并统计
            generateAndCountStrings(response, elements, request.getLength(), request.isAllowRepetition(), filter, request.getOutputOption());

            return response;

        } catch (Exception e) {
            response.setErrorMessage("字符串计数过程中发生错误: " + e.getMessage());
            return response;
        }
    }

    public String validateInput(String baseSet, String lengthString) {
        // 验证基础字符集
        char[] elements = SetrelfunUtil.extractSetElements(baseSet, false);
        if (elements == null) {
            return "基础字符集格式不正确: " + SetrelfunUtil.getExtractErrorMessage();
        }

        // 验证长度
        try {
            int length = Integer.parseInt(lengthString);
            if (length <= 0) {
                return "字符串长度必须是正整数";
            }
        } catch (NumberFormatException e) {
            return "字符串长度必须是有效的整数";
        }

        return null; // null表示验证通过
    }

    public CountStringResponse getExample(String exampleType) {
        CountStringResponse response = new CountStringResponse();

        try {
            switch (exampleType) {
                case "example8_5":
                    response.setBaseSet("\\{0, 1, 2, 3, 4, 5\\}");
                    response.setLength(2);
                    response.setAllowRepetition(false);
                    response.setFilterDescription("\\text{无过滤条件}");
                    break;
                case "example8_7":
                    response.setBaseSet("\\{0, 1, 2, 3, 4, 5, 6, 7, 8, 9\\}");
                    response.setLength(3);
                    response.setAllowRepetition(true);
                    response.setFilterDescription("s[0] \\neq \\{0\\} \\land |\\{s : s \\text{包含} \\{5\\} \\text{中串}\\}| \\geq 1");
                    break;
                case "example8_22":
                    response.setBaseSet("\\{0, 1, 2, 3, 4, 5, 6, 7, 8, 9\\}");
                    response.setLength(6);
                    response.setAllowRepetition(false);
                    response.setFilterDescription("s[0] \\neq \\{0\\} \\land |\\{s : s \\text{包含} \\{12\\} \\text{中串}\\}| \\neq 1");
                    break;
                case "example8_23_1":
                    response.setBaseSet("\\{0, 1\\}");
                    response.setLength(10);
                    response.setAllowRepetition(true);
                    response.setFilterDescription("|\\{s : s \\text{包含} \\{1\\} \\text{中串}\\}| = 3");
                    break;
                case "example8_23_2":
                    response.setBaseSet("\\{0, 1\\}");
                    response.setLength(10);
                    response.setAllowRepetition(true);
                    response.setFilterDescription("|\\{s : s \\text{包含} \\{1\\} \\text{中串}\\}| \\geq 3");
                    break;
                case "example8_23_3":
                    response.setBaseSet("\\{0, 1\\}");
                    response.setLength(10);
                    response.setAllowRepetition(true);
                    response.setFilterDescription("|\\{s : s \\text{包含} \\{1\\} \\text{中串}\\}| \\leq 3");
                    break;
                case "example8_23_4":
                    response.setBaseSet("\\{0, 1\\}");
                    response.setLength(10);
                    response.setAllowRepetition(true);
                    response.setFilterDescription("|\\{s : s \\text{包含} \\{1\\} \\text{中串}\\}| \\geq 6");
                    break;
                case "example8_26_1":
                    response.setBaseSet("\\{0, 1, 2, 3, 4, 5, 6, 7, 8, 9\\}");
                    response.setLength(6);
                    response.setAllowRepetition(true);
                    response.setFilterDescription("s[0] \\neq \\{0\\} \\land |\\{s : s \\text{包含} \\{1, 3, 5, 7, 9\\} \\text{中串}\\}| \\geq 1");
                    break;
                case "example8_26_2":
                    response.setBaseSet("\\{0, 1, 2, 3, 4, 5, 6, 7, 8, 9\\}");
                    response.setLength(6);
                    response.setAllowRepetition(true);
                    response.setFilterDescription("s[0] \\neq \\{0\\} \\land |\\{s : s \\text{包含} \\{0, 2, 4, 6, 8\\} \\text{中串}\\}| \\geq 1");
                    break;
                case "example8_26_3":
                    response.setBaseSet("\\{0, 1, 2, 3, 4, 5, 6, 7, 8, 9\\}");
                    response.setLength(6);
                    response.setAllowRepetition(true);
                    response.setFilterDescription("s[0] \\neq \\{0\\} \\land |\\{s : s \\text{包含} \\{1, 3, 5, 7, 9\\} \\text{中串}\\}| \\geq 2");
                    break;
                case "example8_26_4":
                    response.setBaseSet("\\{0, 1, 2, 3, 4, 5, 6, 7, 8, 9\\}");
                    response.setLength(6);
                    response.setAllowRepetition(true);
                    response.setFilterDescription("s[0] \\neq \\{0\\} \\land |\\{s : s \\text{包含} \\{0, 2, 4, 6, 8\\} \\text{中串}\\}| \\geq 2");
                    break;
                default:
                    response.setErrorMessage("未知的示例类型: " + exampleType);
                    return response;
            }

            response.setSuccess(true);
            return response;

        } catch (Exception e) {
            response.setErrorMessage("获取示例时发生错误: " + e.getMessage());
            return response;
        }
    }

    private StringFilter buildStringFilter(List<CountStringRequest.StringFilterCondition> conditions, List<String> logicOperators) {
        if (conditions == null || conditions.isEmpty()) {
            return null;
        }

        List<StringFilter> filters = new ArrayList<>();

        // 构建每个条件对应的过滤器
        for (CountStringRequest.StringFilterCondition condition : conditions) {
            StringFilter conditionFilter = buildConditionFilter(condition);
            if (conditionFilter != null) {
                filters.add(conditionFilter);
            }
        }

        if (filters.isEmpty()) {
            return null;
        }

        // 组合过滤器
        if (filters.size() == 1) {
            return filters.get(0);
        }

        StringFilter combinedFilter = filters.get(0);
        for (int i = 1; i < filters.size(); i++) {
            if (i - 1 < logicOperators.size()) {
                String operator = logicOperators.get(i - 1);
                if ("OR".equalsIgnoreCase(operator)) {
                    OrGroupStringFilter orFilter = new OrGroupStringFilter(combinedFilter);
                    orFilter.addFilter(filters.get(i));
                    combinedFilter = orFilter;
                } else {
                    AndGroupStringFilter andFilter = new AndGroupStringFilter(combinedFilter);
                    andFilter.addFilter(filters.get(i));
                    combinedFilter = andFilter;
                }
            } else {
                // 默认使用AND连接
                AndGroupStringFilter andFilter = new AndGroupStringFilter(combinedFilter);
                andFilter.addFilter(filters.get(i));
                combinedFilter = andFilter;
            }
        }

        return combinedFilter;
    }

    private StringFilter buildConditionFilter(CountStringRequest.StringFilterCondition condition) {
        StringFilter locationFilter = null;
        StringFilter substringFilter = null;

        // 构建位置过滤器
        if (condition.getLocationFilter() != null) {
            CountStringRequest.LocationFilter locFilter = condition.getLocationFilter();
            if (locFilter.getChars() != null && !locFilter.getChars().trim().isEmpty()) {
                char[] chars = getLocationChars(locFilter.getChars());
                int flag = locFilter.isAppear() ? StringLocationFilter.APPEAR : StringLocationFilter.NOT_APPEAR;
                locationFilter = new StringLocationFilter(flag, locFilter.getPosition(), chars);
            }
        }

        // 构建子串过滤器
        if (condition.getSubstringFilter() != null) {
            CountStringRequest.SubstringFilter subFilter = condition.getSubstringFilter();
            if (subFilter.getSubstrings() != null && !subFilter.getSubstrings().trim().isEmpty()) {
                int flag = convertSubstringType(subFilter.getType());
                String[] substrings = getSubstrings(subFilter.getSubstrings());
                substringFilter = new StringSubstringFilter(flag, subFilter.getNumber(), substrings);
            }
        }

        // 组合同一条件内的位置和子串过滤器（使用AND）
        if (locationFilter != null && substringFilter != null) {
            AndGroupStringFilter andFilter = new AndGroupStringFilter(locationFilter);
            andFilter.addFilter(substringFilter);
            return andFilter;
        } else if (locationFilter != null) {
            return locationFilter;
        } else if (substringFilter != null) {
            return substringFilter;
        }

        return null;
    }

    private int convertSubstringType(CountStringRequest.SubstringFilter.SubstringType type) {
        switch (type) {
            case EXACTLY: return StringSubstringFilter.EXACTLY;
            case AT_LEAST: return StringSubstringFilter.AT_LEAST;
            case NO_MORE_THAN: return StringSubstringFilter.NO_MORE_THAN;
            case NOT_CONTAIN: return StringSubstringFilter.NOT_CONTAIN;
            default: return StringSubstringFilter.EXACTLY;
        }
    }

    private char[] getLocationChars(String charString) {
        String[] chars = charString.split(",");
        char[] result = new char[chars.length];
        for (int i = 0; i < result.length; i++) {
            String temp = chars[i].trim();
            if (temp.length() > 0) {
                result[i] = temp.charAt(0);
            }
        }
        return result;
    }

    private String[] getSubstrings(String substringString) {
        String[] substrings = substringString.split(",");
        for (int i = 0; i < substrings.length; i++) {
            substrings[i] = substrings[i].trim();
        }
        return substrings;
    }

    private void generateAndCountStrings(CountStringResponse response, char[] elements, int length, boolean allowRepetition, StringFilter filter, CountStringRequest.OutputOption outputOption) {
        System.out.println("=== 字符串计数调试信息 ===");
        System.out.println("基集元素: " + new String(elements) + " (长度: " + elements.length + ")");
        System.out.println("字符串长度: " + length);
        System.out.println("是否允许重复: " + allowRepetition);
        System.out.println("过滤器: " + (filter == null ? "无" : filter.toString()));

        StringGenerator generator;
        if (allowRepetition) {
            generator = new StringGenerator(elements, length);
            System.out.println("使用StringGenerator，预期总数: " + Math.pow(elements.length, length));
        } else {
            generator = new PermutationGenerator(elements, length);
            System.out.println("使用PermutationGenerator，预期总数: P(" + elements.length + ", " + length + ")");
        }

        generator.first();
        int totalCount = 0;
        int acceptedCount = 0;
        List<CountStringResponse.StringResultDetail> details = new ArrayList<>();

        boolean detailed = false;
        boolean onlyAccept = false;
        boolean accept50 = false;
        boolean part100 = false;

        if (outputOption != null) {
            switch (outputOption) {
                case ONLY_RESULT:
                    // 只给出计数结果，不显示字符串详情
                    break;
                case ACCEPT_50:
                    // 给出至多50个接受的字符串（只显示被接受的，最多50个）
                    accept50 = true;
                    break;
                case PARTIAL_100:
                    // 给出至多100个字符串的情况（显示前100个字符串，包括接受和不接受的）
                    part100 = true;
                    break;
                case ONLY_ACCEPTED:
                    // 只给出满足条件的串（显示所有被接受的字符串）
                    onlyAccept = true;
                    break;
                case ALL_STRINGS:
                    // 给出所有的字符串（显示所有生成的字符串，包括接受和不接受的）
                    detailed = true;
                    break;
            }
        }

        System.out.println("输出选项: " + (outputOption != null ? outputOption.toString() : "null"));
        System.out.println("detailed=" + detailed + ", onlyAccept=" + onlyAccept + ", accept50=" + accept50 + ", part100=" + part100);
        System.out.println("开始生成字符串...");

        int debugCounter = 0;
        while (true) {
            char[] string = generator.current();
            totalCount++;
            debugCounter++;

            String currentStr = StringGenerator.convertToString(string);
            boolean accepted = (filter == null) || filter.accept(string);

            if (accepted) {
                acceptedCount++;
            }

            // 调试输出前20个字符串
            if (debugCounter <= 20) {
                System.out.println(String.format("第%3d个字符串: %s, 接受: %s, 累计接受: %d",
                    totalCount, currentStr, accepted ? "是" : "否", acceptedCount));
            }

            // 根据输出选项决定是否记录详情
            boolean shouldRecord = false;
            if (detailed) {
                // 显示所有字符串
                shouldRecord = true;
            } else if (onlyAccept) {
                // 只显示满足条件的字符串
                shouldRecord = accepted;
            } else if (part100) {
                // 显示至多100个字符串（包括接受和不接受的）
                shouldRecord = totalCount <= 100;
            } else if (accept50) {
                // 只显示前50个被接受的字符串
                shouldRecord = accepted && acceptedCount <= 50;
            }

            if (shouldRecord) {
                details.add(new CountStringResponse.StringResultDetail(
                    totalCount,
                    currentStr,
                    accepted,
                    acceptedCount
                ));
            }

            if (generator.isLast()) {
                break;
            }
            generator.next();
        }

        System.out.println("字符串生成完成:");
        System.out.println("实际生成总数: " + totalCount);
        System.out.println("接受总数: " + acceptedCount);
        System.out.println("详情记录数: " + details.size());
        System.out.println("=== 调试信息结束 ===");

        // 设置结果
        response.setTotalCount(totalCount);
        response.setAcceptedCount(acceptedCount);
        response.setStringDetails(details);

        // 生成结果公式
        String resultFormula;
        if (allowRepetition) {
            resultFormula = elements.length + "^{" + length + "} = " + totalCount;
        } else {
            resultFormula = "P(" + elements.length + ", " + length + ") = " + totalCount;
        }
        response.setResultFormula(resultFormula);
    }
}