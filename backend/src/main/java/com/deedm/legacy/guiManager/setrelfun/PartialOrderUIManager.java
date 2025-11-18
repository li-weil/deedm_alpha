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
import com.deedm.legacy.setrelfun.OrderedPair;
import com.deedm.legacy.setrelfun.PartialOrder;
import com.deedm.legacy.setrelfun.Relation;
import com.deedm.legacy.setrelfun.Set;
import com.deedm.legacy.setrelfun.SetrelfunUtil;
import com.deedm.legacy.util.Configuration;

/**
 * @author user
 *
 */
public class PartialOrderUIManager extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final int width = Math.max(2* MainSwingFrame.screenWidth/5, 1024)*(MainSwingFrame.screenResolution/96)*(MainSwingFrame.screenResolution/96);
	private static final int height = Math.max(MainSwingFrame.screenHeight/3, 576)*(MainSwingFrame.screenResolution/96)*(MainSwingFrame.screenResolution/96);
	private ImagedTextAreaManager imagedAreaManager = null;
	private PlainTextAreaManager textAreaManager = null;
	private ImagedTextAreaAnimator feedbackAnimator = null;

	private JDialog dialog = null;
	private JButton defaultButton = null;
	private JTextField setAField = null;
	private JTextField setSField = null;
	private JTextArea relationRArea = null;
	private JCheckBox minimumBox = null;
	private JCheckBox maximumBox = null;
	private JCheckBox greatestBox = null;
	private JCheckBox leastBox = null;
	private JCheckBox upperBox = null;
	private JCheckBox lowerBox = null;
	private JCheckBox lubBox = null;
	private JCheckBox glbBox = null;
	private JCheckBox useDivideBox = null;
	private JCheckBox useMatrixBox = null;
	private JCheckBox useGraphBox = null;
	private JCheckBox useHasseBox = null;
	private JRadioButton charTypeButton = null;
	private JRadioButton intTypeButton = null;
	private SimpleListener listener = null;;

	private boolean ok = false;
	private int counter = 0;

	public PartialOrderUIManager(PlainTextAreaManager plainManager, ImagedTextAreaManager imageManager) {
		imagedAreaManager = imageManager;
		textAreaManager = plainManager;

		listener = new SimpleListener();
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
		setSField.setText("");
		relationRArea.setText("");;
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
	
	public boolean setInputs(String setAString, String setSString, String relationRString) {
		boolean isIntElement = false;
		if (intTypeButton.isSelected()) isIntElement = true;
		
		setAField.setText(setAString);
		setSField.setText(setSString);
		relationRArea.setText(relationRString);
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
		Set setS = null;
		if (!setSString.contentEquals("")) {
			char[] setSElements = SetrelfunUtil.extractSetElements(setSString, isIntElement);
			if (setSElements != null) {
				setS = new Set(setSElements);
				feedbackAnimator.appendLaTeXStringAsNewLine("S = " + setS.toLaTeXString());
				if (!setA.isSubset(setS)) {
					feedbackAnimator.appendPlainStringAsNewLine("集合S不是集合A的子集！ ");
					success = false;
				}
			} else {
				feedbackAnimator.appendPlainStringAsNewLine("给出子集S的字符串 " + setAString + " 不是合法的表示集合的字符串！");
				feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + SetrelfunUtil.getExtractErrorMessage());
				success = false;
			}
		}
		Relation relation = SetrelfunUtil.extractRelation(setA, setA, relationRString, isIntElement);
		if (relation == null) {
			feedbackAnimator.appendPlainStringAsNewLine("字符串 " + relationRString + "  不是合法的表示关系的字符串！");
			feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + SetrelfunUtil.getExtractErrorMessage());
			return false;
		} else {
			feedbackAnimator.appendLaTeXStringAsNewLine("R = " + relation.toLaTeXString(isIntElement));
		}
		return success;
	}
	
	public Relation getRelationR() {
		Set fromSet = null;
		boolean isIntElement = false;
		if (intTypeButton.isSelected()) isIntElement = true;
		String content = setAField.getText();
		char[] elements = SetrelfunUtil.extractSetElements(content, isIntElement);
		if (elements == null) {
			feedbackAnimator.appendPlainStringAsNewLine("字符串 " + content + " 不是合法的表示集合的字符串！");
			feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + SetrelfunUtil.getExtractErrorMessage());
			return null;
		}
		Relation relation = null;
		fromSet = new Set(elements);
		if (isIntElement && useDivideBox.isSelected()) {
			relation = PartialOrder.createDivisionOrder(fromSet);
		} else {
			content = relationRArea.getText();
			relation = SetrelfunUtil.extractRelation(fromSet, fromSet, content, isIntElement);
		}
		if (relation == null) {
			feedbackAnimator.appendPlainStringAsNewLine("字符串 " + content + "  不是合法的表示关系的字符串！");
			feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + SetrelfunUtil.getExtractErrorMessage());
			return null;
		} else {
			feedbackAnimator.appendLaTeXStringAsNewLine("R = " + relation.toLaTeXString(isIntElement));
		}
		return relation;
	}
	

	public void setElementsCalculation() {
		minimumBox.setSelected(true);
		maximumBox.setSelected(true);
		leastBox.setSelected(true);
		greatestBox.setSelected(true);
	}
	
	public Set getSetA() {
		boolean isIntElement = false;
		if (intTypeButton.isSelected()) isIntElement = true;
		String content = setAField.getText();
		char[] elements = SetrelfunUtil.extractSetElements(content, isIntElement);
		if (elements == null) return null;
		else return new Set(elements);
	}
	
	public Set getSetS() {
		boolean isIntElement = false;
		if (intTypeButton.isSelected()) isIntElement = true;
		String content = setSField.getText();
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
		intTypeButton.setActionCommand("intType");
		intTypeButton.addActionListener(listener);
		charTypeButton.setActionCommand("intType");
		charTypeButton.addActionListener(listener);
		group.add(intTypeButton);
		tempPanel.add(promptLabel);
		tempPanel.add(charTypeButton);			// 注意是添加广播按钮组件
		tempPanel.add(intTypeButton);		// 而不是添加按钮组对象
		inputPanel.add(tempPanel);

		tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
		promptLabel = new JLabel("集合(S)：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+A可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('S');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		setSField = new JTextField(inputWidth);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(setSField);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(setSField);
		inputPanel.add(tempPanel);
		
		tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
		promptLabel = new JLabel("注意：集合S必须是集合A的子集，用于计算子集的上下界、上下确界！", JLabel.RIGHT);
		tempPanel.add(promptLabel);
		inputPanel.add(tempPanel);
		
		
 		tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 15));
		// 创建一个标签用于提示输入集合信息
		promptLabel = new JLabel("关系(R)：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+R可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('R');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		relationRArea = new JTextArea(2, inputWidth-10);
		relationRArea.setLineWrap(true);;
		JScrollPane scroll = new JScrollPane(relationRArea);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(relationRArea);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(scroll);
		inputPanel.add(tempPanel);
		
 		tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 15));
		useDivideBox = new JCheckBox("以整除关系为偏序");
		if (!intTypeButton.isSelected()) useDivideBox.setEnabled(false);
		if (useDivideBox.isEnabled() && useDivideBox.isSelected()) relationRArea.setEditable(false);
		useDivideBox.setActionCommand("useDivide");
		useDivideBox.addActionListener(listener);
		tempPanel.add(useDivideBox);
		useMatrixBox = new JCheckBox("给出关系矩阵");
		tempPanel.add(useMatrixBox);
		useGraphBox = new JCheckBox("给出关系图");
		tempPanel.add(useGraphBox);
		useHasseBox = new JCheckBox("给出哈斯图");
		useHasseBox.setSelected(true);
		tempPanel.add(useHasseBox);
		inputPanel.add(tempPanel);

		JPanel displayPanel = new JPanel();
		displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
		// 设置该窗格使用的带标题的边框
		displayPanel.setBorder(BorderFactory.createTitledBorder("你输入的集合和关系如下："));
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

		// 创建一临时性窗格，将这些按钮加入到该窗格，该窗格使用流式布局管理
		JPanel buttonPanelOne = new JPanel();
		buttonPanelOne.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 0));
		// 创建用于确定输入信息的“确定”按钮
		JButton actButton = new JButton("偏序关系计算(Y)");
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
		
		JButton exampleButton = new JButton("问题6.38");
		exampleButton.setActionCommand("problem6_38");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("问题6.40(1)");
		exampleButton.setActionCommand("problem6_40_1");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("问题6.40(2)");
		exampleButton.setActionCommand("problem6_40_2");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("问题6.40(3)");
		exampleButton.setActionCommand("problem6_40_3");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("问题6.42(1)");
		exampleButton.setActionCommand("problem6_42_1");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("问题6.42(2)");
		exampleButton.setActionCommand("problem6_42_2");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		// 创建给出变量集信息和范式信息的窗格与相关组件
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 25, 0));
		infoPanel.setBorder(BorderFactory.createTitledBorder("选择要计算的内容："));
		minimumBox = new JCheckBox("极小元");
		infoPanel.add(minimumBox);
		maximumBox = new JCheckBox("极大元");
		infoPanel.add(maximumBox);
		leastBox = new JCheckBox("最小元");
		infoPanel.add(leastBox);
		greatestBox = new JCheckBox("最大元");
		infoPanel.add(greatestBox);
		lowerBox = new JCheckBox("下界");
		infoPanel.add(lowerBox);
		upperBox = new JCheckBox("上界");
		infoPanel.add(upperBox);
		glbBox = new JCheckBox("下确界");
		infoPanel.add(glbBox);
		lubBox = new JCheckBox("下确界");
		infoPanel.add(lubBox);
		
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
				doPartialOrderCalculate();
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
					JOptionPane.showMessageDialog(dialog, "输入的集合与关系都符合要求，可以进行偏序关系计算！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				}
				feedbackAnimator.update();
			} else if (command.equals("intType")) {
				if (intTypeButton.isSelected()) {
					useDivideBox.setEnabled(true);
					useDivideBox.setSelected(false);
					relationRArea.setEnabled(true);
				} else {
					useDivideBox.setEnabled(false);
					useDivideBox.setSelected(false);
					relationRArea.setEnabled(true);
				}
			} else if (command.equals("useDivide")) {
				if (useDivideBox.isEnabled()) {
					if (useDivideBox.isSelected()) relationRArea.setEnabled(false);
					else relationRArea.setEnabled(true);
				}
			} else if (command.equals("problem6_38")) {
				feedbackAnimator.clearContent();
				String setAString = "{1, 2, 3, 4, 6, 12}";
				String relationRString = "{<1, 1>, <1, 2>, <1, 3>, <1, 4>, <1, 6>, <1, 12>, <2, 2>, <2, 4>, <2, 6>, <2, 12>, <3, 3>, <3, 6>, <3, 12>, <4, 4>, <4, 12>, <6, 6>, <6, 12>, <12, 12>}";
				setElementTypeInt(true);
				useDivideBox.setEnabled(true);
				useDivideBox.setSelected(false);
				relationRArea.setEnabled(true);
				setInputs(setAString, "", relationRString);
				minimumBox.setSelected(true);
				maximumBox.setSelected(true);
				leastBox.setSelected(true);
				greatestBox.setSelected(true);
				lowerBox.setSelected(false);
				upperBox.setSelected(false);
				lubBox.setSelected(false);
				glbBox.setSelected(false);
				feedbackAnimator.update();
			} else if (command.equals("problem6_40_1")) {
				feedbackAnimator.clearContent();
				String setAString = "{1, 2, 3, 4}";
				String relationRString = "{<1, 1>, <2, 2>, <3, 3>, <4, 4>}";
				setElementTypeInt(true);
				setInputs(setAString, "", relationRString);
				useDivideBox.setEnabled(true);
				useDivideBox.setSelected(false);
				relationRArea.setEnabled(true);
				minimumBox.setSelected(true);
				maximumBox.setSelected(true);
				leastBox.setSelected(true);
				greatestBox.setSelected(true);
				lowerBox.setSelected(false);
				upperBox.setSelected(false);
				lubBox.setSelected(false);
				glbBox.setSelected(false);
				feedbackAnimator.update();
			} else if (command.equals("problem6_40_2")) {
				feedbackAnimator.clearContent();
				String setAString = "{1, 2, 3, 4}";
				String relationRString = "{<1, 2>, <1, 3>, <2, 4>, <1, 4>, <1, 1>, <2, 2>, <3, 3>, <4, 4>}";
				setElementTypeInt(true);
				setInputs(setAString, "", relationRString);
				useDivideBox.setEnabled(true);
				useDivideBox.setSelected(false);
				relationRArea.setEnabled(true);
				minimumBox.setSelected(true);
				maximumBox.setSelected(true);
				leastBox.setSelected(true);
				greatestBox.setSelected(true);
				lowerBox.setSelected(false);
				upperBox.setSelected(false);
				lubBox.setSelected(false);
				glbBox.setSelected(false);
				feedbackAnimator.update();
			} else if (command.equals("problem6_40_3")) {
				feedbackAnimator.clearContent();
				String setAString = "{1, 2, 3, 4}";
				String relationRString = "{<1, 2>, <1, 3>, <2, 4>, <3, 4>, <1, 4>, <1, 1>, <2, 2>, <3, 3>, <4, 4>}";
				setElementTypeInt(true);
				setInputs(setAString, "", relationRString);
				useDivideBox.setEnabled(true);
				useDivideBox.setSelected(false);
				relationRArea.setEnabled(true);
				minimumBox.setSelected(true);
				maximumBox.setSelected(true);
				leastBox.setSelected(true);
				greatestBox.setSelected(true);
				lowerBox.setSelected(false);
				upperBox.setSelected(false);
				lubBox.setSelected(false);
				glbBox.setSelected(false);
				feedbackAnimator.update();
			} else if (command.equals("problem6_42_1")) {
				feedbackAnimator.clearContent();
				String setAString = "{1, 2, 3, 4, 6, 9, 12, 18}";
				String setSString = "{4, 6, 9}";
				String relationRString = "{<1, 1>, <1, 2>, <1, 3>, <1, 3>, <1, 4>, <1, 6>, <1, 9>, <1, 12>, <1, 18>, <2, 2>, <2, 4>, <2, 6>, <2, 12>, <2, 18>, <3, 3>, <3, 6>, <3, 9>, <3, 12>, <3, 18>, <4, 4>, <4, 12>, <6, 6>, <6, 12>, <6, 18>, <9, 9>, <9, 18>, <12, 12>, <18, 18>}";
				setElementTypeInt(true);
				setInputs(setAString, setSString, relationRString);
				useDivideBox.setEnabled(true);
				useDivideBox.setSelected(false);
				relationRArea.setEnabled(true);
				minimumBox.setSelected(true);
				maximumBox.setSelected(true);
				leastBox.setSelected(true);
				greatestBox.setSelected(true);
				lowerBox.setSelected(true);
				upperBox.setSelected(true);
				lubBox.setSelected(true);
				glbBox.setSelected(true);
				feedbackAnimator.update();
			} else if (command.equals("problem6_42_2")) {
				feedbackAnimator.clearContent();
				String setAString = "{1, 2, 3, 4, 6, 9, 12, 18}";
				String setSString = "{2, 3, 6}";
				String relationRString = "{<1, 1>, <1, 2>, <1, 3>, <1, 3>, <1, 4>, <1, 6>, <1, 9>, <1, 12>, <1, 18>, <2, 2>, <2, 4>, <2, 6>, <2, 12>, <2, 18>, <3, 3>, <3, 6>, <3, 9>, <3, 12>, <3, 18>, <4, 4>, <4, 12>, <6, 6>, <6, 12>, <6, 18>, <9, 9>, <9, 18>, <12, 12>, <18, 18>}";
				setElementTypeInt(true);
				setInputs(setAString, setSString, relationRString);
				useDivideBox.setEnabled(true);
				useDivideBox.setSelected(false);
				relationRArea.setEnabled(true);
				minimumBox.setSelected(true);
				maximumBox.setSelected(true);
				leastBox.setSelected(true);
				greatestBox.setSelected(true);
				lowerBox.setSelected(true);
				upperBox.setSelected(true);
				lubBox.setSelected(true);
				glbBox.setSelected(true);
				feedbackAnimator.update();
			} else if (command.equals("clear")) {
				textAreaManager.clearContent();
				imagedAreaManager.clearContent();
				imagedAreaManager.revalidate();
			}
		}
		
		public boolean doPartialOrderCalculate() {
			Set setA = getSetA();
			if (setA == null) {
				textAreaManager.appendContent("The string " + setAField.getText() + " does not give a legal set!\n");
				imagedAreaManager.appendPlainStringAsNewLine("字符串 " + setAField.getText() + " 没有给出合法的集合A！");
				feedbackAnimator.appendPlainStringAsNewLine("字符串 " + setAField.getText() + " 没有给出合法的集合A！");
				return false;
			} else feedbackAnimator.appendLaTeXStringAsNewLine("A = " + setA.toLaTeXString());
			textAreaManager.appendContent(counter + ": The set A = " + setA.toString() + "\n");
			imagedAreaManager.appendPlainStringAsNewLine(counter + ": 集合 ");
			imagedAreaManager.appendLaTeXStringToLastLine("A = " + setA.toLaTeXString());

			Set setS = getSetS();
			if (setS != null) {
				feedbackAnimator.appendLaTeXStringAsNewLine("S = " + setS.toLaTeXString());
				textAreaManager.appendContent("\tThe subset S = " + setS.toString() + "\n");
				imagedAreaManager.appendPlainStringAsNewLine("    子集 ");
				imagedAreaManager.appendLaTeXStringToLastLine("S = " + setS.toLaTeXString());
			} 
			
			boolean isIntElement = false;
			if (intTypeButton.isSelected()) isIntElement = true;
			Relation relationR = null;
			String content = null;
			if (isIntElement && useDivideBox.isSelected()) {
				relationR = PartialOrder.createDivisionOrder(setA);
				content = relationR.toLaTeXString(isIntElement);
			} else {
				content = relationRArea.getText();
				relationR = SetrelfunUtil.extractRelation(setA, setA, content, isIntElement);
			}
			boolean isPartialOrder = true;
			if (relationR == null) {
				textAreaManager.appendContent("\n\tThe string " + content + " is not a legal relation for R!\n");
				imagedAreaManager.appendPlainStringAsNewLine("    字符串 " + content + " 没有合法地给出关系 R！");
				imagedAreaManager.appendPlainStringAsNewLine("    错误信息： " + SetrelfunUtil.getExtractErrorMessage());
				feedbackAnimator.appendPlainStringAsNewLine("字符串 " + content + "  不是合法的表示关系的字符串！");
				return false;
			} else {
				textAreaManager.appendContent("\n\tR = " + relationR.toLaTeXString(isIntElement) + "\n");
				imagedAreaManager.appendPlainStringAsNewLine("    关系: ");
				imagedAreaManager.appendLaTeXStringToLastLine("R = " + relationR.toLaTeXString(isIntElement) + " \\subseteq A\\times A");
				if (useMatrixBox.isSelected()) {
					Matrix matrix = relationR.getMatrix();
					textAreaManager.appendContent("\t\t" + "M_R = " + matrix.toLaTeXString() + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    关系矩阵: ");
					imagedAreaManager.appendLaTeXStringToLastLine("M_R = " + matrix.toLaTeXString());
				}
				if (useGraphBox.isSelected()) {
					String dotFileName = Configuration.dataFilePath + "RelationR.dot";
					String pngFileName = Configuration.dataFilePath + "RelationR.png";
					try {
						PrintWriter writer = new PrintWriter(dotFileName);
						AbstractGraph graph = relationR.getRelationGraph(isIntElement);
						graph.simplyWriteToDotFile(writer);
						boolean success = MainGUIManager.generatePNGFile(dotFileName, pngFileName, false);
						if (success) {
							textAreaManager.appendContent("\t\tHere gives the relation graph of the relation R!\n");
							imagedAreaManager.appendPlainStringAsNewLine("    关系图: ");
							imagedAreaManager.appendImageFileToLastLine(pngFileName);
						} else {
							textAreaManager.appendContent("\t\tCan not generate the relation graph of the relation R!\n");
							textAreaManager.appendContent(MainGUIManager.errorMessage);
							imagedAreaManager.appendPlainStringAsNewLine("    关系图: 无法生成！");
						}
					} catch (Exception e) {
						textAreaManager.appendContent("\t\tCan not generate the relation graph of the relation R!\n");
						textAreaManager.appendContent("Exception message: " + e.getMessage());
						imagedAreaManager.appendPlainStringAsNewLine("    关系图: 无法生成！");
					}
				}
				feedbackAnimator.appendLaTeXStringAsNewLine("R = " + relationR.toLaTeXString(isIntElement));
				
				String identityLaTeXString = Relation.getIdentity(setA).toLaTeXString();
				boolean hasProperty = relationR.isReflexive();
				if (hasProperty) {
					textAreaManager.appendContent("\tThe relation is reflexive, R\\subseteq \\Delta_A = " + identityLaTeXString + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    关系R是自反关系，");
					imagedAreaManager.appendLaTeXStringToLastLine("R\\subseteq \\Delta_A = " + identityLaTeXString);
				} else {
					OrderedPair exampleOne = Relation.getExampleOne();
					textAreaManager.appendContent("\tThe relation is NOT reflexive, " + exampleOne.toLaTeXString() + "\\not\\in R\n");
					imagedAreaManager.appendPlainStringAsNewLine("    关系R不是自反关系，");
					imagedAreaManager.appendLaTeXStringToLastLine(exampleOne.toLaTeXString() + "\\not\\in R");
					isPartialOrder = false;
				}
				hasProperty = relationR.isAntisymmetric();
				if (hasProperty) {
					Relation Rinv = relationR.inverse();
					Relation result = relationR.intersection(Rinv);
					textAreaManager.appendContent("\tThe relation is anti-symmetric, R \\cap R^{-1} = " + result.toLaTeXString() + "\\subseteq\\Delta_A\n");
					imagedAreaManager.appendPlainStringAsNewLine("    关系R是反对称关系，");
					imagedAreaManager.appendLaTeXStringToLastLine("R \\cap R^{-1} = " + result.toLaTeXString() + "\\subseteq\\Delta_A");
				} else {
					OrderedPair exampleOne = Relation.getExampleOne();
					OrderedPair exampleTwo = Relation.getExampleTwo();
					textAreaManager.appendContent("\tThe relation is NOT anti-symmetric, " + exampleOne.toLaTeXString() + "\\in R, and " + exampleTwo.toLaTeXString() + "\\in R\n");
					imagedAreaManager.appendPlainStringAsNewLine("    关系R不是反对称关系，");
					imagedAreaManager.appendLaTeXStringToLastLine(exampleOne.toLaTeXString() + "\\in R\\quad\\wedge\\quad " + exampleTwo.toLaTeXString() + "\\in R");
					isPartialOrder = false;
				}
				hasProperty = relationR.isTransitive();
				if (hasProperty) {
					Relation result = relationR.composite(relationR);
					textAreaManager.appendContent("\tThe relation is transitive, R \\circ R = " + result.toLaTeXString() + "\\subseteq R\n");
					imagedAreaManager.appendPlainStringAsNewLine("    关系R是传递关系，");
					imagedAreaManager.appendLaTeXStringToLastLine("R \\circ R = " + result.toLaTeXString() + "\\subseteq R");
				} else {
					OrderedPair exampleOne = Relation.getExampleOne();
					OrderedPair exampleTwo = Relation.getExampleTwo();
					OrderedPair pair = new OrderedPair(exampleOne.getFirst(), exampleTwo.getSecond());
					textAreaManager.appendContent("\tThe relation is NOT transitive, " + exampleOne.toLaTeXString() + "\\in R, and " + exampleTwo.toLaTeXString() + "\\in R, but " + pair.toLaTeXString() + "\\not\\in R\n");
					imagedAreaManager.appendPlainStringAsNewLine("    关系R不是传递关系，");
					imagedAreaManager.appendLaTeXStringToLastLine(exampleOne.toLaTeXString() + "\\in R\\quad\\wedge\\quad " + exampleTwo.toLaTeXString() + "\\in R\\quad\\wedge\\quad " + pair.toLaTeXString() + "\\not\\in R");
					isPartialOrder = false;
				}
				if (isPartialOrder) {
					textAreaManager.appendContent("\tThe relation is a partial order\n");
					imagedAreaManager.appendPlainStringAsNewLine("    关系R是偏序关系！");
				} else {
					textAreaManager.appendContent("\tThe relation is NOT a partial order\n");
					imagedAreaManager.appendPlainStringAsNewLine("    关系R不是偏序关系！");
				}
			}
			if (!isPartialOrder) return true;
			PartialOrder partialOrder = new PartialOrder(relationR);
			if (useHasseBox.isSelected()) {
				String dotFileName = Configuration.dataFilePath + "PartialOrderR.dot";
				String pngFileName = Configuration.dataFilePath + "PartialOrderR.png";
				try {
					PrintWriter writer = new PrintWriter(dotFileName);
					AbstractGraph graph = partialOrder.getHasseDigram(isIntElement);
					graph.simplyWriteToDotFile(writer);
					boolean success = MainGUIManager.generatePNGFile(dotFileName, pngFileName, true);
					if (success) {
						textAreaManager.appendContent("\t\tHere gives the Hasse diagram of the partial order R!\n");
						imagedAreaManager.appendPlainStringAsNewLine("    哈斯图: ");
						imagedAreaManager.appendImageFileToLastLine(pngFileName);
					} else {
						textAreaManager.appendContent("\t\tCan not generate the Hasse diagram of the partial order R!\n");
						textAreaManager.appendContent(MainGUIManager.errorMessage);
						imagedAreaManager.appendPlainStringAsNewLine("    哈斯图: 无法生成！");
					}
				} catch (Exception e) {
					textAreaManager.appendContent("\t\tCan not generate the Hasse diagram of the partial order R!\n");
					textAreaManager.appendContent("Exception message: " + e.getMessage());
					imagedAreaManager.appendPlainStringAsNewLine("    哈斯图: 无法生成！");
				}
			}
			
			if (minimumBox.isSelected()) {
				char[] minElements = new char[setA.length()];
				int number = partialOrder.getMinimalElement(setA, minElements);
				String message = Set.getElementsLabel(minElements, isIntElement, number);
				textAreaManager.appendContent("\tThe minimum elements includes: " + message + "\n");
				imagedAreaManager.appendPlainStringAsNewLine("    极小元: ");
				imagedAreaManager.appendLaTeXStringToLastLine(message);
			}
			if (maximumBox.isSelected()) {
				char[] maxElements = new char[setA.length()];
				int number = partialOrder.getMaximalElement(setA, maxElements);
				String message = Set.getElementsLabel(maxElements, isIntElement, number);
				textAreaManager.appendContent("\tThe maximum elements includes: " + message + "\n");
				imagedAreaManager.appendPlainStringAsNewLine("    极大元: ");
				imagedAreaManager.appendLaTeXStringToLastLine(message);
			}
			if (leastBox.isSelected()) {
				if (partialOrder.hasLeastElement(setA)) {
					char element = partialOrder.getLeastElement(setA);
					String message = Set.getElementLabel(element, isIntElement);
					textAreaManager.appendContent("\tThe least element is: " + message + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    最小元: ");
					imagedAreaManager.appendLaTeXStringToLastLine(message);
				} else {
					textAreaManager.appendContent("\tThere is no least element\n");
					imagedAreaManager.appendPlainStringAsNewLine("    最小元: 没有！");
				}
			}
			if (greatestBox.isSelected()) {
				if (partialOrder.hasGreatestElement(setA)) {
					char element = partialOrder.getGreatestElement(setA);
					String message = Set.getElementLabel(element, isIntElement);
					textAreaManager.appendContent("\tThe greatest element is: " + message + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    最大元: ");
					imagedAreaManager.appendLaTeXStringToLastLine(message);
				} else {
					textAreaManager.appendContent("\tThere is no greatest element\n");
					imagedAreaManager.appendPlainStringAsNewLine("    最大元: 没有！");
				}
			}
			
			if (setS == null) return true;
			if (!setA.isSubset(setS)) return true;
			
			if (lowerBox.isSelected()) {
				char[] lowerBounds = new char[setA.length()];
				int number = partialOrder.getLowerBound(setS, lowerBounds);
				if (number > 0) {
					String message = Set.getElementsLabel(lowerBounds, isIntElement, number);
					textAreaManager.appendContent("\tThe lower bounds of the subset S includes: " + message + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    子集S的下界: ");
					imagedAreaManager.appendLaTeXStringToLastLine(message);
				} else {
					textAreaManager.appendContent("\tThere is no lower bound for the subset S!\n");
					imagedAreaManager.appendPlainStringAsNewLine("    子集S的下界: 没有！");
				}
			}
			if (upperBox.isSelected()) {
				char[] upperBounds = new char[setA.length()];
				int number = partialOrder.getUpperBound(setS, upperBounds);
				if (number > 0) {
					String message = Set.getElementsLabel(upperBounds, isIntElement, number);
					textAreaManager.appendContent("\tThe upper bounds of the subset S includes: " + message + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    子集S的上界: ");
					imagedAreaManager.appendLaTeXStringToLastLine(message);
				} else {
					textAreaManager.appendContent("\tThere is no upper bound for the subset S!\n");
					imagedAreaManager.appendPlainStringAsNewLine("    子集S的上界: 没有！");
				}
			}
			if (glbBox.isSelected()) {
				if (partialOrder.hasGreatestLowerBound(setS)) {
					char element = partialOrder.getGreatestLowerBound(setS);
					String message = Set.getElementLabel(element, isIntElement);
					textAreaManager.appendContent("\tThe greatest lower bound of the subset S is: " + message + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    子集S的下确界: ");
					imagedAreaManager.appendLaTeXStringToLastLine(message);
				} else {
					textAreaManager.appendContent("\tThere is no greatest lower for the subset S\n");
					imagedAreaManager.appendPlainStringAsNewLine("    子集S的下确界: 没有！");
				}
			}
			if (lubBox.isSelected()) {
				if (partialOrder.hasLeastUpperBound(setS)) {
					char element = partialOrder.getLeastUpperBound(setS);
					String message = Set.getElementLabel(element, isIntElement);
					textAreaManager.appendContent("\tThe least upper bound of the subset S is: " + message + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    子集S的上确界: ");
					imagedAreaManager.appendLaTeXStringToLastLine(message);
				} else {
					textAreaManager.appendContent("\tThere is no upper bound for the subset S\n");
					imagedAreaManager.appendPlainStringAsNewLine("    子集S的上确界: 没有！");
				}
			}
			return true;
		}
		
		public boolean generateInputs() {
			if (useDivideBox.isSelected()) {
				final int MAX_NUMBER_SEED = 11;
				final int MAX_VALUE_SEED = 31;
				int elementNumber = (int)(Math.random()*MAX_NUMBER_SEED)+1;
				int[] elements = new int[elementNumber];
				for (int i = 0; i < elements.length; i++) {
					elements[i] = (int)(Math.random()*MAX_VALUE_SEED) + 1;
				}
				Relation relationR = PartialOrder.createDivisionOrder(elements);
				Set setA = relationR.getFromSet();
				setAField.setText(setA.toString());
				intTypeButton.setSelected(true);
				charTypeButton.setSelected(false);
				relationRArea.setText(relationR.toString());
			} else {
				Set setA = getSetA();
				if (setA == null) {
					char[] elements = {'0', '1', '2', '3', '4', '5', '6'};
					setA = new Set(elements);
					setAField.setText(setA.toString());
					intTypeButton.setSelected(false);
					charTypeButton.setSelected(true);
				}
				Relation relationR = Relation.getIdentity(setA);
				double seeds = Math.random();
				if (seeds <= 0.5) relationR = PartialOrder.randomGeneratePartialOrder(setA);
				else relationR = Relation.randomGenerate(setA, setA, 10);
				relationRArea.setText(relationR.toString());
			}
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

			Set setS = getSetS();
			if (setS == null) {
				feedbackAnimator.appendPlainStringAsNewLine("字符串 " + setSField.getText() + " 没有给出合法的集合S！");
				return false;
			} else {
				feedbackAnimator.appendLaTeXStringAsNewLine("S = " + setS.toLaTeXString());
				if (!setA.isSubset(setS)) {
					feedbackAnimator.appendPlainStringAsNewLine("集合S不是集合A的子集！ ");
				}
			}

			Relation relationR = null;
			String content = null;
			if (isIntElement && useDivideBox.isSelected()) {
				relationR = PartialOrder.createDivisionOrder(setA);
				content = relationR.toLaTeXString(isIntElement);
			} else {
				content = relationRArea.getText();
				relationR = SetrelfunUtil.extractRelation(setA, setA, content, isIntElement);
			}
			if (relationR == null) {
				feedbackAnimator.appendPlainStringAsNewLine("字符串 " + content + "  不是合法的表示关系的字符串！");
				return false;
			} else {
				feedbackAnimator.appendLaTeXStringAsNewLine("R = " + relationR.toLaTeXString(isIntElement) + " \\subseteq A\\times A");
			}
			return true;
		}
	}
}
