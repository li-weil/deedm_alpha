/**
 * 
 */
package com.deedm.legacy.counting.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author user
 *
 */
public class OrGroupEquationSolverFilter implements EquationSolverFilter {
	private List<EquationSolverFilter> filterList = null;

	public OrGroupEquationSolverFilter() {
		filterList = new ArrayList<EquationSolverFilter>();
	}

	public OrGroupEquationSolverFilter(EquationSolverFilter firstFilter) {
		filterList = new ArrayList<EquationSolverFilter>();
		filterList.add(firstFilter);
	}

	public boolean addFilter(EquationSolverFilter filter) {
		return filterList.add(filter);
	}

	@Override
	public boolean accept(int[] solver) {
		for (EquationSolverFilter filter : filterList) {
			if (filter.accept(solver) == true) return true;
		}
		return false;
	}

	@Override 
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		boolean isFirst = true;
		for (EquationSolverFilter filter : filterList) {
			if (isFirst == true) isFirst = false;
			else buffer.append(" or ");
			buffer.append(filter.toString());
		}
		if (filterList.size() > 1) return "(" + buffer.toString() + ")";
		else return buffer.toString();
	}
	
	@Override
	public String toLaTeXString() {
		StringBuffer buffer = new StringBuffer();
		boolean isFirst = true;
		for (EquationSolverFilter filter : filterList) {
			if (isFirst == true) isFirst = false;
			else buffer.append(" \\vee ");
			buffer.append(filter.toLaTeXString());
		}
		if (filterList.size() > 1) return "(" + buffer.toString() + ")";
		else return buffer.toString();
	}
}
