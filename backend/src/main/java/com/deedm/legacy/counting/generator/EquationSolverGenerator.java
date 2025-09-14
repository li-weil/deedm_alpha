/**
 * 
 */
package counting.generator;

/**
 * @author user
 *
 */
public class EquationSolverGenerator {
	protected int sum = 1;
	protected int[] solver = null;
	protected RepetitionCombinationGenerator generator = null;
	
	public EquationSolverGenerator(int variableNumber, int sum) {
		this.sum = sum;
		solver = new int[variableNumber];
		char[] base = new char[variableNumber];
		for (int i = 0; i < base.length; i++) {
			base[i] = (char)('0' + (i+1));
		}
		generator = new RepetitionCombinationGenerator(base, sum);
	}
	
	public int[] current() {
		return solver;
	}
	
	
	public void first() {
		generator.first();
		char[] string = generator.current();
		covertStringToCurrentSolver(string);
	}
	
	public void next() {
		generator.next();
		char[] string = generator.current();
		covertStringToCurrentSolver(string);
	}
	
	public boolean isLast() {
		return generator.isLast();
	}

	public static String convertSolverToString(int[] solver) {
		StringBuffer results = new StringBuffer("");
		for (int i = 0; i < solver.length; i++) {
			results.append("x_" + (i+1) + " = " + solver[i]);
			if (i != solver.length-1) results.append(", ");
		}
		return results.toString();
	}
	
	private void covertStringToCurrentSolver(char[] string) {
		int counter = 0;
		char currentChar = '0';
		int solverIndex = 0;
		for (int i = 0; i < solver.length; i++) solver[i] = 0;

		for (int i = 0; i < string.length; i++) {
			if (string[i] != currentChar) {
				if (currentChar != '0') {
					solverIndex = currentChar - '0' - 1;
					solver[solverIndex] = counter;
				}
				currentChar = string[i];
				counter = 1;
			} else counter++;
		}
		if (currentChar != '0') {
			solverIndex = currentChar - '0' - 1;
			solver[solverIndex] = counter;
		}
	}
}
