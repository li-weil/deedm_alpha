/**
 * 
 */
package com.deedm.legacy.proplogic.normalFormula;

import java.util.ArrayList;
import java.util.List;

import com.deedm.legacy.proplogic.FormulaTruthTable;
import com.deedm.legacy.proplogic.TruthAssignmentFunction;
import com.deedm.legacy.proplogic.equiv.EquivCalculusRecorder;
import com.deedm.legacy.proplogic.equiv.EquivCalculusStep;
import com.deedm.legacy.proplogic.formula.AndFormula;
import com.deedm.legacy.proplogic.formula.Formula;
import com.deedm.legacy.proplogic.formula.NegFormula;
import com.deedm.legacy.proplogic.formula.OrFormula;
import com.deedm.legacy.proplogic.formula.Symbol;

/**
 * @author Zhou Xiaocong
 *
 */
public class NormalFormulaCalculator {
	private static ExpandNFRecorder expandRecorder = new ExpandNFRecorder(ExpandNFRecorder.TYPE_CNF);
	private static EquivCalculusRecorder calculusRecorder = new EquivCalculusRecorder();

	public static ExpandNFRecorder getExpandRecorder() {
		return expandRecorder;
	}

	public static EquivCalculusRecorder getCalculusRecorder() {
		return calculusRecorder;
	}

	/**
	 * Calculate a logic equivalent CNF for a formula 
	 */
	public static Formula calculateCNF(Formula formula) {
		calculusRecorder.clearStep();
		EquivCalculusStep step = new EquivCalculusStep(formula, " 要计算合取范式的公式");
		calculusRecorder.addStep(step);

		Formula implicationFreeFormula = calculateImplicationFreeFormula(formula);
		step = new EquivCalculusStep(implicationFreeFormula, " 使用蕴涵等值式和双蕴涵等值式消除蕴涵和双蕴涵运算符 ");
		calculusRecorder.addStep(step);

		Formula negationNormalFormula = calculateNegationNormalFormula(implicationFreeFormula);
		step = new EquivCalculusStep(negationNormalFormula, " 将否定运算符移到命题变量的前面 ");
		calculusRecorder.addStep(step);
		
		Formula result = calculateCNFofNegationNormalFormula(negationNormalFormula);
		step = new EquivCalculusStep(result, " 最后得到的合取范式 ");
		calculusRecorder.addStep(step);

		return result;
	}

	/**
	 * Calculate a logic equivalent DNF for a formula 
	 */
	public static Formula calculateDNF(Formula formula) {
		calculusRecorder.clearStep();
		EquivCalculusStep step = new EquivCalculusStep(formula, " 要计算析取范式的公式");
		calculusRecorder.addStep(step);

		Formula implicationFreeFormula = calculateImplicationFreeFormula(formula);
		step = new EquivCalculusStep(implicationFreeFormula, " 使用蕴涵等值式和双蕴涵等值式消除蕴涵和双蕴涵运算符 ");
		calculusRecorder.addStep(step);

		Formula negationNormalFormula = calculateNegationNormalFormula(implicationFreeFormula);
		step = new EquivCalculusStep(negationNormalFormula, " 将否定运算符移到命题变量的前面 ");

		Formula result = calculateDNFofNegationNormalFormula(negationNormalFormula);
		step = new EquivCalculusStep(result, " 最后得到的析取范式 ");
		calculusRecorder.addStep(step);

		return result;
	}

