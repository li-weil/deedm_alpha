/**
 * 
 */
package com.deedm.legacy.counting.generator;

import java.util.ArrayList;
import java.util.List;

import com.deedm.legacy.setrelfun.Function;
import com.deedm.legacy.setrelfun.OrderedPair;
import com.deedm.legacy.setrelfun.Set;

public class FunctionGenerator {
	protected Set from = null;
	protected Set to = null;
	protected StringGenerator generator = null;
	
	public FunctionGenerator(Set from, Set to) {
		this.from = from;
		this.to = to;
		char[] base = to.toCharArray();
		generator = new StringGenerator(base, from.length());
	}

	public Function current() {
		char[] functionValueString = generator.current();
		Function result = new Function(from, to);
		List<OrderedPair> pairs = new ArrayList<OrderedPair>();
		for (int i = 0; i < from.length(); i++) {
			char x = from.get(i);
			char y = functionValueString[i];
			pairs.add(new OrderedPair(x, y));
		}
		result.addPair(pairs);
		return result;
	}
	
	
	public void first() {
		generator.first();
	}
	
	public void next() {
		generator.next();
	}
	
	public boolean isLast() {
		return generator.isLast();
	}
}
