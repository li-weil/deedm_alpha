package com.deedm.legacy.proplogic.formula;

import java.util.ArrayList;
import java.util.List;

import com.deedm.legacy.proplogic.Substitution;
import com.deedm.legacy.proplogic.TruthAssignment;
import com.deedm.legacy.proplogic.TruthAssignmentFunction;

/**
 * @author zxc
 * @date 2020/03/27
 */
public abstract class Formula {
	protected char operator = 'p';
	
	protected Formula left = null;
	protected Formula right = null;

	public Formula(char operator, Formula left, Formula right) {
		this.operator = operator;
		this.left = left;
		this.right = right;
	}

	public char getOperator() {
		return operator;
	}
	
	public Formula getLeft() {
		return left;
	}
	
	public Formula getRight() {
		return right;
	}

	public boolean isAtomicFormula() {
		return false;
	}
	public boolean isNegFormula() {
		return false;
	}
	public boolean isAndFormula() {
		return false;
	}
	public boolean isOrFormula() {
		return false;
	}
	public boolean isImpFormula() {
		return false;
	}
	public boolean isBiImpFormula() {
		return false;
	}
	
	public List<Formula> getAllSubFormulas() {
		List<Formula> result = null;
		if (isAtomicFormula()) {
			result = new ArrayList<Formula>();
		} else if (isNegFormula()) {
			result = right.getAllSubFormulas();
		} else {
			result = left.getAllSubFormulas();
			List<Formula> rightResult = right.getAllSubFormulas();
			result.addAll(rightResult);
		}
		result.add(this);
		return result;
	}
	
	public List<Formula> getAllSyntaxDifferentSubFormulas() {
		List<Formula> result = null;
		if (isAtomicFormula()) {
			result = new ArrayList<Formula>();
		} else if (isNegFormula()) {
			result = right.getAllSyntaxDifferentSubFormulas();
		} else {
			result = left.getAllSyntaxDifferentSubFormulas();
			List<Formula> rightResult = right.getAllSyntaxDifferentSubFormulas();
			for (Formula formula : rightResult) {
				boolean found = false;
				for (Formula resultFormula : result) {
					if (formula.syntaxEqualTo(resultFormula)) {
						found = true;
						break;
					}
				}
				if (found == false) result.add(formula);
			}
		}
		result.add(this);
		return result;
	}

	public List<TruthAssignment> getAllVariablesForAssignmentTruth() {
		List<TruthAssignment> result = new ArrayList<TruthAssignment>();
		if (isAtomicFormula()) {
			TruthAssignment assignment = new TruthAssignment(operator, false);
			result.add(assignment);
		} else if (isNegFormula()) {
			result = right.getAllVariablesForAssignmentTruth();
		} else {
			List<TruthAssignment> leftResult = left.getAllVariablesForAssignmentTruth();
			List<TruthAssignment> rightResult = right.getAllVariablesForAssignmentTruth();
			int leftIndex = 0, rightIndex = 0; 
			while (leftIndex < leftResult.size() && rightIndex < rightResult.size()) {
				TruthAssignment leftAssignment = leftResult.get(leftIndex);
				TruthAssignment rightAssignment = rightResult.get(rightIndex);
				int compare = leftAssignment.compareTo(rightAssignment);
				if (compare < 0) {
					result.add(leftAssignment);
					leftIndex = leftIndex + 1;
				} else if (compare > 0) {
					result.add(rightAssignment);
					rightIndex = rightIndex + 1;
				} else {
					result.add(leftAssignment);
					leftIndex = leftIndex + 1;
					rightIndex = rightIndex + 1;
				}
			}
			while (leftIndex < leftResult.size()) {
				TruthAssignment leftAssignment = leftResult.get(leftIndex);
				result.add(leftAssignment);
				leftIndex = leftIndex + 1;
			}
			while (rightIndex < rightResult.size()) {
				TruthAssignment rightAssignment = rightResult.get(rightIndex);
				result.add(rightAssignment);
				rightIndex = rightIndex + 1;
			}
		}
		return result;
	}

	public char[] getAllVariables() {
		List<TruthAssignment> variableList = getAllVariablesForAssignmentTruth();
		char[] variableSet = new char[variableList.size()];
		int index = 0;
		for (TruthAssignment variable : variableList) {
			variableSet[index] =variable.getVariable();
			index++;
		}
		return variableSet;
	}
	
	public boolean syntaxEqualTo(Formula other) {
		if (this == other) return true;
		if (operator != other.operator) return false;
		if (left != null) {
			if (other.left == null) return false;
			boolean result = left.syntaxEqualTo(other.left);
			if (result == false) return false;
		} else if (other.left != null) return false;
		
		if (right != null) {
			if (other.right == null) return false;
			boolean result = right.syntaxEqualTo(other.right);
			if (result == false) return false;
		} else if (other.right != null) return false;
		return true;
	}

	public String toString() {
		String leftFormula = null;
		String rightFormula = null;
		if (left != null) leftFormula = left.toString();
		if (right != null) rightFormula = right.toString();
		
		if (left == null && right == null) return "" + operator;
		if (left == null) return "(" + operator + rightFormula + ")";
		return "(" + leftFormula + " " + operator + " " + rightFormula + ")";
	}
	
	public abstract String toLaTeXString();
	public abstract String toSimpleLaTeXString();
	
	public abstract boolean isLimitedToNegAndOrOperators();
	public abstract Formula getDulaFormula();
	
	public abstract boolean getTruthValue(List<TruthAssignment> function);
	public abstract Formula getACopy();
	public abstract Formula getSubstitutionResult(List<Substitution> substitutionList);
	public abstract Formula getReplaceResult(Formula subformula, Formula otherformula);
	
	public boolean getTruthValue(TruthAssignmentFunction function) {
		return getTruthValue(function.getTruthAssignmentList());
	}
	
}
