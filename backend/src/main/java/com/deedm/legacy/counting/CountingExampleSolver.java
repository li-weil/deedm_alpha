package com.deedm.legacy.counting;

import java.io.File;
import java.io.PrintStream;

import counting.filter.AndGroupEquationSolverFilter;
import counting.filter.AndGroupIntegerFilter;
import counting.filter.AndGroupStringFilter;
import counting.filter.IntegerDivisionFilter;
import counting.filter.StringFilter;
import counting.filter.StringLocationFilter;
import counting.filter.StringRepetitionFilter;
import counting.filter.StringSubstringFilter;
import counting.generator.FunctionGenerator;
import counting.generator.PermutationGenerator;
import counting.generator.StringGenerator;
import setrelfun.Function;
import setrelfun.Set;
import util.Debug;

public class CountingExampleSolver {

	static class Example1_Filter implements StringFilter {
		
		@Override
		public boolean accept(char[] string) {
			int[] oneIndex = new int[4];
			int indexCounter = 0;
			int length = (int)(string.length/2);
			
			for (int i = 0; i < string.length; i++) {
				if (string[i] == '1') {
					if (indexCounter >= 4) return false;
					oneIndex[indexCounter] = i;
					indexCounter++;
				}
			}
			
			if (indexCounter != 4) return false;
			int paired = 0;
			for (int i = 0; i < oneIndex.length; i++) {
				boolean hasPaired = false;
				for (int j = i+1; j < oneIndex.length; j++) {
					if ((oneIndex[i]+length) == oneIndex[j]) {
						hasPaired = true;
						break;
					}
				}
				if (hasPaired == true) paired++;
			}
			if (paired != 1) return false;
			return true;
		}

		@Override
		public String toLaTeXString() {
			// TODO Auto-generated method stub
			return "Example";
		}
	}
	
	static class Example2_Filter implements StringFilter {
		
		@Override
		public boolean accept(char[] string) {
			if (string.length != 16) return false;
			int counter = 0;
			for (int i = 0; i < 8; i++) {
				if (string[i] == '1') counter++;
			}
			if (counter < 2) return false;
			int first = counter;
			counter = 0;
			for (int i = 8; i < 16; i++) {
				if (string[i] == '1') counter++;
			}
			if (counter < 2 || (first+counter != 5)) return false;
			
			return true;
		}

		@Override
		public String toLaTeXString() {
			// TODO Auto-generated method stub
			return "Example";
		}
	}

	static class Example_Filter implements StringFilter {
		
		@Override
		public boolean accept(char[] string) {
			int counter1 = 0;
			int counter2 = 0;
			int counter3 = 0;
			int counter4 = 0;
			for (int i = 0; i < string.length; i++) {
				if (string[i] == '1') counter1++;
				if (string[i] == '2') counter2++;
				if (string[i] == '3') counter3++;
				if (string[i] == '4') counter4++;
			}
			
			if (counter1 > 0 && counter2 > 0 && counter3 > 0 && counter4 > 0) return true;
			return false;
		}

		@Override
		public String toLaTeXString() {
			// TODO Auto-generated method stub
			return "Example";
		}
	}
	
	public static void example() {
		String rootPath = "E:\\ZxcWork\\Deedm\\data\\";
		String resultFile = rootPath + "test.txt";
		
		PrintStream writer = new PrintStream(System.out); 
		try {
			writer = new PrintStream(new File(resultFile));
		} catch (Exception exc) {
			exc.printStackTrace();
			return;
		}
		
		char[] base2 = {'1', '2', '3', '4'};
		StringCounter counter = new StringCounter(base2, 6);
		counter.setWriter(writer, true);
		Debug.setStart("Begin....");
		int result = counter.counting(new Example_Filter());
		Debug.time("end!");
		System.out.println("Result = " + result);
		
		writer.close();
	}

	static class Example8_5_Filter implements StringFilter {

		@Override
		public boolean accept(char[] string) {
			if (string[0] == '0') return false;
			if (string[0] == string[1]) return false;
			return true;
		}
		
		@Override
		public String toString() {
			return "Filter for example 8.5";
		}

		@Override
		public String toLaTeXString() {
			return "Filter for example 8.5";
		}
	}
	
