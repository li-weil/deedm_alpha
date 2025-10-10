/**
 * 
 */
package com.deedm.legacy.proplogic.equiv;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zhou Xiaocong
 * @since 2023/06/23
 *
 */
public class EquivCalculusRecorder {
	private List<EquivCalculusStep> stepList = null;
	
	public EquivCalculusRecorder() {
		stepList = new ArrayList<EquivCalculusStep>();
	}
	
	public List<EquivCalculusStep> getStepList() {
		return stepList;
	}
	
	public boolean addStep(EquivCalculusStep step) {
		return stepList.add(step);
	}
	
	public void clearStep() {
		stepList.clear();
	}
}
