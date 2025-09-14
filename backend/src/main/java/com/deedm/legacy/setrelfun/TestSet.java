/**
 * 
 */
package com.deedm.legacy.setrelfun;

/**
 * @author zxc
 *
 */
public class TestSet {

	public static void main(String[] args) {
//		char[] elements = {'1', '2', '3', '4', '5', '6', '7', '8'};
		char[] elementsA = {'1', '3', '5', '7'};
		char[] elementsB = {'1', '4', '7'};
		
//		Set setU = new Set(elements);
		Set setA = new Set(elementsA);
		Set setB = new Set(elementsB);
		
		System.out.println("A\\cup B = " + setA.union(setB));
		System.out.println("A\\cap B = " + setA.intersection(setB));
		System.out.println("A - B = " + setA.subtract(setB));
		System.out.println("P(A) = {");
		Set[] powerSet = setA.powerSet();
		for (int i = 0; i < powerSet.length; i++) {
			System.out.println("\t" + powerSet[i]);
		}
		System.out.println("}");
	}
}
