/**
 * 
 */
package com.deedm.legacy.counting;

/**
 * @author user
 *
 */
public class StatementCountingExample {

	public static int addPrinciple(int n, int m) {
		int k = 0;
		for (int i = 1; i <= n; i++) k = k + 1;
		for (int j = 1; j <= m; j++) k = k + 1;
		return k;
	}

	public static int multPrinciple(int n, int m) {
		int k = 0;
		for (int i = 1; i <= n; i++) {
			for (int j = 1; j <= m; j++) k = k + 1;
		}
		return k;
	}

	public static int multPrincipleWithoutRep(int n, int m) {
		int k = 0;
		for (int i = 1; i <= n; i++) {
			for (int j = 1; j <= m; j++) {
				if (i != j) k = k + 1;
			}
		}
		return k;
	}

	public static void problem8_10() {
		int n = 6, m = 8;
		
		int resultOne = problem8_10_CodeSliceOne(n, m);
		System.out.println("n = " + n + ", m = " + m + ", result of code slice one is " + resultOne + ", m * n = " + (n*m));
	
		int resultTwo = problem8_10_CodeSliceTwo(n, m);
		if (m > n) System.out.println("n = " + n + ", m = " + m + ", result of code slice two is " + resultTwo + ", n(2m-n+1)/2 = " + (n*(2*m-n+1)/2));
		else System.out.println("n = " + n + ", m = " + m + ", result of code slice two is " + resultTwo + ", m(m+1)/2 = " + (m*(m+1)/2));
	}

	private static int problem8_10_CodeSliceOne(int n, int m) {
		int k = 0;
		for (int i = 1; i <= n; i++) {
			for (int j = 1; j <= m; j++) k = k + 1;
		}
		return k;
	}

	private static int problem8_10_CodeSliceTwo(int n, int m) {
		int k = 0;
		for (int i = 1; i <= n; i++) {
			for (int j = i; j <= m; j++) k = k + 1;
		}
		return k;
	}

	public static void problem8_33() {
		int n = 8, m = 5;
		
		int resultOne = problem8_33_CodeSliceOne(n, m);
		System.out.println("n = " + n + ", m = " + m + ", result of code slice one is " + resultOne + ", choose(n-1+m, m) = choose(" + (n-1+m) + ", " + m + ") = "+ choose(n-1+m, m));
		int resultOneE = problem8_33_CodeSliceOneExpand(n);
		System.out.println("n = " + n + ", m = " + m + ", result of code slice one is " + resultOneE + ", choose(n-1+m, m) = choose(" + (n-1+m) + ", " + m + ") = "+ choose(n-1+m, m));
	
		int resultTwo = problem8_33_CodeSliceTwo(n, m);
		System.out.println("n = " + n + ", m = " + m + ", result of code slice two is " + resultTwo + ", choose(n-1+m, m) = choose(" + (n-1+m) + ", " + m + ") = "+ choose(n-1+m, m));
		int resultTwoE = problem8_33_CodeSliceTwoExpand(n);
		System.out.println("n = " + n + ", m = " + m + ", result of code slice Two is " + resultTwoE + ", choose(n-1+m, m) = choose(" + (n-1+m) + ", " + m + ") = "+ choose(n-1+m, m));
	}
	
	private static int problem8_33_CodeSliceOne(int n, int m) {
		int k = 0;
		int[] loopVariableValue = new int[m];
		for (int i = 0; i < m; i++) loopVariableValue[i] = 1;
		while (loopVariableValue[0] <= n) {
			k = k + 1;
			int i = m-1;
			while (i > 0 && loopVariableValue[i] == loopVariableValue[i-1]) {
				loopVariableValue[i] = 1;
				i = i - 1;
			}
			loopVariableValue[i] = loopVariableValue[i] + 1;
		}
		return k;
	}

	private static int problem8_33_CodeSliceOneExpand(int n) {
		int k = 0;
		for (int i1 = 1; i1 <= n; i1++) 
			for (int i2 = 1; i2 <= i1; i2++)
				for (int i3 = 1; i3 <= i2; i3++)
					for (int i4 = 1; i4 <= i3; i4++)
						for (int i5 = 1; i5 <= i4; i5++) k = k + 1;
		return k;
	}
	
	private static int problem8_33_CodeSliceTwo(int n, int m) {
		int k = 0;
		int[] loopVariableValue = new int[m];
		loopVariableValue[0] = n;
		for (int i = 1; i < m; i++) loopVariableValue[i] = loopVariableValue[i-1];
		while (loopVariableValue[0] >= 1) {
			k = k + 1;
			int i = m-1;
			while (i > 0 && loopVariableValue[i] == 1) i = i - 1;
			if (i == 0 && loopVariableValue[i] == 1) return k;
			loopVariableValue[i] = loopVariableValue[i] - 1;
			for (int j = i+1; j < m; j++) loopVariableValue[j] = loopVariableValue[j-1];
		}
		return k;
	}

	private static int problem8_33_CodeSliceTwoExpand(int n) {
		int k = 0;
		for (int i1 = n; i1 >= 1; i1--) 
			for (int i2 = i1; i2 >= 1; i2--)
				for (int i3 = i2; i3 >= 1; i3--)
					for (int i4 = i3; i4 >=1; i4--)
						for (int i5 = i4; i5 >= 1; i5--) k = k + 1;
		return k;
	}
	
	private static int choose(int n, int m) {
		int sum = 1;
		for (int i = 0; i < m; i++) sum = sum * (n-i);
		for (int i = 1; i <= m; i++) sum = sum / i;
		return sum;		
	}
	
	public static void main(String[] args) {
//		int k = addPrinciple(5, 8);
//		System.out.println("(1) k = " + k);
//		k = multPrinciple(5, 8);
//		System.out.println("(2) k = " + k);
//		k = multPrincipleWithoutRep(5, 8);
//		System.out.println("(3) k = " + k);
//		k = multPrincipleWithoutRep(8, 5);
//		System.out.println("(4) k = " + k);
		
		problem8_33();
	}
	
}
