package com.deedm.legacy.counting;

import java.io.File;
import java.io.PrintStream;

import com.deedm.legacy.counting.filter.AndGroupStringFilter;
import com.deedm.legacy.counting.filter.StringFilter;
import com.deedm.legacy.counting.filter.StringSubstringFilter;

public class CountingExerciseSolver {
	public static void main(String[] args) {
		solveExercise8_23();
	}

	public static void solveExercise8_24() {
		String rootPath = "E:\\ZxcWork\\DiscreteTools\\data\\";
		String resultFile = rootPath + "test.txt";
		
		PrintStream writer = new PrintStream(System.out); 
		try {
			writer = new PrintStream(new File(resultFile));
		} catch (Exception exc) {
			exc.printStackTrace();
			return;
		}
		
		char[] base2 = {'0', '1'};
		StringCounter counter = new StringCounter(base2, 24);
//		counter.setWriter(writer, true);
		int result = counter.counting(new Exercise8_24Filter());
		
		System.out.println("Result = " + result);

		writer.close();
	}

	public static void solveExercise8_23() {
		String rootPath = "E:\\ZxcWork\\DiscreteTools\\data\\";
		String resultFile = rootPath + "test.txt";
		
		PrintStream writer = new PrintStream(System.out); 
		try {
			writer = new PrintStream(new File(resultFile));
		} catch (Exception exc) {
			exc.printStackTrace();
			return;
		}
		
		char[] base3 = {'0', '1', '2'};
		StringCounter counter = new StringCounter(base3, 10);
		String[] zerostring = {"0"};
		String[] onestring = {"1"};
		AndGroupStringFilter filter = new AndGroupStringFilter(new StringSubstringFilter(StringSubstringFilter.EXACTLY, 3, zerostring));
		filter.addFilter(new StringSubstringFilter(StringSubstringFilter.EXACTLY, 3, onestring));
		int result = counter.counting(filter);
		counter.setWriter(writer, true);
		int result1 = counter.counting(new Exercise8_23_1Filter());
		counter.setWriter(null, false);
		int result2 = counter.counting(new Exercise8_23_2Filter());
		int result3 = counter.counting(new Exercise8_23_3Filter());
		System.out.println("Result 1 = " + result + ", result 2 = " + result2 + ", result 3 = " + result3);

		counter.setWriter(writer, true);

		String[] zeroonestring = {"0", "1"};
		counter.setWriter(null, false);
		filter = new AndGroupStringFilter(new StringSubstringFilter(StringSubstringFilter.AT_LEAST, 3, zeroonestring));
		result2 = counter.counting(filter);

		filter = new AndGroupStringFilter(new StringSubstringFilter(StringSubstringFilter.NO_MORE_THAN, 3, zeroonestring));
		result3 = counter.counting(filter);
		System.out.println("Result 1 = " + result1 + ", result 2 = " + result2 + ", result 3 = " + result3);
		
		result1 = CombCalculator.choose(10, 3) * CombCalculator.choose(7, 3); 
		result2 = CombCalculator.power(3, 10) - (CombCalculator.choose(10, 0) * (CombCalculator.choose(10, 0) + CombCalculator.choose(10, 1) + CombCalculator.choose(10, 2)) + 
				CombCalculator.choose(10, 1) * (CombCalculator.choose(9, 0) + CombCalculator.choose(9, 1) + CombCalculator.choose(9, 2)) +
				CombCalculator.choose(10, 2) * (CombCalculator.choose(8, 0) + CombCalculator.choose(8, 1) + CombCalculator.choose(8, 2)));
		result3 = 2* (CombCalculator.power(2, 10) + CombCalculator.power(2, 9) * CombCalculator.choose(10, 1) + CombCalculator.power(2, 8) * CombCalculator.choose(10, 2) + CombCalculator.power(2, 7) * CombCalculator.choose(10, 3))
				- (CombCalculator.choose(10, 0) * (CombCalculator.choose(10, 0) + CombCalculator.choose(10, 1) + CombCalculator.choose(10, 2)  + CombCalculator.choose(10, 3)) + 
				CombCalculator.choose(10, 1) * (CombCalculator.choose(9, 0) + CombCalculator.choose(9, 1) + CombCalculator.choose(9, 2) + CombCalculator.choose(9, 3)) +
				CombCalculator.choose(10, 2) * (CombCalculator.choose(8, 0) + CombCalculator.choose(8, 1) + CombCalculator.choose(8, 2) + CombCalculator.choose(8, 3)) + 
				CombCalculator.choose(10, 3) * (CombCalculator.choose(7, 0) + CombCalculator.choose(7, 1) + CombCalculator.choose(7, 2) + CombCalculator.choose(7, 3)));
		System.out.println("Result 1 = " + result1 + ", result 2 = " + result2 + ", result 3 = " + result3);
		writer.close();
	}
	