	/**
	 * Give a variable set and a conjunctive normal form, expand it to a principle conjunctive normal formula.   
	 */
	public static PCNFormula expandToPCNF(ConjunctiveNormalFormula conjnormform, char[] variableSet) {
		expandRecorder.clearStep();
		expandRecorder.setType(ExpandNFRecorder.TYPE_CNF);
		
		PCNFormula resultFormula = new PCNFormula(variableSet);
		if (conjnormform.isTautology()) return resultFormula;	// The principle conjunctive normal formula for a tautology contains no simple disjunctive formula.
		
		List<int[]> resultCodeList = new ArrayList<int[]>();
		for (SimpleDisjunctiveFormula disjform : conjnormform.disjformList) {
			int[] code = disjform.getMaxTermBinaryCode(variableSet);
			if (code.length <= 0) continue;	// This simple disjunctive formula is a tautology!
			
			List<int[]> codeList = expandBinaryCode(code);
			int[] resultCodes = new int[codeList.size()];
			int resultIndex = 0;
			for (int[] codeArray : codeList) {
				boolean found = false;
				for (int[] resultCodeArray : resultCodeList) {
					if (isEqualCodeArray(codeArray, resultCodeArray)) {
						found = true;
						break;
					}
				}
				if (found == false) resultCodeList.add(codeArray);

				resultCodes[resultIndex] = toInteger(codeArray);
				resultIndex++;
			}
			
			Formula formula = disjform.toFormula();
			ExpandNFStep step = new ExpandNFStep(formula, code, resultCodes);
			expandRecorder.addStep(step);
		}
		for (int[] resultCodeArray : resultCodeList) {
			SimpleDisjunctiveFormula disjform = createSDFormulaFromBinaryCode(resultCodeArray, variableSet);
			resultFormula.addSimpleDisjunctiveFormula(disjform);
		}
		return resultFormula;
	}
	
	/**
	 * Give a variable set and a conjunctive normal form, expand it to a principle conjunctive normal formula.   
	 */
	public static PDNFormula expandToPDNF(DisjunctiveNormalFormula disjnormform, char[] variableSet) {
		expandRecorder.clearStep();
		expandRecorder.setType(ExpandNFRecorder.TYPE_DNF);
		
		PDNFormula resultFormula = new PDNFormula(variableSet);
		if (disjnormform.isContradiction()) return resultFormula;	// The principle disjunctive normal formula for a contradiction contains no simple conjunctive formula.
		
		List<int[]> resultCodeList = new ArrayList<int[]>();
		for (SimpleConjunctiveFormula conjform : disjnormform.conjformList) {
			int[] code = conjform.getMinTermBinaryCode(variableSet);
			if (code.length <= 0) continue;	// This simple disjunctive formula is a tautology!
			
			List<int[]> codeList = expandBinaryCode(code);
			int[] resultCodes = new int[codeList.size()];
			int resultIndex = 0;
			for (int[] codeArray : codeList) {
				boolean found = false;
				for (int[] resultCodeArray : resultCodeList) {
					if (isEqualCodeArray(codeArray, resultCodeArray)) {
						found = true;
						break;
					}
				}
				if (found == false) resultCodeList.add(codeArray);

				resultCodes[resultIndex] = toInteger(codeArray);
				resultIndex++;
			}
			
			Formula formula = conjform.toFormula();
			ExpandNFStep step = new ExpandNFStep(formula, code, resultCodes);
			expandRecorder.addStep(step);
		}
		for (int[] resultCodeArray : resultCodeList) {
			SimpleConjunctiveFormula disjform = createSCFormulaFromBinaryCode(resultCodeArray, variableSet);
			resultFormula.addSimpleConjunctiveFormula(disjform);
		}
		return resultFormula;
	}

	/**
	 * Get the principle conjunctive normal formula by build the truth table for a formula
	 */
	public static PCNFormula getPCNFByTruthTable(Formula formula) {
		char[] propVariableSet = formula.getAllVariables(); 
		PCNFormula result = new PCNFormula(propVariableSet);
		FormulaTruthTable truthTable = new FormulaTruthTable(formula);
		List<TruthAssignmentFunction> functionList = truthTable.getFalseAssignmentList();
		int[] codeArray = new int[propVariableSet.length];
		for (TruthAssignmentFunction function : functionList) {
			for (int i = 0; i < propVariableSet.length; i++) {
				boolean value = function.getValue(propVariableSet[i]);
				if (value == true) codeArray[i] = 1;
				else codeArray[i] = 0;
			}
			SimpleDisjunctiveFormula disjform = createSDFormulaFromBinaryCode(codeArray, propVariableSet);
			result.addSimpleDisjunctiveFormula(disjform);
		}
		return result;
	}
	
	/**
	 * Get the principle disjunctive normal formula by build the truth table for a formula
	 */
	public static PDNFormula getPDNFByTruthTable(Formula formula) {
		char[] propVariableSet = formula.getAllVariables(); 
		PDNFormula result = new PDNFormula(propVariableSet);
		FormulaTruthTable truthTable = new FormulaTruthTable(formula);
		List<TruthAssignmentFunction> functionList = truthTable.getTrueAssignmentList();
		int[] codeArray = new int[propVariableSet.length];
		for (TruthAssignmentFunction function : functionList) {
			for (int i = 0; i < propVariableSet.length; i++) {
				boolean value = function.getValue(propVariableSet[i]);
				if (value == true) codeArray[i] = 1;
				else codeArray[i] = 0;
			}
			SimpleConjunctiveFormula conjform = createSCFormulaFromBinaryCode(codeArray, propVariableSet);
			result.addSimpleConjunctiveFormula(conjform);
		}
		return result;
	}
	
