/**
 * 
 */
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

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.deedm.legacy.counting.generator.PermutationGenerator;
import com.deedm.legacy.counting.generator.StringGenerator;
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
public class GenPermutationUIManager extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final int width = Math.max(2* MainSwingFrame.screenWidth/5, 1024)*(MainSwingFrame.screenResolution/96);
	private static final int height = Math.max(1*MainSwingFrame.screenHeight/4, 240)*(MainSwingFrame.screenResolution/96);
	private ImagedTextAreaManager imagedAreaManager = null;
	private PlainTextAreaManager textAreaManager = null;
	private ImagedTextAreaAnimator feedbackAnimator = null;

	private JDialog dialog = null;
	private JButton defaultButton = null;
	private JTextField baseSetField = null;
	private JTextField lengthField = null;
	
	private JTextField startField = null;
	private JTextField numberField = null;

	private boolean ok = false;
	private int counter = 0;

	public GenPermutationUIManager(PlainTextAreaManager plainManager, ImagedTextAreaManager imageManager) {
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

	public void clearInput() {
		feedbackAnimator.clearContent();
		baseSetField.setText("");
		lengthField.setText("");
		startField.setText("");
		numberField.setText("");
		feedbackAnimator.update();
	}
	
	public boolean checkInput() {
		String content = baseSetField.getText();
		char[] elements = SetrelfunUtil.extractSetElements(content, false);
		if (elements == null) {
			// 弹出一窗口显示一些信息
			String message = SetrelfunUtil.getExtractErrorMessage();
			JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "输入的基集字符 " + content + "不合法: " + message, "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
			feedbackAnimator.appendPlainStringAsNewLine("输入的基集字符" + content + "不合法！");
			return false;
		}
		int length = 0;
		String lengthString = lengthField.getText();
		try {
			length = Integer.parseInt(lengthString);
			if (length > elements.length) length = elements.length;
		} catch (Exception exc) {
			JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "输入的字符串长度 " + lengthString + "不是合法的整数！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
			feedbackAnimator.appendPlainStringAsNewLine("输入的字符串长度 " + lengthString + "不是合法的整数！");
			return false;
		}
		String startString = startField.getText();
		if (!startString.contentEquals("") && startString.length() != length) {
			JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "起始串" + startString + "的长度不是输入的字符串长度 " + lengthString + "！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
			feedbackAnimator.appendPlainStringAsNewLine("起始串" + startString + "的长度不是输入的字符串长度 " + lengthString + "！");
			return false;
		}
		int number = 0;
		String numberString = numberField.getText();
		try {
			number = Integer.parseInt(numberString);
		} catch (Exception exc) {
			JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "输入的生成串个数 " + numberString + "不是合法的整数，将使用缺省值(20)！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
			feedbackAnimator.appendPlainStringAsNewLine("输入的生成串个数 " + numberString + "不是合法的整数，将使用缺省值(20)！");
			number = 20;
		}
		
		Set baseSet = new Set(elements);
		String message = "P(" + baseSet.length() + ", " + length + ")";
		feedbackAnimator.appendPlainStringAsNewLine("字符串基集：");
		feedbackAnimator.appendLaTeXStringToLastLine("B = " + baseSet.toLaTeXString());
		feedbackAnimator.appendPlainStringToLastLine("，长度：");
		feedbackAnimator.appendLaTeXStringToLastLine("n = " + length);
		feedbackAnimator.appendPlainStringToLastLine("，从：");
		feedbackAnimator.appendLaTeXStringToLastLine(startString);
		feedbackAnimator.appendPlainStringToLastLine(" 开始生成排列 ");
		feedbackAnimator.appendLaTeXStringToLastLine(message);
		feedbackAnimator.appendPlainStringToLastLine(" 的 ");
		feedbackAnimator.appendLaTeXStringToLastLine("" + number);
		feedbackAnimator.appendPlainStringToLastLine(" 个串！");
		
		return true;
	}
	
	// 创建输入关系的窗格
	private JPanel createInputSetPanel() {
		// 创建用于输入公式的窗格，使用盒式布局管理
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		// 设置该窗格使用的带标题的边框
 		inputPanel.setBorder(BorderFactory.createTitledBorder("输入生成的串的字符集、长度以及输出的起始串和个数（由于内存有限不能输出太多串，不给出起始串则从第一个串开始）"));

 		JPanel linePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 48, 15));
		// 创建一个标签用于提示输入集合信息
 		JPanel tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		JLabel promptLabel = new JLabel("基集字符(B)：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+V可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('B');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		baseSetField = new JTextField(72);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(baseSetField);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(baseSetField);
		linePanel.add(tempPanel);
		
		tempPanel = new JPanel();
		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		promptLabel = new JLabel("串的长度(L)：", JLabel.RIGHT);
		promptLabel.setDisplayedMnemonic('L');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		lengthField = new JTextField(8);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(lengthField);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(lengthField);
		linePanel.add(tempPanel);
		inputPanel.add(linePanel);
		
		// 创建一个标签用于提示输入集合信息
 		tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		promptLabel = new JLabel("输出起始串(S)：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+V可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('S');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		startField = new JTextField(72);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(startField);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(startField);
		linePanel.add(tempPanel);
		
		tempPanel = new JPanel();
		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		promptLabel = new JLabel("输出串个数(N)：", JLabel.RIGHT);
		promptLabel.setDisplayedMnemonic('N');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		numberField = new JTextField(8);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(numberField);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(numberField);
		linePanel.add(tempPanel);
		inputPanel.add(linePanel);
		
		JPanel displayPanel = new JPanel();
		displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
		// 设置该窗格使用的带标题的边框
		displayPanel.setBorder(BorderFactory.createTitledBorder("你输入字符串基集、长度、起始串以及输出串的个数："));
 		AnimationArea area = new AnimationArea(width, 2*height/5);
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
		JButton actButton = new JButton("开始生成(Y)");
		actButton.setMnemonic('Y');					// 设置快捷键
		// 设置动作命令的名称，这可用来区分用户到底按了什么按钮
		actButton.setActionCommand("ok");			
		actButton.addActionListener(listener);		// 添加事件监听器
		buttonPanelOne.add(actButton);
		defaultButton = actButton;

		// 创建用于确定输入信息的“检查”按钮
		actButton = new JButton("检查合法性(K)");
		actButton.setMnemonic('K');					// 设置快捷键
		// 设置动作命令的名称，这可用来区分用户到底按了什么按钮
		actButton.setActionCommand("check");
		actButton.addActionListener(listener);		// 添加事件监听器
		buttonPanelOne.add(actButton);

		// 创建用于确定输入信息的“清除输入”按钮
		actButton = new JButton("清除输入(V)");
		actButton.setMnemonic('V');					// 设置快捷键
		// 设置动作命令的名称，这可用来区分用户到底按了什么按钮
		actButton.setActionCommand("remove");
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
		
		JButton exampleButton = new JButton("问题8.38");
		exampleButton.setActionCommand("example8_38");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("例子8.39");
		exampleButton.setActionCommand("example8_39");			
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
				ok = true;
				counter = counter + 1;
				doCountString();
				feedbackAnimator.update();
				imagedAreaManager.revalidate();
			} else if (command.equals("check")) {
				feedbackAnimator.clearContent();
				boolean success = checkInput();
				if (success) {
					JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "输入合法，可以生成排列！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				}
				feedbackAnimator.update();
			} else if (command.equals("cancel")) {
				ok = false;
				dialog.setVisible(false);
			} else if (command.equals("remove")) {
				clearInput();
			} else if (command.equals("example8_38")) {
				feedbackAnimator.clearContent();
				clearInput();
				baseSetField.setText("1, 2, 3, 4, 5, 6, 7, 8");
				lengthField.setText("8");
				startField.setText("63285741");
				numberField.setText("20");
				checkInput();
				feedbackAnimator.update();
			} else if (command.equals("example8_39")) {
				feedbackAnimator.clearContent();
				clearInput();
				baseSetField.setText("1, 2, 3, 4, 5, 6, 7, 8");
				lengthField.setText("8");
				startField.setText("42876351");
				numberField.setText("20");
				checkInput();
				feedbackAnimator.update();
			} else if (command.equals("clear")) {
				textAreaManager.clearContent();
				imagedAreaManager.clearContent();
				imagedAreaManager.revalidate();
			}
		}
		
		public boolean doCountString() {
			String content = baseSetField.getText();
			char[] elements = SetrelfunUtil.extractSetElements(content, false);
			if (elements == null) {
				// 弹出一窗口显示一些信息
				String message = SetrelfunUtil.getExtractErrorMessage();
				JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "输入的基集字符 " + content + "不合法: " + message, "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				feedbackAnimator.appendPlainStringAsNewLine("输入的基集字符" + content + "不合法！");
				return false;
			}
			int length = 0;
			String lengthString = lengthField.getText();
			try {
				length = Integer.parseInt(lengthString);
				if (length > elements.length) length = elements.length;
			} catch (Exception exc) {
				JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "输入的字符串长度 " + lengthString + "不是合法的整数！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				feedbackAnimator.appendPlainStringAsNewLine("输入的字符串长度 " + lengthString + "不是合法的整数！");
				return false;
			}
			String startString = startField.getText();
			if (!startString.contentEquals("") && startString.length() != length) {
				JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "起始串" + startString + "的长度不是输入的字符串长度 " + lengthString + "！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				feedbackAnimator.appendPlainStringAsNewLine("起始串" + startString + "的长度不是输入的字符串长度 " + lengthString + "！");
				startString = "";
			}
			int number = 0;
			String numberString = numberField.getText();
			try {
				number = Integer.parseInt(numberString);
			} catch (Exception exc) {
				JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "输入的生成串个数 " + numberString + "不是合法的整数，将使用缺省值(20)！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				feedbackAnimator.appendPlainStringAsNewLine("输入的生成串个数 " + numberString + "不是合法的整数，将使用缺省值(20)！");
				number = 20;
			}
			
			Set baseSet = new Set(elements);
			String message = "P(" + baseSet.length() + ", " + length + ")";
			StringGenerator generator = new PermutationGenerator(elements, length);
			generator.first();
			if (startString.contentEquals("")) {
				char[] charArray = generator.current();
				startString = StringGenerator.convertToString(charArray);;
			}
			feedbackAnimator.appendPlainStringAsNewLine("字符串基集：");
			feedbackAnimator.appendLaTeXStringToLastLine("B = " + baseSet.toLaTeXString());
			feedbackAnimator.appendPlainStringToLastLine("，长度：");
			feedbackAnimator.appendLaTeXStringToLastLine("n = " + length);
			feedbackAnimator.appendPlainStringToLastLine("，从：");
			feedbackAnimator.appendLaTeXStringToLastLine(startString);
			feedbackAnimator.appendPlainStringToLastLine(" 开始输出排列 ");
			feedbackAnimator.appendLaTeXStringToLastLine(message);
			feedbackAnimator.appendPlainStringToLastLine(" 的 ");
			feedbackAnimator.appendLaTeXStringToLastLine("" + number);
			feedbackAnimator.appendPlainStringToLastLine(" 个串！");
			textAreaManager.appendContent(counter + ": the base set B = " + baseSet.toLaTeXString() + ", length n = " + length + ", and generate " + number + " strings from " + startString + " for " + message);
			imagedAreaManager.appendPlainStringAsNewLine(counter + "：字符串基集：");
			imagedAreaManager.appendLaTeXStringToLastLine("B = " + baseSet.toLaTeXString());
			imagedAreaManager.appendPlainStringToLastLine("，长度：");
			imagedAreaManager.appendLaTeXStringToLastLine("n = " + length);
			imagedAreaManager.appendPlainStringToLastLine("，从：");
			imagedAreaManager.appendLaTeXStringToLastLine(startString);
			imagedAreaManager.appendPlainStringToLastLine(" 开始输出排列 ");
			imagedAreaManager.appendLaTeXStringToLastLine(message);
			imagedAreaManager.appendPlainStringToLastLine(" 的 ");
			imagedAreaManager.appendLaTeXStringToLastLine("" + number);
			imagedAreaManager.appendPlainStringToLastLine(" 个串！");
			
			int totalCounter = 0;
			boolean display = false;
			int displayCounter = 0;
			int numberPerLine = 8;
			while (true) {
				char[] charArray = generator.current();
				String string = StringGenerator.convertToString(charArray);
				totalCounter++;
				if (display == false) {
					if (startString.contentEquals(string)) display = true;
				} else {
					if (displayCounter > number) display = false;
				}
				
				if (display == true) {
					if (displayCounter % numberPerLine == 0) {
						textAreaManager.appendContent("\n\t");
						imagedAreaManager.appendPlainStringAsNewLine("    ");
					}
					if (displayCounter > 0) {
						textAreaManager.appendContent("  -->  ");
						imagedAreaManager.appendLaTeXStringToLastLine("\\quad\\longrightarrow\\quad ");
					}
					textAreaManager.appendContent(string);
					imagedAreaManager.appendLaTeXStringToLastLine(string);
					if (displayCounter == number) {
						textAreaManager.appendContent(" ... ...\n");
						imagedAreaManager.appendLaTeXStringToLastLine("\\quad\\quad\\cdots\\cdots ");
					}
					displayCounter++;
				}
				if (generator.isLast()) break;
				generator.next();
			}
			
			if (displayCounter <= 0) {
				textAreaManager.appendContent("\t\tThe start string " + startString + " is not in the permutation!\n");
				imagedAreaManager.appendPlainStringAsNewLine("        要输出的起始串 ");
				imagedAreaManager.appendLaTeXStringToLastLine(startString);
				imagedAreaManager.appendPlainStringToLastLine(" 不在生成的排列之中！ ");
			}
			
			textAreaManager.appendContent("\tThere are " + totalCounter + " string(s) for " + length + " permutations on the set B (element number = " + baseSet.length() + ")\n");
			imagedAreaManager.appendPlainStringAsNewLine("    基集（字符个数 ");
			imagedAreaManager.appendLaTeXStringToLastLine("="+baseSet.length());
			imagedAreaManager.appendPlainStringToLastLine("）的 ");
			imagedAreaManager.appendLaTeXStringToLastLine("" + length);
			imagedAreaManager.appendPlainStringToLastLine(" 排列总共有 ");
			imagedAreaManager.appendLaTeXStringToLastLine(message + "="+totalCounter);
			imagedAreaManager.appendPlainStringToLastLine(" 个串 ");
			
			return true;
		}
	}

}
