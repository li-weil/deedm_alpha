/**
 * 
 */
package com.deedm.legacy.proplogic;

import java.util.ArrayList;

import com.deedm.legacy.proplogic.formula.AndFormula;
import com.deedm.legacy.proplogic.formula.BiImpFormula;
import com.deedm.legacy.proplogic.formula.Formula;
import com.deedm.legacy.proplogic.formula.ImpFormula;
import com.deedm.legacy.proplogic.formula.NegFormula;
import com.deedm.legacy.proplogic.formula.OrFormula;
import com.deedm.legacy.proplogic.formula.Symbol;
import com.deedm.legacy.proplogic.formula.AtomicFormula;

/**
 * @author zxc
 *
 */
public class FormulaBuilder {
	private static String errorMessage = null;
	
	public static String getErrorMessage() {
		return errorMessage;
	}

	public static Formula buildFromStrictSymbolFormulaString(String formula) {
			char[] buffer = symbolFormulaStringToCharacterArray(formula);
			if (buffer == null) return null;
	
			return buildFromStrictCharacterArray(buffer);
		}

	public static Formula buildFromStrictLaTexFormulaString(String formula) {
			char[] buffer = laTexFormulaStringToCharacterArray(formula);
			if (buffer == null) return null;
	
			return buildFromStrictCharacterArray(buffer);
		}

	public static Formula buildFromSymbolFormulaString(String formula) {
			char[] buffer = symbolFormulaStringToCharacterArray(formula);
			
			if (buffer == null) return null;
			return buildFromCharacterArray(buffer);
		}

	public static Formula buildFromLaTexFormulaString(String formula) {
			char[] buffer = laTexFormulaStringToCharacterArray(formula);
			if (buffer == null) return null;
	
			return buildFromCharacterArray(buffer);
		}

