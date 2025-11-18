package com.deedm.controller.counting;

import com.deedm.model.counting.CountStringRequest;
import com.deedm.model.counting.CountStringResponse;
import com.deedm.service.counting.CountStringService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/count-string")
@CrossOrigin(origins = "*")
public class CountStringController {

    @Autowired
    private CountStringService countStringService;

    @PostMapping("/count")
    public CountStringResponse countStrings(@RequestBody Map<String, Object> requestMap) {
        try {
            // 手动解析请求，确保枚举正确转换
            CountStringRequest request = new CountStringRequest();

            request.setBaseSet((String) requestMap.get("baseSet"));
            request.setLength(((Number) requestMap.get("length")).intValue());
            request.setAllowRepetition((Boolean) requestMap.get("allowRepetition"));

            // 手动转换输出选项
            String outputOptionStr = (String) requestMap.get("outputOption");
            CountStringRequest.OutputOption outputOption = null;
            if (outputOptionStr != null) {
                try {
                    outputOption = CountStringRequest.OutputOption.valueOf(outputOptionStr);
                } catch (IllegalArgumentException e) {
                    System.out.println("无效的输出选项: " + outputOptionStr + ", 使用默认值");
                    outputOption = CountStringRequest.OutputOption.ONLY_RESULT;
                }
            }
            request.setOutputOption(outputOption);

            // 设置过滤条件和逻辑操作符
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> filterConditionsMaps = (List<Map<String, Object>>) requestMap.get("filterConditions");
            List<CountStringRequest.StringFilterCondition> filterConditions = null;

            if (filterConditionsMaps != null && !filterConditionsMaps.isEmpty()) {
                filterConditions = new java.util.ArrayList<>();
                for (Map<String, Object> conditionMap : filterConditionsMaps) {
                    CountStringRequest.StringFilterCondition condition = new CountStringRequest.StringFilterCondition();

                    // 解析位置过滤器
                    @SuppressWarnings("unchecked")
                    Map<String, Object> locationFilterMap = (Map<String, Object>) conditionMap.get("locationFilter");
                    if (locationFilterMap != null) {
                        CountStringRequest.LocationFilter locationFilter = new CountStringRequest.LocationFilter();
                        locationFilter.setPosition(((Number) locationFilterMap.get("position")).intValue());
                        locationFilter.setAppear((Boolean) locationFilterMap.get("appear"));
                        locationFilter.setChars((String) locationFilterMap.get("chars"));
                        condition.setLocationFilter(locationFilter);
                    }

                    // 解析子串过滤器
                    @SuppressWarnings("unchecked")
                    Map<String, Object> substringFilterMap = (Map<String, Object>) conditionMap.get("substringFilter");
                    if (substringFilterMap != null) {
                        CountStringRequest.SubstringFilter substringFilter = new CountStringRequest.SubstringFilter();

                        String typeStr = (String) substringFilterMap.get("type");
                        if (typeStr != null) {
                            try {
                                CountStringRequest.SubstringFilter.SubstringType type =
                                    CountStringRequest.SubstringFilter.SubstringType.valueOf(typeStr);
                                substringFilter.setType(type);
                            } catch (IllegalArgumentException e) {
                                System.out.println("无效的子串类型: " + typeStr);
                            }
                        }

                        substringFilter.setNumber(((Number) substringFilterMap.get("number")).intValue());
                        substringFilter.setSubstrings((String) substringFilterMap.get("substrings"));
                        condition.setSubstringFilter(substringFilter);
                    }

                    filterConditions.add(condition);
                }
            }
            request.setFilterConditions(filterConditions);

            @SuppressWarnings("unchecked")
            List<String> logicOperators = (List<String>) requestMap.get("logicOperators");
            request.setLogicOperators(logicOperators);

            // 添加调试信息
            System.out.println("=== 接收到的请求数据 ===");
            System.out.println("基集: " + request.getBaseSet());
            System.out.println("长度: " + request.getLength());
            System.out.println("允许重复: " + request.isAllowRepetition());
            System.out.println("输出选项字符串: " + outputOptionStr);
            System.out.println("输出选项枚举: " + request.getOutputOption());
            System.out.println("过滤条件数量: " + (filterConditions != null ? filterConditions.size() : 0));

            if (filterConditions != null && !filterConditions.isEmpty()) {
                for (int i = 0; i < filterConditions.size(); i++) {
                    CountStringRequest.StringFilterCondition condition = filterConditions.get(i);
                    System.out.println("  条件" + (i+1) + ":");

                    if (condition.getLocationFilter() != null) {
                        CountStringRequest.LocationFilter loc = condition.getLocationFilter();
                        System.out.println("    位置过滤器: 位置" + loc.getPosition() +
                                         ", " + (loc.isAppear() ? "必须" : "不能") +
                                         " 包含字符: " + loc.getChars());
                    }

                    if (condition.getSubstringFilter() != null) {
                        CountStringRequest.SubstringFilter sub = condition.getSubstringFilter();
                        System.out.println("    子串过滤器: 类型" + sub.getType() +
                                         ", 数量" + sub.getNumber() +
                                         ", 子串: " + sub.getSubstrings());
                    }
                }
            }

            System.out.println("逻辑操作符: " + request.getLogicOperators());
            System.out.println("========================");

            CountStringResponse response = countStringService.countStrings(request);
            return response;
        } catch (Exception e) {
            CountStringResponse errorResponse = new CountStringResponse();
            errorResponse.setErrorMessage("Internal server error: " + e.getMessage());
            return errorResponse;
        }
    }

    @PostMapping("/validate")
    public Map<String, Object> validateInput(@RequestBody Map<String, String> input) {
        try {
            String baseSet = input.get("baseSet");
            String lengthString = input.get("lengthString");

            String errorMessage = countStringService.validateInput(baseSet, lengthString);
            boolean isValid = errorMessage == null;

            return Map.of(
                "valid", isValid,
                "message", isValid ? "输入格式正确" : errorMessage
            );

        } catch (Exception e) {
            return Map.of(
                "valid", false,
                "message", "Internal server error: " + e.getMessage()
            );
        }
    }

    @PostMapping("/example")
    public CountStringResponse getExample(@RequestBody Map<String, String> request) {
        try {
            String exampleType = request.get("exampleType");
            CountStringResponse response = countStringService.getExample(exampleType);
            return response;
        } catch (Exception e) {
            CountStringResponse errorResponse = new CountStringResponse();
            errorResponse.setErrorMessage("Internal server error: " + e.getMessage());
            return errorResponse;
        }
    }
}