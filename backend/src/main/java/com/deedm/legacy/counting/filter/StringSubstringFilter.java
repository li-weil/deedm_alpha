/**
 * 
 */
package counting.filter;

/**
 * @author Zhou Xiaocong
 * @since 2020/10/28
 * 
 * 一个StringSubstringFilter对象给出对要计算的字符串 char[] string所出现的子串的一个约束过滤条件：恰好（或至少，或至多）含有n个某些字符串之一（若n=0，则“至多”和“恰好”都代表不包含这些字符串中的任意一个）。
 */
public class StringSubstringFilter implements StringFilter {
	public static final int NOT_CONTAIN = 2;   // 不能包含 number 个，理解为substrings中子串个数和不等于 number 个。
	public static final int EXACTLY = 0;    // 恰好含有... i.e. == number
	public static final int AT_LEAST = 1;  // 至少含有.... i.e. >= number
	public static final int NO_MORE_THAN = -1; // 至多含有... i.e. <= number
	
	public int flag = EXACTLY; 
	public int number = 0;
	public char[][] substrings = null;
	
	public StringSubstringFilter(int flag, int number, char[][] substrings) {
		this.flag = flag;
		this.number = number;
		this.substrings = substrings;
	}

	public StringSubstringFilter(int flag, int number, String[] substrings) {
		this.flag = flag;
		this.number = number;
		this.substrings = new char[substrings.length][];
		for (int i = 0; i < substrings.length; i++) {
			this.substrings[i] = new char[substrings[i].length()];
			for (int j = 0; j < substrings[i].length(); j++) this.substrings[i][j] = substrings[i].charAt(j);
		}
	}
	

	@Override
	public boolean accept(char[] string) {
		if (substrings == null) return true;
		if (substrings.length <= 0) return true;
		int counter = 0;
		
		for (int j = 0; j < string.length; j++) {
			for (int i = 0; i < substrings.length; i++) {
				boolean matched = true;
				int k = 0;
				while (k < substrings[i].length) {
					if (j + k >= string.length) {
						matched = false;
						break;
					}
					if (string[j+k] != substrings[i][k]) {
						matched = false;
						break;
					}
					k = k + 1;
				}
				if (matched) counter = counter + 1;
			}
		}
		if (flag == EXACTLY && counter == number) return true;
		if (flag == AT_LEAST && counter >= number) return true;
		if (flag == NO_MORE_THAN && counter <= number) return true;
		if (flag == NOT_CONTAIN && counter != number) return true;
		return false;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		if (flag == NOT_CONTAIN) buffer.append("do NOT ");
		buffer.append("contain ");
		if (flag == EXACTLY) buffer.append("exactly ");
		else if (flag == AT_LEAST) buffer.append("at least ");
		else if (flag == NO_MORE_THAN) buffer.append("no more than ");
		buffer.append(number + " substring that is one of (");
		for (int i = 0; i < substrings.length; i++) {
			if (i > 0) buffer.append(", ");
			buffer.append(substrings[i]);
		}
		buffer.append(")");
		return buffer.toString();
	}
	
	@Override
	public String toLaTeXString() {
		StringBuffer buffer = new StringBuffer();
		if (flag == NOT_CONTAIN) buffer.append("\\textrm{do NOT } ");
		buffer.append("\\textrm{contain } ");
		if (flag == EXACTLY) buffer.append("\\textrm{exactly } ");
		else if (flag == AT_LEAST) buffer.append("\\textrm{at least } ");
		else if (flag == NO_MORE_THAN) buffer.append("\\textrm{no more than } ");
		if (substrings.length > 1) buffer.append(number + "\\textrm{ substring(s) that is one of } (");
		else buffer.append(number + " \\textrm{ substring(s) that is } (");
		for (int i = 0; i < substrings.length; i++) {
			if (i > 0) buffer.append(", ");
			buffer.append(substrings[i]);
		}
		buffer.append(")");
		return buffer.toString();
	}
}
