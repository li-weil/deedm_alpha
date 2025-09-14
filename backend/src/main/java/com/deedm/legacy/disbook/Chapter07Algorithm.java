package com.deedm.legacy.disbook;

import java.io.PrintStream;

/**
 * @author user
 *
 */
public class Chapter07Algorithm {

	public static void main(String[] args) {
		int dataLength = 64;
		int maxValue = 1000;
		int[] data = new int[dataLength];
		int randomInt = 1 + (int)(Math.random() * maxValue);
		
		Chapter07Algorithm.randomGenerateData(data, maxValue);
		int randomIndex = (int)(Math.random() * dataLength);
		int givenInt = data[randomIndex];

		int index = algorithm7_1(data, randomInt);
		System.out.println("Using linear search: the index of " + randomInt + " is " + index + " in data ");
		Chapter07Algorithm.println(System.out, data);
		index = algorithm7_1(data, givenInt);
		System.out.println("Using linear search: the index of " + givenInt + "(data[" + randomIndex + "]) is " + index + " in data ");
		
		Chapter08Algorithm.mergeSort(data, 0, data.length);
		givenInt = data[randomIndex];
		index = algorithm7_2(data, randomInt);
		System.out.println("Using binary search: the index of " + randomInt + " is " + index + " in data ");
		Chapter07Algorithm.println(System.out, data);
		index = algorithm7_2(data, givenInt);
		System.out.println("Using binary search: the index of " + givenInt + "(data[" + randomIndex + "]) is " + index + " in data ");
	}
	
	public static int algorithm7_1(int[] data, int x) {
		for (int i = 0; i < data.length; i++) {
			if (data[i] == x) return i;
		}
		return -1;
	}
	
	public static int algorithm7_2(int[] sortedData, int x) {
		int i = 0;
		int j = sortedData.length-1;
		while (i <= j) {
			int m = (i+j)/2;
//			System.out.println("\tCheck data index from " + i + " to " + j + ", and m = " + m + ", data[m] = " + sortedData[m] + ", x = " + x);
			if (sortedData[m] == x) return m;
			else if (x < sortedData[m]) j = m - 1;
			else i = m + 1;
		}
		return -1;
	}

	public static void println(PrintStream out, int[] data) {
		for (int i = 0; i < data.length; i++) out.print(data[i] + " ");
		out.println();
	}

	public static void randomGenerateData(int[] data, int maxValue) {
		for (int i = 0; i < data.length; i++) {
			data[i] = 1 + (int)(Math.random() * maxValue);
		}
	}
	
}
