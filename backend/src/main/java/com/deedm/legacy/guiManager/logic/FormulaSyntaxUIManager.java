package com.deedm.legacy.guiManager.logic;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.deedm.legacy.guiManager.ImagedTextAreaManager;
import com.deedm.legacy.guiManager.LaTeXFormulaInputManager;
import com.deedm.legacy.guiManager.MainGUIManager;
import com.deedm.legacy.guiManager.MainSwingFrame;
import com.deedm.legacy.guiManager.PlainTextAreaManager;
import com.deedm.legacy.proplogic.FormulaBuilder;
import com.deedm.legacy.proplogic.formula.Formula;
import com.deedm.legacy.proplogic.formula.ASTGraph.FormulaASTGraph;
import com.deedm.legacy.util.Configuration;

public class FormulaSyntaxUIManager {
	private static final int width = Math.max(2* MainSwingFrame.screenWidth/5, 1024)*(MainSwingFrame.screenResolution/96);
	private static final int height = Math.max(MainSwingFrame.screenHeight/3, 480)*(MainSwingFrame.screenResolution/96);
	private ImagedTextAreaManager imagedAreaManager = null;
	private PlainTextAreaManager textAreaManager = null;
	private LaTeXFormulaInputManager inputManager = null;
	
	private JCheckBox treeBox = null;
	private JCheckBox strictBox = null;
	private JCheckBox subformBox = null;
	
	private int counter = 0;
	
	public FormulaSyntaxUIManager(PlainTextAreaManager plainManager, ImagedTextAreaManager imageManager) {
		imagedAreaManager = imageManager;
		textAreaManager = plainManager;
		
		JPanel buttonPanel = createButtonPanel();
		inputManager = new LaTeXFormulaInputManager(width, height, buttonPanel);
	}
	
	public void setFormula(String laTeXString) {
		inputManager.setFormula(laTeXString);
	}
	
	public boolean showDialog(Component parent, String title) {
		return inputManager.showDialog(parent, title);
	}
	

