/**
 * 
 */
package com.deedm.legacy.algebra;

public class AlgebraExerciseSolver {

	public static void Exercise10_2() {
		Character[] base = {'a', 'b', 'c'};
		Character[][] opTableOne = {	{'a', 'b', 'c'}, 
								{'b', 'c', 'a'}, 
								{'c', 'a', 'b'} };
		
		BinaryOperator<Character> operatorOne = new BinaryOperator<Character>(base, opTableOne);
		Character[][] opTableTwo = {	{'a', 'b', 'c'}, 
								{'b', 'a', 'c'},
								{'c', 'c', 'c'} };
		BinaryOperator<Character> operatorTwo = new BinaryOperator<Character>(base, opTableTwo);
		
		System.out.println("Operator One: ");
		if (operatorOne.isCommutative()) System.out.println("The operator is commutative!");
		else System.out.println("The operator is NOT commutative!");

		if (operatorOne.isAssociative()) System.out.println("The operator is associative!");
		else System.out.println("The operator is NOT associative!");
		
		if (operatorOne.isIdempotent()) System.out.println("The operator is idempotent!");
		else System.out.println("The operator is NOT idempotent!");
		
		if (operatorOne.hasIdentity()) {
			char identity = operatorOne.getIdentity();
			System.out.println("The operator has identity element : " + identity);
		} else System.out.println("The operator has Not identity element!");

		if (operatorOne.hasZeroElement()) {
			char zero = operatorOne.getZeroElement();
			System.out.println("The operator has zero element : " + zero);
		} else System.out.println("The operator has Not zero element!");
		
		for (int i = 0; i < base.length; i++) {
			if (operatorOne.hasInverse(base[i])) {
				char inverse = operatorOne.getInverse(base[i]);
				System.out.println("Element " + base[i] + " has inverse element : " + inverse);
			} else System.out.println("Element " + base[i] + " has NOT inverse element!");
		}

		System.out.println("Operator Two: ");
		if (operatorTwo.isCommutative()) System.out.println("The operator is commutative!");
		else System.out.println("The operator is NOT commutative!");

		if (operatorTwo.isAssociative()) System.out.println("The operator is associative!");
		else System.out.println("The operator is NOT associative!");
		
		if (operatorTwo.isIdempotent()) System.out.println("The operator is idempotent!");
		else System.out.println("The operator is NOT idempotent!");
		
		if (operatorTwo.hasIdentity()) {
			char identity = operatorOne.getIdentity();
			System.out.println("The operator has identity element : " + identity);
		} else System.out.println("The operator has Not identity element!");

		if (operatorTwo.hasZeroElement()) {
			char zero = operatorTwo.getZeroElement();
			System.out.println("The operator has zero element : " + zero);
		} else System.out.println("The operator has Not zero element!");
		
		for (int i = 0; i < base.length; i++) {
			if (operatorTwo.hasInverse(base[i])) {
				char inverse = operatorTwo.getInverse(base[i]);
				System.out.println("Element " + base[i] + " has inverse element : " + inverse);
			} else System.out.println("Element " + base[i] + " has NOT inverse element!");
		}
		
		if (operatorOne.isDistributiveWith(operatorTwo)) {
			System.out.println("Operator one is distributive with operator two!");
		} else System.out.println("Operator one is NOT distributive with operator two!");
		if (operatorTwo.isDistributiveWith(operatorOne)) {
			System.out.println("Operator two is distributive with operator one!");
		} System.out.println("Operator two is NOT distributive with operator one!");
		
		if (operatorOne.isCommutative() && operatorTwo.isCommutative()) {
			if (operatorOne.isAbsorptiveWith(operatorTwo)) {
				System.out.println("Operator one is absorptive with operator two!");
			} else System.out.println("Operator one is NOT absorptive with operator two!");
		}
	}

	public static void main(String[] args) {
		Exercise10_2();
	}

}
