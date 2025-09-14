/**
 * 
 */
package counting.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author user
 *
 */
public class OrGroupIntegerFilter implements IntegerFilter {
	private List<IntegerFilter> filterList = null;

	public OrGroupIntegerFilter() {
		filterList = new ArrayList<IntegerFilter>();
	}
	
	public OrGroupIntegerFilter(IntegerFilter firstFilter) {
		filterList = new ArrayList<IntegerFilter>();
		filterList.add(firstFilter);
	}
	
	public boolean addFilter(IntegerFilter filter) {
		return filterList.add(filter);
	}

	@Override
	public boolean accept(int number) {
		for (IntegerFilter filter : filterList) {
			if (filter.accept(number) == true) return true;
		}
		return false;
	}

	@Override 
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		boolean isFirst = true;
		for (IntegerFilter filter : filterList) {
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
		for (IntegerFilter filter : filterList) {
			if (isFirst == true) isFirst = false;
			else buffer.append(" \\vee ");
			buffer.append(filter.toLaTeXString());
		}
		if (filterList.size() > 1) return "(" + buffer.toString() + ")";
		else return buffer.toString();
	}
}