	public static Formula buildFromCharacterArray(char[] formula) {
//			System.out.println("Build from [" + characterArrayToString(formula) + "], its length " + formula.length);
			
			if (formula.length == 1) return new AtomicFormula(formula[0]);
	
			char[] operatorStack = new char[formula.length];
			Formula[] operandStack = new Formula[formula.length];
			int operatorStackTop = 0;
			int operandStackTop = 0;
			int formulaIndex = 0;
			
			while (true) {
				char currentChar = Symbol.END_FLAG;
//				System.out.println("Try to look char in formula index " + formulaIndex + ", formula length " + formula.length);
				if (formulaIndex < formula.length) {
					currentChar = formula[formulaIndex];
					formulaIndex = formulaIndex + 1;
					
					if (!Symbol.isOperator(currentChar) && !Symbol.isLeftBracket(currentChar) && !Symbol.isRightBracket(currentChar)) {
						Formula varFormula = new AtomicFormula(currentChar);
						operandStack[operandStackTop] = varFormula;
						operandStackTop = operandStackTop + 1;
						
//						System.out.println("\tPush operand [" + varFormula + "] to stack[" + (operandStackTop-1) + "]!");
						continue;
					}
					
					if (operatorStackTop == 0 || Symbol.isLeftBracket(currentChar)) {
						operatorStack[operatorStackTop] = currentChar;
						operatorStackTop = operatorStackTop + 1;
//						System.out.println("\tPush operator [" + currentChar + "] to stack[" + (operatorStackTop-1) + "]!");
						continue;
					}
				}
				
				if (currentChar == Symbol.END_FLAG && operatorStackTop <= 0) {
					if (operandStackTop != 1) {
						StringBuffer buffer = new StringBuffer();
						buffer.append("Illegal formula end, there are more than one operand in stack!");
						for (int i = 0; i < operandStackTop; i++) buffer.append("OperandStack[" + i + "] = " + operandStack[i].toString());
						errorMessage = buffer.toString();
						return null;
					}
					return operandStack[0];
				}
					
				boolean popStack = false;
				if (Symbol.isRightBracket(currentChar)) {
					if (operatorStackTop == 0) {
						errorMessage = "Illegal right bracket!";
						return null;
					}
					popStack = true;
				}
				if (currentChar == Symbol.END_FLAG) popStack = true;
	
				char operatorInStack = operatorStack[operatorStackTop-1];
				if (Symbol.isPriorTo(operatorInStack, currentChar)) popStack = true;
				
				if (popStack == false) {
					operatorStack[operatorStackTop] = currentChar;
					operatorStackTop = operatorStackTop + 1;
//					System.out.println("\tPush operator [" + currentChar + "] to stack[" + (operatorStackTop-1) + "]!");
					continue;
				}
	
				while (popStack == true) {
//					System.out.println("\tOperator in stack " + operatorInStack + ", current char " + currentChar + ", formula length " + formula.length + ", formula index " + formulaIndex + ", operatorStackTop " + operatorStackTop);
					if (operatorInStack == Symbol.LEFT_BRACKET) {
						if (currentChar == Symbol.RIGHT_BRACKET || currentChar == Symbol.END_FLAG) {
							operatorStackTop = operatorStackTop - 1;
							break;
						} else {
							errorMessage = "Illegal left bracket to mactch operator " + currentChar;
							return null;
						}
					}
					if (operatorInStack == Symbol.OPERATOR_NOT) {
						if (operandStackTop < 1) {
							errorMessage = "Miss operand for NOT!";
							return null;
						}
						Formula rightFormula = operandStack[operandStackTop-1];
						operandStackTop = operandStackTop - 1;
						Formula negFormula = new NegFormula(rightFormula);
						operandStack[operandStackTop] = negFormula;
						operandStackTop = operandStackTop + 1;
						
//						System.out.println("\tPop operator [" + operatorInStack + "] at stack[" + (operatorStackTop-1) + "]!");
//						System.out.println("\t\tReduce formula " + rightFormula + " to " + negFormula + " at operand stack[" + (operandStackTop-1) + "]");
						
					} else {
						if (operandStackTop < 2) {
							errorMessage = "Miss operand for " + operatorInStack + "!";
							return null;
						}
						Formula rightFormula = operandStack[operandStackTop-1];
						Formula leftFormula = operandStack[operandStackTop-2];
						operandStackTop = operandStackTop - 2;
						Formula newFormula = null;
						if (operatorInStack == Symbol.OPERATOR_AND) newFormula = new AndFormula(leftFormula, rightFormula);
						else if (operatorInStack == Symbol.OPERATOR_OR) newFormula = new OrFormula(leftFormula, rightFormula);
						else if (operatorInStack == Symbol.OPERATOR_IMP) newFormula = new ImpFormula(leftFormula, rightFormula);
						else if (operatorInStack == Symbol.OPERATOR_EQ) newFormula = new BiImpFormula(leftFormula, rightFormula);
						else {
							errorMessage = "Illegal operator " + operatorInStack;
							return null;
						}
						operandStack[operandStackTop] = newFormula;
						operandStackTop = operandStackTop + 1;
	
	//					System.out.println("\tPop operator [" + operatorInStack + "] at stack[" + (operatorStackTop-1) + "]!");
	//					System.out.println("\t\tReduce right formula " + rightFormula + " and left formula " + leftFormula + " to " + newFormula + " at operand stack[" + (operandStackTop-1) + "]");
					}
					operatorStackTop = operatorStackTop - 1;
					if (operatorStackTop == 0) popStack = false;
					else {
						operatorInStack = operatorStack[operatorStackTop-1];
						if (currentChar == Symbol.RIGHT_BRACKET || currentChar == Symbol.END_FLAG) popStack = true;
						else if (Symbol.isPriorTo(operatorInStack, currentChar)) popStack = true;
						else popStack = false;
					}
//					System.out.println("\tThe new operator in stack top is " + operatorInStack + " at stack[" + (operatorStackTop-1) + "], currentChar = " + currentChar + ", and popStack = " + popStack);
				}
				if (Symbol.isOperator(currentChar)) {
					operatorStack[operatorStackTop] = currentChar;
					operatorStackTop = operatorStackTop + 1;
//					System.out.println("\tPush operator [" + currentChar + "] to stack[" + (operatorStackTop-1) + "]!");
				}
			}
		}

