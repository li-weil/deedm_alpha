/**
 * 
 */
package com.deedm.legacy.counting;

import counting.filter.AndGroupEquationSolverFilter;
import counting.filter.EquationSolverFilter;
import counting.filter.StringFilter;
import counting.generator.CombinationGenerator;
import counting.generator.EquationSolverGenerator;
import counting.generator.PermutationGenerator;
import counting.generator.RepetitionCombinationGenerator;
import counting.generator.StringGenerator;

/**
 * @author user
 *
 */
public class TestStringGenerator {

	public static void main(String[] args) {
		testStringGenerator();
//		testCombinationGenerator();
//		testPermutationGenerator();
//		testRepetitionCombinationGenerator();
//		testEquationSolverGenerator();
//		testEquationSolverFilter();
//		testReptitionEquation();
//		testIntegerConter();
	}

	public static void testIntegerConter() {
		int counter = 0;
		int rep = 0;
		for (int i = 100; i < 1000; i++) {
			if (i % 3 == 0) {
				int a = i/100;
				int b = (i/10) % 10;
				int c = i% 10;
				if (a == b || b == c || a == c) {
					System.out.println(rep + " No accept " + i);
					rep = rep + 1;
				}
				counter = counter + 1;
			}
		}
		System.out.println("counter = " + counter + ", repetition = " + rep);
		
	}
	
	public static class Filter implements StringFilter {

		@Override
		public boolean accept(char[] string) {
			int count = 1;
			if (string[1] != string[0]) count = count + 1;
			if (string[2] != string[1] && string[2] != string[0]) count = count + 1;
			if (string[3] != string[0] && string[3] != string[1] && string[3] != string[2]) count = count + 1;
			
			if (count == 3) return true;
			return false;
		}

		@Override
		public String toLaTeXString() {
			return "Filter";
		}
	}
	
	public static void testStringGenerator() {
		char[] base = {'1', '2', '3', '4'};
		int length = 4;
		StringFilter filter = new Filter();
		
		StringGenerator generator = new StringGenerator(base, length);
		generator.first();
		int counter = 0, total = 0;
		char[] current = generator.current();
		if (filter.accept(current)) {
			counter = counter + 1;
			System.out.println(StringGenerator.convertToString(current) + ": ACCEPT " + counter);
		} else {
//			System.out.println(StringGenerator.convertToString(current) + ": NOT accept!");
		}
		total = total + 1;

		while (!generator.isLast()) {
			generator.next();
			current = generator.current();
			if (filter.accept(current)) {
				counter = counter + 1;
				System.out.println(StringGenerator.convertToString(current) + ": ACCEPT " + counter);
			} else {
//				System.out.println(StringGenerator.convertToString(current) + ": NOT accept!");
			}
			total = total + 1;
		}
		System.out.println("Total " + total + " strings, accept " + counter + "!");
	}
	
	public static void testCombinationGenerator() {
		char[] base = {'1', '2', '3', '4', '5', '6', '7', '8'};
		int length = 4;
		
		StringGenerator generator = new CombinationGenerator(base, length);
		generator.first();
		int counter = 1;
		System.out.println(StringGenerator.convertToString(generator.current()));
		while (!generator.isLast()) {
			generator.next();
			System.out.println(StringGenerator.convertToString(generator.current()));
			counter = counter + 1;
		}
		System.out.println("Total " + counter + " strings!");
	}
	
	public static void testPermutationGenerator() {
		char[] base = {'1', '2', '3', '4', '5', '6', '7', '8'};
		int length = 8;
		
		StringGenerator generator = new PermutationGenerator(base, length);
		generator.first();
		int counter = 1;
		System.out.println(StringGenerator.convertToString(generator.current()));
		while (!generator.isLast()) {
			generator.next();
			System.out.println(StringGenerator.convertToString(generator.current()));
			counter = counter + 1;
		}
		System.out.println("Total " + counter + " strings!");
	}
	
	public static void testRepetitionCombinationGenerator() {
		char[] base = {'1', '2', '3', '4'};
		int length = 8;
		
		StringGenerator generator = new RepetitionCombinationGenerator(base, length);
		generator.first();
		int counter = 1;
		System.out.println(StringGenerator.convertToString(generator.current()));
		while (!generator.isLast()) {
			generator.next();
			System.out.println(StringGenerator.convertToString(generator.current()));
			counter = counter + 1;
		}
		System.out.println("Total " + counter + " strings!");
	}
	
	public static void testEquationSolverGenerator() {
		int varNumber = 4;
		int sum = 8;
		
		EquationSolverGenerator generator = new EquationSolverGenerator(varNumber, sum);
		generator.first();
		int counter = 1;
		System.out.println(EquationSolverGenerator.convertSolverToString(generator.current()));
		while (!generator.isLast()) {
			generator.next();
			System.out.println(EquationSolverGenerator.convertSolverToString(generator.current()));
			counter = counter + 1;
		}
		System.out.println("Total " + counter + " solvers!");
	}

	public static void testEquationSolverFilter() {
		int varNumber = 3;
		int sum = 6;
		int[] minValue = {1, 1, 1};
		int[] maxValue = {6, 6, 6};
		int counter = 0;
		int[] solver = null;
		int totalCounter = 0;
		
		EquationSolverFilter filter = new AndGroupEquationSolverFilter(minValue, maxValue);
		EquationSolverGenerator generator = new EquationSolverGenerator(varNumber, sum);
		generator.first();
		solver = generator.current();
		if (filter.accept(solver)) {
			System.out.println("Accepted solver: " + EquationSolverGenerator.convertSolverToString(solver));
			counter++;
		} 
		totalCounter = totalCounter + 1; 
		while (!generator.isLast()) {
			generator.next();
			solver = generator.current();
			if (filter.accept(solver)) {
				System.out.println("Accepted solver: " + EquationSolverGenerator.convertSolverToString(solver));
				counter++;
			}
			totalCounter = totalCounter + 1; 
		}
		System.out.println("Total " + counter + " solvers, and total counter = " + totalCounter + "!");
	}
	
	public static void testReptitionEquation() {
		int varNumber = 3;
		int sum = 3;
		int counter = 0;
		int[] solver = null;
		int totalSum = 0;
		
		EquationSolverGenerator generator = new EquationSolverGenerator(varNumber, sum);
		generator.first();
		solver = generator.current();
		counter = counter + 1;
		int prod = 1;
		for (int i = 0; i < varNumber; i++) prod = prod * CombCalculator.factorial(solver[i]);
		totalSum = totalSum + prod;
		while (!generator.isLast()) {
			generator.next();
			solver = generator.current();
			System.out.println("Accepted solver: " + EquationSolverGenerator.convertSolverToString(solver));
			counter = counter + 1;
			prod = 1;
			for (int i = 0; i < varNumber; i++) prod = prod * CombCalculator.factorial(solver[i]);
			totalSum = totalSum + prod;
		}
		System.out.println("Total " + counter + " solvers, and total sum = " + totalSum + ", and power(" + varNumber + ", " + sum + ") = " + CombCalculator.power(varNumber, sum) + "!");
	}
}
