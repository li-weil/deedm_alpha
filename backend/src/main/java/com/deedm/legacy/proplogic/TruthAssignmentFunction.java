/**
 * 
 */
package com.deedm.legacy.proplogic;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zxc
 *
 */
public class TruthAssignmentFunction {
	private List<TruthAssignment> list = null;
	
	public TruthAssignmentFunction() {
		list = new ArrayList<TruthAssignment>();
	}
	
	public TruthAssignmentFunction(List<TruthAssignment> variableList) {
		list = new ArrayList<TruthAssignment>();
		for (TruthAssignment variable : variableList) {
			TruthAssignment copy = new TruthAssignment(variable.getVariable(), variable.getValue());
			list.add(copy);
		}
	}

	public boolean setTruthAssignment(char variable, boolean value) {
		for (TruthAssignment assignment : list) {
			if (assignment.getVariable() == variable) {
				assignment.setValue(value);
				return true;
			}
		}
		TruthAssignment assignment = new TruthAssignment(variable, value);
		return list.add(assignment);
	}
	
	public boolean getValue(char variable) {
		for (TruthAssignment assignment : list) {
			if (assignment.getVariable() == variable) {
				return assignment.getValue();
			}
		}
		return false;
	}
	
	public List<TruthAssignment> getTruthAssignmentList() {
		return list;
	}
	
	public TruthAssignmentFunction getNegationFunction() {
		if (list == null) return null;
		TruthAssignmentFunction result = new TruthAssignmentFunction();
		for (TruthAssignment assignment : list) {
			TruthAssignment resultAssignment = new TruthAssignment(assignment.getVariable(), !assignment.getValue());
			result.list.add(resultAssignment);
		}
		return result;
	}
	
	public String toString() {
		if (list == null) return "{}";
		StringBuffer result = new StringBuffer();
		result.append("{ ");
		boolean isFirst = true;
		for (TruthAssignment assignment : list) {
			if (isFirst) isFirst = false;
			else result.append(", ");
			if (assignment.getValue()) result.append(assignment.getVariable() + "=1");
			else result.append(assignment.getVariable() + "=0"); 
		}
		result.append(" }");
		return result.toString();
	}
	
	public static TruthAssignmentFunction randomAssignment(List<TruthAssignment> variableList) {
		TruthAssignmentFunction result = new TruthAssignmentFunction(variableList);
		for (TruthAssignment assignment : result.list) {
			int value = (int)(Math.random() * 10);
			if (value > 4) assignment.setValue(true);
			else assignment.setValue(false);
		}
		return result;
	}
}
