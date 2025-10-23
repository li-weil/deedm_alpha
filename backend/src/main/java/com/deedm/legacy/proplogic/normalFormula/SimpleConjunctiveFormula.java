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
import com.deedm.legacy.proplogic.formula.AtomicFormula;

/**
 * @author zxc
 * If a simple conjunctive formula has empty literal list, then it is a tautology, whereas, if it has a literal and its negation 
 * simultaneously, it is a contradiction.  
 *
 */
public class SimpleConjunctiveFormula {
	List<Literal> literalList = null;
	
	/**
	 * Convert a formula to a simple Conjunctive formula. If the formula is a simple Conjunctive formula, then return the result, 
	 * otherwise return null
	 */
	public static SimpleConjunctiveFormula convert(Formula formula) {
		List<Literal> resultList = new ArrayList<Literal>(); 
		
		if (convertToLiteralList(formula, resultList) == true) {
			SimpleConjunctiveFormula result = new SimpleConjunctiveFormula(resultList);
			return result;
		}
		return null;
	}
	
	/**
	 * Convert a formula to a simple Conjunctive formula and simplify it. If the formula is a simple Conjunctive formula, then return
	 * the result, otherwise return null. Here to simplify means remove the duplicate literal in the formula.
	 */
	public static SimpleConjunctiveFormula convertAndSimplify(Formula formula) {
		List<Literal> resultList = new ArrayList<Literal>(); 
		
		if (convertToLiteralList(formula, resultList) == true) {
			SimpleConjunctiveFormula result = new SimpleConjunctiveFormula();
			// addLiteral() only add those literals which are not in the list yet.
			for (Literal literal : resultList) result.addLiteral(literal);  
			return result;
		}
		return null;
	}

	/**
	 * Convert this simple Conjunctive formula to an object of the class Formula.
	 */
	public Formula toFormula() {
		if (isTautology()) return new OrFormula(new NegFormula(new AtomicFormula('p')), new AtomicFormula('p'));
		return toFormula(literalList, 0, literalList.size());
	}
	
	public static boolean isSimpleConjunctiveFormula(Formula formula) {
		if (Literal.isLiteral(formula)) return true;
		if (!formula.isAndFormula()) return false;
		Formula left = formula.getLeft();
		if (!isSimpleConjunctiveFormula(left)) return false; 
		Formula right = formula.getRight();
		if (!isSimpleConjunctiveFormula(right)) return false;
		return true;
	}
	
	public boolean isTautology() {
		if (literalList == null) return true;
		if (literalList.size() == 0) return true;
		return false;
	}
	
	public boolean isContradiction() {
		if (literalList == null) return false;
		if (literalList.size() == 0) return false;
		for (Literal literal : literalList) {
			for (Literal other : literalList) {
				if (literal.isNegativeTo(other)) return true;
			}
		}
		return false;
	}
	
	public boolean isMinTerm(char[] variableSet) {
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
	 * Remove the duplicated literal and return a new copy of the formula. 
	 */
	public SimpleConjunctiveFormula simplify() {
		SimpleConjunctiveFormula formula = new SimpleConjunctiveFormula();
		for (Literal literal : literalList) formula.addLiteral(literal);
		return formula;
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
	 * Given a variable set, if this simple conjunctive formula is a min-term, then return its code in binary. If it is a contradiction return an array with zero length. 
	 * If it is a tautology, return an array with all elements -1. If it is not a min-term, return an array with some elements -1 for those variables not in this
	 * conjunctive formula.
	 */
	public int[] getMinTermBinaryCode(char[] variableSet) {
		int[] codeArray = new int[variableSet.length];
		for (int i = 0; i < codeArray.length; i++) codeArray[i] = -1;
		if (isTautology()) return codeArray;
		
		for (int i = 0; i < variableSet.length; i++) {
			for (Literal literal : literalList) {
				if (literal.variable == variableSet[i]) {
					if (codeArray[i] >= 0) {
						// this simple disjunctive formula is a contradiction
						if (codeArray[i] == 0 && literal.isPositive()) return new int[0];
						if (codeArray[i] == 1 && literal.isNegative()) return new int[0];
					}
					if (literal.isPositive()) codeArray[i] = 1;
					else codeArray[i] = 0;
					break;
				}
			}
		}
		return codeArray;
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
		SimpleConjunctiveFormula other = (SimpleConjunctiveFormula) obj;
		if (isTautology()) {
			if (other.isTautology()) return true;
			else return false;
		} else if (other.isTautology()) {
			if (isTautology()) return true;
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
			Formula formula = new AndFormula(left, right);
			return formula;
		}
	}
	
	protected static boolean convertToLiteralList(Formula formula, List<Literal> resultList) {
		boolean success = true;
		if (Literal.isLiteral(formula)) {
			success = resultList.add(Literal.convert(formula));
			return success;
		}
		if (!formula.isAndFormula()) return false;
		success = convertToLiteralList(formula.getLeft(), resultList);
		if (success == false) return false;
		success = convertToLiteralList(formula.getRight(), resultList);
		if (success == false) return false;
		return success;
	}
	
	protected SimpleConjunctiveFormula() {
	}
	
	protected SimpleConjunctiveFormula(Literal literal) {
		literalList = new ArrayList<Literal>();
		literalList.add(literal);
	}

	protected SimpleConjunctiveFormula(List<Literal> list) {
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
