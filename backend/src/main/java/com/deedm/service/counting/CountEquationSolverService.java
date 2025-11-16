package com.deedm.service.counting;

import com.deedm.model.counting.CountEquationSolverRequest;
import com.deedm.model.counting.CountEquationSolverResponse;
import com.deedm.legacy.counting.generator.EquationSolverGenerator;
import com.deedm.legacy.counting.filter.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CountEquationSolverService {

    public CountEquationSolverResponse solveEquation(CountEquationSolverRequest request) {
        CountEquationSolverResponse response = new CountEquationSolverResponse();

        try {
            // 构建方程LaTeX公式
            String equationLaTeX = buildEquationLaTeX(request.getVarNumber(), request.getVarSum());
            response.setFormula(equationLaTeX);
            response.setType("count-equation-solver");

            // 构建约束条件
            EquationSolverFilter filter = buildFilter(request);
            String filterLaTeX = filter != null ? filter.toLaTeXString() : null;
            response.setFilterLaTeX(filterLaTeX);

            // 生成所有解并筛选
            EquationSolverGenerator generator = new EquationSolverGenerator(request.getVarNumber(), request.getVarSum());
            generator.first();

            int totalCount = 0;
            int acceptedCount = 0;
            List<CountEquationSolverResponse.EquationSolution> solutions = new ArrayList<>();

            boolean detailed = "allSolver".equals(request.getOutputMode());
            boolean onlyAccept = "onlyAccept".equals(request.getOutputMode());

            // 处理第一个解
            int[] solver = generator.current();
            totalCount++;
            boolean accept = filter == null || filter.accept(solver);
            if (accept) {
                acceptedCount++;
            }

            // 根据输出模式决定是否保存解
            if (shouldSaveSolution(accept, detailed, onlyAccept)) {
                String solutionLaTeX = EquationSolverGenerator.convertSolverToString(solver);
                solutions.add(new CountEquationSolverResponse.EquationSolution(totalCount, solutionLaTeX, accept));
            }

            // 处理剩余的解
            while (!generator.isLast()) {
                generator.next();
                solver = generator.current();
                totalCount++;
                accept = filter == null || filter.accept(solver);
                if (accept) {
                    acceptedCount++;
                }

                if (shouldSaveSolution(accept, detailed, onlyAccept)) {
                    String solutionLaTeX = EquationSolverGenerator.convertSolverToString(solver);
                    solutions.add(new CountEquationSolverResponse.EquationSolution(totalCount, solutionLaTeX, accept));
                }
            }

            // 设置结果
            response.setTotalCount(totalCount);
            response.setAcceptedCount(acceptedCount);

            // 构建组合数学公式
            String combinationFormula = String.format("C(%d-1+%d, %d) = C(%d, %d) = %d",
                request.getVarNumber(), request.getVarSum(), request.getVarSum(),
                request.getVarNumber() - 1 + request.getVarSum(), request.getVarSum(), totalCount);
            response.setCombinationFormula(combinationFormula);

            // 根据输出模式设置解列表
            if (detailed || onlyAccept) {
                response.setSolutions(solutions);
            }

            return response;

        } catch (Exception e) {
            response.setErrorMessage("方程求解过程中发生错误: " + e.getMessage());
            return response;
        }
    }

    public String validateInput(CountEquationSolverRequest request) {
        try {
            if (request.getVarNumber() <= 0) {
                return "未知数个数必须是正整数";
            }
            if (request.getVarSum() < 0) {
                return "未知数总和必须是非负整数";
            }

            // 验证变量下标是否在有效范围内
            if (request.getIndex11() != null && (request.getIndex11() < 1 || request.getIndex11() > request.getVarNumber())) {
                return "变量下标 " + request.getIndex11() + " 超出有效范围 [1, " + request.getVarNumber() + "]";
            }
            if (request.getIndex12() != null && (request.getIndex12() < 1 || request.getIndex12() > request.getVarNumber())) {
                return "变量下标 " + request.getIndex12() + " 超出有效范围 [1, " + request.getVarNumber() + "]";
            }
            if (request.getIndex21() != null && (request.getIndex21() < 1 || request.getIndex21() > request.getVarNumber())) {
                return "变量下标 " + request.getIndex21() + " 超出有效范围 [1, " + request.getVarNumber() + "]";
            }
            if (request.getIndex22() != null && (request.getIndex22() < 1 || request.getIndex22() > request.getVarNumber())) {
                return "变量下标 " + request.getIndex22() + " 超出有效范围 [1, " + request.getVarNumber() + "]";
            }
            if (request.getIndex31() != null && (request.getIndex31() < 1 || request.getIndex31() > request.getVarNumber())) {
                return "变量下标 " + request.getIndex31() + " 超出有效范围 [1, " + request.getVarNumber() + "]";
            }
            if (request.getIndex32() != null && (request.getIndex32() < 1 || request.getIndex32() > request.getVarNumber())) {
                return "变量下标 " + request.getIndex32() + " 超出有效范围 [1, " + request.getVarNumber() + "]";
            }

            // 验证最小值和最大值的关系
            if (request.getMin11() != null && request.getMax11() != null && request.getMin11() > request.getMax11()) {
                return "最小值不能大于最大值";
            }
            if (request.getMin12() != null && request.getMax12() != null && request.getMin12() > request.getMax12()) {
                return "最小值不能大于最大值";
            }
            if (request.getMin21() != null && request.getMax21() != null && request.getMin21() > request.getMax21()) {
                return "最小值不能大于最大值";
            }
            if (request.getMin22() != null && request.getMax22() != null && request.getMin22() > request.getMax22()) {
                return "最小值不能大于最大值";
            }
            if (request.getMin31() != null && request.getMax31() != null && request.getMin31() > request.getMax31()) {
                return "最小值不能大于最大值";
            }
            if (request.getMin32() != null && request.getMax32() != null && request.getMin32() > request.getMax32()) {
                return "最小值不能大于最大值";
            }

            return null; // null表示验证通过

        } catch (Exception e) {
            return "输入验证过程中发生错误: " + e.getMessage();
        }
    }

    private String buildEquationLaTeX(int varNumber, int varSum) {
        StringBuilder equation = new StringBuilder();
        for (int i = 1; i <= varNumber; i++) {
            if (i > 1) equation.append(" + ");
            equation.append("x_{" + i + "}");
        }
        equation.append(" = " + varSum);
        return equation.toString();
    }

    private EquationSolverFilter buildFilter(CountEquationSolverRequest request) {
        EquationSolverFilter filter1 = buildGroupFilter(
            request.getIndex11(), request.getMin11(), request.getMax11(),
            request.getIndex12(), request.getMin12(), request.getMax12()
        );

        EquationSolverFilter filter2 = buildGroupFilter(
            request.getIndex21(), request.getMin21(), request.getMax21(),
            request.getIndex22(), request.getMin22(), request.getMax22()
        );

        EquationSolverFilter filter3 = buildGroupFilter(
            request.getIndex31(), request.getMin31(), request.getMax31(),
            request.getIndex32(), request.getMin32(), request.getMax32()
        );

        if (filter1 == null && filter2 == null && filter3 == null) {
            return null;
        }

        if (request.isLogicAnd()) {
            AndGroupEquationSolverFilter andFilter = new AndGroupEquationSolverFilter();
            if (filter1 != null) andFilter.addFilter(filter1);
            if (filter2 != null) andFilter.addFilter(filter2);
            if (filter3 != null) andFilter.addFilter(filter3);
            return andFilter;
        } else {
            OrGroupEquationSolverFilter orFilter = new OrGroupEquationSolverFilter();
            if (filter1 != null) orFilter.addFilter(filter1);
            if (filter2 != null) orFilter.addFilter(filter2);
            if (filter3 != null) orFilter.addFilter(filter3);
            return orFilter;
        }
    }

    private EquationSolverFilter buildGroupFilter(Integer index1, Integer min1, Integer max1,
                                                 Integer index2, Integer min2, Integer max2) {
        EquationSolverFilter filter1 = null;
        if (index1 != null && (min1 != null && min1 > 0 || max1 != null && max1 >= 0)) {
            int min = min1 != null ? min1 : -1;
            int max = max1 != null ? max1 : -1;
            filter1 = new EquationSolverRangeFilter(index1, min, max);
        }

        EquationSolverFilter filter2 = null;
        if (index2 != null && (min2 != null && min2 > 0 || max2 != null && max2 >= 0)) {
            int min = min2 != null ? min2 : -1;
            int max = max2 != null ? max2 : -1;
            filter2 = new EquationSolverRangeFilter(index2, min, max);
        }

        if (filter1 == null && filter2 == null) {
            return null;
        } else if (filter1 != null && filter2 == null) {
            return filter1;
        } else if (filter1 == null && filter2 != null) {
            return filter2;
        } else {
            AndGroupEquationSolverFilter andFilter = new AndGroupEquationSolverFilter(filter1);
            andFilter.addFilter(filter2);
            return andFilter;
        }
    }

    private boolean shouldSaveSolution(boolean accept, boolean detailed, boolean onlyAccept) {
        return detailed || (onlyAccept && accept);
    }
}