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
public class AndFormula extends Formula {

	public AndFormula(Formula left, Formula right) {
		super(Symbol.OPERATOR_AND, left, right);
	}
	
	public boolean isAndFormula() {
		return true;
	}
	public Formula getACopy() {
		Formula leftCopy = left.getACopy();
		Formula rightCopy = right.getACopy();
		Formula result = new AndFormula(leftCopy, rightCopy);
		return result;
	}
	public Formula getSubstitutionResult(List<Substitution> substitutionList){
		Formula leftResult = left.getSubstitutionResult(substitutionList);
		Formula rightResult = right.getSubstitutionResult(substitutionList);
		Formula result = new AndFormula(leftResult, rightResult);
		return result;
	}

	public Formula getReplaceResult(Formula subformula, Formula otherformula) {
		if (syntaxEqualTo(subformula)) return otherformula.getACopy();
		Formula leftResult = left.getReplaceResult(subformula, otherformula);
		Formula rightResult = right.getReplaceResult(subformula, otherformula);
		Formula result = new AndFormula(leftResult, rightResult);
		return result;
	}
	
	@Override
	public String toLaTeXString() {
		return "(" + left.toLaTeXString() + Symbol.LATEX_AND + " " + right.toLaTeXString() + ")";
	}

	@Override
	public String toSimpleLaTeXString() {
		String leftString = left.toSimpleLaTeXString();
		if (!left.isAtomicFormula()) {
			if (left.operator != Symbol.OPERATOR_AND && Symbol.isPriorTo(Symbol.OPERATOR_AND, left.operator)) leftString = "(" + leftString + ")";
		}
		String rightString = right.toSimpleLaTeXString();
		if (!right.isAtomicFormula()) {
			if (right.operator != Symbol.OPERATOR_AND && Symbol.isPriorTo(Symbol.OPERATOR_AND, right.operator)) rightString = "(" + rightString + ")";
		}
		return leftString + " " + Symbol.LATEX_AND + " " + rightString;
	}

	
	@Override
	public boolean getTruthValue(List<TruthAssignment> function) {
		boolean leftResult = left.getTruthValue(function);
		if (leftResult == false) return false;
		boolean rightResult = right.getTruthValue(function);
		if (rightResult == false) return false;
		return true;
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
		return new OrFormula(leftResult, rightResult);
	}

}
