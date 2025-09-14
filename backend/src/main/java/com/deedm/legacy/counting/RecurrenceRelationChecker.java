/**
 * 
 */
package com.deedm.legacy.counting;

import java.io.PrintStream;

import counting.filter.StringFilter;

/**
 * @author user
 *
 */
public class RecurrenceRelationChecker {
	static final char[] base2 = {'0', '1'};
	static final char[] base3 = {'0', '1', '2'};

	/**
	 * Filter class for Problem 8.44
	 */
	static class WithTwoConsecutive0sFilter implements StringFilter {
		@Override
		public boolean accept(char[] string) {
			if (string == null) return false;
			
			for (int i = 0; i < string.length-1; i++) {
				if (string[i] == '0' && string[i+1] == '0') return true;
			}
			return false;
		}

		@Override
		public String toString() {
			return "[Two Consecutive 0s]";
		}

		@Override
		public String toLaTeXString() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	/**
	 * Calculator for Problem 8.44
	 */
	public static int calculateWithTwoConsecutive0sBitStrings(int n) {
		int length = n+1;
		if (length < 2) length = 2;
		int[] counter = new int[length];
		counter[0] = 0;
		counter[1] = 0;
		
		int current = 2;
		while (current <= n) {
			counter[current] = counter[current-1] + counter[current-2] + power(2, current-2);  
			current++;
		}
		return counter[n];
	}
	
	/**
	 * Checker for Problem 8.44
	 */
	public static void checkWithTwoConsecutive0sRecurrenceRelation(PrintStream writer, int minLength, int maxLength) {
		int n = minLength;
		while (n <= maxLength) {
			int recurrenceResult = calculateWithTwoConsecutive0sBitStrings(n);

			StringCounter counter = new StringCounter(base2, n);
			StringFilter filter = new WithTwoConsecutive0sFilter();
			int counterResult = counter.counting(filter);
			
			writer.println("Problem 8.44: With two consecutive 0s, length " + n + ", recurrence result: " + recurrenceResult + ", and counter result: " + counterResult);
			n = n + 1;
		}
	}
	
	/**
	 * Filter for Problem 8.45
	 */
	static class WithTwoConsecutive0sOr1sFilter implements StringFilter {
		@Override
		public boolean accept(char[] string) {
			if (string == null) return false;

			for (int i = 0; i < string.length-1; i++) {
				if (string[i] == '0' && string[i+1] == '0') return true;
				if (string[i] == '1' && string[i+1] == '1') return true;
			}
			return false;
		}

		@Override
		public String toString() {
			return "[With Consecutive 0s or 1s]";
		}

		@Override
		public String toLaTeXString() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	/**
	 * Calculator for Problem 8.45
	 */
	public static int calculateWithTwoConsecutive0sOr1sTernaryString(int n) {
		int length = n+1;
		if (length < 2) length = 2;
		int[] counter = new int[length];
		counter[0] = 0;
		counter[1] = 0;
		
		int current = 2;
		while (current <= n) {
			counter[current] = 2 * counter[current-1] + counter[current-2] + 2 * power(3, current-2);  
			current++;
		}
		return counter[n];
	}
	
	/**
	 * Checker for Problem 8.45
	 */
	public static void checkWithTwoConsecutive0sOr1sTernaryRecurrenceRelation(PrintStream writer, int minLength, int maxLength) {
		for (int n = minLength; n <= maxLength; n++) {
			int recurrenceResult = calculateWithTwoConsecutive0sOr1sTernaryString(n);

			StringCounter counter = new StringCounter(base3, n);
			StringFilter filter = new WithTwoConsecutive0sOr1sFilter();
			int counterResult = counter.counting(filter);
			
			writer.println("Problem 8.45: With two consecutive 0s or 1s ternary string, length " + n + ", recurrence result: " + recurrenceResult + ", and counter result: " + counterResult);
		}
	}
	
	/**
	 * Filter for Exercise 8.49 and Exercise 8.53
	 */
	static class WithoutTwoConsecutive0sFilter implements StringFilter {
		@Override
		public boolean accept(char[] string) {
			if (string == null) return true;

			for (int i = 0; i < string.length-1; i++) {
				if (string[i] == '0' && string[i+1] == '0') return false;
			}
			return true;
		}

		@Override
		public String toString() {
			return "[Without Two Consecutive 0s]";
		}

		@Override
		public String toLaTeXString() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	/**
	 * Calculator for Exercise 8.49
	 */
	public static int calculateWithoutTwoConsecutive0sBitStrings(int n) {
		int length = n+1;
		if (length < 2) length = 2;
		int[] counter = new int[length];
		counter[0] = 1;
		counter[1] = 2;
		
		int current = 2;
		while (current <= n) {
			counter[current] = counter[current-1] + counter[current-2];  
			current++;
		}
		return counter[n];
	}

