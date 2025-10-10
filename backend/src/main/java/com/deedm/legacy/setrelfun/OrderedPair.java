/**
 * 
 */
package com.deedm.legacy.setrelfun;

/**
 * @author zxc
 *
 */
public class OrderedPair {
	private char first;
	private char second;
	
	public OrderedPair(char first, char second) {
		this.first = first;
		this.second = second;
	}
	
	public char getFirst() {
		return first;
	}
	
	public char getSecond() {
		return second;
	}
	
	public String toString() {
		return "<" + Set.getElementLabel(first, false) + ", " + Set.getElementLabel(second, false) + ">";
	}

	public String toLaTeXString() {
		return "\\langle " + Set.getElementLabel(first, false) + ", " + Set.getElementLabel(second, false) + "\\rangle ";
	}
	
	public String toLaTeXString(boolean isIntElement) {
		return "\\langle " + Set.getElementLabel(first, isIntElement) + ", " + Set.getElementLabel(second, isIntElement) + "\\rangle ";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = result * prime + first;
		result = result * prime + second;
		return result;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == this) return true;
		if (!(other instanceof OrderedPair)) return false;
		
		OrderedPair otherPair = (OrderedPair)other;
		return (first == otherPair.first && second == otherPair.second);
	}
}
