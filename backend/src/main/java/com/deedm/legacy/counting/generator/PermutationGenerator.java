/**
 * 
 */
package counting.generator;

/**
 * @author user
 *
 */
public class PermutationGenerator extends StringGenerator {
	// Use a combination generator to get a combination of C(base.length, length) for generating all permutations of P(base.length, length)
	private CombinationGenerator combGenerator = null;		  
	
	public PermutationGenerator(char[] base, int length) {
		super(base, 0);
		if (length > base.length) string = new byte[base.length];
		else string = new byte[length];
		combGenerator = new CombinationGenerator(base, length);
	}

	public PermutationGenerator(char[] base) {
		super(base, 0);
		string = new byte[base.length];
		combGenerator = new CombinationGenerator(base, base.length);
	}
	
	public void first() {
		// The first permutation of P(base.length, string.length) is the string of the first combination.
		combGenerator.first();
		
		for (int i = 0; i < string.length; i++) string[i] = combGenerator.string[i];
	}
	
	public void next() {
		if (isLastStringOfCurrentCombination()) {
			// We have generated all permutations of the current combination, and then we get the next combination, and the next permutation is the string
			// of this combination.
			if (combGenerator.isLast()) return;
			combGenerator.next();
			for (int i = 0; i < string.length; i++) string[i] = combGenerator.string[i];
			return;
		}
		
		// The following statements get the next permutation of the current combination, according to Algorithm 8.1 in the textbook. However, 
		// we should note that the index of the Java array is from 0 to array.length-1.
		int i = string.length-2;
		while (string[i] > string[i+1]) i = i - 1;
		int j = string.length-1;
		while (string[i] > string[j]) j = j - 1;
		byte temp = string[i];
		string[i] = string[j];
		string[j] = temp;
		int r = string.length-1;
		int s = i+1;
		while (r > s) {
			temp = string[r];
			string[r] = string[s];
			string[s] = temp;
			r = r - 1;
			s = s + 1;
		}
	}
	
	public boolean isLast() {
		return combGenerator.isLast() && isLastStringOfCurrentCombination();
	}
	
	private boolean isLastStringOfCurrentCombination() {
		for (int i = 0; i < string.length-1; i++) 
			if (string[i] < string[i+1]) return false;
		return true;
	}
}