	/**
	 * Checker for Exercise 8.49
	 */
	public static void checkWithoutTwoConsecutive0sRecurrenceRelation(PrintStream writer, int minLength, int maxLength) {
		for (int n = minLength; n <= maxLength; n++) {
			int recurrenceResult = calculateWithoutTwoConsecutive0sBitStrings(n);

			StringCounter counter = new StringCounter(base2, n);
			StringFilter filter = new WithoutTwoConsecutive0sFilter();
			
			int counterResult = counter.counting(filter);
			
			writer.println("Exercise 8.49: Without two consecutive 0s, length " + n + ", recurrence result: " + recurrenceResult + ", and counter result: " + counterResult);
		}
	}
	
	/**
	 * Filter for Exercise 8.50
	 */
	static class WithThreeConsecutive0sFilter implements StringFilter {
		@Override
		public boolean accept(char[] string) {
			if (string == null) return false;

			for (int i = 0; i < string.length-2; i++) {
				if (string[i] == '0' && string[i+1] == '0' && string[i+2] == '0') return true;
			}
			return false;
		}

		@Override
		public String toString() {
			return "[With Three Consecutive 0s]";
		}

		@Override
		public String toLaTeXString() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	/**
	 * Calculator for Exercise 8.50
	 */
	public static int calculateWithThreeConsecutive0sBitStrings(int n) {
		int length = n+1;
		if (length < 3) length = 3;
		int[] counter = new int[length];
		counter[0] = 0;
		counter[1] = 0;
		counter[2] = 0; 
		
		int current = 3;
		while (current <= n) {
			counter[current] = counter[current-1] + counter[current-2] + counter[current-3] + power(2, current-3);  
			current++;
		}
		return counter[n];
	}
	
	/**
	 * Checker for Exercise 8.50
	 */
	public static void checkWithThreeConsecutive0sRecurrenceRelation(PrintStream writer, int minLength, int maxLength) {
		for (int n = minLength; n <= maxLength; n++) {
			int recurrenceResult = calculateWithThreeConsecutive0sBitStrings(n);

			StringCounter counter = new StringCounter(base2, n);
			StringFilter filter = new WithThreeConsecutive0sFilter();
			int counterResult = counter.counting(filter);
			
			writer.println("Exercise 8.50: With three consecutive 0s, length " + n + ", recurrence result: " + recurrenceResult + ", and counter result: " + counterResult);
		}
	}
	
	/**
	 * Filter for Exercise 8.51
	 */
	static class WithoutThreeConsecutive0sFilter implements StringFilter {
		@Override
		public boolean accept(char[] string) {
			if (string == null) return true;

			for (int i = 0; i < string.length-2; i++) {
				if (string[i] == '0' && string[i+1] == '0' && string[i+2] == '0') return false;
			}
			return true;
		}

		@Override
		public String toString() {
			return "[Without Three Consecutive 0s]";
		}

		@Override
		public String toLaTeXString() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	

	/**
	 * Calculator for Exercise 8.51
	 */
	public static int calculateWithoutThreeConsecutive0sBitStrings(int n) {
		int length = n+1;
		if (length < 3) length = 3;
		int[] counter = new int[length];
		counter[0] = 1;
		counter[1] = 2;
		counter[2] = 4;
		
		int current = 3;
		while (current <= n) {
			counter[current] = counter[current-1] + counter[current-2] + counter[current-3];  
			current++;
		}
		return counter[n];
	}
	

	/**
	 * Checker for Exercise 8.51
	 */
	public static void checkWithoutThreeConsecutive0sRecurrenceRelation(PrintStream writer, int minLength, int maxLength) {
		for (int n = minLength; n <= maxLength; n++) {
			int recurrenceResult = calculateWithoutThreeConsecutive0sBitStrings(n);

			StringCounter counter = new StringCounter(base2, n);
			StringFilter filter = new WithoutThreeConsecutive0sFilter();
			int counterResult = counter.counting(filter);
			
			writer.println("Exercise 8.51: Without three consecutive 0s, length " + n + ", recurrence result: " + recurrenceResult + ", and counter result: " + counterResult);
		}
	}
	

	/**
	 * Filter for Exercise 8.52
	 */
	static class Contain01Filter implements StringFilter {
		@Override
		public boolean accept(char[] string) {
			if (string == null) return false;

			for (int i = 0; i < string.length-1; i++) {
				if (string[i] == '0' && string[i+1] == '1') return true;
			}
			return false;
		}

		@Override
		public String toString() {
			return "[Contain 01]";
		}

		@Override
		public String toLaTeXString() {
			// TODO Auto-generated method stub
			return null;
		}
	}


