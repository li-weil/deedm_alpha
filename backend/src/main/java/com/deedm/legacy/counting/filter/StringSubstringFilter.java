/**
 * 
 */
package counting.filter;

/**
 * @author Zhou Xiaocong
 * @since 2020/10/28
 * 
 * һ��StringSubstringFilter���������Ҫ������ַ��� char[] string�����ֵ��Ӵ���һ��Լ������������ǡ�ã������٣������ࣩ����n��ĳЩ�ַ���֮һ����n=0�������ࡱ�͡�ǡ�á�������������Щ�ַ����е�����һ������
 */
public class StringSubstringFilter implements StringFilter {
	public static final int NOT_CONTAIN = 2;   // ���ܰ��� number �������Ϊsubstrings���Ӵ������Ͳ����� number ����
	public static final int EXACTLY = 0;    // ǡ�ú���... i.e. == number
	public static final int AT_LEAST = 1;  // ���ٺ���.... i.e. >= number
	public static final int NO_MORE_THAN = -1; // ���ຬ��... i.e. <= number
	
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
