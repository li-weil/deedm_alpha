package com.deedm.legacy.algebra;

public class ModNumber {
	private int m = 2;
	private int value = 0;
	
	public final static ModNumber NaN = new ModNumber(0, 0);
	
	public ModNumber(int m, int value) {
		this.m = m;
		this.value = toRange(m, value);
	}
	
	public ModNumber(ModNumber other) {
		this.m = other.m;
		this.value = other.value;
	}
	
	public int getMod() {
		return m;
	}
	
	public int getValue() {
		return value;
	}
	
	public boolean isZero() {
		return value == 0;
	}
	
	public boolean isUnit() {
		return value == 1;
	}
	
	public boolean hasInverse() {
		int result = internalInverse();
		if (result < m) return true;
		return false;
	}
	
	public ModNumber inverse() {
		int result = internalInverse();
		
		if (result < m) return new ModNumber(m, result);
		return NaN;
	}
	
	public ModNumber add(ModNumber other) {
		int result = toRange(m, value + other.value);
		
		return new ModNumber(m, result);
	}

	public ModNumber minus(ModNumber other) {
		int result = toRange(m, value - other.value);
		
		return new ModNumber(m, result);
	}

	public ModNumber times(ModNumber other) {
		int result = toRange(m, value * other.value);
		
		return new ModNumber(m, result);
	}
	
	public ModNumber div(ModNumber other) {
		if (other.value == 0) return NaN;
		int inverse = other.internalInverse();
		if (inverse < m) {
			int result = toRange(m, value*inverse);
			return new ModNumber(m, result);
		}
		return NaN;
	}
	
	public String toString() {
		if (this == NaN) return "NaN";
		else return ""+value;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + value;
		result = prime * result + m;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof ModNumber)) return false;
		ModNumber other = (ModNumber)obj;
		return m == other.m && value == other.value;
	}
	
	private int toRange(int m, int value) {
		if (m < 2) return value;
		
		if (0 <= value && value < m) return value;
		if (-m <= value && value < 0 ) return value+m;
		if (m <= value && value < 2*m) return value - m;
		while (value < 0) value += m;
		while (value >= m) value -= m;
		
		return value;
	}
	
	private int internalInverse() {
		int check = 1;
		while (check < m) {
			int temp = toRange(m, check*value);
			if (temp == 1) return check;
			check++;
		}
		return check;
	}
	
	public static void main(String[] args) {
		int m = 7;
		
		ModNumber one = new ModNumber(m, 5);
		ModNumber two = new ModNumber(m, 6);
		
		ModNumber result = one.add(two);
		System.out.println(one + "+" + two + "=" + result);
		result = one.times(two);
		System.out.println(one + "*" + two + "=" + result);
		
		BinaryOperator<ModNumber> addTable = createAddTable(m);
		addTable.setName("$\\oplus_" + m + "$");
		BinaryOperator<ModNumber> timesTable = createTimesTable(m);
		timesTable.setName("$\\otimes_" + m + "$");
		
		System.out.println("Add table:");
		System.out.println(addTable.toLaTeXString());
		System.out.println();
		System.out.println("Times table:");
		System.out.println(timesTable.toLaTeXString());
	}
	
	private static BinaryOperator<ModNumber> createAddTable(int m) {
		int[][] table = new int[m][m];
		ModNumber[] base = new ModNumber[m];
		
		for (int i = 0; i < m; i++) base[i] = new ModNumber(m, i);
		
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < m; j++) {
				ModNumber result = base[i].add(base[j]);
				table[i][j] = result.getValue();
			}
		}
		return new BinaryOperator<ModNumber>(base, table);		
	}

	private static BinaryOperator<ModNumber> createTimesTable(int m) {
		int[][] table = new int[m][m];
		ModNumber[] base = new ModNumber[m];
		
		for (int i = 0; i < m; i++) base[i] = new ModNumber(m, i);
		
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < m; j++) {
				ModNumber result = base[i].times(base[j]);
				table[i][j] = result.getValue();
			}
		}
		return new BinaryOperator<ModNumber>(base, table);		
	}
}
