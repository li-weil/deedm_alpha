/**
 * 
 */
package com.deedm.legacy.algebra;

import java.util.List;

import setrelfun.PartialOrder;
import setrelfun.Set;

public class AlgebraExampleSolver {

	public static void Problem10_11() {
		Character[] base = {'e', 'a', 'b', 'c'};
		Character[][] operatorTable = {	{'e', 'a', 'b', 'c'}, 
									{'a', 'e', 'c', 'b'}, 
									{'b', 'c', 'e', 'a'}, 
									{'c', 'b', 'a', 'e'} };
		
		BinaryOperator<Character> operator = new BinaryOperator<Character>(base, operatorTable);
		
		if (operator.isCommutative()) System.out.println("The operator is commutative!");
		else System.out.println("The operator is NOT commutative!");

		if (operator.isAssociative()) System.out.println("The operator is associative!");
		else System.out.println("The operator is NOT associative!");
		
		if (operator.isIdempotent()) System.out.println("The operator is idempotent!");
		else System.out.println("The operator is NOT idempotent!");
		
		if (operator.hasIdentity()) {
			char identity = operator.getIdentity();
			System.out.println("The operator has identity element : " + identity);
		} else System.out.println("The operator has Not identity element!");

		if (operator.hasZeroElement()) {
			char zero = operator.getZeroElement();
			System.out.println("The operator has zero element : " + zero);
		} else System.out.println("The operator has Not zero element!");
		
		for (int i = 0; i < base.length; i++) {
			if (operator.hasInverse(base[i])) {
				char inverse = operator.getInverse(base[i]);
				System.out.println("Element " + base[i] + " has inverse element : " + inverse);
			} else System.out.println("Element " + base[i] + " has NOT inverse element!");
		}
		
		if (operator.isCancellative()) System.out.println("The operator is cancellative!");
		else System.out.println("The operator is NOT cancellative!");
	}
	
	public static void Example10_35() {
		GroupUnitModulo U7 = new GroupUnitModulo(7);
		Integer[] elements = U7.getElements();
		
		System.out.println("U7 = {" + GroupUnitModulo.integerArrayToString(elements) + "}");
		if (U7.isCycleGroup()) {
			List<Integer> generator = U7.getGenerator();
			System.out.println("U7 is a cycle group, generator : " + GroupUnitModulo.integerListToString(generator));
		} else System.out.println("U7 is not a cycle group!");
		
		for (int i = 0; i < elements.length; i++) {
			int order = U7.getOrder(elements[i]);
			System.out.println("\tThe order of the element " + elements[i] + " is " + order);
		}
		List<List<Integer>> allSubGroups = U7.getAllGeneratedSubgroup();
		for (List<Integer> subgroup : allSubGroups) {
			System.out.println("Sub group: {" + GroupUnitModulo.integerListToString(subgroup) + "}");
			List<int[]> allCosets = U7.getAllCoset(subgroup);
			System.out.println("\tIts coset include: ");
			for (int[] coset : allCosets) System.out.println("\t\t{" + GroupUnitModulo.integerArrayToString(coset) + "}");
		}
	}

