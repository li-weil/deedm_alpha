package com.deedm.legacy.disbook;


public class Chapter01Algorithm {
	static int step = 0;

	public static void main(String[] args) {
//		int year = 1994;
//		System.out.println("Algorithm_1_1(" + year + ") = " + algorithm_1_1(year) + ", Algorithm_1_2(" + year + ") = " + algorithm_1_2(year));
//		year = 2100;
//		System.out.println("Algorithm_1_1(" + year + ") = " + algorithm_1_1(year) + ", Algorithm_1_2(" + year + ") = " + algorithm_1_2(year));
//		year = 2016;
//		System.out.println("Algorithm_1_1(" + year + ") = " + algorithm_1_1(year) + ", Algorithm_1_2(" + year + ") = " + algorithm_1_2(year));
		
//		int a = 14, b = 2086;
//		System.out.println("Algorithm_1_3(" + a + ", " + b + ") = " + algorithm_1_3(a, b));
//		a = 13;  b = 3089;
//		System.out.println("Algorithm_1_3(" + a + ", " + b + ") = " + algorithm_1_3(a, b));
		
//		String u = "1110101001";
//		System.out.println("Algorithm_1_4(" + u + ") = " + algorithm_1_4(u));
//		u = "11101010111";
//		System.out.println("Algorithm_1_4(" + u + ") = " + algorithm_1_4(u));
	
//		testGCD();
		
		solveExercise1_7();
	}
	
	public static boolean algorithm_1_1(int year) {
		if ((year % 400 == 0) || (year % 4 == 0 && year % 100 != 0)) return true;
		else return false;
	}
	
	public static boolean algorithm_1_2(int year) {
		if (year % 400 == 0) return true;
		if (year % 100 == 0) return false;
		if (year % 4 == 0) return true;
		else return false;
	}
	
	public static boolean algorithm_1_3(int a, int b) {
		if (a == 0) return false;
		int x, y;
		if (a < 0) x = -a; else x = a;
		if (b < 0) y = -b; else y = b;
		int c = 0;
		while (c < y) c = c + x;
		if (c == y) return true; else return false;
	}
	
	public static boolean algorithm_1_4(String u) {
		if (u.equals("0") || u.equals("1")) return true;
		int length = u.length();
		String v = u.substring(1, length-1);
		if ((u.charAt(0) == '0' && u.charAt(length-1) == '0') || (u.charAt(0) == '1' && u.charAt(length-1) == '1'))
			return algorithm_1_4(v);
		return false;
	}
	
	public static int algorithm_1_5(int a, int b) {
		int x, y;
		if (a < 0) x = -a; else x = a;
		if (b < 0) y = -b; else y = b;
		int d, max;
		if (a < b) {
			d = x;  max = y;
		} else {
			d = y;  max = x;
		}
		step = 0;
		if (d == 0) return max;
		while (d >= 1) {
			if (x % d == 0 && y % d == 0) return d;
			d = d - 1;
			step = step+1;
		}
		return 1;
	}
	
	public static int gcdJZ(int a, int b) {
		int x, y;
		if (a < 0) x = -a; else x = a;
		if (b < 0) y = -b; else y = b;
		if (x > y) {
			int temp = x; 
			x = y; 
			y = temp;
		}
		while (x != y) {
			int r = y - x;
			System.out.println(r + " = " + y + " - " + x);
			if (r < x) {
				y = x;
				x = r;
			} else y = r;
		}
		return y;
	}

	public static int algorithm_1_6(int a, int b) {
		int x, y;
		if (a < 0) x = -a; else x = a;
		if (b < 0) y = -b; else y = b;
		if (x > y) {
			int temp = x; 
			x = y; 
			y = temp;
		}
		step = 0;
		while (x > 0) {
			int r = y % x;
			System.out.println(y + " = " + x + " * " + (int)(y/x) + " + " + r);
			y = x;
			x = r;
			step = step + 1;
		}
		return y;
	}
	
	public static int ecluidRec(int a, int b) {
		int x, y;
		if (a < 0) x = -a; else x = a;
		if (b < 0) y = -b; else y = b;
		if (x > y) {
			int temp = x; 
			x = y; 
			y = temp;
		}
		if (x == 0) return y;
		else return ecluidRec(y%x, x);
	}
	
	public static void testGCD() {
		int a = 45, b = 117;
		System.out.println("Algorithm_1_5(" + a + ", " + b + ") = " + algorithm_1_5(a, b));
		System.out.println("\tAlgorithm_1_5 Step = " + step);
		int d = algorithm_1_6(a, b);
		System.out.println("Algorithm_1_6(" + a + ", " + b + ") = " + d);
		System.out.println("\tAlgorithm_1_6 Step = " + step);
		d = gcdJZ(a, b);
		System.out.println("gcdJZ(" + a + ", " + b + ") = " + d);
		System.out.println("ecluidRec(" + a + ", " + b + ") = " + ecluidRec(a, b));
	}
	
	public static void solveExercise1_7() {
		final int MAX_NUMBER = 1000;
		int[] result = new int[MAX_NUMBER];

		int n = 3;
		int number = constructElementsForExercise1_7(3, 5, n, result, MAX_NUMBER);
		System.out.println(number + " elements generated at least " + n + " steps: ");
		for (int i = 0; i < number; i++) System.out.print(result[i] + " ");
	}

	public static int constructElementsForExercise1_7(int a, int b, int n, int[] result, int maxNumber) {
		result[0] = a;  result[1] = b;    // The elements in the inductive base of the definition
		int currentNumber = 2;
		int step = 0;
		
		while (step < n) {
			int[] generatedElements = new int[(currentNumber * (currentNumber+1))/2+1];
			int generatedNumber = 0;
			
			for (int i = 0; i < currentNumber; i++) {
				for (int j = i; j < currentNumber; j++) {
					generatedElements[generatedNumber] = result[i] + result[j];   // Generated a new element
					generatedNumber++;
				}
			}
			
			// Merge all generated elements to the result
			for (int i = 0; i < generatedNumber; i++) {
				boolean found = false;
				for (int j = 0; j < currentNumber; j++) {
					if (generatedElements[i] == result[j]) {
						found = true;
						break;
					}
				}
				if (found == false) {
					result[currentNumber] = generatedElements[i];
					if (++currentNumber > maxNumber) {
						System.out.println("Construct too many elements at least " + n + " steps!");
						return currentNumber;
					}
				}
			}
			step = step + 1;
		}
		return currentNumber;
	}
	
}
