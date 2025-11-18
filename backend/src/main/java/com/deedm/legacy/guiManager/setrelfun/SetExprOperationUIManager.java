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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.deedm.legacy.graphicAnimation.AnimationArea;
import com.deedm.legacy.guiManager.ImagedTextAreaAnimator;
import com.deedm.legacy.guiManager.ImagedTextAreaManager;
import com.deedm.legacy.guiManager.MainSwingFrame;
import com.deedm.legacy.guiManager.PlainTextAreaManager;
import com.deedm.legacy.setrelfun.Set;
import com.deedm.legacy.setrelfun.SetExpr;
import com.deedm.legacy.setrelfun.SetrelfunUtil;

/**
 * @author
 * @since 
 *
 * 用于演示集合表达式运算的界面管理器
 */
public class SetExprOperationUIManager extends JPanel {
	private static final long serialVersionUID = 1L;
	
	// 使用正则表达式规范表达式字符合法性，并进行静态常量化，降低每次使用都要编译的消耗
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("(\\\\cap|\\\\cup|\\\\oplus|\\\\overline|-|A|B|\\}|\\{|\\)|\\(| )+");
    
	
	private static final int width = Math.max(2* MainSwingFrame.screenWidth/5, 1024)*(MainSwingFrame.screenResolution/96);
	private static final int height = Math.max(MainSwingFrame.screenHeight/3, 480)*(MainSwingFrame.screenResolution/96);
	private ImagedTextAreaManager imagedAreaManager = null;
	private PlainTextAreaManager textAreaManager = null;
	private ImagedTextAreaAnimator feedbackAnimator = null;

	private JDialog dialog = null;
	private JButton defaultButton = null;
	private JTextField setUField = null;
	private JTextField setAField = null;
	private JTextField setBField = null;
	private JTextField expressionField = null;
	private JRadioButton charTypeButton = null;
	private JRadioButton intTypeButton = null;

	private boolean ok = false;
	private int counter = 0;
	
