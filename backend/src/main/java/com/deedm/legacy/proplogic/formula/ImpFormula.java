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
public class ImpFormula extends Formula {

	public ImpFormula(Formula left, Formula right) {
		super(Symbol.OPERATOR_IMP, left, right);
	}

	public boolean isImpFormula() {
		return true;
	}

	public Formula getACopy() {
		Formula leftCopy = left.getACopy();
		Formula rightCopy = right.getACopy();
		
		Formula result = new ImpFormula(leftCopy, rightCopy);
		return result;
	}

	public Formula getSubstitutionResult(List<Substitution> substitutionList){
		Formula leftResult = left.getSubstitutionResult(substitutionList);
		Formula rightResult = right.getSubstitutionResult(substitutionList);
		Formula result = new ImpFormula(leftResult, rightResult);
		return result;
	}

	@Override
	public Formula getReplaceResult(Formula subformula, Formula otherformula) {
		if (syntaxEqualTo(subformula)) return otherformula.getACopy();
		Formula leftResult = left.getReplaceResult(subformula, otherformula);
		Formula rightResult = right.getReplaceResult(subformula, otherformula);
		Formula result = new ImpFormula(leftResult, rightResult);
		return result;
	}

	@Override
	public String toLaTeXString() {
		return "(" + left.toLaTeXString() + Symbol.LATEX_IMP + " " + right.toLaTeXString() + ")";
	}

	@Override
	public String toSimpleLaTeXString() {
		String leftString = left.toSimpleLaTeXString();
		if (!left.isAtomicFormula()) {
			if (Symbol.isPriorTo(Symbol.OPERATOR_IMP, left.operator) || left.operator == Symbol.OPERATOR_IMP) leftString = "(" + leftString + ")";
		}
		String rightString = right.toSimpleLaTeXString();
		if (!right.isAtomicFormula()) {
			if (Symbol.isPriorTo(Symbol.OPERATOR_IMP, right.operator)) rightString = "(" + rightString + ")";
		}
		return leftString + Symbol.LATEX_IMP + " " + rightString;
	}
	
	@Override
	public boolean getTruthValue(List<TruthAssignment> function) {
		boolean leftResult = left.getTruthValue(function);
		if (leftResult == false) return true;
		boolean rightResult = right.getTruthValue(function);
		if (rightResult == true) return true;
		return false;
	}
	
	@Override
	public boolean isLimitedToNegAndOrOperators() {
		return false;
	}

	@Override
	public Formula getDulaFormula() {
		return null;
	}
}
