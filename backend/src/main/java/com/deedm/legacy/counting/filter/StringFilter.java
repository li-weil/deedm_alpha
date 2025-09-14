/**
 * 
 */
package counting.filter;

/**
 * @author xiaocong
 *
 */
public interface StringFilter {
	public boolean accept(char[] string);
	
	public String toString();
	
	public String toLaTeXString();
}
