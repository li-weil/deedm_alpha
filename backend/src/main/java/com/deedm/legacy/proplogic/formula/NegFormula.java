package com.deedm.legacy.proplogic.formula;

import java.util.List;

import com.deedm.legacy.proplogic.Substitution;
import com.deedm.legacy.proplogic.TruthAssignment;

public class NegFormula extends Formula {

	public NegFormula(Formula right) {
		super(Symbol.OPERATOR_NOT, null, right);
	}
	public boolean isNegFormula() {
		return true;
	}
	public Formula getACopy() {
		Formula rightCopy = right.getACopy();
		
		Formula result = new NegFormula(rightCopy);
		return result;
	}
	
	public Formula getSubstitutionResult(List<Substitution> substitutionList){
		Formula rightResult = right.getSubstitutionResult(substitutionList);
		Formula result = new NegFormula(rightResult);
		return result;
	}
	
	@Override
	public Formula getReplaceResult(Formula subformula, Formula otherformula) {
		if (syntaxEqualTo(subformula)) return otherformula.getACopy();
		Formula rightResult = right.getReplaceResult(subformula, otherformula);
		Formula result = new NegFormula(rightResult);
		return result;
	}

	@Override
	public String toLaTeXString() {
		return "(" + Symbol.LATEX_NOT + " " + right.toLaTeXString() + ")";
	}

	@Override
	public String toSimpleLaTeXString() {
		String rightString = right.toSimpleLaTeXString();
		if (!right.isAtomicFormula()) {
			if (Symbol.isPriorTo(Symbol.OPERATOR_AND, right.operator)) rightString = "(" + rightString + ")";
		}
		return Symbol.LATEX_NOT + " " + rightString;
	}

	@Override
	public List<TruthAssignment> getAllVariablesForAssignmentTruth() {
		return right.getAllVariablesForAssignmentTruth();
	}

	@Override
	public boolean getTruthValue(List<TruthAssignment> function) {
		if (right.getTruthValue(function) == true) return false;
		return true;
	}
	
	@Override
	public boolean isLimitedToNegAndOrOperators() {
		return right.isLimitedToNegAndOrOperators();
	}

	@Override
	public Formula getDulaFormula() {
		Formula rightResult = right.getDulaFormula();
		if (rightResult == null) return null;
		return new NegFormula(rightResult);
	}
}
