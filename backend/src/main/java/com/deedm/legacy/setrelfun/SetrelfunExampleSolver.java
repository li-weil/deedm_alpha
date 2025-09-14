/**
 * 
 */
package com.deedm.legacy.setrelfun;

import java.io.IOException;
import java.io.PrintWriter;

import graph.AbstractGraph;

/**
 * @author user
 *
 */
public class SetrelfunExampleSolver {

	public static void problem6_10() {
		char[] elementsA = {'1', '2', '3', '4'};
		Set A = new Set(elementsA);
		
		Relation R = new Relation(A);
		R.addPair(new OrderedPair('1', '1'));
		R.addPair(new OrderedPair('1', '3'));
		R.addPair(new OrderedPair('2', '2'));
		R.addPair(new OrderedPair('2', '4'));
		R.addPair(new OrderedPair('3', '1'));
		R.addPair(new OrderedPair('3', '3'));
		R.addPair(new OrderedPair('4', '2'));
		R.addPair(new OrderedPair('4', '4'));

		Relation S = new Relation(A);
		S.addPair(new OrderedPair('1', '1'));
		S.addPair(new OrderedPair('1', '4'));
		S.addPair(new OrderedPair('2', '2'));
		S.addPair(new OrderedPair('3', '3'));
		S.addPair(new OrderedPair('4', '1'));
		S.addPair(new OrderedPair('4', '4'));
		
		Matrix matrixR = R.getMatrix();
		Matrix matrixS = S.getMatrix();
		
		Matrix matrixRcupS = matrixR.logicAdd(matrixS);
		Relation RcupS = new Relation(A);
		RcupS.setPairs(matrixRcupS);
		
		Matrix matrixRcapS = matrixR.logicAnd(matrixS);
		Relation RcapS = new Relation(A);
		RcapS.setPairs(matrixRcapS);

		Matrix matrixRsubS = matrixR.logicDifference(matrixS);
		Relation RsubS = new Relation(A);
		RsubS.setPairs(matrixRsubS);

		Matrix matrixScircR = matrixR.logicProduct(matrixS);
		Relation ScircR = new Relation(A);
		ScircR.setPairs(matrixScircR);

		Matrix matrixRcircS = matrixS.logicProduct(matrixR);
		Relation RcircS = new Relation(A);
		RcircS.setPairs(matrixRcircS);
		
		System.out.println("R = " + R);
		System.out.println("S = " + S);
		
		System.out.println("R \\cup S ~=&~~ " + RcupS);
		System.out.println("R \\cap S ~=&~~ " + RcapS);
		System.out.println("R - S ~=&~~ " + RsubS);
		System.out.println("S \\circ R ~=&~~ " + ScircR);
		System.out.println("R \\circ S ~=&~~ " + RcircS);

		Relation RcircSp = S.composite(R);
		Relation ScircRp = R.composite(S);
		System.out.println("S \\circ R ~=&~~ " + ScircRp);
		System.out.println("R \\circ S ~=&~~ " + RcircSp);
		
		if (ScircR.equalsTo(ScircRp)) System.out.println("Equal!!");
		if (RcircS.equalsTo(RcircSp)) System.out.println("Equal!!");
		
		System.out.println("R \\cup S ~=&~~ " + RcupS.toLaTeXString());
		System.out.println("R \\cap S ~=&~~ " + RcapS.toLaTeXString());
		System.out.println("R - S ~=&~~ " + RsubS.toLaTeXString());
		System.out.println("S \\circ R ~=&~~ " + ScircRp.toLaTeXString());
		System.out.println("R \\circ S ~=&~~ " + RcircSp.toLaTeXString());
	}
	