	public static void Example10_36() {
		char[][] functionValue = {	{'1', '2', '3'}, {'2', '1', '3'}, {'3', '2', '1'}, {'1', '3', '2'}, {'2', '3', '1'}, {'3', '1', '2'} };
		
		GroupPermutation.Bijection[] elements = new GroupPermutation.Bijection[functionValue.length];
		for (int i = 1; i <= functionValue.length; i++) {
			elements[i-1] = new GroupPermutation.Bijection("f"+i, functionValue[i-1]);
		}
		GroupPermutation SGroup = new GroupPermutation(elements);
		
		System.out.println("S3 = {" + GroupPermutation.FunctionArrayToString(elements) + "}");
		for (int i = 0; i < elements.length; i++) {
			System.out.println("\t" + elements[i].toFullString());
		}
		System.out.println("Operation Table: ");
		SGroup.printlnOperationTable();
		System.out.println();
		System.out.println("The power of elements:");
		for (int i = 0; i < elements.length; i++) {
			StringBuffer message = new StringBuffer();
			GroupPermutation.Bijection inverse = SGroup.getInverse(elements[i]);
			message.append("\t" + elements[i].getName() + "^{-1}=" + inverse.getName() + "\t");
			for (int j = 1; j <= elements.length; j++) {
				GroupPermutation.Bijection power = SGroup.power(elements[i], j);
				message.append(elements[i].getName() + "^" + j + "=" + power.getName() + "\t");
				if (power == SGroup.getIdentity()) break;
			}
			System.out.println(message.toString());
		}
		
		if (SGroup.isCycleGroup()) {
			GroupPermutation.Bijection[] generator = GroupPermutation.FunctionListToArray(SGroup.getGenerator());
			System.out.println("S3 is a cycle group, generator : " + GroupPermutation.FunctionArrayToString(generator));
		} else System.out.println("S3 is not a cycle group!");
		
		for (int i = 0; i < elements.length; i++) {
			int order = SGroup.getOrder(elements[i]);
			System.out.println("\tThe order of the element " + elements[i].getName() + " is " + order);
		}
		List<List<GroupPermutation.Bijection>> allSubGroups = SGroup.getAllGeneratedSubgroup();
		for (List<GroupPermutation.Bijection> subgroup : allSubGroups) {
			System.out.println("Sub group: {" + GroupPermutation.FunctionListToString(subgroup) + "}");
			List<GroupPermutation.Coset> allCosets = SGroup.getAllLeftCoset(subgroup);
			System.out.println("\tIts left coset include: ");
			for (GroupPermutation.Coset coset : allCosets) System.out.println("\t\t{" + coset + "}");
			allCosets = SGroup.getAllRightCoset(subgroup);
			System.out.println("\tIts right coset include: ");
			for (GroupPermutation.Coset coset : allCosets) System.out.println("\t\t{" + coset + "}");
		}
	}
	
	public static void problem10_50() {
		int n = 110;
		PartialOrder relation = PartialOrder.createFactorDivisionOrder(n);
		Lattice lattice = Lattice.createLatticeFromPoset(relation);
		if (lattice.isBooleanAlgebra()) {
			System.out.println("The lattice F(" + n + ") is a boolean algebra!");
			char greastElement = lattice.getGreatestElement();
			System.out.println("\tThe greatest element is " + Set.getElementLabel(greastElement, true));
			char leastElement = lattice.getLeastElement();
			System.out.println("\tThe least element is " + Set.getElementLabel(leastElement, true));
			Set base = lattice.getFromSet();
			for (int i = 0; i < base.length(); i++) {
				char element = base.get(i);
				char complement = lattice.getComplement(element);
				System.out.println("\tThe complement of element " + Set.getElementLabel(element, true) + " is " + Set.getElementLabel(complement, true));
			}
		} else {
			System.out.println("The lattice F(" + n + ") is NOT a boolean algebra!");
		}
	}
	
	
	public static void checkGroupUnitModulo() {
		
		for (int n = 9; n < 200; n++) {
			GroupUnitModulo UGroup = new GroupUnitModulo(n);
			Integer[] elements = UGroup.getElements();
			System.out.println("U(" + n + "), elements number: " + elements.length);
			if (elements.length != 18) continue;
			
			System.out.println("U" + n + " = {" + GroupUnitModulo.integerArrayToString(elements) + "}");

			boolean found = false;
			for (int i = 0; i < elements.length; i++) {
				int order = UGroup.getOrder(elements[i]);
				if (order == 6) {
					System.out.println("\tThe order of the element " + elements[i] + " is " + order);
					found = true;
					break;
				}
			}
			if (!found) {
				System.out.println("\tHas not 6 order elements:");
				for (int i = 0; i < elements.length; i++) {
					int order = UGroup.getOrder(elements[i]);
					System.out.println("\t\tThe order of the element " + elements[i] + " is " + order);
				}
				return;
			}
		}
	}
	
	public static void main(String[] args) {
//		Problem10_11();
//		Example10_35();
//		Example10_36();
//		problem10_50();
		checkGroupUnitModulo();
	}
}