	public static Formula buildFromStrictCharacterArray(char[] formula) {
		if (formula.length == 1) return new AtomicFormula(formula[0]);
		
		if (formula[0] != Symbol.LEFT_BRACKET || formula[formula.length-1] != Symbol.RIGHT_BRACKET) return null;
		int leftBracketNumber = 0;
		int rightBracketNumber = 0;
		
		for (int i = 0; i < formula.length; i++) {
			if (Symbol.isLeftBracket(formula[i])) leftBracketNumber++;
			if (Symbol.isRightBracket(formula[i])) rightBracketNumber++;
			
			if (Symbol.isOperator(formula[i]) && (leftBracketNumber == rightBracketNumber+1)) {
				char operator = formula[i];
				
				Formula left = null;
				if (operator != Symbol.OPERATOR_NOT) {
					int leftLength = (i-1)-1 + 1;
					if (leftLength <= 0) return null; 
					char[] leftFormula = new char[leftLength];
					for (int j = 1; j < i; j++) leftFormula[j-1] = formula[j];
					left = buildFromStrictCharacterArray(leftFormula);
					if (left == null) return null;
				}
				
				int rightLength = (formula.length - 2) - (i + 1) + 1;
				if (rightLength <= 0) return null;
				char[] rightFormula = new char[rightLength];
				for (int j = i+1; j < formula.length-1; j++) rightFormula[j-i-1] = formula[j];
				Formula right = buildFromStrictCharacterArray(rightFormula);
				if (right == null) return null;

				Formula resultFormula = null;
				
				if (operator == Symbol.OPERATOR_NOT) resultFormula = new NegFormula(right);
				else if (operator == Symbol.OPERATOR_AND) resultFormula = new AndFormula(left, right);
				else if (operator == Symbol.OPERATOR_OR) resultFormula = new OrFormula(left, right);
				else if (operator == Symbol.OPERATOR_IMP) resultFormula = new ImpFormula(left, right);
				else if (operator == Symbol.OPERATOR_EQ) resultFormula = new BiImpFormula(left, right); 
				return resultFormula;
			}
		}
		return null;
	}

