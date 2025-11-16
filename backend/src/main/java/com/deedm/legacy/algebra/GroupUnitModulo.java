/**
 * 
 */
package com.deedm.legacy.algebra;

import java.util.ArrayList;
import java.util.List;

/**
 * @author user
 *
 */
public class GroupUnitModulo extends Group<Integer> {
	protected int num = 0;
	
	public GroupUnitModulo(int num) {
		this.num = num;
		setBase(num);
		setOperationTable(num);
	}
	
	
	public List<int[]> getAllCoset(int[] subgroup) {
		for (int i = 0; i < subgroup.length; i++) {
			int elementIndex = getElementIndex(subgroup[i]);
			if (elementIndex < 0 || elementIndex >= base.length) return null;
		}

		List<int[]> result = new ArrayList<int[]>();
		for (int index = 0; index < base.length; index++) {
			int[] currentCoset = new int[subgroup.length];
			for (int i = 0; i < subgroup.length; i++) {
				int elementIndex = getElementIndex(subgroup[i]);
				currentCoset[i] = base[operator.getResultIndex(index, elementIndex)];
			}
			
			boolean found = false;
			for (int[] coset : result) {
				if (hasSameElements(coset, currentCoset)) {
					found = true;
					break;
				}
			}
			if (found == false) result.add(currentCoset);
		}
		return result;
	}
	
	public List<int[]> getAllCoset(List<Integer> subgroup) {
		int[] subGroupArray = integerListToArray(subgroup);
		return getAllCoset(subGroupArray);
	}
	
	public BinaryOperator<Integer> getSubgroupOperatorTable(Integer[] subgroupBase) {
		Integer[][] optable = new Integer[subgroupBase.length][];
		for (int i = 0; i < subgroupBase.length; i++) {
			optable[i]  = new Integer[subgroupBase.length];
			for (int j = 0; j < subgroupBase.length; j++) {
				optable[i][j] = (subgroupBase[i] * subgroupBase[j]) % num;
			}
		}
		BinaryOperator<Integer> subgroupOperator = new BinaryOperator<Integer>(subgroupBase, optable);
		subgroupOperator.setName("\\otimes_{" + num + "}");
		
		return subgroupOperator;
	}
	
	public Group<Integer> createSubgroup(List<Integer> subgroup) {
		Integer[] subgroupBase = new Integer[subgroup.size()];
		int subgroupIdentity = 0;
		for (int i = 0; i < subgroupBase.length; i++) {
			subgroupBase[i] = subgroup.get(i);
			if (subgroupBase[i] == 1) subgroupIdentity = i;
		}
		
		Integer[][] optable = new Integer[subgroupBase.length][];
		for (int i = 0; i < subgroupBase.length; i++) {
			optable[i]  = new Integer[subgroupBase.length];
			for (int j = 0; j < subgroupBase.length; j++) {
				optable[i][j] = (subgroupBase[i] * subgroupBase[j]) % num;
			}
		}
		BinaryOperator<Integer> subgroupOperator = new BinaryOperator<Integer>(subgroupBase, optable);
		subgroupOperator.setName("\\otimes_{" + num + "}");
		
		Group<Integer> result = new Group<Integer>(subgroupBase, subgroupOperator, subgroupIdentity);
		return result;
	}
	
	
	public static  int[] integerListToArray(List<Integer> list) {
		if (list.size() <= 0) return null;
		int[] result = new int[list.size()];
		for (int i = 0; i < list.size(); i++) result[i] = list.get(i);
		return result;
	}

	public static String integerArrayToString(int[] array) {
		StringBuffer message = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			if (i > 0) message.append(", ");
			message.append(array[i]);
		}
		return message.toString();
	}

	public static String integerArrayToString(Integer[] array) {
		StringBuffer message = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			if (i > 0) message.append(", ");
			message.append(array[i]);
		}
		return message.toString();
	}
	
	public static String integerListToString(List<Integer> list) {
		StringBuffer message = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			if (i > 0) message.append(", ");
			message.append(list.get(i));
		}
		return message.toString();
	}

	/**
	 * 设置为群 U(num) 的基
	 */
	protected void setBase(int num) {
		int[] temp = new int[num];
		int size = 0;
		
		for (int data = 1; data < num; data++) {
			if (isMutePrime(data, num)) {
				temp[size] = data;
				size = size + 1;
			}
		}
		base = new Integer[size];
		for (int index = 0; index < size; index++) base[index] = temp[index];
		identityIndex = 0;
	}
	
	/**
	 * 设置为群 U(num) 的运算表，注意这时基集应该是群 U(num) 的基 
	 */
	protected void setOperationTable(int num) {
		if (base == null || base.length <= 0) return;
		
		Integer[][] optable = new Integer[base.length][];
		for (int i = 0; i < base.length; i++) {
			optable[i]  = new Integer[base.length];
			for (int j = 0; j < base.length; j++) {
				optable[i][j] = (base[i] * base[j]) % num;
			}
		}
		operator = new BinaryOperator<Integer>(base, optable);
		operator.setName("\\otimes_{" + num + "}");
	}
	
	/**
	 * 判断数 num1 和 num2 是否互素
	 */
	protected boolean isMutePrime(int num1, int num2) {
		int bound = Math.min(num1, num2);
		
		for (int i = 2; i <= bound; i++) {
			if (num1 % i == 0 && num2  % i == 0) {
				// i 是 num1 和 num2 的公因子
				return false;	 
			}
		}
		return true;
	}
	
	private boolean hasSameElements(int[] oneArray, int[] twoArray) {
		for (int i = 0; i < oneArray.length; i++) {
			boolean found = false;
			for (int j = 0; j < twoArray.length; j++) {
				if (oneArray[i] == twoArray[j]) {
					found = true;
					break;
				}
			}
			if (found == false) return false;
		}
		for (int i = 0; i < twoArray.length; i++) {
			boolean found = false;
			for (int j = 0; j < oneArray.length; j++) {
				if (oneArray[j] == twoArray[i]) {
					found = true;
					break;
				}
			}
			if (found == false) return false;
		}
		return true;
	}
}
