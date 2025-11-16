/**
 * 
 */
package com.deedm.legacy.counting.filter;

/**
 * @author user
 *
 */
public interface IntegerFilter {
	public boolean accept(int number);
	
	public String toString();
	
	public String toLaTeXString();
}
