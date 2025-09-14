/**
 * 
 */
package counting.filter;

/**
 * @author Zhou Xiaocong
 * @since 2020/10/28
 * 
 * �����ǰ�� string �����ظ����ַ��򲻽��ܣ�����flase����������ܣ�����true��
 *
 */
public class StringRepetitionFilter implements StringFilter {

	public StringRepetitionFilter() {
	}

	@Override
	public boolean accept(char[] string) {
		for (int i = 0; i < string.length; i++) 
			for (int j = i+1; j < string.length; j++) 
				if (string[i] == string[j]) return false;
		return true;
	}

	@Override
	public String toString() {
		return "NO Repetition";
	}
	
	@Override
	public String toLaTeXString() {
		return "NO Repetition";
	}
}