	/**
	 * Give a variable set and a set of Max-term codes, create a principle conjunctive normal formula. 
	 */
	public static PCNFormula createByMaxtermCodes(int[] codes, char[] propVariableSet) {
		PCNFormula result = new PCNFormula(propVariableSet);
		int variableNumber = propVariableSet.length;
		
		for (int i = 0; i < codes.length; i++) {
			int[] codeArray = toBinaryCode(codes[i], variableNumber);
			if (codeArray == null) return null;
			SimpleDisjunctiveFormula disjform = createSDFormulaFromBinaryCode(codeArray, propVariableSet);
			result.addSimpleDisjunctiveFormula(disjform);
		}
		return result;
	}

	
	/**
	 * Given an array of codes of min-terms, create a principle disjunctive normal formula 
	 */
	public static PDNFormula createByMintermCodes(int[] codes, char[] propVariableSet) {
		PDNFormula result = new PDNFormula(propVariableSet);
		int variableNumber = propVariableSet.length;
		
		for (int i = 0; i < codes.length; i++) {
			int[] codeArray = toBinaryCode(codes[i], variableNumber);
			if (codeArray == null) return null;

			SimpleConjunctiveFormula conjform = createSCFormulaFromBinaryCode(codeArray, propVariableSet);
			result.addSimpleConjunctiveFormula(conjform);
		}
		return result;
	}

	/**
	 * Create a simple disjunctive formula corresponding to the code given by codeArray in binary. Assume that codeArray.length <= variableSet.length, 
	 * and use the first codeArray.length variables in variableSet!! 
	 */
	public static SimpleDisjunctiveFormula createSDFormulaFromBinaryCode(int[] codeArray, char[] variableSet) {
		SimpleDisjunctiveFormula formula = new SimpleDisjunctiveFormula();
		
		for (int i = 0; i < codeArray.length; i++) {
			Literal literal = null;
			if (codeArray[i] == 0) literal = new Literal(variableSet[i], true);
			else if (codeArray[i] == 1) literal = new Literal(variableSet[i], false);
			if (codeArray[i] == 0 || codeArray[i] == 1) formula.addLiteral(literal);
		}
		return formula;
	}


	/**
	 * Create a simple conjunctive formula corresponding to the code given by codeArray in binary. Assume that codeArray.length <= variableSet.length, 
	 * and use the first codeArray.length variables in variableSet!! 
	 */
	public static SimpleConjunctiveFormula createSCFormulaFromBinaryCode(int[] codeArray, char[] variableSet) {
		SimpleConjunctiveFormula formula = new SimpleConjunctiveFormula();
		
		for (int i = 0; i < codeArray.length; i++) {
			Literal literal = null;
			if (codeArray[i] == 0) literal = new Literal(variableSet[i], false);
			else if (codeArray[i] == 1) literal = new Literal(variableSet[i], true);
			if (codeArray[i] == 0 || codeArray[i] == 1) formula.addLiteral(literal);
		}
		return formula;
	}

