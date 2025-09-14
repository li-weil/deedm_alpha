/**
 * 
 */
package com.deedm.legacy.algebra;

import setrelfun.PartialOrder;
import setrelfun.Set;

public class Lattice extends PartialOrder {
	public static int WITHOUT_GREATEST = 3;
	public static int WITHOUT_LEAST = 4;
	public static int NON_DISTRIBUTIVE = 5;
	public static int WITHOUT_COMPLEMENT = 6;
	public static int OTHER = 9;
	private static String nonDistributiveReason = null;
	
	BinaryOperator<Character> supOperator = null;
	BinaryOperator<Character> subOperator = null;
	
	private Lattice(Set set) {
		super(set);
	}
	private Lattice(PartialOrder relation) {
		super(relation);
	}

	public boolean isBounded() {
		if (!hasGreatestElement(from)) {
			reasonType = WITHOUT_GREATEST;
			return false;
		}
		if (!hasLeastElement(from)) {
			reasonType = WITHOUT_LEAST;
			return false;
		}
		return true;
	}
	
	public boolean hasGreatestElement() {
		return hasGreatestElement(from);
	}

	public char getGreatestElement() {
		return getGreatestElement(from);
	}
	
	public boolean hasLeastElement() {
		return hasLeastElement(from);
	}

	public char getLeastElement() {
		return getLeastElement(from);
	}

	public boolean hasComplement(char element) {
		int elementNumber = from.length();
		int greatestIndex = 0;
		int leastIndex = 0;
		if (!hasGreatestElement(from)) return false;
		else greatestIndex = from.getIndex(getGreatestElement(from));
		if (!hasLeastElement(from)) return false;
		else leastIndex = from.getIndex(getLeastElement(from));
		if (greatestIndex < 0 || greatestIndex >= elementNumber || leastIndex < 0 || leastIndex >= elementNumber) return false;
		
		if (supOperator == null || subOperator == null) return false;
		int index = from.getIndex(element);
		if (index < 0 || index >= elementNumber) return false;
		
		for (int i = 0; i < elementNumber; i++) {
			if (supOperator.getResultIndex(index, i) != greatestIndex) continue;
			if (subOperator.getResultIndex(index, i) != leastIndex) continue;
			return true;
		}
		return false;
	}
	
	public char getComplement(char element) {
		int elementNumber = from.length();
		int greatestIndex = 0;
		int leastIndex = 0;
		if (!hasGreatestElement(from)) return 0;
		else greatestIndex = from.getIndex(getGreatestElement(from));
		if (!hasLeastElement(from)) return 0;
		else leastIndex = from.getIndex(getLeastElement(from));
		if (greatestIndex < 0 || greatestIndex >= elementNumber || leastIndex < 0 || leastIndex >= elementNumber) return 0;
		
		if (supOperator == null || subOperator == null) return 0;
		int index = from.getIndex(element);
		if (index < 0 || index >= elementNumber) return 0;
		
		for (int i = 0; i < elementNumber; i++) {
			if (supOperator.getResultIndex(index, i) != greatestIndex) continue;
			if (subOperator.getResultIndex(index, i) != leastIndex) continue;
			return from.get(i);
		}
		return 0;
	}
	
	public int getComplement(char element, char[] complements) {
		int greatestIndex = 0, leastIndex = 0;
		if (!hasGreatestElement(from)) return 0;
		else greatestIndex = from.getIndex(getGreatestElement(from));
		if (!hasLeastElement(from)) return 0;
		else leastIndex = from.getIndex(getLeastElement(from));
		int index = from.getIndex(element);
		int counter = 0;
		int elementNumber = from.length();
		for (int i = 0; i < elementNumber; i++) {
			if (supOperator.getResultIndex(index, i) != greatestIndex) continue;
			if (subOperator.getResultIndex(index, i) != leastIndex) continue;
			complements[counter] = from.get(i);
			counter++;
		}
		return counter;
	}
	
	public boolean isComplemented() {
		for (int i = 0; i < from.length(); i++) {
			char element = from.get(i);
			if (!hasComplement(element)) {
				exampleOne = element;
				reasonType = WITHOUT_COMPLEMENT;
				return false;
			}
		}
		return true;
	}
	
	public boolean isDistributive() {
		if (supOperator == null || subOperator == null) return false;
		if (!supOperator.isDistributiveWith(subOperator)) {
			reasonType = NON_DISTRIBUTIVE;
			nonDistributiveReason = BinaryOperator.getReasonMessage();
			return false;
		}
		if (!subOperator.isDistributiveWith(supOperator)) {
			reasonType = NON_DISTRIBUTIVE;
			nonDistributiveReason = BinaryOperator.getReasonMessage();
			return false;
		}
		return true;
	}
	
	public static String getNonDistributiveReason() {
		return nonDistributiveReason;
	}
	
	public boolean isBooleanAlgebra() {
		return isComplemented() && isDistributive();
	}
	
	public static Lattice createLatticeFromPoset(PartialOrder relation) {
		Set baseSet = relation.getFromSet();
		Character[] base = new Character[baseSet.length()];
		for (int i = 0; i < baseSet.length(); i++) base[i] = baseSet.get(i); 
		
		int[][] supOptable = new int[base.length][base.length];
		int[][] subOptable = new int[base.length][base.length];
		for (int i = 0; i < base.length; i++) {
			supOptable[i][i] = i;
			subOptable[i][i] = i;
			char one = base[i];
			for (int j = i+1; j < base.length; j++) {
				char two = base[j];
				if (!relation.hasLeastUpperBound(one, two)) return null;
				else {
					char lub = relation.getLeastUpperBound(one, two);
					int index = baseSet.getIndex(lub);
					if (index >= 0 && index < base.length) {
						supOptable[i][j] = index;
						supOptable[j][i] = index;
					} else return null;
				}
				if (!relation.hasGreatestLowerBound(one, two)) return null;
				else {
					char glb = relation.getGreatestLowerBound(one, two);
					int index = baseSet.getIndex(glb);
					if (index >= 0 && index < base.length) {
						subOptable[i][j] = index;
						subOptable[j][i] = index;
					} else return null;
				}
			}
		}
		Lattice lattice = new Lattice(relation);
		lattice.supOperator = new BinaryOperator<Character>(base, supOptable);
		lattice.supOperator.setName("\\vee ");
		lattice.subOperator = new BinaryOperator<Character>(base, subOptable);
		lattice.subOperator.setName("\\wedge ");
		return lattice;
	}
	
	public BinaryOperator<Character> getSupOperator() {
		return supOperator;
	}
	
	public BinaryOperator<Character> getSubOperator() {
		return subOperator;
	}
}
