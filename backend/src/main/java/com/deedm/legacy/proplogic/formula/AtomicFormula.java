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
public class AtomicFormula extends Formula {

	public AtomicFormula(char variable) {
		super(variable, null, null);
	}
	public Formula getACopy() {
		Formula result = new AtomicFormula(operator);
		return result;
	}

	public Formula getSubstitutionResult(List<Substitution> substitutionList){
		for (Substitution substitution : substitutionList) {
			if (operator == substitution.getVariable()) {
				Formula result = substitution.getFormula().getACopy();
				return result;
			}
		}
		return getACopy();
	}
	
	@Override
	public Formula getReplaceResult(Formula subformula, Formula otherformula) {
		if (syntaxEqualTo(subformula)) return otherformula.getACopy();
		return getACopy();
	}

	@Override
	public boolean isAtomicFormula() {
		return true;
	}
	
	@Override
	public String toLaTeXString() {
		return "" + operator;
	}

	@Override
	public String toSimpleLaTeXString() {
		return "" + operator;
	}
	
	@Override
	public boolean getTruthValue(List<TruthAssignment> function) {
		for (TruthAssignment assignment : function) {
			if (assignment.getVariable() == operator) return assignment.getValue();
		}
		return false;
	}
	
	@Override
	public boolean isLimitedToNegAndOrOperators() {
		return true;
	}

	@Override
	public Formula getDulaFormula() {
		return getACopy();
	}
	
}
