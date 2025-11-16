/**
 * 
 */
package com.deedm.legacy.counting.filter;

/**
 * @author Zhou Xiaocong
 * @since 2020/10/28
 * 
 * 如果当前串 string 含有重复的字符则不接受（返回flase），否则接受（返回true）
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
