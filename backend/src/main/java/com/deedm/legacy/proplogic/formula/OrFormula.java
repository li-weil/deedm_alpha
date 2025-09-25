/**
 * 
 */
package com.deedm.legacy.proplogic.formula;

import java.util.List;

import com.deedm.legacy.proplogic.Substitution;
import com.deedm.legacy.proplogic.TruthAssignment;

/**
 * @author zxc
 *
 */
public class OrFormula extends Formula {

	public OrFormula(Formula left, Formula right) {
		super(Symbol.OPERATOR_OR, left, right);
	}

	public boolean isOrFormula() {
		return true;
	}

	public Formula getACopy() {
		Formula leftCopy = left.getACopy();
		Formula rightCopy = right.getACopy();
		
		Formula result = new OrFormula(leftCopy, rightCopy);
		return result;
	}
	public Formula getSubstitutionResult(List<Substitution> substitutionList){
		Formula leftResult = left.getSubstitutionResult(substitutionList);
		Formula rightResult = right.getSubstitutionResult(substitutionList);
		Formula result = new OrFormula(leftResult, rightResult);
		return result;
	}
	
	@Override
	public Formula getReplaceResult(Formula subformula, Formula otherformula) {
		if (syntaxEqualTo(subformula)) return otherformula.getACopy();
		Formula leftResult = left.getReplaceResult(subformula, otherformula);
		Formula rightResult = right.getReplaceResult(subformula, otherformula);
		Formula result = new OrFormula(leftResult, rightResult);
		return result;
	}

	@Override
	public String toLaTeXString() {
		return "(" + left.toLaTeXString() + Symbol.LATEX_OR + " " + right.toLaTeXString() + ")";
	}

	@Override
	public String toSimpleLaTeXString() {
		String leftString = left.toSimpleLaTeXString();
		if (!left.isAtomicFormula()) {
			if (left.operator != Symbol.OPERATOR_OR && Symbol.isPriorTo(Symbol.OPERATOR_OR, left.operator)) leftString = "(" + leftString + ")";
			else if (left.operator == Symbol.OPERATOR_AND) leftString = "(" + leftString + ")";
		}
		String rightString = right.toSimpleLaTeXString();
		if (!right.isAtomicFormula()) {
			if (right.operator != Symbol.OPERATOR_OR && Symbol.isPriorTo(Symbol.OPERATOR_OR, right.operator)) rightString = "(" + rightString + ")";
			else if (right.operator == Symbol.OPERATOR_AND) rightString = "(" + rightString + ")";
		}
		return leftString + Symbol.LATEX_OR + " " + rightString;
	}
	
	@Override
	public boolean getTruthValue(List<TruthAssignment> function) {
		boolean leftResult = left.getTruthValue(function);
		if (leftResult == true) return true;
		boolean rightResult = right.getTruthValue(function);
		if (rightResult == true) return true;
		return false;
	}

	@Override
	public boolean isLimitedToNegAndOrOperators() {
		return left.isLimitedToNegAndOrOperators() && right.isLimitedToNegAndOrOperators();
	}

	@Override
	public Formula getDulaFormula() {
		Formula leftResult = left.getDulaFormula();
		if (leftResult == null) return null;
		Formula rightResult = right.getDulaFormula();
		if (rightResult == null) return null;
		return new AndFormula(leftResult, rightResult);
	}
}