	public static void example6_8() {
		char[] elementsA = {'1', '2', '3', '4'};
		char[] elementsB = {'a', 'b', 'c', 'd'};
		char[] elementsC = {'x', 'y', 'z'};
		
		Set A = new Set(elementsA);
		Set B = new Set(elementsB);
		Set C = new Set(elementsC);
		
		Relation R = new Relation(A, B);
		R.addPair(new OrderedPair('1', 'a'));
		R.addPair(new OrderedPair('1', 'b'));
		R.addPair(new OrderedPair('2', 'a'));
		R.addPair(new OrderedPair('3', 'b'));
		R.addPair(new OrderedPair('3', 'd'));
		R.addPair(new OrderedPair('4', 'b'));
		
		Relation S = new Relation(B, C);
		S.addPair(new OrderedPair('a', 'x'));
		S.addPair(new OrderedPair('b', 'y'));
		S.addPair(new OrderedPair('b', 'z'));
		S.addPair(new OrderedPair('c', 'x'));
		S.addPair(new OrderedPair('d', 'x'));
		
		Relation Rinv = R.inverse();
		Relation Sinv = S.inverse();
		
		Relation ScomR = R.composite(S);
		Relation ScomRinv = ScomR.inverse();
		Relation RinvComSinv = Sinv.composite(Rinv);
		
		System.out.println("R^{-1} = " + Rinv);
		System.out.println("S^{-1} = " + Sinv);
		System.out.println("S \\circ R = " + ScomR);
		System.out.println("R^{-1} \\circ S^{-1} = " + RinvComSinv);
		if (ScomRinv.equalsTo(RinvComSinv)) System.out.println("(S \\circ R)^{-1} = R^{-1} \\circ S^{-1}");
		else System.out.println("(S \\circ R)^{-1} != R^{-1} \\circ S^{-1}");
		System.out.println("S \\circ R = " + ScomR.toLaTeXString());
		System.out.println("R^{-1} \\circ S^{-1} = " + RinvComSinv.toLaTeXString());
	}
	
	public static void example9_17() {
		int[][] data = {{0, 1, 0, 0, 0}, {0, 0, 1, 0, 0}, {1, 0, 0, 0, 2}, 
				{1, 1, 0, 0, 1}, {0, 0, 1, 0, 0}};
		Matrix matrix = new Matrix(data);
		Matrix bmatrix = new Matrix(data);
		Matrix matrix2 = matrix.product(matrix);
		bmatrix = bmatrix.add(matrix2);
		Matrix matrix3 = matrix2.product(matrix);
		bmatrix = bmatrix.add(matrix3);
		Matrix matrix4 = matrix3.product(matrix);
		bmatrix = bmatrix.add(matrix4);
		
		System.out.println("matrix: "); 
		System.out.println(matrix);

		System.out.println("matrix2: "); 
		System.out.println(matrix2);

		System.out.println("matrix3: "); 
		System.out.println(matrix3);

		System.out.println("matrix4: "); 
		System.out.println(matrix4);

		System.out.println("bmatrix: "); 
		System.out.println(bmatrix);
	}
	
	public static void example6_25() {
		char[] elementsA = {'1', '2', '3', '4'};
		Set A = new Set(elementsA);
		
		Relation R = new Relation(A);
		R.addPair(new OrderedPair('4', '3'));
		R.addPair(new OrderedPair('2', '1'));
		R.addPair(new OrderedPair('3', '4'));
		R.addPair(new OrderedPair('1', '1'));
		R.addPair(new OrderedPair('2', '3'));
		
		System.out.println("R = " + R);
		if (R.isReflexive()) System.out.println("R is reflexive!");
		else System.out.println("R is NOT reflexive!");
		if (R.isIrreflexive()) System.out.println("R is irreflexive!");
		else System.out.println("R is NOT irreflexive!");
		if (R.isSymmetric()) System.out.println("R is symmetric!");
		else System.out.println("R is NOT symmetric!");
		if (R.isAntisymmetric()) System.out.println("R is anti-symmetric!");
		else System.out.println("R is NOT anti-symmetric!");
		if (R.isAntisymmetric()) System.out.println("R is transitive!");
		else System.out.println("R is NOT transitive!");
		
		Relation rR = R.reflexiveClosure();
		System.out.println("r(R) = " + rR);
		if (rR.isReflexive()) System.out.println("r(R) is reflexive!");
		else System.out.println("r(R) is NOT reflexive!");
		
		Relation sR = R.symmetricClosure();
		System.out.println("s(R) = " + sR);
		if (sR.isSymmetric()) System.out.println("s(R) is symmetric!");
		else System.out.println("s(R) is NOT symmetric!");
		
		Relation tRc = R.transitiveClosureByComposition();
		System.out.println("By composition: t(R) = " + tRc);
		if (tRc.isTransitive()) System.out.println("t(R) (got by composition) is transitive!");
		else System.out.println("t(R) (got by composition) is NOT transitive!");
		
		Relation tRw = R.transitiveClosureByWarshallAlgorithm();
		System.out.println("By Warshall: t(R) = " + tRw);
		if (tRw.isTransitive()) System.out.println("t(R) (got by Warshall algorithm) is transitive!");
		else System.out.println("t(R) is NOT transitive!");
	}
	
