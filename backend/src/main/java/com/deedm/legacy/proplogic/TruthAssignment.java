/**
 * 
 */
package com.deedm.legacy.proplogic;

/**
 * @author zxc
 *
 */
public class TruthAssignment implements Comparable<TruthAssignment>{
	private char variable = 'p';
	private boolean value = true;
	
	public TruthAssignment(char variable, boolean value) {
		this.variable = variable;
		this.value = value;
	}
	
	public char getVariable() {
		return variable;
	}
	
	public boolean getValue() {
		return value;
	}
	
	public void setValue(boolean value) {
		this.value = value;
	}

	@Override
	public int compareTo(TruthAssignment other) {
		if (variable < other.variable) return -1;
		else if (variable > other.variable) return 1;
		else return 0;
	}
}