	public static String characterArrayToString(char[] array) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < array.length && array[i] != 0; i++) buffer.append(array[i]);
		return buffer.toString();
	}

	private static char[] laTexFormulaStringToCharacterArray(String formula) {
		int length = formula.length();
		char[] buffer = new char[length];
		int currentIndex = 0;
		int i = 0;
		while (i < length) {
			char ch = formula.charAt(i);
			i = i + 1;
			if (ch == ' ' || ch == '\r' || ch == '\n' || ch == '$') continue;
			if (ch == Symbol.LEFT_BRACKET || ch == Symbol.RIGHT_BRACKET) {
				buffer[currentIndex] = ch;
				currentIndex = currentIndex + 1;
				continue;
			}
			if (ch == '\\') {
				char[] operator = new char[Symbol.MAX_LATEX_LENGTH+1];
				operator[0] = ch;
				
				if (i >= length) {
					errorMessage = "Return null for incorrect end of the formula(there should be LaTeX command string after \\)!";
					return null;
				}
				int index = 1;
				ch = formula.charAt(i);		// Let ch = the next char after '\\', all latex command char after '\\' should be between 'a' and 'z'
				while (ch >= 'a' && ch <= 'z' && index < Symbol.MAX_LATEX_LENGTH) {
					operator[index] = ch;
					index = index + 1;
					i = i + 1;
					if (i < length) ch = formula.charAt(i);
					else break;
				}
				operator[index] = 0;
				String string = FormulaBuilder.characterArrayToString(operator);
				if (string.equals(Symbol.LATEX_AND)) {
					buffer[currentIndex] = Symbol.OPERATOR_AND;
					currentIndex = currentIndex + 1;
				} else if (string.equals(Symbol.LATEX_EQ)) {
					buffer[currentIndex] = Symbol.OPERATOR_EQ;
					currentIndex = currentIndex + 1;
				} else if (string.equals(Symbol.LATEX_OR)) {
					buffer[currentIndex] = Symbol.OPERATOR_OR;
					currentIndex = currentIndex + 1;
				} else if (string.equals(Symbol.LATEX_NOT)) {
					buffer[currentIndex] = Symbol.OPERATOR_NOT;
					currentIndex = currentIndex + 1;
				} else if (string.equals(Symbol.LATEX_IMP)) {
					buffer[currentIndex] = Symbol.OPERATOR_IMP;
					currentIndex = currentIndex + 1;
				} else {
					errorMessage = "Return null for string [" + string + "](it is not an acceptable propositional operator)!";
					return null;
				}
				continue;
			}
			
			if (ch >= 'a' && ch <= 'z') {
				buffer[currentIndex] = ch;
				currentIndex = currentIndex + 1;;
				
				// Ignore other letters
				if (i >= length) break;
				ch = formula.charAt(i);
				while (ch >= 'a' && ch <= 'z') {
					i = i + 1;
					if (i < length) ch = formula.charAt(i);
					else break;
				}
				continue;
			}
			
			errorMessage = "Return null for character [" + ch + "](maybe it is not an acceptable LaTeX symbol!";
			return null;
		}
		
		char[] result = new char[currentIndex];
		for (i = 0; i < currentIndex; i++) result[i] = buffer[i];
		return result;
	}

	private static char[] symbolFormulaStringToCharacterArray(String formula) {
		char[] buffer = new char[formula.length()];
		int currentIndex = 0;
		int i = 0;
		while (i < formula.length()) {
			char ch = formula.charAt(i);
			i = i + 1;
			if (ch == ' ' || ch == '\r' || ch == '\n') continue;
			if (Symbol.isLeftBracket(ch) || Symbol.isRightBracket(ch) || Symbol.isOperator(ch)) {
				buffer[currentIndex] = ch;
				currentIndex++;
				continue;
			}
			if (ch >= 'a' && ch <= 'z') {
				buffer[currentIndex] = ch;
				currentIndex++;
				
				// Ignore other letters
				if (i >= formula.length()) break;
				ch = formula.charAt(i);
				while (i < formula.length() && ch >= 'a' && ch <= 'z') {
					i = i + 1;
					ch = formula.charAt(i);
				}
				continue;
			}
			return null;
		}
		
		char[] result = new char[currentIndex];
		for (i = 0; i < currentIndex; i++) result[i] = buffer[i];
		return result;
	}

	
	public static Formula randomGenerateFormulaControlledByLeafNumber(int varNumber, int leafNumber) {
		char[] variable = {'p', 'q', 'r', 's', 't', 'u', 'v', 'x', 'y', 'z'};
		char[] operator = {Symbol.OPERATOR_NOT, Symbol.OPERATOR_AND, Symbol.OPERATOR_OR, Symbol.OPERATOR_IMP, Symbol.OPERATOR_EQ}; 
		int[] usedOperator = {2, 0, 0, 0, 0};
		int usedDistinct = 1;
		if (varNumber >= variable.length) varNumber = variable.length;
		
		Formula[] resultFormulas = new Formula[leafNumber];
		int number = 0;
		while (number < leafNumber) {
			int varIndex = (int)(Math.random()*varNumber);
			AtomicFormula formula = new AtomicFormula(variable[varIndex]);
			resultFormulas[number] = formula;
			number++;
			if (number == leafNumber) break;
			if (varIndex < varNumber-1) varIndex = varIndex + 1;
			else varIndex = 0;
			formula = new AtomicFormula(variable[varIndex]);
			resultFormulas[number] = formula;
			number++;
		}
		
		int formulaNumber = leafNumber;
		while (formulaNumber > 1) {
			int index = 0;
			int newFormulaNumber = 0;
			while (index < formulaNumber) {
				int operatorIndex = selectOperatorIndex(usedOperator, usedDistinct); 
				if (usedOperator[operatorIndex] == 0) usedDistinct++;
				usedOperator[operatorIndex]++;
				char selectedOperator = operator[operatorIndex];
				
				if (selectedOperator == Symbol.OPERATOR_NOT) {
					Formula newFormula = new NegFormula(resultFormulas[index]);
					resultFormulas[newFormulaNumber] = newFormula;
					newFormulaNumber++;
					index++;
				} else {
					if (index < formulaNumber-1) {
						Formula right = resultFormulas[index];
						Formula left = resultFormulas[index+1];
						Formula newFormula = null;
						if (selectedOperator == Symbol.OPERATOR_AND) newFormula = new AndFormula(left, right);
						else if (selectedOperator == Symbol.OPERATOR_OR) newFormula = new OrFormula(left, right);
						else if (selectedOperator == Symbol.OPERATOR_IMP) newFormula = new ImpFormula(left, right);
						else newFormula = new BiImpFormula(left, right);
						resultFormulas[newFormulaNumber] = newFormula;
						newFormulaNumber++;
						index += 2;
					} else {
						resultFormulas[newFormulaNumber] = resultFormulas[index];
						newFormulaNumber++;
						index++;
					}
				}
			}
			formulaNumber = newFormulaNumber;
		}
		return resultFormulas[0];
	}

