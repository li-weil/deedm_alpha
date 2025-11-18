package com.deedm.legacy.guiManager.logic;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.deedm.legacy.guiManager.ImagedTextAreaManager;
import com.deedm.legacy.guiManager.LaTeXFormulaInputManager;
import com.deedm.legacy.guiManager.MainSwingFrame;
import com.deedm.legacy.guiManager.PlainTextAreaManager;
import com.deedm.legacy.proplogic.FormulaBuilder;
import com.deedm.legacy.proplogic.TruthAssignmentFunction;
import com.deedm.legacy.proplogic.equiv.EquivCalculusChecker;
import com.deedm.legacy.proplogic.equiv.EquivCalculusRecorder;
import com.deedm.legacy.proplogic.equiv.EquivCalculusStep;
import com.deedm.legacy.proplogic.formula.Formula;

public class EquivCalculusCheckUIManager {
	private static final int width = Math.max(2* MainSwingFrame.screenWidth/5, 1024)*(MainSwingFrame.screenResolution/96);
	private static final int height = Math.max(MainSwingFrame.screenHeight/3, 720)*(MainSwingFrame.screenResolution/96);
	private ImagedTextAreaManager imagedAreaManager = null;
	private PlainTextAreaManager textAreaManager = null;
	private LaTeXFormulaInputManager inputManager = null;
	private int counter = 1;

	public EquivCalculusCheckUIManager(PlainTextAreaManager plainManager, ImagedTextAreaManager imageManager) {
		imagedAreaManager = imageManager;
		textAreaManager = plainManager;
		
		JPanel buttonPanel = createButtonPanel();
		inputManager = new LaTeXFormulaInputManager(width, height, buttonPanel, "演算过程每一步格式为[\\equiv & 公式 & 注释]");
	}

