/**
 * 
 */
package com.deedm.legacy.setrelfun;

/**
 * @author zxc
 *
 */
public class Set {
	protected char[] elements = null;

	public Set(char[] elements) {
		this.elements = elements;
	}
	
	public int length() {
		if (isEmptySet()) return 0;
		return elements.length;
	}
	
	public char[] toCharArray() {
		if (elements == null) return null;
		char[] result = new char[elements.length];
		for (int i = 0; i < elements.length; i++) result[i] = elements[i];
		return result;
	}
	
	public boolean inSet(char element) {
		if (isEmptySet()) return false;
		for (int i = 0; i < elements.length; i++) {
			if (elements[i] == element) return true;
		}
		return false;
	}
	
	public char get(int index) {
		if (isEmptySet()) return ' ';
		return elements[index];
	}
	
	public int getIndex(char element) {
		if (isEmptySet()) return -1;
		for (int i = 0; i < elements.length; i++) {
			if (elements[i] == element) return i;
		}
		return -1;
	}
	
	public boolean isEmptySet() {
		if (elements == null) return true;
		if (elements.length <= 0) return true;
		return false;
	}
	
	public boolean isSubset(Set subset) {
		for (int i = 0; i < subset.elements.length; i++) {
			char element = subset.elements[i];
			if (!inSet(element)) return false;
		}
		return true;
	}
	
	public boolean[] getCharFunctionForSubset(Set subset) {
		if (isEmptySet()) return null; 
		boolean[] result = new boolean[elements.length];
		if (subset.isEmptySet()) return result;
		for (int i = 0; i < elements.length; i++) {
			boolean found = false;
			for (int j = 0; j < subset.elements.length; j++) {
				if (subset.elements[j] == elements[i]) {
					found = true;
					break;
				}
			}
			result[i] = found;
		}
		return result;
	}
	
	public Set getSubsetForCharFunction(boolean[] charfun) {
		if (isEmptySet()) return null;
		int length = getTrueNumber(charfun);
		char[] subsetElements = new char[length];
		int index = 0;
		for (int i = 0; i < charfun.length && i < elements.length; i++) {
			if (charfun[i] == true) {
				subsetElements[index] = elements[i];
				index = index + 1;
			}
		}
		return new Set(subsetElements);
	}
	
	public Set union(Set other) {
		if (isEmptySet()) return other;
		if (other.isEmptySet()) return this;
		
		char[] unionElements = new char[elements.length + other.elements.length];
		for (int i = 0; i < elements.length; i++) unionElements[i] = elements[i];
		int index = elements.length;
		for (int j = 0; j < other.elements.length; j++) {
			boolean found = false;
			for (int i = 0; i < index; i++) {
				if (unionElements[i] == other.elements[j]) {
					found = true;
					break;
				}
			}
			if (found == false) {
				unionElements[index] = other.elements[j];
				index = index + 1;
			}
		}
		unionElements = SetrelfunUtil.trimCharArray(unionElements, index);
		return new Set(unionElements);
	}
	
	public Set intersection(Set other) {
		if (isEmptySet()) return this;
		if (other.isEmptySet()) return other;
		
		char[] interElements = new char[elements.length];
		int index = 0;
		for (int i = 0; i < elements.length; i++) {
			boolean found = false;
			for (int j = 0; j < other.elements.length; j++) {
				if (elements[i] == other.elements[j]) {
					found = true;
					break;
				}
			}
			if (found == true) {
				interElements[index] = elements[i];
				index = index + 1;
			}
		}
		interElements = SetrelfunUtil.trimCharArray(interElements, index);
		return new Set(interElements);
	}
	
	public Set symmetricDifference(Set other) {
		if (isEmptySet()) return other;
		if (other.isEmptySet()) return this;
		
		char[] resultElements = new char[elements.length + other.elements.length];
		int index = 0;
		for (int i = 0; i < elements.length; i++) {
			boolean found = false;
			for (int j = 0; j < other.elements.length; j++) {
				if (elements[i] == other.elements[j]) {
					found = true;
					break;
				}
			}
			if (found == false) {
				resultElements[index] = elements[i];
				index = index + 1;
			}
		}
		for (int j = 0; j < other.elements.length; j++) {
			boolean found = false;
			for (int i = 0; i < elements.length; i++) {
				if (elements[i] == other.elements[j]) {
					found = true;
					break;
				}
			}
			if (found == false) {
				resultElements[index] = other.elements[j];
				index = index + 1;
			}
		}
		resultElements = SetrelfunUtil.trimCharArray(resultElements, index);
		return new Set(resultElements);
	}
	
	public Set subtract(Set other) {
		if (isEmptySet()) return this;
		if (other.isEmptySet()) return other;
		
		char[] subElements = new char[elements.length];
		int index = 0;
		for (int i = 0; i < elements.length; i++) {
			boolean found = false;
			for (int j = 0; j < other.elements.length; j++) {
				if (elements[i] == other.elements[j]) {
					found = true;
					break;
				}
			}
			if (found == false) {
				subElements[index] = elements[i];
				index = index + 1;
			}
		}
		subElements = SetrelfunUtil.trimCharArray(subElements, index);
		return new Set(subElements);
	}
	
