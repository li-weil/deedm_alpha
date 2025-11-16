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
public class AndGroupIntegerFilter implements IntegerFilter {
	private List<IntegerFilter> filterList = null;
	
	public AndGroupIntegerFilter() {
		filterList = new ArrayList<IntegerFilter>();
	}

	public AndGroupIntegerFilter(IntegerFilter firstFilter) {
		filterList = new ArrayList<IntegerFilter>();
		filterList.add(firstFilter);
	}
	
	public boolean addFilter(IntegerFilter filter) {
		return filterList.add(filter);
	}

	@Override
	public boolean accept(int number) {
		for (IntegerFilter filter : filterList) {
			if (filter.accept(number) == false) return false;
		}
		return true;
	}

	@Override 
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		boolean isFirst = true;
		for (IntegerFilter filter : filterList) {
			if (isFirst == true) isFirst = false;
			else buffer.append(" and ");
			buffer.append(filter.toString());
		}
		return buffer.toString();
	}
	
	@Override
	public String toLaTeXString() {
		StringBuffer buffer = new StringBuffer();
		boolean isFirst = true;
		for (IntegerFilter filter : filterList) {
			if (isFirst == true) isFirst = false;
			else buffer.append(" \\wedge ");
			buffer.append(filter.toLaTeXString());
		}
		return buffer.toString();
	}
}
