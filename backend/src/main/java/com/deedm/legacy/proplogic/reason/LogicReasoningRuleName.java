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
	public static final LogicReasoningRuleName Premise = new LogicReasoningRuleName("Premise", "前提", "\\textbf{P}");
	public static final LogicReasoningRuleName AddPremise = new LogicReasoningRuleName("Additional Premise", "附加前提", "\\textbf{AP}");
	public static final LogicReasoningRuleName APMethod = new LogicReasoningRuleName("Proof by Additional Premise", "附加前提法", "\\textbf{PBAP}");
	public static final LogicReasoningRuleName Contradict = new LogicReasoningRuleName("Proof by Contradiction", "反证法", "\\textbf{PBC}");
	public static final LogicReasoningRuleName ImpIntr = new LogicReasoningRuleName("Implication Introduction", "蕴涵引入", "\\rightarrow+");
	public static final LogicReasoningRuleName ImpElim = new LogicReasoningRuleName("Implication Elimination", "假言推理", "\\rightarrow-");
	public static final LogicReasoningRuleName ImpSyl = new LogicReasoningRuleName("Implication Syllogism", "假言三段论", "\\rightarrow3");
	public static final LogicReasoningRuleName ImpNeg = new LogicReasoningRuleName("Contraposition Law", "假言易位", "\\rightarrow\\neg");
	public static final LogicReasoningRuleName ConIntr = new LogicReasoningRuleName("Conjunction Introduction", "合取引入", "\\wedge+");
	public static final LogicReasoningRuleName ConElim = new LogicReasoningRuleName("Conjunction Elimination", "化简规则", "\\wedge-");
	public static final LogicReasoningRuleName DisIntr = new LogicReasoningRuleName("Disjunction Introduction", "附加规则", "\\vee+");
	public static final LogicReasoningRuleName DisElim = new LogicReasoningRuleName("Disjunction Elimination", "析取消除", "\\vee-");
	public static final LogicReasoningRuleName DisSyl = new LogicReasoningRuleName("Disjunction Syllogism", "析取三段论", "\\vee\\neg");
	public static final LogicReasoningRuleName BiImpIntr = new LogicReasoningRuleName("BiImplication Introduction", "双蕴涵引入", "\\leftrightarrow+");
	public static final LogicReasoningRuleName BiImpElim = new LogicReasoningRuleName("BiImplication Elimination", "双蕴涵消除", "\\leftrightarrow-");
	public static final LogicReasoningRuleName Equiv = new LogicReasoningRuleName("Equivalence", "等值置换", "\\textbf{EQ}");

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
