/**
 * 
 */
package com.deedm.legacy.proplogic.normalFormula;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zhou Xiaocong
 * @Since 2023/06/23
 *
 */
public class ExpandNFRecorder {
	public static final int TYPE_CNF = 0;
	public static final int TYPE_DNF = 1;
	
	private List<ExpandNFStep> stepList = null;
	private int type = TYPE_CNF;

	public ExpandNFRecorder(int type) {
		this.stepList = new ArrayList<ExpandNFStep>();
		this.type = type;
	}

	public List<ExpandNFStep> getStepList() {
		return stepList;
	}

	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}

	public boolean addStep(ExpandNFStep step) {
		return stepList.add(step);
	}
	
	public void clearStep() {
		stepList.clear();
	}
}
