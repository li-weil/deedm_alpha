package com.deedm.legacy.guiManager.logic;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.deedm.legacy.guiManager.ImagedTextAreaManager;
import com.deedm.legacy.guiManager.LaTeXFormulaInputManager;
import com.deedm.legacy.guiManager.MainSwingFrame;
import com.deedm.legacy.guiManager.PlainTextAreaManager;
import com.deedm.legacy.proplogic.FormulaBuilder;
import com.deedm.legacy.proplogic.TruthAssignmentFunction;
import com.deedm.legacy.proplogic.formula.Formula;
import com.deedm.legacy.proplogic.reason.LogicReasoning;
import com.deedm.legacy.proplogic.reason.LogicReasoningRuleName;
import com.deedm.legacy.proplogic.reason.ReasonArgumentCheckStep;
import com.deedm.legacy.proplogic.reason.ReasonArgumentChecker;
import com.deedm.legacy.proplogic.reason.ReasonArgumentRecorder;
import com.deedm.legacy.proplogic.reason.ReasonArgumentStep;

public class ReasonArgumentCheckUIManager {
	private static final int width = Math.max(2* MainSwingFrame.screenWidth/5, 1024)*(MainSwingFrame.screenResolution/96);
	private static final int height = Math.max(MainSwingFrame.screenHeight/3, 720)*(MainSwingFrame.screenResolution/96);
	private ImagedTextAreaManager imagedAreaManager = null;
	private PlainTextAreaManager textAreaManager = null;
	private LaTeXFormulaInputManager inputManager = null;
	private JTextField premField = null;
	private JTextField consField = null;
	private int counter = 1;

	public ReasonArgumentCheckUIManager(PlainTextAreaManager plainManager, ImagedTextAreaManager imageManager) {
		imagedAreaManager = imageManager;
		textAreaManager = plainManager;
		
		JPanel buttonPanel = createButtonPanel();
		inputManager = new LaTeXFormulaInputManager(width, height, buttonPanel, "论证过程每一步格式为[(序号) & 公式  & (序号),(序号)推理规则]");
	}

	public void setArgument(String content) {
		inputManager.setContent(content);
	}
	
	public void setReasoning(String premises, String consequent) {
		premField.setText(premises);
		consField.setText(consequent);
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
		JButton actButton = new JButton("检查论证(Y)");
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
		
		// 创建用于确定输入信息的“规则名列表”按钮
		actButton = new JButton("规则名列表(N)");
		actButton.setMnemonic('N');					// 设置快捷键
		// 设置动作命令的名称，这可用来区分用户到底按了什么按钮
		actButton.setActionCommand("rulename");
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
		
		JButton exampleButton = new JButton("例子2.21");
		exampleButton.setActionCommand("problem2_21");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("问题2.22");
		exampleButton.setActionCommand("problem2_22");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("例子2.23");
		exampleButton.setActionCommand("problem2_23");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("问题2.24");
		exampleButton.setActionCommand("problem2_24");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("例子2.25");
		exampleButton.setActionCommand("problem2_25");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("问题2.31");
		exampleButton.setActionCommand("problem2_31");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("问题2.32");
		exampleButton.setActionCommand("problem2_32");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		// 创建给出变量集信息和范式信息的窗格与相关组件
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new GridLayout(2, 1, 0, 10));
		infoPanel.setBorder(BorderFactory.createTitledBorder("要验证推理的前提集和结论公式，前提公式间用英文逗号分隔"));

