package com.deedm.legacy.guiManager.logic;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.deedm.legacy.guiManager.ImagedTextAreaManager;
import com.deedm.legacy.guiManager.LaTeXFormulaInputManager;
import com.deedm.legacy.guiManager.MainSwingFrame;
import com.deedm.legacy.guiManager.PlainTextAreaManager;
import com.deedm.legacy.proplogic.FormulaBuilder;
import com.deedm.legacy.proplogic.FormulaTruthTable;
import com.deedm.legacy.proplogic.equiv.EquivCalculusRecorder;
import com.deedm.legacy.proplogic.equiv.EquivCalculusStep;
import com.deedm.legacy.proplogic.formula.Formula;
import com.deedm.legacy.proplogic.formula.Symbol;
import com.deedm.legacy.proplogic.normalFormula.ConjunctiveNormalFormula;
import com.deedm.legacy.proplogic.normalFormula.DisjunctiveNormalFormula;
import com.deedm.legacy.proplogic.normalFormula.ExpandNFRecorder;
import com.deedm.legacy.proplogic.normalFormula.ExpandNFStep;
import com.deedm.legacy.proplogic.normalFormula.NormalFormulaCalculator;
import com.deedm.legacy.proplogic.normalFormula.PCNFormula;
import com.deedm.legacy.proplogic.normalFormula.PDNFormula;
import com.deedm.legacy.setrelfun.SetrelfunUtil;

public class NormalFormulaCalculationUIManager {

	private static final int width = Math.max(2* MainSwingFrame.screenWidth/5, 1024)*(MainSwingFrame.screenResolution/96);
	private static final int height = Math.max(MainSwingFrame.screenHeight/3, 720)*(MainSwingFrame.screenResolution/96);
	private ImagedTextAreaManager imagedAreaManager = null;
	private PlainTextAreaManager textAreaManager = null;
	private LaTeXFormulaInputManager inputManager = null;
	private JTextField variableSetField = null;
	private JRadioButton orButton = null;
	private JRadioButton andButton = null;
	private JCheckBox cnfBox = null;
	private JCheckBox dnfBox = null;
	private JCheckBox pnfBox = null;
	private JCheckBox tableBox = null;
	private JCheckBox equivBox = null;
	private JCheckBox detailedBox = null;
	
	private int counter = 1;

	public NormalFormulaCalculationUIManager(PlainTextAreaManager plainManager, ImagedTextAreaManager imageManager) {
		imagedAreaManager = imageManager;
		textAreaManager = plainManager;
		
		JPanel buttonPanel = createButtonPanel();
		inputManager = new LaTeXFormulaInputManager(width, height, buttonPanel);
	}

	public void setFormula(String laTeXString) {
		inputManager.setFormula(laTeXString);
	}
	
	public void setFormulaList(String laTeXString) {
		inputManager.setFormulaList(laTeXString);
	}
	
	public void setVariableSet(String variableSetString) {
		variableSetField.setText(variableSetString);
	}
	
	public void setToBeDFN(boolean isDFN) {
		if (isDFN) orButton.setSelected(true);
		else andButton.setSelected(true);
	}
	
	public boolean showDialog(Component parent, String title) {
		return inputManager.showDialog(parent, title);
	}
	
