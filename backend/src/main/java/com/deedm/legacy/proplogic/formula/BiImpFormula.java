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
public class BiImpFormula extends Formula {

	public BiImpFormula(Formula left, Formula right) {
		super(Symbol.OPERATOR_EQ, left, right);
	}
	public boolean isBiImpFormula() {
		return true;
	}
	public Formula getACopy() {
		Formula leftCopy = left.getACopy();
		Formula rightCopy = right.getACopy();
		
		Formula result = new BiImpFormula(leftCopy, rightCopy);
		return result;
	}
	public Formula getSubstitutionResult(List<Substitution> substitutionList){
		Formula leftResult = left.getSubstitutionResult(substitutionList);
		Formula rightResult = right.getSubstitutionResult(substitutionList);
		Formula result = new BiImpFormula(leftResult, rightResult);
		return result;
	}

	@Override
	public Formula getReplaceResult(Formula subformula, Formula otherformula) {
		if (syntaxEqualTo(subformula)) return otherformula.getACopy();
		Formula leftResult = left.getReplaceResult(subformula, otherformula);
		Formula rightResult = right.getReplaceResult(subformula, otherformula);
		Formula result = new BiImpFormula(leftResult, rightResult);
		return result;
	}
	
	@Override
	public String toLaTeXString() {
		return "(" + left.toLaTeXString() + Symbol.LATEX_EQ + " " + right.toLaTeXString() + ")";
	}

	@Override
	public String toSimpleLaTeXString() {
		String leftString = left.toSimpleLaTeXString();
		if (!left.isAtomicFormula()) {
			if (Symbol.isPriorTo(Symbol.OPERATOR_EQ, left.operator)) leftString = "(" + leftString + ")";
		}
		String rightString = right.toSimpleLaTeXString();
		if (!right.isAtomicFormula()) {
			if (Symbol.isPriorTo(Symbol.OPERATOR_EQ, right.operator)) rightString = "(" + rightString + ")";
		}
		return leftString + Symbol.LATEX_EQ + " " + rightString;
	}
	
	@Override
	public boolean getTruthValue(List<TruthAssignment> function) {
		boolean leftResult = left.getTruthValue(function);
		boolean rightResult = right.getTruthValue(function);
		if (rightResult == leftResult) return true;
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
