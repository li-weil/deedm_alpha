/**
 * 
 */
package com.deedm.legacy.setrelfun;

/**
 * @author Zhou Xiaocong
 * @since 2024/03/28
 *
 */
public class SetrelfunUtil {

	private static String errorMessage = null;

	public static char[] extractSetElements(String content, boolean isIntElement) {
		int start = content.indexOf('{');
		if (start < 0 || start >= content.length()) start = 0;
		else start = start+1;
		int end = content.indexOf('}');
		if (end < 0 || end >= content.length()) end = content.length();
		if (start >= end) return null;
		
		content = content.substring(start, end);
		String[] setStringArray = content.split(",");
		if (setStringArray.length <= 0)	return null;
		char[] result = new char[setStringArray.length];
		for (int i = 0; i < setStringArray.length; i++) {
			String variableName = setStringArray[i].trim();
			if (isIntElement == false) {
				if (variableName.length() != 1) {
					errorMessage = "The element [" + variableName + "] is not a single character!";
					return null;
				}
				result[i] = variableName.charAt(0);
			} else {
				try {
					int element = Integer.parseInt(variableName);
					result[i] = (char)element;
				} catch (NumberFormatException exc) {
					errorMessage = "The element [" + variableName + "] is not a legal integer!";
					return null;
				}
			}
		}
		return result;
	}

	public static char[] trimCharArray(char[] array, int length) {
		char[] result = new char[length];
		for (int i = 0; i < length; i++) result[i] = array[i];
		return result;
	}

	public static int power(int n, int base) {
		int result = 1;
		for (int i = 0; i < n; i++) result = result * base;
		return result;
	}

	public static Relation extractRelation(Set from, Set to, String content, boolean isIntElement) {
		Relation result = new Relation(from, to);
		int index = 0;
		boolean inPair = false;
		StringBuffer elementBuffer = null;
		char firstElement = 0;
		boolean hasFirstElement = false;
		while (index < content.length()) {
			char ch = content.charAt(index);
			index = index + 1;
			if (ch == '{' || ch == '\n' || ch == '\r' || Character.isWhitespace(ch)) continue;
			if (ch == '}') break;
			if (ch == '<') {
				if (inPair == true) {
					errorMessage = "Ilegal <, do not support nested ordered pairs!";
					return null;
				} else {
					inPair = true;
					elementBuffer = new StringBuffer();
				}
				continue;
			}
			if (ch == ',') {
				if (inPair != true) continue;
				String firstElementString = elementBuffer.toString().trim();
				if (isIntElement == false) {
					if (firstElementString.length() != 1) {
						errorMessage = "The element [" + firstElementString + "] is not a single character!";
						return null;
					}
					firstElement = firstElementString.charAt(0);
				} else {
					try {
						int element = Integer.parseInt(firstElementString);
						firstElement = (char)element;
					} catch (NumberFormatException exc) {
						errorMessage = "The element [" + firstElementString + "] is not a legal integer!";
						return null;
					}
				}
				if (from.inSet(firstElement) == false) {
					errorMessage = "The first element " + firstElementString + " is not in the from set " + from.toString();
					return null;
				}
				hasFirstElement = true;
				elementBuffer = new StringBuffer();
				continue;
			}
			if (ch == '>') {
				if (inPair == false) {
					errorMessage = "Ilegal >, there is no < match this >!";
					return null;
				}
				if (hasFirstElement == false) {
					errorMessage = "Ilegal >, does not exist two elements between < and >!";
					return null;
				}
				String secondElementString = elementBuffer.toString().trim();
				char secondElement = 0;
				if (isIntElement == false) {
					if (secondElementString.length() != 1) {
						errorMessage = "The element [" + secondElementString + "] is not a single character!";
						return null;
					}
					secondElement = secondElementString.charAt(0);
				} else {
					try {
						int element = Integer.parseInt(secondElementString);
						secondElement = (char)element;
					} catch (NumberFormatException exc) {
						errorMessage = "The element [" + secondElementString + "] is not a legal integer!";
						return null;
					}
				}
				if (to.inSet(secondElement) == false) {
					errorMessage = "The second element " + secondElementString + " is not in the to set " + to.toString();
					return null;
				}
				OrderedPair pair = new OrderedPair(firstElement, secondElement);
				result.addPair(pair);
				hasFirstElement = false;
				inPair = false;
				elementBuffer = new StringBuffer();
				continue;
			}
			elementBuffer.append(""+ch);
		}
		
		return result;
	}

	public static String getExtractErrorMessage() {
		return errorMessage;
	}
}
