/**
 * 
 */
package com.deedm.legacy.counting;

import java.math.BigInteger;

/**
 * @author user
 *
 */
public class CombCalculator {
	
	/**
	 * Calculate x^y 
	 */
	public static int power(int x, int y) {
		int result = 1;
		for (int i = 0; i < y; i++) {
			result = result * x;
			if (result <= 0) return -1;
		}
		return result; 
	}
	
	/**
	 * 支持大数的Calculate x^y 
	 */
	public static BigInteger powerBigInteger(BigInteger x, int y) {
		BigInteger result = new BigInteger("0");
		result = x.pow(y);
		return result;
	}
	
	
	/**
	 * Calculate n!
	 */
	public static int factorial(int n) {
		if (n == 0) return 1;
		int result = n;
		for (int i = n-1; i >= 1; i--) {
			result = result * i;
			if (result < 0) return -1;
		}
		return result;
	}
	
	/**
	 * 支持大数的Calculate n! 
	 */
	public static BigInteger factorialBigInteger(int n) {
		if (n == 0) return BigInteger.valueOf(1);
		if (n < 0) return BigInteger.valueOf(-1);
		BigInteger result = new BigInteger(n+"");
		for (int i = n-1; i >= 1; i--) {
			result = result.multiply(BigInteger.valueOf(i));
		}
		return result;
	}
	
	
	/**
	 * Calculate C(n, m) 
	 */
	public static int choose(int n, int m) {
		if (n < m) return 0;
		if (n == m) return 1;
		if (m == 1 || m == n-1) return n;
		if (m > n/2) m = n - m;
		int result = 1;
		for (int i = 0; i < m; i++) {
			result = result * (n-i);
			if (result < 0) return -1;
		}
		for (int i = 1; i <= m; i++) result = result / i; 
		return result;
	}
	
	/**
	 * 支持大数的Calculate C(n, m) 
	 */
	public static BigInteger chooseBigInteger(int n, int m) {
		if (m < 0 || n < 0) return BigInteger.valueOf(-1);
		if (n < m) return BigInteger.valueOf(0);
		if (n == m) return BigInteger.valueOf(1);
		if (m == 1 || m == n-1) return BigInteger.valueOf(n);
		if (m > n/2) m = n - m;
		BigInteger result = new BigInteger("1");
		for (int i = 0; i < m; i++) {
			result = result.multiply(BigInteger.valueOf(n-i));
		}
		for (int i = 1; i <= m; i++) {
			result = result.divide(BigInteger.valueOf(i));
		}
		return result;
	}
	
	/**
	 * Calculate P(n, m) 
	 */
	public static int permutation(int n, int m) {
		if (n < m) return 0;
		int result = 1;
		for (int i = 0; i < m; i++) {
			result = result * (n-i);
			if (result < 0) return -1;
		}
		return result;
	}
	
	/**
	 * 支持大数的Calculate P(n, m) 
	 */
	public static BigInteger permutationBigInteger(int n, int m) {
		if (m < 0 || n < 0) return BigInteger.valueOf(-1);
		if (n < m) return BigInteger.valueOf(0);
		BigInteger result = new BigInteger("1");
		for (int i = 0; i < m; i++) {
			result = result.multiply(BigInteger.valueOf(n-i));
		}
		return result;
	}
	
	public static void main(String[] args) {
		int n = 100;
		int m = 51;
		
		System.out.println(n + "^" + m + " = " + power(n, m));
		System.out.println(n + "! = " + factorial(n));
		System.out.println("C(" + n + ", " + m + ") = " + choose(n, m));
		System.out.println("P(" + n + ", " + m + ") = " + permutation(n, m));
		System.out.println(powerBigInteger(BigInteger.valueOf(n), m));
		System.out.println(factorialBigInteger(n));
		System.out.println(chooseBigInteger(n, m));
		System.out.println(permutationBigInteger(n, m));
		

	}
}