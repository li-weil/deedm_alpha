package proplogic.reason;

import proplogic.formula.Formula;

public class ReasonArgumentCheckStep {
	private ReasonArgumentStep reasonStep = null;
	private Formula checkFormula = null;
	
	public ReasonArgumentCheckStep(ReasonArgumentStep reasonStep, Formula checkFormula) {
		this.reasonStep = reasonStep;
		this.checkFormula = checkFormula;
	}

	public ReasonArgumentStep getReasonStep() {
		return reasonStep;
	}

	public Formula getCheckFormula() {
		return checkFormula;
	}
}
