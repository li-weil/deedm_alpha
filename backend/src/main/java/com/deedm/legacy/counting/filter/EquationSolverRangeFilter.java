/**
 * 
 */
package counting.filter;

/**
 * @author Zhou Xiaocong
 *
 */
public class EquationSolverRangeFilter implements EquationSolverFilter {
	private int variableIndex = -1;
	private int min = -1;
	private int max = -1;
	
	public EquationSolverRangeFilter(int variableIndex, int min, int max) {
		this.variableIndex = variableIndex;
		this.min = min;
		this.max = max;
	}

	public boolean accept(int[] string) {
//		System.out.println("Range filter : index = " + variableIndex + ", min = " + min + ", max = " + max);
		if (variableIndex < 1 || variableIndex > string.length) return true;
		if (min >= 0) {
			if (string[variableIndex-1] < min) return false;
		}
		if (max >= 0) {
			if (string[variableIndex-1] > max) return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		if (min >= 0) buffer.append(min + " <= ");
		buffer.append("x_{" + variableIndex + "}");
		if (max >= 0) buffer.append(" <= " + max);
		return buffer.toString();
	}
	
	@Override
	public String toLaTeXString() {
		StringBuffer buffer = new StringBuffer();
		if (min >= 0) buffer.append(min + " \\leq ");
		buffer.append("x_{" + variableIndex + "}");
		if (max >= 0) buffer.append(" \\leq " + max);
		return buffer.toString();
	}
}
