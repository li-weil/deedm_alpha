/**
 * 
 */
package com.deedm.legacy.algebra;

import java.util.ArrayList;
import java.util.List;

import counting.generator.PermutationGenerator;
import counting.generator.StringGenerator;

/**
 * @author user
 *
 */
public class GroupPermutation extends Group<GroupPermutation.Bijection> {

	public GroupPermutation(int num) {
		setBase(num);
		setOperationTable();
	}
	
	public GroupPermutation(Bijection[] base) {
		this.base = base;
		for (int i = 0; i < base.length; i++) {
			if (base[i].isIdentity()) {
				identityIndex = i;
				break;
			}
		}
		setOperationTable();
	}

	public List<Coset> getAllLeftCoset(Bijection[] subgroup) {
		List<Coset> result = new ArrayList<Coset>();
		for (int index = 0; index < base.length; index++) {
			Bijection[] currentCoset = new Bijection[subgroup.length];
			for (int i = 0; i < subgroup.length; i++) {
				int elementIndex = getElementIndex(subgroup[i]);
				currentCoset[i] = base[operator.getResultIndex(index, elementIndex)];
			}
			
			boolean found = false;
			for (int i = 0; i < result.size(); i++) {
				Bijection[] coset = result.get(i).getCoset();
				if (hasSameElements(coset, currentCoset)) {
					found = true;
					break;
				}
			}
			if (found == false) result.add(new Coset(currentCoset));
		}
		return result;
	}
	
	public List<Coset> getAllLeftCoset(List<Bijection> subgroup) {
		Bijection[] subGroupArray = FunctionListToArray(subgroup);
		return getAllLeftCoset(subGroupArray);
	}
	
	public List<Coset> getAllRightCoset(Bijection[] subgroup) {
		List<Coset> result = new ArrayList<Coset>();
		for (int index = 0; index < base.length; index++) {
			Bijection[] currentCoset = new Bijection[subgroup.length];
			for (int i = 0; i < subgroup.length; i++) {
				int elementIndex = getElementIndex(subgroup[i]);
				currentCoset[i] = base[operator.getResultIndex(elementIndex, index)];
			}
			
			boolean found = false;
			for (int i = 0; i < result.size(); i++) {
				Bijection[] coset = result.get(i).getCoset();
				if (hasSameElements(coset, currentCoset)) {
					found = true;
					break;
				}
			}
			if (found == false) result.add(new Coset(currentCoset));
		}
		return result;
	}
	
	public List<Coset> getAllRightCoset(List<Bijection> subgroup) {
		Bijection[] subGroupArray = FunctionListToArray(subgroup);
		return getAllRightCoset(subGroupArray);
	}
	
	public boolean isNormalSubgroup(Bijection[] subgroup) {
		for (int index = 0; index < base.length; index++) {
			Bijection[] leftCoset = new Bijection[subgroup.length];
			Bijection[] rightCoset = new Bijection[subgroup.length];
			for (int i = 0; i < subgroup.length; i++) {
				int elementIndex = getElementIndex(subgroup[i]);
				leftCoset[i] = base[operator.getResultIndex(index, elementIndex)];
				rightCoset[i] = base[operator.getResultIndex(elementIndex, index)];
			}
			if (!hasSameElements(leftCoset, rightCoset)) {
//				System.out.println("Left coset is " + FunctionArrayToString(leftCoset));
//				System.out.println("Right coset is " + FunctionArrayToString(rightCoset));
				return false;
			}
		}
		return true;
	}
	
	public boolean isNormalSubgroup(List<Bijection> subgroup) {
		Bijection[] subGroupArray = FunctionListToArray(subgroup);
		return isNormalSubgroup(subGroupArray);
	}
	
