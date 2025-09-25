/**
 * 
 */
package proplogic.equiv;

import java.util.List;

import com.deedm.legacy.proplogic.FormulaTruthTable;
import com.deedm.legacy.proplogic.TruthAssignmentFunction;
import com.deedm.legacy.proplogic.formula.BiImpFormula;
import com.deedm.legacy.proplogic.formula.Formula;

/**
 * @author Zhou Xiaocong
 * @Since 2023/06/23
 *
 */
public class EquivCalculusChecker {
	private EquivCalculusRecorder calculusRecorder = null;
	private EquivCalculusStep errorStep = null;
	private TruthAssignmentFunction counterExample = null;
	private Formula checkingFormula = null;
	
	public EquivCalculusChecker(EquivCalculusRecorder calculusRecorder) {
		this.calculusRecorder = calculusRecorder; 
	}

	public boolean checkByUsingTruthTable() {
		List<EquivCalculusStep> stepList = calculusRecorder.getStepList();
		
		Formula prevFormula = null;
		for (EquivCalculusStep step : stepList) {
			if (prevFormula == null) prevFormula = step.getFormula();
			else {
				Formula currFormula = step.getFormula();
				checkingFormula = new BiImpFormula(prevFormula, currFormula);
				FormulaTruthTable truthTable = new FormulaTruthTable(checkingFormula);
				counterExample = truthTable.getFirstFalseAssignment();
				if (counterExample != null) {
					errorStep = step;
					return false;
				}
			}
		}
		return true;
	}

	public EquivCalculusRecorder getCalculusRecorder() {
		return calculusRecorder;
	}

	public EquivCalculusStep getErrorStep() {
		return errorStep;
	}

	public TruthAssignmentFunction getCounterExample() {
		return counterExample;
	}

	public Formula getCheckingFormula() {
		return checkingFormula;
	}
}