/*	
	public static Formula randomGenerateFormulaControlledByLeafNumber(int varNumber, int leafNumber) {
		char[] variable = {'p', 'q', 'r', 's', 't', 'u', 'v', 'x', 'y', 'z'};
		char[] operator = {Symbol.OPERATOR_NOT, Symbol.OPERATOR_AND, Symbol.OPERATOR_OR, Symbol.OPERATOR_IMP, Symbol.OPERATOR_EQ}; 
		int[] usedOperator = {2, 0, 0, 0, 0};
		int usedDistinct = 1;
		if (varNumber >= variable.length) varNumber = variable.length;
		
		ArrayList<Formula> resultList = new ArrayList<Formula>();
		int number = 0;
		while (number < leafNumber) {
			int varIndex = (int)(Math.random()*varNumber);
			AtomicFormula formula = new AtomicFormula(variable[varIndex]);
			resultList.add(formula);
			number++;
			if (number == leafNumber) break;
			if (varIndex < varNumber-1) varIndex = varIndex + 1;
			else varIndex = 0;
			formula = new AtomicFormula(variable[varIndex]);
			resultList.add(formula);
			number++;
		}
		int startIndex = 0;
		int endIndex = resultList.size()-1;
		int loopCounter = 0;
		final int MAX_LOOP_NUMBER = 10;
		while (endIndex > startIndex && loopCounter < MAX_LOOP_NUMBER) {
			loopCounter++;
			int index = startIndex;
			while (index < endIndex) {
				int operatorIndex = selectOperatorIndex(usedOperator, usedDistinct); 
				if (usedOperator[operatorIndex] == 0) usedDistinct++;
				usedOperator[operatorIndex]++;
				char selectedOperator = operator[operatorIndex];
				Formula right = resultList.get(index);
				Formula left = resultList.get(index+1);
				index = index + 2;
				
				Formula resultFormula = null;
				if (selectedOperator == Symbol.OPERATOR_NOT) { resultFormula = new NegFormula(right); index--; }
				else if (selectedOperator == Symbol.OPERATOR_AND) resultFormula = new AndFormula(left, right);
				else if (selectedOperator == Symbol.OPERATOR_OR) resultFormula = new OrFormula(left, right);
				else if (selectedOperator == Symbol.OPERATOR_IMP) resultFormula = new ImpFormula(left, right);
				else resultFormula = new BiImpFormula(left, right); 
				
				resultList.add(resultFormula);
			}
			
			if (index == endIndex) {
				// The formula at endIndex has not been used!
				startIndex = endIndex;
			} else startIndex = endIndex + 1;
			endIndex = resultList.size() - 1;
		}
//		System.out.println("Result List size = " + resultList.size() + ", endIndex = " + endIndex + ", loopCounter = " + loopCounter);
		return resultList.get(resultList.size()-1);
	}
*/	
	public static String randomGenerateFormulaControlledByOperatorNumber(int varNumber, int operatorNumber) {
		char[] variable = {'p', 'q', 'r', 's', 't', 'u', 'v', 'x', 'y', 'z'};
		char[] operator = {Symbol.OPERATOR_NOT, Symbol.OPERATOR_AND, Symbol.OPERATOR_OR, Symbol.OPERATOR_IMP, Symbol.OPERATOR_EQ}; 
		int[] usedOperator = {2, 0, 0, 0, 0};
		int usedDistinct = 1;
		if (varNumber >= variable.length) varNumber = variable.length;
		
		String leftBracket = "" + Symbol.LEFT_BRACKET;
		String rightBracket = "" + Symbol.RIGHT_BRACKET;
		
		String left = "";
		String right = "";
		
		int number = 0;
		while (number < operatorNumber-1) {
			String subformula = "";
			if (number < operatorNumber - 2) {
				int operatorIndex = selectOperatorIndex(usedOperator, usedDistinct); 
				if (usedOperator[operatorIndex] == 0) usedDistinct++;
				usedOperator[operatorIndex]++;
				int leftVariableIndex = (int)(Math.random() * varNumber);
				int rightVariableIndex = (int)(Math.random() * varNumber);
				if (rightVariableIndex == leftVariableIndex) rightVariableIndex = (leftVariableIndex + 1) % varNumber;
				
				if (operatorIndex > 0) {
					subformula =  leftBracket + variable[leftVariableIndex] + operator[operatorIndex] + variable[rightVariableIndex] + rightBracket;
				} else {
					subformula =  leftBracket + operator[operatorIndex] + variable[rightVariableIndex] + rightBracket;					
				}
			} else {
				int variableIndex = (int)(Math.random() * varNumber);
				subformula = subformula + variable[variableIndex];
			}
//			System.out.println("subformula = " + subformula);
			int sideChoice = (int)(Math.random() * 2);
			if (sideChoice < 1) {
				if (left.equals("")) {
					left = subformula;
					number = number+1;
				} else {
					int operatorIndex = selectOperatorIndex(usedOperator, usedDistinct); 
					if (usedOperator[operatorIndex] == 0) usedDistinct++;
					usedOperator[operatorIndex]++;
					if (operatorIndex > 0) {
						left = leftBracket + left + operator[operatorIndex] + subformula + rightBracket;
						number = number + 2;
					} else {
						left = leftBracket + operator[operatorIndex] + left + rightBracket;
						number = number +1;
					}
				}
			} else {
				if (right.equals("")) {
					right = subformula;
					number = number + 1;
				} else {
					int operatorIndex = selectOperatorIndex(usedOperator, usedDistinct); 
					if (usedOperator[operatorIndex] == 0) usedDistinct++;
					usedOperator[operatorIndex]++;
					if (operatorIndex > 0) {
						right = leftBracket + subformula + operator[operatorIndex] + right + rightBracket;
						number = number + 2;
					} else {
						right = leftBracket + operator[operatorIndex] + right + rightBracket;
						number = number +1;
					}
				}
			}
//			System.out.println("left = " + left + ", right = " + right);
		}
		if (left.equals("")) {
			int variableIndex = (int)(Math.random() * varNumber);
			left = left + variable[variableIndex];
		}
		if (right.equals("")) {
			int variableIndex = (int)(Math.random() * varNumber);
			right = right + variable[variableIndex];
		}
		int operatorIndex = selectOperatorIndex(usedOperator, usedDistinct); 
		if (usedOperator[operatorIndex] == 0) usedDistinct++;
		usedOperator[operatorIndex]++;
		String result = "";
		if (operatorIndex > 0) {
			result = leftBracket + left + operator[operatorIndex] + right + rightBracket;
		} else {
			int anotherOperatorIndex = selectOperatorIndex(usedOperator, usedDistinct);
			if (anotherOperatorIndex == 0) anotherOperatorIndex++;
			if (usedOperator[anotherOperatorIndex] == 0) usedDistinct++;
			usedOperator[anotherOperatorIndex]++;
			result = leftBracket + operator[operatorIndex] + leftBracket + left + operator[anotherOperatorIndex] + right + rightBracket + rightBracket;					
		}
		return result;
	}

	public static String randomGenerateFormulaLimitedToNegAndOrOperators(int varNumber, int operatorNumber) {
		char[] variable = {'p', 'q', 'r', 's', 't', 'u', 'v', 'x', 'y', 'z'};
		char[] operator = {Symbol.OPERATOR_NOT, Symbol.OPERATOR_AND, Symbol.OPERATOR_OR}; 
		int[] usedOperator = {2, 0, 0};
		int usedDistinct = 1;
		if (varNumber >= variable.length) varNumber = variable.length;
		
		String leftBracket = "" + Symbol.LEFT_BRACKET;
		String rightBracket = "" + Symbol.RIGHT_BRACKET;
		
		String left = "";
		String right = "";
		
		int number = 0;
		while (number < operatorNumber-1) {
			String subformula = "";
			if (number < operatorNumber - 2) {
				int operatorIndex = selectOperatorIndex(usedOperator, usedDistinct); 
				if (usedOperator[operatorIndex] == 0) usedDistinct++;
				usedOperator[operatorIndex]++;
				int leftVariableIndex = (int)(Math.random() * varNumber);
				int rightVariableIndex = (int)(Math.random() * varNumber);
				if (rightVariableIndex == leftVariableIndex) rightVariableIndex = (leftVariableIndex + 1) % varNumber;
				
				if (operatorIndex > 0) {
					subformula =  leftBracket + variable[leftVariableIndex] + operator[operatorIndex] + variable[rightVariableIndex] + rightBracket;
				} else {
					subformula =  leftBracket + operator[operatorIndex] + variable[rightVariableIndex] + rightBracket;					
				}
			} else {
				int variableIndex = (int)(Math.random() * varNumber);
				subformula = subformula + variable[variableIndex];
			}
//			System.out.println("subformula = " + subformula);
			int sideChoice = (int)(Math.random() * 2);
			if (sideChoice < 1) {
				if (left.equals("")) {
					left = subformula;
					number = number+1;
				} else {
					int operatorIndex = selectOperatorIndex(usedOperator, usedDistinct); 
					if (usedOperator[operatorIndex] == 0) usedDistinct++;
					usedOperator[operatorIndex]++;
					if (operatorIndex > 0) {
						left = leftBracket + left + operator[operatorIndex] + subformula + rightBracket;
						number = number + 2;
					} else {
						left = leftBracket + operator[operatorIndex] + left + rightBracket;
						number = number +1;
					}
				}
			} else {
				if (right.equals("")) {
					right = subformula;
					number = number + 1;
				} else {
					int operatorIndex = selectOperatorIndex(usedOperator, usedDistinct); 
					if (usedOperator[operatorIndex] == 0) usedDistinct++;
					usedOperator[operatorIndex]++;
					if (operatorIndex > 0) {
						right = leftBracket + subformula + operator[operatorIndex] + right + rightBracket;
						number = number + 2;
					} else {
						right = leftBracket + operator[operatorIndex] + right + rightBracket;
						number = number +1;
					}
				}
			}
//			System.out.println("left = " + left + ", right = " + right);
		}
		if (left.equals("")) {
			int variableIndex = (int)(Math.random() * varNumber);
			left = left + variable[variableIndex];
		}
		if (right.equals("")) {
			int variableIndex = (int)(Math.random() * varNumber);
			right = right + variable[variableIndex];
		}
		int operatorIndex = selectOperatorIndex(usedOperator, usedDistinct); 
		if (usedOperator[operatorIndex] == 0) usedDistinct++;
		usedOperator[operatorIndex]++;
		String result = "";
		if (operatorIndex > 0) {
			result = leftBracket + left + operator[operatorIndex] + right + rightBracket;
		} else {
			int anotherOperatorIndex = selectOperatorIndex(usedOperator, usedDistinct);
			if (anotherOperatorIndex == 0) anotherOperatorIndex++;
			if (usedOperator[anotherOperatorIndex] == 0) usedDistinct++;
			usedOperator[anotherOperatorIndex]++;
			result = leftBracket + operator[operatorIndex] + leftBracket + left + operator[anotherOperatorIndex] + right + rightBracket + rightBracket;					
		}
		return result;
	}
	
	private static int selectOperatorIndex(int[] usedOperator, int usedDistinct) {
		int index = 0;
		if (usedDistinct < 3) {
			index = (int)(Math.random() * usedOperator.length);
		} else {
			int minIndex = 0;
			for (int i = 1; i < usedOperator.length; i++) {
				if (usedOperator[i] < usedOperator[minIndex]) minIndex = i;
			}
			index = minIndex;
		}
		return index;
	}
}
