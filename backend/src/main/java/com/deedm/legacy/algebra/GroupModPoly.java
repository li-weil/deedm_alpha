package com.deedm.legacy.algebra;

import java.util.ArrayList;
import java.util.List;

public class GroupModPoly extends Group<SVModPoly> {
	private int m = 3;
	private SVModPoly modPoly = null;
	
	public GroupModPoly(int m, int[] coeff) {
		this.m = m;
		modPoly = new SVModPoly(m, coeff);

		setGroup();
	}
	
	public SVModPoly getModPoly() {return modPoly; }
	
	public String getElementString() {
		StringBuffer message = new StringBuffer();
		for (int i = 0; i < base.length; i++) message.append(base[i] + "\\quad ");
		return message.toString();
	}
	
	protected void setGroup() {
		SVModPoly[] oldbase = createAllModPoly(m, modPoly.getMostPower()-2);
		BinaryOperator<SVModPoly> table = createTimesTable(oldbase, modPoly);
		table.setName("$\\otimes_"+m+"$");

		boolean[] flag = new boolean[oldbase.length];
		int size = 0;
		for (int i = 0; i < oldbase.length; i++) {
			if (oldbase[i].isUnit()) {
				flag[i] = true;  size++;
			} else if (!oldbase[i].isZero() && !oldbase[i].isFactor(modPoly)) {
				flag[i] = true;
				size++;
			} else flag[i] = false;
		}
		base = new SVModPoly[size];
		int index = 0;
		for (int i = 0; i < oldbase.length; i++) {
			if (flag[i]) {
				base[index] = oldbase[i];
				if (base[index].isUnit()) identityIndex = index;
				index++;
			}
		}
		
		SVModPoly[][] result = new SVModPoly[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				result[i][j] = table.getOperationResult(base[i], base[j]);
			}
		}
		
