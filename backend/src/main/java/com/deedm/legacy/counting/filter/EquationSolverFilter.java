/**
 * 
 */
package counting.filter;

/**
 * @author user
 *
 */
public interface EquationSolverFilter {
	public boolean accept(int[] string);
	
	public String toString();
	
	public String toLaTeXString();
}