	/**
	 * Random generate a simple disjunctive formula by random select variable in varSet, and random select as positive literal or negative literal.  
	 */
	public static String randomGenerateSimpleDisjunctiveFormula(char[] varSet) {
		int length = varSet.length;
		int size = (int)(Math.random() * length) + 1;
		if (size > length) size = length;
		int[] index = new int[size];
		if (size == length) {
			for (int i = 0; i < length; i++) index[i] = i;
		} else {
			int counter = 0; 
			int maxCounter = 0;
			while (counter < size && maxCounter < 100) {
				int randomIndex = (int)(Math.random() * length);
				boolean found = false;
				for (int i = 0; i < counter; i++) {
					if (index[i] == randomIndex) {
						found = true;
						break;
					}
				}
				if (found == false) {
					index[counter] = randomIndex;
					counter++;
				}
				maxCounter++;
			}
			if (counter < size) {
				int[] tempIndex = new int[counter];
				for (int i = 0; i < counter; i++) tempIndex[i] = index[i];
				index = new int[counter];
				for (int i = 0; i < counter; i++) index[i] = tempIndex[i];
			}
		}
		StringBuffer result = new StringBuffer();
		int randomInt = (int)(Math.random() * 10);
		if (randomInt > 5) result.append(""+varSet[index[0]]);
		else result.append("(" + Symbol.OPERATOR_NOT + varSet[index[0]] + ")");
		for (int i = 1; i < size; i++) {
			randomInt = (int)(Math.random() * 10);
			if (randomInt > 5) result.append("" + Symbol.OPERATOR_OR + varSet[index[i]]);
			else result.append("" + Symbol.OPERATOR_OR + "(" + Symbol.OPERATOR_NOT + varSet[index[i]] + ")");
		}
		if (size == 1) return result.toString();
		else return "(" + result.toString() + ")";
	}

	/**
	 * Random generate a simple conjunctive formula by random select variable in varSet, and random select as positive literal or negative literal.  
	 */
	public static String randomGenerateSimpleConjunctiveFormula(char[] varSet) {
		int length = varSet.length;
		int size = (int)(Math.random() * length) + 1;
		if (size > length) size = length;
		int[] index = new int[size];
		if (size == length) {
			for (int i = 0; i < length; i++) index[i] = i;
		} else {
			int counter = 0; 
			int maxCounter = 0;
			while (counter < size && maxCounter < 100) {
				int randomIndex = (int)(Math.random() * length);
				boolean found = false;
				for (int i = 0; i < counter; i++) {
					if (index[i] == randomIndex) {
						found = true;
						break;
					}
				}
				if (found == false) {
					index[counter] = randomIndex;
					counter++;
				}
				maxCounter++;
			}
			if (counter < size) {
				int[] tempIndex = new int[counter];
				for (int i = 0; i < counter; i++) tempIndex[i] = index[i];
				index = new int[counter];
				for (int i = 0; i < counter; i++) index[i] = tempIndex[i];
			}
		}
		StringBuffer result = new StringBuffer();
		int randomInt = (int)(Math.random() * 10);
		if (randomInt > 5) result.append("" + varSet[index[0]]);
		else result.append("(" + Symbol.OPERATOR_NOT + varSet[index[0]] + ")");
		for (int i = 1; i < size; i++) {
			randomInt = (int)(Math.random() * 10);
			if (randomInt > 5) result.append("" + Symbol.OPERATOR_AND + varSet[index[i]]);
			else result.append("" + Symbol.OPERATOR_AND + "(" + Symbol.OPERATOR_NOT + varSet[index[i]] + ")");
		}
		if (size == 1) return result.toString();
		else return "(" + result.toString() + ")";
	}
	
	
	/**
	 * Expand a binary code (maybe include -1) to a list of binary code by using all combination of 
	 * 0 and 1 to fill -1 in the binary code!
	 */
	private static List<int[]> expandBinaryCode(int[] code) {
		List<int[]> resultList = new ArrayList<int[]>();
		int emptyCodeNumber = 0;
		for (int i = 0; i < code.length; i++) {
			if (code[i] == -1) emptyCodeNumber++;
		}
		if (emptyCodeNumber == 0) {
			int[] copyCode = new int[code.length];
			for (int i = 0; i < code.length; i++) copyCode[i] = code[i];
			resultList.add(copyCode);
			return resultList;
		}
		
		int[] expandingCode = new int[emptyCodeNumber];
		for (int i = 0; i < expandingCode.length; i++) expandingCode[i] = 0;
		
		while (true) {
			int[] copyCode = new int[code.length];
			int emptyCodeIndex = 0;
			for (int i = 0; i < code.length; i++) {
				copyCode[i] = code[i];
				if (code[i] == -1) {
					copyCode[i] = expandingCode[emptyCodeIndex];
					emptyCodeIndex++;
				}
			}
			resultList.add(copyCode);

			int i = expandingCode.length-1;
			for (i = expandingCode.length-1; i >= 0; i--) {
				if (expandingCode[i] == 1) expandingCode[i] = 0;
				else break;
			}
			if (i >= 0) expandingCode[i] = 1;
			else break;
		}
		return resultList;
	}
	
