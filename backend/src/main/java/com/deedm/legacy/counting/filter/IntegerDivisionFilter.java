/**
 * 
 */
package counting.filter;

/**
 * @author user
 *
 */
public class IntegerDivisionFilter implements IntegerFilter {
	public static final int DIVIDED = 0;
	public static final int NOT_DIVIDED = 1;
	
	private int[] dividens = null;
	private int flag = DIVIDED;
	
	public IntegerDivisionFilter(int flag, int[] dividens) {
		this.flag = flag;
		this.dividens = dividens;
	}

	@Override
	public boolean accept(int number) {
		if (dividens == null) return true;
		if (dividens.length <= 0) return true;
		boolean found = false;
		for (int i = 0; i < dividens.length; i++) {
			if (number % dividens[i] == 0) {
				found = true;
				break;
			}
		}
		if (flag == DIVIDED) return (found == true);
		else return (found == false);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < dividens.length; i++) {
			if (i > 0) buffer.append(" || ");
			buffer.append("n % " + dividens[i]);
			if (flag == DIVIDED) buffer.append(" == 0");
			else buffer.append(" != 0");
		}
		return buffer.toString();
	}
	
	@Override
	public String toLaTeXString() {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < dividens.length; i++) {
			if (i > 0) {
				if (flag == DIVIDED) buffer.append(" \\vee ");
				else buffer.append(" \\wedge ");
			}
			buffer.append("n\\quad\\%\\quad " + dividens[i]);
			if (flag == DIVIDED) buffer.append(" = 0");
			else buffer.append(" \\neq 0");
		}
		return buffer.toString();
	}
}
