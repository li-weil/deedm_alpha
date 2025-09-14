package com.deedm.legacy.counting;

import java.io.PrintStream;

import counting.filter.StringFilter;
import counting.generator.StringGenerator;


public class StringCounter {
	private StringGenerator generator = null;
	private PrintStream writer = null;
	private int length = 0;
	private boolean onlyPrintAccepted = false;
	
	public StringCounter(char[] base, int length) {
		this.length = length;
		generator = new StringGenerator(base, length);
	}
	
	public StringCounter(StringGenerator generator) {
		this.generator = generator;
		length = generator.getStringLength();
	}
	
	public void setWriter(PrintStream writer) {
		this.writer = writer;
	}

	public void setWriter(PrintStream writer, boolean onlyPrintAccepted) {
		this.writer = writer;
		this.onlyPrintAccepted = onlyPrintAccepted;
	}
	
	public int counting(StringFilter filter) {
		if (length == 0) {
			if (filter.accept(null)) {
				if (writer != null) writer.println("Accept null string!");
				return 1;
			}
			if (writer != null) writer.println("DO NOT accept null string!");
			return 0;
		}
		
		generator.first();
		int result = 0;
		while (true) {
			char[] string = generator.current();
			if (filter.accept(string)) {
				result++;
				if (writer != null) writer.println(StringGenerator.convertToString(string) + ", accept " + result);
			} else {
				if (writer != null && !onlyPrintAccepted) writer.println(StringGenerator.convertToString(string) + ", NOT accept!");
			}
			if (generator.isLast()) break;
			generator.next();
		}

		if (writer != null) writer.println("Total " + result + " strings for filter condition " + filter);
		return result;
	}
}