		operator = new BinaryOperator<SVModPoly>(base, result);
		operator.setName("$\\otimes_"+m+"$");
	}

	
	public static BinaryOperator<SVModPoly> removeZeroFactor(BinaryOperator<SVModPoly> table, SVModPoly modPoly) {
		SVModPoly[] base = table.getBase();
		boolean[] flag = new boolean[base.length];
		int size = 0;
		for (int i = 0; i < base.length; i++) {
			if (base[i].isUnit()) {
				flag[i] = true;  size++;
			} else if (!base[i].isZero() && !base[i].isFactor(modPoly)) {
				flag[i] = true;
				size++;
			} else flag[i] = false;
		}
		SVModPoly[] nbase = new SVModPoly[size];
		int index = 0;
		for (int i = 0; i < base.length; i++) {
			if (flag[i]) {
				nbase[index] = base[i];
				index++;
			}
		}
		
		SVModPoly[][] result = new SVModPoly[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				result[i][j] = table.getOperationResult(nbase[i], nbase[j]);
			}
		}
		
		return new BinaryOperator<SVModPoly>(nbase, result);
	}
	
	
	public static BinaryOperator<SVModPoly> createTimesTable(SVModPoly[] base, SVModPoly modPoly) {
		int[][] table = new int[base.length][base.length];
		
		for (int i = 0; i < base.length; i++) {
			for (int j = 0; j < base.length; j++) {
				SVModPoly resultPoly = base[i].times(base[j]);
//				System.out.println("times: " + resultPoly + " = " + base[i] + " * " + base[j]);
				resultPoly = resultPoly.mod(modPoly);
//				System.out.println("mod: " + resultPoly + " % " + modPoly);
				
				boolean found = false;
				for (int index = 0; index < base.length; index++) {
					if (base[index].equals(resultPoly)) {
						table[i][j] = index;
						found = true;
						break;
					}
				}
				if (!found) {
					System.out.println("Has not found result: " + resultPoly + " = " + base[i] + " * " + base[j]);
				}
			}
		}
		BinaryOperator<SVModPoly> result = new BinaryOperator<SVModPoly>(base, table);
		return result;
	}
	
	public static SVModPoly[] createAllModPoly(int m, int highestPower) {
		int resultPolyNumber = 1;
		for (int i = 0; i <= highestPower; i++) resultPolyNumber = resultPolyNumber * m;
		int[] coeffArray = new int[highestPower+1];
		for (int i = 0; i < coeffArray.length; i++) coeffArray[i] = 0;
		coeffArray[coeffArray.length-1] = 1;
		
		SVModPoly[] resultPolyArray = new SVModPoly[resultPolyNumber];
		resultPolyArray[0] = new SVModPoly(m, 0);
		int resultIndex = 1;
		while (true) {
			int firstNonZeroIndex = 0;
			while (coeffArray[firstNonZeroIndex] == 0) firstNonZeroIndex++;
			
			int[] currentCoeff = new int[coeffArray.length-firstNonZeroIndex];
			for (int i = 0; i < currentCoeff.length; i++) currentCoeff[i] = coeffArray[firstNonZeroIndex+i];
			resultPolyArray[resultIndex] = new SVModPoly(m, currentCoeff);
			resultIndex = resultIndex + 1;
			
			boolean isLast = true;
			for (int i = coeffArray.length-1; i >= 0; i--) {
				if (coeffArray[i] == (m-1)) coeffArray[i] = 0;
				else {
					coeffArray[i] += 1;
					isLast = false;
					break;
				}
			}
			if (isLast) break;
		}
		
		return resultPolyArray;
	}

	public BinaryOperator<SVModPoly> getSubgroupOperatorTable(SVModPoly[] subgroupBase) {
		SVModPoly[][] optable = new SVModPoly[subgroupBase.length][];
		for (int i = 0; i < subgroupBase.length; i++) {
			optable[i]  = new SVModPoly[subgroupBase.length];
			for (int j = 0; j < subgroupBase.length; j++) {
				optable[i][j] = operator.getOperationResult(subgroupBase[i], subgroupBase[j]);
			}
		}
		BinaryOperator<SVModPoly> subgroupOperator = new BinaryOperator<SVModPoly>(subgroupBase, optable);
		subgroupOperator.setName("\\otimes_{" + m + "}");
		
		return subgroupOperator;
	}
	
	public List<SVModPoly[]> getAllCosets(SVModPoly[] subgroup) {
		for (int i = 0; i < subgroup.length; i++) {
			int elementIndex = getElementIndex(subgroup[i]);
			if (elementIndex < 0 || elementIndex >= base.length) return null;
		}

		List<SVModPoly[]> result = new ArrayList<SVModPoly[]>();
		for (int index = 0; index < base.length; index++) {
			SVModPoly[] currentCoset = new SVModPoly[subgroup.length];
			for (int i = 0; i < subgroup.length; i++) {
				int elementIndex = getElementIndex(subgroup[i]);
				currentCoset[i] = base[operator.getResultIndex(index, elementIndex)];
			}
			
			boolean found = false;
			for (int i = 0; i < result.size(); i++) {
				SVModPoly[] coset = result.get(i);
				if (hasSameElements(coset, currentCoset)) {
					found = true;
					break;
				}
			}
			if (found == false) result.add(currentCoset);
		}
		return result;
	}
	
	public List<SVModPoly[]> getAllCosets(List<SVModPoly> subgroup) {
		SVModPoly[] subGroupArray = polyListToArray(subgroup);
		return getAllCosets(subGroupArray);
	}

	public static SVModPoly[] polyListToArray(List<SVModPoly> list) {
		if (list.size() <= 0) return null;
		SVModPoly[] result = new SVModPoly[list.size()];
		for (int i = 0; i < list.size(); i++) result[i] = list.get(i);
		return result;
	}
	
	public String polyListToString(List<SVModPoly> list) {
		StringBuffer message = new StringBuffer();
		for (int i = 0; i < list.size(); i++) message.append(list.get(i) + "\\quad ");
		return message.toString();
	}

	public String polyArrayToString(SVModPoly[] list) {
		StringBuffer message = new StringBuffer();
		for (int i = 0; i < list.length; i++) message.append(list[i] + "\\quad ");
		return message.toString();
	}

	private boolean hasSameElements(SVModPoly[] oneArray, SVModPoly[] twoArray) {
		for (int i = 0; i < oneArray.length; i++) {
			boolean found = false;
			for (int j = 0; j < twoArray.length; j++) {
				if (oneArray[i].equals(twoArray[j])) {
					found = true;
					break;
				}
			}
			if (found == false) return false;
		}
		for (int i = 0; i < twoArray.length; i++) {
			boolean found = false;
			for (int j = 0; j < oneArray.length; j++) {
				if (oneArray[j].equals(twoArray[i])) {
					found = true;
					break;
				}
			}
			if (found == false) return false;
		}
		return true;
	}
}
