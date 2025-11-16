/**
 * 
 */
package com.deedm.legacy.algebra;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.deedm.legacy.counting.generator.CombinationGenerator;
import com.deedm.legacy.counting.generator.StringGenerator;

/**
 * @author user
 *
 */
public class Group<T> {
	protected T[] base = null;
	protected BinaryOperator<T> operator = null;
	protected int identityIndex = 0;
	
	public Group() {
	}

	public Group(T[] base, BinaryOperator<T> operator, int identityIndex) {
		this.base = base;
		this.operator = operator;
		this.identityIndex = identityIndex;
	}
	
	public T[] getElements() {
		return base;
	}
	
	public T getIdentity() {
		return base[identityIndex];
	}
	
	public int getOrder(T element) {
		int index = getElementIndex(element);
		if (index < 0 || index > base.length) return -1;
		if (index == identityIndex) return 1;
		
		int resIndex = index;
		int order = 2;
		for (order = 2; order <= base.length; order++) {
			resIndex = operator.getResultIndex(resIndex, index);
			if (resIndex == identityIndex) break;
		}
		return order;
	}
	
	public T getInverse(T element) {
		int index = getElementIndex(element);
		if (index < 0 || index > base.length) return null;
		if (index == identityIndex) return base[identityIndex];
		
		for (int i = 0; i < base.length; i++) {
			if (operator.getResultIndex(index, i) == identityIndex && operator.getResultIndex(i, index) == identityIndex) return base[i];
		}
		return null;
	}
	
	public boolean isSubgroup(List<T> subset) {
		boolean hasIdentity = false;
		for (int i = 0; i < subset.size(); i++) {
			T one = subset.get(i);
			int indexOne = getElementIndex(one);
			if (indexOne < 0 || indexOne >= base.length) {
				return false;
			}
			if (indexOne == identityIndex) {
				hasIdentity = true;
				continue;
			}
			boolean hasInverse = false;
			for (int j = 0; j < subset.size(); j++) {
				T two = subset.get(j);
				int indexTwo = getElementIndex(two);
				if (indexTwo < 0 || indexTwo >= base.length) {
					return false;
				}
				int resultIndex = operator.getResultIndex(indexOne, indexTwo);
				if (resultIndex == identityIndex) {
					resultIndex = operator.getResultIndex(indexTwo, indexOne);
					if (resultIndex == identityIndex) hasInverse = true;
				} else {
					boolean inSubset = false;
					for (int k = 0; k < subset.size(); k++) {
						if (base[resultIndex].equals(subset.get(k))) {
							inSubset = true;
							break;
						}
					}
					if (inSubset == false) {
						return false;
					}
				}
			}
			if (hasInverse == false) {
				return false;
			}
		}
		if (hasIdentity == false) {
			return false;
		}
		return true;
	}
	
	public boolean isSubgroup(T[] subset) {
		boolean hasIdentity = false;
		for (int i = 0; i < subset.length; i++) {
			T one = subset[i];
			int indexOne = getElementIndex(one);
			if (indexOne < 0 || indexOne > base.length) return false;
			if (indexOne == identityIndex) {
				hasIdentity = true;
				continue;
			}
			boolean hasInverse = false;
			for (int j = 0; j < subset.length; j++) {
				T two = subset[i];
				int indexTwo = getElementIndex(two);
				if (indexTwo < 0 || indexTwo > base.length) return false;
				int resultIndex = operator.getResultIndex(indexOne, indexTwo);
				if (resultIndex == identityIndex) {
					resultIndex = operator.getResultIndex(indexTwo, indexOne);
					if (resultIndex == identityIndex) hasInverse = true;
				} else {
					boolean inSubset = false;
					for (int k = 0; k < subset.length; k++) {
						if (base[resultIndex].equals(subset[k])) {
							inSubset = true;
							break;
						}
					}
					if (inSubset == false) return false;
				}
				if (hasInverse == false) return false;
			}
		}
		if (hasIdentity == false) return false;
		return true;
	}
	
	public boolean isCycleGroup() {
		for (int index = 0; index < base.length; index++) {
			if (index == identityIndex) continue;

			int resIndex = index;
			int order = 2;
			for (order = 2; order <= base.length; order++) {
				resIndex = operator.getResultIndex(resIndex, index);
				if (resIndex == identityIndex) break;
			}
			if (order == base.length) return true;
		}
		return false;
	}

	public List<T> getGenerator() {
		List<T> generatorList = new ArrayList<T>();
		for (int index = 0; index < base.length; index++) {
			if (index == identityIndex) continue;
			int resIndex = index;

			int order = 2;
			for (order = 2; order <= base.length; order++) {
				resIndex = operator.getResultIndex(resIndex, index);
				if (resIndex == identityIndex) break;
			}
			if (order == base.length) {
				// base[index] is a generator for the group!
				generatorList.add(base[index]);
			}
		}
		
		return generatorList;
	}
	
