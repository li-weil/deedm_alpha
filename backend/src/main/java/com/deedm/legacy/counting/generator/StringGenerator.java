package counting.generator;

/**
 * @author Lenovo
 *
 */
public class StringGenerator {
	protected char[] base = {'0', '1'};
	protected byte[] string = null;

	public StringGenerator(char[] base, int length) {
		this.base = base;
		if (length > 0) string = new byte[length]; 
	}
	
	public char[] getBase() {
		return base;
	}
	
	public int getStringLength() {
		if (string == null) return 0;
		return string.length;
	}
	
	public char[] current() {
		char[] contents = new char[string.length];
		for (int i = 0; i < string.length; i++) {
			contents[i] = base[string[i]];
		}
		return contents;
	}
	
	public void first() {
		for (int i = 0; i < string.length; i++) string[i] = 0;
	}
	
	public void next() {
		for (int i = string.length-1; i >= 0; i--) {
			int sum = string[i] + 1;
			if (sum < base.length) {
				string[i] = (byte)sum;
				return;
			}
			string[i] = 0;
		}
	}
	
	public boolean isLast() {
		for (int i = 0; i < string.length; i++) {
			if (string[i] != base.length-1) return false;
		}
		return true;
	}

	public static String convertToString(char[] contents) {
		StringBuffer results = new StringBuffer("");
		for (int i = 0; i < contents.length; i++) {
			results.append(contents[i]);
		}
		return results.toString();
	}
}