	// 创建按钮及其所在的窗格
	private JPanel createButtonPanel() {
		// 创建一事件监听器，该监听器监听下述两个按钮上发生的事件
		SimpleListener listener = new SimpleListener();

		JPanel buttonPanelOne = new JPanel();
		buttonPanelOne.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 0));
		// 创建用于确定输入信息的“确定”按钮
		JButton actButton = new JButton("开始分析(Y)");
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
		
		// 创建用于确定输入信息的“检查合法性”按钮，检查输入是否合法
		actButton = new JButton("检查合法性(K)");
		actButton.setMnemonic('K');					// 设置快捷键
		// 设置动作命令的名称，这可用来区分用户到底按了什么按钮
		actButton.setActionCommand("check");
		actButton.addActionListener(listener);		// 添加事件监听器
		buttonPanelOne.add(actButton);

		// 创建用于放弃输入信息的“清理屏幕”按钮，清理用于输入结果的屏幕
		actButton = new JButton("清理屏幕(C)");
		actButton.setMnemonic('C');				// 设置快捷键
		// 设置动作命令的名称，这可用来区分用户到底按了什么按钮
		actButton.setActionCommand("clear");			
		actButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelOne.add(actButton);

		// 创建用于放弃输入信息的“返回”按钮，返回主菜单
		actButton = new JButton("返回(R)");
		actButton.setMnemonic('R');				// 设置快捷键
		// 设置动作命令的名称，这可用来区分用户到底按了什么按钮
		actButton.setActionCommand("cancel");			
		actButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelOne.add(actButton);

		JPanel buttonPanelTwo = new JPanel();
		buttonPanelTwo.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 0));
 		buttonPanelTwo.setBorder(BorderFactory.createTitledBorder("《离散数学基础》教材示例"));
		
		JButton exampleButton = new JButton("例子2.4");
		exampleButton.setActionCommand("example2_4");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("问题2.6(1)");
		exampleButton.setActionCommand("problem2_6_1");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("问题2.6(2)");
		exampleButton.setActionCommand("problem2_6_2");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("问题2.6(3)");
		exampleButton.setActionCommand("problem2_6_3");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("问题2.6(4)");
		exampleButton.setActionCommand("problem2_6_4");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("问题2.17");
		exampleButton.setActionCommand("problem2_17");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		// 创建给出变量集信息和范式信息的窗格与相关组件
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 25, 0));
		infoPanel.setBorder(BorderFactory.createTitledBorder("选择进行的展示："));
		treeBox = new JCheckBox("给出公式的抽象语法树");
		treeBox.setSelected(true);
		infoPanel.add(treeBox);
		subformBox = new JCheckBox("给出公式的所有子公式");
		subformBox.setSelected(true);
		infoPanel.add(subformBox);
		strictBox = new JCheckBox("给出符合公式归纳定义的严格形式公式");
		strictBox.setSelected(true);
		infoPanel.add(strictBox);
		
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
				inputManager.clearFeedback();
				List<String> laTeXStringList = inputManager.getFormulaList();
				for (String laTeXString : laTeXStringList) {
					try {
						counter = counter+1;
						Formula formula = FormulaBuilder.buildFromLaTexFormulaString(laTeXString);
						if (formula != null) {
							inputManager.appendLaTeXStringAsNewFeedbackLine(laTeXString);
							textAreaManager.appendContent(counter + ": " + laTeXString + "\n");
							
							imagedAreaManager.appendPlainStringAsNewLine(counter + ": 公式 ");
							imagedAreaManager.appendLaTeXStringToLastLine(laTeXString);
							imagedAreaManager.appendPlainStringToLastLine(" 是[" + getFormulaTypeChineseName(formula) + "]");

							if (treeBox.isSelected()) {
								String dotFileName = Configuration.dataFilePath + "ProplogicAST.dot";
								String pngFileName = Configuration.dataFilePath + "ProplogicAST.png";
								FormulaASTGraph ast = FormulaASTGraph.createASTGraph(formula, "PropAST");
								try {
									PrintWriter writer = new PrintWriter(dotFileName);
									ast.simplyWriteToDotFile(writer);
									boolean success = MainGUIManager.generatePNGFile(dotFileName, pngFileName, true);
									if (success) {
										textAreaManager.appendContent("\t\tHere gives the AST of the current formula!\n");
										imagedAreaManager.appendPlainStringAsNewLine("    抽象语法树：");
										imagedAreaManager.appendImageFileToLastLine(pngFileName);
									} else {
										textAreaManager.appendContent("\t\tCan not generate the graph of the current formula!\n");
										textAreaManager.appendContent(MainGUIManager.errorMessage);
										imagedAreaManager.appendPlainStringAsNewLine("        抽象语法树: 无法生成！");
									}
								} catch (Exception e) {
									textAreaManager.appendContent("\t\tCan not generate the graph of the current formula!\n");
									textAreaManager.appendContent(MainGUIManager.errorMessage);
									imagedAreaManager.appendPlainStringAsNewLine("        抽象语法树: 无法生成！");
								}
							}
							if (subformBox.isSelected()) {
								String subformString = getSubformulasJMathLaTexString(formula);
								textAreaManager.appendContent("\tAll subformulas are as follows: \n");
								textAreaManager.appendContent("\t\t" + subformString + "\n");
								
								imagedAreaManager.appendPlainStringAsNewLine("    该公式包含的所有子公式如下：");
								imagedAreaManager.appendPlainStringAsNewLine("        ");
								imagedAreaManager.appendLaTeXStringToLastLine(subformString);
							}
							if (strictBox.isSelected()) {
								String strictString = formula.toLaTeXString();
								String simpleString = formula.toSimpleLaTeXString();
								textAreaManager.appendContent("\tThe strict form of the formula is: " + strictString + ", can be simplified to: " + simpleString + "\n");
								
								imagedAreaManager.appendPlainStringAsNewLine("    严格形式的公式是：");
								imagedAreaManager.appendLaTeXStringToLastLine(strictString);
								imagedAreaManager.appendPlainStringToLastLine("，建议可简写为：");
								imagedAreaManager.appendLaTeXStringToLastLine(simpleString);
							}
						} else {
							// 弹出一窗口显示一些信息
							JOptionPane.showMessageDialog(inputManager.getDialog(), "输入的字符串 " + laTeXString + " 不是合法的公式！\n错误信息：" + FormulaBuilder.getErrorMessage(), "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
							textAreaManager.appendContent(counter + " : " + laTeXString + " is not a legal LaTeX formula!\n");
							imagedAreaManager.appendPlainStringAsNewLine(counter + " : 公式 " + laTeXString + "不是合法的公式！");
						}
					} catch (Exception exc) {
						exc.printStackTrace();
						JOptionPane.showMessageDialog(null, exc.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);		
					}
				}
				inputManager.updateFeedback();
				imagedAreaManager.revalidate();
			} else if (command.equals("cancel")) {
				inputManager.setDialogStatus(false);
				inputManager.setDialogVisible(false);
			} else if (command.equals("remove")) {
				inputManager.clearFormula();
			} else if (command.equals("generate")) {
				String plainFormula = FormulaBuilder.randomGenerateFormulaControlledByOperatorNumber(4, 5);
				Formula formula = FormulaBuilder.buildFromSymbolFormulaString(plainFormula);
				if (formula != null) {
					String laTeXString = formula.toSimpleLaTeXString();
					String content = inputManager.getContent();
					if (content.length() == 0) content = laTeXString;
					else content = content + ";" + laTeXString; 
					inputManager.setFormulaList(content);
				} else {
					// 弹出一窗口显示一些信息
					JOptionPane.showMessageDialog(inputManager.getDialog(), "自动生成公式失败！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				}
			} else if (command.equals("check")) {
				inputManager.clearFeedback();
				List<String> laTeXStringList = inputManager.getFormulaList();
				for (String laTeXString : laTeXStringList) {
					Formula formula = FormulaBuilder.buildFromLaTexFormulaString(laTeXString);
					if (formula == null) {
						// 弹出一窗口显示一些信息
						JOptionPane.showMessageDialog(inputManager.getDialog(), "输入的字符串中 " + laTeXString + " 不是合法的公式！\n" + "错误信息：" + FormulaBuilder.getErrorMessage(), "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
						inputManager.appendPlainStringAsNewFeedbackLine(laTeXString + " 不是合法的公式！");
						inputManager.appendPlainStringAsNewFeedbackLine("    错误信息：" + FormulaBuilder.getErrorMessage());
						break;
					} else {
						inputManager.appendLaTeXStringAsNewFeedbackLine(laTeXString);
					}
				}
				inputManager.updateFeedback();
			} else if (command.equals("example2_4")) {
				String laTeXString = "(\\neg(\\neg p)\\rightarrow(t\\vee s))\\wedge(s\\leftrightarrow r)";
				inputManager.setFormula(laTeXString);
			} else if (command.equals("problem2_6_1")) {
				String laTeXString = "p\\wedge(\\neg s)\\wedge(\\neg s)";
				inputManager.setFormula(laTeXString);
			} else if (command.equals("problem2_6_2")) {
				String laTeXString = "\\neg(\\neg r)\\rightarrow(\\neg(q\\vee p))";
				inputManager.setFormula(laTeXString);
			} else if (command.equals("problem2_6_3")) {
				String laTeXString = "((p\\vee p)\\leftrightarrow(p\\wedge s))\\rightarrow(p\\wedge s)";
				inputManager.setFormula(laTeXString);
			} else if (command.equals("problem2_6_4")) {
				String laTeXString = "(r\\rightarrow(q\\leftrightarrow s))\\vee(q\\leftrightarrow s)";
				inputManager.setFormula(laTeXString);
			} else if (command.equals("problem2_17")) {
				String laTeXString = "\\neg(p\\rightarrow q)\\wedge((\\neg q)\\leftrightarrow r)";
				inputManager.setFormula(laTeXString);
			} else if (command.equals("clear")) {
				textAreaManager.clearContent();
				imagedAreaManager.clearContent();
				imagedAreaManager.revalidate();
			}
		}
	}
	
	public static String getFormulaTypeChineseName(Formula formula) {
		if (formula.isAtomicFormula()) return "原子公式";
		if (formula.isNegFormula()) return "否定式";
		if (formula.isOrFormula()) return "析取式";
		if (formula.isAndFormula()) return "合取式";
		if (formula.isBiImpFormula()) return "双蕴涵式";
		if (formula.isImpFormula()) return "蕴涵式";
		return "未知类型公式";
	}
	
	public static String getSubformulasJMathLaTexString(Formula formula) {
		List<Formula> subformList = formula.getAllSyntaxDifferentSubFormulas();
		int colNumber = 6;
		if (subformList.size() <= 8) colNumber = subformList.size();
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("\\begin{tabular}{");
		for (int colIndex = 0; colIndex < colNumber; colIndex++) {
			if (colIndex == 0) buffer.append("c");
			else buffer.append("|c");
		}
		buffer.append("}\n");
		
		int index = 0;
		while (index < subformList.size()) {
			buffer.append("\\hline ");
			for (int colIndex = 0; colIndex < colNumber; colIndex++) {
				String latexString = "\\quad";
				if (index < subformList.size()) {
					latexString = subformList.get(index).toLaTeXString();
					index = index + 1;
				}
				if (colIndex == 0) buffer.append(latexString);
				else buffer.append(" & " + latexString);
			}
			buffer.append(" \\\\\n");
		}
		buffer.append("\\hline\n");
		buffer.append("\\end{tabular}\n");
		buffer.trimToSize();
		return buffer.toString();
	}
}
