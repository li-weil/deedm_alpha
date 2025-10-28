/**
 * 
 */
package com.deedm.legacy.proplogic.reason;

import java.util.ArrayList;
import java.util.List;

import com.deedm.legacy.proplogic.FormulaTruthTable;
import com.deedm.legacy.proplogic.TruthAssignmentFunction;
import com.deedm.legacy.proplogic.formula.AndFormula;
import com.deedm.legacy.proplogic.formula.Formula;
import com.deedm.legacy.proplogic.formula.ImpFormula;

/**
 * @author Zhou Xiaocong
 *
 */
public class ReasonArgumentChecker {
	public static final int ERROR_TYPE_FALSE = 1;
	public static final int ERROR_TYPE_CONSEQUENT = 2;
	public static final int ERROR_TYPE_ADDPREMISE = 3;
	public static final int ERROR_TYPE_PREMISE = 4;
	
	private ReasonArgumentRecorder argumentRecorder = null;
	private ReasonArgumentStep errorStep = null;
	private TruthAssignmentFunction counterExample = null;
	private Formula checkingFormula = null;
	private List<ReasonArgumentCheckStep> checkStepList = null;
	private int errorType = 1;
	
	public ReasonArgumentChecker(ReasonArgumentRecorder argumentRecorder) {
		this.argumentRecorder = argumentRecorder;
	}

	public int getErrorType() {
		return errorType;
	}
	
	public boolean checkByUsingTruthTable() {
		Formula premiseFormula = argumentRecorder.getFormulaForAllReasoningPremises();
		List<Formula> extraPremiseList = new ArrayList<Formula>();
		checkStepList = new ArrayList<ReasonArgumentCheckStep>();
		for (ReasonArgumentStep step : argumentRecorder.getStepList()) {
			if (step.isPremiseStep()) {
				checkingFormula = step.getFormula();
				checkStepList.add(new ReasonArgumentCheckStep(step, checkingFormula));
				if (!argumentRecorder.isReasoningPremise(checkingFormula)) {
					errorStep = step;  errorType = ERROR_TYPE_PREMISE;
					return false;
				}
			} else if (step.isAdditionalPremiseStep()) {
				checkingFormula = step.getFormula();
				checkStepList.add(new ReasonArgumentCheckStep(step, checkingFormula));
				extraPremiseList.add(checkingFormula);
			} else {
				if (step.isProofByContradictionStep() || step.isProofByAdditionalPremiseStep()) {
					Formula additionalPremise = argumentRecorder.getReferredAdditionalPremise(step);
					if (additionalPremise != null) extraPremiseList.remove(additionalPremise);
				}
				Formula allPremiseFormula = getAllPremiseFormula(premiseFormula, extraPremiseList);
				if (allPremiseFormula != null) checkingFormula = new ImpFormula(allPremiseFormula, step.getFormula());
				else checkingFormula = step.getFormula();
				checkStepList.add(new ReasonArgumentCheckStep(step, checkingFormula));
				FormulaTruthTable truthTable = new FormulaTruthTable(checkingFormula);
				counterExample = truthTable.getFirstFalseAssignment();
				if (counterExample != null) {
					errorStep = step;  errorType = ERROR_TYPE_FALSE;
					return false;
				}
			}
		}
		if (!argumentRecorder.isCorrectLastStep()) {
			errorStep = argumentRecorder.getLastStep();
			checkingFormula = errorStep.getFormula();
			errorType = ERROR_TYPE_CONSEQUENT;
			return false;
		} else if (extraPremiseList.size() > 0) {
			errorStep = argumentRecorder.getLastStep();
			checkingFormula = extraPremiseList.get(0);
			errorType = ERROR_TYPE_ADDPREMISE;
			return false;
		}
		return true;
	}
	
	public ReasonArgumentRecorder getArgumentRecorder() {
		return argumentRecorder;
	}

	public ReasonArgumentStep getErrorStep() {
		return errorStep;
	}

	public TruthAssignmentFunction getCounterExample() {
		return counterExample;
	}

	public Formula getCheckingFormula() {
		return checkingFormula;
	}
	
	public List<ReasonArgumentCheckStep> getCheckStepList() {
		return checkStepList;
	}
	
	private Formula getAllPremiseFormula(Formula premiseFormula, List<Formula> AdditionalPremiseList) {
		Formula result = null;
		for (Formula formula : AdditionalPremiseList) {
			if (result == null) result = formula;
			else result = new AndFormula(result, formula);
		}
		if (result != null) {
			if (premiseFormula != null) return new AndFormula(premiseFormula, result);
			else return result;
		} else return premiseFormula;
	}
}