	public static void example8_5() {
//		String rootPath = "E:\\ZxcWork\\DiscreteTools\\data\\";
//		String resultFile = rootPath + "test.txt";
		
		PrintStream writer = new PrintStream(System.out); 
//		try {
//			writer = new PrintStream(new File(resultFile));
//		} catch (Exception exc) {
//			exc.printStackTrace();
//			return;
//		}
		
		char[] base6 = {'0', '1', '2', '3', '4', '5'};
		StringCounter counter = new StringCounter(base6, 2);
		counter.setWriter(writer, false);
		Debug.setStart("Use old filter....");
		int result = counter.counting(new Example8_5_Filter());
		Debug.time("Use old filter...., end!");
		System.out.println("Result = " + result);
		
		char[] charArray = {'0'};
		AndGroupStringFilter filter = new AndGroupStringFilter(new StringRepetitionFilter());
		filter.addFilter(new StringLocationFilter(StringLocationFilter.NOT_APPEAR, 0, charArray));
		Debug.setStart("Use new filter....");
		result = counter.counting(filter);
		Debug.time("Use new filter...., end!");
		System.out.println("Result = " + result);
//		writer.close();
	}
	
	static class Example8_7_Filter implements StringFilter {

		@Override
		public boolean accept(char[] string) {
			if (string[0] == '0') return false;
			for (int i = 0; i < string.length; i++) 
				if (string[i] == '5') return true;
			return false;
		}
		
		@Override
		public String toString() {
			return "Filter for example 8.7";
		}

		@Override
		public String toLaTeXString() {
			return "Filter for example 8.7";
		}
	}
	
	public static void example8_7() {
//		String rootPath = "E:\\ZxcWork\\DiscreteTools\\data\\";
//		String resultFile = rootPath + "test.txt";
		
		PrintStream writer = System.out; 
//		try {
//			writer = new PrintStream(new File(resultFile));
//		} catch (Exception exc) {
//			exc.printStackTrace();
//			return;
//		}
		String[] substrings = {"5"};
		char[] charArray = {'0'};
		AndGroupStringFilter filter = new AndGroupStringFilter(new StringSubstringFilter(StringSubstringFilter.AT_LEAST, 1, substrings));
		filter.addFilter(new StringLocationFilter(StringLocationFilter.NOT_APPEAR, 0, charArray));
		
		char[] base10 = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
		StringCounter counter = new StringCounter(base10, 3);
		counter.setWriter(writer, false);

		Debug.setStart("Use old filter....");
		int result = counter.counting(new Example8_7_Filter());
		Debug.time("Use old filter...., end!");
		System.out.println("Result = " + result);
		
		Debug.setStart("Use new filter....");
		result = counter.counting(filter);
		Debug.time("Use new filter...., end!");
		System.out.println("Result = " + result);
		
//		writer.close();
	}
	
	public static void problem8_11() {
		int maxValue = 1000;
		int counter = 0;
		int[] dividens = {3, 5};
		int filterCounter = 0;
		AndGroupIntegerFilter filter = new AndGroupIntegerFilter(new IntegerDivisionFilter(IntegerDivisionFilter.DIVIDED, dividens));
		
		for (int i = 1; i <= maxValue; i++) {
			if (i % 3 == 0 || i % 5 == 0) counter++;
			if (filter.accept(i)) filterCounter++;
		}
		System.out.println("For those positive integers which are less than or equal to 1000: ");
		System.out.println("\tThere are " + counter + " integers which can be divided by 3 or 5!");
		System.out.println("\tUsing filter, there are " + filterCounter + " integers which can be divided by 3 or 5!");
	}