	public static void solveExercise8_21() {
		String rootPath = "E:\\ZxcWork\\DiscreteTools\\data\\";
		String resultFile = rootPath + "test.txt";
		
		PrintStream writer = new PrintStream(System.out); 
		try {
			writer = new PrintStream(new File(resultFile));
		} catch (Exception exc) {
			exc.printStackTrace();
			return;
		}
		
		char[] base10 = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
		StringCounter counter = new StringCounter(base10, 5);
		int result1 = counter.counting(new Exercise8_21_1Filter());
//		counter.setWriter(writer, true);
		int result2 = counter.counting(new Exercise8_21_2Filter());
		
		System.out.println("Result 1 = " + result1 + ", result 2 = " + result2);

		writer.close();
	}

	public static void solveExercise8_10() {
		int counter1 = 0;
		int counter2 = 0;
		int counter3 = 0;
		int counter4 = 0;
		
		for (int i = 1; i <= 300; i++) {
			boolean divided3 = (i%3 == 0);
			boolean divided5 = (i%5 == 0);
			boolean divided7 = (i%7 == 0);;
			
			if (divided3 && divided5 && divided7) counter1++;
			if (!divided3 && !divided5 && !divided7) counter2++;
			if (divided3 && !divided5 && !divided7) counter3++;
			if ((divided3 && !divided5 && !divided7) || (divided5 && !divided3 && !divided7) || (divided7 && !divided3 && !divided5)) counter4++;
		}
		System.out.println("Counter1 = " + counter1 + ", counter2 = " + counter2 + ", counter3 = " + counter3 + ", counter4 = " + counter4);
	}

	public static void solveExercise8_11() {
		int counter1 = 0;
		int counter2 = 0;
		int counter3 = 0;
		
		for (int i = 1; i < 1000; i++) {
			boolean divided2 = (i%2 == 0);
			boolean divided3 = (i%3 == 0);
			boolean divided5 = (i%5 == 0);;
			boolean divided7 = (i%7 == 0);;
			boolean divided11 = (i%11 == 0);;
			boolean divided13 = (i%13 == 0);;
			
			if (divided2 || divided3 || divided5) counter1++;
			if (!divided7 && !divided11 && !divided13) counter2++;
			if (divided3 && !divided7) counter3++;
		}
		System.out.println("Counter1 = " + counter1 + ", counter2 = " + counter2 + ", counter3 = " + counter3);
	}
	
	public static void solveExercise8_15() {
		for (int i = 101; i <= 200; i++) {
			for (int j = i+1; j <= 200; j++) {
				if (j % i == 0) System.out.println("Yes, " + j + " % " + i + " = 0!");
			}
			if (i % 19 == 0) System.out.println("i = " + i);
		}
		
		int a[] = new int[100];
		a[0] = 17; a[1] = 3 * 2* 2 * 2 * 2 *2* 2; a[2] = 5 * 2 * 2 * 2 * 2 * 2;
		for (int i = 3; 2*i+1 <= 11; i++) a[i] = (2*i+1) * 2 * 2 * 2 * 2;
		for (int i = 6; 2*i+1 <= 25; i++) a[i] = (2*i+1) * 2 * 2 * 2;
		for (int i = 13; 2*i+1 <= 49; i++) a[i] = (2*i+1) * 2 * 2;
		for (int i = 25; 2*i+1 <= 99; i++) a[i] = (2*i+1) * 2;
		for (int i = 50; i < 100; i++) a[i] = (2*i+1);
		
		for (int i = 0; i < 100; i++) {
			for (int j = i+1; j < 100; j++) {
				if (a[i] % a[j] == 0) System.out.println("a[i] = " + a[i] + "a[j] = " + a[j] + ", Failure!");
				if (a[j] % a[i] == 0) System.out.println("a[i] = " + a[i] + "a[j] = " + a[j] + ", Failure!");
			}
			if (a[i] < 101) System.out.println("On!!");
			System.out.println(a[i]);
		}
	}
	
	public static void solveExercise8_7() {
		String rootPath = "E:\\ZxcWork\\DiscreteTools\\data\\";
		String resultFile = rootPath + "temp.txt";
		
		PrintStream writer = null; 
		try {
			writer = new PrintStream(new File(resultFile));
		} catch (Exception exc) {
			exc.printStackTrace();
			return;
		}
		
		char[] base10 = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
		StringCounter counter = new StringCounter(base10, 4);
//		counter.setWriter(writer);
		int result1 = counter.counting(new Exercise8_7_1Filter());
		int result2 = counter.counting(new Exercise8_7_2Filter());
		
		System.out.println("Result 1 = " + result1 + ", result 2 = " + result2);

		writer.close();
	}