	public static void problem6_26() {
		char[] elementsA = {'1', '2', '3', '4'};
		Set A = new Set(elementsA);
		
		Relation R = new Relation(A);
		R.addPair(new OrderedPair('3', '4'));
		R.addPair(new OrderedPair('3', '1'));
		R.addPair(new OrderedPair('4', '3'));
		R.addPair(new OrderedPair('2', '2'));
		R.addPair(new OrderedPair('1', '2'));

		System.out.println("R = " + R);
		if (R.isReflexive()) System.out.println("R is reflexive!");
		else System.out.println("R is NOT reflexive!");
		if (R.isIrreflexive()) System.out.println("R is irreflexive!");
		else System.out.println("R is NOT irreflexive!");
		if (R.isSymmetric()) System.out.println("R is symmetric!");
		else System.out.println("R is NOT symmetric!");
		if (R.isAntisymmetric()) System.out.println("R is anti-symmetric!");
		else System.out.println("R is NOT anti-symmetric!");
		if (R.isAntisymmetric()) System.out.println("R is transitive!");
		else System.out.println("R is NOT transitive!");
		
		Relation rR = R.reflexiveClosure();
		System.out.println("r(R) = " + rR);
		if (rR.isReflexive()) System.out.println("r(R) is reflexive!");
		else System.out.println("r(R) is NOT reflexive!");
		
		Relation sR = R.symmetricClosure();
		System.out.println("s(R) = " + sR);
		if (sR.isSymmetric()) System.out.println("s(R) is symmetric!");
		else System.out.println("s(R) is NOT symmetric!");
		
		Relation tRc = R.transitiveClosureByComposition();
		System.out.println("By composition: t(R) = " + tRc);
		if (tRc.isTransitive()) System.out.println("t(R) (got by composition) is transitive!");
		else System.out.println("t(R) (got by composition) is NOT transitive!");
		
		Relation tRw = R.transitiveClosureByWarshallAlgorithm();
		System.out.println("By Warshall: t(R) = " + tRw);
		if (tRw.isTransitive()) System.out.println("t(R) (got by Warshall algorithm) is transitive!");
		else System.out.println("t(R) is NOT transitive!");
	}

