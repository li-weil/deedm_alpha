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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.deedm.legacy.counting.filter.AndGroupEquationSolverFilter;
import com.deedm.legacy.counting.filter.EquationSolverFilter;
import com.deedm.legacy.counting.filter.EquationSolverRangeFilter;
import com.deedm.legacy.counting.filter.OrGroupEquationSolverFilter;
import com.deedm.legacy.counting.generator.EquationSolverGenerator;
import com.deedm.legacy.graphicAnimation.AnimationArea;
import com.deedm.legacy.guiManager.ImagedTextAreaAnimator;
import com.deedm.legacy.guiManager.ImagedTextAreaManager;
import com.deedm.legacy.guiManager.MainSwingFrame;
import com.deedm.legacy.guiManager.PlainTextAreaManager;

/**
 * @author user
 *
 */
public class CountEquationSolverUIManager extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final int width = Math.max(1* MainSwingFrame.screenWidth/2, 1024)*(MainSwingFrame.screenResolution/96);
	private static final int height = Math.max(2*MainSwingFrame.screenHeight/5, 240)*(MainSwingFrame.screenResolution/96);
	private ImagedTextAreaManager imagedAreaManager = null;
	private PlainTextAreaManager textAreaManager = null;
	private ImagedTextAreaAnimator feedbackAnimator = null;

	private JDialog dialog = null;
	private JButton defaultButton = null;
	private JTextField varNumberField = null;
	private JTextField varSumField = null;
	private JRadioButton logicAndButton = null;
	private JRadioButton logicOrButton = null;
	
	private JTextField index11Field = null;
	private JTextField min11Field = null;
	private JTextField max11Field = null;
	private JTextField index12Field = null;
	private JTextField min12Field = null;
	private JTextField max12Field = null;
	
	private JTextField index21Field = null;
	private JTextField min21Field = null;
	private JTextField max21Field = null;
	private JTextField index22Field = null;
	private JTextField min22Field = null;
	private JTextField max22Field = null;

	private JTextField index31Field = null;
	private JTextField min31Field = null;
	private JTextField max31Field = null;
	private JTextField index32Field = null;
	private JTextField min32Field = null;
	private JTextField max32Field = null;

	private JRadioButton onlyResultButton = null;
	private JRadioButton onlyAcceptButton = null;
	private JRadioButton allSolverButton = null;

	private boolean ok = false;
	private int counter = 0;

	public CountEquationSolverUIManager(PlainTextAreaManager plainManager, ImagedTextAreaManager imageManager) {
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
		varNumberField.setText("");
		varSumField.setText("");

		index11Field.setText("");
		min11Field.setText("");
		max11Field.setText("");

		index12Field.setText("");
		min12Field.setText("");
		max12Field.setText("");

		index21Field.setText("");
		min21Field.setText("");
		max21Field.setText("");

		index22Field.setText("");
		min22Field.setText("");
		max22Field.setText("");

		index31Field.setText("");
		min31Field.setText("");
		max31Field.setText("");

		index32Field.setText("");
		min32Field.setText("");
		max32Field.setText("");
		
		feedbackAnimator.update();
	}
	
	public boolean checkInput() {
		int varNumber = 0;
		String varNumberString = varNumberField.getText();
		try {
			varNumber = Integer.parseInt(varNumberString);
		} catch (Exception exc) {
			JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "输入的变量个数 " + varNumberString + "不是合法的整数！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
			feedbackAnimator.appendPlainStringAsNewLine("输入的变量个数 " + varNumberString + "不是合法的整数！");
			return false;
		}

		int varSum = 0;
		String varSumString = varSumField.getText();
		try {
			varSum = Integer.parseInt(varSumString);
		} catch (Exception exc) {
			JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "输入的变量总和 " + varSumString + "不是合法的整数！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
			feedbackAnimator.appendPlainStringAsNewLine("输入的变量总和 " + varSumString + "不是合法的整数！");
			return false;
		}
		StringBuffer equation = new StringBuffer();
		for (int i = 1; i <= varNumber; i++) {
			if (i > 1) equation.append(" + ");
			equation.append("x_{" + i + "}");
		}
		equation.append(" = " + varSum);

		feedbackAnimator.appendPlainStringAsNewLine("不定方程： ");
		feedbackAnimator.appendLaTeXStringToLastLine(equation.toString());
		
		EquationSolverFilter filter1 = getFilter1();
		EquationSolverFilter filter2 = getFilter2();
		EquationSolverFilter filter3 = getFilter3();
		
		EquationSolverFilter filter = getCombineFilter(filter1, filter2, filter3);
		if (filter == null) {
			feedbackAnimator.appendPlainStringAsNewLine("计数条件：没有，或者没有给出合法的计数条件！");
			return false;
		} 
		feedbackAnimator.appendPlainStringAsNewLine("计数条件：");
		feedbackAnimator.appendLaTeXStringToLastLine(filter.toLaTeXString());
		return true;
	}
	
	private EquationSolverFilter getFilter1() {
		EquationSolverFilter filter = null;

		EquationSolverFilter filter1 = null;
		String indexString = index11Field.getText();
		if (!indexString.contentEquals("")) {
			int index = 0;
			try {
				index = Integer.parseInt(indexString);
				String minString = min11Field.getText();
				int min = -1;
				try {
					min = Integer.parseInt(minString);
				} catch (Exception exc) {
					min = -1;
				}
				String maxString = max11Field.getText();
				int max = -1;
				try {
					max = Integer.parseInt(maxString);
				} catch (Exception exc) {
					max = -1;
				}
				if (min > 0 || max >= 0) filter1 = new EquationSolverRangeFilter(index, min, max);
			} catch (Exception exc) {
				JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "输入的变量下标 " + indexString + "不是合法的整数！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				feedbackAnimator.appendPlainStringAsNewLine("输入的变量下标 " + indexString + "不是合法的整数！");
			}
		}

		EquationSolverFilter filter2 = null;
		indexString = index12Field.getText();
		if (!indexString.contentEquals("")) {
			int index = 0;
			try {
				index = Integer.parseInt(indexString);
				String minString = min12Field.getText();
				int min = -1;
				try {
					min = Integer.parseInt(minString);
				} catch (Exception exc) {
					min = -1;
				}
				String maxString = max12Field.getText();
				int max = -1;
				try {
					max = Integer.parseInt(maxString);
				} catch (Exception exc) {
					max = -1;
				}
				if (min > 0 || max >= 0) filter2 = new EquationSolverRangeFilter(index, min, max);
			} catch (Exception exc) {
				JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "输入的变量下标 " + indexString + "不是合法的整数！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				feedbackAnimator.appendPlainStringAsNewLine("输入的变量下标 " + indexString + "不是合法的整数！");
			}
		}
		
		if (filter1 == null && filter2 == null) return null;
		else if (filter1 != null && filter2 == null) filter = filter1;
		else if (filter1 == null && filter2 != null) filter = filter2;
		else {
			filter = new AndGroupEquationSolverFilter(filter1);
			((AndGroupEquationSolverFilter)filter).addFilter(filter2);
		}
		return filter;
	}
	
	private EquationSolverFilter getFilter2() {
		EquationSolverFilter filter = null;

		EquationSolverFilter filter1 = null;
		String indexString = index21Field.getText();
		if (!indexString.contentEquals("")) {
			int index = 0;
			try {
				index = Integer.parseInt(indexString);
				String minString = min21Field.getText();
				int min = -1;
				try {
					min = Integer.parseInt(minString);
				} catch (Exception exc) {
					min = -1;
				}
				String maxString = max21Field.getText();
				int max = -1;
				try {
					max = Integer.parseInt(maxString);
				} catch (Exception exc) {
					max = -1;
				}
				if (min > 0 || max >= 0) filter1 = new EquationSolverRangeFilter(index, min, max);
			} catch (Exception exc) {
				JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "输入的变量下标 " + indexString + "不是合法的整数！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				feedbackAnimator.appendPlainStringAsNewLine("输入的变量下标 " + indexString + "不是合法的整数！");
			}
		}

		EquationSolverFilter filter2 = null;
		indexString = index22Field.getText();
		if (!indexString.contentEquals("")) {
			int index = 0;
			try {
				index = Integer.parseInt(indexString);
				String minString = min22Field.getText();
				int min = -1;
				try {
					min = Integer.parseInt(minString);
				} catch (Exception exc) {
					min = -1;
				}
				String maxString = max22Field.getText();
				int max = -1;
				try {
					max = Integer.parseInt(maxString);
				} catch (Exception exc) {
					max = -1;
				}
				if (min > 0 || max >= 0) filter2 = new EquationSolverRangeFilter(index, min, max);
			} catch (Exception exc) {
				JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "输入的变量下标 " + indexString + "不是合法的整数！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				feedbackAnimator.appendPlainStringAsNewLine("输入的变量下标 " + indexString + "不是合法的整数！");
			}
		}
		
		if (filter1 == null && filter2 == null) return null;
		else if (filter1 != null && filter2 == null) filter = filter1;
		else if (filter1 == null && filter2 != null) filter = filter2;
		else {
			filter = new AndGroupEquationSolverFilter(filter1);
			((AndGroupEquationSolverFilter)filter).addFilter(filter2);
		}
		return filter;
	}
	
	private EquationSolverFilter getFilter3() {
		EquationSolverFilter filter = null;

		EquationSolverFilter filter1 = null;
		String indexString = index31Field.getText();
		if (!indexString.contentEquals("")) {
			int index = 0;
			try {
				index = Integer.parseInt(indexString);
				String minString = min31Field.getText();
				int min = -1;
				try {
					min = Integer.parseInt(minString);
				} catch (Exception exc) {
					min = -1;
				}
				String maxString = max31Field.getText();
				int max = -1;
				try {
					max = Integer.parseInt(maxString);
				} catch (Exception exc) {
					max = -1;
				}
				if (min > 0 || max >= 0) filter1 = new EquationSolverRangeFilter(index, min, max);
			} catch (Exception exc) {
				JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "输入的变量下标 " + indexString + "不是合法的整数！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				feedbackAnimator.appendPlainStringAsNewLine("输入的变量下标 " + indexString + "不是合法的整数！");
			}
		}

		EquationSolverFilter filter2 = null;
		indexString = index32Field.getText();
		if (!indexString.contentEquals("")) {
			int index = 0;
			try {
				index = Integer.parseInt(indexString);
				String minString = min32Field.getText();
				int min = -1;
				try {
					min = Integer.parseInt(minString);
				} catch (Exception exc) {
					min = -1;
				}
				String maxString = max32Field.getText();
				int max = -1;
				try {
					max = Integer.parseInt(maxString);
				} catch (Exception exc) {
					max = -1;
				}
				if (min > 0 || max >= 0) filter2 = new EquationSolverRangeFilter(index, min, max);
			} catch (Exception exc) {
				JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "输入的变量下标 " + indexString + "不是合法的整数！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				feedbackAnimator.appendPlainStringAsNewLine("输入的变量下标 " + indexString + "不是合法的整数！");
			}
		}
		
		if (filter1 == null && filter2 == null) return null;
		else if (filter1 != null && filter2 == null) filter = filter1;
		else if (filter1 == null && filter2 != null) filter = filter2;
		else {
			filter = new AndGroupEquationSolverFilter(filter1);
			((AndGroupEquationSolverFilter)filter).addFilter(filter2);
		}
		return filter;
	}
	
	private EquationSolverFilter getCombineFilter(EquationSolverFilter filter1, EquationSolverFilter filter2, EquationSolverFilter filter3) {
		if (filter1 == null && filter2 == null && filter3 == null) return null;
		
		EquationSolverFilter filter = null;
		if (logicAndButton.isSelected()) {
			filter = new AndGroupEquationSolverFilter();
			if (filter1 != null) ((AndGroupEquationSolverFilter)filter).addFilter(filter1);
			if (filter2 != null) ((AndGroupEquationSolverFilter)filter).addFilter(filter2);
			if (filter3 != null) ((AndGroupEquationSolverFilter)filter).addFilter(filter3);
		} else {
			filter = new OrGroupEquationSolverFilter();
			if (filter1 != null) ((OrGroupEquationSolverFilter)filter).addFilter(filter1);
			if (filter2 != null) ((OrGroupEquationSolverFilter)filter).addFilter(filter2);
			if (filter3 != null) ((OrGroupEquationSolverFilter)filter).addFilter(filter3);
		}
		return filter;
	}
	
	// 创建输入关系的窗格
	private JPanel createInputSetPanel() {
		// 创建用于输入公式的窗格，使用盒式布局管理
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		// 设置该窗格使用的带标题的边框
 		inputPanel.setBorder(BorderFactory.createTitledBorder("输入计数的方程的变量个数、变量总和以及变量约束条件（变量大于等于某个值且小于等于某个值，如果为空或输入-1则表示不约束，变量下标从1开始）"));

 		JPanel linePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 48, 5));
		// 创建一个标签用于提示输入集合信息
 		JPanel tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		JLabel promptLabel = new JLabel("变量（未知数）个数(N)：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+V可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('N');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		varNumberField = new JTextField(12);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(varNumberField);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(varNumberField);
		linePanel.add(tempPanel);
		
		tempPanel = new JPanel();
		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		promptLabel = new JLabel("变量（未知数）总和(S)：", JLabel.RIGHT);
		promptLabel.setDisplayedMnemonic('S');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		varSumField = new JTextField(12);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(varSumField);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(varSumField);
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
		promptLabel = new JLabel("第一组条件： 第 ", JLabel.RIGHT);
		tempPanel.add(promptLabel);
		index11Field = new JTextField(12);
		tempPanel.add(index11Field);
		promptLabel = new JLabel(" 个变量 大于等于 ", JLabel.RIGHT);
		tempPanel.add(promptLabel);
		min11Field = new JTextField(12);
		tempPanel.add(min11Field);
		promptLabel = new JLabel("且 小于等于 ", JLabel.RIGHT);
		tempPanel.add(promptLabel);
		max11Field = new JTextField(12);
		tempPanel.add(max11Field);
		promptLabel = new JLabel("    而且 ", JLabel.LEFT);
		tempPanel.add(promptLabel);
		linePanel.add(tempPanel);
		
		tempPanel = new JPanel();
		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		promptLabel = new JLabel("  第 ", JLabel.RIGHT);
		tempPanel.add(promptLabel);
		index12Field = new JTextField(12);
		tempPanel.add(index12Field);
		promptLabel = new JLabel("个变量  大于等于 ", JLabel.RIGHT);
		tempPanel.add(promptLabel);
		min12Field = new JTextField(12);
		tempPanel.add(min12Field);
		promptLabel = new JLabel("且 小于等于 ", JLabel.RIGHT);
		tempPanel.add(promptLabel);
		max12Field = new JTextField(12);
		tempPanel.add(max12Field);
		linePanel.add(tempPanel);
		inputPanel.add(linePanel);
		
		linePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
		tempPanel = new JPanel();
		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		promptLabel = new JLabel("第二组条件： 第 ", JLabel.RIGHT);
		tempPanel.add(promptLabel);
		index21Field = new JTextField(12);
		tempPanel.add(index21Field);
		promptLabel = new JLabel(" 个变量 大于等于 ", JLabel.RIGHT);
		tempPanel.add(promptLabel);
		min21Field = new JTextField(12);
		tempPanel.add(min21Field);
		promptLabel = new JLabel("且 小于等于 ", JLabel.RIGHT);
		tempPanel.add(promptLabel);
		max21Field = new JTextField(12);
		tempPanel.add(max21Field);
		promptLabel = new JLabel("      而且 ", JLabel.LEFT);
		tempPanel.add(promptLabel);
		linePanel.add(tempPanel);
		
		tempPanel = new JPanel();
		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		promptLabel = new JLabel("  第 ", JLabel.RIGHT);
		tempPanel.add(promptLabel);
		index22Field = new JTextField(12);
		tempPanel.add(index22Field);
		promptLabel = new JLabel("个变量  大于等于 ", JLabel.RIGHT);
		tempPanel.add(promptLabel);
		min22Field = new JTextField(12);
		tempPanel.add(min22Field);
		promptLabel = new JLabel("且 小于等于 ", JLabel.RIGHT);
		tempPanel.add(promptLabel);
		max22Field = new JTextField(12);
		tempPanel.add(max22Field);
		linePanel.add(tempPanel);
		inputPanel.add(linePanel);
		
		linePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
		tempPanel = new JPanel();
		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		promptLabel = new JLabel("第三组条件： 第 ", JLabel.RIGHT);
		tempPanel.add(promptLabel);
		index31Field = new JTextField(12);
		tempPanel.add(index31Field);
		promptLabel = new JLabel(" 个变量 大于等于 ", JLabel.RIGHT);
		tempPanel.add(promptLabel);
		min31Field = new JTextField(12);
		tempPanel.add(min31Field);
		promptLabel = new JLabel("且 小于等于 ", JLabel.RIGHT);
		tempPanel.add(promptLabel);
		max31Field = new JTextField(12);
		tempPanel.add(max31Field);
		promptLabel = new JLabel("      而且 ", JLabel.LEFT);
		tempPanel.add(promptLabel);
		linePanel.add(tempPanel);
		
		tempPanel = new JPanel();
		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		promptLabel = new JLabel("  第 ", JLabel.RIGHT);
		tempPanel.add(promptLabel);
		index32Field = new JTextField(12);
		tempPanel.add(index32Field);
		promptLabel = new JLabel("个变量  大于等于 ", JLabel.RIGHT);
		tempPanel.add(promptLabel);
		min32Field = new JTextField(12);
		tempPanel.add(min32Field);
		promptLabel = new JLabel("且 小于等于 ", JLabel.RIGHT);
		tempPanel.add(promptLabel);
		max32Field = new JTextField(12);
		tempPanel.add(max32Field);
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
		onlyAcceptButton = new JRadioButton("只给出满足条件的解");
		group.add(onlyAcceptButton);
		allSolverButton = new JRadioButton("给出所有解的搜索情况");
		group.add(allSolverButton);
		infoPanel.add(onlyResultButton);
		infoPanel.add(onlyAcceptButton);
		infoPanel.add(allSolverButton);
		
		// 创建给出教材示例的窗格与按钮
		JPanel buttonPanelTwo = new JPanel();
		buttonPanelTwo.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 0));
 		buttonPanelTwo.setBorder(BorderFactory.createTitledBorder("《离散数学基础》教材示例"));
		
		JButton exampleButton = new JButton("例子8.30");
		exampleButton.setActionCommand("example8_30");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("例子8.31");
		exampleButton.setActionCommand("example8_31");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("问题8.32");
		exampleButton.setActionCommand("example8_32");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("问题8.34");
		exampleButton.setActionCommand("example8_34");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("问题8.35");
		exampleButton.setActionCommand("example8_35");			
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
				doCountSolver();
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
			} else if (command.equals("example8_30")) {
				feedbackAnimator.clearContent();
				clearInput();
				varNumberField.setText("3");
				varSumField.setText("2");
				checkInput();
				feedbackAnimator.update();
			} else if (command.equals("example8_31")) {
				feedbackAnimator.clearContent();
				clearInput();
				varNumberField.setText("3");
				varSumField.setText("6");
				checkInput();
				feedbackAnimator.update();
			} else if (command.equals("example8_32")) {
				feedbackAnimator.clearContent();
				clearInput();
				varNumberField.setText("4");
				varSumField.setText("15");
				index11Field.setText("1");
				min11Field.setText("3");
				index12Field.setText("2");
				min12Field.setText("4");
				checkInput();
				feedbackAnimator.update();
			} else if (command.equals("example8_34")) {
				feedbackAnimator.clearContent();
				clearInput();
				varNumberField.setText("3");
				varSumField.setText("6");
				index11Field.setText("1");
				max11Field.setText("3");
				index12Field.setText("2");
				max12Field.setText("2");
				index21Field.setText("3");
				max21Field.setText("4");
				checkInput();
				feedbackAnimator.update();
			} else if (command.equals("example8_35")) {
				feedbackAnimator.clearContent();
				clearInput();
				varNumberField.setText("5");
				varSumField.setText("15");
				index11Field.setText("1");
				min11Field.setText("2");
				max11Field.setText("5");
				index12Field.setText("2");
				max12Field.setText("2");
				index21Field.setText("3");
				min21Field.setText("1");
				max21Field.setText("4");
				checkInput();
				feedbackAnimator.update();
			} else if (command.equals("clear")) {
				textAreaManager.clearContent();
				imagedAreaManager.clearContent();
				imagedAreaManager.revalidate();
			}
		}
		
		public boolean doCountSolver() {
			int varNumber = 0;
			String varNumberString = varNumberField.getText();
			try {
				varNumber = Integer.parseInt(varNumberString);
			} catch (Exception exc) {
				JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "输入的变量个数 " + varNumberString + "不是合法的整数！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				feedbackAnimator.appendPlainStringAsNewLine("输入的变量个数 " + varNumberString + "不是合法的整数！");
				return false;
			}

			int varSum = 0;
			String varSumString = varSumField.getText();
			try {
				varSum = Integer.parseInt(varSumString);
			} catch (Exception exc) {
				JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "输入的变量总和 " + varSumString + "不是合法的整数！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				feedbackAnimator.appendPlainStringAsNewLine("输入的变量总和 " + varSumString + "不是合法的整数！");
				return false;
			}
			StringBuffer equation = new StringBuffer();
			for (int i = 1; i <= varNumber; i++) {
				if (i > 1) equation.append(" + ");
				equation.append("x_{" + i + "}");
			}
			equation.append(" = " + varSum);

			feedbackAnimator.appendPlainStringAsNewLine("不定方程： ");
			feedbackAnimator.appendLaTeXStringToLastLine(equation.toString());
			textAreaManager.appendContent(counter + " : Equation " + equation.toString());
			imagedAreaManager.appendPlainStringAsNewLine(counter + "：不定方程： ");
			imagedAreaManager.appendLaTeXStringToLastLine(equation.toString());
			
			EquationSolverFilter filter1 = getFilter1();
			EquationSolverFilter filter2 = getFilter2();
			EquationSolverFilter filter3 = getFilter3();
			
			EquationSolverFilter filter = getCombineFilter(filter1, filter2, filter3);
			if (filter == null) {
				feedbackAnimator.appendPlainStringAsNewLine("计数条件：没有，或者没有给出合法的计数条件！");
				textAreaManager.appendContent("\tFilter condition: None\n");
				imagedAreaManager.appendPlainStringAsNewLine("    解的约束条件：没有，或者没有给出合法的计数条件！");
			} else {
				feedbackAnimator.appendPlainStringAsNewLine("解的约束条件：");
				feedbackAnimator.appendLaTeXStringToLastLine(filter.toLaTeXString());
				textAreaManager.appendContent("\tFilter condition: " +filter.toLaTeXString()+"\n");
				imagedAreaManager.appendPlainStringAsNewLine("    解的约束条件：");
				imagedAreaManager.appendLaTeXStringToLastLine(filter.toLaTeXString());
			}

			boolean detailed = false;
			boolean onlyAccept = false;
			if (onlyAcceptButton.isSelected()) onlyAccept = true;
			else if (allSolverButton.isSelected()) detailed = true;

			int result = 0;
			int totalCounter = 0;
			EquationSolverGenerator generator = new EquationSolverGenerator(varNumber, varSum);
			generator.first();
			int[] solver = generator.current();
			totalCounter = totalCounter + 1; 
			boolean accept = true;
			if (filter != null) accept = filter.accept(solver);
			if (accept) {
				result++;
				if (detailed || onlyAccept) {
					String solverString = EquationSolverGenerator.convertSolverToString(solver);
					textAreaManager.appendContent("No. " + totalCounter + ", accepted " + result + " solver: " + solverString + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    第 " + totalCounter + " 个解，第 " + result + " 个满足条件的解：");
					imagedAreaManager.appendLaTeXStringToLastLine(solverString);
				} 
			} else if (detailed) {
				String solverString = EquationSolverGenerator.convertSolverToString(solver);
				textAreaManager.appendContent("No. " + totalCounter + ", NOT accepted solver: " + solverString + "\n");
				imagedAreaManager.appendPlainStringAsNewLine("    第 " + totalCounter + " 个解，不满足约束条件：");
				imagedAreaManager.appendLaTeXStringToLastLine(solverString);
			} 
			while (!generator.isLast()) {
				generator.next();
				solver = generator.current();
				totalCounter = totalCounter + 1; 
				accept = true;
				if (filter != null) accept = filter.accept(solver);
				if (accept) {
					result++;
					if (detailed || onlyAccept) {
						String solverString = EquationSolverGenerator.convertSolverToString(solver);
						textAreaManager.appendContent("No. " + totalCounter + ", accepted " + result + " solver: " + solverString + "\n");
						imagedAreaManager.appendPlainStringAsNewLine("    第 " + totalCounter + " 个解，第 " + result + " 个满足条件的解：");
						imagedAreaManager.appendLaTeXStringToLastLine(solverString);
					}
				} else if (detailed) {
					String solverString = EquationSolverGenerator.convertSolverToString(solver);
					textAreaManager.appendContent("No. " + totalCounter + ", NOT accepted solver: " + solverString + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    第 " + totalCounter + " 个解，不满足约束条件：");
					imagedAreaManager.appendLaTeXStringToLastLine(solverString);
				}
			}
			
			textAreaManager.appendContent("\tThere are " + result + " solvers(s) satisfy the filter condition in the total " + totalCounter + " solver(s)!\n");
			imagedAreaManager.appendPlainStringAsNewLine("    计数结果：有 ");
			imagedAreaManager.appendLaTeXStringToLastLine(""+result);
			imagedAreaManager.appendPlainStringToLastLine(" 个满足条件的解，一共检查了 ");
			String message = "C(" + varNumber + "-1+" + varSum + ", " + varSum + ") = C(" + (varNumber-1+varSum) + ", " + varSum + ") = " + totalCounter; 
			imagedAreaManager.appendLaTeXStringToLastLine(message);
			imagedAreaManager.appendPlainStringToLastLine(" 个解！");
			
			return true;
		}
	}
}