	public static void problem8_12() {
		int startValue = 100;
		int maxValue = 999;
		int counter1 = 0;
		int counter2 = 0;
		int counter3 = 0;
		
		int[] dividen1 = {3};
		int[] dividen2 = {3, 5};
		int[] dividen3 = {5};
		int filterCounter1 = 0;
		int filterCounter2 = 0;
		int filterCounter3 = 0;
		
		AndGroupIntegerFilter filter1= new AndGroupIntegerFilter(new IntegerDivisionFilter(IntegerDivisionFilter.NOT_DIVIDED, dividen1));
		AndGroupIntegerFilter filter2= new AndGroupIntegerFilter(new IntegerDivisionFilter(IntegerDivisionFilter.NOT_DIVIDED, dividen2));
		AndGroupIntegerFilter filter3= new AndGroupIntegerFilter(new IntegerDivisionFilter(IntegerDivisionFilter.DIVIDED, dividen1));
		filter3.addFilter(new IntegerDivisionFilter(IntegerDivisionFilter.NOT_DIVIDED, dividen3));
		
		for (int i = startValue; i <= maxValue; i++) {
			if (i % 3 != 0) counter1++;
			if (i % 3 != 0 && i % 5 != 0) counter2++;
			if (i % 3 == 0 && i % 5 != 0) counter3++;
			
			if (filter1.accept(i)) filterCounter1++;
			if (filter2.accept(i)) filterCounter2++;
			if (filter3.accept(i)) filterCounter3++;
		}
		System.out.println("For those positive integers which has three digitals: ");
		System.out.println("\tThere are " + counter1 + " integers which can not be divided by 3!");
		System.out.println("\tThere are " + counter2 + " integers which can not be divided by 3 or 5!");
		System.out.println("\tThere are " + counter3 + " integers which can be divided by 3 but not be divided by 5!");
		System.out.println("\tUsing filter, there are " + filterCounter1 + " integers which can not be divided by 3!");
		System.out.println("\tUsing filter, there are " + filterCounter2 + " integers which can not be divided by 3 or 5!");
		System.out.println("\tUsing filter, there are " + filterCounter3 + " integers which can be divided by 3 but not be divided by 5!");
	}

	static class Example8_22_Filter implements StringFilter {

		@Override
		public boolean accept(char[] string) {
			if (string[0] == '0') return false;
			for (int i = 0; i < string.length-1; i++) 
				if (string[i] == '1' && string[i+1] == '2') return false;
			return true;
		}
		
		@Override
		public String toString() {
			return "Filter for problem 8.22";
		}

		@Override
		public String toLaTeXString() {
			return "Filter for problem 8.22";
		}
	}
	
	public static void problem8_22() {
//		String rootPath = "E:\\ZxcWork\\DiscreteTools\\data\\";
//		String resultFile = rootPath + "test.txt";
		
		PrintStream writer = System.out; 
//		try {
//			writer = new PrintStream(new File(resultFile));
//		} catch (Exception exc) {
//			exc.printStackTrace();
//			return;
//		}
		
		String[] substrings = {"12"};
		char[] charArray = {'0'};
		AndGroupStringFilter filter = new AndGroupStringFilter(new StringSubstringFilter(StringSubstringFilter.EXACTLY, 0, substrings));
		filter.addFilter(new StringLocationFilter(StringLocationFilter.NOT_APPEAR, 0, charArray));

		char[] base10 = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
		StringGenerator generator = new PermutationGenerator(base10, 6);
		StringCounter counter = new StringCounter(generator);
		counter.setWriter(writer, false);

		Debug.setStart("Use old filter....");
		int result = counter.counting(new Example8_22_Filter());
		Debug.time("Use old filter...., end!");
		System.out.println("Result = " + result);

		Debug.setStart("Use new filter....");
		result = counter.counting(new Example8_22_Filter());
		Debug.time("Use new filter...., end!");
		System.out.println("Result = " + result);
		
//		writer.close();
	}
	
	static class Example8_23_1_Filter implements StringFilter {

		@Override
		public boolean accept(char[] string) {
			int oneCounter = 0;
			for (int i = 0; i < string.length; i++) { 
				if (string[i] == '1') {
					oneCounter++;
					if (oneCounter > 3) return false;
				}
			}
			if (oneCounter == 3) return true;
			else return false;
		}
		
		@Override
		public String toString() {
			return "Filter for problem 8.23(1)";
		}

		@Override
		public String toLaTeXString() {
			return "Filter for problem 8.23(1)";
		}
	}
	
	static class Example8_23_2_Filter implements StringFilter {