	public void setEquivCalculus(String content) {
		inputManager.setContent(content);
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
		JButton actButton = new JButton("检查演算(Y)");
		actButton.setMnemonic('Y');					// 设置快捷键
		// 设置动作命令的名称，这可用来区分用户到底按了什么按钮
		actButton.setActionCommand("ok");			
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
		
		JButton exampleButton = new JButton("问题2.12(1)");
		exampleButton.setActionCommand("problem2_12_1");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("问题2.12(2)");
		exampleButton.setActionCommand("problem2_12_2");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("问题2.12(3)");
		exampleButton.setActionCommand("problem2_12_3");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("问题2.12(4)");
		exampleButton.setActionCommand("problem2_12_4");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("问题2.14(1)");
		exampleButton.setActionCommand("problem2_14_1");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("问题2.14(2)");
		exampleButton.setActionCommand("problem2_14_2");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
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
				String content = inputManager.getContent();
				checkEquivCalculus(content);
				counter = counter + 1;
				inputManager.updateFeedback();
				imagedAreaManager.revalidate();
			} else if (command.equals("cancel")) {
				inputManager.setDialogStatus(false);
				inputManager.setDialogVisible(false);
			} else if (command.equals("remove")) {
				inputManager.clearFormula();
			} else if (command.equals("check")) {
				inputManager.clearFeedback();
				String content = inputManager.getContent();
				checkInputEquivCalculus(content);
				inputManager.updateFeedback();
			} else if (command.equals("problem2_14_1")) {
				String laTeXString = "& \\neg (p\\rightarrow q)\\wedge((\\neg q)\\leftrightarrow r) &\n"+
						"\\equiv & \\neg(p\\rightarrow q)\\wedge((\\neg q\\rightarrow r)\\wedge (r\\rightarrow\\neg q))  & 双蕴涵等值式\n"+
						"\\equiv & \\neg(\\neg p\\vee q)\\wedge((\\neg (\\neg q)\\vee r)\\wedge (\\neg r\\vee \\neg q))  & 蕴涵等值式\n"+
						"\\equiv & (\\neg (\\neg p)\\wedge \\neg q)\\wedge(\\neg (\\neg q)\\vee r)\\wedge (\\neg r\\vee \\neg q)  & 德摩尔根律\n"+
						"\\equiv & (p\\wedge \\neg q)\\wedge(q\\vee r)\\wedge (\\neg r\\vee \\neg q)  & 双重否定律";
				inputManager.setContent(laTeXString);
				checkInputEquivCalculus(laTeXString);
			} else if (command.equals("problem2_14_2")) {
				String laTeXString = "p\\wedge\\neg q\\wedge(q\\vee r)\\wedge(\\neg r\\vee\\neg q) &\n"+
						"\\equiv &  ((p\\wedge\\neg q\\wedge q)\\vee(p\\wedge\\neg q\\wedge r))\\wedge(\\neg r\\vee\\neg q)  & 分配律\n"+
						"\\equiv &  (p\\wedge\\neg q\\wedge r)\\wedge(\\neg r\\vee\\neg q)  & 矛盾律、零律、同一律\n"+
						"\\equiv &  (p\\wedge\\neg q\\wedge r\\wedge\\neg r)\\vee(p\\wedge\\neg q\\wedge r\\wedge\\neg q)  & 分配律\n"+
						"\\equiv &  (p\\wedge\\neg q\\wedge r\\wedge\\neg q)  & 矛盾律、零律、同一律\n"+
						"\\equiv &  p\\wedge\\neg q\\wedge r  & 交换律、幂等律";
				inputManager.setContent(laTeXString);
				checkInputEquivCalculus(laTeXString);
			} else if (command.equals("problem2_12_1")) {
				String laTeXString = " & (p\\rightarrow q)\\wedge(p\\rightarrow r)  \n"+
						" \\equiv & (\\neg p\\vee q)\\wedge(\\neg p\\vee r)  & 蕴涵等值式\n"+
						" \\equiv & \\neg p\\vee(q\\wedge r)  & 分配律 \n"+
						" \\equiv & p\\rightarrow(q\\wedge r)  & 蕴涵等值式";
				inputManager.setContent(laTeXString);
				checkInputEquivCalculus(laTeXString);
			} else if (command.equals("problem2_12_2")) {
				String laTeXString = " & (p\\rightarrow q)\\vee (p\\rightarrow r)  \n" + 
						"\\equiv & (\\neg p\\vee q)\\vee(\\neg p\\vee r)  & 蕴涵等值式 \n"+
						" \\equiv &  (\\neg p\\vee \\neg p)\\vee (q\\vee r)   & 交换律、结合律\n"+
						" \\equiv &  \\neg p\\vee (q\\vee r)   & 幂等律\n"+
						" \\equiv &  p\\rightarrow (q\\vee r)  & 蕴涵等值式";
				inputManager.setContent(laTeXString);
				checkInputEquivCalculus(laTeXString);
			} else if (command.equals("problem2_12_3")) {
				String laTeXString = " & (p\\rightarrow r)\\wedge(q\\rightarrow r) \n"+
						" \\equiv &  (\\neg p\\vee r)\\wedge(\\neg q\\vee r)  & 蕴涵等值式\n"+
						" \\equiv &  (\\neg p\\wedge\\neg q)\\vee r  & 分配律\n"+
						" \\equiv &  \\neg (p\\vee q)\\vee r  & 德摩尔根律\n"+
						" \\equiv &  (p\\vee q)\\rightarrow r  & 蕴涵等值式";
				inputManager.setContent(laTeXString);
				checkInputEquivCalculus(laTeXString);
			} else if (command.equals("problem2_12_4")) {
				String laTeXString = " & (p\\rightarrow r)\\vee (q\\rightarrow r) & \n"+
						" \\equiv &  (\\neg p\\vee r)\\vee (\\neg q\\vee r)  & 蕴涵等值式\n"+
						" \\equiv &  (\\neg p\\vee \\neg q)\\vee(r\\vee r)  & 交换律、结合律\n"+
						" \\equiv &  (\\neg p\\vee \\neg q)\\vee r  & 幂等律\n"+
						" \\equiv &  \\neg(p\\wedge q)\\vee r & 德摩尔根律\n"+
						" \\equiv &  (p\\wedge q)\\rightarrow r  & 蕴涵等值式";
				inputManager.setContent(laTeXString);
				checkInputEquivCalculus(laTeXString);
			} else if (command.equals("clear")) {
				textAreaManager.clearContent();
				imagedAreaManager.clearContent();
				imagedAreaManager.revalidate();
			}
		}
		
		private boolean checkEquivCalculus(String content) {
			EquivCalculusRecorder calculusRecorder = getEquivCalculusRecorder(content);
			if (calculusRecorder == null) {
				imagedAreaManager.appendPlainStringAsNewLine(counter + ": 录入的命题逻辑等值演算过程有错误！");
				textAreaManager.appendContentAsNewLine(counter + ": the input propositional equivalence calculus is error!");
				inputManager.appendPlainStringAsNewFeedbackLine("录入的命题逻辑等值演算过程有误！");
				return false;
			}

			imagedAreaManager.appendPlainStringAsNewLine(counter + ": 检查下面的命题逻辑等值演算过程：");
			textAreaManager.appendContentAsNewLine(counter + ": checking the following propositional equivalence calculus: ");
			List<EquivCalculusStep> calculusStepList = calculusRecorder.getStepList();
			boolean isFirst = true;
			for (EquivCalculusStep step : calculusStepList) {
				Formula stepFormula = step.getFormula();
				String comments = step.getComments();
				if (isFirst) {
					imagedAreaManager.appendPlainStringAsNewLine("            ");
					textAreaManager.appendContentAsNewLine("\t\t");
					inputManager.appendPlainStringAsNewFeedbackLine("    ");
					isFirst = false;
				} else {
					imagedAreaManager.appendPlainStringAsNewLine("        ");
					imagedAreaManager.appendLaTeXStringToLastLine("\\quad\\quad\\equiv\\quad\\quad");
					textAreaManager.appendContentAsNewLine("\t\t\\equiv");
					inputManager.appendLaTeXStringAsNewFeedbackLine("\\quad\\quad\\equiv\\quad\\quad");
				}
				imagedAreaManager.appendLaTeXStringToLastLine(stepFormula.toSimpleLaTeXString());
				imagedAreaManager.appendPlainStringToLastLine(" // " + comments);
				textAreaManager.appendContentToLastLine("~" + stepFormula.toSimpleLaTeXString() + " // " + comments);

				inputManager.appendLaTeXStringToLastFeedbackLine(stepFormula.toSimpleLaTeXString());
				inputManager.appendPlainStringToLastFeedbackLine(" // " + comments);
			}
			
			EquivCalculusChecker checker = new EquivCalculusChecker(calculusRecorder); 
			boolean success = checker.checkByUsingTruthTable();
			if (success) {
				imagedAreaManager.appendPlainStringAsNewLine("    " + "通过构造真值表检验，上述命题逻辑等值演算过程没有错误！");
				textAreaManager.appendContentAsNewLine("\tThe above calculus is correct checking by truth table!");
			} else {
				String checkingFormula = checker.getCheckingFormula().toSimpleLaTeXString();
				TruthAssignmentFunction example = checker.getCounterExample();
				imagedAreaManager.appendPlainStringAsNewLine("    在真值赋值 " + example + " 下，下面的公式不是永真式！");
				imagedAreaManager.appendPlainStringAsNewLine("        ");
				imagedAreaManager.appendLaTeXStringToLastLine(checkingFormula);
				textAreaManager.appendContentAsNewLine("\tGiven truth assignment " + example + ", the following formula is not tautology!");
				textAreaManager.appendContentAsNewLine("\t\t" + checkingFormula);
			}
			return true;
		}
		
		private boolean checkInputEquivCalculus(String content) {
			inputManager.clearFeedback();;
			String[] splittedSteps = content.trim().split("[\n\r;]");
			for (int i = 0; i < splittedSteps.length; i++) {
				String stepContent = splittedSteps[i].trim();
				String[] stepCells = stepContent.split("&");
				if (stepCells.length <= 0) continue;
				
				String cell = stepCells[0].trim();
				if (!cell.contentEquals("") && !cell.contentEquals("\\equiv")) {
					Formula formula = FormulaBuilder.buildFromLaTexFormulaString(cell);
					if (formula == null) {
						JOptionPane.showMessageDialog(inputManager.getDialog(), "输入第 " + i + " 行 [" + stepContent + "] 包含不是合法公式的串 " + cell + " !\n错误信息：" + FormulaBuilder.getErrorMessage(), "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
						return false;
					}
					inputManager.appendLaTeXStringAsNewFeedbackLine(cell);
					if (stepCells.length >=2) {
						inputManager.appendPlainStringToLastFeedbackLine("  " + stepCells[1]);
					}
				} else {
					inputManager.appendLaTeXStringAsNewFeedbackLine("\\quad " + cell + "\\quad");
					if (stepCells.length <= 1) {
						JOptionPane.showMessageDialog(inputManager.getDialog(), "输入第 " + i + " 行 [" + stepContent + "] 中没有给出公式!");
						return false;
					}
					cell = stepCells[1].trim();
					Formula formula = FormulaBuilder.buildFromLaTexFormulaString(cell);
					if (formula == null) {
						JOptionPane.showMessageDialog(inputManager.getDialog(), "输入第 " + i + " 行 [" + stepContent + "] 包含不是合法公式的串 " + cell + " !\n错误信息：" + FormulaBuilder.getErrorMessage(), "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
						return false;
					}
					inputManager.appendLaTeXStringToLastFeedbackLine(cell);
					if (stepCells.length >=3) {
						inputManager.appendPlainStringToLastFeedbackLine("  " + stepCells[2]);
					}
				}
			}
			inputManager.updateFeedback();
			return true;
		}

		private EquivCalculusRecorder getEquivCalculusRecorder(String content) {
			EquivCalculusRecorder resultRecorder = new EquivCalculusRecorder();
			
			String[] splittedSteps = content.trim().split("[\n\r;]");
			for (int i = 0; i < splittedSteps.length; i++) {
				String stepContent = splittedSteps[i].trim();
				String[] stepCells = stepContent.split("&");
				if (stepCells.length <= 0) continue;
				
				String cell = stepCells[0].trim();
				if (!cell.contentEquals("") && !cell.contentEquals("\\equiv")) {
					Formula formula = FormulaBuilder.buildFromLaTexFormulaString(cell);
					if (formula == null) {
						JOptionPane.showMessageDialog(inputManager.getDialog(), "输入第 " + i + " 行 [" + stepContent + "] 包含不是合法公式的串 " + cell + " !\n错误信息：" + FormulaBuilder.getErrorMessage(), "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
						return null;
					}
				} else {
					if (stepCells.length <= 1) {
						JOptionPane.showMessageDialog(inputManager.getDialog(), "输入第 " + i + " 行 [" + stepContent + "] 中没有给出公式!");
						return null;
					}
					cell = stepCells[1].trim();
					Formula formula = FormulaBuilder.buildFromLaTexFormulaString(cell);
					if (formula == null) {
						JOptionPane.showMessageDialog(inputManager.getDialog(), "输入第 " + i + " 行 [" + stepContent + "] 包含不是合法公式的串 " + cell + " !\n错误信息：" + FormulaBuilder.getErrorMessage(), "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
						return null;
					}
					String comments = "";
					if (stepCells.length >= 3) comments = stepCells[2];
					resultRecorder.addStep(new EquivCalculusStep(formula, comments));
				}
			}
			return resultRecorder;
		}
	}
}