	public static void problem6_42() {
		int[] numbers = {1, 2, 3, 4, 6, 9, 12, 18};
		PartialOrder relation = PartialOrder.createDivisionOrder(numbers);
		
		String rootPath = "E:\\";
		String path = rootPath + "ZxcWork\\DiscreteTools\\data\\";
		String fileName = "result.txt";
		try {
			PrintWriter writer = new PrintWriter(path + fileName);
			AbstractGraph graph = relation.getRelationGraph(true);
			graph.simplyWriteToDotFile(writer);
			
			graph = relation.getHasseDigram(true);
			graph.simplyWriteToDotFile(writer);
			
			writer.close();
			
			Set baseSet = relation.getFromSet();
			char[] maxElements = new char[baseSet.length()];
			int maxNumber = relation.getMaximalElement(baseSet, maxElements);
			if (maxNumber > 0) {
				System.out.println("The maximail elements include " + charArrayToString(maxElements, maxNumber));
			} else System.out.println("There is no maximail elements!");
			char[] minElements = new char[baseSet.length()];
			int minNumber = relation.getMinimalElement(baseSet, minElements);
			if (minNumber > 0) {
				System.out.println("The minimail elements include " + charArrayToString(minElements, minNumber));
			} else System.out.println("There is no minimail elements!");
			if (relation.hasGreatestElement(baseSet)) {
				char greatestElement = relation.getGreatestElement(baseSet);
				System.out.println("The greatest element is " + greatestElement);
			} else System.out.println("There is no greatest element!");
			if (relation.hasLeastElement(baseSet)) {
				char leastElement = relation.getLeastElement(baseSet);
				System.out.println("The least element is " + (int)leastElement);
			} else System.out.println("There is no least element!");
			
			char[] subsetOneElement = {(char)4, (char)6, (char)9 };
			Set subsetOne = new Set(subsetOneElement);
			char[] subsetTwoElement = {(char)2, (char)3, (char)6 };
			Set subsetTwo = new Set(subsetTwoElement);
			
			System.out.println("For subset one: " + charArrayToString(subsetOneElement, subsetOneElement.length));
			char[] upperBounds = new char[baseSet.length()];
			int upperNumber = relation.getUpperBound(subsetOne, upperBounds);
			if (upperNumber > 0) {
				System.out.println("\tUpper bound: " + charArrayToString(upperBounds, upperNumber));
			} else System.out.println("\tNo upper bound!");
			if (relation.hasLeastUpperBound(subsetOne)) {
				int lub = relation.getLeastUpperBound(subsetOne);
				System.out.println("\tLeast upper bound: " + lub);
			} else System.out.println("\tNo least upper bound!");
			char[] lowerBounds = new char[baseSet.length()];
			int lowerNumber = relation.getLowerBound(subsetOne, lowerBounds);
			if (lowerNumber > 0) {
				System.out.println("\tLower bound: " + charArrayToString(lowerBounds, lowerNumber));
			} else System.out.println("\tNo lower bound!");
			if (relation.hasGreatestLowerBound(subsetOne)) {
				int glb = relation.getGreatestLowerBound(subsetOne);
				System.out.println("\tGreatest lower bound: " + glb);
			} else System.out.println("\tNo greatest lower bound!");
			
			System.out.println("For subset one: " + charArrayToString(subsetTwoElement, subsetTwoElement.length));
			upperBounds = new char[baseSet.length()];
			upperNumber = relation.getUpperBound(subsetTwo, upperBounds);
			if (upperNumber > 0) {
				System.out.println("\tUpper bound: " + charArrayToString(upperBounds, upperNumber));
			} else System.out.println("\tNo upper bound!");
			if (relation.hasLeastUpperBound(subsetTwo)) {
				int lub = relation.getLeastUpperBound(subsetTwo);
				System.out.println("\tLeast upper bound: " + lub);
			} else System.out.println("\tNo least upper bound!");
			lowerBounds = new char[baseSet.length()];
			lowerNumber = relation.getLowerBound(subsetTwo, lowerBounds);
			if (lowerNumber > 0) {
				System.out.println("\tLower bound: " + charArrayToString(lowerBounds, lowerNumber));
			} else System.out.println("\tNo lower bound!");
			if (relation.hasGreatestLowerBound(subsetTwo)) {
				int glb = relation.getGreatestLowerBound(subsetTwo);
				System.out.println("\tGreatest lower bound: " + glb);
			} else System.out.println("\tNo greatest lower bound!");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
//		example6_8();
//		problem6_10();
//		example9_17();
//		example6_25();
//		problem6_26();
		problem6_42();
	}

	public static String charArrayToString(char[] charArray, int number) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < number; i++) buffer.append(Set.getElementLabel(charArray[i], true) + " ");
		return buffer.toString();
	}
}
