/**
 * 
 */
package com.deedm.legacy.proplogic.normalFormula;

import com.deedm.legacy.proplogic.formula.Formula;
import com.deedm.legacy.proplogic.formula.Symbol;

/**
 * @author Zhou Xiaocong
 * @Since 2023/06/23
 *
 */
public class ExpandNFStep {
	private Formula formula = null;
	private int[] formulaBinaryCode = null;
	private int[] resultCodes = null;

	public ExpandNFStep(Formula formula, int[] formulaBinaryCode, int[] resultCodes) {
		this.formula = formula;
		this.formulaBinaryCode = formulaBinaryCode;
		this.resultCodes = resultCodes;
	}

	public Formula getFormula() {
		return formula;
	}

	public int[] getFormulaBinaryCode() {
		return formulaBinaryCode;
	}

	public int[] getResultCodes() {
		return resultCodes;
	}
	
	public String getFormulaBinaryCodeString() {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < formulaBinaryCode.length; i++) {
			if (formulaBinaryCode[i] == -1) buffer.append("-");
			else buffer.append(formulaBinaryCode[i]);
		}
		return buffer.toString();
	}
	
	public String getResultCodesNamedLaTeXString(int type) {
		char typeChar = 'm';
		if (type == ExpandNFRecorder.TYPE_DNF) {
			if (formulaBinaryCode.length == 0) return "0";
		}
		if (type == ExpandNFRecorder.TYPE_CNF) {
			if (formulaBinaryCode.length == 0) return "1";
			typeChar = 'M';
		}

		StringBuffer buffer = new StringBuffer();
		buffer.append(typeChar+ "_{" + resultCodes[0] + "}");
		for (int i = 1; i < resultCodes.length; i++) buffer.append(" " + Symbol.LATEX_OR + " " + typeChar + "_{" + resultCodes[i] + "}");
		return buffer.toString();
	}
}