		// 创建一临时窗格用于放置提示输入的标签，以及用于输入姓名的组件
		JPanel tempPanel = new JPanel();
		tempPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 25, 0));
		// 创建一个标签用于提示输入姓名信息
		JLabel promptLabel = new JLabel("推理前提列表(P)：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+S可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('P');	
		// 创建输入姓名的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		premField = new JTextField(120);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(premField);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(premField);
		infoPanel.add(tempPanel);

		tempPanel = new JPanel();
		tempPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 25, 0));
		// 创建一个标签用于提示输入姓名信息
		promptLabel = new JLabel("推理结论公式(S)：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+S可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('S');	
		// 创建输入姓名的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		consField = new JTextField(60);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(consField);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(consField);
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
				checkReasonArgument();
				counter = counter + 1;
				inputManager.updateFeedback();
				imagedAreaManager.revalidate();
			} else if (command.equals("cancel")) {
				inputManager.setDialogStatus(false);
				inputManager.setDialogVisible(false);
			} else if (command.equals("remove")) {
				inputManager.clearFormula();
			} else if (command.equals("rulename")) {
				JOptionPane.showMessageDialog(inputManager.getDialog(), getDefinedRuleNameList(), "支持的命题逻辑推理规则名列表", JOptionPane.INFORMATION_MESSAGE);
			} else if (command.equals("check")) {
				inputManager.clearFeedback();
				boolean success = checkInputArgument();
				if (success) {
					JOptionPane.showMessageDialog(inputManager.getDialog(), "录入的推理前提集、结论公式和论证过程没有错误！", "离散数学基础例题习题展示", JOptionPane.INFORMATION_MESSAGE);
				}
				inputManager.updateFeedback();
			} else if (command.equals("problem2_21")) {
				String laTeXString = "(1) &  p & 前提 \n"+
						"(2) &  p\\rightarrow q & 前提 \n"+
						"(3) &  q & (1), (2)假言推理 \n"+
						"(4) &  q\\rightarrow r & 前提 \n"+
						"(5) &  r & (3), (4)假言推理";
				inputManager.setContent(laTeXString);
				String premises = "p\\rightarrow q, q\\rightarrow r, p";
				String consequent = "r";
				setReasoning(premises, consequent);
				checkInputArgument();
			} else if (command.equals("problem2_22")) {
				String laTeXString = "(1) &  t\\wedge r  & 前提\n"+
						"(2) &  t  & (1)化简规则\n"+
						"(3) &  s\\leftrightarrow t  & 前提\n"+
						"(4) &  (s\\rightarrow t)\\wedge(t\\rightarrow s)  & (3)等值置换\n"+
						"(5) &  t\\rightarrow s  & (4)化简规则\n"+
						"(6) &  s  & (2),(5)假言推理\n"+
						"(7) &  q\\leftrightarrow s  & 前提\n"+
						"(8) &  (s\\rightarrow q)\\wedge(q\\rightarrow s)  & (7)等值置换\n"+
						"(9) &  s\\rightarrow q  & (8)化简规则\n"+
						"(10) &  q  & (6),(9)假言推理\n"+
						"(11) &  q\\rightarrow p  & 前提\n"+
						"(12) &  p  & (10),(11)假言推理\n"+
						"(13) &  p\\wedge q  & (10),(12)合取引入";
				inputManager.setContent(laTeXString);
				String premises = "q\\rightarrow p, q\\leftrightarrow s, s\\leftrightarrow t, t\\wedge r";
				String consequent = "p\\wedge q";
				setReasoning(premises, consequent);
				checkInputArgument();
			} else if (command.equals("problem2_23")) {
				String laTeXString = "(1) &  p  & 附加前提\n"+
						"(2) &  p\\rightarrow q  & 前提\n"+
						"(3) &  q  & (1), (2)假言推理\n"+
						"(4) &  q\\rightarrow r  & 前提\n"+
						"(5) &  r  & (3), (4)假言推理\n"+
						"(6) &  p\\rightarrow r  & (1), (5)附加前提法";
				inputManager.setContent(laTeXString);
				String premises = "p\\rightarrow q, q\\rightarrow r";
				String consequent = "p\\rightarrow r";
				setReasoning(premises, consequent);
				checkInputArgument();
			} else if (command.equals("problem2_24")) {
				String laTeXString = "(1) &  r  & 附加前提\n"+
						"(2) &  \\neg r\\vee p  & 前提\n"+
						"(3) &  p  & (1),(2)析取三段论\n"+
						"(4) &  p\\rightarrow(q\\rightarrow s)  & 前提\n"+
						"(5) &  q\\rightarrow s  & (3),(4)假言推理\n"+
						"(6) &  q  & 前提\n"+
						"(7) &  s  & (5),(6)假言推理\n"+
						"(8) &  r\\rightarrow s  & (1),(7)附加前提法";
				inputManager.setContent(laTeXString);
				String premises = "p\\rightarrow(q\\rightarrow s), \\neg r\\vee p, q";
				String consequent = "r\\rightarrow s";
				setReasoning(premises, consequent);
				checkInputArgument();
			} else if (command.equals("problem2_25")) {
				String laTeXString = "(1) &  p\\wedge q  & 附加前提\n"+
						"(2) &  p  & (1) 化简规则\n"+
						"(3) &  \\neg p\\vee \\neg q  & 前提\n"+
						"(4) &  \\neg q  & (2),(3)析取三段论\n"+
						"(5) &  q  & (1)化简规则\n"+
						"(6) &  \\neg(p\\wedge q)  & (1), (4), (5)反证法";
				inputManager.setContent(laTeXString);
				String premises = "\\neg p\\vee \\neg q";
				String consequent = "\\neg (p\\wedge q)";
				setReasoning(premises, consequent);
				checkInputArgument();
			} else if (command.equals("problem2_31")) {
				String laTeXString = "(1) &   w  & 附加前提\n"+
						"(2) &   w\\rightarrow(r\\wedge s)  & 前提\n"+
						"(3) &   r\\wedge s  & (1),(2)假言推理\n"+
						"(4) &   r  & (3)化简规则\n"+
						"(5) &   \\neg p\\rightarrow\\neg r  & 前提\n"+
						"(6) &   p  & (4),(5)假言易位\n"+
						"(7) &   t  & 前提\n"+
						"(8) &   t\\rightarrow(\\neg p\\vee\\neg q)  & 前提\n"+
						"(9) &   \\neg p\\vee\\neg q  & (7),(8)假言推理\n"+
						"(10) &  \\neg q  & (6),(9)析取三段论\n"+
						"(11) &  s  & (3)化简规则\n"+
						"(12) &  \\neg q\\rightarrow\\neg s  & 前提\n"+
						"(13) &  q  & (11),(12)假言易位\n"+
						"(14) &  \\neg w  & (1),(10),(13)反证法";
				inputManager.setContent(laTeXString);
				String premises = "\\neg p\\rightarrow\\neg r, \\neg q\\rightarrow\\neg s, t\\rightarrow(\\neg p\\vee \\neg q), t, w\\rightarrow(r\\wedge s)";
				String consequent = "\\neg w";
				setReasoning(premises, consequent);
				checkInputArgument();
			} else if (command.equals("problem2_32")) {
				String laTeXString = "(1) &  p  & 附加前提\n"+
						"(2) &  p\\rightarrow q  & 前提\n"+
						"(3) &  q  & (1), (2) 假言推理\n"+
						"(4) &  s\\rightarrow \\neg q  & 前提\n"+
						"(5) &  \\neg s  & (3),(4) 假言易位\n"+
						"(6) &  r\\vee s  & 前提\n"+
						"(7) &  r  & (5),(6) 析取三段论\n"+
						"(8) &  \\neg q\\vee \\neg r  & 前提\n"+
						"(9) &  \\neg r  & (3), (8) 析取三段论\n"+
						"(10) &  \\neg p  & (1), (7), (9) 反证法";
				inputManager.setContent(laTeXString);
				String premises = "p\\rightarrow q, \\neg (q\\wedge r), \\neg(\\neg r\\wedge \\neg s), s\\rightarrow \\neg q";
				String consequent = "\\neg p";
				setReasoning(premises, consequent);
				checkInputArgument();
			} else if (command.equals("clear")) {
				textAreaManager.clearContent();
				imagedAreaManager.clearContent();
				imagedAreaManager.revalidate();
			}
		}
		
		private boolean checkReasonArgument() {
			ReasonArgumentRecorder argumentRecorder = getReasonArgumentRecorder();
			if (argumentRecorder == null) {
				imagedAreaManager.appendPlainStringAsNewLine(counter + ": 录入推理前提集、结论公式或者论证过程有错误！");
				textAreaManager.appendContentAsNewLine(counter + ": the input reasoning or its arguments have error!");
				inputManager.appendPlainStringAsNewFeedbackLine("录入推理前提集、结论公式或者论证过程有错误！");
				return false;
			}

			String reasonString = argumentRecorder.getReasoning().toLaTeXString();
			imagedAreaManager.appendPlainStringAsNewLine(counter + ": 验证推理：");
			imagedAreaManager.appendLaTeXStringToLastLine(reasonString);
			textAreaManager.appendContentAsNewLine(counter + ": checking reasoning: " + reasonString);
			List<ReasonArgumentStep> argumentStepList = argumentRecorder.getStepList();
			for (ReasonArgumentStep step : argumentStepList) {
				int serialNo = step.getSerialNo();
				Formula stepFormula = step.getFormula();
				String prevSNString = "";
				int[] prevSN = step.getPreviousSN();
				if (prevSN != null) {
					boolean isFirstSN = true;
					for (int i = 0; i < prevSN.length; i++) {
						if (isFirstSN) {
							prevSNString = prevSNString + "(" + prevSN[i] + ")";
							isFirstSN = false;
						} else {
							prevSNString = prevSNString + ",(" + prevSN[i]+")";
						}
					}
				}
				String stepRule = step.getRuleName().getChineseName();
				
				imagedAreaManager.appendPlainStringAsNewLine("            (" + serialNo + ") ");
				textAreaManager.appendContentAsNewLine("\t\t(" + serialNo + ") ");
				
				imagedAreaManager.appendLaTeXStringToLastLine(stepFormula.toSimpleLaTeXString());
				imagedAreaManager.appendPlainStringToLastLine(" // " + prevSNString + stepRule);
				textAreaManager.appendContentToLastLine("~" + stepFormula.toSimpleLaTeXString() + " // " + prevSNString + stepRule);
			}
			
			ReasonArgumentChecker checker = new ReasonArgumentChecker(argumentRecorder); 
			boolean success = checker.checkByUsingTruthTable();
			List<ReasonArgumentCheckStep> checkStepList = checker.getCheckStepList();
			for (ReasonArgumentCheckStep step : checkStepList) {
				int serialNo = step.getReasonStep().getSerialNo();
				LogicReasoningRuleName ruleName = step.getReasonStep().getRuleName(); 
				String stepFormulaString = step.getCheckFormula().toSimpleLaTeXString();
				imagedAreaManager.appendPlainStringAsNewLine("            检查第(" + serialNo + ")步：");
				if (ruleName == LogicReasoningRuleName.AddPremise) {
					imagedAreaManager.appendPlainStringToLastLine("   附加前提 ");
				} else if (ruleName == LogicReasoningRuleName.Premise) {
					imagedAreaManager.appendPlainStringToLastLine("   确认前提 ");
				} else {
					imagedAreaManager.appendPlainStringToLastLine(" 确认永真式 ");
				}
				imagedAreaManager.appendLaTeXStringToLastLine(stepFormulaString);
				textAreaManager.appendContentAsNewLine("\t\tChecking (" + serialNo + "): " + stepFormulaString);
			}
			
			if (success) {
				imagedAreaManager.appendPlainStringAsNewLine("    " + "通过构造真值表检验，上述验证推理有效性的论证过程没有错误！");
				textAreaManager.appendContentAsNewLine("\tThe above argument is correct checking by truth table!");
			} else {
				String checkingFormula = checker.getCheckingFormula().toSimpleLaTeXString();
				TruthAssignmentFunction example = checker.getCounterExample();
				int errorType = checker.getErrorType();
				if (errorType == ReasonArgumentChecker.ERROR_TYPE_FALSE) {
					imagedAreaManager.appendPlainStringAsNewLine("    论证存在错误！在真值赋值 " + example + " 下，下面的公式不是永真式！");
					imagedAreaManager.appendPlainStringAsNewLine("        ");
					imagedAreaManager.appendLaTeXStringToLastLine(checkingFormula);
					textAreaManager.appendContentAsNewLine("\tGiven truth assignment " + example + ", the following formula is not tautology!");
					textAreaManager.appendContentAsNewLine("\t\t" + checkingFormula);
				} else if (errorType == ReasonArgumentChecker.ERROR_TYPE_PREMISE) {
					imagedAreaManager.appendPlainStringAsNewLine("    论证存在错误！公式 ");
					imagedAreaManager.appendLaTeXStringToLastLine(checkingFormula);
					imagedAreaManager.appendPlainStringToLastLine(" 不是前提集中的公式！ ");
					textAreaManager.appendContentAsNewLine("\tFormula " + checkingFormula + " is not in premise formula set!");
				} else if (errorType == ReasonArgumentChecker.ERROR_TYPE_CONSEQUENT) {
					imagedAreaManager.appendPlainStringAsNewLine("    论证存在错误！论证最后一个公式 ");
					imagedAreaManager.appendLaTeXStringToLastLine(checkingFormula);
					imagedAreaManager.appendPlainStringToLastLine(" 不是待验证推理的结论公式！ ");
					textAreaManager.appendContentAsNewLine("\tThe last formula " + checkingFormula + " is not the consequence of the reasoning!");
				} else if (errorType == ReasonArgumentChecker.ERROR_TYPE_ADDPREMISE) {
					imagedAreaManager.appendPlainStringAsNewLine("    论证存在错误！至少还有一个附加前提 ");
					imagedAreaManager.appendLaTeXStringToLastLine(checkingFormula);
					imagedAreaManager.appendPlainStringToLastLine(" 没有在得到结论公式前被消除掉！ ");
					textAreaManager.appendContentAsNewLine("\tAt least one additional premise " + checkingFormula + " has not been eliminated before obtaining the consequence!");
				}
				return false;
			}
			return true;
		}
		
		private boolean checkInputArgument() {
			inputManager.clearFeedback();
			
			Formula formula = null;
			String premiseString = premField.getText().trim();
			if (!premiseString.contentEquals("")) {
				String[] splitPremise = premiseString.split("[,;]");
				for (int i = 0; i < splitPremise.length; i++) {
					String cell = splitPremise[i].trim();
					formula = FormulaBuilder.buildFromLaTexFormulaString(cell);
					if (formula == null) {
						JOptionPane.showMessageDialog(inputManager.getDialog(), "推理前提集  [" + premiseString + "] 包含不是合法公式的串 " + cell + " !\n错误信息：" + FormulaBuilder.getErrorMessage(), "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
						return false;
					}
				}
			}
			String consequentString = consField.getText().trim();
			formula = FormulaBuilder.buildFromLaTexFormulaString(consequentString);
			if (formula == null) {
				JOptionPane.showMessageDialog(inputManager.getDialog(), "推理结论 公式  [" + consequentString + "] 不是合法的公式串 !\n错误信息：" + FormulaBuilder.getErrorMessage(), "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			inputManager.appendPlainStringAsNewFeedbackLine("验证推理：");
			inputManager.appendLaTeXStringToLastFeedbackLine(premiseString + "\\vdash " + consequentString);
			
			String content = inputManager.getContent();
			String[] splittedSteps = content.trim().split("[\n\r;]");
			for (int i = 0; i < splittedSteps.length; i++) {
				String stepContent = splittedSteps[i].trim();
				String[] stepCells = stepContent.split("&");
				if (stepCells.length <= 0) continue;
				
				String cell = stepCells[0].trim();
				int serialNo = getSerialNumber(cell);
				
				if (serialNo != (i+1)) {
					JOptionPane.showMessageDialog(inputManager.getDialog(), "输入第 " + (i+1) + " 行 [" + stepContent + "] 没有以正确序号 开始 " + serialNo + " !", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
					return false;
				}
				inputManager.appendPlainStringAsNewFeedbackLine(cell);

				if (stepCells.length <= 1) {
					JOptionPane.showMessageDialog(inputManager.getDialog(), "输入第 " + i + " 行 [" + stepContent + "] 中没有给出公式!");
					return false;
				}

				cell = stepCells[1].trim();
				formula = FormulaBuilder.buildFromLaTexFormulaString(cell);
				if (formula == null) {
					JOptionPane.showMessageDialog(inputManager.getDialog(), "输入第 " + i + " 行 [" + stepContent + "] 包含不是合法公式的串 " + cell + " !\n错误信息：" + FormulaBuilder.getErrorMessage(), "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
					return false;
				}
				inputManager.appendLaTeXStringToLastFeedbackLine(cell);
				
				if (stepCells.length <=2) {
					JOptionPane.showMessageDialog(inputManager.getDialog(), "输入第 " + i + " 行 [" + stepContent + "] 中没有给出所使用的推理规则名作为注释!");
					return false;
				}
				
				cell = stepCells[2].trim();
				LogicReasoningRuleName ruleName = LogicReasoningRuleName.getRuleName(cell);
				if (ruleName == null) {
					JOptionPane.showMessageDialog(inputManager.getDialog(), "输入第 " + i + " 行 [" + stepContent + "] 包含不能识别的推理规则名 " + cell + " !", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
					return false;
				}
				inputManager.appendPlainStringToLastFeedbackLine("  // " + cell);
			}
			inputManager.updateFeedback();
			return true;
		}
		
		private int getSerialNumber(String cell) {
			int startIndex = 0;
			int endIndex = cell.length();
			if (cell.charAt(0) == '(') startIndex++;
			if (cell.charAt(cell.length()-1) == ')') endIndex--;
			String valueString = cell.substring(startIndex, endIndex).trim();
			int value = -1;
			try {
				value = Integer.parseInt(valueString);
			} catch (NumberFormatException exc) {
				value = -1;
			}
			return value;
		}
		
		private int[] getPreviousSN(String cell) {
			final int MAX_NUMBER = 5;
			int[] values = new int[MAX_NUMBER];
			int counter = 0;
			int startIndex = -1;
			for (int i = 0; i < cell.length(); i++) {
				if (cell.charAt(i) == '(') {
					if (startIndex < 0) startIndex = i+1;
				} else if (cell.charAt(i) == ')') {
					if (startIndex >= 0 && startIndex < i) {
						String valueString = cell.substring(startIndex, i);
						startIndex = -1;
						int value = -1;
						try {
							value = Integer.parseInt(valueString);
						} catch (NumberFormatException exc) {
							value = -1;
						}
						if (value >= 1 && counter < MAX_NUMBER) {
							values[counter] = value;
							counter++;
						}
					}
				}
			}
			if (counter > 0) {
				int[] result = new int[counter];
				for (int i = 0; i < counter; i++) result[i] = values[i];
				return result;
			} 
			return null;
		}

		private String getDefinedRuleNameList() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("规则中文名----规则英文名--------简化的LaTeX串\n");
			
			LogicReasoningRuleName[] definedRuleNames = LogicReasoningRuleName.getDefinedRuleNames();
			for (int i = 0; i < definedRuleNames.length; i++) {
				LogicReasoningRuleName ruleName = LogicReasoningRuleName.AddPremise;
				buffer.append(ruleName.getChineseName() + "----" + ruleName.getEnglishName() + "----" + ruleName.getLatexString() + "\n");
			}
			return buffer.toString();
		}
		
		private ReasonArgumentRecorder getReasonArgumentRecorder() {
			List<Formula> premisList = new ArrayList<Formula>();
			Formula formula = null;
			String premiseString = premField.getText().trim();
			if (!premiseString.contentEquals("")) {
				String[] splitPremise = premiseString.split("[,;]");
				for (int i = 0; i < splitPremise.length; i++) {
					String cell = splitPremise[i].trim();
					formula = FormulaBuilder.buildFromLaTexFormulaString(cell);
					if (formula == null) {
						JOptionPane.showMessageDialog(inputManager.getDialog(), "推理前提集  [" + premiseString + "] 包含不是合法公式的串 " + cell + " !\n错误信息：" + FormulaBuilder.getErrorMessage(), "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
						return null;
					}
					premisList.add(formula);
				}
			}
			String consequentString = consField.getText().trim();
			Formula consequent = FormulaBuilder.buildFromLaTexFormulaString(consequentString);
			if (consequent == null) {
				JOptionPane.showMessageDialog(inputManager.getDialog(), "推理结论 公式  [" + consequentString + "] 不是合法的公式串 !\n错误信息：" + FormulaBuilder.getErrorMessage(), "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				return null;
			}
			inputManager.appendPlainStringAsNewFeedbackLine("验证推理：");
			inputManager.appendLaTeXStringToLastFeedbackLine(premiseString + "\\vdash " + consequentString);
			
			LogicReasoning reasoning = new LogicReasoning(premisList, consequent);
			List<ReasonArgumentStep> stepList = new ArrayList<ReasonArgumentStep>();
			
			String content = inputManager.getContent();
			String[] splittedSteps = content.trim().split("[\n\r;]");
			for (int i = 0; i < splittedSteps.length; i++) {
				String stepContent = splittedSteps[i].trim();
				String[] stepCells = stepContent.split("&");
				if (stepCells.length <= 0) continue;
				
				String cell = stepCells[0].trim();
				int serialNo = getSerialNumber(cell);
				
				if (serialNo != (i+1)) {
					JOptionPane.showMessageDialog(inputManager.getDialog(), "输入第 " + (i+1) + " 行 [" + stepContent + "] 没有以正确序号 开始 " + serialNo + " !", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
					return null;
				}
				inputManager.appendPlainStringAsNewFeedbackLine(cell);

				if (stepCells.length <= 1) {
					JOptionPane.showMessageDialog(inputManager.getDialog(), "输入第 " + i + " 行 [" + stepContent + "] 中没有给出公式!");
					return null;
				}

				cell = stepCells[1].trim();
				formula = FormulaBuilder.buildFromLaTexFormulaString(cell);
				if (formula == null) {
					JOptionPane.showMessageDialog(inputManager.getDialog(), "输入第 " + i + " 行 [" + stepContent + "] 包含不是合法公式的串 " + cell + " !\n错误信息：" + FormulaBuilder.getErrorMessage(), "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
					return null;
				}
				inputManager.appendLaTeXStringToLastFeedbackLine(cell);
				
				if (stepCells.length <=2) {
					JOptionPane.showMessageDialog(inputManager.getDialog(), "输入第 " + i + " 行 [" + stepContent + "] 中没有给出所使用的推理规则名作为注释!");
					return null;
				}
				
				cell = stepCells[2].trim();
				int[] prevSN = getPreviousSN(cell);
				LogicReasoningRuleName ruleName = LogicReasoningRuleName.getRuleName(cell);
				if (ruleName == null) {
					JOptionPane.showMessageDialog(inputManager.getDialog(), "输入第 " + i + " 行 [" + stepContent + "] 包含不能识别的推理规则名 " + cell + " !", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
					return null;
				}
				inputManager.appendPlainStringToLastFeedbackLine(" // " + cell);
				
				ReasonArgumentStep step = new ReasonArgumentStep(serialNo, formula, prevSN, ruleName);
				stepList.add(step);
			}
			
			ReasonArgumentRecorder argumentRecorder = new ReasonArgumentRecorder(reasoning, stepList);
			return argumentRecorder;
		}
	}

}
