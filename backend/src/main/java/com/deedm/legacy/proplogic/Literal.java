/**
 * 
 */
package proplogic.normalFormula;

import proplogic.formula.Formula;
import proplogic.formula.NegFormula;
import proplogic.formula.AtomicFormula;

/**
 * @author zxc
 *
 */
public class Literal {
	protected char variable = 'p';
	protected boolean isPositive = true;
	
	public Literal(char variable, boolean isPositive) {
		this.variable = variable;
		this.isPositive = isPositive;
	}

	
	public char getVariable() {
		return variable;
	}


	public boolean isNegative() {
		return isPositive == false;
	}

	public boolean isPositive() {
		return isPositive;
	}

	public boolean isNegativeTo(Literal other) {
		if (variable != other.variable) return false;
		if (isPositive == other.isPositive) return false;
		return true;
	}
	
	public Formula toFormula() {
		Formula subformula = new AtomicFormula(variable);
		if (isPositive == false) return new NegFormula(subformula);
		return subformula;
	}
	
	public static boolean isLiteral(Formula formula) {
		if (formula.isAtomicFormula()) return true;
		if (formula.isNegFormula()) {
			if (formula.getRight().isAtomicFormula()) return true;
		}
		return false;
	}
	
	public static Literal convert(Formula formula) {
		if (formula.isAtomicFormula()) {
			return new Literal(formula.getOperator(), true);
		} else if (formula.isNegFormula()) {
			Formula right = formula.getRight();
			if (right.isAtomicFormula()) return new Literal(right.getOperator(), false);
		}
		return null;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isPositive ? 1231 : 1237);
		result = prime * result + variable;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Literal other = (Literal) obj;
		if (isPositive != other.isPositive) return false;
		if (variable != other.variable) return false;
		return true;
	}
	
	public String toString() {
		if (isPositive) return ""+variable;
		else return "~"+variable;
	}

	public String toLaTeXString() {
		if (isPositive) return ""+variable;
		else return "\\neg "+variable;
	}
}
