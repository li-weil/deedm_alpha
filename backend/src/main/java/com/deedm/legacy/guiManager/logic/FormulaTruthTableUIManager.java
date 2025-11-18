/**
 * 
 */
package com.deedm.legacy.guiManager.logic;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.deedm.legacy.guiManager.ImagedTextAreaManager;
import com.deedm.legacy.guiManager.LaTeXFormulaInputManager;
import com.deedm.legacy.guiManager.MainSwingFrame;
import com.deedm.legacy.guiManager.PlainTextAreaManager;
import com.deedm.legacy.proplogic.FormulaBuilder;
import com.deedm.legacy.proplogic.FormulaTruthTable;
import com.deedm.legacy.proplogic.formula.Formula;

/**
 * @author Zhou Xiaocong
 * @since 2020/09/30
 * 
 * 与用户进行交互，输入一个或多个公式，构造这些公式的真值表
 */
public class FormulaTruthTableUIManager {
	private static final int width = Math.max(2* MainSwingFrame.screenWidth/5, 1024)*(MainSwingFrame.screenResolution/96);
	private static final int height = Math.max(MainSwingFrame.screenHeight/3, 480)*(MainSwingFrame.screenResolution/96);
	private ImagedTextAreaManager imagedAreaManager = null;
	private PlainTextAreaManager textAreaManager = null;
	private LaTeXFormulaInputManager inputManager = null;
	
	private JCheckBox detailedBox = null;
	private JCheckBox typeBox = null;
	
	private int counter = 0;
	
	public FormulaTruthTableUIManager(PlainTextAreaManager plainManager, ImagedTextAreaManager imageManager) {
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
		JButton actButton = new JButton("开始构造(Y)");
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
		detailedBox = new JCheckBox("构造详细形式的真值表");
		detailedBox.setSelected(true);
		infoPanel.add(detailedBox);
		typeBox = new JCheckBox("判断公式是否是永真式、矛盾式或可满足式");
		typeBox.setSelected(true);
		infoPanel.add(typeBox);
		
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

							FormulaTruthTable table = new FormulaTruthTable(formula);
							String truthTableString = null;
							if (detailedBox.isSelected()) truthTableString = table.createDetailedTruthTable();
							else truthTableString = table.createSimpleTruthTable(); 
							textAreaManager.appendContent("\t" + truthTableString + "\n");
							imagedAreaManager.appendPlainStringAsNewLine("    ");
							imagedAreaManager.appendLaTeXStringToLastLine(truthTableString);
							
							if (typeBox.isSelected()) {
								String typeChineseString = null;
								if (table.isAllTruthAssignment()) typeChineseString = "永真式";
								else if (table.hasTruthAssignment()) typeChineseString = "非永真的可满足式";
								else typeChineseString = "矛盾式";

								imagedAreaManager.appendPlainStringAsNewLine("    公式 ");
								imagedAreaManager.appendLaTeXStringToLastLine(laTeXString);
								imagedAreaManager.appendPlainStringToLastLine(" 是 [" + typeChineseString + "]！");
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
}


