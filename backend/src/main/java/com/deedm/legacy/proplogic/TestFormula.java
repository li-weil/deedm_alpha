/**
 * 
 */
package com.deedm.legacy.proplogic;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import dataTable.DataTableManager;
import dataTable.DataTableUtil;
import proplogic.formula.Formula;
import proplogic.formula.Symbol;
import proplogic.formula.ASTGraph.FormulaASTGraph;
import proplogic.normalFormula.ConjunctiveNormalFormula;
import proplogic.normalFormula.DisjunctiveNormalFormula;
import proplogic.normalFormula.NormalFormulaCalculator;
import proplogic.normalFormula.PCNFormula;
import proplogic.normalFormula.PDNFormula;
import proplogic.normalFormula.ResolutionSolver;
import proplogic.normalFormula.SimpleConjunctiveFormula;
import proplogic.normalFormula.SimpleDisjunctiveFormula;
import util.Debug;

/**
 * @author zxc
 *
 */
public class TestFormula {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String resultFile = "E:\\ZxcWork\\Deedm\\data\\result.txt";
		PrintWriter writer = new PrintWriter(System.out);
		try {
//			writer = new PrintWriter(resultFile);
//			Debug.setWriter(writer);
//			Debug.setScreenOn();
		} catch (Exception exc) {
			exc.printStackTrace();
			return;
		}
		
//		Debug.setScreenOn();

//		testResolution();
		
//		testDualFormula();
		
