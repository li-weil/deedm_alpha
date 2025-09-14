/**
 * 
 */
package com.deedm.legacy.proplogic;

import proplogic.formula.Formula;

/**
 * @author zxc
 *
 */
public class Substitution {
	private char variable = 'p';
	private Formula formula = null;
	
	public Substitution(char variable, Formula formula) {
		this.variable = variable;
		this.formula = formula;
	}
	
	public char getVariable() {
		return variable;
	}
	
	public Formula getFormula() {
		return formula;
	}

}