	public List<Coset> getAllCoset(Bijection[] normalSubgroup) {
		List<Coset> cosetList = new ArrayList<Coset>();
		for (int index = 0; index < base.length; index++) {
			Bijection[] currentLeftCoset = new Bijection[normalSubgroup.length];
			Bijection[] currentRightCoset = new Bijection[normalSubgroup.length];
			for (int i = 0; i < normalSubgroup.length; i++) {
				int elementIndex = getElementIndex(normalSubgroup[i]);
				currentLeftCoset[i] = base[operator.getResultIndex(index, elementIndex)];
				currentRightCoset[i] = base[operator.getResultIndex(elementIndex, index)];
			}
			if (!hasSameElements(currentLeftCoset, currentRightCoset)) return null;

			boolean found = false;
			for (int i = 0; i < cosetList.size(); i++) {
				Bijection[] coset = cosetList.get(i).getCoset();
				if (hasSameElements(coset, currentLeftCoset)) {
					found = true;
					break;
				}
			}
			if (found == false) cosetList.add(new Coset(currentLeftCoset));
		}
		return cosetList;
	}
	
	public QuotientGroup getQuotientGroup(Bijection[] normalSubgroup) {
		List<Coset> cosetList = getAllCoset(normalSubgroup);
		if (cosetList == null) return null;
		
		QuotientGroup quotient = new QuotientGroup();
		quotient.setBase(this, cosetList);
		if (!quotient.setOperationTable(this)) return null;
		return quotient;
	}
	
	public QuotientGroup getQuotientGroup(List<Bijection> normalSubgroup) {
		Bijection[] subGroupArray = FunctionListToArray(normalSubgroup);
		return getQuotientGroup(subGroupArray);
	}
	
	public BinaryOperator<Bijection> getSubgroupOperatorTable(Bijection[] subgroupBase) {
		Bijection[][] optable = new Bijection[subgroupBase.length][];
		for (int i = 0; i < subgroupBase.length; i++) {
			optable[i]  = new Bijection[subgroupBase.length];
			for (int j = 0; j < subgroupBase.length; j++) {
				optable[i][j] = subgroupBase[i].composite(subgroupBase[j]);
			}
		}
		BinaryOperator<Bijection> subgroupOperator = new BinaryOperator<Bijection>(subgroupBase, optable);
		subgroupOperator.setName("\\circ ");
		
		return subgroupOperator;
	}
	

	public String getLaTeXTableStringOfElements() {
		int length = base[0].values.length;
		StringBuffer buffer = new StringBuffer();
		buffer.append("\\begin{tabular}{c|");
		for (int i = 0; i < length; i++) buffer.append("c");
		buffer.append("}\n");
		
		buffer.append("\\hline\\quad\\quad ");
		for (int i = 1; i <= length; i++) buffer.append(" & " + i);
		buffer.append("\\\\\n");
		
		for (int i = 0; i < base.length; i++) {
			if (i == 0) buffer.append("\\hline " + base[i]);
			else buffer.append(base[i]);
			for (int j = 0; j < length; j++) {
				buffer.append(" & " + base[i].values[j]);
			}
			buffer.append("\\\\\n");
		}

		buffer.append("\\hline\n");
		buffer.append("\\end{tabular}\n");
		return buffer.toString();
		
	}
	
