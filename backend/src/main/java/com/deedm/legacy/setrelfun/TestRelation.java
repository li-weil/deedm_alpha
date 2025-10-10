package com.deedm.legacy.setrelfun;

import java.io.IOException;
import java.io.PrintWriter;

import com.deedm.legacy.graph.AbstractGraph;

/**
 * @author zxc
 *
 */
public class TestRelation {

	public static void randomTestRelationProperties() {
		char[] elementsA = {'1', '2', '3', '4', '5'};
		Set A = new Set(elementsA);
		
		Relation R = Relation.randomGenerate(A, A, 10);

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
		
		if (!tRc.equalsTo(tRw)) System.out.println("Transitive Closure got by composition does NOT equal to the closure got by Warshall algorithm!");
	}
	
	public static void randomExample() {
		char[] elementsA = {'1', '2', '3', '4'};
		char[] elementsB = {'a', 'b', 'c', 'd'};
		char[] elementsC = {'x', 'y', 'z'};
		
		Set A = new Set(elementsA);
		Set B = new Set(elementsB);
		Set C = new Set(elementsC);
		
		Relation R = Relation.randomGenerate(A, B, 5);
		Relation S = Relation.randomGenerate(B, C, 5);
		
		Relation Rinv = R.inverse();
		Relation Sinv = S.inverse();
		
		Relation ScomR = R.composite(S);
		Relation ScomRinv = ScomR.inverse();
		Relation RinvComSinv = Sinv.composite(Rinv);

		System.out.println("R = " + R);
		System.out.println("S = " + S);
		
		System.out.println("R^{-1} = " + Rinv);
		System.out.println("S^{-1} = " + Sinv);
		System.out.println("S \\circ R = " + ScomR);
		System.out.println("R^{-1} \\circ S^{-1} = " + RinvComSinv);
		if (ScomRinv.equalsTo(RinvComSinv)) System.out.println("(S \\circ R)^{-1} = R^{-1} \\circ S^{-1}");
		else System.out.println("(S \\circ R)^{-1} != R^{-1} \\circ S^{-1}");

		System.out.println("R = " + R.toLaTeXString());
		System.out.println("S = " + S.toLaTeXString());
	}
	

	public static void testRelationGraph() {
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
//		randomExample();
//		randomTestRelationProperties();
		testRelationGraph();
	}
	
}
