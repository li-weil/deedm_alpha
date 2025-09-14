/**
 * 
 */
package com.deedm.legacy.proplogic;

import java.util.ArrayList;
import java.util.List;

import dataTable.DataTableManager;
import dataTable.DataTableUtil;
import proplogic.formula.Formula;
import proplogic.formula.AtomicFormula;

/**
 * @author zxc
 *
 */
public class FormulaTruthTable {
	private Formula formula = null;
	private char[] variables = null;
	private List<TruthAssignmentFunction> functionList = null;
	private boolean[] resultValues = null;
	
	public FormulaTruthTable(Formula formula, char[] variables, List<TruthAssignmentFunction> functionList) {
		this.formula = formula;
		this.variables = variables;
		this.functionList = functionList;
		calculateTruthValues();
	}
	
	public FormulaTruthTable(Formula formula, char[] variables) {
		this.formula = formula;
		this.variables = variables;
		this.functionList = createAllTruthAssignmentFunction(variables);
		calculateTruthValues();
	}

	public FormulaTruthTable(Formula formula, List<TruthAssignmentFunction> functionList) {
		this.formula = formula;
		this.variables = formula.getAllVariables();
		this.functionList = functionList;
		calculateTruthValues();
	}

	public FormulaTruthTable(Formula formula, TruthAssignmentFunction function) {
		this.formula = formula;
		this.variables = formula.getAllVariables();
		this.functionList = new ArrayList<TruthAssignmentFunction>();
		this.functionList.add(function);
		calculateTruthValues();
	}

	public FormulaTruthTable(Formula formula) {
		this.formula = formula;
		this.variables = formula.getAllVariables();
		this.functionList = createAllTruthAssignmentFunction(variables);
		calculateTruthValues();
	}
	
	public DataTableManager getSimpleTruthTableManager() {
		DataTableManager resultTable = new DataTableManager("truthtable");
		
		List<TruthAssignment> variableList = formula.getAllVariablesForAssignmentTruth();
		List<Formula> tableColumnFormulas = new ArrayList<Formula>();
		
		for (TruthAssignment variable : variableList) {
			Formula subformula = new AtomicFormula(variable.getVariable());
			tableColumnFormulas.add(subformula);
		}
		tableColumnFormulas.add(formula);
		
		String[] columnNames = new String[variables.length+1];
		for (int i = 0; i < variables.length; i++) {
			columnNames[i] = "$" + variables[i] + "$";
		}
		columnNames[columnNames.length-1] = "$" + formula.toSimpleLaTeXString() + "$";
		resultTable.setColumnNames(columnNames);
		
		int resultValueIndex = 0;
		for (TruthAssignmentFunction function : functionList) {
			String[] tableLine = new String[columnNames.length];
			for (int i = 0; i < variables.length; i++) {
				boolean value = false;
				for (TruthAssignment assignment : function.getTruthAssignmentList()) {
					if (assignment.getVariable() == variables[i]) {
						value = assignment.getValue();
						break;
					}
				}
				if (value == true) {
					tableLine[i] = "$" + "\\mathbf{1}" + "$";
				} else tableLine[i] = "$" + "\\mathbf{0}" + "$";
			}
			if (resultValues[resultValueIndex] == true) {
				tableLine[columnNames.length-1] = "$" + "\\mathbf{1}" + "$";
			} else tableLine[columnNames.length-1] = "$" + "\\mathbf{0}" + "$";
			resultValueIndex++;
			
			resultTable.appendLine(tableLine);
		}
		return resultTable;
	}
	
	public String createSimpleTruthTable() {
		String result = null;

		DataTableManager resultTable = getSimpleTruthTableManager();
		String[] columnNames = resultTable.getColumnNameArray();
		
		result = DataTableUtil.getJMathLaTeXString(resultTable, columnNames);
		return result;
	}