	// 创建按钮及其所在的窗格
	private JPanel createButtonPanel() {
		// 创建一事件监听器，该监听器监听下述两个按钮上发生的事件
		SimpleListener listener = new SimpleListener();

		// 创建一临时性窗格，将这些按钮加入到该窗格，该窗格使用流式布局管理
		JPanel buttonPanelOne = new JPanel();
		buttonPanelOne.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 0));
		// 创建用于确定输入信息的“确定”按钮
		JButton actButton = new JButton("计算范式(Y)");
		actButton.setMnemonic('Y');					// 设置快捷键
		// 设置动作命令的名称，这可用来区分用户到底按了什么按钮
		actButton.setActionCommand("ok");			
		actButton.addActionListener(listener);		// 添加事件监听器
		buttonPanelOne.add(actButton);

		// 创建用于确定输入信息的“生成”按钮
		actButton = new JButton("随机生成(G)");
		actButton.setMnemonic('G');					// 设置快捷键
		// 设置动作命令的名称，这可用来区分用户到底按了什么按钮
		actButton.setActionCommand("generate");
		actButton.addActionListener(listener);		// 添加事件监听器
		buttonPanelOne.add(actButton);

		// 创建用于确定输入信息的“清除输入”按钮
		actButton = new JButton("清除输入(V)");
		actButton.setMnemonic('V');					// 设置快捷键
		// 设置动作命令的名称，这可用来区分用户到底按了什么按钮
		actButton.setActionCommand("remove");
		actButton.addActionListener(listener);		// 添加事件监听器
		buttonPanelOne.add(actButton);
		
		// 创建用于确定输入信息的“检查”按钮
		actButton = new JButton("检查合法性(K)");
		actButton.setMnemonic('K');					// 设置快捷键
		// 设置动作命令的名称，这可用来区分用户到底按了什么按钮
		actButton.setActionCommand("check");
		actButton.addActionListener(listener);		// 添加事件监听器
		buttonPanelOne.add(actButton);

		// 创建用于清理屏幕的按钮
		actButton = new JButton("清理屏幕(C)");
		actButton.setMnemonic('C');				// 设置快捷键
		// 设置动作命令的名称，这可用来区分用户到底按了什么按钮
		actButton.setActionCommand("clear");			
		actButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelOne.add(actButton);

		// 创建用于放弃输入信息的“放弃”按钮
		actButton = new JButton("返回(R)");
		actButton.setMnemonic('R');				// 设置快捷键
		// 设置动作命令的名称，这可用来区分用户到底按了什么按钮
		actButton.setActionCommand("cancel");			
		actButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelOne.add(actButton);

		// 创建给出教材示例的窗格与按钮
		JPanel buttonPanelTwo = new JPanel();
		buttonPanelTwo.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 0));
 		buttonPanelTwo.setBorder(BorderFactory.createTitledBorder("《离散数学基础》教材示例"));
		
		JButton exampleButton = new JButton("问题2.14(1)");
		exampleButton.setActionCommand("problem2_14_1");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("问题2.14(2)");
		exampleButton.setActionCommand("problem2_14_2");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("问题2.18");
		exampleButton.setActionCommand("problem2_18");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("问题2.30");
		exampleButton.setActionCommand("problem2_30");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("问题2.32");
		exampleButton.setActionCommand("problem2_32");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		// 创建给出变量集信息和范式信息的窗格与相关组件
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new GridLayout(2, 2, 20, 0));
		infoPanel.setBorder(BorderFactory.createTitledBorder("计算范式所需的相关信息"));

		// 创建一临时窗格用于放置提示输入的标签，以及用于输入姓名的组件
		JPanel tempPanel = new JPanel();
		tempPanel.setLayout(new GridLayout(1, 2, 0, 0));
		// 创建一个标签用于提示输入姓名信息
		JLabel promptLabel = new JLabel("命题变量集(S)：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+S可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('S');	
		// 创建输入姓名的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		variableSetField = new JTextField(16);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(variableSetField);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(variableSetField);
		infoPanel.add(tempPanel);
		
		tempPanel = new JPanel();
		tempPanel.setLayout(new GridLayout(1, 3, 0, 0));
		// 创建一个标签用于提示输入性别信息
		promptLabel = new JLabel("这些公式之间的逻辑关系是：", JLabel.RIGHT);
		// 创建用于将下两个广播按钮捆绑成一组的按钮组对象，同一组的广播按钮只能选中一个，
		// 不同组的广播按钮则可以同时选中不同的广播按钮
		ButtonGroup group = new ButtonGroup();
		// 创建选择性别的广播按钮
		orButton = new JRadioButton("析取关系");
		orButton.setSelected(true);		// 缺省时选中该广播按钮
		group.add(orButton);
		andButton = new JRadioButton("合取关系");
		group.add(andButton);
		tempPanel.add(promptLabel);
		tempPanel.add(orButton);			// 注意是添加广播按钮组件
		tempPanel.add(andButton);		// 而不是添加按钮组对象
		infoPanel.add(tempPanel);

		tempPanel = new JPanel();
		tempPanel.setLayout(new GridLayout(1, 3, 0, 0));
		dnfBox = new JCheckBox("计算析取范式");
		cnfBox = new JCheckBox("计算合取范式");
		cnfBox.setSelected(true);
		pnfBox = new JCheckBox("扩展成主范式");
		tempPanel.add(dnfBox);
		tempPanel.add(cnfBox);
		tempPanel.add(pnfBox);
		infoPanel.add(tempPanel);
		
		tempPanel = new JPanel();
		tempPanel.setLayout(new GridLayout(1, 3, 0, 0));
		equivBox = new JCheckBox("使用等值演算法");
		equivBox.setSelected(true);
		tableBox = new JCheckBox("用构造真值表法");
		detailedBox = new JCheckBox("给出计算过程信息");
		tempPanel.add(equivBox);
		tempPanel.add(tableBox);
		tempPanel.add(detailedBox);
		infoPanel.add(tempPanel);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.add(infoPanel);
		buttonPanel.add(buttonPanelTwo);
		buttonPanel.add(buttonPanelOne);
		
		return buttonPanel;
	}

	// 监听按钮的事件
	private class SimpleListener implements ActionListener {
		// 事件监听程序
		public void actionPerformed(ActionEvent evt) {
			String command = evt.getActionCommand();
			if (command.equals("ok")) {
				inputManager.setDialogStatus(true);
				inputManager.clearFeedback();
				String variableSetString = variableSetField.getText();
				boolean isDNF = true;
				if (andButton.isSelected()) isDNF = false;
				List<String> laTeXStringList = inputManager.getFormulaList();
				calculateNormalFormula(variableSetString, laTeXStringList, isDNF);
				counter = counter + 1;
				inputManager.updateFeedback();
				imagedAreaManager.revalidate();
			} else if (command.equals("cancel")) {
				inputManager.setDialogStatus(false);
				inputManager.setDialogVisible(false);
			} else if (command.equals("remove")) {
				inputManager.clearFormula();
			} else if (command.equals("generate")) {
				String variableSetString = variableSetField.getText();
				char[] varSet = SetrelfunUtil.extractSetElements(variableSetString, false);
				if (varSet == null) {
					JOptionPane.showMessageDialog(inputManager.getDialog(), "输入的命题变量集 " + variableSetString + " 不合法！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				} else {
					boolean isDNF = true;
					if (andButton.isSelected()) isDNF = false;
					StringBuffer buffer = new StringBuffer();
					String symbolString = null;
					for (int i = 0; i < 4; i++) {
						if (isDNF) symbolString = NormalFormulaCalculator.randomGenerateSimpleConjunctiveFormula(varSet);
						else symbolString = NormalFormulaCalculator.randomGenerateSimpleDisjunctiveFormula(varSet);
						Formula formula = FormulaBuilder.buildFromSymbolFormulaString(symbolString);
						if (formula != null) {
							buffer.append("(" + formula.toSimpleLaTeXString() + ")\n");
						}
					}
					String laTeXString = buffer.toString();
					inputManager.setFormulaList(laTeXString);
				}
			} else if (command.equals("check")) {
				inputManager.clearFeedback();
				String variableSetString = variableSetField.getText();
				boolean isDNF = true;
				if (andButton.isSelected()) isDNF = false;
				List<String> laTeXStringList = inputManager.getFormulaList();
				checkNormalFormula(variableSetString, laTeXStringList, isDNF);
				inputManager.updateFeedback();
			} else if (command.equals("problem2_14_1")) {
				String laTeXString = "\\neg(p\\rightarrow q)\\wedge(\\neg q\\leftrightarrow r)";
				inputManager.setFormulaList(laTeXString);
				variableSetField.setText("p, q, r");
				andButton.setSelected(true);
			} else if (command.equals("problem2_14_2")) {
				String laTeXString = "p\\wedge\\neg q\\wedge r";
				inputManager.setFormulaList(laTeXString);
				variableSetField.setText("p, q, r");
				orButton.setSelected(true);
			} else if (command.equals("problem2_18")) {
				String laTeXString = "p\\rightarrow q\nr\\rightarrow s";
				inputManager.setFormulaList(laTeXString);
				variableSetField.setText("p, q, r, s");
				andButton.setSelected(true);
			} else if (command.equals("problem2_30")) {
				String laTeXString = "p\\rightarrow q\\vee r\n\\neg(r\\wedge s)\n(q\\wedge\\neg t)\\vee(t\\wedge\\neg q)\n\\neg s\\rightarrow\\neg p";
				inputManager.setFormulaList(laTeXString);
				variableSetField.setText("p, q, r, s, t");
				andButton.setSelected(true);
			} else if (command.equals("problem2_32")) {
				String laTeXString = "p\\rightarrow q\n\\neg(q\\wedge r)\n\\neg(\\neg r\\wedge\\neg s)\ns\\rightarrow\\neg q";
				inputManager.setFormulaList(laTeXString);
				variableSetField.setText("p, q, r, s");
				andButton.setSelected(true);
			} else if (command.equals("clear")) {
				textAreaManager.clearContent();
				imagedAreaManager.clearContent();
				imagedAreaManager.revalidate();
			}
		}
		
		private boolean calculateNormalFormula(String variableSetString, List<String> laTeXStringList, boolean isDNF) {
			if (laTeXStringList.size() == 0) return false;
			char[] varSet = SetrelfunUtil.extractSetElements(variableSetString, false);
			if (varSet == null) {
				JOptionPane.showMessageDialog(inputManager.getDialog(), "输入的命题变量集 " + variableSetString + " 不合法！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			
			String wholeString = NormalFormulaCalculationUIManager.getWholeString(laTeXStringList, isDNF);
			Formula formula = FormulaBuilder.buildFromLaTexFormulaString(wholeString);
			if (formula == null) {
				for (String laTeXString : laTeXStringList) {
					formula = FormulaBuilder.buildFromLaTexFormulaString(laTeXString);
					if (formula == null) {
						JOptionPane.showMessageDialog(inputManager.getDialog(), "输入的字符串中包含不是合法公式的串 " + laTeXString + " !", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
						return false;
					}
				}
				JOptionPane.showMessageDialog(inputManager.getDialog(), "输入的字符串 " + wholeString + " 整体上不是合法的公式!", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			if (!cnfBox.isSelected() && !dnfBox.isSelected()) {
				JOptionPane.showMessageDialog(inputManager.getDialog(), "你没有选择计算合取范式，也没有选择计算析取范式!", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			
			if (cnfBox.isSelected()) {
				textAreaManager.appendContent(counter + ": Calculate conjunctive normal formula for " + formula.toSimpleLaTeXString() + "\n");
				imagedAreaManager.appendPlainStringAsNewLine(counter + ": 计算合取范式 ");
				imagedAreaManager.appendLaTeXStringToLastLine(formula.toSimpleLaTeXString());
				
				if (!tableBox.isSelected() || equivBox.isSelected()) {
					Formula calResult = NormalFormulaCalculator.calculateCNF(formula);
					ConjunctiveNormalFormula conjnormform = ConjunctiveNormalFormula.convertAndSimplify(calResult);
					Formula cnfFormula = conjnormform.toFormula();
					if (detailedBox.isSelected()) {
						textAreaManager.appendContent("    The calculation information as follows\n");
						imagedAreaManager.appendPlainStringAsNewLine("    计算合取范式的过程如下： ");
						EquivCalculusRecorder calculusRecorder = NormalFormulaCalculator.getCalculusRecorder();
						List<EquivCalculusStep> calculusStepList = calculusRecorder.getStepList();
						boolean isFirst = true;
						for (EquivCalculusStep step : calculusStepList) {
							Formula stepFormula = step.getFormula();
							String comments = step.getComments();
							if (isFirst) {
								imagedAreaManager.appendPlainStringAsNewLine("            ");
								textAreaManager.appendContentAsNewLine("\t\t\t");
								isFirst = false;
							} else {
								imagedAreaManager.appendPlainStringAsNewLine("        ");
								imagedAreaManager.appendLaTeXStringToLastLine("\\quad\\quad\\equiv\\quad\\quad");
								textAreaManager.appendContentAsNewLine("\t\t\\equiv");
							}
							imagedAreaManager.appendLaTeXStringToLastLine(stepFormula.toSimpleLaTeXString());
							imagedAreaManager.appendPlainStringToLastLine(" // " + comments);
							textAreaManager.appendContentToLastLine("~" + stepFormula.toSimpleLaTeXString() + " // " + comments);
						}
					}
					textAreaManager.appendContent("    Finally simplified to cnf" + cnfFormula.toSimpleLaTeXString() + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    最后化简得到合取范式： ");
					imagedAreaManager.appendLaTeXStringToLastLine(cnfFormula.toSimpleLaTeXString());
					
					if (pnfBox.isSelected()) {
						PCNFormula lastPCNF = NormalFormulaCalculator.expandToPCNF(conjnormform, varSet);
						
						textAreaManager.appendContent("    Continue to expand conjunctive normal formula " + cnfFormula.toSimpleLaTeXString() + "\n");
						imagedAreaManager.appendPlainStringAsNewLine("    继续扩展合取范式 ");
						imagedAreaManager.appendLaTeXStringToLastLine(cnfFormula.toSimpleLaTeXString());
						imagedAreaManager.appendPlainStringToLastLine("，变量集 ");
						imagedAreaManager.appendLaTeXStringToLastLine("\\{" + variableSetString + "\\}");

						if (detailedBox.isSelected()) {
							ExpandNFRecorder expandRecorder = NormalFormulaCalculator.getExpandRecorder();
							int type = expandRecorder.getType();
							List<ExpandNFStep> stepList = expandRecorder.getStepList();
							for (ExpandNFStep step : stepList) {
								Formula stepFormula = step.getFormula();

								textAreaManager.appendContent("\tExpand " + stepFormula.toSimpleLaTeXString() + "[" + step.getFormulaBinaryCodeString() + "] to max terms: " + step.getResultCodesNamedLaTeXString(type) + "\n");
								imagedAreaManager.appendPlainStringAsNewLine("    扩展简单析取式 ");
								imagedAreaManager.appendLaTeXStringToLastLine(stepFormula.toSimpleLaTeXString());
								imagedAreaManager.appendPlainStringToLastLine("[" + step.getFormulaBinaryCodeString() + "] 得到极大项 : ");
								imagedAreaManager.appendLaTeXStringToLastLine(step.getResultCodesNamedLaTeXString(type));
							}
						}
						textAreaManager.appendContent("\tObtain  PCNF: " + lastPCNF.toNamedLaTeXString() + "\n");
						textAreaManager.appendContent("\tAnd its PDNF: " + lastPCNF.toNamedPDNFLaTeXString() + "\n");
						
						imagedAreaManager.appendPlainStringAsNewLine("    最终的主合取范式是: ");
						imagedAreaManager.appendLaTeXStringToLastLine(lastPCNF.toNamedLaTeXString());
						imagedAreaManager.appendPlainStringAsNewLine("    相应的主析取范式是: ");
						imagedAreaManager.appendLaTeXStringToLastLine(lastPCNF.toNamedPDNFLaTeXString());
					}
				}
				
				if (tableBox.isSelected()) {
					textAreaManager.appendContent("    Use truth table to get PCNF for " + formula.toSimpleLaTeXString() + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    使用构造真值表得到主范式 ");
					imagedAreaManager.appendLaTeXStringToLastLine(formula.toSimpleLaTeXString());
					
					PCNFormula pcnf = NormalFormulaCalculator.getPCNFByTruthTable(formula);
					if (detailedBox.isSelected()) {
						FormulaTruthTable truthTable = new FormulaTruthTable(formula);
						String truthTableString = truthTable.createDetailedTruthTable();
						textAreaManager.appendContent("\t" + truthTableString + "\n");
						imagedAreaManager.appendPlainStringAsNewLine("    ");
						imagedAreaManager.appendLaTeXStringToLastLine(truthTableString);
					}
					textAreaManager.appendContent("\tObtain  PCNF by using truth table: " + pcnf.toNamedLaTeXString() + "\n");
					textAreaManager.appendContent("\tAnd its PDNF: " + pcnf.toNamedPDNFLaTeXString() + "\n");
					
					imagedAreaManager.appendPlainStringAsNewLine("    使用真值表得到主合取范式: ");
					imagedAreaManager.appendLaTeXStringToLastLine(pcnf.toNamedLaTeXString());
					imagedAreaManager.appendPlainStringAsNewLine("    相应的主析取范式是: ");
					imagedAreaManager.appendLaTeXStringToLastLine(pcnf.toNamedPDNFLaTeXString());
				}
			}
			
			if (dnfBox.isSelected()) {
				if (!cnfBox.isSelected()) {
					textAreaManager.appendContent(counter + ": Calculate disjunctive normal formula for " + formula.toSimpleLaTeXString() + "\n");
					imagedAreaManager.appendPlainStringAsNewLine(counter + ": 计算析取范式 ");
					imagedAreaManager.appendLaTeXStringToLastLine(formula.toSimpleLaTeXString());
				} else {
					textAreaManager.appendContent("    And also calculate disjunctive normal formula for " + formula.toSimpleLaTeXString() + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    同时计算析取范式 ");
					imagedAreaManager.appendLaTeXStringToLastLine(formula.toSimpleLaTeXString());
				}
				
				if (!tableBox.isSelected() || equivBox.isSelected()) {
					Formula calResult = NormalFormulaCalculator.calculateDNF(formula);
					DisjunctiveNormalFormula disjnormform = DisjunctiveNormalFormula.convertAndSimplify(calResult);
					Formula dnfFormula = disjnormform.toFormula();
					if (detailedBox.isSelected()) {
						textAreaManager.appendContent("    The calculation information as follows\n");
						imagedAreaManager.appendPlainStringAsNewLine("    计算析取范式的过程如下： ");
						EquivCalculusRecorder calculusRecorder = NormalFormulaCalculator.getCalculusRecorder();
						List<EquivCalculusStep> calculusStepList = calculusRecorder.getStepList();
						boolean isFirst = true;
						for (EquivCalculusStep step : calculusStepList) {
							Formula stepFormula = step.getFormula();
							String comments = step.getComments();
							if (isFirst) {
								imagedAreaManager.appendPlainStringAsNewLine("            ");
								textAreaManager.appendContentAsNewLine("\t\t\t");
								isFirst = false;
							} else {
								imagedAreaManager.appendPlainStringAsNewLine("        ");
								imagedAreaManager.appendLaTeXStringToLastLine("\\quad\\quad\\equiv\\quad\\quad");
								textAreaManager.appendContentAsNewLine("\t\t\\equiv");
							}
							imagedAreaManager.appendLaTeXStringToLastLine(stepFormula.toSimpleLaTeXString());
							imagedAreaManager.appendPlainStringToLastLine(" // " + comments);
							textAreaManager.appendContentToLastLine("~" + stepFormula.toSimpleLaTeXString() + " // " + comments);
						}
					}
					textAreaManager.appendContent("    Finally simplified to dnf" + dnfFormula.toSimpleLaTeXString() + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    最后化简得到析取范式： ");
					imagedAreaManager.appendLaTeXStringToLastLine(dnfFormula.toSimpleLaTeXString());
					
					if (pnfBox.isSelected()) {
						PDNFormula lastPDNF = NormalFormulaCalculator.expandToPDNF(disjnormform, varSet);
						
						textAreaManager.appendContent("    Continue to expand disjunctive normal formula " + dnfFormula.toSimpleLaTeXString() + "\n");
						imagedAreaManager.appendPlainStringAsNewLine("    继续扩展析取范式 ");
						imagedAreaManager.appendLaTeXStringToLastLine(dnfFormula.toSimpleLaTeXString());
						imagedAreaManager.appendPlainStringToLastLine("，变量集 ");
						imagedAreaManager.appendLaTeXStringToLastLine("\\{" + variableSetString + "\\}");

						if (detailedBox.isSelected()) {
							ExpandNFRecorder expandRecorder = NormalFormulaCalculator.getExpandRecorder();
							int type = expandRecorder.getType();
							List<ExpandNFStep> stepList = expandRecorder.getStepList();
							for (ExpandNFStep step : stepList) {
								Formula stepFormula = step.getFormula();

								textAreaManager.appendContent("\tExpand " + stepFormula.toSimpleLaTeXString() + "[" + step.getFormulaBinaryCodeString() + "] to max terms: " + step.getResultCodesNamedLaTeXString(type) + "\n");
								imagedAreaManager.appendPlainStringAsNewLine("    扩展简单合取式 ");
								imagedAreaManager.appendLaTeXStringToLastLine(stepFormula.toSimpleLaTeXString());
								imagedAreaManager.appendPlainStringToLastLine("[" + step.getFormulaBinaryCodeString() + "] 得到极小项 : ");
								imagedAreaManager.appendLaTeXStringToLastLine(step.getResultCodesNamedLaTeXString(type));
							}
						}
						textAreaManager.appendContent("\tObtain  PDNF: " + lastPDNF.toNamedLaTeXString() + "\n");
						textAreaManager.appendContent("\tAnd its PCNF: " + lastPDNF.toNamedPCNFLaTeXString() + "\n");
						
						imagedAreaManager.appendPlainStringAsNewLine("    最终的主析取范式是: ");
						imagedAreaManager.appendLaTeXStringToLastLine(lastPDNF.toNamedLaTeXString());
						imagedAreaManager.appendPlainStringAsNewLine("    相应的主合取范式是: ");
						imagedAreaManager.appendLaTeXStringToLastLine(lastPDNF.toNamedPCNFLaTeXString());
					}
				}
				if (tableBox.isSelected()) {
					textAreaManager.appendContent("    Use truth table to get PDNF for " + formula.toSimpleLaTeXString() + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    使用构造真值表得到主范式 ");
					imagedAreaManager.appendLaTeXStringToLastLine(formula.toSimpleLaTeXString());
					
					PDNFormula pdnf = NormalFormulaCalculator.getPDNFByTruthTable(formula);
					if (detailedBox.isSelected()) {
						FormulaTruthTable truthTable = new FormulaTruthTable(formula);
						String truthTableString = truthTable.createDetailedTruthTable();
						textAreaManager.appendContent("\t" + truthTableString + "\n");
						imagedAreaManager.appendPlainStringAsNewLine("    ");
						imagedAreaManager.appendLaTeXStringToLastLine(truthTableString);
					}
					textAreaManager.appendContent("\tObtain  PDNF by using truth table: " + pdnf.toNamedLaTeXString() + "\n");
					textAreaManager.appendContent("\tAnd its PCNF: " + pdnf.toNamedPCNFLaTeXString() + "\n");
					
					imagedAreaManager.appendPlainStringAsNewLine("    使用真值表得到主析取范式: ");
					imagedAreaManager.appendLaTeXStringToLastLine(pdnf.toNamedLaTeXString());
					imagedAreaManager.appendPlainStringAsNewLine("    相应的主合取范式是: ");
					imagedAreaManager.appendLaTeXStringToLastLine(pdnf.toNamedPCNFLaTeXString());
				}
			}
			
			return true;
		}
		
		private boolean checkNormalFormula(String variableSetString, List<String> laTeXStringList, boolean isDNF) {
			char[] varSet = SetrelfunUtil.extractSetElements(variableSetString, false);
			if (varSet == null) {
				JOptionPane.showMessageDialog(inputManager.getDialog(), "输入的命题变量集 " + variableSetString + " 不合法！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			
			String wholeString = NormalFormulaCalculationUIManager.getWholeString(laTeXStringList, isDNF);
			Formula formula = FormulaBuilder.buildFromLaTexFormulaString(wholeString);
			if (formula == null) {
				for (String laTeXString : laTeXStringList) {
					formula = FormulaBuilder.buildFromLaTexFormulaString(laTeXString);
					if (formula == null) {
						JOptionPane.showMessageDialog(inputManager.getDialog(), "输入的字符串中包含不是合法公式的串 " + laTeXString + " !\n错误信息：" + FormulaBuilder.getErrorMessage(), "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
						return false;
					}
				}
				JOptionPane.showMessageDialog(inputManager.getDialog(), "输入的字符串 " + wholeString + " 整体上不是合法的公式!", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			return true;
		}
	}

	private static String getWholeString(List<String> list, boolean isDNF) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("(" + list.get(0) + ")");
		for (int i = 1; i < list.size(); i++) {
			if (isDNF) buffer.append(" " + Symbol.LATEX_OR + " (" + list.get(i) + ")");
			else buffer.append(" " + Symbol.LATEX_AND + " (" + list.get(i) + ")");
		}
		return buffer.toString();
	}
}
