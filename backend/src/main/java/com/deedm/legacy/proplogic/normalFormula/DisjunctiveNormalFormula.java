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
 * 
 * If a disjunctive normal formula has empty simple conjunctive formula list, then it is a contradiction, whereas, if it has 
 * a simple conjunctive formula which has no literals, it is a tautology. 
 */
public class DisjunctiveNormalFormula {
	protected List<SimpleConjunctiveFormula> conjformList = null;
	
	/**
	 * Convert a formula to a disjunctive normal formula. If the formula is a disjunctive normal formula, then return the result, 
	 * otherwise return null
	 */
	public static DisjunctiveNormalFormula convert(Formula formula) {
		List<SimpleConjunctiveFormula> resultList = new ArrayList<SimpleConjunctiveFormula>(); 
		
		if (convertToSimpleConjunctiveFormulaList(formula, resultList) == true) {
			DisjunctiveNormalFormula result = new DisjunctiveNormalFormula(resultList);
			return result;
		}
		return null;
	}
	
	/**
	 * Convert a formula to a disjunctive normal formula and simplify it. If the formula is a disjunctive normal formula, then return
	 * the result, otherwise return null. Here to simplify means remove the duplicate simple conjunctive formula and contradictions in the formula.
	 */
	public static DisjunctiveNormalFormula convertAndSimplify(Formula formula) {
		List<SimpleConjunctiveFormula> resultList = new ArrayList<SimpleConjunctiveFormula>(); 
		
		if (convertToSimpleConjunctiveFormulaListAndSimplify(formula, resultList) == true) {
			DisjunctiveNormalFormula result = new DisjunctiveNormalFormula();
			// addSimpleConjunctiveFormula() only add those SimpleConjunctiveFormula which are not in the list yet.
			for (SimpleConjunctiveFormula conjform : resultList) {
				if (conjform.isContradiction() == false) result.addSimpleConjunctiveFormula(conjform);  
			}
			return result;
		}
		return null;
	}

	public static boolean isDisjunctiveNormalFormula(Formula formula) {
		if (SimpleConjunctiveFormula.isSimpleConjunctiveFormula(formula)) return true;
		
		if (!formula.isOrFormula()) return false;
		if (!isDisjunctiveNormalFormula(formula.getLeft())) return false;
		if (!isDisjunctiveNormalFormula(formula.getRight())) return false;
		return true;
	}

	/**
	 * Convert this disjunctive normal formula to an object of the class Formula.
	 */
	public Formula toFormula() {
		if (isContradiction()) return new AndFormula(new NegFormula(new AtomicFormula('p')), new AtomicFormula('p'));
		return toFormula(conjformList, 0, conjformList.size());
	}
	
	public boolean isContradiction() {
		if (conjformList == null) return true;
		if (conjformList.size() == 0) return true;
		for (SimpleConjunctiveFormula conjunctiveFormula : conjformList) {
			if (conjunctiveFormula.isContradiction() == false) return false;
		}
		return true;
	}
	
	/**
	 * Remove the duplicated literal in its simple conjunctive formula, and remove duplicated simple conjunctive formula, and remove 
	 * those simple conjunctive formula which is a contradiction. Do not change this disjunctive normal formula, but return a simplified copy. 
	 */
	public DisjunctiveNormalFormula simplify() {
		DisjunctiveNormalFormula result = new DisjunctiveNormalFormula();
		if (isContradiction()) return result;
		
		for (SimpleConjunctiveFormula conjform : conjformList) {
			if (conjform.isContradiction() == false) result.addSimpleConjunctiveFormula(conjform.simplify());  
		}
		return result;
	}

	/**
	 * Convert a formula to a list of simple conjunctive formula and simplify it. If the formula is a disjunctive normal formula, then return true,
	 * otherwise return false. Here to simplify means remove the duplicate literals in a simple conjunctive formula.
	 */
	protected static boolean convertToSimpleConjunctiveFormulaListAndSimplify(Formula formula, List<SimpleConjunctiveFormula> resultList) {
		boolean success = true;
		if (SimpleConjunctiveFormula.isSimpleConjunctiveFormula(formula)) {
			success = resultList.add(SimpleConjunctiveFormula.convertAndSimplify(formula));
			return success;
		}
		if (!formula.isOrFormula()) return false;
		success = convertToSimpleConjunctiveFormulaListAndSimplify(formula.getLeft(), resultList);
		if (success == false) return false;
		success = convertToSimpleConjunctiveFormulaListAndSimplify(formula.getRight(), resultList);
		if (success == false) return false;
		return success;
	}
	
	/**
	 * Convert a formula to a list of simple conjunctive formula. If the formula is a disjunctive normal formula, then return true,
	 * otherwise return false. 
	 */
	protected static boolean convertToSimpleConjunctiveFormulaList(Formula formula, List<SimpleConjunctiveFormula> resultList) {
		boolean success = true;
		if (SimpleConjunctiveFormula.isSimpleConjunctiveFormula(formula)) {
			success = resultList.add(SimpleConjunctiveFormula.convert(formula));
			return success;
		}
		if (!formula.isOrFormula()) return false;
		success = convertToSimpleConjunctiveFormulaList(formula.getLeft(), resultList);
		if (success == false) return false;
		success = convertToSimpleConjunctiveFormulaList(formula.getRight(), resultList);
		if (success == false) return false;
		return success;
	}
	
	/**
	 * Convert the simple conjunctive formulas in the list from the index of start(included) to the index of end(excluded) to a formula 
	 */
	protected Formula toFormula(List<SimpleConjunctiveFormula> list, int start, int end) {
		if (start == end-1) {
			SimpleConjunctiveFormula conjform = list.get(start);
			return conjform.toFormula();
		}
		
		int mid = (end - start)/2+start;
		Formula left = toFormula(list, start, mid);
		Formula right = toFormula(list, mid, end);
		Formula formula = new OrFormula(left, right);
		return formula;
	}
	
	protected DisjunctiveNormalFormula() {
	}
	
	protected DisjunctiveNormalFormula(SimpleConjunctiveFormula conjform) {
		conjformList = new ArrayList<SimpleConjunctiveFormula>();
		conjformList.add(conjform);
	}

	protected DisjunctiveNormalFormula(List<SimpleConjunctiveFormula> list) {
		conjformList = new ArrayList<SimpleConjunctiveFormula>();
		conjformList.addAll(list);
	}
	
	protected boolean addSimpleConjunctiveFormulaAllowedDuplication(SimpleConjunctiveFormula conjform) {
		if (conjformList == null) conjformList = new ArrayList<SimpleConjunctiveFormula>();
		return conjformList.add(conjform);
	}
	
	protected boolean addSimpleConjunctiveFormula(SimpleConjunctiveFormula conjform) {
		if (conjformList == null) {
			conjformList = new ArrayList<SimpleConjunctiveFormula>();
			return conjformList.add(conjform);
		} 
		if (conjformList.contains(conjform)) return false;
		return conjformList.add(conjform);
	}
}
