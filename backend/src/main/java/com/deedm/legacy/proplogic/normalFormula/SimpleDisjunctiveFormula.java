/**
 * 
 */
package com.deedm.legacy.proplogic.normalFormula;

import java.util.ArrayList;
import java.util.List;

import com.deedm.legacy.proplogic.formula.AndFormula;
import com.deedm.legacy.proplogic.formula.Formula;
import com.deedm.legacy.proplogic.formula.NegFormula;
import com.deedm.legacy.proplogic.formula.OrFormula;
import com.deedm.legacy.proplogic.formula.Symbol;
import com.deedm.legacy.proplogic.formula.AtomicFormula;

/**
 * @author zxc
 * If a simple disjunctive formula has empty literal list, then it is a contradiction, whereas, if it has a literal and its negation 
 * simultaneously, it is a tautology.  
 */
public class SimpleDisjunctiveFormula {
	List<Literal> literalList = null;

	/**
	 * Convert a formula to a simple disjunctive formula. If the formula is a simple disjunctive formula, then return the result, 
	 * otherwise return null
	 */
	public static SimpleDisjunctiveFormula convert(Formula formula) {
		List<Literal> resultList = new ArrayList<Literal>(); 
		
		if (convertToLiteralList(formula, resultList) == true) {
			SimpleDisjunctiveFormula result = new SimpleDisjunctiveFormula(resultList);
			return result;
		}
		return null;
	}
	
	/**
	 * Convert a formula to a simple disjunctive formula and simplify it. If the formula is a simple disjunctive formula, then return
	 * the result, otherwise return null. Here to simplify means remove the duplicate literal in the formula.
	 */
	public static SimpleDisjunctiveFormula convertAndSimplify(Formula formula) {
		List<Literal> resultList = new ArrayList<Literal>(); 
		
		if (convertToLiteralList(formula, resultList) == true) {
			SimpleDisjunctiveFormula result = new SimpleDisjunctiveFormula();
			// addLiteral() only add those literals which are not in the list yet.
			for (Literal literal : resultList) result.addLiteral(literal);  
			return result;
		}
		return null;
	}

	/**
	 * Remove the duplicated literal and return a new copy of the formula. 
	 */
	public SimpleDisjunctiveFormula simplify() {
		SimpleDisjunctiveFormula formula = new SimpleDisjunctiveFormula();
		for (Literal literal : literalList) formula.addLiteral(literal);
		return formula;
	}
	
	public static boolean isSimpleDisjunctiveFormula(Formula formula) {
		if (Literal.isLiteral(formula)) return true;
		if (formula.getOperator() != Symbol.OPERATOR_OR) return false;
		Formula left = formula.getLeft();
		if (!isSimpleDisjunctiveFormula(left)) return false; 
		Formula right = formula.getRight();
		if (!isSimpleDisjunctiveFormula(right)) return false;
		return true;
	}

	public boolean isTautology() {
		if (literalList == null) return false;
		if (literalList.size() == 0) return false;
		for (Literal literal : literalList) {
			for (Literal other : literalList) {
				if (literal.isNegativeTo(other)) return true;
			}
		}
		return false;
	}
	
	public boolean isContradiction() {
		if (literalList == null) return true;
		if (literalList.size() == 0) return true;
		return false;
	}
	
	public boolean isMaxTerm(char[] variableSet) {
		if (literalList.size() != variableSet.length) return false;
		for (int i = 0; i < variableSet.length; i++) {
			boolean found = false;
			for (Literal literal : literalList) {
				if (literal.variable == variableSet[i]) {
					found = true;
					break;
				}
			}
			if (found == false) return false;
		}
		return true;
	}

	/**
	 * Convert this simple disjunctive formula to an object of the class Formula.
	 */
	public Formula toFormula() {
		if (isContradiction()) return new AndFormula(new NegFormula(new AtomicFormula('p')), new AtomicFormula('p'));
		return toFormula(literalList, 0, literalList.size());
	}
	
	public void sortLiterals() {
		if (literalList == null) return;
		if (literalList.size() < 2) return;
		
		Literal[] literalArray = new Literal[literalList.size()];
		int index = 0;
		for (Literal literal : literalList) {
			if (index == 0) {
				literalArray[index] = literal;
				index++;
			} else {
				int insertIndex = index;
				while (insertIndex > 0) {
					if (literalArray[insertIndex-1].variable > literal.variable) {
						literalArray[insertIndex] = literalArray[insertIndex-1];
						insertIndex--;
					} else break;
				}
				literalArray[insertIndex] = literal;
				index++;
			}
		}
		literalList.clear();
		for (int i = 0; i < literalArray.length; i++) {
			literalList.add(literalArray[i]);
		}
	}
	