	public static String FunctionArrayToString(Bijection[] array) {
		StringBuffer message = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			if (i > 0) message.append(", ");
			message.append(array[i].name);
		}
		return message.toString();
	}

	public static String FunctionListToString(List<Bijection> list) {
		StringBuffer message = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			if (i > 0) message.append(", ");
			message.append(list.get(i).name);
		}
		return message.toString();
	}
	
	public static Bijection[] FunctionListToArray(List<Bijection> list) {
		Bijection[] result = new Bijection[list.size()];
		for (int i = 0; i < list.size(); i++) result[i] = list.get(i);
		return result;
	}
	
	
	
	protected void setBase(int num) {
		char[] set = new char[num];
		int size = 1;
		for (int i = 1; i <= num; i++) {
			set[i-1] = (char)('0' + i);
			size = size * i;
		}
		base = new Bijection[size];
		
		StringGenerator generator = new PermutationGenerator(set, num);
		generator.first();
		int counter = 1;
		base[counter-1] = new Bijection("f_{" + counter + "}", generator.current());
		if (base[counter-1].isIdentity()) identityIndex = counter-1;
		counter = counter + 1;
		while (!generator.isLast()) {
			generator.next();
			base[counter-1] = new Bijection("f_{" + counter + "}", generator.current());
			if (base[counter-1].isIdentity()) identityIndex = counter-1;
			counter = counter + 1;
		}
	}
	
	protected void setOperationTable() {
		if (base == null || base.length <= 0) return;
		
		int[][] optable = new int[base.length][];
		for (int i = 0; i < base.length; i++) {
			optable[i]  = new int[base.length];
			for (int j = 0; j < base.length; j++) {
				Bijection result = base[i].composite(base[j]);
				if (result == null) {
					optable = null;
					return;
				}
				int resultIndex = 0;
				while (!base[resultIndex].equals(result) && resultIndex < base.length) resultIndex++;
				if (resultIndex >= base.length) {
					System.out.println("Cannot find composite result " + result + " of " + base[i] + " and " + base[j] + " in base!");
					optable = null;
					return;
				}
				optable[i][j] = resultIndex;
			}
		}
		operator = new BinaryOperator<Bijection>(base, optable);
		operator.setName("\\circ ");
	}
	
	private boolean hasSameElements(Bijection[] oneArray, Bijection[] twoArray) {
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
	
	
	public static class Permutation {
		List<String> cycleList = null;
		
		public Permutation(String string) {
			cycleList = new ArrayList<String>();
			cycleList.add(string);
		}
		
		public Permutation(List<String> stringList) {
			this.cycleList = stringList;
		}

		public Permutation(String[] strings) {
			cycleList = new ArrayList<String>();
			for (int i = 0; i < strings.length; i++) cycleList.add(strings[i]);
		}
		
		public int permutation(int value) {
			for (String cycle : cycleList) {
				for (int i = 0; i < cycle.length(); i++) {
					if (cycle.charAt(i) == ('0'+value)) {
						if (i == cycle.length()) return cycle.charAt(0)-'0';
						else return cycle.charAt(i+1)-'0';
					}
				}
			}
			return -1;
		}
		
		public boolean isEvenPermutation() {
			int sum = 0;
			for (String cycle : cycleList) {
				sum = sum + cycle.length()-1;
			}
			if (sum % 2 == 0) return true;
			return false;
		}
		
		public String toCycleString() {
			if (cycleList.size() == 0) return "(1)";
			StringBuffer message = new StringBuffer();
			for (String cycle : cycleList) {
				message.append("(" + cycle.charAt(0));
				for (int i = 1; i < cycle.length(); i++) {
					message.append(" "+cycle.charAt(i));
				}
				message.append(")");
			}
			return message.toString();
		}
		
		public String toTranspositionString() {
			if (cycleList.size() == 0) return "(1)";
			StringBuffer message = new StringBuffer();
			for (String cycle : cycleList) {
				for (int i = 1; i < cycle.length(); i++) {
					message.append("(" + cycle.charAt(i-1));
					message.append(" "+cycle.charAt(i));
					message.append(")");
				}
			}
			return message.toString();
		}
		
		public static Permutation getCycleList(Bijection function) {
			List<String> stringList = new ArrayList<String>();
			
			boolean[] flag = new boolean[function.elementNumber()];
			for (int i = 0; i < flag.length; i++) flag[i] = false;
			
			boolean finish = false;
			int start = 1;
			while (finish == false) {
				StringBuffer message = new StringBuffer();
				flag[start-1] = true;
				int next = function.permutation(start);
				
//				System.out.println("start = " + start);
				if (next != start) {
					message.append((char)(start+'0'));
					while (next != start) {
//						System.out.println("next = " + next);
						message.append((char)(next+'0'));
						flag[next-1] = true;
						next = function.permutation(next);
					}
				} 
				if (message.length() > 0) stringList.add(message.toString());
				
//				System.out.println("Message: " + message.toString());
				finish = true;
				for (int i = 0; i < flag.length; i++) {
					if (flag[i] == false) {
						finish = false;
						start = i+1;
						break;
					}
				}
			}
			
			return new Permutation(stringList);
		}
	}
	
	public static class Bijection {
		String name = null;
		char[] values = null;
		
		public Bijection(String name, char[] values) {
			this.name = name;
			this.values = new char[values.length];
			for (int i = 0; i < values.length; i++) this.values[i] = values[i];
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public char getValue(int index) {
			return values[index];
		}
		
		public int permutation(int value) {
			return values[value-1]-'0';
		}
		
		public int elementNumber() {
			return values.length;
		}
		
		public boolean isIdentity() {
			if (values == null) return false;
			for (int i = 0; i < values.length; i++) 
				if (values[i] != '0'+i+1) return false;
			return true;
		}
		
		public Bijection composite(Bijection other) {
			char[] result = new char[values.length];
			for (int i = 0; i < values.length; i++) {
				int midResult = other.getValue(i) - '0';
				if (midResult < 1 || midResult > values.length) {
					System.out.println("Internal Error: the composition of " + this + " and " + other + " get error index!");
					return null;
				}
				result[i] = values[midResult-1];
			}
			return new Bijection(name + "\\circ " + other.name, result);
		}
		
		@Override
		public String toString() {
			return name;
		}
		
		public String toFullString() {
			StringBuffer message = new StringBuffer();
			message.append("{ ");
			for (int i = 1; i <= values.length; i++) {
				message.append(name + "(" + i + ")=" + values[i-1] + "\t");
			}
			message.append(" }");
			return message.toString();
		}
		
		public String toPermString() {
			StringBuffer message = new StringBuffer();
			message.append("(");
			message.append(values[0]);
			for (int i = 1; i < values.length; i++) {
				message.append(" " + values[i]);
			}
			message.append(")");
			return message.toString();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!(obj instanceof Bijection)) return false;
			Bijection other = (Bijection)obj;
			if (values.length != other.values.length) return false;
			for (int i = 0; i < values.length; i++) 
				if (values[i] != other.values[i]) return false;
			return true;
		}
	}
	
	public static class QuotientGroup extends Group<Coset> {
		private QuotientGroup() {
		}
		
		public void setBase(GroupPermutation group, List<Coset> cosetList) {
			base = new Coset[cosetList.size()];
			for (int i = 0; i < cosetList.size(); i++) {
				base[i] = cosetList.get(i);
				for (int k = 0; k < base[i].length(); k++) {
					int index = group.getElementIndex(base[i].get(k));
					// The coset quotient.base[i] contains the identity element of the group, so it is the identity of the quotient group.				
					if (index == group.identityIndex) identityIndex = i; 
				}
			}
		}
		
		public boolean setOperationTable(GroupPermutation group) {
			int[][] optable = new int[base.length][base.length];

			for (int i = 0; i < base.length; i++) {
				int oneIndex = group.getElementIndex(base[i].get(0));
				for (int j = 0; j < base.length; j++) {
					int twoIndex = group.getElementIndex(base[j].get(0));
					// The result coset of two cosets in the quotient group is the coset which includes the result of the arbitrary two 
					// representatives in those two cosets.
					Bijection result = group.base[group.operator.getResultIndex(oneIndex, twoIndex)];
					int resultCosetIndex = 0;
					for (resultCosetIndex = 0; resultCosetIndex < base.length; resultCosetIndex++) {
						boolean found = false;
						for (int k = 0; k < base[resultCosetIndex].length(); k++) {
							if (result.equals(base[resultCosetIndex].get(k))) {
								// quotient.base[resultCosetIndex] contains the resultFunction, and so, it is the result of two cosets (i.e. quotient.base[i] and quotient.base[j]
								found = true;		
								break;
							}
						}
						if (found == true) break;
					}
					if (resultCosetIndex >= base.length) {
						// Can not find the result coset, and so we can not get a quotient group of the given normalSubgroup. In general, such case occurs due to the given
						// parameter normalSubgroup is not a normal subgroup indeed!
						return false;    
					}
					optable[i][j] = resultCosetIndex;
				}
			}
			operator = new BinaryOperator<Coset>(base, optable);
			operator.setName("\\circ ");
			return true;
		}
	}
	
	public static class Coset {
		protected Bijection[] coset = null;
		
		private Coset(Bijection[] coset) {
			this.coset = coset;
		}
		public Bijection[] getCoset() {
			return coset;
		}
		
		public int length() {
			return coset.length;
		}
		
		public Bijection get(int index) {
			return coset[index];
		}
		
		@Override
		public String toString() {
			return "\\{ " + FunctionArrayToString(coset) + "\\}";
		}
	}
}
