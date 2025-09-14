package com.deedm.legacy.disbook;

public class Chapter04Algorithm {

	public static void main(String[] args) {
		int n = 3;
		int fib = Fib(n);
		System.out.println("Fib(" + n + ") = " + fib);
		
		Hanoi(n, 'A', 'C', 'B');
	}
	
	public static int Fib(int n) {
		if (n == 0) return 0;
		if (n == 1) return 1;
		return Fib(n-1)+Fib(n-2);
	}
	
	public static void Hanoi(int n, char from, char to, char aux) {
		if (n == 1) System.out.println("1: " + from + " ----> " + to);
		else {
			Hanoi(n-1, from, aux, to);
			System.out.println(n + ": " + from + " ----> " + to);
			Hanoi(n-1, aux, to, from);
		}
	}

}
