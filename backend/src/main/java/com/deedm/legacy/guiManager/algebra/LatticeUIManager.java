/**
 * 
 */
package com.deedm.legacy.guiManager.algebra;

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

import com.deedm.legacy.algebra.BinaryOperator;
import com.deedm.legacy.algebra.Lattice;
import com.deedm.legacy.graph.AbstractGraph;
import com.deedm.legacy.graphicAnimation.AnimationArea;
import com.deedm.legacy.guiManager.ImagedTextAreaAnimator;
import com.deedm.legacy.guiManager.ImagedTextAreaManager;
import com.deedm.legacy.guiManager.MainGUIManager;
import com.deedm.legacy.guiManager.MainSwingFrame;
import com.deedm.legacy.guiManager.PlainTextAreaManager;
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
public class LatticeUIManager extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final int width = Math.max(1* MainSwingFrame.screenWidth/2, 1280)*(MainSwingFrame.screenResolution/96)*(MainSwingFrame.screenResolution/96);
	private static final int height = Math.max(2*MainSwingFrame.screenHeight/5, 576)*(MainSwingFrame.screenResolution/96)*(MainSwingFrame.screenResolution/96);
	private ImagedTextAreaManager imagedAreaManager = null;
	private PlainTextAreaManager textAreaManager = null;
	private ImagedTextAreaAnimator feedbackAnimator = null;

	private JDialog dialog = null;
	private JButton defaultButton = null;
	private JTextField setAField = null;
	private JTextArea relationRArea = null;
	private JCheckBox distributeBox = null;
	private JCheckBox boundBox = null;
	private JCheckBox complementBox = null;
	private JCheckBox boolBox = null;
	private JCheckBox useHasseBox = null;
	private JRadioButton charTypeButton = null;
	private JRadioButton intTypeButton = null;

	private boolean ok = false;
	private int counter = 0;

	public LatticeUIManager(PlainTextAreaManager plainManager, ImagedTextAreaManager imageManager) {
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
	
	public void setUseHasseDiagramAsInput(boolean use) {
		useHasseBox.setSelected(use);
	}
	
	public boolean setInputs(String setAString, String relationRString) {
		boolean isIntElement = false;
		if (intTypeButton.isSelected()) isIntElement = true;
		
		setAField.setText(setAString);
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
		fromSet = new Set(elements);
		content = relationRArea.getText();
		Relation relation = SetrelfunUtil.extractRelation(fromSet, fromSet, content, isIntElement);
		if (relation == null) {
			feedbackAnimator.appendPlainStringAsNewLine("字符串 " + content + "  不是合法的表示关系的字符串！");
			feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + SetrelfunUtil.getExtractErrorMessage());
			return null;
		} else {
			feedbackAnimator.appendLaTeXStringAsNewLine("R = " + relation.toLaTeXString(isIntElement));
		}
		return relation;
	}
	

	public void setAllProperties() {
		distributeBox.setSelected(true);
		boundBox.setSelected(true);
		boolBox.setSelected(true);
		complementBox.setSelected(true);
	}
	
	public Set getSetA() {
		boolean isIntElement = false;
		if (intTypeButton.isSelected()) isIntElement = true;
		String content = setAField.getText();
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
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 15));
		// 创建一个标签用于提示输入集合信息
		promptLabel = new JLabel("关系(R)：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+R可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('R');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		relationRArea = new JTextArea(3, inputWidth-10);
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
		useHasseBox = new JCheckBox("是基于哈斯图输入的偏序关系");
		useHasseBox.setSelected(true);
		tempPanel.add(useHasseBox);
		inputPanel.add(tempPanel);

		JPanel displayPanel = new JPanel();
		displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
		// 设置该窗格使用的带标题的边框
		displayPanel.setBorder(BorderFactory.createTitledBorder("你输入的集合和关系如下（基于哈斯图输入关系时会计算输入它的自反和传递闭包作为真正待判断的偏序关系）："));
 		AnimationArea area = new AnimationArea(width, 3*height/5);
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
		JButton actButton = new JButton("性质判断(Y)");
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
		buttonPanelTwo.setLayout(new BoxLayout(buttonPanelTwo, BoxLayout.Y_AXIS));
 		buttonPanelTwo.setBorder(BorderFactory.createTitledBorder("《离散数学基础》教材示例"));
		
 		JPanel linePanel = new JPanel();
		linePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 5));
		JButton exampleButton = new JButton("例子10.44(a)");
		exampleButton.setActionCommand("example10_44_a");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		linePanel.add(exampleButton);
		
		exampleButton = new JButton("例子10.44(b)");
		exampleButton.setActionCommand("example10_44_b");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		linePanel.add(exampleButton);

		exampleButton = new JButton("例子10.44(c)");
		exampleButton.setActionCommand("example10_44_c");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		linePanel.add(exampleButton);

		exampleButton = new JButton("问题10.45(a)");
		exampleButton.setActionCommand("example10_45_a");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		linePanel.add(exampleButton);

		exampleButton = new JButton("问题10.45(b)");
		exampleButton.setActionCommand("example10_45_b");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		linePanel.add(exampleButton);
		buttonPanelTwo.add(linePanel);

 		linePanel = new JPanel();
		linePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 5));
		exampleButton = new JButton("问题10.45(c)");
		exampleButton.setActionCommand("example10_45_c");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		linePanel.add(exampleButton);

		exampleButton = new JButton("问题10.45(d)");
		exampleButton.setActionCommand("example10_45_d");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		linePanel.add(exampleButton);

		exampleButton = new JButton("例子10.47(L1)");
		exampleButton.setActionCommand("example10_47_1");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		linePanel.add(exampleButton);

		exampleButton = new JButton("例子10.47(L2)");
		exampleButton.setActionCommand("example10_47_2");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		linePanel.add(exampleButton);

		exampleButton = new JButton("例子10.47(L3)");
		exampleButton.setActionCommand("example10_47_3");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		linePanel.add(exampleButton);

		exampleButton = new JButton("例子10.47(L4)");
		exampleButton.setActionCommand("example10_47_4");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		linePanel.add(exampleButton);
		buttonPanelTwo.add(linePanel);
		
		// 创建给出变量集信息和范式信息的窗格与相关组件
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 25, 0));
		infoPanel.setBorder(BorderFactory.createTitledBorder("选择要计算的内容："));
		distributeBox = new JCheckBox("是否分配格");
		infoPanel.add(distributeBox);
		boundBox = new JCheckBox("是否有界格");
		infoPanel.add(boundBox);
		complementBox = new JCheckBox("是否有补格");
		infoPanel.add(complementBox);
		boolBox = new JCheckBox("是否布尔代数");
		infoPanel.add(boolBox);
		
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
				doLatticeCalculate();
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
					JOptionPane.showMessageDialog(dialog, "输入的集合与关系都符合要求，可以进行格性质判断！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				}
				feedbackAnimator.update();
			} else if (command.equals("example10_44_a")) {
				feedbackAnimator.clearContent();
				String setAString = "{a, b, c, d, e}";
				String relationRString = "{<a, b>, <a, c>, <a, d>, <b, e>, <c, e>, <d, e>}";
				setElementTypeInt(false);
				setInputs(setAString, relationRString);
				useHasseBox.setSelected(true);
				distributeBox.setSelected(false);
				boundBox.setSelected(false);
				complementBox.setSelected(false);
				boolBox.setSelected(false);
				feedbackAnimator.update();
			} else if (command.equals("example10_44_b")) {
				feedbackAnimator.clearContent();
				String setAString = "{a, b, c, d, e, f}";
				String relationRString = "{<a, b>, <a, c>, <c, d>, <b, d>, <b, e>, <d, f>, <e, f>}";
				setElementTypeInt(false);
				setInputs(setAString, relationRString);
				useHasseBox.setSelected(true);
				distributeBox.setSelected(false);
				boundBox.setSelected(false);
				complementBox.setSelected(false);
				boolBox.setSelected(false);
				feedbackAnimator.update();
			} else if (command.equals("example10_44_c")) {
				feedbackAnimator.clearContent();
				String setAString = "{a, b, c, d, e, f}";
				String relationRString = "{<a, b>, <a, c>, <a, d>, <b, e>, <c, e>, <d, f>, <e, f>}";
				setElementTypeInt(false);
				setInputs(setAString, relationRString);
				useHasseBox.setSelected(true);
				distributeBox.setSelected(false);
				boundBox.setSelected(false);
				complementBox.setSelected(false);
				boolBox.setSelected(false);
				feedbackAnimator.update();
			} else if (command.equals("example10_45_a")) {
				feedbackAnimator.clearContent();
				String setAString = "{a, b, c, d, e, f}";
				String relationRString = "{<a, c>, <b, c>, <c, d>, <d, e>, <d, f>}";
				setElementTypeInt(false);
				setInputs(setAString, relationRString);
				useHasseBox.setSelected(true);
				distributeBox.setSelected(false);
				boundBox.setSelected(false);
				complementBox.setSelected(false);
				boolBox.setSelected(false);
				feedbackAnimator.update();
			} else if (command.equals("example10_45_b")) {
				feedbackAnimator.clearContent();
				String setAString = "{a, b, c, d, e}";
				String relationRString = "{<a, b>, <a, d>, <b, c>, <d, c>, <b, e>, <d, e>}";
				setElementTypeInt(false);
				setInputs(setAString, relationRString);
				useHasseBox.setSelected(true);
				distributeBox.setSelected(false);
				boundBox.setSelected(false);
				complementBox.setSelected(false);
				boolBox.setSelected(false);
				feedbackAnimator.update();
			} else if (command.equals("example10_45_c")) {
				feedbackAnimator.clearContent();
				String setAString = "{a, b, c, d, e, f}";
				String relationRString = "{<a, b>, <a, c>, <b, d>, <b, e>, <c, d>, <c, e>, <d, f>, <e, f>}";
				setElementTypeInt(false);
				setInputs(setAString, relationRString);
				useHasseBox.setSelected(true);
				distributeBox.setSelected(false);
				boundBox.setSelected(false);
				complementBox.setSelected(false);
				boolBox.setSelected(false);
				feedbackAnimator.update();
			} else if (command.equals("example10_45_d")) {
				feedbackAnimator.clearContent();
				String setAString = "{a, b, c, d, e, f, g}";
				String relationRString = "{<a, b>, <a, c>, <b, d>, <c, d>, <g, f>, <f, e>}";
				setElementTypeInt(false);
				setInputs(setAString, relationRString);
				useHasseBox.setSelected(true);
				distributeBox.setSelected(false);
				boundBox.setSelected(false);
				complementBox.setSelected(false);
				boolBox.setSelected(false);
				feedbackAnimator.update();
			} else if (command.equals("example10_47_1")) {
				feedbackAnimator.clearContent();
				String setAString = "{a, b, c}";
				String relationRString = "{<a, b>, <b, c>}";
				setElementTypeInt(false);
				setInputs(setAString, relationRString);
				useHasseBox.setSelected(true);
				distributeBox.setSelected(true);
				boundBox.setSelected(true);
				complementBox.setSelected(true);
				boolBox.setSelected(false);
				feedbackAnimator.update();
			} else if (command.equals("example10_47_2")) {
				feedbackAnimator.clearContent();
				String setAString = "{a, b, c, d}";
				String relationRString = "{<a, b>, <a, c>, <b, d>, <c, d>}";
				setElementTypeInt(false);
				setInputs(setAString, relationRString);
				useHasseBox.setSelected(true);
				distributeBox.setSelected(true);
				boundBox.setSelected(true);
				complementBox.setSelected(true);
				boolBox.setSelected(false);
				feedbackAnimator.update();
			} else if (command.equals("example10_47_3")) {
				feedbackAnimator.clearContent();
				String setAString = "{a, b, c, d, e}";
				String relationRString = "{<a, b>, <a, c>, <a, d>, <b, e>, <c, e>, <d, e>}";
				setElementTypeInt(false);
				setInputs(setAString, relationRString);
				useHasseBox.setSelected(true);
				distributeBox.setSelected(true);
				boundBox.setSelected(true);
				complementBox.setSelected(true);
				boolBox.setSelected(false);
				feedbackAnimator.update();
			} else if (command.equals("example10_47_4")) {
				feedbackAnimator.clearContent();
				String setAString = "{a, b, c, d, e}";
				String relationRString = "{<a, b>, <a, c>, <b, e>, <c, d>, <d, e>}";
				setElementTypeInt(false);
				setInputs(setAString, relationRString);
				useHasseBox.setSelected(true);
				distributeBox.setSelected(true);
				boundBox.setSelected(true);
				complementBox.setSelected(true);
				boolBox.setSelected(false);
				feedbackAnimator.update();
			} else if (command.equals("clear")) {
				textAreaManager.clearContent();
				imagedAreaManager.clearContent();
				imagedAreaManager.revalidate();
			}
		}
		
		public boolean doLatticeCalculate() {
			Set setA = getSetA();
			if (setA == null) {
				textAreaManager.appendContent("The string " + setAField.getText() + " does not give a legal set!\n");
				imagedAreaManager.appendPlainStringAsNewLine("字符串 " + setAField.getText() + " 没有给出合法的集合A！");
				feedbackAnimator.appendPlainStringAsNewLine("字符串 " + setAField.getText() + " 没有给出合法的集合A！");
				return false;
			}  
			feedbackAnimator.appendLaTeXStringAsNewLine("A = " + setA.toLaTeXString());
			textAreaManager.appendContent(counter + ": The set A = " + setA.toString() + "\n");
			imagedAreaManager.appendPlainStringAsNewLine(counter + ": 集合 ");
			imagedAreaManager.appendLaTeXStringToLastLine("A = " + setA.toLaTeXString());

			boolean isIntElement = false;
			if (intTypeButton.isSelected()) isIntElement = true;
			String content = relationRArea.getText();
			Relation relationR = SetrelfunUtil.extractRelation(setA, setA, content, isIntElement);
			boolean isPartialOrder = true;
			if (relationR == null) {
				textAreaManager.appendContent("\tThe string " + content + " is not a legal relation for R!\n");
				imagedAreaManager.appendPlainStringAsNewLine("    字符串 " + content + " 没有合法地给出关系 R！");
				imagedAreaManager.appendPlainStringAsNewLine("    错误信息： " + SetrelfunUtil.getExtractErrorMessage());
				feedbackAnimator.appendPlainStringAsNewLine("字符串 " + content + "  不是合法的表示关系的字符串！");
				return false;
			}

			boolean useHasse = useHasseBox.isSelected();
			textAreaManager.appendContent("\tR = " + relationR.toLaTeXString(isIntElement) + "\n");
			imagedAreaManager.appendPlainStringAsNewLine("    关系: ");
			imagedAreaManager.appendLaTeXStringToLastLine("R = " + relationR.toLaTeXString(isIntElement));
			feedbackAnimator.appendLaTeXStringAsNewLine("R = " + relationR.toLaTeXString(isIntElement));
			if (useHasse) {
				relationR = relationR.transitiveClosureByWarshallAlgorithm();
				relationR = relationR.reflexiveClosure();
				textAreaManager.appendContent("\tIts reflexive and transitive closure tr(R) = " + relationR.toLaTeXString(isIntElement));
				imagedAreaManager.appendPlainStringAsNewLine("    它的自反传递闭包是: ");
				imagedAreaManager.appendLaTeXStringToLastLine("tr(R) = " + relationR.toLaTeXString(isIntElement));
				feedbackAnimator.appendLaTeXStringAsNewLine("tr(R) = " + relationR.toLaTeXString(isIntElement));
			}
			
			boolean hasProperty = true;
			if (!useHasse) {
				hasProperty = relationR.isReflexive();
				if (!hasProperty) {
					OrderedPair exampleOne = Relation.getExampleOne();
					textAreaManager.appendContent("\t\tThe relation is NOT reflexive, " + exampleOne.toLaTeXString() + "\\not\\in R\n");
					imagedAreaManager.appendPlainStringAsNewLine("        关系R不是自反关系，");
					imagedAreaManager.appendLaTeXStringToLastLine(exampleOne.toLaTeXString() + "\\not\\in R");
					isPartialOrder = false;
				}
			}
			hasProperty = relationR.isAntisymmetric();
			if (!hasProperty) {
				OrderedPair exampleOne = Relation.getExampleOne();
				OrderedPair exampleTwo = Relation.getExampleTwo();
				if (useHasse) {
					textAreaManager.appendContent("\t\tThe relation is NOT anti-symmetric, " + exampleOne.toLaTeXString() + "\\in tr(R), and " + exampleTwo.toLaTeXString() + "\\in tr(R)\n");
					imagedAreaManager.appendPlainStringAsNewLine("    关系tr(R)不是反对称关系，");
					imagedAreaManager.appendLaTeXStringToLastLine(exampleOne.toLaTeXString() + "\\in tr(R)\\quad\\wedge\\quad " + exampleTwo.toLaTeXString() + "\\in tr(R)");
				} else {
					textAreaManager.appendContent("\t\tThe relation is NOT anti-symmetric, " + exampleOne.toLaTeXString() + "\\in R, and " + exampleTwo.toLaTeXString() + "\\in R\n");
					imagedAreaManager.appendPlainStringAsNewLine("    关系R不是反对称关系，");
					imagedAreaManager.appendLaTeXStringToLastLine(exampleOne.toLaTeXString() + "\\in R\\quad\\wedge\\quad " + exampleTwo.toLaTeXString() + "\\in R");
				}
				isPartialOrder = false;
			}
			if (!useHasse) {
				hasProperty = relationR.isTransitive();
				if (!hasProperty) {
					OrderedPair exampleOne = Relation.getExampleOne();
					OrderedPair exampleTwo = Relation.getExampleTwo();
					OrderedPair pair = new OrderedPair(exampleOne.getFirst(), exampleTwo.getSecond());
					textAreaManager.appendContent("\t\tThe relation is NOT transitive, " + exampleOne.toLaTeXString() + "\\in R, and " + exampleTwo.toLaTeXString() + "\\in R, but " + pair.toLaTeXString() + "\\not\\in R\n");
					imagedAreaManager.appendPlainStringAsNewLine("        关系R不是传递关系，");
					imagedAreaManager.appendLaTeXStringToLastLine(exampleOne.toLaTeXString() + "\\in R\\quad\\wedge\\quad " + exampleTwo.toLaTeXString() + "\\in R\\quad\\wedge\\quad " + pair.toLaTeXString() + "\\not\\in R");
					isPartialOrder = false;
				}
			}
			if (isPartialOrder) {
				textAreaManager.appendContent("\tThe relation is a partial order\n");
				if (useHasse) imagedAreaManager.appendPlainStringAsNewLine("    关系tr(R)是偏序关系！");
				else imagedAreaManager.appendPlainStringAsNewLine("    关系R是偏序关系！");
			} else {
				textAreaManager.appendContent("\tThe relation is NOT a partial order\n");
				if (useHasse) imagedAreaManager.appendPlainStringAsNewLine("    关系tr(R)不是偏序关系！");
				else imagedAreaManager.appendPlainStringAsNewLine("    关系R不是偏序关系！");
				return true;
			}
			
			PartialOrder partialOrder = new PartialOrder(relationR);
			String dotFileName = Configuration.dataFilePath + "PartialOrderR.dot";
			String pngFileName = Configuration.dataFilePath + "PartialOrderR.png";
			try {
				PrintWriter writer = new PrintWriter(dotFileName);
				AbstractGraph graph = partialOrder.getHasseDigram(isIntElement);
				graph.simplyWriteToDotFile(writer);
				boolean success = MainGUIManager.generatePNGFile(dotFileName, pngFileName, false);
				if (success) {
					textAreaManager.appendContent("\t\tHere gives the Hasse diagram of the partial order R!\n");
					imagedAreaManager.appendPlainStringAsNewLine("        哈斯图: ");
					imagedAreaManager.appendImageFileToLastLine(pngFileName);
				} else {
					textAreaManager.appendContent("\t\tCan not generate the Hasse diagram of the partial order R!\n");
					textAreaManager.appendContent(MainGUIManager.errorMessage);
					imagedAreaManager.appendPlainStringAsNewLine("       哈斯图: 无法生成！");
				}
			} catch (Exception e) {
				textAreaManager.appendContent("\t\tCan not generate the Hasse diagram of the partial order R!\n");
				textAreaManager.appendContent("Exception message: " + e.getMessage());
				imagedAreaManager.appendPlainStringAsNewLine("        哈斯图: 无法生成！");
			}
			
			boolean isLattice = partialOrder.isLattice();
			if (!isLattice) {
				char elementOne = PartialOrder.getElementOne();
				char elementTwo = PartialOrder.getElementTwo();
				int reasonType = PartialOrder.getReasonType();
				String elementOneString = Set.getElementLabel(elementOne, isIntElement);
				String elementTwoString = Set.getElementLabel(elementTwo, isIntElement);
				
				textAreaManager.appendContent("\tThe relation is NOT a lattice: ");
				if (useHasse) imagedAreaManager.appendPlainStringAsNewLine("    关系tr(R)不是格：元素 ");
				else imagedAreaManager.appendPlainStringAsNewLine("    关系R不是格：元素 ");

				textAreaManager.appendContent(elementOneString + " and " + elementTwoString);
				imagedAreaManager.appendLaTeXStringToLastLine(elementOneString);
				imagedAreaManager.appendPlainStringToLastLine(" 和 ");
				imagedAreaManager.appendLaTeXStringToLastLine(elementTwoString);
				if (reasonType == PartialOrder.WITHOUT_GLB) {
					textAreaManager.appendContent(" have not greatest lower bound!\n");
					imagedAreaManager.appendPlainStringToLastLine(" 没有下确界！ ");
				} else {
					textAreaManager.appendContent(" have not least upper bound!\n");
					imagedAreaManager.appendPlainStringToLastLine(" 没有上确界！ ");
				}
				return true;
			}
			
			Lattice lattice = Lattice.createLatticeFromPoset(partialOrder);
			BinaryOperator<Character> supOperator = lattice.getSupOperator();
			String supOptableString = supOperator.toLaTeXString();
			BinaryOperator<Character> subOperator = lattice.getSubOperator();
			String subOptableString = subOperator.toLaTeXString();
			textAreaManager.appendContent("\tThis parital order is a lattice, the operation tables are as follows:\n");
			textAreaManager.appendContent("\t\t" + supOptableString);
			textAreaManager.appendContent("\t\t" + subOptableString);
			imagedAreaManager.appendPlainStringAsNewLine("    这个偏序关系是格， 求上确界运算 ");
			imagedAreaManager.appendLaTeXStringToLastLine(supOperator.getName());
			imagedAreaManager.appendPlainStringToLastLine(" 和求下确界运算 ");
			imagedAreaManager.appendLaTeXStringToLastLine(subOperator.getName());
			imagedAreaManager.appendPlainStringToLastLine(" 的运算表如下：");
			imagedAreaManager.appendPlainStringAsNewLine("        ");
			imagedAreaManager.appendLaTeXStringToLastLine(supOptableString);
			imagedAreaManager.appendPlainStringToLastLine("    ");
			imagedAreaManager.appendLaTeXStringToLastLine(subOptableString);
			
			if (distributeBox.isSelected()) {
				boolean isDistributive = lattice.isDistributive();
				if (isDistributive) {
					textAreaManager.appendContent("\tThis lattice is a distributive lattice!\n");
					imagedAreaManager.appendPlainStringAsNewLine("    这个格是分配格！ ");
				} else {
					String reasonMessage = Lattice.getNonDistributiveReason();
					textAreaManager.appendContent("\tThis lattice is NOT a distributive lattice: " + reasonMessage + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    这个格不是分配格：");
					imagedAreaManager.appendLaTeXStringToLastLine(reasonMessage);
				}
			}
			if (boundBox.isSelected()) {
				boolean isBound = lattice.isBounded();
				if (isBound) {
					char greatest = lattice.getGreatestElement();
					String greatestString = Set.getElementLabel(greatest, isIntElement);
					char least = lattice.getLeastElement();
					String leastString = Set.getElementLabel(least, isIntElement);
					textAreaManager.appendContent("\tThis lattice is a bounded lattice, the greatest element is " + greatestString + ", and the least element is " + leastString + "!\n");
					imagedAreaManager.appendPlainStringAsNewLine("    这个格是有界格，最大元是 ");
					imagedAreaManager.appendLaTeXStringToLastLine(greatestString);
					imagedAreaManager.appendPlainStringToLastLine(" ，最小元是 ");
					imagedAreaManager.appendLaTeXStringToLastLine(leastString);
				} else {
					int reasonType = Lattice.getReasonType();
					if (reasonType == Lattice.WITHOUT_GREATEST) {
						textAreaManager.appendContent("\tThis lattice is NOT a bounded lattice, it has no greatest element!\n");
						imagedAreaManager.appendPlainStringAsNewLine("    这个格不是有界格，它没有最大元！");
					} else {
						textAreaManager.appendContent("\tThis lattice is NOT a bounded lattice, it has no least element!\n");
						imagedAreaManager.appendPlainStringAsNewLine("    这个格不是有界格，它没有最小元！");
					}
				}
			}
			if (complementBox.isSelected()) {
				boolean isBound = lattice.isBounded();
				if (isBound) {
					boolean hasComplement = lattice.isComplemented();
					if (hasComplement) {
						char greatest = lattice.getGreatestElement();
						String greatestString = Set.getElementLabel(greatest, isIntElement);
						char least = lattice.getLeastElement();
						String leastString = Set.getElementLabel(least, isIntElement);
						textAreaManager.appendContent("\tThis lattice is a complement lattice, the greatest element is " + greatestString + ", and the least element is " + leastString + "!\n");
						imagedAreaManager.appendPlainStringAsNewLine("    这个格是有补格，最大元是 ");
						imagedAreaManager.appendLaTeXStringToLastLine(greatestString);
						imagedAreaManager.appendPlainStringToLastLine(" ，最小元是 ");
						imagedAreaManager.appendLaTeXStringToLastLine(leastString);
						
						char[] complements = new char[setA.length()];
						for (int i = 0; i < setA.length(); i++) {
							char element = setA.get(i);
							String elementString = Set.getElementLabel(element, isIntElement);
							int complementNumber = lattice.getComplement(element, complements);
							String complementString = Set.getElementsLabel(complements, isIntElement, complementNumber);
							textAreaManager.appendContent("\t\tThe complements of the element " + elementString + " have " + complementString);
							imagedAreaManager.appendPlainStringAsNewLine("        元素 ");
							imagedAreaManager.appendLaTeXStringToLastLine(elementString);
							imagedAreaManager.appendPlainStringToLastLine(" 的补元是：");
							imagedAreaManager.appendLaTeXStringToLastLine(complementString);
						}
					} else {
						char elementOne = Lattice.getElementOne();
						String elementOneString = Set.getElementLabel(elementOne, isIntElement);
						textAreaManager.appendContent("\tThis lattice is NOT a compelment lattice, the element " + elementOneString + " has no complement element!\n");
						imagedAreaManager.appendPlainStringAsNewLine("    这个格不是有补格，元素 ");
						imagedAreaManager.appendLaTeXStringToLastLine(elementOneString);
						imagedAreaManager.appendPlainStringToLastLine(" 没有补元！");
					}
				} else {
					int reasonType = Lattice.getReasonType();
					if (reasonType == Lattice.WITHOUT_GREATEST) {
						textAreaManager.appendContent("\tThis lattice is NOT a compelment lattice, it has no greatest element!\n");
						imagedAreaManager.appendPlainStringAsNewLine("    这个格不是有补格，它不是有界格，没有最大元！");
					} else {
						textAreaManager.appendContent("\tThis lattice is NOT a complement lattice, it has no least element!\n");
						imagedAreaManager.appendPlainStringAsNewLine("    这个格不是有补格，它不是有界格，没有最小元！");
					}
				}
			}
			if (boolBox.isSelected()) {
				boolean isDistributive = lattice.isDistributive();
				if (isDistributive) {
					boolean isBound = lattice.isBounded();
					if (isBound) {
						boolean hasComplement = lattice.isComplemented();
						if (hasComplement) {
							if (boundBox.isSelected()) {
								textAreaManager.appendContent("\tThis lattice is a boolean algebra!\n");
								imagedAreaManager.appendPlainStringAsNewLine("    这个格是布尔代数。");
							} else {
								char greatest = lattice.getGreatestElement();
								String greatestString = Set.getElementLabel(greatest, isIntElement);
								char least = lattice.getLeastElement();
								String leastString = Set.getElementLabel(least, isIntElement);
								textAreaManager.appendContent("\tThis lattice is a boolean algebra, the greatest element is " + greatestString + ", and the least element is " + leastString + "!\n");
								imagedAreaManager.appendPlainStringAsNewLine("    这个格是布尔代数，最大元是 ");
								imagedAreaManager.appendLaTeXStringToLastLine(greatestString);
								imagedAreaManager.appendPlainStringToLastLine(" ，最小元是 ");
								imagedAreaManager.appendLaTeXStringToLastLine(leastString);
							}
							if (!complementBox.isSelected()) {
								char[] complements = new char[setA.length()];
								for (int i = 0; i < setA.length(); i++) {
									char element = setA.get(i);
									String elementString = Set.getElementLabel(element, isIntElement);
									int complementNumber = lattice.getComplement(element, complements);
									String complementString = Set.getElementsLabel(complements, isIntElement, complementNumber);
									textAreaManager.appendContent("\t\tThe complements of the element " + elementString + " have " + complementString + "\n");
									imagedAreaManager.appendPlainStringAsNewLine("        元素 ");
									imagedAreaManager.appendLaTeXStringToLastLine(elementString);
									imagedAreaManager.appendPlainStringToLastLine("的补元是：");
									imagedAreaManager.appendLaTeXStringToLastLine(complementString);
								}
							}
						} else {
							char elementOne = Lattice.getElementOne();
							String elementOneString = Set.getElementLabel(elementOne, isIntElement);
							textAreaManager.appendContent("\tThis lattice is NOT a boolean algebra, it is NOT a compelment lattice, the element " + elementOneString + " has no complement element!\n");
							imagedAreaManager.appendPlainStringAsNewLine("    这个格不是布尔代数，它不是有补格，元素 ");
							imagedAreaManager.appendLaTeXStringToLastLine(elementOneString);
							imagedAreaManager.appendPlainStringToLastLine("没有补元！");
						}
					} else {
						int reasonType = Lattice.getReasonType();
						if (reasonType == Lattice.WITHOUT_GREATEST) {
							textAreaManager.appendContent("\tThis lattice is NOT a boolean algebra, it is not a bound lattice, it has no greatest element!\n");
							imagedAreaManager.appendPlainStringAsNewLine("    这个格不是布尔代数，它不是有界格，没有最大元！");
						} else {
							textAreaManager.appendContent("\tThis lattice is NOT a boolean algebra, it is not a bound lattice, it has no least element!\n");
							imagedAreaManager.appendPlainStringAsNewLine("    这个格不是布尔代数，它不是有界格，没有最小元！");
						}
					}
				} else {
					String reasonMessage = Lattice.getNonDistributiveReason();
					textAreaManager.appendContent("\tThis lattice is NOT a boolean algebra, it is NOT a distributive lattice: " + reasonMessage + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    这个格不是布尔代数，它不是分配格：");
					imagedAreaManager.appendLaTeXStringToLastLine(reasonMessage);
				}
			}
			
			return true;
		}
		
		public boolean generateInputs() {
			Set setA = getSetA();
			if (setA == null) {
				char[] elements = {'0', '1', '2', '3', '4', '5', '6'};
				setA = new Set(elements);
				setAField.setText(setA.toString());
				intTypeButton.setSelected(false);
				charTypeButton.setSelected(true);
			}
			Relation relationR = Relation.getIdentity(setA);
			relationR = PartialOrder.randomGeneratePartialOrder(setA);
			relationRArea.setText(relationR.toString());
			useHasseBox.setSelected(false);
			
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

			String content = relationRArea.getText();
			Relation relationR = SetrelfunUtil.extractRelation(setA, setA, content, isIntElement);
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