		testRandomGenerateFormula();
		
//		testFormulaSyntax();
		
//		testTruthTable();

//		testCalculateNormalFormula();
		
//		testNormalForm(writer);
		
//		testExpandNormalForm(writer);
//		writer.close();
	}

	public static void testTruthTable() {
		String formulaString = "p\\rightarrow (q\\rightarrow r)\\vee\\neg (p\\wedge q)\\vee r";
		Formula formula = FormulaBuilder.buildFromLaTexFormulaString(formulaString);
		
		FormulaTruthTable truthTable = new FormulaTruthTable(formula);
		System.out.println("��ʽ$" + formula.toSimpleLaTeXString() + "$����ֵ�����£�");
		System.out.println(truthTable.createDetailedTruthTable());
	}

	public static void testResolution() {
//		String formulaString = "\\neg((\\neg p\\wedge\\neg q\\wedge r)\\vee(\\neg p\\wedge\\neg r)\\vee(\\neg p\\wedge q)\\vee p)";
		String formulaString = "(p\\vee q\\vee\\neg r)\\wedge(r\\vee s)\\wedge(\\neg p)\\wedge(\\neg q\\vee\\neg s)";
		Formula formula = FormulaBuilder.buildFromLaTexFormulaString(formulaString);
		System.out.println("��ʽ: " + formula.toSimpleLaTeXString());
		
		Formula CNF = NormalFormulaCalculator.calculateCNF(formula);
		ConjunctiveNormalFormula simpleCNF = ConjunctiveNormalFormula.convertAndSimplify(CNF);
		System.out.println("\t���ĺ�ȡ��ʽ: " + CNF.toSimpleLaTeXString());
		System.out.println("\t\t�����ĺ�ȡ��ʽ: " + simpleCNF.toFormula().toSimpleLaTeXString());
		
		ResolutionSolver solver = new ResolutionSolver();
		boolean result = solver.tryResolution(simpleCNF, true);
		System.out.println("������ʽ��������Ľ����: " + result + "����ϸ��������Ϣ����: ");
		System.out.println("\t�õ��������Ӿ�: " + solver.resultClausesLaTeXString());
		System.out.println("\t���е�����ʽ��: ");
		System.out.println(solver.resolventLaTeXString("\t\t"));
	}
	
	public static void testCalculateNormalFormula() {
//		String formulaString = FormulaBuilder.randomGenerateFormulaControlledByOperatorNumber(3, 5);
//		Formula formula = FormulaBuilder.buildFromSymbolFormulaString(formulaString);
		String formulaString = "((p\\vee q)\\wedge (p\\rightarrow r)\\leftrightarrow q)\\wedge p";
//		String formulaString = "p\\wedge q\\wedge r";
		Formula formula = FormulaBuilder.buildFromLaTexFormulaString(formulaString);
		System.out.println("������ɵĹ�ʽ��: " + formula.toSimpleLaTeXString());

		char[] varSet = formula.getAllVariables(); 
		PDNFormula pdnf = NormalFormulaCalculator.getPDNFByTruthTable(formula);
		System.out.println("\tͨ����ֵ��õ�������ȡ��ʽ��: " + pdnf.toNamedLaTeXString());

		Formula CNF = NormalFormulaCalculator.calculateCNF(formula);
		System.out.println("\tͨ���㷨�õ���ȡ��ʽ: " + CNF.toSimpleLaTeXString());
		ConjunctiveNormalFormula simpleCNF = ConjunctiveNormalFormula.convertAndSimplify(CNF);
		System.out.println("\t�����õ���ȡ��ʽ: " + simpleCNF.toFormula().toSimpleLaTeXString());
		PCNFormula pcnf = NormalFormulaCalculator.expandToPCNF(simpleCNF, varSet);
		System.out.println("\t��չΪ����ȡ��ʽ��: " + pcnf.toNamedLaTeXString());
		System.out.println("\t��Ӧ������ȡ��ʽ��: " + pcnf.toNamedPDNFLaTeXString());

		Formula DNF = NormalFormulaCalculator.calculateDNF(formula);
		System.out.println("\tͨ���㷨�õ���ȡ��ʽ: " + DNF.toSimpleLaTeXString());
		DisjunctiveNormalFormula simpleDNF = DisjunctiveNormalFormula.convertAndSimplify(DNF);
		System.out.println("\t�����õ���ȡ��ʽ: " + simpleDNF.toFormula().toSimpleLaTeXString());
		pdnf = NormalFormulaCalculator.expandToPDNF(simpleDNF, varSet);
		System.out.println("\t��չΪ����ȡ��ʽ��: " + pdnf.toNamedLaTeXString());
		System.out.println("\t��Ӧ������ȡ��ʽ��: " + pdnf.toNamedPCNFLaTeXString());

}
	
	public static void testExpandNormalForm(PrintWriter writer) {
		char[] varSet = {'p', 'q', 'r', 's'};
//		String[] formulaStringArray = {"(\\neg p\\vee q\\vee r)", "(\\neg r\\vee \\neg s)", "(q\\vee t)", "(\\neg q\\vee \\neg t)", 
//				"(\\neg p\\vee s)"};
		String[] formulaStringArray = {"(\\neg p)", "(p)"};
		boolean isDNF = false;
		String wholeString = getWholeString(formulaStringArray, isDNF);
		
		for (int i = 0; i < formulaStringArray.length; i++) {
			Formula formula = FormulaBuilder.buildFromLaTexFormulaString(formulaStringArray[i]);
			if (isDNF) {
				SimpleConjunctiveFormula simpleform = SimpleConjunctiveFormula.convert(formula);
				int[] codeArray = simpleform.getMinTermBinaryCode(varSet);
				
				DisjunctiveNormalFormula disjnormform = DisjunctiveNormalFormula.convert(formula);
				if (disjnormform != null) {
					PDNFormula pdnf = NormalFormulaCalculator.expandToPDNF(disjnormform, varSet);
					System.out.println("Expand " + formula.toLaTeXString() + "[" + toString(codeArray) + "] to pdnf : " + pdnf.toNamedLaTeXString());
				} else System.out.println("Formula " + formula.toLaTeXString() + " is not disjunctive normal formula!");
			} else {
				SimpleDisjunctiveFormula simpleform = SimpleDisjunctiveFormula.convert(formula);
				int[] codeArray = simpleform.getMaxTermBinaryCode(varSet);

				ConjunctiveNormalFormula conjnormform = ConjunctiveNormalFormula.convert(formula);
				if (conjnormform != null) {
					PCNFormula pcnf = NormalFormulaCalculator.expandToPCNF(conjnormform, varSet);
					System.out.println("Expand " + formula.toLaTeXString() + "[" + toString(codeArray) + "] to pcnf : " + pcnf.toNamedLaTeXString());
				} else System.out.println("Formula " + formula.toLaTeXString() + " is not conjunctive normal formula!");
			}
		}
		System.out.println();
		Formula formula = FormulaBuilder.buildFromLaTexFormulaString(wholeString);
		if (isDNF) {
			DisjunctiveNormalFormula disjnormform = DisjunctiveNormalFormula.convert(formula);
			if (disjnormform != null) {
				PDNFormula pdnf = NormalFormulaCalculator.expandToPDNF(disjnormform, varSet);
				System.out.println("Expand " + formula.toLaTeXString() + " to PDNF : " + pdnf.toNamedLaTeXString());
				System.out.println("\tAnd its PCNF : " + pdnf.toNamedPCNFLaTeXString());
			} else System.out.println("Formula " + formula.toLaTeXString() + " is not disjunctive normal formula!");
		} else {
			ConjunctiveNormalFormula conjnormform = ConjunctiveNormalFormula.convert(formula);
			if (conjnormform != null) {
				PCNFormula pcnf = NormalFormulaCalculator.expandToPCNF(conjnormform, varSet);
				System.out.println("Expand " + formula.toLaTeXString() + " to PDNF : " + pcnf.toNamedLaTeXString());
				System.out.println("\tAnd its PDNF : " + pcnf.toNamedPDNFLaTeXString());
			} else System.out.println("Formula " + formula.toLaTeXString() + " is not conjunctive normal formula!");
		}
	}

	private static String toString(int[] codeArray) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < codeArray.length; i++) {
			if (codeArray[i] == -1) buffer.append("-");
			else buffer.append(codeArray[i]);
		}
		return buffer.toString();
	}
	private static String getWholeString(String[] array, boolean isDNF) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(array[0]);
		for (int i = 1; i < array.length; i++) {
			if (isDNF) buffer.append(" " + Symbol.LATEX_OR + " " + array[i]);
			else buffer.append(" " + Symbol.LATEX_AND + " " + array[i]);
		}
		return buffer.toString();
	}
	
	public static void testNormalForm(PrintWriter writer) {
		char[] varSet = {'p', 'q', 'r', 's'};
		int[] codes = {0, 2, 5, 10, 13, 15};
		
		PDNFormula normFormOne = NormalFormulaCalculator.createByMintermCodes(codes, varSet);
		Formula formula = normFormOne.toFormula();
		System.out.println("PDNF(0, 2, 5, 12): " + formula);
		
		if (DisjunctiveNormalFormula.isDisjunctiveNormalFormula(formula)) System.out.println("Yes, it is a DNF!");
		else System.out.println("No, it is not a DNF!");
		if (PDNFormula.isPrincipleDisjunctiveNormalForm(formula, varSet)) System.out.println("Yes, it is a PDNF!");
		else System.out.println("No, it is not a PDNF!");
		
		String formulaString = "(\\neg p\\vee q)\\wedge(\\neg r\\vee s)";
		formula = FormulaBuilder.buildFromLaTexFormulaString(formulaString);
		DisjunctiveNormalFormula disjnormform = DisjunctiveNormalFormula.convert(formula);
		ConjunctiveNormalFormula conjnormform = ConjunctiveNormalFormula.convert(formula);
		
		if (disjnormform != null) {
			PDNFormula pdnf = NormalFormulaCalculator.expandToPDNF(disjnormform, varSet);
			System.out.println("Expand " + formula.toLaTeXString() + " to pdnf : " + pdnf.toNamedLaTeXString());
		} else System.out.println("Formula " + formula.toLaTeXString() + " is not disjunctive normal formula!");
		if (conjnormform != null) {
			PCNFormula pcnf = NormalFormulaCalculator.expandToPCNF(conjnormform, varSet);
			System.out.println("Expand " + formula.toLaTeXString() + " to pcnf : " + pcnf.toNamedLaTeXString());
		} else System.out.println("Formula " + formula.toLaTeXString() + " is not conjunctive normal formula!");
	}

	public static void testFormulaSyntax() {
		String formulaLaTeXString = "((r\\vee(p\\wedge(\\neg q)))\\wedge(r\\rightarrow(p\\wedge q)))";
		Formula form = FormulaBuilder.buildFromStrictLaTexFormulaString(formulaLaTeXString);
		System.out.println("��ʽ "+form.toLaTeXString()+" ���Լ�дΪ��");
		System.out.println("\t" + form.toSimpleLaTeXString());
		
		List<Formula> subformulas = form.getAllSubFormulas();
		System.out.println("��ʽ " + form.toLaTeXString() + "�������ӹ�ʽ����ȫ�γ����У����£�");
		for (Formula formula: subformulas) System.out.println("\t" + formula.toLaTeXString());
		
		subformulas = form.getAllSyntaxDifferentSubFormulas();
		System.out.println("��ʽ" + form.toLaTeXString() +"������γ��������£�");
		for (Formula formula: subformulas) System.out.println("\t" + formula.toLaTeXString());

		formulaLaTeXString = "p\\leftrightarrow q\\vee p\\rightarrow r\\wedge q";
		form = FormulaBuilder.buildFromLaTexFormulaString(formulaLaTeXString);
		String formula2LaTeXString = "(p\\rightarrow q)";
		String formula3LaTeXString = "(q\\wedge r)";
		List<Substitution> substitutionList = new ArrayList<Substitution>();
		Formula form2 = FormulaBuilder.buildFromStrictLaTexFormulaString(formula2LaTeXString);
		substitutionList.add(new Substitution('p', form2));
		Formula form3 = FormulaBuilder.buildFromStrictLaTexFormulaString(formula3LaTeXString);
		substitutionList.add(new Substitution('q', form3));

		System.out.println("��������滻 " + form.toLaTeXString() + "[" + form2.toLaTeXString() + "/p, " + form3.toLaTeXString() + "/q] �Ľ������ ��");
		Formula substitutionResult = form.getSubstitutionResult(substitutionList);
		System.out.println("\t" + substitutionResult.toLaTeXString());
		
		formulaLaTeXString = "((p\\rightarrow q)\\vee (q\\wedge(p\\rightarrow q)))";
		form = FormulaBuilder.buildFromStrictLaTexFormulaString(formulaLaTeXString);
		formula2LaTeXString = "(p\\rightarrow q)";
		formula3LaTeXString = "((\\neg p)\\vee q)";
		form2 = FormulaBuilder.buildFromStrictLaTexFormulaString(formula2LaTeXString);
		form3 = FormulaBuilder.buildFromStrictLaTexFormulaString(formula3LaTeXString);
		Formula replaceResult = form.getReplaceResult(form2, form3);
		System.out.println("�ӹ�ʽ�û� " + form.toLaTeXString() + "[" + form3.toLaTeXString() + "/" + form2.toLaTeXString() + "] �Ľ������ ��");
		System.out.println("\t" + replaceResult.toLaTeXString());
	}
	
	public static void testDualFormula() {
//		String formulaString = FormulaBuilder.randomGenerateFormulaLimitedToNegAndOrOperators(4, 6);
		String formulaString = "((p\\wedge r)\\vee q)\\wedge ((q\\wedge p)\\vee \\neg q)";
//		Formula form = FormulaBuilder.buildFromSymbolFormulaString(formulaString);
		Formula form = FormulaBuilder.buildFromLaTexFormulaString(formulaString);
		System.out.println("���ɵĴ���" + form.toString() + ", �õ��Ĺ�ʽ��" + form.toSimpleLaTeXString());
		if (form.isLimitedToNegAndOrOperators()) {
			Formula dualForm = form.getDulaFormula();
			List<TruthAssignment> variableList = form.getAllVariablesForAssignmentTruth();
			TruthAssignmentFunction assignment = TruthAssignmentFunction.randomAssignment(variableList);
			TruthAssignmentFunction negAssignment = assignment.getNegationFunction();
			
			boolean formValue = form.getTruthValue(assignment);
			boolean dualFormValue = dualForm.getTruthValue(assignment);
			boolean negValue = form.getTruthValue(negAssignment);
			boolean negDualValue = dualForm.getTruthValue(negAssignment);
			System.out.println("\t�����ʽֻ���з񶨡���ȡ����ȡ������������Ķ�ż��ʽ�ǣ�" + dualForm.toSimpleLaTeXString());
			System.out.println("\t��ֵ��ֵ����: " + assignment + ", ��񶨺���: " + negAssignment);
			System.out.println("\t\tԭ��ʽ����ֵ��ֵ�����µ���ֵ\\sigma(A)�� " + formValue + ", ��ż��ʽ�ڷ񶨺����µ���ֵ\\sigma-(A*)��  " + negDualValue);
			System.out.println("\t\t��ż��ʽ����ֵ��ֵ�����µ���ֵ\\sigma(A*)�� " + dualFormValue + ", ԭ��ʽ�ڷ񶨺����µ���ֵ\\sigma-(A)�� " + negValue);
		} else System.out.println("�����ʽ��ֻ���з񶨡���ȡ����ȡ�������");
	}

	public static void testRandomGenerateFormula() {
		int number = 5;
		int varNumber = 4;
		int operatorNumber = 8; 
		int leafNumber = 8; 
		System.out.println("ʹ�ÿ�����������㷨���趨 " + operatorNumber + "���������������� " + number + "����ʽ��");
		for (int i = 0; i < number; i++) {
			String formulaString = FormulaBuilder.randomGenerateFormulaControlledByOperatorNumber(varNumber, operatorNumber);
			Formula form = FormulaBuilder.buildFromSymbolFormulaString(formulaString);
			System.out.println("���ɵĴ���" + form.toString() + ", �õ��Ĺ�ʽ��" + form.toSimpleLaTeXString());
		}

		System.out.println("ʹ�ÿ���Ҷ�ӽڵ����㷨���趨 " + leafNumber + "��Ҷ�ӽڵ㣬������� " + number + "����ʽ��");
		for (int i = 0; i < number; i++) {
			Formula form = FormulaBuilder.randomGenerateFormulaControlledByLeafNumber(varNumber, leafNumber);
			System.out.println("��ʽ�Ĵ���" + form.toString() + ", ���ɵĹ�ʽ��" + form.toSimpleLaTeXString());
		}
	}
	
	public static void testFormula(PrintWriter writer) {
		String formula2LaTeXString = "(p\\wedge q)";
		String formula3LaTeXString = "(p\\rightarrow q)";
		List<Substitution> substitutionList = new ArrayList<Substitution>();
		Formula form2 = FormulaBuilder.buildFromStrictLaTexFormulaString(formula2LaTeXString);
		substitutionList.add(new Substitution('p', form2));
		Formula form3 = FormulaBuilder.buildFromStrictLaTexFormulaString(formula3LaTeXString);
		substitutionList.add(new Substitution('q', form3));
		
		int counter = 0;
		int total = 1;
		while (counter < total) {
//			int number = (int)(Math.random() *10) + 1;
//			String formulaString = Formula.randomGenerateFormula(number);
//			Formula form = Formula.buildFromSymbolFormulaString(formulaString);
			String formulaLaTeXString = "((p\\vee q)\\leftrightarrow (p\\vee r))\\leftrightarrow(p\\vee(q\\leftrightarrow r))";
//			String simpleLaTeXString = "\\neg (r\\rightarrow\\neg q)\\vee (p\\rightarrow \\neg r)";
			Formula form = FormulaBuilder.buildFromLaTexFormulaString(formulaLaTeXString);
			
			if (form == null) {
				Debug.println("Get null for string " + formulaLaTeXString);
				break;
			}
			Debug.println(formulaLaTeXString + " ===> " + form.toLaTeXString());
			List<Formula> subformulas = form.getAllSubFormulas();
			Debug.println("There are following sub-formulas in : " + form);
			for (Formula formula: subformulas) Debug.println("\t" + formula);
			
			subformulas = form.getAllSyntaxDifferentSubFormulas();
			Debug.println("There are following different sub-formulas in : " + form);
			for (Formula formula: subformulas) Debug.print("$" + formula.toLaTeXString() + "$, ");
			Debug.println("");

			List<TruthAssignment> assignmentList = form.getAllVariablesForAssignmentTruth();
			TruthAssignmentFunction function = new TruthAssignmentFunction();
			Debug.println("There are following variables in : " + form);
			for (TruthAssignment assignment: assignmentList) {
				Debug.print(assignment.getVariable() + " ");
				function.setTruthAssignment(assignment.getVariable(), false);
			}
			Debug.println("");
			boolean value = form.getTruthValue(function);
			Debug.println("The truth value of form " + form + " is " + value);

			Formula substitutionResult = form.getSubstitutionResult(substitutionList);
			String simpleLaTeXString = form.toSimpleLaTeXString();
			Debug.println("Use $" + form2.toSimpleLaTeXString() + "$ to substitution $p$, and $" + form3.toSimpleLaTeXString() + "$ to substitution $q$ in formula $" + simpleLaTeXString + "$:");
			Debug.println("\t$" + substitutionResult.toSimpleLaTeXString() + "$");
			
			FormulaTruthTable truthTable = new FormulaTruthTable(form, function);
			DataTableManager truthTableLineManager = truthTable.getDetailedTruthTableManager();
			FormulaASTGraph graph = FormulaASTGraph.createASTGraph(form, "propform2_2_1");
			truthTable = new FormulaTruthTable(form);
			DataTableManager truthTableManager = truthTable.getDetailedTruthTableManager();
			
			try {
				writer.println();
				graph.simplyWriteToDotFile(writer);
				DataTableUtil.writeDataLinesAsLatexTableLines(writer, truthTableLineManager, null);
				writer.println();
				writer.println("The truth table of " + form);
				DataTableUtil.writeDataLinesAsLatexTableLines(writer, truthTableManager, null);
			} catch (Exception exc) {
				exc.printStackTrace();
			}
			counter = counter+1;
		}
		
	}
}
