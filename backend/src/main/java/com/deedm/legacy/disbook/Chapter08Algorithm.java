package com.deedm.legacy.disbook;

public class Chapter08Algorithm {

	public static void main(String[] args) {
		int[] data = new int[100];
		Chapter07Algorithm.randomGenerateData(data, 1000);
		
		System.out.println("Before sorting.....");
		Chapter07Algorithm.println(System.out, data);
		mergeSort(data, 0, data.length);
		System.out.println("After sorting.....");
		Chapter07Algorithm.println(System.out, data);
	}
	
	/**
	 * Sort the data in the array data from the index startIndex(include) to the index endIndex(exclude). The sorted data will be
	 * placed back to the array data.  
	 */
	public static void mergeSort(int[] data, int startIndex, int endIndex) {
		if (startIndex >= endIndex - 1) return;
		int middle = (startIndex + endIndex) / 2;
		
		mergeSort(data, startIndex, middle);
		mergeSort(data, middle, endIndex);
		
		int[] buffer = new int[endIndex - startIndex];
		int i = startIndex;
		int j = middle;
		int k = 0; 
		while (i < middle && j < endIndex) {
			if (data[i] <= data[j]) {
				buffer[k] = data[i]; 
				i = i + 1;
			} else {
				buffer[k] = data[j];
				j = j + 1;
			}
			k = k + 1;
		}
		while (i < middle) {
			buffer[k] = data[i];
			i = i + 1;
			k = k + 1;
		}
		while (j < endIndex) {
			buffer[k] = data[j];
			j = j + 1;
			k = k + 1;
		}
		
		for (k = startIndex; k < endIndex; k++) data[k] = buffer[k-startIndex];
	}
}
