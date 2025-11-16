/**
 * 
 */
package com.deedm.legacy.counting;

import java.io.PrintStream;

import com.deedm.legacy.counting.filter.EquationSolverFilter;
import com.deedm.legacy.counting.generator.EquationSolverGenerator;

/**
 * @author user
 *
 */
public class EquationSolverCounter {
	private EquationSolverGenerator generator = null;
	private PrintStream writer = null;
	private boolean onlyPrintAccepted = false;
	
	public EquationSolverCounter(int variableNumber, int sum) {
		generator = new EquationSolverGenerator(variableNumber, sum);
	}
	
	public EquationSolverCounter(EquationSolverGenerator generator) {
		this.generator = generator;
	}
	
	public void setWriter(PrintStream writer) {
		this.writer = writer;
	}

	public void setWriter(PrintStream writer, boolean onlyPrintAccepted) {
		this.writer = writer;
		this.onlyPrintAccepted = onlyPrintAccepted;
	}
	
	public int counting(EquationSolverFilter filter) {
		generator.first();
		int result = 0;
		while (true) {
			int[] string = generator.current();
			if (filter.accept(string)) {
				result++;
				if (writer != null) writer.println(EquationSolverGenerator.convertSolverToString(string) + ", accept " + result);
			} else {
				if (writer != null && !onlyPrintAccepted) writer.println(EquationSolverGenerator.convertSolverToString(string) + ", NOT accept!");
			}
			if (generator.isLast()) break;
			generator.next();
		}

		if (writer != null) writer.println("Total " + result + " solvers for filter condition " + filter);
		return result;
	}
}
