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
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.deedm.legacy.counting.filter.AndGroupIntegerFilter;
import com.deedm.legacy.counting.filter.IntegerDivisionFilter;
import com.deedm.legacy.counting.filter.IntegerFilter;
import com.deedm.legacy.counting.filter.OrGroupIntegerFilter;
import com.deedm.legacy.graphicAnimation.AnimationArea;
import com.deedm.legacy.guiManager.ImagedTextAreaAnimator;
import com.deedm.legacy.guiManager.ImagedTextAreaManager;
import com.deedm.legacy.guiManager.MainSwingFrame;
import com.deedm.legacy.guiManager.PlainTextAreaManager;

/**
 * @author user
 *
 */
public class CountIntegerUIManager extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final int width = Math.max(1* MainSwingFrame.screenWidth/2, 1024)*(MainSwingFrame.screenResolution/96);
	private static final int height = Math.max(2*MainSwingFrame.screenHeight/5, 240)*(MainSwingFrame.screenResolution/96);
	private ImagedTextAreaManager imagedAreaManager = null;
	private PlainTextAreaManager textAreaManager = null;
	private ImagedTextAreaAnimator feedbackAnimator = null;

	private JDialog dialog = null;
	private JButton defaultButton = null;
	private JTextField startField = null;
	private JTextField endField = null;
	private JRadioButton logicAndButton = null;
	private JRadioButton logicOrButton = null;
	
	private String[] divideFlag = {"可以整除", "不能整除"};
	
	private JTextField integers11Field = null;
	private JComboBox<String> divideFlag11Box = null;
	private JTextField integers12Field = null;
	private JComboBox<String> divideFlag12Box = null;
	
	private JTextField integers21Field = null;
	private JComboBox<String> divideFlag21Box = null;
	private JTextField integers22Field = null;
	private JComboBox<String> divideFlag22Box = null;

	private JTextField integers31Field = null;
	private JComboBox<String> divideFlag31Box = null;
	private JTextField integers32Field = null;
	private JComboBox<String> divideFlag32Box = null;

	private JRadioButton onlyResultButton = null;
	private JRadioButton onlyAcceptButton = null;
	private JRadioButton accept50Button = null;
	private JRadioButton part100Button = null;
	private JRadioButton allStringButton = null;

	private boolean ok = false;
	private int counter = 0;

	public CountIntegerUIManager(PlainTextAreaManager plainManager, ImagedTextAreaManager imageManager) {
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
		startField.setText("");
		endField.setText("");

		integers11Field.setText("");
		integers12Field.setText("");
		integers21Field.setText("");
		integers22Field.setText("");
		integers31Field.setText("");
		integers32Field.setText("");
		
		feedbackAnimator.update();
	}
	
	public boolean checkInput() {
		int start = 0;
		String startString = startField.getText();
		try {
			start = Integer.parseInt(startString);
		} catch (Exception exc) {
			JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "输入的起始整数 " + startString + "不是合法的整数！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
			feedbackAnimator.appendPlainStringAsNewLine("输入的起始整数 " + startString + "不是合法的整数！");
			return false;
		}

		int end = 0;
		String endString = endField.getText();
		try {
			end = Integer.parseInt(endString);
		} catch (Exception exc) {
			JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "输入的终止整数 " + endString + "不是合法的整数！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
			feedbackAnimator.appendPlainStringAsNewLine("输入的终止整数 " + endString + "不是合法的整数！");
			return false;
		}

		feedbackAnimator.appendPlainStringAsNewLine("整数范围：从 ");
		feedbackAnimator.appendLaTeXStringToLastLine(""+start);
		feedbackAnimator.appendPlainStringToLastLine(" 到 ");
		feedbackAnimator.appendLaTeXStringToLastLine(""+end);
		
		IntegerFilter filter1 = getFilter1();
		IntegerFilter filter2 = getFilter2();
		IntegerFilter filter3 = getFilter3();
		
		IntegerFilter filter = getCombineFilter(filter1, filter2, filter3);
		if (filter == null) {
			feedbackAnimator.appendPlainStringAsNewLine("计数条件：没有，或者没有给出合法的计数条件！");
			return false;
		} 
		feedbackAnimator.appendPlainStringAsNewLine("计数条件：");
		feedbackAnimator.appendLaTeXStringToLastLine(filter.toLaTeXString());
		return true;
	}
	
	private IntegerFilter getFilter1() {
		IntegerFilter filter1 = null;

		IntegerFilter filter11 = null;
		String integers11String = integers11Field.getText();
		if (!integers11String.contentEquals("")) {
			int[] integers = getIntegers(integers11String);
			if (integers == null) {
				JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "输入的整数 " + integers11String + " 含有非法的整数串，或者含有整数0！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
			} else {
				if (divideFlag11Box.getSelectedIndex() == 0) filter11 = new IntegerDivisionFilter(IntegerDivisionFilter.DIVIDED, integers);
				else filter11 = new IntegerDivisionFilter(IntegerDivisionFilter.NOT_DIVIDED, integers);
			}
		}

		IntegerFilter filter12 = null;
		String integers12String = integers12Field.getText();
		if (!integers12String.contentEquals("")) {
			int[] integers = getIntegers(integers12String);
			if (integers == null) {
				JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "输入的整数 " + integers12String + " 含有非法的整数串，或者含有整数0！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
			} else {
				if (divideFlag12Box.getSelectedIndex() == 0) filter12 = new IntegerDivisionFilter(IntegerDivisionFilter.DIVIDED, integers);
				else filter12 = new IntegerDivisionFilter(IntegerDivisionFilter.NOT_DIVIDED, integers);
			}
		}
		
		if (filter11 == null && filter12 == null) return null;
		else if (filter11 != null && filter12 == null) filter1 = filter11;
		else if (filter11 == null && filter12 != null) filter1 = filter12;
		else {
			filter1 = new AndGroupIntegerFilter(filter11);
			((AndGroupIntegerFilter)filter1).addFilter(filter12);
		}
		return filter1;
	}
	
	private IntegerFilter getFilter2() {
		IntegerFilter filter2 = null;

		IntegerFilter filter21 = null;
		String integers21String = integers21Field.getText();
		if (!integers21String.contentEquals("")) {
			int[] integers = getIntegers(integers21String);
			if (integers == null) {
				JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "输入的整数 " + integers21String + " 含有非法的整数串，或者含有整数0！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
			} else {
				if (divideFlag21Box.getSelectedIndex() == 0) filter21 = new IntegerDivisionFilter(IntegerDivisionFilter.DIVIDED, integers);
				else filter21 = new IntegerDivisionFilter(IntegerDivisionFilter.NOT_DIVIDED, integers);
			}
		}

		IntegerFilter filter22 = null;
		String integers22String = integers22Field.getText();
		if (!integers22String.contentEquals("")) {
			int[] integers = getIntegers(integers22String);
			if (integers == null) {
				JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "输入的整数 " + integers22String + " 含有非法的整数串，或者含有整数0！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
			} else {
				if (divideFlag22Box.getSelectedIndex() == 0) filter22 = new IntegerDivisionFilter(IntegerDivisionFilter.DIVIDED, integers);
				else filter22 = new IntegerDivisionFilter(IntegerDivisionFilter.NOT_DIVIDED, integers);
			}
		}
		
		if (filter21 == null && filter22 == null) return null;
		else if (filter21 != null && filter22 == null) filter2 = filter21;
		else if (filter21 == null && filter22 != null) filter2 = filter22;
		else {
			filter2 = new AndGroupIntegerFilter(filter21);
			((AndGroupIntegerFilter)filter2).addFilter(filter22);
		}
		return filter2;
	}
	
	private IntegerFilter getFilter3() {
		IntegerFilter filter3 = null;

		IntegerFilter filter31 = null;
		String integers31String = integers31Field.getText();
		if (!integers31String.contentEquals("")) {
			int[] integers = getIntegers(integers31String);
			if (integers == null) {
				JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "输入的整数 " + integers31String + " 含有非法的整数串，或者含有整数0！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
			} else {
				if (divideFlag31Box.getSelectedIndex() == 0) filter31 = new IntegerDivisionFilter(IntegerDivisionFilter.DIVIDED, integers);
				else filter31 = new IntegerDivisionFilter(IntegerDivisionFilter.NOT_DIVIDED, integers);
			}
		}

		IntegerFilter filter32 = null;
		String integers32String = integers32Field.getText();
		if (!integers32String.contentEquals("")) {
			int[] integers = getIntegers(integers32String);
			if (integers == null) {
				JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "输入的整数 " + integers32String + " 含有非法的整数串，或者含有整数0！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
			} else {
				if (divideFlag32Box.getSelectedIndex() == 0) filter32 = new IntegerDivisionFilter(IntegerDivisionFilter.DIVIDED, integers);
				else filter32 = new IntegerDivisionFilter(IntegerDivisionFilter.NOT_DIVIDED, integers);
			}
		}
		
		if (filter31 == null && filter32 == null) return null;
		else if (filter31 != null && filter32 == null) filter3 = filter31;
		else if (filter31 == null && filter32 != null) filter3 = filter32;
		else {
			filter3 = new AndGroupIntegerFilter(filter31);
			((AndGroupIntegerFilter)filter3).addFilter(filter32);
		}
		return filter3;
	}
	
	private IntegerFilter getCombineFilter(IntegerFilter filter1, IntegerFilter filter2, IntegerFilter filter3) {
		if (filter1 == null && filter2 == null && filter3 == null) return null;
		
		IntegerFilter filter = null;
		if (logicAndButton.isSelected()) {
			filter = new AndGroupIntegerFilter();
			if (filter1 != null) ((AndGroupIntegerFilter)filter).addFilter(filter1);
			if (filter2 != null) ((AndGroupIntegerFilter)filter).addFilter(filter2);
			if (filter3 != null) ((AndGroupIntegerFilter)filter).addFilter(filter3);
		} else {
			filter = new OrGroupIntegerFilter();
			if (filter1 != null) ((OrGroupIntegerFilter)filter).addFilter(filter1);
			if (filter2 != null) ((OrGroupIntegerFilter)filter).addFilter(filter2);
			if (filter3 != null) ((OrGroupIntegerFilter)filter).addFilter(filter3);
		}
		return filter;
	}
	
	private int[] getIntegers(String integerString) {
		String[] integerstringArray = integerString.split(",");
		int[] integers = new int[integerstringArray.length];
		for (int i = 0; i < integerstringArray.length; i++) {
			integerstringArray[i] = integerstringArray[i].trim();
			try {
				integers[i] = Integer.parseInt(integerstringArray[i]);
				if (integers[i] == 0) return null;
			} catch (Exception exc) {
				return null;
			}
		}
		return integers;
	}
	
	// 创建输入关系的窗格
	private JPanel createInputSetPanel() {
		// 创建用于输入公式的窗格，使用盒式布局管理
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		// 设置该窗格使用的带标题的边框
 		inputPanel.setBorder(BorderFactory.createTitledBorder("输入计数的整数范围及计数条件（计数条件只限于整除性质）"));

 		JPanel linePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 48, 5));
		// 创建一个标签用于提示输入集合信息
 		JPanel tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		JLabel promptLabel = new JLabel("起始整数(S)（计数范围包含该整数）：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+V可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('S');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		startField = new JTextField(12);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(startField);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(startField);
		linePanel.add(tempPanel);
		
		tempPanel = new JPanel();
		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		promptLabel = new JLabel("终止整数(E)（计数范围包含该整数）：", JLabel.RIGHT);
		promptLabel.setDisplayedMnemonic('E');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		endField = new JTextField(12);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(endField);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(endField);
		linePanel.add(tempPanel);
		
		tempPanel = new JPanel();
		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		// 创建一个标签用于提示输入性别信息
		promptLabel = new JLabel("下面每组条件之间的逻辑关系：", JLabel.RIGHT);
		// 创建用于将下两个广播按钮捆绑成一组的按钮组对象，同一组的广播按钮只能选中一个，
		// 不同组的广播按钮则可以同时选中不同的广播按钮
		ButtonGroup group = new ButtonGroup();
		// 创建选择性别的广播按钮
		logicAndButton = new JRadioButton("逻辑与关系");
		logicAndButton.setSelected(true);		// 缺省时选中该广播按钮
		group.add(logicAndButton);
		logicOrButton = new JRadioButton("逻辑或关系");
		group.add(logicOrButton);
		promptLabel.setLabelFor(logicAndButton);
		tempPanel.add(promptLabel);
		tempPanel.add(logicAndButton);			// 注意是添加广播按钮组件
		tempPanel.add(logicOrButton);		// 而不是添加按钮组对象
		linePanel.add(tempPanel);
		inputPanel.add(linePanel);
		
		linePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
		tempPanel = new JPanel();
		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		promptLabel = new JLabel("第一组条件： ", JLabel.RIGHT);
		divideFlag11Box = new JComboBox<String>(divideFlag);
		integers11Field = new JTextField(20);
		tempPanel.add(promptLabel);
		tempPanel.add(divideFlag11Box);
		tempPanel.add(integers11Field);
		promptLabel = new JLabel(" 这些整数之一         而且 ", JLabel.LEFT);
		tempPanel.add(promptLabel);
		linePanel.add(tempPanel);
		
		divideFlag12Box = new JComboBox<String>(divideFlag);
		integers12Field = new JTextField(20);
		tempPanel.add(divideFlag12Box);
		tempPanel.add(integers12Field);
		promptLabel = new JLabel(" 这些整数之一 ", JLabel.LEFT);
		tempPanel.add(promptLabel);
		linePanel.add(tempPanel);
		inputPanel.add(linePanel);
		
		linePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
		tempPanel = new JPanel();
		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		promptLabel = new JLabel("第二组条件： ", JLabel.RIGHT);
		divideFlag21Box = new JComboBox<String>(divideFlag);
		integers21Field = new JTextField(20);
		tempPanel.add(promptLabel);
		tempPanel.add(divideFlag21Box);
		tempPanel.add(integers21Field);
		promptLabel = new JLabel(" 这些整数之一         而且 ", JLabel.LEFT);
		tempPanel.add(promptLabel);
		linePanel.add(tempPanel);
		
		divideFlag22Box = new JComboBox<String>(divideFlag);
		integers22Field = new JTextField(20);
		tempPanel.add(divideFlag22Box);
		tempPanel.add(integers22Field);
		promptLabel = new JLabel(" 这些整数之一 ", JLabel.LEFT);
		tempPanel.add(promptLabel);
		linePanel.add(tempPanel);
		inputPanel.add(linePanel);
		
		linePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
		tempPanel = new JPanel();
		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		promptLabel = new JLabel("第三组条件： ", JLabel.RIGHT);
		integers31Field = new JTextField(20);
		divideFlag31Box = new JComboBox<String>(divideFlag);
		tempPanel.add(promptLabel);
		tempPanel.add(divideFlag31Box);
		tempPanel.add(integers31Field);
		promptLabel = new JLabel(" 这些整数之一         而且 ", JLabel.LEFT);
		tempPanel.add(promptLabel);
		linePanel.add(tempPanel);
		
		divideFlag32Box = new JComboBox<String>(divideFlag);
		integers32Field = new JTextField(20);
		tempPanel.add(divideFlag32Box);
		tempPanel.add(integers32Field);
		promptLabel = new JLabel(" 这些整数之一 ", JLabel.LEFT);
		tempPanel.add(promptLabel);
		linePanel.add(tempPanel);
		inputPanel.add(linePanel);
		
		JPanel displayPanel = new JPanel();
		displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
		// 设置该窗格使用的带标题的边框
		displayPanel.setBorder(BorderFactory.createTitledBorder("你输入整数范围以及计数条件如下（计数条件中的n表示待判断的整数）："));
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
		JButton actButton = new JButton("开始计数(Y)");
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

		// 创建给出变量集信息和范式信息的窗格与相关组件
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 25, 0));
		infoPanel.setBorder(BorderFactory.createTitledBorder("选择计数结果的展示方式："));
		ButtonGroup group = new ButtonGroup();
		// 创建选择性别的广播按钮
		onlyResultButton = new JRadioButton("只给出计数结果");
		onlyResultButton.setSelected(true);		// 缺省时选中该广播按钮
		group.add(onlyResultButton);
		accept50Button = new JRadioButton("给出至多50个接受的整数");
		group.add(accept50Button);
		part100Button = new JRadioButton("给出至多100个整数的情况");
		group.add(part100Button);
		onlyAcceptButton = new JRadioButton("只给出满足条件的整数");
		group.add(onlyAcceptButton);
		allStringButton = new JRadioButton("给出范围内所有整数");
		group.add(allStringButton);
		infoPanel.add(onlyResultButton);
		infoPanel.add(accept50Button);
		infoPanel.add(part100Button);
		infoPanel.add(onlyAcceptButton);
		infoPanel.add(allStringButton);
		
		// 创建给出教材示例的窗格与按钮
		JPanel buttonPanelTwo = new JPanel();
		buttonPanelTwo.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 0));
 		buttonPanelTwo.setBorder(BorderFactory.createTitledBorder("《离散数学基础》教材示例"));
		
		JButton exampleButton = new JButton("问题8.11");
		exampleButton.setActionCommand("example8_11");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("问题8.12(1)");
		exampleButton.setActionCommand("example8_12_1");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("问题8.12(2)");
		exampleButton.setActionCommand("example8_12_2");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("问题8.12(3)");
		exampleButton.setActionCommand("example8_12_3");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

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
				feedbackAnimator.clearContent();
				ok = true;
				counter = counter + 1;
				doCountInteger();
				feedbackAnimator.update();
				imagedAreaManager.revalidate();
			} else if (command.equals("check")) {
				feedbackAnimator.clearContent();
				checkInput();
				feedbackAnimator.update();
			} else if (command.equals("cancel")) {
				ok = false;
				dialog.setVisible(false);
			} else if (command.equals("remove")) {
				clearInput();
			} else if (command.equals("example8_11")) {
				feedbackAnimator.clearContent();
				clearInput();
				startField.setText("1");
				endField.setText("1000");
				divideFlag11Box.setSelectedIndex(0);
				integers11Field.setText("3, 5");
				logicAndButton.setSelected(true);
				checkInput();
				feedbackAnimator.update();
			} else if (command.equals("example8_12_1")) {
				feedbackAnimator.clearContent();
				clearInput();
				startField.setText("100");
				endField.setText("999");
				divideFlag11Box.setSelectedIndex(1);
				integers11Field.setText("3");
				logicAndButton.setSelected(true);
				checkInput();
				feedbackAnimator.update();
			} else if (command.equals("example8_12_2")) {
				feedbackAnimator.clearContent();
				clearInput();
				startField.setText("100");
				endField.setText("999");
				divideFlag11Box.setSelectedIndex(1);
				integers11Field.setText("3, 5");
				logicAndButton.setSelected(true);
				checkInput();
				feedbackAnimator.update();
			} else if (command.equals("example8_12_3")) {
				feedbackAnimator.clearContent();
				clearInput();
				startField.setText("100");
				endField.setText("999");
				divideFlag11Box.setSelectedIndex(0);
				integers11Field.setText("3");
				divideFlag12Box.setSelectedIndex(1);
				integers12Field.setText("5");
				logicAndButton.setSelected(true);
				checkInput();
				feedbackAnimator.update();
			} else if (command.equals("clear")) {
				textAreaManager.clearContent();
				imagedAreaManager.clearContent();
				imagedAreaManager.revalidate();
			}
		}
		
		public boolean doCountInteger() {
			int start = 0;
			String startString = startField.getText();
			try {
				start = Integer.parseInt(startString);
			} catch (Exception exc) {
				feedbackAnimator.appendPlainStringAsNewLine("输入的起始整数 " + startString + "不是合法的整数！");
				return false;
			}

			int end = 0;
			String endString = endField.getText();
			try {
				end = Integer.parseInt(endString);
			} catch (Exception exc) {
				feedbackAnimator.appendPlainStringAsNewLine("输入的终止整数 " + endString + "不是合法的整数！");
				return false;
			}

			feedbackAnimator.appendPlainStringAsNewLine("整数范围：从 ");
			feedbackAnimator.appendLaTeXStringToLastLine(""+start);
			feedbackAnimator.appendPlainStringToLastLine(" 到 ");
			feedbackAnimator.appendLaTeXStringToLastLine(""+end);
			
			textAreaManager.appendContent(counter + " : Counting integers from " + start + " to " + end);
			imagedAreaManager.appendPlainStringAsNewLine(counter + "：整数范围：从 ");
			imagedAreaManager.appendLaTeXStringToLastLine(""+start);
			imagedAreaManager.appendPlainStringToLastLine(" 到 ");
			imagedAreaManager.appendLaTeXStringToLastLine(""+end);
			
			IntegerFilter filter1 = getFilter1();
			IntegerFilter filter2 = getFilter2();
			IntegerFilter filter3 = getFilter3();
			
			IntegerFilter filter = getCombineFilter(filter1, filter2, filter3);
			if (filter == null) {
				feedbackAnimator.appendPlainStringAsNewLine("计数条件：没有，或者没有给出合法的计数条件！");
				textAreaManager.appendContent(", filter condition: none (or do not give legal conditions!\n");
				imagedAreaManager.appendPlainStringToLastLine("，计数条件：没有，或者没有给出合法的计数条件！");
			} else {
				feedbackAnimator.appendPlainStringAsNewLine("计数条件：");
				feedbackAnimator.appendLaTeXStringToLastLine(filter.toLaTeXString());
				textAreaManager.appendContent(", filter condition: " + filter.toLaTeXString() + "\n");
				imagedAreaManager.appendPlainStringToLastLine("，计数条件：");
				imagedAreaManager.appendLaTeXStringToLastLine(filter.toLaTeXString());
			}

			boolean detailed = false;
			boolean onlyAccept = false;
			boolean accept50 = false;
			boolean part100 = false;
			if (onlyAcceptButton.isSelected()) onlyAccept = true;
			else if (allStringButton.isSelected()) detailed = true;
			else if (accept50Button.isSelected()) accept50 = true;
			else if (part100Button.isSelected()) part100 = true;

			int result = 0;
			int totalCounter = 0;
			for (int integer = start; integer <= end; integer++) {
				totalCounter++;
				String acceptMessage = "NOT accepted";
				boolean accepted = true;
				if (filter != null) accepted = filter.accept(integer);
				if (accepted) {
					result++;
					acceptMessage = "accepted " + result;
				}
				if (detailed || onlyAccept) {
					textAreaManager.appendContent("\t" + totalCounter + " : " + integer + ", " + acceptMessage + "\n");
				} else if ((part100 && totalCounter <= 100) || accept50 && result <= 50) {
					textAreaManager.appendContent("\t" + totalCounter + " : " + integer + ", " + acceptMessage + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    ");
					imagedAreaManager.appendLaTeXStringToLastLine(totalCounter + " : " + integer + ", \\textrm{" + acceptMessage + "}");
					if ((part100 && totalCounter == 100) || accept50 && result == 50) {
						textAreaManager.appendContent("\t\t\t\t ... ... \n");
						imagedAreaManager.appendPlainStringAsNewLine("            ");
						imagedAreaManager.appendLaTeXStringToLastLine("\\vdots ");
					}
				}
			}
			
			textAreaManager.appendContent("\tThere are " + result + " integers(s) satisfy the filter condition in the total " + totalCounter + " integer(s)!\n");
			imagedAreaManager.appendPlainStringAsNewLine("    计数结果：有 ");
			imagedAreaManager.appendLaTeXStringToLastLine(""+result);
			imagedAreaManager.appendPlainStringToLastLine(" 个整数满足计数条件，一共检查了 ");
			String message = end + " - " + start + " + 1 = " + totalCounter;
			imagedAreaManager.appendLaTeXStringToLastLine(message);
			imagedAreaManager.appendPlainStringToLastLine(" 个整数！");
			
			return true;
		}
	}
}