	/**
	 * Given a variable set, if this simple disjunctive formula is a max-term, then return its code in binary. If it is a tautology return an array with zero length. 
	 * If it is a contradiction, return an array with all elements -1. If it is not a max-term, return an array with some elements -1 for those variables not in this
	 * disjunctive formula.
	 */
	public int[] getMaxTermBinaryCode(char[] variableSet) {
		int[] codeArray = new int[variableSet.length];
		for (int i = 0; i < codeArray.length; i++) codeArray[i] = -1;
		if (isContradiction()) return codeArray;
		
		for (int i = 0; i < variableSet.length; i++) {
			for (Literal literal : literalList) {
				if (literal.variable == variableSet[i]) {
					if (codeArray[i] >= 0) {
						// this simple disjunctive formula is a tautology
						if ((codeArray[i] == 0) && literal.isNegative()) return new int[0];
						if ((codeArray[i] == 1) && literal.isPositive()) return new int[0];
					}
					if (literal.isPositive()) codeArray[i] = 0;
					else codeArray[i] = 1;
					break;
				}
			}
		}
		return codeArray;
	}
	
	public String toSetString() {
		if (literalList == null) return "{ }";
		if (literalList.size() == 0) return "{ }";
	
		sortLiterals();
		StringBuffer result = new StringBuffer();
		result.append("{ ");
		boolean first = true;
		for (Literal literal : literalList) {
			if (first) {
				result.append(literal.toString());
				first = false;
			} else result.append(", " + literal.toString());
		}
		result.append(" }");
		return result.toString();
	}

	public String toSetLaTeXString() {
		if (literalList == null) return "{ }";
		if (literalList.size() == 0) return "{ }";

		sortLiterals();
		StringBuffer result = new StringBuffer();
		result.append("{ ");
		boolean first = true;
		for (Literal literal : literalList) {
			if (first) {
				result.append(literal.toLaTeXString());
				first = false;
			} else result.append(", " + literal.toLaTeXString());
		}
		result.append(" }");
		return result.toString();
	}
	
	/**
	 * Convert a formula to a list of literal. If the formula is a simple disjunctive formula, then return true and the list in the given resultList, 
	 * otherwise return null
	 */
	protected static boolean convertToLiteralList(Formula formula, List<Literal> resultList) {
		boolean success = true;
		if (Literal.isLiteral(formula)) {
			success = resultList.add(Literal.convert(formula));
			return success;
		}
		if (formula.getOperator() != Symbol.OPERATOR_OR) return false;
		success = convertToLiteralList(formula.getLeft(), resultList);
		if (success == false) return false;
		success = convertToLiteralList(formula.getRight(), resultList);
		if (success == false) return false;
		return success;
	}
	
	/**
	 * Convert the literals in the list from the index of start(included) to the index of end(excluded) to a formula 
	 */
	protected Formula toFormula(List<Literal> list, int start, int end) {
		if (start == end-1) {
			Literal literal = list.get(start);
			return literal.toFormula();
		} else {
			int mid = (end - start)/2+start;
			Formula left = toFormula(list, start, mid);
			Formula right = toFormula(list, mid, end);
			Formula formula = new OrFormula(left, right);
			return formula;
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if (literalList == null) return prime * result;
		
		for (Literal literal : literalList) {
			result = prime * result + literal.hashCode();
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		SimpleDisjunctiveFormula other = (SimpleDisjunctiveFormula) obj;
		if (isContradiction()) {
			if (other.isContradiction()) return true;
			else return false;
		} else if (other.isContradiction()) {
			if (isContradiction()) return true;
			else return false;
		}
		for (Literal literal : literalList) {
			if (!other.literalList.contains(literal)) return false;
		}
		for (Literal literal : other.literalList) {
			if (!literalList.contains(literal)) return false;
		}
		return true;
	}
	
	protected SimpleDisjunctiveFormula() {
	}
	
	protected SimpleDisjunctiveFormula(Literal literal) {
		literalList = new ArrayList<Literal>();
		literalList.add(literal);
	}

	protected SimpleDisjunctiveFormula(List<Literal> list) {
		literalList = new ArrayList<Literal>();
		literalList.addAll(list);
	}
	
	protected boolean addLiteralAllowedDuplication(Literal literal) {
		if (literalList == null) literalList = new ArrayList<Literal>();
		return literalList.add(literal);
	}
	
	protected boolean addLiteral(Literal literal) {
		if (literalList == null) {
			literalList = new ArrayList<Literal>();
			return literalList.add(literal);
		} 
		if (literalList.contains(literal)) return false;
		return literalList.add(literal);
	}
}
