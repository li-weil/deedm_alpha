/**
 * 
 */
package com.deedm.legacy.algebra;

import com.deedm.legacy.setrelfun.Set;

public class BinaryOperator<T> {
	private static String reasonMessage = null;
	
	protected T[] base = null;
	protected int[][] optable = null; // It should satisfy that base[optable[i][j]] = base[i] * base[j] 
	String name = "+";
	
	public BinaryOperator(T[] base) {
		this.base = base;
	}
	
	public BinaryOperator(T[] base, T[][] optable) {
		this.base = base;
		this.optable = new int[base.length][base.length];
		
		for (int i = 0; i < base.length; i++) {
			for (int j = 0; j < base.length; j++) {
				int index = getElementIndex(optable[i][j]);
				this.optable[i][j] = index;
			}
		}
	}
	
	public BinaryOperator(T[] base, int[][] optable) {
		this.base = base;
		this.optable = new int[base.length][base.length];
		
		for (int i = 0; i < base.length; i++) {
			for (int j = 0; j < base.length; j++) {
				this.optable[i][j] = optable[i][j];
			}
		}
	}
	
	public T[] getBase() {
		return base;
	}

	public int[][] getIndexedOperationTable() {
		return optable;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean setOperationResult(T first, T second, T result) {
		int firstIndex = -1;
		int secondIndex = -1;
		
		for (int i = 0; i < base.length; i++) {
			if (base[i].equals(first)) firstIndex = i;
			if (base[i].equals(second)) secondIndex = i;
			if (firstIndex >= 0 && secondIndex >= 0) break;
		}
		if (firstIndex < 0 || secondIndex < 0) return false;
		if (optable == null) optable = new int[base.length][base.length];
		optable[firstIndex][secondIndex] = getElementIndex(result);
		return true;
	}
	
	public T getOperationResult(T first, T second) {
		int firstIndex = -1;
		int secondIndex = -1;
		
		for (int i = 0; i < base.length; i++) {
			if (base[i].equals(first)) firstIndex = i;
			if (base[i].equals(second)) secondIndex = i;
			if (firstIndex >= 0 && secondIndex >= 0) break;
		}
		if (optable == null || firstIndex < 0 || secondIndex < 0) return null;
		return base[optable[firstIndex][secondIndex]];
	}
	
	public boolean hasIdentity() {
		if (optable == null) return false;
		int i, j;
		
		for (i = 0; i < base.length; i++) {
			for (j = 0; j < base.length; j++) 
				if (!(base[optable[i][j]].equals(base[j]))) break;
			if (j < base.length) continue;		// 第 i 个元素不可能是单位元
			
			// 第 i 行的运算结果与基集相同，检查第 i 列的运算结果是否也与基集相同
			for (j = 0; j < base.length; j++) 
				if (!(base[optable[j][i]].equals(base[j]))) break;
			if (j < base.length) continue;		// 第 i 个元素不是单位元
			
			return true;		// base[i] 是单位元
		}
		return false;
	}

	public T getIdentity() {
		if (optable == null) return null;
		int i, j;
		
		for (i = 0; i < base.length; i++) {
			for (j = 0; j < base.length; j++) 
				if (!(base[optable[i][j]].equals(base[j]))) break;
			if (j < base.length) continue;		// 第 i 个元素不可能是单位元
			
			// 第 i 行的运算结果与基集相同，检查第 i 列的运算结果是否也与基集相同
			for (j = 0; j < base.length; j++) 
				if (!(base[optable[j][i]].equals(base[j]))) break;
			if (j < base.length) continue;		// 第 i 个元素不是单位元
			
			return base[i];		// base[i] 是单位元
		}
		return null;
	}
	
	public boolean hasZeroElement() {
		if (optable == null) return false;
		int i, j;
		
		for (i = 0; i < base.length; i++) {
			for (j = 0; j < base.length; j++) 
				if (!(base[optable[i][j]].equals(base[i]))) break;
			if (j < base.length) continue;		// 第 i 个元素不可能是零元
			
			// 第 i 行的运算结果与基集相同，检查第 i 列的运算结果是否也与基集相同
			for (j = 0; j < base.length; j++) 
				if (!(base[optable[j][i]].equals(base[i]))) break;
			if (j < base.length) continue;		// 第 i 个元素不是零元
			
			return true;		// base[i] 是零元
		}
		return false;
	}

	public T getZeroElement() {
		if (optable == null) return null;
		int i, j;
		
		for (i = 0; i < base.length; i++) {
			for (j = 0; j < base.length; j++) 
				if (!(base[optable[i][j]].equals(base[i]))) break;
			if (j < base.length) continue;		// 第 i 个元素不可能是零元
			
			// 第 i 行的运算结果与基集相同，检查第 i 列的运算结果是否也与基集相同
			for (j = 0; j < base.length; j++) 
				if (!(base[optable[j][i]].equals(base[i]))) break;
			if (j < base.length) continue;		// 第 i 个元素不是零元
			
			return base[i];		// base[i] 是零元
		}
		return null;
	}
	
	public boolean hasInverse() {
		if (!hasIdentity()) {
			reasonMessage = "Operator has NOT identity element!";
			return false;
		}
		T identity = getIdentity();
		
		int i, j;
		for (i = 0; i < base.length; i++) {
			int inverseIndex = -1;
			for (j = 0; j < base.length; j++) {
				if (base[optable[i][j]].equals(identity)) {
					if (inverseIndex < 0) inverseIndex = j;
					else {
						// 元素i与元素j的运算结果是单位元，且与元素inverseIndex的运算结果也是单位元
						reasonMessage = "Element [" + base[i] + "] has two inverses: [" + base[j] + "] and [" + base[inverseIndex] + "]!";
						return false;  
					}
				}
			}
			if (inverseIndex < 0) {
				reasonMessage = "Element [" + base[i] + "] has NOT inverse!";
				return false;	// 元素 i 没有逆元
			}
			for (j = 0; j < base.length; j++) {
				if (base[optable[inverseIndex][j]].equals(identity)) {
					// 元素 inverseIndex 与元素 j 的运算结果是单位元，但j 不等于 i，这表明 i 的逆元不是
					// inverseIndex
					if (j != i) {
						reasonMessage = "Element [" + base[i] + "] and [" + base[inverseIndex] + " are not inverse to each other!";
						return false;		
					}
					break;
				}
			}
			if (j >= base.length) {
				reasonMessage = "Element [" + base[inverseIndex] + "] has NOT inverse!";
				return false;	// 元素 inverseIndex 没有逆元
			}
		}
		// 每个元素都有逆元！
		return true;
	}
	
	public boolean hasInverse(T element) {
		if (!hasIdentity()) {
			return false;
		}
		T identity = getIdentity();
		
		int i = getElementIndex(element);
		if (i < 0 || i >= base.length) return false;
		
		int inverseIndex = -1;
		int j = 0;
		for (j = 0; j < base.length; j++) {
			if (base[optable[i][j]].equals(identity)) {
				if (inverseIndex < 0) inverseIndex = j;
				else return false;			// 元素i与元素j的运算结果是单位元，且与元素inverseIndex的运算结果也是单位元
			}
		}
		if (inverseIndex < 0) return false; 	// 元素 i 没有逆元

		for (j = 0; j < base.length; j++) {
			if (base[optable[inverseIndex][j]].equals(identity)) {
				// 元素 inverseIndex 与元素 j 的运算结果是单位元，但j 不等于 i，这表明 i 的逆元不在inverseIndex
				if (j != i) return false;		
				break;
			}
		}
		if (j >= base.length) return false;		// 元素 reverseIndex 没有逆元
		return true;			// 元素 i 和元素 inverseIndex 互为逆元
	}

	public T getInverse(T element) {
		if (!hasIdentity()) {
			return null;
		}
		T identity = getIdentity();
		
		int i = getElementIndex(element);
		if (i < 0 || i >= base.length) return null;
		
		int inverseIndex = -1;
		int j = 0;
		for (j = 0; j < base.length; j++) {
			if (base[optable[i][j]].equals(identity)) {
				if (inverseIndex < 0) inverseIndex = j;
				else return null;			// 元素i与元素j的运算结果是单位元，且与元素reverseIndex的运算结果也是单位元
			}
		}
		if (inverseIndex < 0) return null; 	// 元素 i 没有逆元

		for (j = 0; j < base.length; j++) {
			if (base[optable[inverseIndex][j]].equals(identity)) {
				// 元素 reverseIndex 与元素 j 的运算结果是单位元，但j 不等于 i，这表明 i 的逆元不在  reverseIndex
				if (j != i) return null;		
				break;
			}
		}
		if (j >= base.length) return null;		// 元素 reverseIndex 没有逆元
		return base[inverseIndex];			// 元素 i 和元素 reverseIndex 互为逆元
	}

	public int getInverse(T element, T[] inverses) {
		if (!hasIdentity()) return 0;

		T identity = getIdentity();
		int i = getElementIndex(element);
		if (i < 0 || i >= base.length) return 0;
		
		int counter = 0;
		for (int j = 0; j < base.length; j++) {
			if (base[optable[i][j]].equals(identity) && base[optable[j][i]].equals(identity)) {
				inverses[counter] = base[j];
				counter = counter + 1;
			}
		}
		return counter;
	}
	
	public boolean isAssociative() {
		int i, j, k;
		
		for (i = 0; i < base.length; i++) {
			for (j = 0; j < base.length; j++) {
				for (k = 0; k < base.length; k++) {
					int res1 = optable[i][j];	// 先运算元素 i 和 j 
					if (res1 < 0 || res1 >= base.length) return false;
					
					int res2 = optable[j][k];	// 先运算 j 和 k
					if (res2 < 0 || res2 >= base.length) return false;
					
					if (optable[res1][k] != optable[i][res2]) {
						T one = base[i];
						T two = base[j];
						T three = base[k];
						reasonMessage = base[optable[res1][k]] + "= (" + one + name + two + ")" + name + three + "\\neq " + one + name + "(" + two + name + three + ") = " + base[optable[i][res2]]; 
						return false;
					}
				}
			}
		}
		return true;
	}

	public boolean isCommutative() {
		for (int i = 0; i < base.length; i++) {
			for (int j = 0; j < base.length; j++) {
				if (optable[i][j] != optable[j][i]) {
					T one = base[i];
					T two = base[j];
					reasonMessage =  base[optable[i][j]] + "=" + one + name + two +  "\\neq " + two + name + one + " = " + base[optable[j][i]]; 
					return false; 
				}
			}
		}
		return true;
	}
	
	public boolean isIdempotent() {
		for (int i = 0; i < base.length; i++) {
			if (!base[optable[i][i]].equals(base[i])) {
				T one = base[i];
				reasonMessage =  base[optable[i][i]] + "=" + one + name + one +  "\\neq " + one; 
				return false; 
			}
		}
		return true;
	}

	public boolean isDistributiveWith(BinaryOperator<T> other) {
		if (base != other.base) {
			if (base.length != other.base.length) {
				reasonMessage = "\\textrm{base.length}(" + base.length + ") \\neq \\textrm{other.base.length}(" + other.base.length + ")";
				return false;
			}
			for (int i = 0; i < base.length; i++) 
				if (!base[i].equals(other.base[i])) {
					reasonMessage = "\\textrm{base[i]}(" + base[i] + ") \\neq \\textrm{other.base[i]}(" + other.base[i] + ")(i = " + i + ")";
					return false;
				}
		}
		for (int i = 0; i < base.length; i++) {
			for (int j = 0; j < base.length; j++) {
				for (int k = 0; k < base.length; k++) {
					int indexOfResult = optable[j][k];
					int indexOfOtherOne = other.optable[i][j];
					int indexofOtherTwo = other.optable[i][k];
					if (other.optable[i][indexOfResult] != optable[indexOfOtherOne][indexofOtherTwo]) {
						T one = base[i];
						T two = base[j];
						T three = base[k];
						reasonMessage = other.base[other.optable[i][indexOfResult]] + " = " + one + other.name + "(" + two + name + three + ") \\neq (" + one + other.name + two + ")" + name + "(" + one + other.name + three + ") = " + base[optable[indexOfOtherOne][indexofOtherTwo]];  
						return false;
					}
					
					indexOfOtherOne = other.optable[j][i];
					indexofOtherTwo = other.optable[k][i];
					if (other.optable[indexOfResult][i] != optable[indexOfOtherOne][indexofOtherTwo]) {
						T one = base[i];
						T two = base[j];
						T three = base[k];
						reasonMessage = other.base[other.optable[indexOfResult][i]] + " = (" + two + name + three + ")" + other.name + one + "\\neq (" + two + other.name + one + ")" + name + "(" + three + other.name + one + ") = " + base[optable[indexOfOtherOne][indexofOtherTwo]];  
						return false;
					}
				}
			}
		}
		
		return true;
	}

	public boolean isAbsorptiveWith(BinaryOperator<T> other) {
		if (base != other.base) {
			if (base.length != other.base.length) return false;
			for (int i = 0; i < base.length; i++) 
				if (base[i] != other.base[i]) return false;
		}
		for (int i = 0; i < base.length; i++) {
			for (int j = 0; j < base.length; j++) {
				int indexOfResult = optable[i][j];
				if (!other.base[other.optable[i][indexOfResult]].equals(base[i])) {
					T one = base[i];
					T two = base[j];
					reasonMessage = other.base[other.optable[i][indexOfResult]] + " = " + one + other.name + "(" + one + name + two + ")\\neq " + one + " = " + base[i];  
					return false;
				}

				indexOfResult = other.optable[i][j];
				if (!base[optable[i][indexOfResult]].equals(base[i])) {
					T one = base[i];
					T two = base[j];
					reasonMessage = base[optable[i][indexOfResult]] + " = " + one + name + "(" + one + other.name + two + ")\\neq " + one + " = " + base[i];  
					return false;
				}
			}
		}
		return true;
	}

	public boolean isCancellative() {
		boolean hasZero = hasZeroElement();
		T zero = null;
		if (hasZero) zero = getZeroElement();
		
		for (int i = 0; i < base.length; i++) {
			if (hasZero && base[i].equals(zero)) continue;
			
			for (int j = 0; j < base.length; j++) {
				for (int k = 0; k < base.length; k++) {
					if (optable[i][j] == optable[i][k] && j != k) {
						T one = base[i];
						T two = base[j];
						T three = base[k];
						reasonMessage = base[optable[i][j]] + " = (" + one + name + two + " = " + one + name + three + ")\\quad\\textrm{but}\\quad " + two + "\\neq " + three;  
						return false;
					}
					if (optable[j][i] == optable[k][i] && j != k) {
						T one = base[i];
						T two = base[j];
						T three = base[k];
						reasonMessage = base[optable[i][j]] + "= (" + two + name + one + " = " + three + name + one + ")\\quad\\textrm{but}\\quad " + two + "\\neq " + three;  
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		if (name == null) buffer.append("    " + "|");
		else buffer.append(name + "|");
		for (int i = 0; i < base.length; i++) buffer.append("  " + base[i] + " ");
		buffer.append("\n");
		
		buffer.append("----" + "+");
		for (int i = 0; i < base.length; i++) buffer.append("----");
		buffer.append("--\n");
		
		for (int i = 0; i < base.length; i++) {
			buffer.append(" " + base[i] + " |");
			for (int j = 0; j < base.length; j++) {
				buffer.append(" " + base[optable[i][j]] + " ");
			}
			buffer.append("\n");
		}
		return buffer.toString();
	}
	
	public String toLaTeXString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\\begin{tabular}{c|");
		for (int i = 0; i < base.length; i++) buffer.append("c");
		buffer.append("}\n");
		
		if (name == null) buffer.append("\\hline\\quad\\quad ");
		else buffer.append("\\hline " + name);
		for (int i = 0; i < base.length; i++) buffer.append(" & " + base[i]);
		buffer.append("\\\\\n");
		
		for (int i = 0; i < base.length; i++) {
			if (i == 0) buffer.append("\\hline " + base[i]);
			else buffer.append(base[i]);
			for (int j = 0; j < base.length; j++) {
				buffer.append(" & " + base[optable[i][j]]);
			}
			buffer.append("\\\\\n");
		}

		buffer.append("\\hline\n");
		buffer.append("\\end{tabular}\n");
		return buffer.toString();
	}
	
	public String toLaTeXString(boolean isIntElement) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\\begin{tabular}{c|");
		for (int i = 0; i < base.length; i++) buffer.append("c");
		buffer.append("}\n");
		
		if (name == null) buffer.append("\\hline\\quad\\quad ");
		else buffer.append("\\hline " + name);
		for (int i = 0; i < base.length; i++) buffer.append(" & " + getElementLabel(base[i], isIntElement));
		buffer.append("\\\\\n");
		
		for (int i = 0; i < base.length; i++) {
			if (i == 0) buffer.append("\\hline " + getElementLabel(base[i], isIntElement));
			else buffer.append(getElementLabel(base[i], isIntElement));
			for (int j = 0; j < base.length; j++) {
				buffer.append(" & " + getElementLabel(base[optable[i][j]], isIntElement));
			}
			buffer.append("\\\\\n");
		}

		buffer.append("\\hline\n");
		buffer.append("\\end{tabular}\n");
		return buffer.toString();
	}

	public static String getReasonMessage() {
		return reasonMessage;
	}
	
	public static BinaryOperator<Character> extractBinaryOperator(Character[] baseSet, String content, boolean isIntElement) {
		char[] baseElement = new char[baseSet.length];
		for (int i = 0; i < baseSet.length; i++) baseElement[i] = (char)baseSet[i];

		Set base = new Set(baseElement);
		int[][] optable = new int[base.length()][base.length()];
		for (int i = 0; i < base.length(); i++)
			for (int j = 0; j < base.length(); j++) optable[i][j] = 0;
		
		int index = 0;
		boolean inPair = false;
		StringBuffer elementBuffer = null;
		int firstIndex = 0;
		int secondIndex = 0;
		boolean hasFirstElement = false;
		boolean hasSecondElement = false;
		while (index < content.length()) {
			char ch = content.charAt(index);
			index = index + 1;
			if (ch == '{' || ch == '\n' || ch == '\r' || Character.isWhitespace(ch)) continue;
			if (ch == '}') break;
			if (ch == '<') {
				if (inPair == true) {
					reasonMessage = "Ilegal <, do not support nested ordered pairs!";
					return null;
				} else {
					inPair = true;
					elementBuffer = new StringBuffer();
				}
				continue;
			}
			if (ch == ',') {
				if (inPair != true) continue;
				if (!hasFirstElement) {
					String firstElementString = elementBuffer.toString().trim();
					char firstElement = 0;
					if (isIntElement == false) {
						if (firstElementString.length() != 1) {
							reasonMessage = "The element [" + firstElementString + "] is not a single character!";
							return null;
						}
						firstElement = firstElementString.charAt(0);
					} else {
						try {
							int element = Integer.parseInt(firstElementString);
							firstElement = (char)element;
						} catch (NumberFormatException exc) {
							reasonMessage = "The element [" + firstElementString + "] is not a legal integer!";
							return null;
						}
					}
					firstIndex = base.getIndex(firstElement);
					if (firstIndex < 0 || firstIndex >= base.length()) {
						reasonMessage = "The first element " + firstElementString + " is not in the set " + base.toString();
						return null;
					}
					hasFirstElement = true;
					elementBuffer = new StringBuffer();
				} else {
					String secondElementString = elementBuffer.toString().trim();
					char secondElement = 0;
					if (isIntElement == false) {
						if (secondElementString.length() != 1) {
							reasonMessage = "The element [" + secondElementString + "] is not a single character!";
							return null;
						}
						secondElement = secondElementString.charAt(0);
					} else {
						try {
							int element = Integer.parseInt(secondElementString);
							secondElement = (char)element;
						} catch (NumberFormatException exc) {
							reasonMessage = "The element [" + secondElementString + "] is not a legal integer!";
							return null;
						}
					}
					secondIndex = base.getIndex(secondElement);
					if (secondIndex < 0 || secondIndex >= base.length()) {
						reasonMessage = "The second element " + secondElementString + " is not in the set " + base.toString();
						return null;
					}
					hasSecondElement = true;
					elementBuffer = new StringBuffer();
				}
				continue;
			}
			if (ch == '>') {
				if (inPair == false) {
					reasonMessage = "Ilegal >, there is no < match this >!";
					return null;
				}
				if (hasFirstElement == false || hasSecondElement == false) {
					reasonMessage = "Ilegal >, does not exist three elements between < and >!";
					return null;
				}
				String resultElementString = elementBuffer.toString().trim();
				char resultElement = 0;
				if (isIntElement == false) {
					if (resultElementString.length() != 1) {
						reasonMessage = "The element [" + resultElementString + "] is not a single character!";
						return null;
					}
					resultElement = resultElementString.charAt(0);
				} else {
					try {
						int element = Integer.parseInt(resultElementString);
						resultElement = (char)element;
					} catch (NumberFormatException exc) {
						reasonMessage = "The element [" + resultElementString + "] is not a legal integer!";
						return null;
					}
				}
				int resultIndex = base.getIndex(resultElement);
				if (resultIndex < 0 || resultIndex >= base.length()) {
					reasonMessage = "The result element " + resultElementString + " is not in the to set " + base.toString();
					return null;
				}
				optable[firstIndex][secondIndex] = resultIndex;
				
				hasFirstElement = false;
				hasSecondElement = false;
				inPair = false;
				elementBuffer = new StringBuffer();
				continue;
			}
			elementBuffer.append(""+ch);
		}

		BinaryOperator<Character> result = new BinaryOperator<Character>(baseSet, optable);
		return result;
	}
	
	public int getElementIndex(T element) {
		int i = 0;
		for (i = 0; i < base.length; i++) {
			if (base[i].equals(element)) break;
		}
		if (i >= base.length) return -1;
		else return i;
	}
	
	public int getResultIndex(int first, int second) {
		return optable[first][second];
	}

	private String getElementLabel(T element, boolean isIntElement) {
		if (element instanceof Character) {
			char ch = (char)element;
			String label = null;
			if (Character.isLetterOrDigit(ch) && isIntElement == false) label = ch + "";
			else label = (int)ch + "";
			return label;
		} else return element.toString();
	}
}
