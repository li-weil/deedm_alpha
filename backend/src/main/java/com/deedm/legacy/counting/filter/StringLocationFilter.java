/**
 * 
 */
package com.deedm.legacy.counting.filter;

/**
 * @author Zhou Xiaocong
 * @since 2020/10/28
 *  
 * 一个StringLocationFilter对象给出对要计算的字符串 char[] string的位置上的一个约束过滤条件：第i个位置必须出现或者必须不出现某些字符之一
 */
public class StringLocationFilter implements StringFilter {
	public static final int APPEAR = 1;
	public static final int NOT_APPEAR = -1;
	private int location = -1;			// given the location
	private char[] charArray = null;	// given the characters which appear or not appear in the string. 
	private int flag = APPEAR;
	
	public StringLocationFilter(int flag, int location, char[] charArray) {
		this.flag = flag;
		this.location = location;
		this.charArray = charArray;
	}

	@Override
	public boolean accept(char[] string) {
		if (location < 0 || location >= string.length) return true;
		if (charArray == null) return true;
		if (charArray.length <= 0) return true;
		if (flag != APPEAR && flag != NOT_APPEAR) return true;
		
		boolean found = false;
		for (int i = 0; i < charArray.length; i++) {
			if (charArray[i] == string[location]) {
				found = true;  break;
			}
		}
		if (flag == APPEAR) return (found == true);
		else return (found == false);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("string[" + location + "] is ");
		if (flag == NOT_APPEAR) buffer.append(" NOT ");
		buffer.append(" one of (");
		for (int i = 0; i < charArray.length; i++) {
			if (i > 0) buffer.append(", ");
			buffer.append(charArray[i]);
		}
		buffer.append(")");
		return buffer.toString();
	}
	
	@Override
	public String toLaTeXString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\\textrm{string}[" + location + "] \\textrm{ is }");
		if (flag == NOT_APPEAR) buffer.append(" \\textrm{NOT }");
		if (charArray.length > 1) buffer.append(" \\textrm{one of } (");
		else buffer.append("(");
		for (int i = 0; i < charArray.length; i++) {
			if (i > 0) buffer.append(", ");
			buffer.append(charArray[i]);
		}
		buffer.append(")");
		return buffer.toString();
	}
}
