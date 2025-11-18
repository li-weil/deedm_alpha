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
import com.deedm.legacy.graphicAnimation.AnimationArea;
import com.deedm.legacy.guiManager.ImagedTextAreaAnimator;
import com.deedm.legacy.guiManager.ImagedTextAreaManager;
import com.deedm.legacy.guiManager.MainSwingFrame;
import com.deedm.legacy.guiManager.PlainTextAreaManager;
import com.deedm.legacy.setrelfun.Set;
import com.deedm.legacy.setrelfun.SetrelfunUtil;

/**
 * @author user
 *
 */
public class BinaryOperatorUIManager extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final int width = Math.max(2* MainSwingFrame.screenWidth/5, 1024)*(MainSwingFrame.screenResolution/96);
	private static final int height = Math.max(MainSwingFrame.screenHeight/2, 720)*(MainSwingFrame.screenResolution/96);
	private ImagedTextAreaManager imagedAreaManager = null;
	private PlainTextAreaManager textAreaManager = null;
	private ImagedTextAreaAnimator feedbackAnimator = null;

	private JDialog dialog = null;
	private JButton defaultButton = null;
	private JTextField setAField = null;
	private JTextArea operator1Area = null;
	private JTextArea operator2Area = null;
	private JCheckBox commutativeBox = null;
	private JCheckBox associativeBox = null;
	private JCheckBox idempotentBox = null;
	private JCheckBox cancellationBox = null;
	private JCheckBox distributiveBox = null;
	private JCheckBox absorptionBox = null;
	private JCheckBox identityBox = null;
	private JCheckBox zeroBox = null;
	private JCheckBox inverseBox = null;
	private JRadioButton charTypeButton = null;
	private JRadioButton intTypeButton = null;

	private boolean ok = false;
	private int counter = 0;

	public BinaryOperatorUIManager(PlainTextAreaManager plainManager, ImagedTextAreaManager imageManager) {
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
		operator1Area.setText("");
		operator2Area.setText("");
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
	
	public boolean setInputs(String setAString, String operator1String, String operator2String) {
		boolean isIntElement = false;
		if (intTypeButton.isSelected()) isIntElement = true;
		
		setAField.setText(setAString);
		operator1Area.setText(operator1String);
		operator2Area.setText(operator2String);
		boolean success = true;
		Set setA = null;
		char[] setAElements = SetrelfunUtil.extractSetElements(setAString, isIntElement);
		if (setAElements != null) {
			setA = new Set(setAElements);
			feedbackAnimator.appendLaTeXStringAsNewLine("A = " + setA.toLaTeXString());
		} else {
			feedbackAnimator.appendPlainStringAsNewLine("给出集合A的字符串 " + setAString + " 不是合法的表示集合的字符串！");
			feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + SetrelfunUtil.getExtractErrorMessage());
			return false;
		}
		Character[] baseSet = new Character[setA.length()];
		for (int i = 0; i < setAElements.length; i++) baseSet[i] = new Character(setAElements[i]);
		BinaryOperator<Character> operator1 = BinaryOperator.extractBinaryOperator(baseSet, operator1String, isIntElement);
		if (operator1 == null) {
			feedbackAnimator.appendPlainStringAsNewLine("字符串 " + operator1String + "  不是合法的表示运算表的字符串！");
			feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + BinaryOperator.getReasonMessage());
			return false;
		} else {
			operator1.setName("\\circ ");
			feedbackAnimator.appendLaTeXStringAsNewLine(operator1.toLaTeXString());
		}
		if (!operator2String.contentEquals("")) {
			BinaryOperator<Character> operator2 = BinaryOperator.extractBinaryOperator(baseSet, operator2String, isIntElement);
			if (operator2 == null) {
				feedbackAnimator.appendPlainStringAsNewLine("字符串 " + operator2String + "  不是合法的表示运算表的字符串！");
				feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + BinaryOperator.getReasonMessage());
				return false;
			} else {
				operator2.setName(" * ");
				feedbackAnimator.appendLaTeXStringToLastLine("\\quad\\quad " + operator2.toLaTeXString());
			}
		}
		return success;
	}
	
	public void setAllProperties() {
		commutativeBox.setSelected(true);
		associativeBox.setSelected(true);
		idempotentBox.setSelected(true);
		cancellationBox.setSelected(true);
		distributiveBox.setSelected(true);
		absorptionBox.setSelected(true);
		identityBox.setSelected(true);
		zeroBox.setSelected(true);
		inverseBox.setSelected(true);
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
		inputPanel.setLayout(new GridLayout(2, 2, 5, 0));
		// 设置该窗格使用的带标题的边框
 		inputPanel.setBorder(BorderFactory.createTitledBorder("请输入集合和运算表，二元运算表的元素用<first, second, result>的形式给出，first和second是参与运算的元素，result是运算结果："));

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
		promptLabel = new JLabel("二元运算一：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+R可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('R');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		operator1Area = new JTextArea(4, inputWidth-10);
		operator1Area.setLineWrap(true);;
		JScrollPane scroll = new JScrollPane(operator1Area);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(operator1Area);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(scroll);
		inputPanel.add(tempPanel);
		
 		tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 15));
		// 创建一个标签用于提示输入集合信息
		promptLabel = new JLabel("二元运算二：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+R可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('S');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		operator2Area = new JTextArea(4, inputWidth-10);
		operator2Area.setLineWrap(true);;
		scroll = new JScrollPane(operator2Area);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(operator2Area);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(scroll);
		inputPanel.add(tempPanel);

		JPanel displayPanel = new JPanel();
		displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
		// 设置该窗格使用的带标题的边框
		displayPanel.setBorder(BorderFactory.createTitledBorder("你输入的集合和运算如下："));
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
		JButton actButton = new JButton("运算性质判断(Y)");
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
		
		JButton exampleButton = new JButton("例子10.5");
		exampleButton.setActionCommand("example10_5");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("问题10.9");
		exampleButton.setActionCommand("problem10_9");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("问题10.11");
		exampleButton.setActionCommand("problem10_11");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		// 创建给出变量集信息和范式信息的窗格与相关组件
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 0));
		infoPanel.setBorder(BorderFactory.createTitledBorder("选择要判断的运算性质："));
		commutativeBox = new JCheckBox("交换性");
		infoPanel.add(commutativeBox);
		associativeBox = new JCheckBox("结合性");
		infoPanel.add(associativeBox);
		idempotentBox = new JCheckBox("幂等性");
		infoPanel.add(idempotentBox);
		cancellationBox = new JCheckBox("消去律");
		infoPanel.add(cancellationBox);
		distributiveBox = new JCheckBox("分配律");
		infoPanel.add(distributiveBox);
		absorptionBox = new JCheckBox("吸收律");
		infoPanel.add(absorptionBox);
		identityBox = new JCheckBox("单位元");
		infoPanel.add(identityBox);
		zeroBox = new JCheckBox("零元");
		infoPanel.add(zeroBox);
		inverseBox = new JCheckBox("逆元");
		infoPanel.add(inverseBox);
		
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
				doOperatorProperties();
				feedbackAnimator.update();
				imagedAreaManager.revalidate();
			} else if (command.equals("cancel")) {
				ok = false;
				dialog.setVisible(false);
			} else if (command.equals("generate")) {
				feedbackAnimator.clearContent();
				generateInputs();
				commutativeBox.setSelected(true);
				associativeBox.setSelected(true);
				idempotentBox.setSelected(true);
				cancellationBox.setSelected(true);
				distributiveBox.setSelected(true);
				absorptionBox.setSelected(true);
				identityBox.setSelected(true);
				zeroBox.setSelected(true);
				inverseBox.setSelected(true);
				feedbackAnimator.update();
			} else if (command.equals("remove")) {
				clearInput();
				commutativeBox.setSelected(false);
				associativeBox.setSelected(false);
				idempotentBox.setSelected(false);
				cancellationBox.setSelected(false);
				distributiveBox.setSelected(false);
				absorptionBox.setSelected(false);
				identityBox.setSelected(false);
				zeroBox.setSelected(false);
				inverseBox.setSelected(false);
			} else if (command.equals("check")) {
				feedbackAnimator.clearContent();
				boolean success = checkInputs();
				if (success != true) {
					JOptionPane.showMessageDialog(dialog, "输入的集合与运算表不完全符合要求！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(dialog, "输入的集合与运算表都符合要求，可以进行运算性质判断！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				}
				feedbackAnimator.update();
			} else if (command.equals("example10_5")) {
				feedbackAnimator.clearContent();
				String setAString = "{e, a, b, c}";
				String operator1String = "{<e, e, e>, <e, a, a>, <e, b, b>, <e, c, c>, <a, e, a>, <a, a, e>, <a, b, c>, <a, c, b>, <b, e, b>, <b, a, c>, <b, b, e>, <b, c, a>, <c, e, c>, <c, a, b>, <c, b, a>, <c, c, e>}";
				setElementTypeInt(false);
				setInputs(setAString, operator1String, "");
				commutativeBox.setSelected(false);
				associativeBox.setSelected(false);
				idempotentBox.setSelected(false);
				cancellationBox.setSelected(false);
				distributiveBox.setSelected(false);
				absorptionBox.setSelected(false);
				identityBox.setSelected(false);
				zeroBox.setSelected(false);
				inverseBox.setSelected(false);
				feedbackAnimator.update();
			} else if (command.equals("problem10_9")) {
				feedbackAnimator.clearContent();
				String setAString = "{e, x, y, z}";
				String operator1String = "{<e, e, e>, <e, x, x>, <e, y, y>, <e, z, z>, <x, e, x>, <x, x, x>, <x, y, e>, <x, z, e>, <y, e, y>, <y, x, e>, <y, y, x>, <y, z, x>, <z, e, z>, <z, x, e>, <z, y, x>, <z, z, x>}";
				setElementTypeInt(false);
				setInputs(setAString, operator1String, "");
				commutativeBox.setSelected(false);
				associativeBox.setSelected(true);
				idempotentBox.setSelected(false);
				cancellationBox.setSelected(false);
				distributiveBox.setSelected(false);
				absorptionBox.setSelected(false);
				identityBox.setSelected(false);
				zeroBox.setSelected(false);
				inverseBox.setSelected(true);
				feedbackAnimator.update();
			} else if (command.equals("problem10_11")) {
				feedbackAnimator.clearContent();
				String setAString = "{e, a, b, c}";
				String operator1String = "{<e, e, e>, <e, a, a>, <e, b, b>, <e, c, c>, <a, e, a>, <a, a, e>, <a, b, c>, <a, c, b>, <b, e, b>, <b, a, c>, <b, b, e>, <b, c, a>, <c, e, c>, <c, a, b>, <c, b, a>, <c, c, e>}";
				setElementTypeInt(false);
				setInputs(setAString, operator1String, "");
				commutativeBox.setSelected(true);
				associativeBox.setSelected(true);
				idempotentBox.setSelected(true);
				cancellationBox.setSelected(true);
				distributiveBox.setSelected(false);
				absorptionBox.setSelected(false);
				identityBox.setSelected(true);
				zeroBox.setSelected(true);
				inverseBox.setSelected(true);
				feedbackAnimator.update();
			} else if (command.equals("clear")) {
				textAreaManager.clearContent();
				imagedAreaManager.clearContent();
				imagedAreaManager.revalidate();
			}
		}
		
		public boolean doOperatorProperties() {
			Set setA = getSetA();
			if (setA == null) {
				textAreaManager.appendContent("The string " + setAField.getText() + " does not give a legal set!\n");
				imagedAreaManager.appendPlainStringAsNewLine("字符串 " + setAField.getText() + " 没有给出合法的集合A！");
				feedbackAnimator.appendPlainStringAsNewLine("字符串 " + setAField.getText() + " 没有给出合法的集合A！");
				return false;
			}
			Character[] baseSet = new Character[setA.length()];
			for (int i = 0; i < setA.length(); i++) baseSet[i] = new Character(setA.get(i));
			
			feedbackAnimator.appendLaTeXStringAsNewLine("A = " + setA.toLaTeXString());
			textAreaManager.appendContent(counter + ": The set A = " + setA.toString());
			imagedAreaManager.appendPlainStringAsNewLine(counter + ": 集合 ");
			imagedAreaManager.appendLaTeXStringToLastLine("A = " + setA.toLaTeXString());

			boolean isIntElement = false;
			if (intTypeButton.isSelected()) isIntElement = true;

			String operator1String = operator1Area.getText();
			BinaryOperator<Character> operator1 = BinaryOperator.extractBinaryOperator(baseSet, operator1String, isIntElement);
			if (operator1 == null) {
				feedbackAnimator.appendPlainStringAsNewLine("字符串 " + operator1String + "  不是合法的表示运算表的字符串！");
				feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + BinaryOperator.getReasonMessage());
				return false;
			}
			operator1.setName("\\circ ");
			feedbackAnimator.appendLaTeXStringAsNewLine(operator1.toLaTeXString());
			
			textAreaManager.appendContent(", the binary operator one, denoted as \\circ");
			imagedAreaManager.appendPlainStringToLastLine("，运算一（用 ");
			imagedAreaManager.appendLaTeXStringToLastLine(operator1.getName());
			imagedAreaManager.appendPlainStringToLastLine(" 表示）");
			
			String operator2String = operator2Area.getText();
			BinaryOperator<Character> operator2 = null;
			if (!operator2String.contentEquals("")) {
				operator2 = BinaryOperator.extractBinaryOperator(baseSet, operator2String, isIntElement);
				if (operator2 == null) {
					feedbackAnimator.appendPlainStringAsNewLine("字符串 " + operator2String + "  不是合法的表示运算表的字符串！");
					feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + BinaryOperator.getReasonMessage());
				} else {
					operator2.setName(" * ");
					feedbackAnimator.appendLaTeXStringToLastLine("\\quad\\quad " + operator2.toLaTeXString());
					textAreaManager.appendContent(", the binary operator two, denoted as * ");
					imagedAreaManager.appendPlainStringToLastLine("，运算二（用 ");
					imagedAreaManager.appendLaTeXStringToLastLine(operator2.getName());
					imagedAreaManager.appendPlainStringToLastLine(" 表示）");
				}
			}
			textAreaManager.appendContent(", the operation tables are as follows: \n");
			imagedAreaManager.appendPlainStringToLastLine("，运算表如下：");
			textAreaManager.appendContent("\t" + operator1.toLaTeXString() + "\n");
			imagedAreaManager.appendPlainStringAsNewLine("        ");
			imagedAreaManager.appendLaTeXStringToLastLine(operator1.toLaTeXString());
			if (operator2 != null) {
				textAreaManager.appendContent("\t" + operator2.toLaTeXString() + "\n");
				imagedAreaManager.appendPlainStringToLastLine("        ");
				imagedAreaManager.appendLaTeXStringToLastLine(operator2.toLaTeXString());
			}
			
			textAreaManager.appendContent("\tThe properties of the operator " + operator1.getName() + " are as follows: \n");
			imagedAreaManager.appendPlainStringAsNewLine("    运算 ");
			imagedAreaManager.appendLaTeXStringToLastLine(operator1.getName());
			imagedAreaManager.appendPlainStringToLastLine(" 的性质如下：");
			if (commutativeBox.isSelected()) {
				boolean success = operator1.isCommutative();
				if (success) {
					textAreaManager.appendContent("\t\tThe operator " + operator1.getName() + " is commutative!\n");
					imagedAreaManager.appendPlainStringAsNewLine("        运算 ");
					imagedAreaManager.appendLaTeXStringToLastLine(operator1.getName());
					imagedAreaManager.appendPlainStringToLastLine(" 满足交换律！");
				} else {
					textAreaManager.appendContent("\t\tThe operator " + operator1.getName() + " is Not commutative: " + BinaryOperator.getReasonMessage() + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("        运算 ");
					imagedAreaManager.appendLaTeXStringToLastLine(operator1.getName());
					imagedAreaManager.appendPlainStringToLastLine(" 不满足交换律：");
					imagedAreaManager.appendLaTeXStringToLastLine(BinaryOperator.getReasonMessage());
				}
			}
			
			if (associativeBox.isSelected()) {
				boolean success = operator1.isAssociative();
				if (success) {
					textAreaManager.appendContent("\t\tThe operator " + operator1.getName() + " is assoicative!\n");
					imagedAreaManager.appendPlainStringAsNewLine("        运算 ");
					imagedAreaManager.appendLaTeXStringToLastLine(operator1.getName());
					imagedAreaManager.appendPlainStringToLastLine(" 满足结合律！");
				} else {
					textAreaManager.appendContent("\t\tThe operator " + operator1.getName() + " is NOT assoicative: " + BinaryOperator.getReasonMessage() + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("        运算 ");
					imagedAreaManager.appendLaTeXStringToLastLine(operator1.getName());
					imagedAreaManager.appendPlainStringToLastLine(" 不满足结合律：");
					imagedAreaManager.appendLaTeXStringToLastLine(BinaryOperator.getReasonMessage());
				}
				
			}

			if (idempotentBox.isSelected()) {
				boolean success = operator1.isIdempotent();
				if (success) {
					textAreaManager.appendContent("\t\tThe operator " + operator1.getName() + " is idempotent!\n");
					imagedAreaManager.appendPlainStringAsNewLine("        运算 ");
					imagedAreaManager.appendLaTeXStringToLastLine(operator1.getName());
					imagedAreaManager.appendPlainStringToLastLine(" 满足幂等律！");
				} else {
					textAreaManager.appendContent("\t\tThe operator " + operator1.getName() + " is NOT idempotent: " + BinaryOperator.getReasonMessage() + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("        运算 ");
					imagedAreaManager.appendLaTeXStringToLastLine(operator1.getName());
					imagedAreaManager.appendPlainStringToLastLine(" 不满足幂等律：");
					imagedAreaManager.appendLaTeXStringToLastLine(BinaryOperator.getReasonMessage());
				}
			}

			if (cancellationBox.isSelected()) {
				boolean success = operator1.isCancellative();
				if (success) {
					textAreaManager.appendContent("\t\tThe operator " + operator1.getName() + " is cancellative!\n");
					imagedAreaManager.appendPlainStringAsNewLine("        运算 ");
					imagedAreaManager.appendLaTeXStringToLastLine(operator1.getName());
					imagedAreaManager.appendPlainStringToLastLine(" 满足消去律！");
				} else {
					textAreaManager.appendContent("\t\tThe operator " + operator1.getName() + " is NOT cancellative: " + BinaryOperator.getReasonMessage() + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("        运算 ");
					imagedAreaManager.appendLaTeXStringToLastLine(operator1.getName());
					imagedAreaManager.appendPlainStringToLastLine(" 不满足消去律：");
					imagedAreaManager.appendLaTeXStringToLastLine(BinaryOperator.getReasonMessage());
				}
				
			}

			if (identityBox.isSelected()) {
				boolean success = operator1.hasIdentity();
				if (success) {
					char identity = operator1.getIdentity();
					textAreaManager.appendContent("\t\tThe operator " + operator1.getName() + " has identity: " + Set.getElementLabel(identity, isIntElement) + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("        运算 ");
					imagedAreaManager.appendLaTeXStringToLastLine(operator1.getName());
					imagedAreaManager.appendPlainStringToLastLine(" 有单位元：");
					imagedAreaManager.appendLaTeXStringToLastLine(Set.getElementLabel(identity, isIntElement));
				} else {
					textAreaManager.appendContent("\t\tThe operator " + operator1.getName() + " has NOT identity!\n");
					imagedAreaManager.appendPlainStringAsNewLine("        运算 ");
					imagedAreaManager.appendLaTeXStringToLastLine(operator1.getName());
					imagedAreaManager.appendPlainStringToLastLine(" 没有单位元");
				}
				
			}
			
			if (zeroBox.isSelected()) {
				boolean success = operator1.hasZeroElement();
				if (success) {
					char zero = operator1.getZeroElement();
					textAreaManager.appendContent("\t\tThe operator " + operator1.getName() + " has zero element: " + Set.getElementLabel(zero, isIntElement) + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("        运算 ");
					imagedAreaManager.appendLaTeXStringToLastLine(operator1.getName());
					imagedAreaManager.appendPlainStringToLastLine(" 有零元：");
					imagedAreaManager.appendLaTeXStringToLastLine(Set.getElementLabel(zero, isIntElement));
				} else {
					textAreaManager.appendContent("\t\tThe operator " + operator1.getName() + " has NOT zero element!\n");
					imagedAreaManager.appendPlainStringAsNewLine("        运算 ");
					imagedAreaManager.appendLaTeXStringToLastLine(operator1.getName());
					imagedAreaManager.appendPlainStringToLastLine(" 没有零元");
				}
				
			}
				
			if (inverseBox.isSelected()) {
				boolean success = operator1.hasIdentity();
				if (success) {
					textAreaManager.appendContent("\t\tFor operator " + operator1.getName() + ", the inverse of each element is as follows: \n");
					imagedAreaManager.appendPlainStringAsNewLine("        对运算 ");
					imagedAreaManager.appendLaTeXStringToLastLine(operator1.getName());
					imagedAreaManager.appendPlainStringToLastLine(" 各元素的逆元如下：");
					for (int i = 0; i < setA.length(); i++) {
						char element = setA.get(i);
						Character[] inverses = new Character[setA.length()];
						int number = operator1.getInverse(element, inverses);
						if (number > 0) {
							char[] inversElements = new char[number];
							for (int j = 0; j < number; j++) inversElements[j] = inverses[j];
							textAreaManager.appendContent("\t\t\tThe inverse of element " + Set.getElementLabel(element, isIntElement) + " has " + Set.getElementsLabel(inversElements, isIntElement, number) + "\n");
							imagedAreaManager.appendPlainStringAsNewLine("            元素 ");
							imagedAreaManager.appendLaTeXStringToLastLine(Set.getElementLabel(element, isIntElement));
							imagedAreaManager.appendPlainStringToLastLine(" 的逆元有：");
							imagedAreaManager.appendLaTeXStringToLastLine(Set.getElementsLabel(inversElements, isIntElement, number));
						} else {
							textAreaManager.appendContent("\t\t\tThe element " + Set.getElementLabel(element, isIntElement) + " has NOT inverse element!\n");
							imagedAreaManager.appendPlainStringAsNewLine("            元素 ");
							imagedAreaManager.appendLaTeXStringToLastLine(Set.getElementLabel(element, isIntElement));
							imagedAreaManager.appendPlainStringToLastLine(" 没有逆元！");
						}
					}
				} else {
					textAreaManager.appendContent("\t\tThe operator " + operator1.getName() + " has NOT identity, all element has NOT inverse!\n");
					imagedAreaManager.appendPlainStringAsNewLine("        运算 ");
					imagedAreaManager.appendLaTeXStringToLastLine(operator1.getName());
					imagedAreaManager.appendPlainStringToLastLine(" 没有单位元，因此所有元素都没有逆元！");
				}
			}

			if (operator2 != null) {
				textAreaManager.appendContent("\tThe properties of the operator " + operator2.getName() + " are as follows: \n");
				imagedAreaManager.appendPlainStringAsNewLine("    运算 ");
				imagedAreaManager.appendLaTeXStringToLastLine(operator2.getName());
				imagedAreaManager.appendPlainStringToLastLine(" 的性质如下：");
				if (commutativeBox.isSelected()) {
					boolean success = operator2.isCommutative();
					if (success) {
						textAreaManager.appendContent("\t\tThe operator " + operator2.getName() + " is commutative!\n");
						imagedAreaManager.appendPlainStringAsNewLine("        运算 ");
						imagedAreaManager.appendLaTeXStringToLastLine(operator2.getName());
						imagedAreaManager.appendPlainStringToLastLine(" 满足交换律！");
					} else {
						textAreaManager.appendContent("\t\tThe operator " + operator2.getName() + " is NOT commutative: " + BinaryOperator.getReasonMessage() + "\n");
						imagedAreaManager.appendPlainStringAsNewLine("        运算 ");
						imagedAreaManager.appendLaTeXStringToLastLine(operator2.getName());
						imagedAreaManager.appendPlainStringToLastLine(" 不满足交换律：");
						imagedAreaManager.appendLaTeXStringToLastLine(BinaryOperator.getReasonMessage());
					}
				}
				if (associativeBox.isSelected()) {
					boolean success = operator2.isAssociative();
					if (success) {
						textAreaManager.appendContent("\t\tThe operator " + operator2.getName() + " is assoicative!\n");
						imagedAreaManager.appendPlainStringAsNewLine("        运算 ");
						imagedAreaManager.appendLaTeXStringToLastLine(operator2.getName());
						imagedAreaManager.appendPlainStringToLastLine(" 满足结合律！");
					} else {
						textAreaManager.appendContent("\t\tThe operator " + operator2.getName() + " is NOT assoicative: " + BinaryOperator.getReasonMessage() + "\n");
						imagedAreaManager.appendPlainStringAsNewLine("        运算 ");
						imagedAreaManager.appendLaTeXStringToLastLine(operator2.getName());
						imagedAreaManager.appendPlainStringToLastLine(" 不满足结合律：");
						imagedAreaManager.appendLaTeXStringToLastLine(BinaryOperator.getReasonMessage());
					}
				}
				if (idempotentBox.isSelected()) {
					boolean success = operator2.isIdempotent();
					if (success) {
						textAreaManager.appendContent("\t\tThe operator " + operator2.getName() + " is idempotent!\n");
						imagedAreaManager.appendPlainStringAsNewLine("        运算 ");
						imagedAreaManager.appendLaTeXStringToLastLine(operator2.getName());
						imagedAreaManager.appendPlainStringToLastLine(" 满足幂等律！");
					} else {
						textAreaManager.appendContent("\t\tThe operator " + operator2.getName() + " is NOT idempotent: " + BinaryOperator.getReasonMessage() + "\n");
						imagedAreaManager.appendPlainStringAsNewLine("        运算 ");
						imagedAreaManager.appendLaTeXStringToLastLine(operator2.getName());
						imagedAreaManager.appendPlainStringToLastLine(" 不满足幂等律：");
						imagedAreaManager.appendLaTeXStringToLastLine(BinaryOperator.getReasonMessage());
					}
				}
				if (cancellationBox.isSelected()) {
					boolean success = operator2.isCancellative();
					if (success) {
						textAreaManager.appendContent("\t\tThe operator " + operator2.getName() + " is cancellative!\n");
						imagedAreaManager.appendPlainStringAsNewLine("        运算 ");
						imagedAreaManager.appendLaTeXStringToLastLine(operator2.getName());
						imagedAreaManager.appendPlainStringToLastLine(" 满足消去律！");
					} else {
						textAreaManager.appendContent("\t\tThe operator " + operator2.getName() + " is NOT cancellative: " + BinaryOperator.getReasonMessage() + "\n");
						imagedAreaManager.appendPlainStringAsNewLine("        运算 ");
						imagedAreaManager.appendLaTeXStringToLastLine(operator2.getName());
						imagedAreaManager.appendPlainStringToLastLine(" 不满足消去律：");
						imagedAreaManager.appendLaTeXStringToLastLine(BinaryOperator.getReasonMessage());
					}
				}
				if (identityBox.isSelected()) {
					boolean success = operator2.hasIdentity();
					if (success) {
						char identity = operator2.getIdentity();
						textAreaManager.appendContent("\t\tThe operator " + operator2.getName() + " has identity: " + Set.getElementLabel(identity, isIntElement) + "\n");
						imagedAreaManager.appendPlainStringAsNewLine("        运算 ");
						imagedAreaManager.appendLaTeXStringToLastLine(operator2.getName());
						imagedAreaManager.appendPlainStringToLastLine(" 有单位元：");
						imagedAreaManager.appendLaTeXStringToLastLine(Set.getElementLabel(identity, isIntElement));
					} else {
						textAreaManager.appendContent("\t\tThe operator " + operator2.getName() + " has NOT identity!\n");
						imagedAreaManager.appendPlainStringAsNewLine("        运算 ");
						imagedAreaManager.appendLaTeXStringToLastLine(operator2.getName());
						imagedAreaManager.appendPlainStringToLastLine(" 没有单位元");
					}
				}
				if (zeroBox.isSelected()) {
					boolean success = operator2.hasZeroElement();
					if (success) {
						char zero = operator2.getZeroElement();
						textAreaManager.appendContent("\t\tThe operator " + operator2.getName() + " has zero element: " + Set.getElementLabel(zero, isIntElement) + "\n");
						imagedAreaManager.appendPlainStringAsNewLine("        运算 ");
						imagedAreaManager.appendLaTeXStringToLastLine(operator2.getName());
						imagedAreaManager.appendPlainStringToLastLine(" 有零元：");
						imagedAreaManager.appendLaTeXStringToLastLine(Set.getElementLabel(zero, isIntElement));
					} else {
						textAreaManager.appendContent("\t\tThe operator " + operator2.getName() + " has NOT zero element!\n");
						imagedAreaManager.appendPlainStringAsNewLine("        运算 ");
						imagedAreaManager.appendLaTeXStringToLastLine(operator2.getName());
						imagedAreaManager.appendPlainStringToLastLine(" 没有零元");
					}
				}
				if (inverseBox.isSelected()) {
					boolean success = operator2.hasIdentity();
					if (success) {
						textAreaManager.appendContent("\t\tFor operator " + operator2.getName() + ", the inverse of each element is as follows: \n");
						imagedAreaManager.appendPlainStringAsNewLine("        对运算 ");
						imagedAreaManager.appendLaTeXStringToLastLine(operator1.getName());
						imagedAreaManager.appendPlainStringToLastLine(" 各元素的逆元如下：");
						for (int i = 0; i < setA.length(); i++) {
							char element = setA.get(i);
							Character[] inverses = new Character[setA.length()];
							int number = operator2.getInverse(element, inverses);
							if (number > 0) {
								char[] inversElements = new char[number];
								for (int j = 0; j < number; j++) inversElements[j] = inverses[j];
								textAreaManager.appendContent("\t\t\tThe inverse of element " + Set.getElementLabel(element, isIntElement) + " has " + Set.getElementsLabel(inversElements, isIntElement, number) + "\n");
								imagedAreaManager.appendPlainStringAsNewLine("            元素 ");
								imagedAreaManager.appendLaTeXStringToLastLine(Set.getElementLabel(element, isIntElement));
								imagedAreaManager.appendPlainStringToLastLine(" 的逆元有：");
								imagedAreaManager.appendLaTeXStringToLastLine(Set.getElementsLabel(inversElements, isIntElement, number));
							} else {
								textAreaManager.appendContent("\t\t\tThe element " + Set.getElementLabel(element, isIntElement) + " has NOT inverse element!\n");
								imagedAreaManager.appendPlainStringAsNewLine("            元素 ");
								imagedAreaManager.appendLaTeXStringToLastLine(Set.getElementLabel(element, isIntElement));
								imagedAreaManager.appendPlainStringToLastLine(" 没有逆元！");
							}
						}
					} else {
						textAreaManager.appendContent("\t\tThe operator " + operator2.getName() + " has NOT identity, all element has NOT inverse!\n");
						imagedAreaManager.appendPlainStringAsNewLine("        运算 ");
						imagedAreaManager.appendLaTeXStringToLastLine(operator2.getName());
						imagedAreaManager.appendPlainStringToLastLine(" 没有单位元，因此所有元素都没有逆元！");
					}
				}
			}


			if (distributiveBox.isSelected() && operator2 != null) {
				boolean success = operator1.isDistributiveWith(operator2);
				if (success) {
					textAreaManager.appendContent("\tThe operator " + operator2.getName() + " is distributive with " + operator1.getName() + "!\n");
					imagedAreaManager.appendPlainStringAsNewLine("    运算 ");
					imagedAreaManager.appendLaTeXStringToLastLine(operator2.getName());
					imagedAreaManager.appendPlainStringToLastLine(" 对运算 ");
					imagedAreaManager.appendLaTeXStringToLastLine(operator1.getName());
					imagedAreaManager.appendPlainStringToLastLine(" 满足分配律！");
				} else {
					textAreaManager.appendContent("\tThe operator " + operator2.getName() + " is NOT distributive with " + operator1.getName() + ": " + BinaryOperator.getReasonMessage() + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    运算 ");
					imagedAreaManager.appendLaTeXStringToLastLine(operator2.getName());
					imagedAreaManager.appendPlainStringToLastLine(" 对运算 ");
					imagedAreaManager.appendLaTeXStringToLastLine(operator1.getName());
					imagedAreaManager.appendPlainStringToLastLine(" 不满足分配律：");
					imagedAreaManager.appendLaTeXStringToLastLine(BinaryOperator.getReasonMessage());
				}
				
				success = operator2.isDistributiveWith(operator1);
				if (success) {
					textAreaManager.appendContent("\tThe operator " + operator1.getName() + " is distributive with " + operator2.getName() + "!\n");
					imagedAreaManager.appendPlainStringAsNewLine("    运算 ");
					imagedAreaManager.appendLaTeXStringToLastLine(operator1.getName());
					imagedAreaManager.appendPlainStringToLastLine(" 对运算 ");
					imagedAreaManager.appendLaTeXStringToLastLine(operator2.getName());
					imagedAreaManager.appendPlainStringToLastLine(" 满足分配律！");
				} else {
					textAreaManager.appendContent("\tThe operator " + operator1.getName() + " is NOT distributive with " + operator2.getName() + ": " + BinaryOperator.getReasonMessage() + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    运算 ");
					imagedAreaManager.appendLaTeXStringToLastLine(operator1.getName());
					imagedAreaManager.appendPlainStringToLastLine(" 对运算 ");
					imagedAreaManager.appendLaTeXStringToLastLine(operator2.getName());
					imagedAreaManager.appendPlainStringToLastLine(" 不满足分配律：");
					imagedAreaManager.appendLaTeXStringToLastLine(BinaryOperator.getReasonMessage());
				}
			}
			
			if (absorptionBox.isSelected() && operator2 != null) {
				boolean success = operator1.isAbsorptiveWith(operator2);
				if (success) {
					textAreaManager.appendContent("\tThe operator " + operator1.getName() + " and " + operator2.getName() + " is absorptive!\n");
					imagedAreaManager.appendPlainStringAsNewLine("    运算 ");
					imagedAreaManager.appendLaTeXStringToLastLine(operator1.getName());
					imagedAreaManager.appendPlainStringToLastLine(" 和运算 ");
					imagedAreaManager.appendLaTeXStringToLastLine(operator2.getName());
					imagedAreaManager.appendPlainStringToLastLine(" 满足吸收律！");
				} else {
					textAreaManager.appendContent("\tThe operator " +  operator1.getName() + " and " + operator2.getName()  + " is NOT absorptive: " + BinaryOperator.getReasonMessage() + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    运算 ");
					imagedAreaManager.appendLaTeXStringToLastLine(operator1.getName());
					imagedAreaManager.appendPlainStringToLastLine(" 和运算 ");
					imagedAreaManager.appendLaTeXStringToLastLine(operator2.getName());
					imagedAreaManager.appendPlainStringToLastLine(" 不满足吸收律：");
					imagedAreaManager.appendLaTeXStringToLastLine(BinaryOperator.getReasonMessage());
				}
			}

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

			Character[] baseSet = new Character[setA.length()];
			for (int i = 0; i < setA.length(); i++) baseSet[i] = new Character(setA.get(i));

			String operator1String = operator1Area.getText();
			BinaryOperator<Character> operator1 = BinaryOperator.extractBinaryOperator(baseSet, operator1String, isIntElement);
			if (operator1 == null) {
				feedbackAnimator.appendPlainStringAsNewLine("字符串 " + operator1String + "  不是合法的表示运算表的字符串！");
				feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + BinaryOperator.getReasonMessage());
				return false;
			} else {
				operator1.setName("\\circ ");
				feedbackAnimator.appendLaTeXStringAsNewLine(operator1.toLaTeXString());
			}
			String operator2String = operator2Area.getText();
			if (!operator2String.contentEquals("")) {
				BinaryOperator<Character> operator2 = BinaryOperator.extractBinaryOperator(baseSet, operator2String, isIntElement);
				if (operator2 == null) {
					feedbackAnimator.appendPlainStringAsNewLine("字符串 " + operator2String + "  不是合法的表示运算表的字符串！");
					feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + BinaryOperator.getReasonMessage());
					return false;
				} else {
					operator2.setName(" * ");
					feedbackAnimator.appendLaTeXStringToLastLine("\\quad\\quad " + operator2.toLaTeXString());
				}
			}
			return true;
		}
		
		public boolean generateInputs() {
			Set setA = getSetA();
			if (setA == null) {
				char[] elements = {'1', '2', '3', '4', '5'};
				setA = new Set(elements);
				setAField.setText(setA.toString());
				intTypeButton.setSelected(false);
				charTypeButton.setSelected(true);
			}
			StringBuffer buffer = new StringBuffer();
			buffer.append("{");
			boolean isFirst = true;
			for (int i = 0; i < setA.length(); i++) {
				char first = setA.get(i);
				for (int j = 0; j < setA.length(); j++) {
					char second = setA.get(j);
					int index = (int)(Math.random() * setA.length());
					char result = setA.get(index);
					if (isFirst) isFirst = false;
					else buffer.append(", ");
					buffer.append("<" + first + ", " + second + ", " + result + ">");
				}
			}
			buffer.append("}");
			operator1Area.setText(buffer.toString());
			
			buffer = new StringBuffer();
			buffer.append("{");
			isFirst = true;
			for (int i = 0; i < setA.length(); i++) {
				char first = setA.get(i);
				for (int j = 0; j < setA.length(); j++) {
					char second = setA.get(j);
					int index = (int)(Math.random() * setA.length());
					char result = setA.get(index);
					if (isFirst) isFirst = false;
					else buffer.append(", ");
					buffer.append("<" + first + ", " + second + ", " + result + ">");
				}
			}
			buffer.append("}");
			operator2Area.setText(buffer.toString());
			checkInputs();		// 让生成的信息通过合法性检查后反馈在输入界面之上
			return true;
		}
	}
}
