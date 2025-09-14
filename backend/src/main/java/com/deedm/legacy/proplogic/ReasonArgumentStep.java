package proplogic.reason;

import proplogic.formula.Formula;

/**
 * @author Zhou Xiaocong
 * @Since 2023/06/23
 * 
 */
public class ReasonArgumentStep {
	private int serialNo = 0;
	private Formula formula = null;
	private int[] previousSN = null;
	private LogicReasoningRuleName ruleName = null;

	public ReasonArgumentStep(int serialNo, Formula formula, int[] previousSN, LogicReasoningRuleName ruleName) {
		this.serialNo = serialNo;
		this.formula = formula;
		this.previousSN = previousSN;
		this.ruleName = ruleName;
	}

	public int getSerialNo() {
		return serialNo;
	}

	public Formula getFormula() {
		return formula;
	}

	public int[] getPreviousSN() {
		return previousSN;
	}

	public boolean isPremiseStep() {
		return ruleName == LogicReasoningRuleName.Premise;
	}
	
	public boolean isAdditionalPremiseStep() {
		return ruleName == LogicReasoningRuleName.AddPremise;
	}
	
	public boolean isProofByAdditionalPremiseStep() {
		return ruleName == LogicReasoningRuleName.APMethod;
	}
	
	public boolean isProofByContradictionStep() {
		return ruleName == LogicReasoningRuleName.Contradict;
	}

	public LogicReasoningRuleName getRuleName() {
		return ruleName;
	}
}