	public Set[] powerSet() {
		if (isEmptySet()) {
			Set[] result = new Set[1];
			result[0] = this;
			return result;
		}
		int subsetNumber = SetrelfunUtil.power(elements.length, 2);
		Set[] result = new Set[subsetNumber];
		int index = 0;
		
		boolean[] charfun = new boolean[elements.length];
		while (isAllTrue(charfun) == false) {
			Set subset = getSubsetForCharFunction(charfun);
			result[index] = subset;
			index = index + 1;

			int i = elements.length-1;
			while (charfun[i] == true) {
				charfun[i] = false;
				i--;
			}
			charfun[i] = true;
		}
		result[index] = this;
		return result;
	}
	
	public boolean equalsTo(Set other) {
		if (this == other) return true;
		if (elements == null && other.elements == null) return true;
		if (elements == null) return false;
		if (other.elements == null) return false;
		for (int i = 0; i < elements.length; i++) {
			boolean found = false;
			for (int j = 0; j < other.elements.length; j++) {
				if (other.elements[j] == elements[i]) {
					found = true;
					break;
				}
			}
			if (found == false) return false;
		}
		for (int i = 0; i < other.elements.length; i++) {
			boolean found = false;
			for (int j = 0; j < elements.length; j++) {
				if (elements[j] == other.elements[i]) {
					found = true;
					break;
				}
			}
			if (found == false) return false;
		}
		return true;
	}
	
	public static String getElementLabel(char element, boolean isIntElement) {
		String label = null;
		if (Character.isLetterOrDigit(element) && isIntElement == false) label = element + "";
		else label = (int)element + "";
		return label;
	}
	
	public static String getElementsLabel(char[] elements, boolean isIntElement, int number) {
		StringBuffer labelString = new StringBuffer();
		for (int i = 0; i < elements.length && i < number; i++) {
			char element = elements[i];
			String label = null;
			if (Character.isLetterOrDigit(element) && isIntElement == false) label = element + "";
			else label = (int)element + "";
			if (i == 0) labelString.append(label);
			else labelString.append(", " + label);
		}
		return labelString.toString();
	}

	public String toString() {
		String result = "{ ";
		
		if (!isEmptySet()) {
			result = result + getElementLabel(elements[0], false);
			for (int i = 1; i < elements.length; i++) result = result + ", " + getElementLabel(elements[i], false);
		}
		result = result + " }";
		return result;
	}
	
	public String toLaTeXString() {
		String result = "\\{ ";
		
		if (!isEmptySet()) {
			result = result + getElementLabel(elements[0], false);
			for (int i = 1; i < elements.length; i++) result = result + ", " + getElementLabel(elements[i], false);
		}
		result = result + " \\}";
		return result;
	}
	
	public String toLaTeXString(boolean isIntElement) {
		String result = "\\{ ";
		
		if (!isEmptySet()) {
			result = result + getElementLabel(elements[0], isIntElement);
			for (int i = 1; i < elements.length; i++) result = result + ", " + getElementLabel(elements[i], isIntElement);
		}
		result = result + " \\}";
		return result;
	}

	/**
	 * Check the string content represent empty set or not. Note that the empty set string is: {}
	 */
	public static boolean isEmptySetString(String content) {
		int start = content.indexOf('{');
		if (start < 0 || start >= content.length()) return false;
		int end = content.indexOf('}');
		if (end < 0 || end >= content.length()) return false;
		if (start+1 >= end-1) return true;
		else return false;
	}
	
	public static Set randomGenerateSubset(Set universalSet) {
		if (universalSet.isEmptySet()) return null;
		boolean[] charFunction = new boolean[universalSet.elements.length];
		for (int i = 0; i < charFunction.length; i++) {
			double test = Math.random();
			if (test >= 0.5) charFunction[i] = true;
			else charFunction[i] = false;
		}
		return universalSet.getSubsetForCharFunction(charFunction);
	}
	
	public static Set randomGenerateSubset(Set universalSet, int elementNumber) {
		if (universalSet.isEmptySet()) return null;
		if (universalSet.elements.length <= elementNumber) return universalSet;
		
		boolean[] charFunction = new boolean[universalSet.elements.length];
		int counter = 0;
		for (int i = 0; i < charFunction.length; i++) {
			double test = Math.random();
			if (test >= 0.5) {
				charFunction[i] = true;
				counter = counter + 1;
				if (counter == elementNumber) break;
			}
			else charFunction[i] = false;
		}
		if (counter < elementNumber) {
			int startIndex = (int)(Math.random() * charFunction.length);
			while (counter < elementNumber) {
				if (charFunction[startIndex] == false) {
					charFunction[startIndex] = true;
					counter = counter + 1;
				}
				startIndex = startIndex + 1;
				if (startIndex >= charFunction.length) startIndex = 0;
			}
		}
		return universalSet.getSubsetForCharFunction(charFunction);
	}

	private static int getTrueNumber(boolean[] charfun) {
		int result = 0;
		for (int i = 0; i < charfun.length; i++) {
			if (charfun[i] == true) result++;
		}
		return result;
	}
	
	private static boolean isAllTrue(boolean[] charfun) {
		for (int i = 0; i < charfun.length; i++) {
			if (charfun[i] == false) return false;
		}
		return true;
	}
}