		@Override
		public boolean accept(char[] string) {
			int oneCounter = 0;
			for (int i = 0; i < string.length; i++) { 
				if (string[i] == '1') {
					oneCounter++;
					if (oneCounter >= 3) return true;
				}
			}
			return false;
		}
		
		@Override
		public String toString() {
			return "Filter for problem 8.23(2)";
		}

		@Override
		public String toLaTeXString() {
			return "Filter for problem 8.23(2)";
		}
	}
	
	static class Example8_23_3_Filter implements StringFilter {

		@Override
		public boolean accept(char[] string) {
			int oneCounter = 0;
			for (int i = 0; i < string.length; i++) { 
				if (string[i] == '1') {
					oneCounter++;
					if (oneCounter > 3) return false;
				}
			}
			if (oneCounter <= 3) return true;
			else return false;
		}
		
		@Override
		public String toString() {
			return "Filter for problem 8.23(3)";
		}

		@Override
		public String toLaTeXString() {
			return "Filter for problem 8.23(3)";
		}
	}

	static class Example8_23_4_Filter implements StringFilter {

		@Override
		public boolean accept(char[] string) {
			int oneCounter = 0;
			for (int i = 0; i < string.length; i++) { 
				if (string[i] == '1') oneCounter++;
			}
			int zeroCounter = string.length - oneCounter;
			if (oneCounter > zeroCounter) return true;
			else return false;
		}
		
		@Override
		public String toString() {
			return "Filter for problem 8.23(4)";
		}

		@Override
		public String toLaTeXString() {
			return "Filter for problem 8.23(4)";
		}
	}
	
	public static void problem8_23() {
//		String rootPath = "E:\\ZxcWork\\DiscreteTools\\data\\";
//		String resultFile = rootPath + "test.txt";
		
//		PrintStream writer = System.out; 
//		try {
//			writer = new PrintStream(new File(resultFile));
//		} catch (Exception exc) {
//			exc.printStackTrace();
//			return;
//		}
		
		char[] base2 = {'0', '1'};
		StringGenerator generator = new StringGenerator(base2, 10);
		StringCounter counter = new StringCounter(generator);
//		counter.setWriter(writer, false);
		
		Debug.setStart("Use old filter....");
		int result1= counter.counting(new Example8_23_1_Filter());
		int result2= counter.counting(new Example8_23_2_Filter());
		int result3= counter.counting(new Example8_23_3_Filter());
		int result4= counter.counting(new Example8_23_4_Filter());
		Debug.time("Use old filter...., end!");
		System.out.println("Exactly contains three 1s, result = " + result1);
		System.out.println("Contains at least three 1s, result = " + result2);
		System.out.println("Contains at most three 1s, result = " + result3);
		System.out.println("Contains 1 more than 0, result = " + result4);

		String[] substrings = {"1"};
		Debug.setStart("Use new filter....");
		result1= counter.counting(new AndGroupStringFilter(new StringSubstringFilter(StringSubstringFilter.EXACTLY, 3, substrings)));
		result2= counter.counting(new AndGroupStringFilter(new StringSubstringFilter(StringSubstringFilter.AT_LEAST, 3, substrings)));
		result3= counter.counting(new StringSubstringFilter(StringSubstringFilter.NO_MORE_THAN, 3, substrings));
		result4= counter.counting(new StringSubstringFilter(StringSubstringFilter.AT_LEAST, 6, substrings));
		Debug.time("Use new filter...., end!");
		System.out.println("Exactly contains three 1s, result = " + result1);
		System.out.println("Contains at least three 1s, result = " + result2);
		System.out.println("Contains at most three 1s, result = " + result3);
		System.out.println("Contains 1 more than 0, result = " + result4);
		
		
//		writer.close();
	}

	private static boolean isOdd(char ch) {
		if (ch == '1' || ch == '3' || ch == '5' || ch == '7' || ch == '9') return true;
		return false;
	}
	
	static class Example8_26_1_Filter implements StringFilter {

		@Override
		public boolean accept(char[] string) {
			if (string[0] == '0') return false;
			for (int i = 0; i < string.length; i++) {
				if (isOdd(string[i])) return true;
			}
			return false;
		}
		
		@Override
		public String toString() {
			return "Filter for problem 8.26(1)";
		}

