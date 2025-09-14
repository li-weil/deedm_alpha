/**
 * 
 */
package counting.generator;

/**
 * @author user
 *
 */
public class RepetitionCombinationGenerator extends StringGenerator {

	public RepetitionCombinationGenerator(char[] base, int length) {
		super(base, length);
	}

	public void first() {
		for (int i = 0; i < string.length; i++) string[i] = 0;
	}
	
	public void next() {
		int r = string.length;
		int n = base.length;
		int i = r-1;
		while (string[i] == n-1) i = i - 1;
		string[i] = (byte)(string[i] + 1);
		for (int j = i+1; j < r; j++) string[j] = string[i];
	}
	
	public boolean isLast() {
		for (int i = 0; i < string.length; i++) {
			if (string[i] != base.length-1) return false;
		}
		return true;
	}
}
