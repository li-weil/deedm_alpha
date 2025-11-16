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
public class OrGroupStringFilter implements StringFilter {
	private List<StringFilter> filterList = null;

	public OrGroupStringFilter() {
		filterList = new ArrayList<StringFilter>();
	}
	
	public OrGroupStringFilter(StringFilter firstFilter) {
		filterList = new ArrayList<StringFilter>();
		filterList.add(firstFilter);
	}
	
	public boolean addFilter(StringFilter filter) {
		return filterList.add(filter);
	}

	@Override
	public boolean accept(char[] string) {
		for (StringFilter filter : filterList) {
			if (filter.accept(string) == true) return true;
		}
		return false;
	}

	@Override 
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		boolean isFirst = true;
		for (StringFilter filter : filterList) {
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
		for (StringFilter filter : filterList) {
			if (isFirst == true) isFirst = false;
			else buffer.append(" \\vee ");
			buffer.append(filter.toLaTeXString());
		}
		if (filterList.size() > 1) return "(" + buffer.toString() + ")";
		else return buffer.toString();
	}
}
