package com.deedm.legacy.guiManager.count;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.deedm.legacy.counting.ExprCalculator;
import com.deedm.legacy.graphicAnimation.AnimationArea;
import com.deedm.legacy.guiManager.ImagedTextAreaAnimator;
import com.deedm.legacy.guiManager.ImagedTextAreaManager;
import com.deedm.legacy.guiManager.MainSwingFrame;
import com.deedm.legacy.guiManager.PlainTextAreaManager;

/**
 * 用于演示组合表达式运算的界面管理器
 *
 */
public class ExprCalculatorUIManager extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static final int width = Math.max(2* MainSwingFrame.screenWidth/5, 1024)*(MainSwingFrame.screenResolution/96);
	private static final int height = Math.max(MainSwingFrame.screenHeight/6, 350)*(MainSwingFrame.screenResolution/96);
	
	private ImagedTextAreaManager imagedAreaManager = null;
	private PlainTextAreaManager textAreaManager = null;
	private ImagedTextAreaAnimator feedbackAnimator = null;

	private JDialog dialog = null;
	private JButton defaultButton = null;
	private JTextField inputField = null;

	private boolean ok = false;
	private int counter = 0;
	
    // 使用正则表达式规范表达式字符合法性，并进行静态常量化，降低每次使用都要编译的消耗
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("[0-9\\.+-/*^!PC,() ]+");

	public ExprCalculatorUIManager(PlainTextAreaManager plainManager, ImagedTextAreaManager imageManager) {
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

	public boolean setInputs(String input) {
		inputField.setText(input+"");
		feedbackAnimator.appendPlainStringAsNewLine("组合表达式 = ");
		feedbackAnimator.appendLaTeXStringToLastLine(input);
		return true;
	}
	
	public void clearInputs() {
		feedbackAnimator.clearContent();
		inputField.setText("");
		feedbackAnimator.update();
	}
	
	// 创建输入关系的窗格
	private JPanel createInputSetPanel() {
		// 创建用于输入公式的窗格，使用盒式布局管理
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new GridLayout(1, 2, 5, 0));
		// 设置该窗格使用的带标题的边框
 		inputPanel.setBorder(BorderFactory.createTitledBorder("输入进行计算的组合表达式（支持加减乘除、幂、组合数、排列数、阶乘、括号的组合计算，支持超过整数范围的结果计算，P和C的输入若是表达式的话需要添加括号，如示例）"));

 		int inputWidth = 120;
		// 创建一个标签用于提示输入集合信息
 		JPanel tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 45));
		JLabel promptLabel = new JLabel("组合表达式(n)：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+N可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('n');	
		// 创建输入集合元素的文本字段，缺省宽度为50, 但这不意味着只能输入50个字符
		inputField = new JTextField(inputWidth);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(inputField);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(inputField);
		inputPanel.add(tempPanel);
		
		JPanel displayPanel = new JPanel();
		displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
		// 设置该窗格使用的带标题的边框
		displayPanel.setBorder(BorderFactory.createTitledBorder("你输入的表达式如下："));
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
		JButton actButton = new JButton("开始计算(Y)");
		actButton.setMnemonic('Y');					// 设置快捷键
		// 设置动作命令的名称，这可用来区分用户到底按了什么按钮
		actButton.setActionCommand("ok");			
		actButton.addActionListener(listener);		// 添加事件监听器
		buttonPanelOne.add(actButton);
		defaultButton = actButton;
		
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

		// 创建给出变量集信息和范式信息的窗格与相关组件
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
//		buttonPanel.add(infoPanel);
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
				counter = counter + 1;
				feedbackAnimator.clearContent();
				doCombCalculatorDisplay();
				imagedAreaManager.revalidate();
				feedbackAnimator.update();
			} else if (command.equals("remove")) {
				clearInputs();
			} else if (command.equals("check")) {
				boolean success = inputValidation();
				String result = null;
				if (success) {
					result = ExprCalculator.executeExpression(inputField.getText());
					if (!result.equals("error")) {
						JOptionPane.showMessageDialog(dialog, "输入的组合表达式符合要求，可以进行运算！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
					}
					else {
						JOptionPane.showMessageDialog(dialog, "输入的组合表达式有误，请检查！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
					}
				}
				// 输入非法时才清空顶层窗口的表达式输出区域
				if (!success || result.equals("error")) {
					feedbackAnimator.clearContent();
				}
				feedbackAnimator.update();
			} else if (command.equals("cancel")) {
				ok = false;
				dialog.setVisible(false);
			} else if (command.equals("clear")) {
				textAreaManager.clearContent();
				imagedAreaManager.clearContent();
				imagedAreaManager.revalidate();
			}
		}
		
		/**
		 * 对表达式输入进行校验
		 * */
		private boolean inputValidation() {
			String input = inputField.getText();
			Matcher matcher = EXPRESSION_PATTERN.matcher(input);
			
	    	if (null == input || "".equals(input.trim())) { // 非空校验
				JOptionPane.showMessageDialog(dialog, "表达式不能为空", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				return false;
	    	} else if (!matcher.matches()) { // 表达式字符合法性校验
				JOptionPane.showMessageDialog(dialog, "表达式含有非法字符", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				return false;
	    	} 
	    	return true;
		}
		
		// 情况一：为了处理2^3!或2^3!!等类似情况在latex显示下感叹号无法与3对齐的情况，为输入增添花括号，如2^{3!}
		// 情况二：以及幂指数是表达式的情况，也就是幂指数存在括号时，添加花括号，如2^(1+1)→2^{(1+1)}
		private String addCurlyBraces(String input) {
			for (int i = 1; i < input.length(); i++) { 
				if (input.charAt(i) == '!') { // 情况1
					for (int j = i-2; j>=0; j--) {
						// 遇到'^'时增添'{'，并在'!'后增添'}'
						if (input.charAt(j) != '!' && input.charAt(j) != '{' && input.charAt(j) != '}' 
								&& (input.charAt(j) < '0' || input.charAt(j) > '9')) {
							if (input.charAt(j) == '^') {
								StringBuilder stringBuilder1 = new StringBuilder(input);
								stringBuilder1.insert(j+1, "{");
								stringBuilder1.insert(i+2, "}");
								i += 2; // 因为两个括号加了长度2，因此令i增2
								input = stringBuilder1.toString();
							}
							break;
						}
					}
				}
				if (input.charAt(i) == '^' && input.charAt(i+1) == '(') { // 情况2
					StringBuilder stringBuilder2 = new StringBuilder(input);
					stringBuilder2.insert(i+1, "{");
					Stack<String> aStack = new Stack<>();
					aStack.push("(");
					int j;
					for (j = i+2; !aStack.empty(); j++) { // 括号匹配
						if (input.charAt(j) == '(') {
							aStack.push("(");
						} else if (input.charAt(j) == ')') {
							aStack.pop();
						}
					}
					stringBuilder2.insert(j+1, "}");
					input = stringBuilder2.toString();
				}	
			}
			return input;
		}
		
		public boolean doCombCalculatorDisplay() {
			String input = inputField.getText();
			
			// 对输入的表达式字符串进行校验，校验不通过则不进行运算
			if (!inputValidation()) {
				return true;
			}
			
			// 左边区域输出样式
			String result = ExprCalculator.executeExpression(input);
			// 除以上常规校验外，若表达式仍然不规范，则提示
			if (result.equals("error")) {
				JOptionPane.showMessageDialog(dialog, "表达式输入有误", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				return true;
			}
			input = addCurlyBraces(input);
			imagedAreaManager.appendPlainStringAsNewLine(counter + ": 要进行计算的表达式是： ");
			imagedAreaManager.appendLaTeXStringToLastLine("expression: " + input + "\\quad\\quad");
			imagedAreaManager.appendPlainStringAsNewLine("    ");
			imagedAreaManager.appendPlainStringToLastLine("计算结果如下：");
			imagedAreaManager.appendPlainStringAsNewLine("    ");
			imagedAreaManager.appendLaTeXStringToLastLine(input + " = " + result + "\\quad\\quad\\quad\\quad");	
			
			// 右边区域输出样式
			textAreaManager.appendContent(counter + " : expression = " + input + "\n");
			textAreaManager.appendContent("\t");
			textAreaManager.appendContent(input + " = " + result + "\t");
			
			feedbackAnimator.appendPlainStringAsNewLine("组合表达式 = ");
			feedbackAnimator.appendLaTeXStringToLastLine(input);
			
			return true;
		}

	}
}