	public static void solveExercise8_2() {
		String rootPath = "E:\\ZxcWork\\DiscreteTools\\data\\";
		String resultFile = rootPath + "temp.txt";
		
		PrintStream writer = null; 
		try {
			writer = new PrintStream(new File(resultFile));
		} catch (Exception exc) {
			exc.printStackTrace();
			return;
		}
		
		char[] base7 = {'0', '1', '2', '3', '4', '5', '6'};
		StringCounter counter = new StringCounter(base7, 3);
		counter.setWriter(writer);
		int result1 = counter.counting(new Exercise8_2_1Filter());
		int result2 = counter.counting(new Exercise8_2_2Filter());
		int result3 = counter.counting(new Exercise8_2_3Filter());
		int result4 = counter.counting(new Exercise8_2_4Filter());
		int result5 = counter.counting(new Exercise8_2_5Filter());
		
		System.out.println("Result 1 = " + result1 + ", result 2 = " + result2 + ", result 3 = " + result3 + ", result 4 = " + result4 + ", result 5 = " + result5);
		result1 = 6 * 7 * 7;
		result2 = 6 * 6 * 5;
		result3 = 6 * 7 * 3;
		result4 = 3 * 5 * 5;
		result5 = 1 * 6 * 5 + 3 * 5 * 5;
		System.out.println("Result 1 = " + result1 + ", result 2 = " + result2 + ", result 3 = " + result3 + ", result 4 = " + result4 + ", result 5 = " + result5);

		writer.close();
	}
}

class Exercise8_24Filter implements StringFilter {
	@Override
	public boolean accept(char[] string) {
		if (string == null) return false;
		int partLenOne = 6;
		int partLenTwo = 8;
		
		int counter = 0;
		for (int i = 0; i < string.length; i++) {
			if (string[i] == '1') counter++;
		}
		if (counter != 6) return false;
		
		boolean found = false;
		for (int i = 0; i < partLenOne; i++) {
			if (string[i] == '1') {
				found = true;
				break;
			}
		}

		if (found == false) return false;

		found = false;
		for (int i = partLenOne; i < (partLenOne+partLenTwo); i++) {
			if (string[i] == '1') {
				found = true;
				break;
			}
		}
		if (found == false) return false;

		found = false;
		for (int i = (partLenOne+partLenTwo); i < string.length; i++) {
			if (string[i] == '1') {
				found = true;
				break;
			}
		}
		if (found == false) return false;
		return true;
		
	}

	@Override
	public String toString() {
		return "[Exercise8_24]";
	}

	@Override
	public String toLaTeXString() {
		return "[Exercise8_24]";
	}
}

class Exercise8_23_1Filter implements StringFilter {
	@Override
	public boolean accept(char[] string) {
		if (string == null) return false;

		int counter0 = 0;
		int counter1 = 0;
		for (int i = 0; i < string.length; i++) {
			if (string[i] == '0') counter0++;
			if (string[i] == '1') counter1++;
		}
		if (counter0 == 3 && counter1 == 3) return true;
		else return false;
	}

	@Override
	public String toString() {
		return "[Exercise8_23_1]";
	}

	@Override
	public String toLaTeXString() {
		return "[Exercise8_23_1]";
	}
}

class Exercise8_23_2Filter implements StringFilter {
	@Override
	public boolean accept(char[] string) {
		if (string == null) return false;

		int counter0 = 0;
		int counter1 = 0;
		for (int i = 0; i < string.length; i++) {
			if (string[i] == '0') counter0++;
			if (string[i] == '1') counter1++;
		}
		if (counter0 >= 3 || counter1 >= 3) return true;
		else return false;
	}

	@Override
	public String toString() {
		return "[Exercise8_23_2]";
	}

	@Override
	public String toLaTeXString() {
		return "[Exercise8_23_2]";
	}
}


class Exercise8_23_3Filter implements StringFilter {
	@Override
	public boolean accept(char[] string) {
		if (string == null) return false;

		int counter0 = 0;
		int counter1 = 0;
		for (int i = 0; i < string.length; i++) {
			if (string[i] == '0') counter0++;
			if (string[i] == '1') counter1++;
		}
		if (counter0 <= 3 || counter1 <= 3) return true;
		else return false;
	}

	@Override
	public String toString() {
		return "[Exercise8_23_3]";
	}

