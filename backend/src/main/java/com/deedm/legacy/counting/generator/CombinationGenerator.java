/**
 * 
 */
package com.deedm.legacy.counting.generator;

/**
 * @author user
 *
 */
public class CombinationGenerator extends StringGenerator {

	public CombinationGenerator(char[] base, int length) {
		super(base, 0);
		if (length > base.length) string = new byte[base.length];
		else string = new byte[length];
	}

	public void first() {
		for (int i = 0; i < string.length; i++) string[i] = (byte)i;
	}
	
	public void next() {
		int r = string.length;
		int n = base.length;
		int i = r-1;
		while (string[i] == n-r+i) i = i - 1;
		string[i] = (byte)(string[i] + 1);
		for (int j = i+1; j < r; j++) string[j] = (byte)(string[i] + j - i);
	}
	
	public boolean isLast() {
		for (int i = 0; i < string.length; i++) {
			if (string[i] != base.length-string.length+i) return false;
		}
		return true;
	}
}