	public DataTableManager getDetailedTruthTableManager() {
		DataTableManager resultTable = new DataTableManager("truthtable");
		
		List<Formula> subFormulaList = formula.getAllSyntaxDifferentSubFormulas();
		List<Formula> tableColumnFormulas = new ArrayList<Formula>();
		
		for (int i = 0; i < variables.length; i++) {
			Formula subformula = new AtomicFormula(variables[i]);
			tableColumnFormulas.add(subformula);
		}
		for (Formula subformula : subFormulaList) {
			if (!subformula.isAtomicFormula()) tableColumnFormulas.add(subformula);
		}
		String[] columnNames = new String[tableColumnFormulas.size()];
		for (int i = 0; i < tableColumnFormulas.size(); i++) {
			Formula subformula = tableColumnFormulas.get(i);
			columnNames[i] = "$" + subformula.toSimpleLaTeXString() + "$";
		}
		
		resultTable.setColumnNames(columnNames);
		for (TruthAssignmentFunction function : functionList) {
			String[] tableLine = new String[tableColumnFormulas.size()];
			for (int i = 0; i < tableColumnFormulas.size(); i++) {
				Formula subformula = tableColumnFormulas.get(i);
				boolean value = subformula.getTruthValue(function);
				if (value == true) {
					tableLine[i] = "$" + "\\mathbf{1}" + "$";
				} else tableLine[i] = "$" + "\\mathbf{0}" + "$";
			}
			resultTable.appendLine(tableLine);
		}
		return resultTable;
	}
	
	public String createDetailedTruthTable() {
		String result = null;

		DataTableManager resultTable = getDetailedTruthTableManager();
		String[] columnNames = resultTable.getColumnNameArray();
		
		result = DataTableUtil.getJMathLaTeXString(resultTable, columnNames);
		return result;
	}

	public boolean isAllTruthAssignment() {
		for (int i = 0; i < resultValues.length; i++) {
			if (resultValues[i] == false) return false;
		}
		return true;
	}
	
	public boolean hasTruthAssignment() {
		for (int i = 0; i < resultValues.length; i++) {
			if (resultValues[i] == true) return true;
		}
		return false;
	}

	public List<TruthAssignmentFunction> getTrueAssignmentList() {
		List<TruthAssignmentFunction> resultList = new ArrayList<TruthAssignmentFunction>();
		int resultValueIndex = 0;
		for (TruthAssignmentFunction function : functionList) {
			if (resultValues[resultValueIndex] == true) resultList.add(function);
			resultValueIndex++;
		}
		return resultList;
	}
	
	public List<TruthAssignmentFunction> getFalseAssignmentList() {
		List<TruthAssignmentFunction> resultList = new ArrayList<TruthAssignmentFunction>();
		int resultValueIndex = 0;
		for (TruthAssignmentFunction function : functionList) {
			if (resultValues[resultValueIndex] == false) resultList.add(function);
			resultValueIndex++;
		}
		return resultList;
	}

	public TruthAssignmentFunction getFirstTrueAssignment() {
		int resultValueIndex = 0;
		for (TruthAssignmentFunction function : functionList) {
			if (resultValues[resultValueIndex] == true) return function;
			resultValueIndex++;
		}
		return null;
	}
	
	public TruthAssignmentFunction getFirstFalseAssignment() {
		int resultValueIndex = 0;
		for (TruthAssignmentFunction function : functionList) {
			if (resultValues[resultValueIndex] == false) return function;
			resultValueIndex++;
		}
		return null;
	}
	
	protected List<TruthAssignmentFunction> createAllTruthAssignmentFunction(char[] variables) {
		List<TruthAssignmentFunction> result = new ArrayList<TruthAssignmentFunction>();
		boolean[] code = new boolean[variables.length];
		for (int i = 0; i < code.length; i++) code[i] = false;
		
		while (true) {
			List<TruthAssignment> assignmentList = new ArrayList<TruthAssignment>();
			for (int i = 0; i < variables.length; i++) {
				TruthAssignment assignment = new TruthAssignment(variables[i], code[i]);
				assignmentList.add(assignment);
			}
			TruthAssignmentFunction function = new TruthAssignmentFunction(assignmentList);
			result.add(function);
			
			int i = code.length-1;
			for (i = code.length-1; i >= 0; i--) {
				if (code[i] == true) code[i] = false;
				else break;
			}
			if (i >= 0) code[i] = true;
			else break;
		}
		return result;
	}
	
	protected void calculateTruthValues() {
		resultValues = new boolean[functionList.size()];
		int resultIndex = 0;
		for (TruthAssignmentFunction function : functionList) {
			boolean value = formula.getTruthValue(function);
			resultValues[resultIndex] = value;
			resultIndex++;
		}
	}
}
