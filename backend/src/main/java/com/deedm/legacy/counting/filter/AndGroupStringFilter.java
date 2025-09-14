/**
 * 
 */
package counting.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zhou Xiaocong
 * @since 2020/10/28
 * 
 * һ�� DefaultStringFilter ����һϵ�е����� StringFilter����ֻ����Щ StringFilter �����ܵ�ǰ���ַ���ʱ���Ž�������ַ���
 *
 */
public class AndGroupStringFilter implements StringFilter {
	private List<StringFilter> filterList = null;

	public AndGroupStringFilter() {
		filterList = new ArrayList<StringFilter>();
	}
	
	public AndGroupStringFilter(StringFilter firstFilter) {
		filterList = new ArrayList<StringFilter>();
		filterList.add(firstFilter);
	}
	
	public boolean addFilter(StringFilter filter) {
		return filterList.add(filter);
	}

	@Override
	public boolean accept(char[] string) {
		for (StringFilter filter : filterList) {
			if (!filter.accept(string)) return false;
		}
		return true;
	}
	
	@Override 
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		boolean isFirst = true;
		for (StringFilter filter : filterList) {
			if (isFirst == true) isFirst = false;
			else buffer.append(" and ");
			buffer.append(filter.toString());
		}
		return buffer.toString();
	}
	
	@Override
	public String toLaTeXString() {
		StringBuffer buffer = new StringBuffer();
		boolean isFirst = true;
		for (StringFilter filter : filterList) {
			if (isFirst == true) isFirst = false;
			else buffer.append(" \\wedge ");
			buffer.append(filter.toLaTeXString());
		}
		return buffer.toString();
	}
}
