package com.deedm.legacy.algebra;

import java.util.ArrayList;
import java.util.List;

public class FiniteRing<T> {
	protected T[] base = null;
	protected BinaryOperator<T> plus = null;
	protected BinaryOperator<T> times = null;
	protected int zeroIndex = 0;
	protected int identityIndex = -1;
	

	protected FiniteRing () {
	
	}
	
	public FiniteRing(T[] base, BinaryOperator<T> plus, BinaryOperator<T> times, int zeroIndex) {
		this.base = base;
		this.plus = plus;
		this.times = times;
		this.zeroIndex = zeroIndex;
	}

	public FiniteRing(T[] base, BinaryOperator<T> plus, BinaryOperator<T> times, int zeroIndex, int identityIndex) {
		this.base = base;
		this.plus = plus;
		this.times = times;
		this.zeroIndex = zeroIndex;
		this.identityIndex = identityIndex;
	}

	public T[] getElements() {
		return base;
	}
	
	public T getZero() {
		return base[zeroIndex];
	}
	
	public boolean hasIdentity() {
		return identityIndex > 0;
	}
	
	public T getIdentity() {
		if (identityIndex <= 0) return null;
		return base[identityIndex];
	}
	
	public boolean isCommutative() {
		return times.isCommutative();
	}

	public int getElementIndex(T e) {
		for (int i = 0; i < base.length; i++) {
			if (e.equals(base[i])) return i;
		}
		return -1;
	}
	
	public T getElement(int index) {
		if (index >= 0 && index < base.length) return base[index];
		return null;
	}
	
	public List<T> getZeroFactorList() {
		List<T> resultList = new ArrayList<T>();
		
		for (int i = 0; i < base.length; i++) {
			if (i == zeroIndex) continue;
			for (int j = 0; j < base.length; j++) {
				if (j == zeroIndex) continue;
				if (times.optable[i][j] == zeroIndex) {
					resultList.add(base[i]);
					resultList.add(base[j]);
				}
			}
		}
		return resultList;
	}
	
	public boolean hasZeroFactor() {
		for (int i = 0; i < base.length; i++) {
			if (i == zeroIndex) continue;
			for (int j = 0; j < base.length; j++) {
				if (j == zeroIndex) continue;
				if (times.optable[i][j] == zeroIndex) return true;
			}
		}
		return false;
	}

	public List<T> getUnitList() {
		List<T> resultList = new ArrayList<T>();

		if (identityIndex <= 0 || identityIndex > base.length) return resultList;
		
		for (int i = 0; i < base.length; i++) {
			if (i == zeroIndex) continue;
			for (int j = 0; j < base.length; j++) {
				if (j == zeroIndex) continue;
				if (times.optable[i][j] == identityIndex) {
					boolean found = false;
					for (int k = 0; k < resultList.size(); k++) {
						if (base[i].equals(resultList.get(k))) {
							found = true;
							break;
						}
					}
					if (found == false) resultList.add(base[i]);
					found = false;
					for (int k = 0; k < resultList.size(); k++) {
						if (base[j].equals(resultList.get(k))) {
							found = true;
							break;
						}
					}
					if (found == false) resultList.add(base[j]);
				}
			}
		}
		return resultList;
	}
	
	public boolean isIntegerDomain() {
		if (!hasIdentity()) return false;
		if (!isCommutative()) return false;
		if (hasZeroFactor()) return false;
		return true;
	}
	
	public boolean isField() {
		if (!hasIdentity()) return false;
		if (!isCommutative()) return false;
		if (hasZeroFactor()) return false;
		
		for (int i = 0; i < base.length; i++) {
			if (i == zeroIndex) continue;
			boolean hasInverse = false;
			for (int j = 0; j < base.length; j++) {
				if (times.optable[i][j] == identityIndex) {
					hasInverse = true;
					break;
				}
			}
			if (hasInverse == false) return false;
		}
		return true;
	}
	
	public int getPlusOrder(T e) {
		int index = getElementIndex(e);
		if (index < 0) return 0;
		if (index == zeroIndex) return 1;
		int resIndex = index;

		int order = 2;
		for (order = 2; order <= base.length; order++) {
			resIndex = plus.optable[resIndex][index];
			if (resIndex == zeroIndex) break;
		}
		return order;
	}

	public int getTimesOrder(T e) {
		if (!hasIdentity()) return 0;
		
		int index = getElementIndex(e);
		if (index < 0) return 0;
		if (index == zeroIndex) return 0;
		if (index == identityIndex) return 1;
		int resIndex = index;

		int order = 2;
		for (order = 2; order <= base.length; order++) {
			resIndex = times.optable[resIndex][index];
			if (resIndex == identityIndex) break;
		}
		if (order < base.length) return order;
		return 0;
	}
	
	public T getNegativeElement(T e) {
		int index = getElementIndex(e);
		if (index < 0) return null;

		for (int i = 0; i < base.length; i++) {
			if (plus.optable[index][i] == zeroIndex) return getElement(i);
		}
		return null;
	}
	
	public T getInverseElement(T e) {
		if (!hasIdentity()) return null;
		
		int index = getElementIndex(e);
		if (index < 0) return null;

		for (int i = 0; i < base.length; i++) {
			if (times.optable[index][i] == identityIndex && times.optable[i][index] == identityIndex) return getElement(i);
		}
		return null;
	}
	
	public T getPlusResult(T one, T two) {
		int indexOne = getElementIndex(one);
		if (indexOne < 0) return null;
		
		int indexTwo = getElementIndex(two);
		if (indexTwo < 0) return null;
		
		return getElement(plus.optable[indexOne][indexTwo]);
	}
	
	public T getTimesResult(T one, T two) {
		int indexOne = getElementIndex(one);
		if (indexOne < 0) return null;
		
		int indexTwo = getElementIndex(two);
		if (indexTwo < 0) return null;
		
		return getElement(times.optable[indexOne][indexTwo]);
	}
	
	public String getPlusTableLaTexString() {
		return plus.toLaTeXString();
	}
	
	public String getTimesTableLaTexString() {
		return times.toLaTeXString();
	}

	public BinaryOperator<T> getPlusTable() {
		return plus;
	}

	public BinaryOperator<T> getTimesTable() {
		return times;
	}
}
