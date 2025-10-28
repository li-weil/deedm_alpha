/**
 * 
 */
package com.deedm.legacy.proplogic.reason;

import java.util.List;

import com.deedm.legacy.proplogic.formula.AndFormula;
import com.deedm.legacy.proplogic.formula.Formula;

/**
 * @author Zhou Xiaocong
 * @Since 2023/06/23
 *
 */
public class LogicReasoning {
	private List<Formula> premiseList = null;
	private Formula consequent = null;
	
	public LogicReasoning(List<Formula> premiseList, Formula consequent) {
		this.premiseList = premiseList;
		this.consequent = consequent;
	}

	public List<Formula> getPremiseList() {
		return premiseList;
	}

	public Formula getConsequent() {
		return consequent;
	}
	
	public boolean isPremise(Formula checkingFormula) {
		boolean found = false;
		for (Formula formula : premiseList) {
			if (formula.syntaxEqualTo(checkingFormula)) {
				found = true;
				break;
			}
		}
		return found;
	}
	
	public Formula getConjunctiveFormulaForAllPremises() {
		Formula result = null;
		for (Formula formula : premiseList) {
			if (result == null) result = formula;
			else result = new AndFormula(result, formula);
		}
		return result;
	}
	
	public String toLaTeXString() {
		StringBuffer buffer = new StringBuffer();
		
		boolean isFirst = true;
		for (Formula formula : premiseList) {
			if (isFirst) {
				buffer.append(formula.toSimpleLaTeXString());
				isFirst = false;
			} else {
				buffer.append("," + formula.toSimpleLaTeXString());
			}
		}
		buffer.append("\\vdash ");
		buffer.append(consequent.toSimpleLaTeXString());
		return buffer.toString();
	}
}