	/**
	 * Calculator for Exercise 8.52
	 */
	public static int calculateContain01BitString(int n) {
		int length = n+1;
		if (length < 2) length = 2;
		int[] counter = new int[length];
		counter[0] = 0;
		counter[1] = 0;
		
		int current = 2;
		while (current <= n) {
			counter[current] = counter[current-1] + power(2, current-1) - 1;  
			current++;
		}
		return counter[n];
	}
	

	/**
	 * Checker for Exercise 8.52
	 */
	public static void checkContain01RecurrenceRelation(PrintStream writer, int minLength, int maxLength) {
		for (int n = minLength; n <= maxLength; n++) {
			int recurrenceResult = calculateContain01BitString(n);

			StringCounter counter = new StringCounter(base2, n);
			StringFilter filter = new Contain01Filter();
			int counterResult = counter.counting(filter);
			
			writer.println("Exercise 8.52: Contain 01, length " + n + ", recurrence result: " + recurrenceResult + ", and counter result: " + counterResult);
		}
	}


	/**
	 * Calculator for Exercise 8.53
	 */
	public static int calculateWithoutTwoConsecutive0sTernaryString(int n) {
		int length = n+1;
		if (length < 2) length = 2;
		int[] counter = new int[length];
		counter[0] = 1;
		counter[1] = 3;
		
		int current = 2;
		while (current <= n) {
			counter[current] = 2 * (counter[current-1] + counter[current-2]);  
			current++;
		}
		return counter[n];
	}
	

	/**
	 * Checker for Exercise 8.53
	 */
	public static void checkWithoutTwoConsecutive0sTernaryRecurrenceRelation(PrintStream writer, int minLength, int maxLength) {
		for (int n = minLength; n <= maxLength; n++) {
			int recurrenceResult = calculateWithoutTwoConsecutive0sTernaryString(n);

			StringCounter counter = new StringCounter(base3, n);
			StringFilter filter = new WithoutTwoConsecutive0sFilter();
			int counterResult = counter.counting(filter);
			
			writer.println("Exercise 8.53: Without two consecutive 0s ternary string, length " + n + ", recurrence result: " + recurrenceResult + ", and counter result: " + counterResult);
		}
	}
	

	/**
	 * Filter for Exercise 8.54
	 */
	static class WithoutTwoConsecutive0sOr1sFilter implements StringFilter {
		@Override
		public boolean accept(char[] string) {
			if (string == null) return true;

			for (int i = 0; i < string.length-1; i++) {
				if (string[i] == '0' && string[i+1] == '0') return false;
				if (string[i] == '1' && string[i+1] == '1') return false;
			}
			return true;
		}

		@Override
		public String toString() {
			return "[Without Two Consecutive 0s or 1s]";
		}

		@Override
		public String toLaTeXString() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	/**
	 * Calculator for Exercise 8.54
	 */
	public static int calculateWithoutTwoConsecutive0sOr1sTernaryString(int n) {
		int length = n+1;
		if (length < 2) length = 2;
		int[] counter = new int[length];
		counter[0] = 1;
		counter[1] = 3;
		
		int current = 2;
		while (current <= n) {
			counter[current] = 2 * counter[current-1] + counter[current-2];  
			current++;
		}
		return counter[n];
	}

	/**
	 * Checker for Exercise 8.54
	 */
	public static void checkWithoutTwoConsecutive0sOr1sTernaryRecurrenceRelation(PrintStream writer, int minLength, int maxLength) {
		for (int n = minLength; n <= maxLength; n++) {
			int recurrenceResult = calculateWithoutTwoConsecutive0sOr1sTernaryString(n);

			StringCounter counter = new StringCounter(base3, n);
			StringFilter filter = new WithoutTwoConsecutive0sOr1sFilter();
			int counterResult = counter.counting(filter);
			
			writer.println("Exercise 8.54: Without two consecutive 0s or 1s ternary string, length " + n + ", recurrence result: " + recurrenceResult + ", and counter result: " + counterResult);
		}
	}

	/**
	 * Filter for Exercise 8.55
	 */
	static class WithoutConsecutiveSymbolsFilter implements StringFilter {
		@Override
		public boolean accept(char[] string) {
			if (string == null) return true;

			for (int i = 0; i < string.length-1; i++) {
				if (string[i] == string[i+1]) return false;
			}
			return true;
		}

		@Override
		public String toString() {
			return "[Without Consecutive Symbols]";
		}

		@Override
		public String toLaTeXString() {
			// TODO Auto-generated method stub
			return null;
		}
	}


	/**
	 * Calculator for Exercise 8.55
	 */
	public static int calculateWithoutConsecutiveSymbolsTernaryString(int n) {
		int length = n+1;
		if (length < 2) length = 2;
		int[] counter = new int[length];
		counter[0] = 1;
		counter[1] = 3;
		
		int current = 2;
		while (current <= n) {
			counter[current] = 2 * counter[current-1];  
			current++;
		}
		return counter[n];
	}

