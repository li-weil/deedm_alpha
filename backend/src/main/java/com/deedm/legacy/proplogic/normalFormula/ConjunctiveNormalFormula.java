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
 * If a conjunctive normal formula has empty simple disjunctive formula list, then it is a tautology, whereas, if it has an empty clause (i.e.
 * a simple disjunctive formula which has no literals), it is a contradiction. 
 */
public class ConjunctiveNormalFormula {
	protected List<SimpleDisjunctiveFormula> disjformList = null;
	
	/**
	 * Convert a formula to a conjunctive normal formula. If the formula is a conjunctive normal formula, then return the result, 
	 * otherwise return null
	 */
	public static ConjunctiveNormalFormula convert(Formula formula) {
		List<SimpleDisjunctiveFormula> resultList = new ArrayList<SimpleDisjunctiveFormula>(); 
		
		if (convertToSimpleDisjunctiveFormulaList(formula, resultList) == true) {
			ConjunctiveNormalFormula result = new ConjunctiveNormalFormula(resultList);
			return result;
		}
		return null;
	}
	
	/**
	 * Convert a formula to a conjunctive normal formula and simplify it. If the formula is a conjunctive normal formula, then return
	 * the result, otherwise return null. Here to simplify means remove the duplicate simple disjunctive formula and tautologies in the formula.
	 */
	public static ConjunctiveNormalFormula convertAndSimplify(Formula formula) {
		List<SimpleDisjunctiveFormula> resultList = new ArrayList<SimpleDisjunctiveFormula>(); 
		
		if (convertToSimpleDisjunctiveFormulaListAndSimplify(formula, resultList) == true) {
			ConjunctiveNormalFormula result = new ConjunctiveNormalFormula();
			// addSimpleConjunctiveFormula() only add those SimpleConjunctiveFormula which are not in the list yet.
			for (SimpleDisjunctiveFormula disjform : resultList) { 
				if (disjform.isTautology() == false) result.addSimpleDisjunctiveFormula(disjform);  
			}
			return result;
		}
		return null;
	}

	public static boolean isConjunctiveNormalFormula(Formula formula) {
		if (SimpleDisjunctiveFormula.isSimpleDisjunctiveFormula(formula)) return true;
		
		if (!formula.isAndFormula()) return false;
		if (!isConjunctiveNormalFormula(formula.getLeft())) return false;
		if (!isConjunctiveNormalFormula(formula.getRight())) return false;
		return true;
	}

	/**
	 * Convert this conjunctive normal formula to an object of the class Formula.
	 */
	public Formula toFormula() {
		if (isTautology()) return new OrFormula(new NegFormula(new AtomicFormula('p')), new AtomicFormula('p'));
		return toFormula(disjformList, 0, disjformList.size());
	}

	public boolean isTautology() {
		if (disjformList == null) return true;
		if (disjformList.size() == 0) return true;
		for (SimpleDisjunctiveFormula disjunctiveFormula : disjformList) {
			if (disjunctiveFormula.isTautology() == false) return false;
		}
		return true;
	}
	
	/**
	 * Remove the duplicated literal in its simple disjunctive formula, and remove duplicated simple disjunctive formula, and remove 
	 * those simple disjunctive formula which is a tautology. Do not change this conjunctive normal formula, but return a simplified copy. 
	 */
	public ConjunctiveNormalFormula simplify() {
		ConjunctiveNormalFormula result = new ConjunctiveNormalFormula();
		if (isTautology()) return result;
		
		for (SimpleDisjunctiveFormula disjform : disjformList) {
			if (disjform.isTautology() == false) result.addSimpleDisjunctiveFormula(disjform.simplify());  
		}
		return result;
	}

	/**
	 * Convert a formula to a list of simple disjunctive formula and simplify it. If the formula is a conjunctive normal formula, then return true,
	 * otherwise return false. Here to simplify means remove the duplicate literals in a simple disjunctive formula.
	 */
	protected static boolean convertToSimpleDisjunctiveFormulaListAndSimplify(Formula formula, List<SimpleDisjunctiveFormula> resultList) {
		boolean success = true;
		if (SimpleDisjunctiveFormula.isSimpleDisjunctiveFormula(formula)) {
			success = resultList.add(SimpleDisjunctiveFormula.convertAndSimplify(formula));
			return success;
		}
		if (!formula.isAndFormula()) return false;
		success = convertToSimpleDisjunctiveFormulaListAndSimplify(formula.getLeft(), resultList);
		if (success == false) return false;
		success = convertToSimpleDisjunctiveFormulaListAndSimplify(formula.getRight(), resultList);
		if (success == false) return false;
		return success;
	}

	/**
	 * Convert a formula to a list of simple disjunctive formula. If the formula is a conjunctive normal formula, then return true,
	 * otherwise return false. 
	 */
	protected static boolean convertToSimpleDisjunctiveFormulaList(Formula formula, List<SimpleDisjunctiveFormula> resultList) {
		boolean success = true;
		if (SimpleDisjunctiveFormula.isSimpleDisjunctiveFormula(formula)) {
			success = resultList.add(SimpleDisjunctiveFormula.convert(formula));
			return success;
		}
		if (!formula.isAndFormula()) return false;
		success = convertToSimpleDisjunctiveFormulaList(formula.getLeft(), resultList);
		if (success == false) return false;
		success = convertToSimpleDisjunctiveFormulaList(formula.getRight(), resultList);
		if (success == false) return false;
		return success;
	}
	
	/**
	 * Convert the simple conjunctive formulas in the list from the index of start(included) to the index of end(excluded) to a formula 
	 */
	protected Formula toFormula(List<SimpleDisjunctiveFormula> list, int start, int end) {
		if (start == end-1) {
			SimpleDisjunctiveFormula conjform = list.get(start);
			return conjform.toFormula();
		}
		
		int mid = (end - start)/2+start;
		Formula left = toFormula(list, start, mid);
		Formula right = toFormula(list, mid, end);
		Formula formula = new AndFormula(left, right);
		return formula;
	}

	protected ConjunctiveNormalFormula() {
	}
	
	protected ConjunctiveNormalFormula(SimpleDisjunctiveFormula disjform) {
		disjformList = new ArrayList<SimpleDisjunctiveFormula>();
		disjformList.add(disjform);
	}

	protected ConjunctiveNormalFormula(List<SimpleDisjunctiveFormula> list) {
		disjformList = new ArrayList<SimpleDisjunctiveFormula>();
		disjformList.addAll(list);
	}
	
	protected boolean addSimpleDisjunctiveFormulaAllowedDuplication(SimpleDisjunctiveFormula disjform) {
		if (disjformList == null) disjformList = new ArrayList<SimpleDisjunctiveFormula>();
		return disjformList.add(disjform);
	}
	
	protected boolean addSimpleDisjunctiveFormula(SimpleDisjunctiveFormula disjform) {
		if (disjformList == null) {
			disjformList = new ArrayList<SimpleDisjunctiveFormula>();
			return disjformList.add(disjform);
		} 
		if (disjformList.contains(disjform)) return false;
		return disjformList.add(disjform);
	}

}