	/**
	 * Get generators of a subset of the base. A generator of a subset means that all elements in the subset can be 
	 * represented as a power of the generator. 
	 */
	public List<T> getGenerator(T[] subset) {
		List<T> generatorList = new ArrayList<T>();
		boolean[] represented = new boolean[subset.length];
		for (int i = 0; i < subset.length; i++) {
			for (int j = 0; j < represented.length; j++) represented[j] = false;
			represented[i] = true;
			
			int order = 2;
			int index = getElementIndex(subset[i]);
			if (index < 0 || index >= base.length) return generatorList;
			int resIndex = index;
			for (order = 2; order <= base.length; order++) {
				// Calculate subset[i]^order, and check if it represent a element in subset.
				resIndex = operator.getResultIndex(resIndex, index);
				for (int k = 0; k < subset.length; k++) {
					if (represented[k] == true) continue;
					if (base[resIndex].equals(subset[k])) represented[k] = true;
				}
				if (resIndex == identityIndex) break;
			}
			boolean allRepresented = true;
			for (int k = 0; k < subset.length; k++) 
				if (represented[k] == false) {
					allRepresented = false;
					break;
				}
			if (allRepresented == true) {
				// All element can be represented as subset[i]^j for some j, so subset[i] is a generator of this subset.
				generatorList.add(subset[i]);
			}
		}
		
		return generatorList;
	}
	
	public List<T> getGeneratedSubgroup(T element) {
		int index = getElementIndex(element);
		if (index < 0 || index >= base.length) return null;
		
		List<T> elementList = new ArrayList<T>();
		elementList.add(base[identityIndex]);
		if (element.equals(base[identityIndex])) return elementList;
		elementList.add(element);
		
		int resIndex = index;
		int order = 2;
		for (order = 2; order <= base.length; order++) {
			resIndex = operator.getResultIndex(resIndex, index);
			if (resIndex == identityIndex) break;
			elementList.add(base[resIndex]);
		}
		return elementList;
	}
	
	public List<List<T>> getAllGeneratedSubgroup() {
		List<List<T>> result = new ArrayList<List<T>>();
		for (int index = 0; index < base.length; index++) {
			List<T> elementList = new ArrayList<T>();
			elementList.add(base[identityIndex]);
			if (index != identityIndex) {
				elementList.add(base[index]);
				
				int resIndex = index;
				int order = 2;
				for (order = 2; order <= base.length; order++) {
					resIndex = operator.getResultIndex(resIndex, index);
					if (resIndex == identityIndex) break;
					elementList.add(base[resIndex]);
				}
			}
			boolean found = false;
			for (int i = 0; i < result.size(); i++) {
				List<T> subgroup = result.get(i);
				if (hasSameElements(subgroup, elementList)) {
					found = true;
					break;
				}
			}
			if (found == false) result.add(elementList);
		}
		
		return result;
	}
	
	public List<List<T>> getAllSubgroup() {
		List<List<T>> result = new ArrayList<List<T>>();
		
		List<T> subgroup = new ArrayList<T>();
		subgroup.add(base[identityIndex]);
		result.add(subgroup);
		
		char[] elementBase = new char[base.length];
		for (int i = 0; i < base.length; i++) elementBase[i] = (char)i;
		for (int order = 2; order < base.length; order++) {
			if (base.length % order != 0) continue;		// Because the order of a subgroup must be the factor of base.length according to the Lagrange Theorem.
//			if (order != 3) continue;
			
//			System.out.println("order = " + order);
			StringGenerator combinationGenerator = new CombinationGenerator(elementBase, order);
			combinationGenerator.first();
			while (true) {
				char[] indexArray = combinationGenerator.current();
				boolean hasIdentity = false;
				for (int i = 0; i < indexArray.length; i++) {
					if (indexArray[i] == identityIndex) {
						hasIdentity = true;
						break;
					}
				}
				if (hasIdentity != false) {
					subgroup = new ArrayList<T>();
					for (int i = 0; i < indexArray.length; i++) {
						subgroup.add(base[(int)indexArray[i]]);
					}
					
//					System.out.print("\t\tGet element: ");
//					for (int i = 0; i < subgroup.size(); i++) {
//						System.out.print(subgroup.get(i) + " ");
//					}
//					System.out.println();
					if (isSubgroup(subgroup)) {
//						System.out.println("\t\tAdd a subgroup!");
						result.add(subgroup);
					}
				}
				
				if (combinationGenerator.isLast()) break;
				combinationGenerator.next();
			}
		}
		subgroup = new ArrayList<T>();
		for (int i = 0; i < base.length; i++) subgroup.add(base[i]);
		result.add(subgroup);
		return result;
	}
	
	public T power(T x, int pow) {
		int dataIndex = getElementIndex(x);
		if (dataIndex < 0) return null;

		int resultIndex = dataIndex;
		for (int i = 1; i < pow; i++) {
			resultIndex = operator.getResultIndex(resultIndex, dataIndex); 
		}
		return base[resultIndex];
	}
	
	public void printlnOperationTable() {
		System.out.println(operator);
	}
	
	public void printlnOperationTable(PrintStream printer) {
		printer.println(operator);
	}
	
	public String getPlainStringOfOperatorTable() {
		return operator.toString();
	}

	public String getLaTeXStringOfOperatorTable() {
		return operator.toLaTeXString();
	}

	protected int getElementIndex(T data) {
		return operator.getElementIndex(data);
	}
	
	private boolean hasSameElements(List<T> oneList, List<T> twoList) {
		if (oneList.containsAll(twoList) && twoList.containsAll(oneList)) return true;
		return false;
	}
}