	/**
	 * Checker for Exercise 8.55
	 */
	public static void checkWithoutConsecutiveSymbolsTernaryRecurrenceRelation(PrintStream writer, int minLength, int maxLength) {
		for (int n = minLength; n <= maxLength; n++) {
			int recurrenceResult = calculateWithoutConsecutiveSymbolsTernaryString(n);

			StringCounter counter = new StringCounter(base3, n);
			StringFilter filter = new WithoutConsecutiveSymbolsFilter();
			int counterResult = counter.counting(filter);
			
			System.out.println("Exercise 8.55: Without consecutive symbols ternary string, length " + n + ", recurrence result: " + recurrenceResult + ", and counter result: " + counterResult);
		}
	}


	public static int calculateWithTwoConsecutive0sTernaryString(int n) {
		int length = n+1;
		if (length < 2) length = 2;
		int[] counter = new int[length];
		counter[0] = 0;
		counter[1] = 0;
		
		int current = 2;
		while (current <= n) {
			counter[current] = 2 * (counter[current-1] + counter[current-2]) + power(3, current-2);  
			current++;
		}
		return counter[n];
	}
	
	public static void checkWithTwoConsecutive0sTernaryRecurrenceRelation(PrintStream writer, int minLength, int maxLength) {
		for (int n = minLength; n <= maxLength; n++) {
			int recurrenceResult = calculateWithTwoConsecutive0sTernaryString(n);

			StringCounter counter = new StringCounter(base3, n);
			StringFilter filter = new WithTwoConsecutive0sFilter();
			int counterResult = counter.counting(filter);
			
			writer.println("Non exercise: With two consecutive 0s ternary string, length " + n + ", recurrence result: " + recurrenceResult + ", and counter result: " + counterResult);
		}
	}


	static class WithConsecutiveSymbolsFilter implements StringFilter {
		@Override
		public boolean accept(char[] string) {
			if (string == null) return false;

			for (int i = 0; i < string.length-1; i++) {
				if (string[i] == string[i+1]) return true;
			}
			return false;
		}

		@Override
		public String toString() {
			return "[With Consecutive Symbols]";
		}

		@Override
		public String toLaTeXString() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	public static int calculateWithConsecutiveSymbolsTernaryString(int n) {
		int length = n+1;
		if (length < 2) length = 2;
		int[] counter = new int[length];
		counter[0] = 0;
		counter[1] = 0;
		
		int current = 2;
		while (current <= n) {
			counter[current] = 2 * counter[current-1] + power(3, current-1);  
			current++;
		}
		return counter[n];
	}

	public static void checkWithConsecutiveSymbolsTernaryRecurrenceRelation(PrintStream writer, int minLength, int maxLength) {
		for (int n = minLength; n <= maxLength; n++) {
			int recurrenceResult = calculateWithConsecutiveSymbolsTernaryString(n);

			StringCounter counter = new StringCounter(base3, n);
			StringFilter filter = new WithConsecutiveSymbolsFilter();
			int counterResult = counter.counting(filter);
			
			writer.println("The last one: With consecutive symbols ternary string, length " + n + ", recurrence result: " + recurrenceResult + ", and counter result: " + counterResult);
		}
	}
	
	
	public static void main(String[] args) {
		// Problem 8.44
//		checkWithTwoConsecutive0sRecurrenceRelation(System.out, 0, 7);
		// Problem 8.45
//		checkWithTwoConsecutive0sOr1sTernaryRecurrenceRelation(System.out, 0, 7);
		// Exercise 8.49
//		checkWithoutTwoConsecutive0sRecurrenceRelation(System.out, 0, 7);
		// Exercise 8.50
//		checkWithThreeConsecutive0sRecurrenceRelation(System.out, 0, 7);
		// Exercise 8.51
//		checkWithoutThreeConsecutive0sRecurrenceRelation(System.out, 0, 7);
		// Exercise 8.52
//		checkContain01RecurrenceRelation(System.out, 0, 7);
		// Exercise 8.53
//		checkWithoutTwoConsecutive0sTernaryRecurrenceRelation(System.out, 0, 7);
		// Exercise 8.54
//		checkWithoutTwoConsecutive0sOr1sTernaryRecurrenceRelation(System.out, 0, 7);
		// Exercise 8.55
//		checkWithoutConsecutiveSymbolsTernaryRecurrenceRelation(System.out, 0, 7);
		
		checkWithTwoConsecutive0sTernaryRecurrenceRelation(System.out, 0, 7);
		checkWithConsecutiveSymbolsTernaryRecurrenceRelation(System.out, 0, 7);
	}


	/**
	 * Calculate base^n 
	 */
	private static int power(int base, int n) {
		int result = 1;
		int count = 0;
		while (count < n) {
			result = result * base;
			count = count + 1;
		}
		return result;
	}
}
