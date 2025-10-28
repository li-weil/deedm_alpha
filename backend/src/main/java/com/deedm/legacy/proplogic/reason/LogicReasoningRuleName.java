/**
 * 
 */
package com.deedm.legacy.proplogic.reason;

/**
 * @author Zhou Xiaocong
 * @Since 2023/06/23
 *
 */
public class LogicReasoningRuleName {
	public static final LogicReasoningRuleName Premise = new LogicReasoningRuleName("Premise", "ǰ��", "\\textbf{P}");
	public static final LogicReasoningRuleName AddPremise = new LogicReasoningRuleName("Additional Premise", "����ǰ��", "\\textbf{AP}");
	public static final LogicReasoningRuleName APMethod = new LogicReasoningRuleName("Proof by Additional Premise", "����ǰ�ᷨ", "\\textbf{PBAP}");
	public static final LogicReasoningRuleName Contradict = new LogicReasoningRuleName("Proof by Contradiction", "��֤��", "\\textbf{PBC}");
	public static final LogicReasoningRuleName ImpIntr = new LogicReasoningRuleName("Implication Introduction", "�̺�����", "\\rightarrow+");
	public static final LogicReasoningRuleName ImpElim = new LogicReasoningRuleName("Implication Elimination", "��������", "\\rightarrow-");
	public static final LogicReasoningRuleName ImpSyl = new LogicReasoningRuleName("Implication Syllogism", "����������", "\\rightarrow3");
	public static final LogicReasoningRuleName ImpNeg = new LogicReasoningRuleName("Contraposition Law", "������λ", "\\rightarrow\\neg");
	public static final LogicReasoningRuleName ConIntr = new LogicReasoningRuleName("Conjunction Introduction", "��ȡ����", "\\wedge+");
	public static final LogicReasoningRuleName ConElim = new LogicReasoningRuleName("Conjunction Elimination", "�������", "\\wedge-");
	public static final LogicReasoningRuleName DisIntr = new LogicReasoningRuleName("Disjunction Introduction", "���ӹ���", "\\vee+");
	public static final LogicReasoningRuleName DisElim = new LogicReasoningRuleName("Disjunction Elimination", "��ȡ����", "\\vee-");
	public static final LogicReasoningRuleName DisSyl = new LogicReasoningRuleName("Disjunction Syllogism", "��ȡ������", "\\vee\\neg");
	public static final LogicReasoningRuleName BiImpIntr = new LogicReasoningRuleName("BiImplication Introduction", "˫�̺�����", "\\leftrightarrow+");
	public static final LogicReasoningRuleName BiImpElim = new LogicReasoningRuleName("BiImplication Elimination", "˫�̺�����", "\\leftrightarrow-");
	public static final LogicReasoningRuleName Equiv = new LogicReasoningRuleName("Equivalence", "��ֵ�û�", "\\textbf{EQ}");

	private static LogicReasoningRuleName[] definedRuleNames = {
			// APMethod must before AddPremise, and AddPremise must before Premise, since the Chinese name of AddPremise included in the name of APMethod!
			APMethod, AddPremise, Premise, Contradict, ImpIntr, ImpElim, ImpSyl, ImpNeg, ConIntr, 
			ConElim, DisIntr, DisElim, DisSyl, BiImpIntr, BiImpElim, Equiv 
	};
	
	private String engName = null;
	private String chnName = null;
	private String latexString = null;
	
	private LogicReasoningRuleName(String engName, String chnName, String latexString) {
		this.engName = engName;
		this.chnName = chnName;
		this.latexString = latexString;
	}

	public String getEnglishName() {
		return engName;
	}

	public String getChineseName() {
		return chnName;
	}

	public String getLatexString() {
		return latexString;
	}
	
	public static LogicReasoningRuleName getRuleName(String cell) {
		for (int i = 0; i < definedRuleNames.length; i++) {
			LogicReasoningRuleName ruleName = definedRuleNames[i];
			if (cell.contains(ruleName.getChineseName()) || cell.contains(ruleName.getEnglishName()) || cell.contains(ruleName.getLatexString())) return ruleName;
		}
		return null;
	}
	
	public static LogicReasoningRuleName[] getDefinedRuleNames() {
		return definedRuleNames;
	}
}