	/**
	 * Translate away all implication and bi-implication in the formula to a implication and bi-implication free formula
	 */
	private static Formula calculateImplicationFreeFormula(Formula formula) {
		if (formula.isAtomicFormula()) return formula.getACopy();
		
		Formula result = null;
		if (formula.isNegFormula()) {
			Formula rightResult = calculateImplicationFreeFormula(formula.getRight());
			result = new NegFormula(rightResult);
		} else if (formula.isAndFormula()) {
			Formula leftResult = calculateImplicationFreeFormula(formula.getLeft());
			Formula rightResult = calculateImplicationFreeFormula(formula.getRight());
			result = new AndFormula(leftResult, rightResult);
		} else if (formula.isOrFormula()) {
			Formula leftResult = calculateImplicationFreeFormula(formula.getLeft());
			Formula rightResult = calculateImplicationFreeFormula(formula.getRight());
			result = new OrFormula(leftResult, rightResult);
		} else if (formula.isImpFormula()) {
			Formula leftResult = calculateImplicationFreeFormula(formula.getLeft());
			Formula rightResult = calculateImplicationFreeFormula(formula.getRight());
			result = new OrFormula(new NegFormula(leftResult), rightResult);
		} else if (formula.isBiImpFormula()) {
			Formula leftResult = calculateImplicationFreeFormula(formula.getLeft());
			Formula rightResult = calculateImplicationFreeFormula(formula.getRight());
			result = new AndFormula(new OrFormula(new NegFormula(leftResult), rightResult), new OrFormula(leftResult, new NegFormula(rightResult)));
		}
		return result;
	}

	/**
	 * Move all negation to atomic formula. The given formula must be a implication and bi-implication free formula.
	 */
	private static Formula calculateNegationNormalFormula(Formula formula) {
		if (Literal.isLiteral(formula)) return formula.getACopy();
		
		Formula result = null;
		if (formula.isNegFormula()) {
			Formula right = formula.getRight();
			if (right.isNegFormula()) result = right.getRight().getACopy();
			else if (right.isAndFormula()) {
				Formula rightLeft = right.getLeft();
				Formula rightRight = right.getRight();
				Formula leftResult = calculateNegationNormalFormula(new NegFormula(rightLeft));
				Formula rightResult = calculateNegationNormalFormula(new NegFormula(rightRight));
				result = new OrFormula(leftResult, rightResult);
			} else if (right.isOrFormula()) {
				Formula rightLeft = right.getLeft();
				Formula rightRight = right.getRight();
				Formula leftResult = calculateNegationNormalFormula(new NegFormula(rightLeft));
				Formula rightResult = calculateNegationNormalFormula(new NegFormula(rightRight));
				result = new AndFormula(leftResult, rightResult);
			}
		} else if (formula.isAndFormula()) {
			Formula leftResult = calculateNegationNormalFormula(formula.getLeft());
			Formula rightResult = calculateNegationNormalFormula(formula.getRight());
			result = new AndFormula(leftResult, rightResult);
		} else if (formula.isOrFormula()) {
			Formula leftResult = calculateNegationNormalFormula(formula.getLeft());
			Formula rightResult = calculateNegationNormalFormula(formula.getRight());
			result = new OrFormula(leftResult, rightResult);
		}
		return result;
	}

	/**
	 * Calculate a logical equivalent Conjunctive Normal Formula for a negation normal formula, which is a implication and bi-implication
	 * free formula with all negations applied only on atomic formula.
	 */
	private static Formula calculateCNFofNegationNormalFormula(Formula formula) {
		if (formula.isAtomicFormula()) return formula.getACopy();
		
		Formula result = null;
		if (formula.isNegFormula()) {
			// Here assume that all negations are applied only on atomic formula
			return formula.getACopy();
		} else if (formula.isAndFormula()) {
			Formula leftResult = calculateCNFofNegationNormalFormula(formula.getLeft());
			Formula rightResult = calculateCNFofNegationNormalFormula(formula.getRight());
			result = new AndFormula(leftResult, rightResult);
		} else if (formula.isOrFormula()) {
			Formula leftResult = calculateCNFofNegationNormalFormula(formula.getLeft());
			Formula rightResult = calculateCNFofNegationNormalFormula(formula.getRight());
			result = calculateDistributionForCNF(leftResult, rightResult);
		}
		return result;
	}