	@Override
	public String toLaTeXString() {
		return "[Exercise8_23_3]";
	}
}


class Exercise8_21_1Filter implements StringFilter {
	@Override
	public boolean accept(char[] string) {
		if (string == null) return false;

		if (string[0] == '0') return false;
		for (int i = 0; i < string.length; i++) 
			for (int j = i+1; j < string.length; j++) {
				if (string[i] == string[j]) return false;
			}
		return true;
	}

	@Override
	public String toString() {
		return "[Exercise8_21_1]";
	}

	@Override
	public String toLaTeXString() {
		return "[Exercise8_21_1]";
	}
}

class Exercise8_21_2Filter implements StringFilter {
	@Override
	public boolean accept(char[] string) {
		if (string == null) return false;

		if (string[0] == '0') return false;
		for (int i = 0; i < string.length; i++) {
			for (int j = i+1; j < string.length; j++) {
				if (string[i] == string[j]) return false;
			}
		}
		for (int i = 1; i < string.length; i++) {
			if (string[i-1] == '2' && string[i] == '0') {
//				System.out.println("Return true: " + StringCounter.toString(string));
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "[Exercise8_21_2]";
	}

	@Override
	public String toLaTeXString() {
		return "[Exercise8_21_2]";
	}
}

class Exercise8_7_1Filter implements StringFilter {
	@Override
	public boolean accept(char[] string) {
		if (string == null) return false;

		if (string[0] == '0') return false;
		for (int i = 1; i < string.length; i++) 
			if (string[i] == '0') return true;
		return false;
	}

	@Override
	public String toString() {
		return "[Exercise8_7_1]";
	}

	@Override
	public String toLaTeXString() {
		return "[Exercise8_7_1]";
	}
}

class Exercise8_7_2Filter implements StringFilter {
	@Override
	public boolean accept(char[] string) {
		if (string == null) return false;

		if (string[0] == '0') return false;
		for (int i = 0; i < string.length; i++) 
			if (string[i] == '2') return true;
		return false;
	}

	@Override
	public String toString() {
		return "[Exercise8_7_2]";
	}

	@Override
	public String toLaTeXString() {
		return "[Exercise8_7_2]";
	}
}

class Exercise8_2_1Filter implements StringFilter {
	@Override
	public boolean accept(char[] string) {
		if (string == null) return false;

		if (string[0] == '0') return false;
		return true;
	}

	@Override
	public String toString() {
		return "[Exercise8_2_1]";
	}

	@Override
	public String toLaTeXString() {
		return "[Exercise8_2_1]";
	}
}

class Exercise8_2_2Filter implements StringFilter {
	@Override
	public boolean accept(char[] string) {
		if (string == null) return false;

		if (string[0] == '0') return false;
		
		for (int i = 0; i < string.length; i++) {
			for (int j = i+1; j < string.length; j++) {
				if (string[i] == string[j]) return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "[Exercise8_2_2]";
	}

	@Override
	public String toLaTeXString() {
		return "[Exercise8_2_2]";
	}
}

class Exercise8_2_3Filter implements StringFilter {
	@Override
	public boolean accept(char[] string) {
		if (string == null) return false;

		if (string[0] == '0') return false;
		
		if ((string[string.length-1]-'0')%2 == 1 ) return true;
		return false;
	}


	@Override
	public String toString() {
		return "[Exercise8_2_3]";
	}


	@Override
	public String toLaTeXString() {
		return "[Exercise8_2_3]";
	}
}

class Exercise8_2_4Filter implements StringFilter {
	@Override
	public boolean accept(char[] string) {
		if (string == null) return false;

		if (string[0] == '0') return false;
		
		if ((string[string.length-1]-'0')%2 != 1 ) return false;
		for (int i = 0; i < string.length; i++) {
			for (int j = i+1; j < string.length; j++) {
				if (string[i] == string[j]) return false;
			}
		}
		return true;
	}


	@Override
	public String toString() {
		return "[Exercise8_2_4]";
	}


	@Override
	public String toLaTeXString() {
		return "[Exercise8_2_4]";
	}
}


class Exercise8_2_5Filter implements StringFilter {
	@Override
	public boolean accept(char[] string) {
		if (string == null) return false;

		if (string[0] == '0') return false;
		
		if ((string[string.length-1]-'0')%2 != 0 ) return false;
		for (int i = 0; i < string.length; i++) {
			for (int j = i+1; j < string.length; j++) {
				if (string[i] == string[j]) return false;
			}
		}
		return true;
	}


	@Override
	public String toString() {
		return "[Exercise8_2_5]";
	}


	@Override
	public String toLaTeXString() {
		return "[Exercise8_2_5]";
	}
}