	public SetExprOperationUIManager(PlainTextAreaManager plainManager, ImagedTextAreaManager imageManager) {
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

	public void clearInputs() {
		feedbackAnimator.clearContent();
		setUField.setText("");
		setAField.setText("");
		setBField.setText("");
		expressionField.setText("");
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
	
	public boolean setAllSets(String setUString, String setAString, String setBString, String expressionString) {
		boolean isIntElement = false;
		if (intTypeButton.isSelected()) isIntElement = true;
		char[] setUElements = SetrelfunUtil.extractSetElements(setUString, isIntElement);
		char[] setAElements = SetrelfunUtil.extractSetElements(setAString, isIntElement);
		char[] setBElements = SetrelfunUtil.extractSetElements(setBString, isIntElement);
		
		setUField.setText(setUString);
		setAField.setText(setAString);
		setBField.setText(setBString);
		expressionField.setText(expressionString);
		
		boolean success = true;
		if (setUElements != null) {
			Set setU = new Set(setUElements);
			feedbackAnimator.appendLaTeXStringAsNewLine("U = " + setU.toLaTeXString());
		} else {
			success = false;
			feedbackAnimator.appendPlainStringAsNewLine("字符串 " + setUString + " 没有合法地给出全集的元素！");  
		}
		if (setAElements != null) {
			Set setA = new Set(setAElements);
			feedbackAnimator.appendLaTeXStringAsNewLine("A = " + setA.toLaTeXString());
		} else {
			success = false;
			feedbackAnimator.appendPlainStringAsNewLine("字符串 " + setAString + " 没有合法地给出集合A的元素！");  
		}
		if (setBElements != null) {
			Set setB = new Set(setBElements);
			feedbackAnimator.appendLaTeXStringAsNewLine("B = " + setB.toLaTeXString());
		} else {
			success = false;
			feedbackAnimator.appendPlainStringAsNewLine("字符串 " + setBString + " 没有合法地给出集合B的元素！");  
		} 
		if (null != expressionString && !"".equals(expressionString.trim())) {
			// 删除用户输入的多余空格
			expressionString = expressionString.replaceAll(" ", "");
			// 添加\cap,\cup等指令后面必要的空格
			expressionString = convertToLatexString(expressionString);
			// 临时窗口输出打印
			feedbackAnimator.appendPlainStringAsNewLine("集合运算表达式 = ");
			feedbackAnimator.appendLaTeXStringToLastLine(expressionString);
    	} else {
    		success = false;
    		feedbackAnimator.appendPlainStringAsNewLine("集合表达式不能为空");
    	}
		return success;
	}
	
	public void clearFeedback() {
		feedbackAnimator.clearContent();
	}
	
	public void updateFeedback() {
		feedbackAnimator.update();
	}
	
	public Set getSetU() {
		boolean isIntElement = false;
		if (intTypeButton.isSelected()) isIntElement = true;
		String content = setUField.getText();
		char[] elements = SetrelfunUtil.extractSetElements(content, isIntElement);
		if (elements == null) return null;
		else return new Set(elements);
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
	
	// 创建输入集合的窗格
	private JPanel createInputSetPanel() {
		// 创建用于输入公式的窗格，使用盒式布局管理
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new GridLayout(3, 2, 5, 0));
		// 设置该窗格使用的带标题的边框
 		inputPanel.setBorder(BorderFactory.createTitledBorder("请输入集合的元素(可用也可不用左右花括号)和表达式，表达式的运算符请用latex命令格式"));

 		int inputWidth = 60;
		// 创建一个标签用于提示输入集合信息
 		JPanel tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
		JLabel promptLabel = new JLabel("全集(U)：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+U可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('U');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		setUField = new JTextField(inputWidth);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(setUField);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(setUField);
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
		// 创建一个标签用于提示输入集合信息
		promptLabel = new JLabel("集合(A)：", JLabel.RIGHT);
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
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
		// 创建一个标签用于提示输入集合信息
		promptLabel = new JLabel("集合(B)：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+B可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('B');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		setBField = new JTextField(inputWidth);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(setBField);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(setBField);
		inputPanel.add(tempPanel);
 		
		// 创建一个标签用于提示输入集合信息
 		tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
		promptLabel = new JLabel("集合表达式(n)：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+N可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('n');	
		// 创建输入集合元素的文本字段，缺省宽度为50, 但这不意味着只能输入50个字符
		expressionField = new JTextField(inputWidth);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(expressionField);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(expressionField);
		inputPanel.add(tempPanel);
		
		JPanel displayPanel = new JPanel();
		displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
		// 设置该窗格使用的带标题的边框
		displayPanel.setBorder(BorderFactory.createTitledBorder("你输入的集合和表达式如下："));
 		AnimationArea area = new AnimationArea(width, height/2);
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
		JButton actButton = new JButton("集合运算(Y)");
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
		
		JButton exampleButton = new JButton("例子5.4");
		exampleButton.setActionCommand("example5_4");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("例子5.9");
		exampleButton.setActionCommand("example5_9");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("例子5.13");
		exampleButton.setActionCommand("example5_13");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("例子5.15");
		exampleButton.setActionCommand("example5_15");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("例子5.17");
		exampleButton.setActionCommand("example5_17");			
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
				ok = true;
				feedbackAnimator.clearContent();
				counter = counter + 1;
				doSetOperation();
				feedbackAnimator.update();
				imagedAreaManager.revalidate();
			} else if (command.equals("cancel")) {
				ok = false;
				dialog.setVisible(false);
			} else if (command.equals("remove")) {
				clearInputs();
			} else if (command.equals("generate")) {
				feedbackAnimator.clearContent();
				generateSets();
				feedbackAnimator.update();
			} else if (command.equals("check")) {
				feedbackAnimator.clearContent();
				boolean success = checkInputs();
				if (success != true) {
					JOptionPane.showMessageDialog(dialog, "输入的集合不完全符合要求！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(dialog, "输入的集合都符合要求，可以进行集合运算！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				}
				feedbackAnimator.update();
			} else if (command.equals("example5_4")) {
				String setUString = "{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16}";
				String setAString = "{1, 3, 5, 7, 9, 11}";
				String setBString = "{1, 4, 7, 10, 13, 16}";
				String expressionString = "\\overline{A\\cap B \\oplus A}";
				setElementTypeInt(true);
				setAllSets(setUString, setAString, setBString, expressionString);
			} else if (command.equals("example5_9")) {
				String setUString = "{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16}";
				String setAString = "{1, 3, 5, 7, 9, 11}";
				String setBString = "{1, 4, 7, 10, 13, 16}";
				String expressionString = "\\overline{A\\cap B \\oplus A}";
				setElementTypeInt(true);
				setAllSets(setUString, setAString, setBString, expressionString);
			} else if (command.equals("example5_13")) {
				String setUString = "{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16}";
				String setAString = "{1, 3, 5, 7, 9, 11}";
				String setBString = "{1, 4, 7, 10, 13, 16}";
				String expressionString = "\\overline{A\\cap B \\oplus A}";
				setElementTypeInt(true);
				setAllSets(setUString, setAString, setBString, expressionString);
			} else if (command.equals("example5_15")) {
				String setUString = "{1, 2, 3, 4}";
				String setAString = "{1, 2, 3}";
				String setBString = "{1, 4}";
				String expressionString = "\\overline{A\\cap B \\oplus A}";
				setElementTypeInt(true);
				setAllSets(setUString, setAString, setBString, expressionString);
			} else if (command.equals("example5_17")) {
				String setUString = "{1, 2, 3, 4, 5, 6, 7, 8}";
				String setAString = "{1, 3, 5, 7}";
				String setBString = "{1, 4, 7}";
				String expressionString = "\\overline{A\\cap B \\oplus A}";
				setElementTypeInt(true);
				setAllSets(setUString, setAString, setBString, expressionString);
			} else if (command.equals("clear")) {
				textAreaManager.clearContent();
				imagedAreaManager.clearContent();
				imagedAreaManager.revalidate();
			}
		}
		

		
		public boolean generateSets() {
			Set setU = getSetU();
			String expressionString = "\\overline{A\\cap B \\oplus A}";
			if (setU == null) {
				char[] elements = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
				setU = new Set(elements);
				setElementTypeInt(true);
			}
			Set setA = Set.randomGenerateSubset(setU);
			Set setB = Set.randomGenerateSubset(setU);
			setAllSets(setU.toString(), setA.toString(), setB.toString(), expressionString);
			return true;
		}
		
		public boolean checkInputs() {
			Set setU = getSetU();
			if (setU == null) {
				feedbackAnimator.appendPlainStringAsNewLine("字符串 " + setUField.getText() + " 没有给出合法的全集！");
				return false;
			} else feedbackAnimator.appendLaTeXStringAsNewLine("U = " + setU.toLaTeXString());
			Set setA = getSetA();
			if (setA == null) {
				feedbackAnimator.appendPlainStringAsNewLine("字符串 " + setAField.getText() + " 没有给出合法的集合A！");
				return false;
			}  else feedbackAnimator.appendLaTeXStringAsNewLine("A = " + setA.toLaTeXString());
			Set setB = getSetB();
			if (setB == null) {
				feedbackAnimator.appendPlainStringAsNewLine("字符串 " + setBField.getText() + " 没有给出合法的集合B！");
				return false;
			}  else feedbackAnimator.appendLaTeXStringAsNewLine("B = " + setB.toLaTeXString());
			if (!setU.isSubset(setA)) {
				feedbackAnimator.appendPlainStringAsNewLine("集合 ");
				feedbackAnimator.appendLaTeXStringToLastLine("A = " + setA.toLaTeXString());
				feedbackAnimator.appendPlainStringToLastLine(" 不是集合 ");
				feedbackAnimator.appendLaTeXStringToLastLine("U = " + setU.toLaTeXString());
				feedbackAnimator.appendPlainStringToLastLine(" 的子集！");
				return false;
			}
			if (!setU.isSubset(setB)) {
				feedbackAnimator.appendPlainStringAsNewLine("集合 ");
				feedbackAnimator.appendLaTeXStringToLastLine("B = " + setB.toLaTeXString());
				feedbackAnimator.appendPlainStringToLastLine(" 不是集合 ");
				feedbackAnimator.appendLaTeXStringToLastLine("U = " + setU.toLaTeXString());
				feedbackAnimator.appendPlainStringToLastLine(" 的子集！");
				return false;
			}
			String input = expressionField.getText();
			input = input.replaceAll(" ", "");
			
			Matcher matcher = EXPRESSION_PATTERN.matcher(input);
			
	    	if (null == input || "".equals(input.trim())) { // 非空校验
	    		feedbackAnimator.appendPlainStringAsNewLine("集合表达式不能为空");
				return false;
	    	} else if (!matcher.matches()) { // 表达式字符合法性校验
	    		feedbackAnimator.appendPlainStringAsNewLine("表达式含有非法字符或字符串");
				return false;
	    	} 
	    	String expressionString = expressionField.getText();
			// 构建判断错误信息的集合
			char[] setErrorElements = SetrelfunUtil.extractSetElements("e,r,r,o,r", false);
	    	Set errorMessageSet = new Set(setErrorElements);
			Set result = SetExpr.executeExpression(expressionString, setU, setA, setB, true);
			if (result.equalsTo(errorMessageSet)) {
				return false;
			}
			
			return true;
		}
	}
	
	/**
	 * 做集合表达式运算,并管理结果面板显示 
	 * 
	 */
	public boolean doSetOperation() {
		Set setU = getSetU();
		Set setA = getSetA();
		Set setB = getSetB();	
		// 对于集合输入和表达式的输入校验封装到方法setValidation()和inputValidation()中
		
		String expressionString = expressionField.getText();
		// 构建判断错误信息的集合
		char[] setErrorElements = SetrelfunUtil.extractSetElements("e,r,r,o,r", false);
    	Set errorMessageSet = new Set(setErrorElements);
    	
		Set result1 = null;
		if (setValidation() && inputValidation()) result1 = SetExpr.executeExpression(expressionString, setU, setA, setB, true);
		else return true;
		if (result1.equalsTo(errorMessageSet)) {
			JOptionPane.showMessageDialog(dialog, "表达式输入不合法", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
			return true;
		}
		// 删除用户输入的多余空格
		expressionString = expressionString.replaceAll(" ", "");
		// 添加\cap,\cup等指令后面必要的空格
		expressionString = convertToLatexString(expressionString);
		
		// 输入窗口的提示模块打印
		feedbackAnimator.appendPlainStringAsNewLine("集合运算表达式 = ");
		feedbackAnimator.appendLaTeXStringToLastLine(expressionString);
		
		// 主界面左边输出打印
		imagedAreaManager.appendPlainStringAsNewLine(counter + ": 全集 ");
		imagedAreaManager.appendLaTeXStringToLastLine("U = " + setU.toLaTeXString());
		imagedAreaManager.appendPlainStringAsNewLine("    集合 ");
		imagedAreaManager.appendLaTeXStringToLastLine("A = " + setA.toLaTeXString());
		imagedAreaManager.appendPlainStringAsNewLine("    集合 ");
		imagedAreaManager.appendLaTeXStringToLastLine("B = " + setB.toLaTeXString());
		
		imagedAreaManager.appendPlainStringAsNewLine("    集合表达式 ");
		imagedAreaManager.appendLaTeXStringToLastLine("expression = " + expressionString);
		imagedAreaManager.appendPlainStringAsNewLine("    集合表达式计算结果 ");
		imagedAreaManager.appendLaTeXStringToLastLine("result = " + result1.toLaTeXString());
		
		
		// 主界面右边输出打印
		textAreaManager.appendContent(counter + ": The universal set U = " + setU.toString() + "\n");
		textAreaManager.appendContent("\tA = " + setA.toString() + "\n");
		textAreaManager.appendContent("\tB = " + setA.toString() + "\n");

		return true;
	}

	
	/**
	 * 用户在输入表达式字符串时可能存在忘记在\cap等命令后加空格的情况，这里做一个空格添加
	 * */
	public String convertToLatexString(String expression) {
		for (int i = 0;i < expression.length(); i++) {
			if ((expression.charAt(i) == 'p' &&  expression.charAt(i-1) != 'o') || expression.charAt(i) == 's') {
				StringBuilder stringBuilder = new StringBuilder(expression);
				stringBuilder.insert(i+1, " ");
				expression = stringBuilder.toString();
			}
		}
		return expression;
	}
	
	/**
	 * 对集合输入进行校验
	 */
	private boolean setValidation() {
		Set setU = getSetU();
		if (setU == null) {
			textAreaManager.appendContent("The string " + setUField.getText() + " does not give a legal set!\n");
			imagedAreaManager.appendPlainStringAsNewLine("字符串 " + setUField.getText() + " 没有给出合法的全集！");
			feedbackAnimator.appendPlainStringAsNewLine("字符串 " + setUField.getText() + " 没有给出合法的全集！");
			return false;
		} else feedbackAnimator.appendLaTeXStringAsNewLine("U = " + setU.toLaTeXString());
		Set setA = getSetA();
		if (setA == null) {
			textAreaManager.appendContent("The string " + setAField.getText() + " does not give a legal set!\n");
			imagedAreaManager.appendPlainStringAsNewLine("字符串 " + setAField.getText() + " 没有给出合法的集合A！");
			feedbackAnimator.appendPlainStringAsNewLine("字符串 " + setAField.getText() + " 没有给出合法的集合A！");
			return false;
		}  else feedbackAnimator.appendLaTeXStringAsNewLine("A = " + setA.toLaTeXString());
		Set setB = getSetB();
		if (setB == null) {
			textAreaManager.appendContent("The string " + setBField.getText() + " does not give a legal set!\n");
			imagedAreaManager.appendPlainStringAsNewLine("字符串 " + setBField.getText() + " 没有给出合法的集合B！");
			feedbackAnimator.appendPlainStringAsNewLine("字符串 " + setBField.getText() + " 没有给出合法的集合B！");
			return false;
		}  else feedbackAnimator.appendLaTeXStringAsNewLine("B = " + setB.toLaTeXString());
		return true;
	}
	
	/**
	 * 对表达式输入进行校验
	 * */
	private boolean inputValidation() {
		String input = expressionField.getText();
		input = input.replaceAll(" ", "");
		
		Matcher matcher = EXPRESSION_PATTERN.matcher(input);
		
    	if (null == input || "".equals(input.trim())) { // 非空校验
			JOptionPane.showMessageDialog(dialog, "表达式不能为空", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
			return false;
    	} else if (!matcher.matches()) { // 表达式字符合法性校验
			JOptionPane.showMessageDialog(dialog, "表达式含有非法字符或字符串", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
			return false;
    	} 
    	return true;
	}
}
