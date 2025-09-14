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
public class AndGroupEquationSolverFilter implements EquationSolverFilter {
	private List<EquationSolverFilter> filterList = null;
	
	public AndGroupEquationSolverFilter(int[] minValue, int[] maxValue) {
		filterList = new ArrayList<EquationSolverFilter>();
		for (int i = 0; i < minValue.length || i < maxValue.length; i++) {
			int min = -1;
			int max = -1;
			if (i < minValue.length) min = minValue[i];
			if (i < maxValue.length) max = maxValue[i];
			filterList.add(new EquationSolverRangeFilter(i, min, max));
		}
	}
	
	public AndGroupEquationSolverFilter() {
		filterList = new ArrayList<EquationSolverFilter>();
	}

	public AndGroupEquationSolverFilter(EquationSolverFilter firstFilter) {
		filterList = new ArrayList<EquationSolverFilter>();
		filterList.add(firstFilter);
	}
	
	public boolean addFilter(EquationSolverFilter filter) {
		return filterList.add(filter);
	}

	@Override
	public boolean accept(int[] solver) {
		for (EquationSolverFilter filter : filterList) {
			if (filter.accept(solver) == false) return false;
		}
		return true;
	}

	@Override 
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		boolean isFirst = true;
		for (EquationSolverFilter filter : filterList) {
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
		for (EquationSolverFilter filter : filterList) {
			if (isFirst == true) isFirst = false;
			else buffer.append(" \\wedge ");
			buffer.append(filter.toLaTeXString());
		}
		return buffer.toString();
	}

}
