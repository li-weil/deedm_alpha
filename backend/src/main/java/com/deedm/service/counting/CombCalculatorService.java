package com.deedm.service.counting;

import com.deedm.model.counting.CombCalculatorRequest;
import com.deedm.model.counting.CombCalculatorResponse;
import com.deedm.legacy.counting.CombCalculator;

import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class CombCalculatorService {

    public CombCalculatorResponse calculateCombinations(CombCalculatorRequest request) {
        CombCalculatorResponse response = new CombCalculatorResponse();

        try {
            int n = request.getN();
            int m = request.getM();

            // 设置基本公式信息
            response.setFormula("n = " + n + ", \\quad m = " + m + " \\quad \\text{的计算结果如下：}");

            // 计算幂运算 n^m
            if (request.isCalculatePower()) {
                BigInteger result = CombCalculator.powerBigInteger(BigInteger.valueOf(n), m);
                response.setPowerResult(result.toString());
            }

            // 计算组合数 C(n, m)
            if (request.isCalculateCombination()) {
                BigInteger result = CombCalculator.chooseBigInteger(n, m);
                if (result.compareTo(BigInteger.valueOf(-1)) != 0) {
                    response.setCombinationResult(result.toString());
                } else {
                    response.setCombinationResult("NA");
                }
            }

            // 计算排列数 P(n, m)
            if (request.isCalculatePermutation()) {
                BigInteger result = CombCalculator.permutationBigInteger(n, m);
                if (result.compareTo(BigInteger.valueOf(-1)) != 0) {
                    response.setPermutationResult(result.toString());
                } else {
                    response.setPermutationResult("NA");
                }
            }

            // 计算阶乘 n! 和 m!
            if (request.isCalculateFactorial()) {
                BigInteger resultN = CombCalculator.factorialBigInteger(n);
                if (resultN.compareTo(BigInteger.valueOf(-1)) != 0) {
                    response.setFactorialNResult(resultN.toString());
                } else {
                    response.setFactorialNResult("NA");
                }

                BigInteger resultM = CombCalculator.factorialBigInteger(m);
                if (resultM.compareTo(BigInteger.valueOf(-1)) != 0) {
                    response.setFactorialMResult(resultM.toString());
                } else {
                    response.setFactorialMResult("NA");
                }
            }

            return response;

        } catch (Exception e) {
            response.setErrorMessage("排列组合计算过程中发生错误: " + e.getMessage());
            return response;
        }
    }

    public String validateInput(String nStr, String mStr) {
        try {
            int n = Integer.parseInt(nStr);
            int m = Integer.parseInt(mStr);

            if (n < 0 || m < 0) {
                return "n和m必须为非负整数";
            }

            if (n > 1000 || m > 1000) {
                return "n和m的值过大，建议控制在1000以内以避免计算时间过长";
            }

            return null; // null表示验证通过

        } catch (NumberFormatException e) {
            return "请输入有效的整数";
        }
    }
}