		@Override
		public String toLaTeXString() {
			return "Filter for problem 8.26(1)";
		}
	}
	
	static class Example8_26_2_Filter implements StringFilter {

		@Override
		public boolean accept(char[] string) {
			if (string[0] == '0') return false;
			for (int i = 0; i < string.length; i++) {
				if (!isOdd(string[i])) return true;
			}
			return false;
		}
		
		@Override
		public String toString() {
			return "Filter for problem 8.26(2)";
		}

		@Override
		public String toLaTeXString() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	static class Example8_26_3_Filter implements StringFilter {

		@Override
		public boolean accept(char[] string) {
			if (string[0] == '0') return false;
			int oddCounter = 0;
			for (int i = 0; i < string.length; i++) { 
				if (isOdd(string[i])) {
					oddCounter++;
					if (oddCounter >= 2) return true;
				}
			}
			return false;
		}
		
		@Override
		public String toString() {
			return "Filter for problem 8.26(3)";
		}

		@Override
		public String toLaTeXString() {
			return "Filter for problem 8.26(3)";
		}
	}

	static class Example8_26_4_Filter implements StringFilter {

		@Override
		public boolean accept(char[] string) {
			if (string[0] == '0') return false;
			int evenCounter = 0;
			for (int i = 0; i < string.length; i++) { 
				if (!isOdd(string[i])) {
					evenCounter++;
					if (evenCounter >= 2) return true;
				}
			}
			return false;
		}
		
		@Override
		public String toString() {
			return "Filter for problem 8.26(4)";
		}

		@Override
		public String toLaTeXString() {
			return "Filter for problem 8.26(4)";
		}
	}
	
	public static void problem8_26() {
//		String rootPath = "E:\\ZxcWork\\DiscreteTools\\data\\";
//		String resultFile = rootPath + "test.txt";
		
//		PrintStream writer = System.out; 
//		try {
//			writer = new PrintStream(new File(resultFile));
//		} catch (Exception exc) {
//			exc.printStackTrace();
//			return;
//		}
		
		char[] base10 = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
		StringGenerator generator = new StringGenerator(base10, 6);
		StringCounter counter = new StringCounter(generator);
//		counter.setWriter(writer, false);
		
		Debug.setStart("Use old filter....!");
		int result1= counter.counting(new Example8_26_1_Filter());
		int result2= counter.counting(new Example8_26_2_Filter());
		int result3= counter.counting(new Example8_26_3_Filter());
		int result4= counter.counting(new Example8_26_4_Filter());
		Debug.time("Use old filter...., end!");
		System.out.println("Contains odd numbers, result = " + result1);
		System.out.println("Contains even numbers, result = " + result2);
		System.out.println("Contains at least 2 odd numbers, result = " + result3);
		System.out.println("Contains at least 2 even numbers, result = " + result4);

		String[] oddNumbers = {"1", "3", "5", "7", "9"};
		String[] evenNumbers = {"0", "2", "4", "6", "8"};
		char[] charArray = {'0'};

		Debug.setStart("Use new filter....!");
		AndGroupStringFilter filter = new AndGroupStringFilter(new StringLocationFilter(StringLocationFilter.NOT_APPEAR, 0, charArray));
		filter.addFilter(new StringSubstringFilter(StringSubstringFilter.AT_LEAST, 1, oddNumbers));
		result1= counter.counting(filter);
		
		filter = new AndGroupStringFilter(new StringLocationFilter(StringLocationFilter.NOT_APPEAR, 0, charArray));
		filter.addFilter(new StringSubstringFilter(StringSubstringFilter.AT_LEAST, 1, evenNumbers));
		result2= counter.counting(filter);
		
		filter = new AndGroupStringFilter(new StringLocationFilter(StringLocationFilter.NOT_APPEAR, 0, charArray));
		filter.addFilter(new StringSubstringFilter(StringSubstringFilter.AT_LEAST, 2, oddNumbers));
		result3= counter.counting(new Example8_26_3_Filter());

		filter = new AndGroupStringFilter(new StringLocationFilter(StringLocationFilter.NOT_APPEAR, 0, charArray));
		filter.addFilter(new StringSubstringFilter(StringSubstringFilter.AT_LEAST, 2, evenNumbers));
		result4= counter.counting(new Example8_26_4_Filter());
		Debug.time("Use new filter...., end!");
		
		System.out.println("Contains odd numbers, result = " + result1);
		System.out.println("Contains even numbers, result = " + result2);
		System.out.println("Contains at least 2 odd numbers, result = " + result3);
		System.out.println("Contains at least 2 even numbers, result = " + result4);
		
//		writer.close();
	}
	
