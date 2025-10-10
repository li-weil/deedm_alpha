/**
 * 
 */
package com.deedm.legacy.setrelfun;

/**
 * @author user
 *
 */
public class Function extends Relation {
	private static char example = 0;

	public Function(Set from, Set to) {
		super(from, to);
	}

	public Function(Set set) {
		super(set);
	}

	public Function(Relation other) {
		super(other);
	}
	
	public boolean isInjection() {
		for (int i = 0; i < to.length(); i++) {
			char element = to.get(i);
			boolean found = false;
			for (OrderedPair pair : pairs) {
				if (pair.getSecond() == element) {
					if (found == false) found = true;
					else {
						example = element;
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public boolean isSurjection() {
		for (int i = 0; i < to.length(); i++) {
			char element = to.get(i);
			boolean found = false;
			for (OrderedPair pair : pairs) {
				if (pair.getSecond() == element) {
					found = true;
					break;
				}
			}
			if (found == false) {
				example = element;
				return false;
			}
		}
		return true;
	}

	public boolean isBijection() {
		for (int i = 0; i < to.length(); i++) {
			char element = to.get(i);
			boolean found = false;
			for (OrderedPair pair : pairs) {
				if (pair.getSecond() == element) {
					if (found == false) found = true;
					else {
						example = element;
						return false;
					}
				}
			}
			if (found == false) {
				example = element;
				return false;
			}
		}
		return true;
	}
	
	public static char getExample() {
		return example;
	}
	
	public static Function randomGenerateBijection(Set from, Set to) {
		int n = from.length();
		if (to.length() != n) return null;
		
		char[] values = new char[n];
		for (int i = 0; i < values.length; i++) values[i] = to.get(i);
		for (int i = 0; i < values.length; i++) {
			int swapIndex = (int)(Math.random() * values.length);
			// Random swap values[i] with another value (i.e. values[swapIndex])
			if (swapIndex != i) {
				char temp = values[i];
				values[i] = values[swapIndex];
				values[swapIndex] = temp;
			}
		}
		Function result = new Function(from, to);
		for (int i = 0; i < values.length; i++) {
			OrderedPair pair = new OrderedPair(from.get(i), values[i]);
			result.addPair(pair);
		}
		return result;
	}

	public static Function randomGenerateInjection(Set from, Set to) {
		int n = from.length();
		if (to.length() < n) return null;
		Set toSubset = Set.randomGenerateSubset(to, n);
		
		return randomGenerateBijection(from, toSubset);
	}

	public static Function randomGenerateSurjection(Set from, Set to) {
		int m = to.length();
		if (from.length() < m) return null;
		Set fromSubset = Set.randomGenerateSubset(from, m);
		
		Function temp = randomGenerateBijection(fromSubset, to);
		Function result = new Function(from, to);
		for (int i = 0; i < from.length(); i++) {
			char element = from.get(i);
			boolean found = false;
			for (OrderedPair pair : temp.pairs) {
				if (pair.getFirst() == element) {
					result.addPair(new OrderedPair(pair.getFirst(), pair.getSecond()));
					found = true;
					break;
				}
			}
			if (found == false) {
				int index = (int)(Math.random() * m);
				OrderedPair pair = new OrderedPair(element, to.get(index));
				result.addPair(pair);
			}
		}
		return result;
	}

	
	public static Function randomGenerateFunction(Set from, Set to) {
		Function result = new Function(from, to);
		for (int i = 0; i < from.length(); i++) {
			int valueIndex = (int)(Math.random() * to.length());
			OrderedPair pair = new OrderedPair(from.get(i), to.get(valueIndex));
			result.addPair(pair);
		}
		return result;
	}
}
