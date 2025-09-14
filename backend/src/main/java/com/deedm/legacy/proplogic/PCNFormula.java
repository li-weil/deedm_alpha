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
 * If a conjunctive normal formula has empty simple disjunctive formula list, then it is a tautology, whereas, if it has an empty clause (i.e.
 * a simple disjunctive formula which has no literals), it is a contradiction. 
 */
public class PCNFormula extends ConjunctiveNormalFormula {
	protected char[] variableSet = null;
	
	protected PCNFormula(char[] variableSet) {
		super();
		this.variableSet = variableSet;
	}
	
	protected PCNFormula(SimpleDisjunctiveFormula disjform, char[] variableSet) {
		super(disjform);
		this.variableSet = variableSet;
	}

	protected PCNFormula(List<SimpleDisjunctiveFormula> list, char[] variableSet) {
		super(list);
		this.variableSet = variableSet;
	}

	/**
	 * Give a variable set, check if the given formula is a principle conjunctive normal formula.   
	 */
	public static boolean isPrincipleConjunctiveNormalForm(Formula formula, char[] propVariableSet) {
		SimpleDisjunctiveFormula disjform = SimpleDisjunctiveFormula.convert(formula);
		if (disjform != null) {
			if (disjform.isMaxTerm(propVariableSet)) return true;
		}
		
		if (!formula.isAndFormula()) return false;
		if (!isPrincipleConjunctiveNormalForm(formula.getLeft(), propVariableSet)) return false;
		if (!isPrincipleConjunctiveNormalForm(formula.getRight(), propVariableSet)) return false;
		return true;
	}

	public char[] getVariableSet() {
		return variableSet;
	}
	
	/**
	 * Return the max-term code array of this principle conjunctive normal formula. If a simple disjunctive formula is not a max-term for  
	 * the variable set, then it will return NULL. Here we assume that this formula is not a tautology!
	 */
	public int[] getAllCodesofMaxTerm() {
		int[] result = new int[disjformList.size()];
		for (int i = 0; i < result.length; i++) result[i] = -1;
		int index = 0;
		for (SimpleDisjunctiveFormula disjform: disjformList) {
			int[] codeArray = disjform.getMaxTermBinaryCode(variableSet);
			int code = NormalFormulaCalculator.toInteger(codeArray);
			if (code < 0) return null;
			PDNFormula.insertToCodeArray(code, result, index);
			index++;
		}
		return result;
	}
	
	
	/**
	 * If the formula is a principle conjunctive normal form when all variables contained in the array ariableSet, the return its all min-term codes.
	 * If it is a contradiction, then return an array with zero length. 
	 * Therefore, by this method, the principle disjunctive normal form can be obtained by this principle conjunctive normal form. 
	 */
	public int[] getAllCodesofMinTerm() {
		int powerof2 = 1;
		for (int i = 0; i < variableSet.length; i++) powerof2 = powerof2 * 2;
		
		if (isTautology()) {
			int[] result = new int[powerof2];
			for (int i = 0; i < powerof2; i++) result[i] = i;
			return result;
		}
		
		int[] maxCodeArray = getAllCodesofMaxTerm();
		int[] result = new int[powerof2 - maxCodeArray.length];
		for (int i = 0; i < result.length; i++) result[i] = -1;
		
		int code = 0, maxCodeIndex = 0;
		int index = 0;
		while (code < powerof2) {
			if (maxCodeIndex >= maxCodeArray.length) {
				result[index] = code;
				index++;
			} else if (code < maxCodeArray[maxCodeIndex]) {
				result[index] = code;
				index++;
			} else maxCodeIndex++;
			code++;
		}
		return result;
	}
	
	/**
	 * Return a string such as "M_0 & M_2" to represent this principle conjunctive normal formula.
	 */
	public String toNamedString() {
		if (isTautology()) return "1";
		
		StringBuffer buffer = new StringBuffer();
		int[] codeArray = getAllCodesofMaxTerm();
		buffer.append("M" + codeArray[0]);
		for (int i = 1; i < codeArray.length; i++) buffer.append(" " + Symbol.OPERATOR_AND + " M" + codeArray[i]);
		return buffer.toString();
	}
	
	/**
	 * Return a string such as "M_0 \wedge M_2" to represent this principle conjunctive normal formula.
	 */
	public String toNamedLaTeXString() {
		if (isTautology()) return "1";

		StringBuffer buffer = new StringBuffer();
		int[] codeArray = getAllCodesofMaxTerm();
		if (codeArray.length == 0) return "1";
		buffer.append("M_{" + codeArray[0] + "}");
		for (int i = 1; i < codeArray.length; i++) buffer.append(" " + Symbol.LATEX_AND + " M_{" + codeArray[i] + "}");
		return buffer.toString();
	}

	/**
	 * Return a string such as "m_0 | m_2" to represent the principle disjunctive normal formula for this principle conjunctive normal formula.
	 */
	public String toNamedPDNFString() {
		StringBuffer buffer = new StringBuffer();
		int[] codeArray = getAllCodesofMinTerm();
		if (codeArray == null || codeArray.length == 0) return "0";
		
		buffer.append("m" + codeArray[0]);
		for (int i = 1; i < codeArray.length; i++) buffer.append(" " + Symbol.OPERATOR_OR + "m" + codeArray[i]);
		return buffer.toString();
	}
	
	/**
	 * Return a string such as "m_0 \vee m_2" to represent the principle disjunctive normal formula for this principle conjunctive normal formula.
	 */
	public String toNamedPDNFLaTeXString() {
		StringBuffer buffer = new StringBuffer();
		int[] codeArray = getAllCodesofMinTerm();
		if (codeArray == null || codeArray.length == 0) return "0";
		
		buffer.append("m_{" + codeArray[0] + "}");
		for (int i = 1; i < codeArray.length; i++) buffer.append(" " + Symbol.LATEX_OR + " m_{" + codeArray[i] + "}");
		return buffer.toString();
	}
}
