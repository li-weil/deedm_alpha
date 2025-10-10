package com.deedm.legacy.setrelfun;

import java.util.ArrayList;
import java.util.List;

public class EquivalenceRelation extends Relation {

	public EquivalenceRelation(Set set) {
		super(set);
	}
	
	public EquivalenceRelation(Relation relation) {
		super(relation.from);
		pairs.addAll(relation.pairs);
	}

	public Set getEquivalenceClass(char element) {
		if (!from.inSet(element)) return null;
		char[] result = new char[from.elements.length];
		result[0] = element;
		int counter = 1;
		
		for (OrderedPair pair : pairs) {
			char other = 0;
			boolean related = false;
			if (pair.getFirst() == element) {
				related = true;
				other = pair.getSecond();
			} else if (pair.getSecond() == element) {
				related = true;
				other = pair.getFirst();
			}
			if (related) {
				boolean inClass = false;
				for (int i = 0; i < counter; i++) {
					if (result[i] == other) {
						inClass = true;
						break;
					}
				}
				if (inClass == false) {
					result[counter] = other;
					counter++;
				}
			}
		}
		char[] trimedResult = new char[counter];
		for (int i = 0; i < counter; i++) trimedResult[i] = result[i];
		Set resultClass = new Set(trimedResult);
		return resultClass;
	}
	
	public List<Set> getQuotientSet() {
		List<Set> result = new ArrayList<Set>();
		for (int i = 0; i < from.length(); i++) {
			char element = from.get(i);
			Set elementClass = getEquivalenceClass(element);
			boolean inResult = false;
			for (Set resultClass : result) {
				if (resultClass.equalsTo(elementClass)) {
					inResult = true;
					break;
				}
			}
			if (inResult == false) result.add(elementClass);
		}
		return result;
	}
	
	public static Set[] randomGeneratePartition(Set set) {
		if (set.length() == 0) return null;
		int[] setSize = new int[set.length()];
		for (int i = 0; i < setSize.length; i++) setSize[i] = 0;
		int remainNumber = set.length();
		int counter = 0;
		// Determine the number of elements in each set of the partition 
		while (remainNumber > 0) {
			int size = (int)(Math.random() * remainNumber) + 1;
			setSize[counter] = size;
			counter = counter + 1;
			remainNumber = remainNumber - size;
		}
		Set[] result = new Set[counter];
		// The function value of permutation gets a random permutation of the elements in the set.
		Function permutation = Function.randomGenerateBijection(set, set);		
		OrderedPair[] pairs = permutation.getPairs();
		int startIndex = 0; 
		// Distribute the elements to each partition
		for (int i = 0; i < counter; i++) {
			int size = setSize[i];
			char[] elements = new char[size];
			for (int j = 0; j < size; j++) {
				elements[j] = pairs[startIndex].getSecond();
				startIndex = startIndex+1;
			}
			result[i] = new Set(elements);
		}
		return result;
	}
	
	public static Relation randomGenerateEquivalence(Set set) {
		if (set.length() == 0) return null;
		Set[] partitions = randomGeneratePartition(set);
		Relation result = new Relation(set);
		for (int i = 0; i < partitions.length; i++) {
			Relation product = Relation.getCartesianProduct(partitions[i], partitions[i]);
			result.addPair(product.pairs);
		}
		return result;
	}
}
