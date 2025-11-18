package com.deedm.legacy.guiManager.logic;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.deedm.legacy.graphicAnimation.AnimationArea;
import com.deedm.legacy.guiManager.ImagedTextAreaAnimator;
import com.deedm.legacy.guiManager.ImagedTextAreaManager;
import com.deedm.legacy.guiManager.MainSwingFrame;
import com.deedm.legacy.guiManager.PlainTextAreaManager;
import com.deedm.legacy.proplogic.FormulaBuilder;
import com.deedm.legacy.proplogic.FormulaTruthTable;
import com.deedm.legacy.proplogic.TruthAssignment;
import com.deedm.legacy.proplogic.TruthAssignmentFunction;
import com.deedm.legacy.proplogic.formula.Formula;
import com.deedm.legacy.setrelfun.SetrelfunUtil;

public class FormulaTruthValueUIManager extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final int width = Math.max(2* MainSwingFrame.screenWidth/5, 1024)*(MainSwingFrame.screenResolution/96);
	private static final int height = Math.max(MainSwingFrame.screenHeight/3, 600)*(MainSwingFrame.screenResolution/96);
	private ImagedTextAreaManager imagedAreaManager = null;
	private PlainTextAreaManager textAreaManager = null;
	private ImagedTextAreaAnimator feedbackAnimator = null;
	
	private JDialog dialog = null;
	private JButton defaultButton = null;
	private JTextArea formulaArea = null;
	private JTextField varField = null;
	private JTextArea assignmentArea = null;
	private JCheckBox detailedBox = null;
	
	private boolean ok = false;
	private int counter = 0;

	public FormulaTruthValueUIManager(PlainTextAreaManager plainManager, ImagedTextAreaManager imageManager) {
		imagedAreaManager = imageManager;
		textAreaManager = plainManager;

		setLayout(new BorderLayout());
		JPanel inputPanel = createInputPanel();
		JPanel buttonPanel = createButtonPanel();

		add(inputPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	public boolean showDialog(Component parent, String title) {
		ok = false;
		
		Frame owner = null;
		if (parent instanceof Frame) owner = (Frame)parent;
		else owner = (Frame)SwingUtilities.getAncestorOfClass(Frame.class, parent);
		
		if (dialog == null || dialog.getOwner() != owner) {
			dialog = new JDialog(owner, true);
			dialog.add(this);
			if (defaultButton != null) dialog.getRootPane().setDefaultButton(defaultButton);

			dialog.setPreferredSize(new Dimension(width, height));
			dialog.pack();
		}
		
		Point position = owner.getLocation();
		Dimension size = owner.getSize();
		position.x = position.x + (size.width - width)/2;
		position.y = position.y + (size.height -height)/2;
		dialog.setLocation(position);
		dialog.setTitle(title);
		dialog.setVisible(true);
		return ok;
	}

	public void clearFeedback() {
		feedbackAnimator.clearContent();
	}
	
	public void updateFeedback() {
		feedbackAnimator.update();
	}

	public void clearInput() {
		feedbackAnimator.clearContent();
		formulaArea.setText("");
		varField.setText("");
		assignmentArea.setText("");;
		feedbackAnimator.update();
	}
	
	public boolean setInputs(String formulaString, String varString, String assignmentString) {
		formulaArea.setText(formulaString);
		varField.setText(varString);
		assignmentArea.setText(assignmentString);
		return checkInput();
	}

	public Formula getFormula() {
		String content = formulaArea.getText();
		Formula formula = FormulaBuilder.buildFromLaTexFormulaString(content);
		return formula;
	}

	public void setFormula(String formulaString) {
		formulaArea.setText(formulaString);
		checkInput();
	}
	
	public char[] getVariableSet(Formula formula) {
		String content = varField.getText().trim();
		if (content.contentEquals("")) return formula.getAllVariables();
		else {
			char[] elements = SetrelfunUtil.extractSetElements(content, false);
			return elements;
		}
	}
	
	public List<TruthAssignmentFunction> getFunctionList(char[] varSet) {
		List<TruthAssignmentFunction> resultList = new ArrayList<TruthAssignmentFunction>();
		
		String content = assignmentArea.getText().trim();
		if (content.contentEquals("")) return resultList;

		String[] splitedStrings = content.split("[,\n\r]");
		for (int i = 0; i < splitedStrings.length; i++) {
			String values = splitedStrings[i].trim();
			int index = 0;
			List<TruthAssignment> assignmentList = new ArrayList<TruthAssignment>();
			while (index < values.length() && index < varSet.length) {
				if (values.charAt(index) == '0') assignmentList.add(new TruthAssignment(varSet[index], false));
				else assignmentList.add(new TruthAssignment(varSet[index], true));
				index++;
			}
			resultList.add(new TruthAssignmentFunction(assignmentList));
		}
		return resultList;
	}

	public boolean checkInput() {
		String content = formulaArea.getText();
		Formula formula = FormulaBuilder.buildFromLaTexFormulaString(content);
		if (formula == null) {
			feedbackAnimator.appendPlainStringAsNewLine("字符串 " + content + " 不是合法的命题逻辑公式！");
			feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + FormulaBuilder.getErrorMessage());
			return false;
		} else {
			feedbackAnimator.appendLaTeXStringAsNewLine(content);
		}
		feedbackAnimator.update();
	
		return true;
	}
	
	// 创建输入关系的窗格
	private JPanel createInputPanel() {
		// 创建用于输入公式的窗格，使用盒式布局管理
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BorderLayout());
		// 设置该窗格使用的带标题的边框
 		inputPanel.setBorder(BorderFactory.createTitledBorder("请输入使用LaTeX命令串表示的公式，命题变量集（用p,q,r等形式）和赋值（用000,001等形式)"));

 		JPanel center = new JPanel();
 		center.setLayout(new GridLayout(2,1));
 		
 		JPanel tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 15));
		// 创建一个标签用于提示输入集合信息
 		JLabel promptLabel = new JLabel("    命题逻辑公式(F)：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+F可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('F');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		formulaArea = new JTextArea(3, 100);
		formulaArea.setLineWrap(true);;
		JScrollPane scroll = new JScrollPane(formulaArea);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(formulaArea);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(scroll);
		center.add(tempPanel);

		tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 15));
		// 创建一个标签用于提示输入集合信息
		promptLabel = new JLabel("对命题变量的赋值(A)：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+A可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('A');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		assignmentArea = new JTextArea(3, 100);
		assignmentArea.setLineWrap(true);;
		scroll = new JScrollPane(assignmentArea);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(assignmentArea);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(scroll);
		center.add(tempPanel);
		inputPanel.add(center, BorderLayout.CENTER);

		// 创建给出变量集信息和范式信息的窗格与相关组件
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BorderLayout());

		int inputWidth = 60;
		// 创建一个标签用于提示输入集合信息
		tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
		promptLabel = new JLabel("命题变量(V)：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+V可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('V');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		varField = new JTextField(inputWidth);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(varField);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(varField);
		infoPanel.add(tempPanel, BorderLayout.CENTER);

		detailedBox = new JCheckBox("给出计算公式真值的详细过程     ");
		detailedBox.setSelected(true);
		infoPanel.add(detailedBox, BorderLayout.EAST);
		inputPanel.add(infoPanel, BorderLayout.SOUTH);
		
		
		JPanel displayPanel = new JPanel();
		displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
		// 设置该窗格使用的带标题的边框
		displayPanel.setBorder(BorderFactory.createTitledBorder("输入的命题逻辑公式如下："));
 		AnimationArea area = new AnimationArea(width, height/5);
		feedbackAnimator = new ImagedTextAreaAnimator(area);
		displayPanel.add(area);
		
		JPanel formulaPanel = new JPanel();
		formulaPanel.setLayout(new GridLayout(2, 1));
		formulaPanel.add(inputPanel);
		formulaPanel.add(displayPanel);
		return formulaPanel;
	}

	// 创建按钮及其所在的窗格
	private JPanel createButtonPanel() {
		// 创建一事件监听器，该监听器监听下述两个按钮上发生的事件
		SimpleListener listener = new SimpleListener();

		JPanel buttonPanelOne = new JPanel();
		buttonPanelOne.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 0));
		// 创建用于确定输入信息的“确定”按钮
		JButton actButton = new JButton("开始计算(Y)");
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
				feedbackAnimator.clearContent();
				counter = counter+1;
				String laTeXString = formulaArea.getText();
				Formula formula = FormulaBuilder.buildFromLaTexFormulaString(laTeXString);
				if (formula != null) {
					feedbackAnimator.appendLaTeXStringAsNewLine(laTeXString);
					textAreaManager.appendContent(counter + ": calculate the truth values of formula " + laTeXString + " as follows\n");
					
					imagedAreaManager.appendPlainStringAsNewLine(counter + ": 计算公式 ");
					imagedAreaManager.appendLaTeXStringToLastLine(laTeXString);
					imagedAreaManager.appendPlainStringToLastLine(" 的真值如下：");

					char[] varSet = getVariableSet(formula);
					List<TruthAssignmentFunction> functionList = getFunctionList(varSet);
					
					FormulaTruthTable table = null;
					if (functionList.size() > 0) table = new FormulaTruthTable(formula, varSet, functionList);
					else table = new FormulaTruthTable(formula, varSet);
					
					String truthTableString = null;
					if (detailedBox.isSelected()) truthTableString = table.createDetailedTruthTable();
					else truthTableString = table.createSimpleTruthTable(); 
					textAreaManager.appendContent("\t" + truthTableString + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    ");
					imagedAreaManager.appendLaTeXStringToLastLine(truthTableString);
				} else {
					// 弹出一窗口显示一些信息
					JOptionPane.showMessageDialog(dialog, "输入的字符串 " + laTeXString + " 不是合法的公式！\n错误信息：" + FormulaBuilder.getErrorMessage(), "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
					textAreaManager.appendContent(counter + " : " + laTeXString + " is not a legal LaTeX formula!\n");
					imagedAreaManager.appendPlainStringAsNewLine(counter + " : 公式 " + laTeXString + "不是合法的公式！");
				}
				feedbackAnimator.update();
				imagedAreaManager.revalidate();
			} else if (command.equals("cancel")) {
				ok = false;
				dialog.setVisible(false);
			} else if (command.equals("remove")) {
				clearInput();
			} else if (command.equals("generate")) {
				String plainFormula = FormulaBuilder.randomGenerateFormulaControlledByOperatorNumber(4, 5);
				Formula formula = FormulaBuilder.buildFromSymbolFormulaString(plainFormula);
				if (formula != null) {
					String laTeXString = formula.toSimpleLaTeXString();
					formulaArea.setText(laTeXString);
					feedbackAnimator.clearContent();
					feedbackAnimator.appendLaTeXStringAsNewLine(laTeXString);
					feedbackAnimator.update();
				} else {
					// 弹出一窗口显示一些信息
					JOptionPane.showMessageDialog(dialog, "自动生成公式失败！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				}
			} else if (command.equals("check")) {
				feedbackAnimator.clearContent();
				String laTeXString = formulaArea.getText();
				Formula formula = FormulaBuilder.buildFromLaTexFormulaString(laTeXString);
				if (formula == null) {
					// 弹出一窗口显示一些信息
					JOptionPane.showMessageDialog(dialog, "输入的字符串 " + laTeXString + " 不是合法的公式！\n错误信息：" + FormulaBuilder.getErrorMessage(), "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
					feedbackAnimator.appendPlainStringAsNewLine(counter + " : 公式 " + laTeXString + "不是合法的公式！");
				} else {
					// 弹出一窗口显示一些信息
					JOptionPane.showMessageDialog(dialog, "输入的字符串 " + laTeXString + " 是合法的公式！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
					feedbackAnimator.appendLaTeXStringAsNewLine(laTeXString);
				}
				feedbackAnimator.update();
			} else if (command.equals("example2_4")) {
				String laTeXString = "(\\neg(\\neg p)\\rightarrow(t\\vee s))\\wedge(s\\leftrightarrow r)";
				formulaArea.setText(laTeXString);
			} else if (command.equals("problem2_6_1")) {
				String laTeXString = "p\\wedge(\\neg s)\\wedge(\\neg s)";
				formulaArea.setText(laTeXString);
			} else if (command.equals("problem2_6_2")) {
				String laTeXString = "\\neg(\\neg r)\\rightarrow(\\neg(q\\vee p))";
				formulaArea.setText(laTeXString);
			} else if (command.equals("problem2_6_3")) {
				String laTeXString = "((p\\vee p)\\leftrightarrow(p\\wedge s))\\rightarrow(p\\wedge s)";
				formulaArea.setText(laTeXString);
			} else if (command.equals("problem2_6_4")) {
				String laTeXString = "(r\\rightarrow(q\\leftrightarrow s))\\vee(q\\leftrightarrow s)";
				formulaArea.setText(laTeXString);
			} else if (command.equals("problem2_17")) {
				String laTeXString = "\\neg(p\\rightarrow q)\\wedge((\\neg q)\\leftrightarrow r)";
				formulaArea.setText(laTeXString);
			} else if (command.equals("clear")) {
				textAreaManager.clearContent();
				imagedAreaManager.clearContent();
				imagedAreaManager.revalidate();
			}
		}
	}
	
}