	public static void problem8_32() {
//		String rootPath = "E:\\ZxcWork\\DiscreteTools\\data\\";
//		String resultFile = rootPath + "test.txt";
		
//		PrintStream writer = System.out; 
//		try {
//			writer = new PrintStream(new File(resultFile));
//		} catch (Exception exc) {
//			exc.printStackTrace();
//			return;
//		}
		
		int varNumber = 4;
		int sum = 15;
		EquationSolverCounter counter = new EquationSolverCounter(varNumber, sum);
		int[] minValue = {3, 4, 0, 0};
		int[] maxValue = {15, 15, 15, 15};
//		counter.setWriter(writer, false);
		int result = counter.counting(new AndGroupEquationSolverFilter(minValue, maxValue));
		System.out.println("Equation x_1+x_2+x_3+x_4 = 15, x_1>=3, x_2>=4, result = " + result);
//		writer.close();
	}

	public static void problem8_34() {
//		String rootPath = "E:\\ZxcWork\\DiscreteTools\\data\\";
//		String resultFile = rootPath + "test.txt";
		
		PrintStream writer = System.out; 
//		try {
//			writer = new PrintStream(new File(resultFile));
//		} catch (Exception exc) {
//			exc.printStackTrace();
//			return;
//		}
		
		int varNumber = 3;
		int sum = 6;
		EquationSolverCounter counter = new EquationSolverCounter(varNumber, sum);
		int[] minValue = {0, 0, 0};
		int[] maxValue = {3, 2, 4};
		counter.setWriter(writer, false);
		int result = counter.counting(new AndGroupEquationSolverFilter(minValue, maxValue));
		System.out.println("Equation x_1+x_2+x_3 = 6, x_1<=3, x_2<=2, x_3<=4, result = " + result);
//		writer.close();
	}

	public static void problem8_35() {
//		String rootPath = "E:\\ZxcWork\\DiscreteTools\\data\\";
//		String resultFile = rootPath + "test.txt";
		
//		PrintStream writer = System.out; 
//		try {
//			writer = new PrintStream(new File(resultFile));
//		} catch (Exception exc) {
//			exc.printStackTrace();
//			return;
//		}
		
		int varNumber = 5;
		int sum = 15;
		EquationSolverCounter counter = new EquationSolverCounter(varNumber, sum);
		int[] minValue = {2, 0, 1};
		int[] maxValue = {5, 2, 4};
//		counter.setWriter(writer, false);
		int result = counter.counting(new AndGroupEquationSolverFilter(minValue, maxValue));
		System.out.println("Equation x_1+x_2+x_3+x_4 <= 15, 2<=x_1<=5, x_2<=2, 1<=x_3<=4, result = " + result);
//		writer.close();
	}
	
	public static void problem8_36() {
		char[] fromSet = {'1', '2', '3', '4', '5', '6'};
		char[] toSet = {'a', 'b', 'c'};
		Set from = new Set(fromSet);
		Set to = new Set(toSet);
		FunctionGenerator generator = new FunctionGenerator(from, to);
		generator.first();
		int counter = 0;
		while (true) {
			Function function = generator.current();
			if (function.isSurjection()) {
				System.out.println("Surjection: " + function);
				counter++;
			} else System.out.println("Not surjection: " + function);
			if (generator.isLast()) break;
			generator.next();
		}
		System.out.println("result = " + counter);
	}
	
	public static void main(String[] args) {
		example();
//		example8_5();
//		example8_7();
//		problem8_11();
//		problem8_12();
//		problem8_22();
//		problem8_23();
//		problem8_26();
//		problem8_32();
//		problem8_34();
//		problem8_35();
//		problem8_36();
	}

}