	/**
	 * Calculate distribution for 'left or right' when left and right are CNF, and get a logic equivalent CNF  
	 */
	private static Formula calculateDistributionForCNF(Formula left, Formula right) {
		Formula result = null;
		
		if (left.isAndFormula()) {
			Formula leftLeft = left.getLeft();
			Formula leftRight = left.getRight();
			Formula leftResult = calculateDistributionForCNF(leftLeft, right);
			Formula rightResult  = calculateDistributionForCNF(leftRight, right);
			result = new AndFormula(leftResult, rightResult);
		} else {
			if (right.isAndFormula()) {
				Formula rightLeft = right.getLeft();
				Formula rightRight = right.getRight();
				Formula leftResult = calculateDistributionForCNF(left, rightLeft);
				Formula rightResult  = calculateDistributionForCNF(left, rightRight);
				result = new AndFormula(leftResult, rightResult);
			} else result = new OrFormula(left, right);
		} 
		return result;
	}

	/**
	 * Calculate a logical equivalent Disjunctive Normal Formula for a negation normal formula, which is a implication and bi-implication
	 * free formula with all negations applied only on atomic formula.
	 */
	private static Formula calculateDNFofNegationNormalFormula(Formula formula) {
		if (formula.isAtomicFormula()) return formula.getACopy();
		
		Formula result = null;
		if (formula.isNegFormula()) {
			// Here assume that all negations are applied only on atomic formula
			return formula.getACopy();
		} else if (formula.isOrFormula()) {
			Formula leftResult = calculateDNFofNegationNormalFormula(formula.getLeft());
			Formula rightResult = calculateDNFofNegationNormalFormula(formula.getRight());
			result = new OrFormula(leftResult, rightResult);
		} else if (formula.isAndFormula()) {
			Formula leftResult = calculateDNFofNegationNormalFormula(formula.getLeft());
			Formula rightResult = calculateDNFofNegationNormalFormula(formula.getRight());
			result = calculateDistributionForDNF(leftResult, rightResult);
		}
		return result;
	}

	/**
	 * Calculate distribution for 'left or right' when left and right are DNF, and get a logic equivalent DNF  
	 */
	private static Formula calculateDistributionForDNF(Formula left, Formula right) {
		Formula result = null;
		
		if (left.isOrFormula()) {
			Formula leftLeft = left.getLeft();
			Formula leftRight = left.getRight();
			Formula leftResult = calculateDistributionForDNF(leftLeft, right);
			Formula rightResult  = calculateDistributionForDNF(leftRight, right);
			result = new OrFormula(leftResult, rightResult);
		} else {
			if (right.isOrFormula()) {
				Formula rightLeft = right.getLeft();
				Formula rightRight = right.getRight();
				Formula leftResult = calculateDistributionForDNF(left, rightLeft);
				Formula rightResult  = calculateDistributionForDNF(left, rightRight);
				result = new OrFormula(leftResult, rightResult);
			} else result = new AndFormula(left, right);
		} 
		return result;
	}

	/**
	 * Convert a integer to its binary representation with the given maxLength.
	 */
	protected static int[] toBinaryCode(int code, int maxLength) {
		int[] codeArray = new int[maxLength];
		int index = maxLength-1;
		int value = code;
		while (value > 0 && index >= 0) {
			codeArray[index] = value % 2;
			index = index - 1;
			value = value / 2;
		}
		if (index < -1) {
			System.out.println("The code " + code + " is not in the range, and maximum length is " + maxLength);
		}
		return codeArray;
	}

	/**
	 * Convert a binary array to an integer.
	 */
	protected static int toInteger(int[] codeArray) {
		int result = 0;
		for (int i = 0; i < codeArray.length; i++) {
			if (codeArray[i] == 1) result = result * 2 + 1;
			else if (codeArray[i] == 0) result = result * 2;
			else return -1;
		}
		return result;
	}
	
	/**
	 * Check if the given two code arrays includes the same integers. 
	 */
	public static boolean isEqualCodeArray(int[] oneCodeArray, int[] twoCodeArray) {
		if (oneCodeArray.length != twoCodeArray.length) return false;
		for (int i = 0; i < oneCodeArray.length; i++) {
			if (oneCodeArray[i] != twoCodeArray[i]) return false;
		}
		return true;
	}
	

}
