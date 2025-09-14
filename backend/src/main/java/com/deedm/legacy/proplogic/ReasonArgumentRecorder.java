/**
 * 
 */
package proplogic.reason;

import java.util.ArrayList;
import java.util.List;

import proplogic.formula.Formula;

/**
 * @author Zhou Xiaocong
 * @Since 2023/06/23
 *
 */
public class ReasonArgumentRecorder {
	private LogicReasoning reasoning = null;
	private List<ReasonArgumentStep> stepList = null;

	public ReasonArgumentRecorder(LogicReasoning reasoning, List<ReasonArgumentStep> stepList) {
		this.reasoning = reasoning;
		this.stepList = stepList;
	}

	public LogicReasoning getReasoning() {
		return reasoning;
	}

	public List<ReasonArgumentStep> getStepList() {
		return stepList;
	}
	
	public boolean isReasoningPremise(Formula formula) {
		return reasoning.isPremise(formula);
	}
	
	public Formula getReasoningConsequent() {
		return reasoning.getConsequent();
	}
	
	public ReasonArgumentStep getLastStep() {
		return stepList.get(stepList.size()-1);
	}
	
	public boolean isCorrectLastStep() {
		Formula lastFormula = getLastStep().getFormula();
		Formula consequent = reasoning.getConsequent();
		if (lastFormula.syntaxEqualTo(consequent)) return true;
		else return false;
	}
	
	public Formula getFormulaForAllReasoningPremises() {
		return reasoning.getConjunctiveFormulaForAllPremises();
	}
	
	public Formula getReferredAdditionalPremise(ReasonArgumentStep step) {
		int[] previousSN = step.getPreviousSN();
		for (int i = 0; i < previousSN.length; i++) {
			for (ReasonArgumentStep prevStep : stepList) {
				if (prevStep.getSerialNo() == previousSN[i] && prevStep.isAdditionalPremiseStep()) {
					return prevStep.getFormula();
				}
			}
		}
		return null;
	}
	
	public List<Formula> getReferredFormula(ReasonArgumentStep step) {
		List<Formula> result = new ArrayList<Formula>();
		int[] previousSN = step.getPreviousSN();
		for (int i = 0; i < previousSN.length; i++) {
			for (ReasonArgumentStep prevStep : stepList) {
				if (prevStep.getSerialNo() == previousSN[i]) {
					result.add(step.getFormula());
					break;
				}
			}
		}
		return result;
	}
}
