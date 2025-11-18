/**
 * 
 */
package com.deedm.legacy.guiManager.setrelfun;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.deedm.legacy.graph.AbstractGraph;
import com.deedm.legacy.graphicAnimation.AnimationArea;
import com.deedm.legacy.guiManager.ImagedTextAreaAnimator;
import com.deedm.legacy.guiManager.ImagedTextAreaManager;
import com.deedm.legacy.guiManager.MainGUIManager;
import com.deedm.legacy.guiManager.MainSwingFrame;
import com.deedm.legacy.guiManager.PlainTextAreaManager;
import com.deedm.legacy.setrelfun.Matrix;
import com.deedm.legacy.setrelfun.Function;
import com.deedm.legacy.setrelfun.Relation;
import com.deedm.legacy.setrelfun.Set;
import com.deedm.legacy.setrelfun.SetrelfunUtil;
import com.deedm.legacy.util.Configuration;

/**
 * @author user
 *
 */
public class FunctionUIManager extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final int width = Math.max(2* MainSwingFrame.screenWidth/5, 1024)*(MainSwingFrame.screenResolution/96);
	private static final int height = Math.max(MainSwingFrame.screenHeight/3, 576)*(MainSwingFrame.screenResolution/96);
	private ImagedTextAreaManager imagedAreaManager = null;
	private PlainTextAreaManager textAreaManager = null;
	private ImagedTextAreaAnimator feedbackAnimator = null;

	private JDialog dialog = null;
	private JButton defaultButton = null;
	private JTextField setAField = null;
	private JTextField setBField = null;
	private JTextArea functionArea = null;
	private JCheckBox injectionBox = null;
	private JCheckBox surjectionBox = null;
	private JCheckBox bijectionBox = null;
	private JCheckBox useMatrixBox = null;
	private JCheckBox useGraphBox = null;
	private JRadioButton charTypeButton = null;
	private JRadioButton intTypeButton = null;

	private boolean ok = false;
	private int counter = 0;

	public FunctionUIManager(PlainTextAreaManager plainManager, ImagedTextAreaManager imageManager) {
		imagedAreaManager = imageManager;
		textAreaManager = plainManager;

		setLayout(new BorderLayout());
		JPanel inputSetPanel = createInputSetPanel();
		JPanel buttonPanel = createButtonPanel();

		add(inputSetPanel, BorderLayout.CENTER);
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
		setAField.setText("");
		setBField.setText("");
		functionArea.setText("");;
		feedbackAnimator.update();
	}
	
	public void setElementTypeInt(boolean intTypeElement) {
		if (intTypeElement == true) {
			intTypeButton.setSelected(true);
			charTypeButton.setSelected(false);
		} else {
			intTypeButton.setSelected(false);
			charTypeButton.setSelected(true);
		}
	}
	
	public boolean setInputs(String setAString, String setBString, String functionString) {
		boolean isIntElement = false;
		if (intTypeButton.isSelected()) isIntElement = true;
		
		setAField.setText(setAString);
		setBField.setText(setBString);
		functionArea.setText(functionString);
		boolean success = true;
		Set setA = null;
		if (!setAString.contentEquals("")) {
			char[] setAElements = SetrelfunUtil.extractSetElements(setAString, isIntElement);
			if (setAElements != null) {
				setA = new Set(setAElements);
				feedbackAnimator.appendLaTeXStringAsNewLine("A = " + setA.toLaTeXString());
			} else {
				feedbackAnimator.appendPlainStringAsNewLine("给出集合A的字符串 " + setAString + " 不是合法的表示集合的字符串！");
				feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + SetrelfunUtil.getExtractErrorMessage());
				success = false;
			}
		}
		Set setB = null;
		if (!setBString.contentEquals("")) {
			char[] setBElements = SetrelfunUtil.extractSetElements(setBString, isIntElement);
			if (setBElements != null) {
				setB = new Set(setBElements);
				feedbackAnimator.appendLaTeXStringAsNewLine("B = " + setB.toLaTeXString());
			} else {
				feedbackAnimator.appendPlainStringAsNewLine("给出集合B的字符串 " + setAString + " 不是合法的表示集合的字符串！");
				feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + SetrelfunUtil.getExtractErrorMessage());
				success = false;
			}
		}
		Relation relation = SetrelfunUtil.extractRelation(setA, setB, functionString, isIntElement);
		if (relation == null) {
			feedbackAnimator.appendPlainStringAsNewLine("字符串 " + functionString + "  不是合法的表示关系的字符串！");
			feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + SetrelfunUtil.getExtractErrorMessage());
			return false;
		} else {
			feedbackAnimator.appendLaTeXStringAsNewLine("F = " + relation.toLaTeXString(isIntElement));
		}
		return success;
	}
	
	public Relation getFunctionF() {
		boolean isIntElement = false;
		if (intTypeButton.isSelected()) isIntElement = true;
		String content = setAField.getText();
		char[] elements = SetrelfunUtil.extractSetElements(content, isIntElement);
		if (elements == null) {
			feedbackAnimator.appendPlainStringAsNewLine("字符串 " + content + " 不是合法的表示集合的字符串！");
			feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + SetrelfunUtil.getExtractErrorMessage());
			return null;
		}
		Set fromSet = new Set(elements);

		content = setBField.getText();
		elements = SetrelfunUtil.extractSetElements(content, isIntElement);
		if (elements == null) {
			feedbackAnimator.appendPlainStringAsNewLine("字符串 " + content + " 不是合法的表示集合的字符串！");
			feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + SetrelfunUtil.getExtractErrorMessage());
			return null;
		}
		Set toSet = new Set(elements);
		
		content = functionArea.getText();
		Relation relation = SetrelfunUtil.extractRelation(fromSet, toSet, content, isIntElement);
		if (relation == null) {
			feedbackAnimator.appendPlainStringAsNewLine("字符串 " + content + "  不是合法的表示关系的字符串！");
			feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + SetrelfunUtil.getExtractErrorMessage());
			return null;
		} else {
			feedbackAnimator.appendLaTeXStringAsNewLine("F = " + relation.toLaTeXString(isIntElement));
		}
		return relation;
	}
	

	public void setFunctionProperties() {
		injectionBox.setSelected(true);
		surjectionBox.setSelected(true);
		bijectionBox.setSelected(true);
	}
	
	public Set getSetA() {
		boolean isIntElement = false;
		if (intTypeButton.isSelected()) isIntElement = true;
		String content = setAField.getText();
		char[] elements = SetrelfunUtil.extractSetElements(content, isIntElement);
		if (elements == null) return null;
		else return new Set(elements);
	}
	
	public Set getSetB() {
		boolean isIntElement = false;
		if (intTypeButton.isSelected()) isIntElement = true;
		String content = setBField.getText();
		char[] elements = SetrelfunUtil.extractSetElements(content, isIntElement);
		if (elements == null) return null;
		else return new Set(elements);
	}

	// 创建输入关系的窗格
	private JPanel createInputSetPanel() {
		// 创建用于输入公式的窗格，使用盒式布局管理
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new GridLayout(3, 2, 5, 0));
		// 设置该窗格使用的带标题的边框
 		inputPanel.setBorder(BorderFactory.createTitledBorder("请输入集合和关系的元素，可用也可不用左右花括号括住，关系的元素用<a, b>的形式给出："));

 		int inputWidth = 60;
		// 创建一个标签用于提示输入集合信息
 		JPanel tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
		JLabel promptLabel = new JLabel("集合(A)：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+A可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('A');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		setAField = new JTextField(inputWidth);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(setAField);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(setAField);
		inputPanel.add(tempPanel);
		
		tempPanel = new JPanel();
		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 35, 15));
		// 创建一个标签用于提示输入性别信息
		promptLabel = new JLabel("集合元素类型：", JLabel.RIGHT);
		// 创建用于将下两个广播按钮捆绑成一组的按钮组对象，同一组的广播按钮只能选中一个，
		// 不同组的广播按钮则可以同时选中不同的广播按钮
		ButtonGroup group = new ButtonGroup();
		// 创建选择性别的广播按钮
		charTypeButton = new JRadioButton("单个字符");
		charTypeButton.setSelected(true);		// 缺省时选中该广播按钮
		group.add(charTypeButton);
		intTypeButton = new JRadioButton("整数类型");
		group.add(intTypeButton);
		tempPanel.add(promptLabel);
		tempPanel.add(charTypeButton);			// 注意是添加广播按钮组件
		tempPanel.add(intTypeButton);		// 而不是添加按钮组对象
		inputPanel.add(tempPanel);

		tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
		promptLabel = new JLabel("集合(B)：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+A可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('B');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		setBField = new JTextField(inputWidth);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(setBField);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(setBField);
		inputPanel.add(tempPanel);
		
		tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
		promptLabel = new JLabel("注意：集合A是函数的源集合，集合B是函数的目标集合！", JLabel.RIGHT);
		tempPanel.add(promptLabel);
		inputPanel.add(tempPanel);
		
		
 		tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 15));
		// 创建一个标签用于提示输入集合信息
		promptLabel = new JLabel("函数(F)：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+F可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('F');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		functionArea = new JTextArea(2, inputWidth-10);
		functionArea.setLineWrap(true);;
		JScrollPane scroll = new JScrollPane(functionArea);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(functionArea);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(scroll);
		inputPanel.add(tempPanel);
		
 		tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 15));
		useMatrixBox = new JCheckBox("给出函数（关系）矩阵");
		tempPanel.add(useMatrixBox);
		useGraphBox = new JCheckBox("给出函数（关系）图");
		tempPanel.add(useGraphBox);
		inputPanel.add(tempPanel);

		JPanel displayPanel = new JPanel();
		displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
		// 设置该窗格使用的带标题的边框
		displayPanel.setBorder(BorderFactory.createTitledBorder("你输入的集合和函数（关系）如下："));
 		AnimationArea area = new AnimationArea(width, 5*height/3);
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

		// 创建一临时性窗格，将这些按钮加入到该窗格，该窗格使用流式布局管理
		JPanel buttonPanelOne = new JPanel();
		buttonPanelOne.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 0));
		// 创建用于确定输入信息的“确定”按钮
		JButton actButton = new JButton("函数性质判断(Y)");
		actButton.setMnemonic('Y');					// 设置快捷键
		// 设置动作命令的名称，这可用来区分用户到底按了什么按钮
		actButton.setActionCommand("ok");			
		actButton.addActionListener(listener);		// 添加事件监听器
		buttonPanelOne.add(actButton);
		defaultButton = actButton;

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
		
		JButton exampleButton = new JButton("例子7.1(1)");
		exampleButton.setActionCommand("example7_1_1");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("例子7.1(2)");
		exampleButton.setActionCommand("example7_1_2");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("例子7.1(3)");
		exampleButton.setActionCommand("example7_1_3");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("例子7.1(4)");
		exampleButton.setActionCommand("example7_1_4");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("问题7.7(1)");
		exampleButton.setActionCommand("problem7_7_1");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("问题7.7(2)");
		exampleButton.setActionCommand("problem7_7_2");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("问题7.7(3)");
		exampleButton.setActionCommand("problem7_7_3");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("问题7.7(4)");
		exampleButton.setActionCommand("problem7_7_4");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		// 创建给出变量集信息和范式信息的窗格与相关组件
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 25, 0));
		infoPanel.setBorder(BorderFactory.createTitledBorder("选择要判断的内容："));
		injectionBox = new JCheckBox("是否单函数");
		infoPanel.add(injectionBox);
		surjectionBox = new JCheckBox("是否满函数");
		infoPanel.add(surjectionBox);
		bijectionBox = new JCheckBox("是否双函数");
		infoPanel.add(bijectionBox);
		
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
				ok = true;
				feedbackAnimator.clearContent();
				counter = counter + 1;
				doFunctionProperties();
				feedbackAnimator.update();
				imagedAreaManager.revalidate();
			} else if (command.equals("cancel")) {
				ok = false;
				dialog.setVisible(false);
			} else if (command.equals("remove")) {
				clearInput();
			} else if (command.equals("generate")) {
				feedbackAnimator.clearContent();
				generateInputs();
				feedbackAnimator.update();
			} else if (command.equals("check")) {
				feedbackAnimator.clearContent();
				boolean success = checkInputs();
				if (success != true) {
					JOptionPane.showMessageDialog(dialog, "输入的集合与关系不完全符合要求！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(dialog, "输入的集合与关系都符合要求，可以判断是否是函数及函数性质！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				}
				feedbackAnimator.update();
			} else if (command.equals("example7_1_1")) {
				feedbackAnimator.clearContent();
				String setAString = "{1, 2, 3, 4, 5}";
				String setBString = "{a, b, c, d}";
				String relationRString = "{<1, a>, <2, a>, <3, c>, <3, d>, <4, b>, <5, b>}";
				setElementTypeInt(false);
				setInputs(setAString, setBString, relationRString);
				injectionBox.setSelected(true);
				surjectionBox.setSelected(true);
				bijectionBox.setSelected(true);
				feedbackAnimator.update();
			} else if (command.equals("example7_1_2")) {
				feedbackAnimator.clearContent();
				String setAString = "{1, 2, 3, 4, 5}";
				String setBString = "{a, b, c, d}";
				String relationRString = "{<1, a>, <2, b>, <3, c>, <4, d>}";
				setElementTypeInt(false);
				setInputs(setAString, setBString, relationRString);
				injectionBox.setSelected(true);
				surjectionBox.setSelected(true);
				bijectionBox.setSelected(true);
				feedbackAnimator.update();
			} else if (command.equals("example7_1_3")) {
				feedbackAnimator.clearContent();
				String setAString = "{1, 2, 3, 4, 5}";
				String setBString = "{a, b, c, d}";
				String relationRString = "{<1, a>, <2, b>, <3, c>, <3, d>}";
				setElementTypeInt(false);
				setInputs(setAString, setBString, relationRString);
				injectionBox.setSelected(true);
				surjectionBox.setSelected(true);
				bijectionBox.setSelected(true);
				feedbackAnimator.update();
			} else if (command.equals("example7_1_4")) {
				feedbackAnimator.clearContent();
				String setAString = "{1, 2, 3, 4, 5}";
				String setBString = "{a, b, c, d}";
				String relationRString = "{<1, a>, <2, b>, <3, b>, <4, d>, <5, c>}";
				setElementTypeInt(false);
				setInputs(setAString, setBString, relationRString);
				injectionBox.setSelected(true);
				surjectionBox.setSelected(true);
				bijectionBox.setSelected(true);
				feedbackAnimator.update();
			} else if (command.equals("problem7_7_1")) {
				feedbackAnimator.clearContent();
				String setAString = "{1, 2, 3, 4, 5}";
				String setBString = "{a, b, c, d, e}";
				String relationRString = "{<1, b>, <2, d>, <3, b>, <4, d>, <5, a>}";
				setElementTypeInt(false);
				setInputs(setAString, setBString, relationRString);
				injectionBox.setSelected(true);
				surjectionBox.setSelected(true);
				bijectionBox.setSelected(true);
				feedbackAnimator.update();
			} else if (command.equals("problem7_7_2")) {
				feedbackAnimator.clearContent();
				String setAString = "{1, 2, 3, 4, 5}";
				String setBString = "{a, b, c, d, e}";
				String relationRString = "{<1, a>, <2, d>, <3, c>, <4, e>, <5, b>}";
				setElementTypeInt(false);
				setInputs(setAString, setBString, relationRString);
				injectionBox.setSelected(true);
				surjectionBox.setSelected(true);
				bijectionBox.setSelected(true);
				feedbackAnimator.update();
			} else if (command.equals("problem7_7_3")) {
				feedbackAnimator.clearContent();
				String setAString = "{1, 2, 3, 4, 5}";
				String setBString = "{a, b, c, d, e}";
				String relationRString = "{<1, d>, <2, c>, <3, e>, <4, e>, <5, d>}";
				setElementTypeInt(false);
				setInputs(setAString, setBString, relationRString);
				injectionBox.setSelected(true);
				surjectionBox.setSelected(true);
				bijectionBox.setSelected(true);
				feedbackAnimator.update();
			} else if (command.equals("problem7_7_4")) {
				feedbackAnimator.clearContent();
				String setAString = "{1, 2, 3, 4, 5}";
				String setBString = "{a, b, c, d, e}";
				String relationRString = "{<1, d>, <2, e>, <3, b>, <4, c>, <5, a>}";
				setElementTypeInt(false);
				setInputs(setAString, setBString, relationRString);
				injectionBox.setSelected(true);
				surjectionBox.setSelected(true);
				bijectionBox.setSelected(true);
				feedbackAnimator.update();
			} else if (command.equals("clear")) {
				textAreaManager.clearContent();
				imagedAreaManager.clearContent();
				imagedAreaManager.revalidate();
			}
		}
		
		public boolean doFunctionProperties() {
			Set setA = getSetA();
			if (setA == null) {
				textAreaManager.appendContent("The string " + setAField.getText() + " does not give a legal set!\n");
				imagedAreaManager.appendPlainStringAsNewLine("字符串 " + setAField.getText() + " 没有给出合法的集合A！");
				feedbackAnimator.appendPlainStringAsNewLine("字符串 " + setAField.getText() + " 没有给出合法的集合A！");
				return false;
			} else feedbackAnimator.appendLaTeXStringAsNewLine("A = " + setA.toLaTeXString());
			textAreaManager.appendContent(counter + ": The set A = " + setA.toString());
			imagedAreaManager.appendPlainStringAsNewLine(counter + ": 集合 ");
			imagedAreaManager.appendLaTeXStringToLastLine("A = " + setA.toLaTeXString());

			Set setB = getSetB();
			if (setB == null) {
				textAreaManager.appendContent("\nThe string " + setBField.getText() + " does not give a legal set!\n");
				imagedAreaManager.appendPlainStringAsNewLine("字符串 " + setBField.getText() + " 没有给出合法的集合B！");
				feedbackAnimator.appendPlainStringAsNewLine("字符串 " + setBField.getText() + " 没有给出合法的集合B！");
				return false;
			} else feedbackAnimator.appendLaTeXStringAsNewLine("B = " + setB.toLaTeXString());
			textAreaManager.appendContent(", B = " + setB.toString() + "\n");
			imagedAreaManager.appendLaTeXStringToLastLine(", B = " + setB.toLaTeXString());
			
			boolean isIntElement = false;
			if (intTypeButton.isSelected()) isIntElement = true;
			String content = functionArea.getText();
			Relation functionF = SetrelfunUtil.extractRelation(setA, setB, content, isIntElement);
			if (functionF == null) {
				textAreaManager.appendContent("\n\tThe string " + content + " is not a legal relation for F!\n");
				imagedAreaManager.appendPlainStringAsNewLine("    字符串 " + content + " 没有合法地给出关系F！");
				imagedAreaManager.appendPlainStringAsNewLine("    错误信息： " + SetrelfunUtil.getExtractErrorMessage());
				feedbackAnimator.appendPlainStringAsNewLine("字符串 " + content + "  不是合法的表示关系的字符串！");
				return false;
			} else {
				textAreaManager.appendContent("\n\tF = " + functionF.toLaTeXString(isIntElement) + "\n");
				imagedAreaManager.appendPlainStringAsNewLine("    函数（关系）: ");
				imagedAreaManager.appendLaTeXStringToLastLine("F = " + functionF.toLaTeXString(isIntElement) + " \\subseteq A\\times B");
				if (useMatrixBox.isSelected()) {
					Matrix matrix = functionF.getMatrix();
					textAreaManager.appendContent("\t\t" + "M_F = " + matrix.toLaTeXString() + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    函数（关系）矩阵: ");
					imagedAreaManager.appendLaTeXStringToLastLine("M_F = " + matrix.toLaTeXString());
				}
				if (useGraphBox.isSelected()) {
					String dotFileName = Configuration.dataFilePath + "FunctionF.dot";
					String pngFileName = Configuration.dataFilePath + "FunctionF.png";
					try {
						PrintWriter writer = new PrintWriter(dotFileName);
						AbstractGraph graph = functionF.getRelationGraph(isIntElement);
						graph.simplyWriteToDotFile(writer);
						boolean success = MainGUIManager.generatePNGFile(dotFileName, pngFileName, false);
						if (success) {
							textAreaManager.appendContent("\t\tHere gives the relation graph of the relation F!\n");
							imagedAreaManager.appendPlainStringAsNewLine("    函数（关系）图: ");
							imagedAreaManager.appendImageFileToLastLine(pngFileName);
						} else {
							textAreaManager.appendContent("\t\tCan not generate the relation graph of the relation F!\n");
							textAreaManager.appendContent(MainGUIManager.errorMessage);
							imagedAreaManager.appendPlainStringAsNewLine("    函数（关系）图: 无法生成！");
						}
					} catch (Exception e) {
						textAreaManager.appendContent("\t\tCan not generate the relation graph of the relation F!\n");
						textAreaManager.appendContent("Exception message: " + e.getMessage());
						imagedAreaManager.appendPlainStringAsNewLine("    函数（关系）图: 无法生成！");
					}
				}
				feedbackAnimator.appendLaTeXStringAsNewLine("F = " + functionF.toLaTeXString(isIntElement));
			}
			boolean isFunction = functionF.isFunction();
			if (isFunction) {
				textAreaManager.appendContent("\tThe relation F is a function\n");
				imagedAreaManager.appendPlainStringAsNewLine("    关系F是函数！");
			} else {
				textAreaManager.appendContent("\tThe relation is NOT a function\n");
				imagedAreaManager.appendPlainStringAsNewLine("    关系R不是函数！");
			}
			if (!isFunction) return true;
			Function function = new Function(functionF);
			
			if (injectionBox.isSelected()) {
				if (function.isInjection()) {
					textAreaManager.appendContent("\tThe function F is an injection!\n");
					imagedAreaManager.appendPlainStringAsNewLine("    函数F是单函数！");
				} else {
					char example = Function.getExample();
					String message = Set.getElementLabel(example, isIntElement);
					textAreaManager.appendContent("\tThe function F is NOT an injection, the element " + message + " has more than one pre-image!\n");
					imagedAreaManager.appendPlainStringAsNewLine("    函数F不是单函数：元素 ");
					imagedAreaManager.appendLaTeXStringToLastLine(message);
					imagedAreaManager.appendPlainStringToLastLine(" 有多个原像！");
				}
			}
			if (surjectionBox.isSelected()) {
				if (function.isSurjection()) {
					textAreaManager.appendContent("\tThe function F is a surjection!\n");
					imagedAreaManager.appendPlainStringAsNewLine("    函数F是满函数！");
				} else {
					char example = Function.getExample();
					String message = Set.getElementLabel(example, isIntElement);
					textAreaManager.appendContent("\tThe function F is NOT an surjection, the element " + message + " has not pre-image!\n");
					imagedAreaManager.appendPlainStringAsNewLine("    函数F不是满函数：元素 ");
					imagedAreaManager.appendLaTeXStringToLastLine(message);
					imagedAreaManager.appendPlainStringToLastLine(" 没有原像！");
				}
			}
			if (bijectionBox.isSelected()) {
				if (function.isBijection()) {
					textAreaManager.appendContent("\tThe function F is a bijection!\n");
					imagedAreaManager.appendPlainStringAsNewLine("    函数F是双函数！");
				} else {
					char example = Function.getExample();
					String message = Set.getElementLabel(example, isIntElement);
					textAreaManager.appendContent("\tThe function F is NOT an bijection, the element " + message + " does not has only one pre-image!\n");
					imagedAreaManager.appendPlainStringAsNewLine("    函数F不是双函数：元素 ");
					imagedAreaManager.appendLaTeXStringToLastLine(message);
					imagedAreaManager.appendPlainStringToLastLine(" 不是恰好有一个原像或者它没有原像！");
				}
			}
			
			return true;
		}
		
		public boolean generateInputs() {
			Set setA = getSetA();
			if (setA == null) {
				char[] elements = {'1', '2', '3', '4', '5', '6'};
				setA = new Set(elements);
				setAField.setText(setA.toString());
				intTypeButton.setSelected(false);
				charTypeButton.setSelected(true);
			}
			Set setB = getSetB();
			if (setB == null) {
				int number = (int)(Math.random() * 6) + 3;
				char[] elements = new char[number];
				for (int i = 0; i < elements.length; i++) elements[i] = (char)('a' + i);
				setB = new Set(elements);
				setBField.setText(setB.toString());
				intTypeButton.setSelected(false);
				charTypeButton.setSelected(true);
			}
			Relation FunctionF = null;
			Double seeds = Math.random();
			if (seeds < 0.5) FunctionF = Function.randomGenerateFunction(setA, setB);
			else {
				if (setA.length() > setB.length()) FunctionF = Function.randomGenerateSurjection(setA, setB);
				else if (setA.length() < setB.length()) FunctionF = Function.randomGenerateInjection(setA, setB);
				else FunctionF = Function.randomGenerateBijection(setA, setB);
			}
			functionArea.setText(FunctionF.toString());
			
			checkInputs();		// 让生成的信息通过合法性检查后反馈在输入界面之上
			return true;
		}
		
		public boolean checkInputs() {
			Set setA = getSetA();
			if (setA == null) {
				feedbackAnimator.appendPlainStringAsNewLine("字符串 " + setAField.getText() + " 没有给出合法的集合A！");
				return false;
			} else feedbackAnimator.appendLaTeXStringAsNewLine("A = " + setA.toLaTeXString());
			
			boolean isIntElement = false;
			if (intTypeButton.isSelected()) isIntElement = true;

			Set setB = getSetB();
			if (setB == null) {
				feedbackAnimator.appendPlainStringAsNewLine("字符串 " + setBField.getText() + " 没有给出合法的集合S！");
				return false;
			} else {
				feedbackAnimator.appendLaTeXStringAsNewLine("B = " + setB.toLaTeXString());
			}

			String content = functionArea.getText();
			Relation relationF = SetrelfunUtil.extractRelation(setA, setB, content, isIntElement);
			if (relationF == null) {
				feedbackAnimator.appendPlainStringAsNewLine("字符串 " + content + "  不是合法的表示关系的字符串！");
				return false;
			} else {
				feedbackAnimator.appendLaTeXStringAsNewLine("F = " + relationF.toLaTeXString(isIntElement) + " \\subseteq A\\times B");
			}
			return true;
		}
	}
	
}
