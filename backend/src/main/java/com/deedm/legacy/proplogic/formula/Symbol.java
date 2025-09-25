/**
 * 
 */
package com.deedm.legacy.proplogic.formula;

// import com.deedm.legacy.util.Configuration; - removed due to missing dependency

/**
 * Enumerate the symbols allowed in a propositional logic. 
 * @author Zhou Xiaocong
 * @date 2023/05/31
 *
 */
public class Symbol {
	public final static char END_FLAG = '#';
	public final static char OPERATOR_AND = '&';
	public final static char OPERATOR_NOT = '~';
	public final static char OPERATOR_OR = '|';
	public final static char OPERATOR_IMP = '>';
	public final static char OPERATOR_EQ = '=';
	public final static char LEFT_BRACKET = '(';
	public final static char RIGHT_BRACKET = ')';
	
	public final static String LATEX_AND = "\\wedge";
	public final static String LATEX_NOT = "\\neg";
	public final static String LATEX_OR = "\\vee";
	public final static String LATEX_IMP = "\\rightarrow";
	public final static String LATEX_EQ = "\\leftrightarrow";
	public final static int MAX_LATEX_LENGTH = 20;

	public static boolean isOperator(char ch) {
		if (ch == OPERATOR_AND || ch == OPERATOR_NOT || ch == OPERATOR_OR || ch == OPERATOR_IMP || ch == OPERATOR_EQ) return true;
		return false;
	}
	public static boolean isLeftBracket(char ch) {
		if (ch == LEFT_BRACKET) return true;
		else return false;
	}
	public static boolean isRightBracket(char ch) {
		if (ch == RIGHT_BRACKET) return true;
		else return false;
	}
	
	public static boolean isPriorTo(char operatorOne, char operatorTwo) {
		// operatorOne is in stack, and operatorTwo is out the stack
		if (operatorTwo == RIGHT_BRACKET) return true; 
		if (operatorOne == LEFT_BRACKET) return false;
		if (operatorTwo == LEFT_BRACKET) return false;
		if (operatorOne == OPERATOR_NOT) {
			if (operatorTwo == OPERATOR_NOT) return false;
			else return true;
		}
		if (operatorTwo == OPERATOR_NOT) return false;
		if (operatorOne == operatorTwo) {
			if (operatorOne != OPERATOR_IMP) return true;
			else return false;
		}
		if (operatorOne == OPERATOR_AND) return true;
		if (operatorTwo == OPERATOR_AND) return false;
		if (operatorOne == OPERATOR_OR) return true;
		if (operatorTwo == OPERATOR_OR) return false;
		if (operatorOne == OPERATOR_IMP) return true;
		if (operatorTwo == OPERATOR_IMP) return false;
		return true;
	}
	
	public static String getLaTeXOperator(char operator) {
		if (operator == OPERATOR_AND) return LATEX_AND;
		else if (operator == OPERATOR_OR) return LATEX_OR;
		else if (operator == OPERATOR_NOT) return LATEX_NOT;
		else if (operator == OPERATOR_IMP) return LATEX_IMP;
		else if (operator == OPERATOR_EQ) return LATEX_EQ;
		else return "Undefined";
	}
	
	public static String getOperatorImageFileName(char operator) {
		String fileName = null;
		if (operator == OPERATOR_AND) fileName = "wedge.png";
		else if (operator == OPERATOR_OR) fileName = "vee.png";
		else if (operator == OPERATOR_NOT) fileName = "neg.png";
		else if (operator == OPERATOR_IMP) fileName = "rightarrow.png";
		else fileName = "leftrightarrow.png";
		return "/images/" + fileName; // Simplified path instead of Configuration
	}
}
