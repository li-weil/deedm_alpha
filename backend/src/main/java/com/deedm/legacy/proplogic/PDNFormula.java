/**
 * 
 */
package proplogic.normalFormula;

import java.util.List;

import proplogic.formula.Formula;
import proplogic.formula.Symbol;

/**
 * @author zxc
 *
 * If a disjunctive normal formula has empty simple conjunctive formula list, then it is a contradiction, whereas, if it has 
 * a simple conjunctive formula which has no literals, it is a tautology. 
 */
public class PDNFormula extends DisjunctiveNormalFormula {
	protected char[] variableSet = null;
	
	protected PDNFormula(char[] variableSet) {
		super();
		this.variableSet = variableSet;
	}
	
	public char[] getVariableSet() {
		return variableSet;
	}

	/**
	 * Check if a principle disjunctive normal formula or not 
	 */
	public static boolean isPrincipleDisjunctiveNormalForm(Formula formula, char[] propVariableSet) {
		SimpleConjunctiveFormula conjform = SimpleConjunctiveFormula.convert(formula);
		if (conjform != null) {
			if (conjform.isMinTerm(propVariableSet)) return true;
		}
		
		if (!formula.isOrFormula()) return false;
		if (!isPrincipleDisjunctiveNormalForm(formula.getLeft(), propVariableSet)) return false;
		if (!isPrincipleDisjunctiveNormalForm(formula.getRight(), propVariableSet)) return false;
		return true;
	}

	/**
	 * If the formula is a principle disjunctive normal form when all variables contained in the array PropVariableSet, the return its all min-term codes
	 * otherwise return null. We assume this formula is not a contradiction.
	 */
	public int[] getAllCodesofMinTerm() {
		int[] result = new int[conjformList.size()];
		for (int i = 0; i < result.length; i++) result[i] = -1;
		int index = 0;
		for (SimpleConjunctiveFormula conjform: conjformList) {
			int[] codeArray = conjform.getMinTermBinaryCode(variableSet);
			int code = NormalFormulaCalculator.toInteger(codeArray);
			if (code < 0) return null;
			insertToCodeArray(code, result, index);
			index++;
		}
		return result;
	}
	
	/**
	 * If the formula is a principle disjunctive normal form when all variables contained in the array PropVariableSet, the return its all max-term codes.
	 * If it is a tautology, then return an array with zero length. 
	 * Therefore, by this method, the principle conjunctive normal form can be obtained by this principle disjunctive normal form. 
	 */
	public int[] getAllCodesofMaxTerm() {
		int powerof2 = 1;
		for (int i = 0; i < variableSet.length; i++) powerof2 = powerof2 * 2;
		
		if (isContradiction()) {
			int[] result = new int[powerof2];
			for (int i = 0; i < powerof2; i++) result[i] = i;
			return result;
		}

		int[] minCodeArray = getAllCodesofMinTerm();
		int[] result = new int[powerof2 - minCodeArray.length];
		for (int i = 0; i < result.length; i++) result[i] = -1;

		int code = 0, minCodeIndex = 0;
		int index = 0;
		while (code < powerof2) {
			if (minCodeIndex >= minCodeArray.length) {
				result[index] = code;
				index++;
			} else if (code < minCodeArray[minCodeIndex]) {
				result[index] = code;
				index++;
			} else minCodeIndex++;
			code++;
		}
		return result;
	}

	/**
	 * Return a string such as "m_0 | m_2" to represent this principle disjunctive normal formula.
	 */
	public String toNamedString() {
		if (isContradiction()) return "0";
		
		StringBuffer buffer = new StringBuffer();
		int[] codeArray = getAllCodesofMinTerm();
		buffer.append("m" + codeArray[0]);
		for (int i = 1; i < codeArray.length; i++) buffer.append(" " + Symbol.OPERATOR_OR + "m" + codeArray[i]);
		return buffer.toString();
	}
	
	/**
	 * Return a string such as "m_0 \vee m_2" to represent this principle disjunctive normal formula.
	 */
	public String toNamedLaTeXString() {
		if (isContradiction()) return "0";

		StringBuffer buffer = new StringBuffer();
		int[] codeArray = getAllCodesofMinTerm();
		buffer.append("m_{" + codeArray[0] + "}");
		for (int i = 1; i < codeArray.length; i++) buffer.append(" " + Symbol.LATEX_OR + " m_{" + codeArray[i] + "}");
		return buffer.toString();
	}

	/**
	 * Return a string such as "M_0 & M_2" to represent the principle conjunctive normal formula for this principle disjunctive normal formula.
	 */
	public String toNamedPCNFString() {
		StringBuffer buffer = new StringBuffer();
		int[] codeArray = getAllCodesofMaxTerm();
		if (codeArray == null || codeArray.length == 0) return "1";

		buffer.append("M" + codeArray[0]);
		for (int i = 1; i < codeArray.length; i++) buffer.append(" " + Symbol.OPERATOR_AND + "M" + codeArray[i]);
		return buffer.toString();
	}
	
	/**
	 * Return a string such as "M_0 \wedge M_2" to represent the principle conjunctive normal formula for this principle disjunctive normal formula.
	 */
	public String toNamedPCNFLaTeXString() {
		StringBuffer buffer = new StringBuffer();
		int[] codeArray = getAllCodesofMaxTerm();
		if (codeArray == null || codeArray.length == 0) return "1";

		buffer.append("M_{" + codeArray[0] + "}");
		for (int i = 1; i < codeArray.length; i++) buffer.append(" " + Symbol.LATEX_AND + " M_{" + codeArray[i] + "}");
		return buffer.toString();
	}
	
	/**
	 * Use insertion sort algorithm to insert a code to the codeArray and sorted the codeArray in increasing order.
	 * currentLength is the current number of valid code in the codeArray. currentLength must less than the length of codeArray.
	 */
	protected static boolean insertToCodeArray(int code, int[] codeArray, int currentLength) {
		int point = 0;
		while (point < currentLength && codeArray[point] < code) point++;
		if (point < currentLength && codeArray[point] == code) return false;
		for (int i = currentLength; i > point; i--) codeArray[i] = codeArray[i-1];
		codeArray[point] = code;
		return true;
	}
}
