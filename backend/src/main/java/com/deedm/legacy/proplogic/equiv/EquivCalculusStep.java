/**
 * 
 */
package com.deedm.legacy.proplogic.equiv;

import com.deedm.legacy.proplogic.formula.Formula;

/**
 * @author Zhou Xiaocong
 * @since 2023/06/23
 *
 */
public class EquivCalculusStep {
	private Formula formula = null;
	private String comments = null;

	public EquivCalculusStep(Formula formula, String comments) {
		this.formula = formula;
		this.comments = comments;
	}

	public Formula getFormula() {
		return formula;
	}

	public String getComments() {
		return comments;
	}
}
