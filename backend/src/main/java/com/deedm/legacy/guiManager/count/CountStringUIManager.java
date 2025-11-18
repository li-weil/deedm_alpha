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

import com.deedm.legacy.counting.filter.AndGroupStringFilter;
import com.deedm.legacy.counting.filter.OrGroupStringFilter;
import com.deedm.legacy.counting.filter.StringFilter;
import com.deedm.legacy.counting.filter.StringLocationFilter;
import com.deedm.legacy.counting.filter.StringSubstringFilter;
import com.deedm.legacy.counting.generator.PermutationGenerator;
import com.deedm.legacy.counting.generator.StringGenerator;
import com.deedm.legacy.graphicAnimation.AnimationArea;
import com.deedm.legacy.guiManager.ImagedTextAreaAnimator;
import com.deedm.legacy.guiManager.ImagedTextAreaManager;
import com.deedm.legacy.guiManager.MainSwingFrame;
import com.deedm.legacy.guiManager.PlainTextAreaManager;
import com.deedm.legacy.setrelfun.Set;
import com.deedm.legacy.setrelfun.SetrelfunUtil;

public class CountStringUIManager extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final int width = Math.max(3* MainSwingFrame.screenWidth/5, 1024)*(MainSwingFrame.screenResolution/96);
	private static final int height = Math.max(2*MainSwingFrame.screenHeight/5, 240)*(MainSwingFrame.screenResolution/96);
	private ImagedTextAreaManager imagedAreaManager = null;
	private PlainTextAreaManager textAreaManager = null;
	private ImagedTextAreaAnimator feedbackAnimator = null;

	private JDialog dialog = null;
	private JButton defaultButton = null;
	private JTextField baseSetField = null;
	private JTextField lengthField = null;
	private JRadioButton repetitionButton = null;
	private JRadioButton nonrepButton = null;
	
	private String[] locationFlag = {"必须", "不能"};
	private String[] substringFlag = {"恰好", "至少", "至多", "不能"};
	
	private JTextField location1Field = null;
	private JComboBox<String> locationFlag1Box = null;
	private JTextField locationChar1Field = null;
	private JComboBox<String> substringFlag1Box = null;
	private JTextField number1Field = null;
	private JTextField substring1Field = null;

	private JTextField location2Field = null;
	private JComboBox<String> locationFlag2Box = null;
	private JTextField locationChar2Field = null;
	private JComboBox<String> substringFlag2Box = null;
	private JTextField number2Field = null;
	private JTextField substring2Field = null;
	
	private JTextField location3Field = null;
	private JComboBox<String> locationFlag3Box = null;
	private JTextField locationChar3Field = null;
	private JComboBox<String> substringFlag3Box = null;
	private JTextField number3Field = null;
	private JTextField substring3Field = null;
	
	private JComboBox<String> group1Box = null;
	private JComboBox<String> group2Box = null;
	private String[] logicString = {"而且", "或者"};
	
	private JRadioButton onlyResultButton = null;
	private JRadioButton onlyAcceptButton = null;
	private JRadioButton accept50Button = null;
	private JRadioButton part100Button = null;
	private JRadioButton allStringButton = null;

	private boolean ok = false;
	private int counter = 0;

	public CountStringUIManager(PlainTextAreaManager plainManager, ImagedTextAreaManager imageManager) {
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

		location1Field.setText("");
		locationChar1Field.setText("");
		number1Field.setText("");
		substring1Field.setText("");

		location2Field.setText("");
		locationChar2Field.setText("");
		number2Field.setText("");
		substring2Field.setText("");

		location3Field.setText("");
		locationChar3Field.setText("");
		number3Field.setText("");
		substring3Field.setText("");

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
		} catch (Exception exc) {
			JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "输入的字符串长度 " + lengthString + "不是合法的整数！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
			feedbackAnimator.appendPlainStringAsNewLine("输入的字符串长度 " + lengthString + "不是合法的整数！");
			return false;
		}
		feedbackAnimator.appendPlainStringAsNewLine("字符串基集：");
		Set baseSet = new Set(elements);
		feedbackAnimator.appendLaTeXStringToLastLine("B = " + baseSet.toLaTeXString());
		feedbackAnimator.appendPlainStringToLastLine("，长度：");
		feedbackAnimator.appendLaTeXStringToLastLine("n = " + length);
		if (repetitionButton.isSelected()) feedbackAnimator.appendPlainStringToLastLine("，计数允许字符重复的串：");
		else feedbackAnimator.appendPlainStringToLastLine("，计数不允许字符重复的串：");
		
		StringFilter filter1 = getFilter1();
		StringFilter filter2 = getFilter2();
		StringFilter filter3 = getFilter3();
		
		StringFilter filter = getCombineFilter(filter1, filter2, filter3);
		if (filter == null) {
			feedbackAnimator.appendPlainStringAsNewLine("计数条件：没有，或者没有给出合法的计数条件！");
			return false;
		} 
		feedbackAnimator.appendPlainStringAsNewLine("计数条件：");
		feedbackAnimator.appendLaTeXStringToLastLine(filter.toLaTeXString());
		return true;
	}
	
	private StringFilter getFilter1() {
		StringFilter locationFilter1 = null;
		StringFilter substringFilter1 = null;
		
		String locationCharString = locationChar1Field.getText();
		if (!locationCharString.contentEquals("")) {
			int location = 0;
			String locationString = location1Field.getText();
			try {
				location = Integer.parseInt(locationString);
			} catch (Exception exc) {
				// 弹出一窗口显示一些信息
				JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "第一组条件中的位置 " + locationString + "不是合法的整数!", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				return null;
			}
			char[] charArray = getLocationChars(locationCharString);
			int flag = StringLocationFilter.APPEAR;
			if (locationFlag1Box.getSelectedIndex() == 1) flag = StringLocationFilter.NOT_APPEAR; 
			locationFilter1 = new StringLocationFilter(flag, location, charArray);
		}
		String substringString = substring1Field.getText();
		if (!substringString.contentEquals("")) {
			int flag = StringSubstringFilter.EXACTLY;
			if (substringFlag1Box.getSelectedIndex() == 1) flag = StringSubstringFilter.AT_LEAST;
			else if (substringFlag1Box.getSelectedIndex() == 2) flag = StringSubstringFilter.NO_MORE_THAN;
			else if (substringFlag1Box.getSelectedIndex() == 3) flag = StringSubstringFilter.NOT_CONTAIN;

			int number = 0;
			String numberString = number1Field.getText();
			try {
				number = Integer.parseInt(numberString);
			} catch (Exception exc) {
				// 弹出一窗口显示一些信息
				JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "第一组条件中的个数 " + numberString + "不是合法的整数!", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				return null;
			}
			
			String[] substrings = getSubstrings(substringString);
			substringFilter1 = new StringSubstringFilter(flag, number, substrings);
		}

		StringFilter filter1 = null;
		if (locationFilter1 != null && substringFilter1 != null) {
			filter1 = new AndGroupStringFilter(locationFilter1);
			((AndGroupStringFilter)filter1).addFilter(substringFilter1);
		} else if (locationFilter1 != null) filter1 = locationFilter1;
		else if (substringFilter1 != null) filter1 = substringFilter1;
		
		return filter1;
	}
	
	private StringFilter getFilter2() {
		StringFilter locationFilter2 = null;
		StringFilter substringFilter2 = null;

		String locationCharString = locationChar2Field.getText();
		if (!locationCharString.contentEquals("")) {
			int location = 0;
			String locationString = location2Field.getText();
			try {
				location = Integer.parseInt(locationString);
			} catch (Exception exc) {
				// 弹出一窗口显示一些信息
				JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "第二组条件中的位置 " + locationString + "不是合法的整数!", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				return null;
			}
			char[] charArray = getLocationChars(locationCharString);
			int flag = StringLocationFilter.APPEAR;
			if (locationFlag2Box.getSelectedIndex() == 1) flag = StringLocationFilter.NOT_APPEAR; 
			locationFilter2 = new StringLocationFilter(flag, location, charArray);
		}
		String substringString = substring2Field.getText();
		if (!substringString.contentEquals("")) {
			int flag = StringSubstringFilter.EXACTLY;
			if (substringFlag2Box.getSelectedIndex() == 1) flag = StringSubstringFilter.AT_LEAST;
			else if (substringFlag2Box.getSelectedIndex() == 2) flag = StringSubstringFilter.NO_MORE_THAN;
			else if (substringFlag2Box.getSelectedIndex() == 3) flag = StringSubstringFilter.NOT_CONTAIN;

			int number = 0;
			String numberString = number2Field.getText();
			try {
				number = Integer.parseInt(numberString);
			} catch (Exception exc) {
				// 弹出一窗口显示一些信息
				JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "第二组条件中的个数 " + numberString + "不是合法的整数!", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				return null;
			}
			
			String[] substrings = getSubstrings(substringString);
			substringFilter2 = new StringSubstringFilter(flag, number, substrings);
		}

		StringFilter filter2 = null;
		if (locationFilter2 != null && substringFilter2 != null) {
			filter2 = new AndGroupStringFilter(locationFilter2);
			((AndGroupStringFilter)filter2).addFilter(substringFilter2);
		} else if (locationFilter2 != null) filter2 = locationFilter2;
		else if (substringFilter2 != null) filter2 = substringFilter2;

		return filter2;
	}
	
	private StringFilter getFilter3() {
		StringFilter locationFilter3 = null;
		StringFilter substringFilter3 = null;
		
		String locationCharString = locationChar3Field.getText();
		if (!locationCharString.contentEquals("")) {
			int location = 0;
			String locationString = location3Field.getText();
			try {
				location = Integer.parseInt(locationString);
			} catch (Exception exc) {
				// 弹出一窗口显示一些信息
				JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "第三组条件中的位置 " + locationString + "不是合法的整数!", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				return null;
			}
			char[] charArray = getLocationChars(locationCharString);
			int flag = StringLocationFilter.APPEAR;
			if (locationFlag3Box.getSelectedIndex() == 1) flag = StringLocationFilter.NOT_APPEAR; 
			locationFilter3 = new StringLocationFilter(flag, location, charArray);
		}
		String substringString = substring3Field.getText();
		if (!substringString.contentEquals("")) {
			int flag = StringSubstringFilter.EXACTLY;
			if (substringFlag3Box.getSelectedIndex() == 1) flag = StringSubstringFilter.AT_LEAST;
			else if (substringFlag3Box.getSelectedIndex() == 2) flag = StringSubstringFilter.NO_MORE_THAN;
			else if (substringFlag3Box.getSelectedIndex() == 3) flag = StringSubstringFilter.NOT_CONTAIN;

			int number = 0;
			String numberString = number3Field.getText();
			try {
				number = Integer.parseInt(numberString);
			} catch (Exception exc) {
				// 弹出一窗口显示一些信息
				JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "第三组条件中的个数 " + numberString + "不是合法的整数!", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				return null;
			}
			
			String[] substrings = getSubstrings(substringString);
			substringFilter3 = new StringSubstringFilter(flag, number, substrings);
		}
		
		StringFilter filter3 = null;
		if (locationFilter3 != null && substringFilter3 != null) {
			filter3 = new AndGroupStringFilter(locationFilter3);
			((AndGroupStringFilter)filter3).addFilter(substringFilter3);
		} else if (locationFilter3 != null) filter3 = locationFilter3;
		else if (substringFilter3 != null) filter3 = substringFilter3;
		
		return filter3;
	}
	
	private StringFilter getCombineFilter(StringFilter filter1, StringFilter filter2, StringFilter filter3) {
		StringFilter filter = null;
		if (filter1 == null && filter2 == null && filter3 == null) return null;
		if (filter1 != null) {
			if (filter2 == null && filter3 == null) filter = filter1;
			else if (group1Box.getSelectedIndex() == 0) {
				if (filter2 != null && filter3 != null) {
					if (group2Box.getSelectedIndex() == 1) {
						AndGroupStringFilter temp = new AndGroupStringFilter(filter1);
						temp.addFilter(filter2);
						filter = new OrGroupStringFilter(temp);
						((OrGroupStringFilter)filter).addFilter(filter3);
					} else {
						filter = new AndGroupStringFilter(filter1);
						((AndGroupStringFilter)filter).addFilter(filter2);
						((AndGroupStringFilter)filter).addFilter(filter3);
					}
				} else if (filter2 != null) {
					filter = new AndGroupStringFilter(filter1);
					((AndGroupStringFilter)filter).addFilter(filter2);
				} else if (filter3 != null) {
					filter = new AndGroupStringFilter(filter1);
					((AndGroupStringFilter)filter).addFilter(filter3);
				}
			} else if (group1Box.getSelectedIndex() == 1) {
				if (filter2 != null && filter3 != null) {
					if (group2Box.getSelectedIndex() == 0) {
						AndGroupStringFilter temp = new AndGroupStringFilter(filter2);
						temp.addFilter(filter3);
						filter = new OrGroupStringFilter(filter1);
						((OrGroupStringFilter)filter).addFilter(temp);
					} else {
						filter = new OrGroupStringFilter(filter1);
						((OrGroupStringFilter)filter).addFilter(filter2);
						((OrGroupStringFilter)filter).addFilter(filter3);
					}
				} else if (filter2 != null) {
					filter = new OrGroupStringFilter(filter1);
					((OrGroupStringFilter)filter).addFilter(filter2);
				} else if (filter3 != null) {
					filter = new OrGroupStringFilter(filter1);
					((OrGroupStringFilter)filter).addFilter(filter3);
				}
			} 
		} else if (filter2 != null) {
			if (filter3 == null) filter = filter2;
			else if (group2Box.getSelectedIndex() == 0) {
				filter = new AndGroupStringFilter(filter2);
				((AndGroupStringFilter)filter).addFilter(filter3);
			} else if (group2Box.getSelectedIndex() == 1) {
				filter = new OrGroupStringFilter(filter2);
				((OrGroupStringFilter)filter).addFilter(filter3);
			}
		} else filter = filter3;
		return filter;
	}
	
	
	private char[] getLocationChars(String charString) {
		String[] chars = charString.split(",");
		char[] result = new char[chars.length];
		for (int i = 0; i < result.length; i++) {
			String temp = chars[i].trim();
			result[i] = temp.charAt(0);
		}
		return result;
	}
	
	private String[] getSubstrings(String substringString) {
		String[] substrings = substringString.split(",");
		for (int i = 0; i < substrings.length; i++) substrings[i] = substrings[i].trim();
		return substrings;
	}
	
	// 创建输入关系的窗格
	private JPanel createInputSetPanel() {
		// 创建用于输入公式的窗格，使用盒式布局管理
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		// 设置该窗格使用的带标题的边框
 		inputPanel.setBorder(BorderFactory.createTitledBorder("输入计数的串的字符集长度以及计数条件（字符串中字符位置下标从0开始，条件中的字符是指单个的基集字符，但可填写用逗号分隔的多个字符，字符串是基集上的串，同样可填用逗号分隔的多个串）"));

 		JPanel linePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 48, 5));
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
		
		tempPanel = new JPanel();
		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		// 创建一个标签用于提示输入性别信息
		promptLabel = new JLabel("字符可否重复：", JLabel.RIGHT);
		// 创建用于将下两个广播按钮捆绑成一组的按钮组对象，同一组的广播按钮只能选中一个，
		// 不同组的广播按钮则可以同时选中不同的广播按钮
		ButtonGroup group = new ButtonGroup();
		// 创建选择性别的广播按钮
		repetitionButton = new JRadioButton("可以重复");
		repetitionButton.setSelected(true);		// 缺省时选中该广播按钮
		group.add(repetitionButton);
		nonrepButton = new JRadioButton("不可以重复");
		group.add(nonrepButton);
		promptLabel.setLabelFor(repetitionButton);
		tempPanel.add(promptLabel);
		tempPanel.add(repetitionButton);			// 注意是添加广播按钮组件
		tempPanel.add(nonrepButton);		// 而不是添加按钮组对象
		linePanel.add(tempPanel);
		inputPanel.add(linePanel);
		
		linePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
		tempPanel = new JPanel();
		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		promptLabel = new JLabel("第一组条件：第 ", JLabel.RIGHT);
		location1Field = new JTextField(4);
		promptLabel.setLabelFor(location1Field);
		tempPanel.add(promptLabel);
		tempPanel.add(location1Field);
		promptLabel = new JLabel(" 位的字符 ", JLabel.CENTER);
		locationFlag1Box = new JComboBox<String>(locationFlag);
		promptLabel.setLabelFor(locationFlag1Box);
		tempPanel.add(promptLabel);
		tempPanel.add(locationFlag1Box);
		promptLabel = new JLabel("是  ", JLabel.CENTER);
		locationChar1Field = new JTextField(12);
		promptLabel.setLabelFor(locationChar1Field);
		tempPanel.add(promptLabel);
		tempPanel.add(locationChar1Field);
		promptLabel = new JLabel(" 这些字符之一 ", JLabel.LEFT);
		tempPanel.add(promptLabel);
		linePanel.add(tempPanel);
		
		promptLabel = new JLabel(" 而且  ", JLabel.CENTER);
		linePanel.add(promptLabel);
		
		tempPanel = new JPanel();
		substringFlag1Box = new JComboBox<String>(substringFlag);
		promptLabel = new JLabel(" 包含  ", JLabel.CENTER);
		tempPanel.add(substringFlag1Box);
		tempPanel.add(promptLabel);
		number1Field = new JTextField(4);
		promptLabel = new JLabel(" 个  ", JLabel.CENTER);
		tempPanel.add(number1Field);
		tempPanel.add(promptLabel);
		substring1Field = new JTextField(40);
		promptLabel = new JLabel(" 这些字符串之一 ", JLabel.LEFT);
		tempPanel.add(substring1Field);
		tempPanel.add(promptLabel);
		linePanel.add(tempPanel);
		
		group1Box = new JComboBox<String>(logicString);
		linePanel.add(group1Box);
		inputPanel.add(linePanel);
		
		linePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
		tempPanel = new JPanel();
		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		promptLabel = new JLabel("第二组条件：第 ", JLabel.RIGHT);
		location2Field = new JTextField(4);
		promptLabel.setLabelFor(location2Field);
		tempPanel.add(promptLabel);
		tempPanel.add(location2Field);
		promptLabel = new JLabel(" 位的字符 ", JLabel.CENTER);
		locationFlag2Box = new JComboBox<String>(locationFlag);
		promptLabel.setLabelFor(locationFlag2Box);
		tempPanel.add(promptLabel);
		tempPanel.add(locationFlag2Box);
		promptLabel = new JLabel("是  ", JLabel.CENTER);
		locationChar2Field = new JTextField(12);
		promptLabel.setLabelFor(locationChar2Field);
		tempPanel.add(promptLabel);
		tempPanel.add(locationChar2Field);
		promptLabel = new JLabel(" 这些字符之一 ", JLabel.LEFT);
		tempPanel.add(promptLabel);
		linePanel.add(tempPanel);
		
		promptLabel = new JLabel(" 而且  ", JLabel.CENTER);
		linePanel.add(promptLabel);
		
		tempPanel = new JPanel();
		substringFlag2Box = new JComboBox<String>(substringFlag);
		promptLabel = new JLabel(" 包含  ", JLabel.CENTER);
		tempPanel.add(substringFlag2Box);
		tempPanel.add(promptLabel);
		number2Field = new JTextField(4);
		promptLabel = new JLabel(" 个  ", JLabel.CENTER);
		tempPanel.add(number2Field);
		tempPanel.add(promptLabel);
		substring2Field = new JTextField(40);
		promptLabel = new JLabel(" 这些字符串之一 ", JLabel.LEFT);
		tempPanel.add(substring2Field);
		tempPanel.add(promptLabel);
		linePanel.add(tempPanel);
		
		group2Box = new JComboBox<String>(logicString);
		linePanel.add(group2Box);
		inputPanel.add(linePanel);
		
		linePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
		tempPanel = new JPanel();
		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		promptLabel = new JLabel("第三组条件：第 ", JLabel.RIGHT);
		location3Field = new JTextField(4);
		promptLabel.setLabelFor(location3Field);
		tempPanel.add(promptLabel);
		tempPanel.add(location3Field);
		promptLabel = new JLabel(" 位的字符 ", JLabel.CENTER);
		locationFlag3Box = new JComboBox<String>(locationFlag);
		promptLabel.setLabelFor(locationFlag3Box);
		tempPanel.add(promptLabel);
		tempPanel.add(locationFlag3Box);
		promptLabel = new JLabel("是  ", JLabel.CENTER);
		locationChar3Field = new JTextField(12);
		promptLabel.setLabelFor(locationChar3Field);
		tempPanel.add(promptLabel);
		tempPanel.add(locationChar3Field);
		promptLabel = new JLabel(" 这些字符之一 ", JLabel.LEFT);
		tempPanel.add(promptLabel);
		linePanel.add(tempPanel);
		
		promptLabel = new JLabel(" 而且  ", JLabel.CENTER);
		linePanel.add(promptLabel);
		
		tempPanel = new JPanel();
		substringFlag3Box = new JComboBox<String>(substringFlag);
		promptLabel = new JLabel(" 包含  ", JLabel.CENTER);
		tempPanel.add(substringFlag3Box);
		tempPanel.add(promptLabel);
		number3Field = new JTextField(4);
		promptLabel = new JLabel(" 个  ", JLabel.CENTER);
		tempPanel.add(number3Field);
		tempPanel.add(promptLabel);
		substring3Field = new JTextField(40);
		promptLabel = new JLabel(" 这些字符串之一 ", JLabel.LEFT);
		tempPanel.add(substring3Field);
		tempPanel.add(promptLabel);
		linePanel.add(tempPanel);
		
		promptLabel = new JLabel("        ");
		linePanel.add(promptLabel);
		inputPanel.add(linePanel);
		
		JPanel displayPanel = new JPanel();
		displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
		// 设置该窗格使用的带标题的边框
		displayPanel.setBorder(BorderFactory.createTitledBorder("你输入字符串基集、长度以及计数条件如下（计数条件中的string表示待判断的字符串）："));
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
		accept50Button = new JRadioButton("给出至多50个接受的字符串");
		group.add(accept50Button);
		part100Button = new JRadioButton("给出至多100个字符串的情况");
		group.add(part100Button);
		onlyAcceptButton = new JRadioButton("只给出满足条件的串");
		group.add(onlyAcceptButton);
		allStringButton = new JRadioButton("给出所有的字符串");
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
		
		JButton exampleButton = new JButton("例子8.5");
		exampleButton.setActionCommand("example8_5");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("问题8.7");
		exampleButton.setActionCommand("example8_7");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("问题8.22");
		exampleButton.setActionCommand("example8_22");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("问题8.23(1)");
		exampleButton.setActionCommand("example8_23_1");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("问题8.23(2)");
		exampleButton.setActionCommand("example8_23_2");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("问题8.23(3)");
		exampleButton.setActionCommand("example8_23_3");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("问题8.23(4)");
		exampleButton.setActionCommand("example8_23_4");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("问题8.26(1)");
		exampleButton.setActionCommand("example8_26_1");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("问题8.26(2)");
		exampleButton.setActionCommand("example8_26_2");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("问题8.26(3)");
		exampleButton.setActionCommand("example8_26_3");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("问题8.26(4)");
		exampleButton.setActionCommand("example8_26_4");			
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
				doCountString();
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
			} else if (command.equals("example8_5")) {
				feedbackAnimator.clearContent();
				clearInput();
				baseSetField.setText("0, 1, 2, 3, 4, 5");
				lengthField.setText("2");
				nonrepButton.setSelected(true);
				checkInput();
				feedbackAnimator.update();
			} else if (command.equals("example8_7")) {
				feedbackAnimator.clearContent();
				clearInput();
				baseSetField.setText("0, 1, 2, 3, 4, 5, 6, 7, 8, 9");
				lengthField.setText("3");
				repetitionButton.setSelected(true);
				// 第 0 位不能是 0
				location1Field.setText("0");
				locationFlag1Box.setSelectedIndex(1);
				locationChar1Field.setText("0");
				// 至少有一位数字是5
				substringFlag1Box.setSelectedIndex(1);
				number1Field.setText("1");
				substring1Field.setText("5");
				checkInput();
				feedbackAnimator.update();
			} else if (command.equals("example8_22")) {
				feedbackAnimator.clearContent();
				clearInput();
				baseSetField.setText("0, 1, 2, 3, 4, 5, 6, 7, 8, 9");
				lengthField.setText("6");
				nonrepButton.setSelected(true);
				// 第 0 位不能是 0
				location1Field.setText("0");
				locationFlag1Box.setSelectedIndex(1);
				locationChar1Field.setText("0");
				// 不包含12
				substringFlag1Box.setSelectedIndex(3);
				number1Field.setText("1");
				substring1Field.setText("12");

				checkInput();
				feedbackAnimator.update();
			} else if (command.equals("example8_23_1")) {
				feedbackAnimator.clearContent();
				clearInput();
				baseSetField.setText("0, 1");
				lengthField.setText("10");
				repetitionButton.setSelected(true);
				// 恰好含有3个1
				substringFlag1Box.setSelectedIndex(0);
				number1Field.setText("3");
				substring1Field.setText("1");
				
				checkInput();
				feedbackAnimator.update();
			} else if (command.equals("example8_23_2")) {
				feedbackAnimator.clearContent();
				clearInput();
				baseSetField.setText("0, 1");
				lengthField.setText("10");
				repetitionButton.setSelected(true);
				// 至少含有3个1
				substringFlag1Box.setSelectedIndex(1);
				number1Field.setText("3");
				substring1Field.setText("1");
				
				checkInput();
				feedbackAnimator.update();
			} else if (command.equals("example8_23_3")) {
				feedbackAnimator.clearContent();
				clearInput();
				baseSetField.setText("0, 1");
				lengthField.setText("10");
				repetitionButton.setSelected(true);
				// 至多含有3个1
				substringFlag1Box.setSelectedIndex(2);
				number1Field.setText("3");
				substring1Field.setText("1");
				
				checkInput();
				feedbackAnimator.update();
			} else if (command.equals("example8_23_4")) {
				feedbackAnimator.clearContent();
				clearInput();
				baseSetField.setText("0, 1");
				lengthField.setText("10");
				repetitionButton.setSelected(true);
				// 1比0多，即至少含有6个1
				substringFlag1Box.setSelectedIndex(1);
				number1Field.setText("6");
				substring1Field.setText("1");
				
				checkInput();
				feedbackAnimator.update();
			} else if (command.equals("example8_26_1")) {
				feedbackAnimator.clearContent();
				clearInput();
				baseSetField.setText("0, 1, 2, 3, 4, 5, 6, 7, 8, 9");
				lengthField.setText("6");
				repetitionButton.setSelected(true);
				// 第 0 位不能是 0
				location1Field.setText("0");
				locationFlag1Box.setSelectedIndex(1);
				locationChar1Field.setText("0");
				// 含有奇数数字
				substringFlag1Box.setSelectedIndex(1);
				number1Field.setText("1");
				substring1Field.setText("1, 3, 5, 7, 9");

				checkInput();
				feedbackAnimator.update();
			} else if (command.equals("example8_26_2")) {
				feedbackAnimator.clearContent();
				clearInput();
				baseSetField.setText("0, 1, 2, 3, 4, 5, 6, 7, 8, 9");
				lengthField.setText("6");
				repetitionButton.setSelected(true);
				// 第 0 位不能是 0
				location1Field.setText("0");
				locationFlag1Box.setSelectedIndex(1);
				locationChar1Field.setText("0");
				// 含有偶数数字
				substringFlag1Box.setSelectedIndex(1);
				number1Field.setText("1");
				substring1Field.setText("0, 2, 4, 6, 8");

				checkInput();
				feedbackAnimator.update();
			} else if (command.equals("example8_26_3")) {
				feedbackAnimator.clearContent();
				clearInput();
				baseSetField.setText("0, 1, 2, 3, 4, 5, 6, 7, 8, 9");
				lengthField.setText("6");
				repetitionButton.setSelected(true);
				// 第 0 位不能是 0
				location1Field.setText("0");
				locationFlag1Box.setSelectedIndex(1);
				locationChar1Field.setText("0");
				// 至少含有2个奇数数字
				substringFlag1Box.setSelectedIndex(1);
				number1Field.setText("2");
				substring1Field.setText("1, 3, 5, 7, 9");

				checkInput();
				feedbackAnimator.update();
			} else if (command.equals("example8_26_4")) {
				feedbackAnimator.clearContent();
				clearInput();
				baseSetField.setText("0, 1, 2, 3, 4, 5, 6, 7, 8, 9");
				lengthField.setText("6");
				repetitionButton.setSelected(true);
				// 第 0 位不能是 0
				location1Field.setText("0");
				locationFlag1Box.setSelectedIndex(1);
				locationChar1Field.setText("0");
				// 至少含有2个偶数数字
				substringFlag1Box.setSelectedIndex(1);
				number1Field.setText("2");
				substring1Field.setText("0, 2, 4, 6, 8");

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
			} catch (Exception exc) {
				JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "输入的字符串长度 " + lengthString + "不是合法的整数！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				feedbackAnimator.appendPlainStringAsNewLine("输入的字符串长度 " + lengthString + "不是合法的整数！");
				return false;
			}
			Set baseSet = new Set(elements);
			feedbackAnimator.appendPlainStringAsNewLine("字符串基集：");
			feedbackAnimator.appendLaTeXStringToLastLine("B = " + baseSet.toLaTeXString());
			feedbackAnimator.appendPlainStringToLastLine("，长度：");
			feedbackAnimator.appendLaTeXStringToLastLine("n = " + length);
			
			textAreaManager.appendContent(counter + ": Base set: B = " + baseSet.toLaTeXString() + ", length: " + "n = " + length);
			imagedAreaManager.appendPlainStringAsNewLine(counter + "：字符串基集：");
			imagedAreaManager.appendLaTeXStringToLastLine("B = " + baseSet.toLaTeXString());
			imagedAreaManager.appendPlainStringToLastLine("，长度：");
			imagedAreaManager.appendLaTeXStringToLastLine("n = " + length);
			if (repetitionButton.isSelected()) {
				feedbackAnimator.appendPlainStringToLastLine("，计数允许字符重复的串：");
				textAreaManager.appendContent(", count strings that allow repetition: \n");
				imagedAreaManager.appendPlainStringToLastLine("，计数允许字符重复的串：");
			} else {
				feedbackAnimator.appendPlainStringToLastLine("，计数不允许字符重复的串：");
				textAreaManager.appendContent("count strings that do NOT allow repetition: \n");
				imagedAreaManager.appendPlainStringToLastLine("，计数不允许字符重复的串：");
			}
			
			StringFilter filter1 = getFilter1();
			StringFilter filter2 = getFilter2();
			StringFilter filter3 = getFilter3();
			
			StringFilter filter = getCombineFilter(filter1, filter2, filter3);
			if (filter == null) {
				feedbackAnimator.appendPlainStringAsNewLine("计数条件：没有，或者没有给出合法的计数条件！");
				textAreaManager.appendContent("\tFilter condition: none (or do not given legal filter condition)！\n");
				imagedAreaManager.appendPlainStringAsNewLine("    计数条件：没有，或者没有给出合法的计数条件！");
			} else {
				feedbackAnimator.appendPlainStringAsNewLine("计数条件：");
				feedbackAnimator.appendLaTeXStringToLastLine(filter.toLaTeXString());
				textAreaManager.appendContent("\tFilter condition: " + filter.toString() + "\n");
				imagedAreaManager.appendPlainStringAsNewLine("    计数条件：");
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
			
			StringGenerator generator = null;
			if (repetitionButton.isSelected()) generator = new StringGenerator(elements, length);
			else generator = new PermutationGenerator(elements, length);
			
			generator.first();
			int result = 0;
			int totalCounter = 0;
			while (true) {
				char[] string = generator.current();
				totalCounter++;
				boolean accepted = true;
				if (filter != null) accepted = filter.accept(string);
				if (accepted) {
					result++;
					if (detailed || onlyAccept) {
						textAreaManager.appendContent("\t" + totalCounter + " : " + StringGenerator.convertToString(string) + ", accepted " + result + "\n");
					} else if ((part100 == true && totalCounter <= 100) || (accept50 == true && result <= 50)) {
						textAreaManager.appendContent("\t" + totalCounter + " : " + StringGenerator.convertToString(string) + ", accepted " + result + "\n");
						imagedAreaManager.appendPlainStringAsNewLine("    ");
						imagedAreaManager.appendLaTeXStringToLastLine(totalCounter + " : " + StringGenerator.convertToString(string) + ", \\textrm{accepted }" + result);
						if ((part100 && totalCounter == 100) || accept50 && result == 50) {
							textAreaManager.appendContent("\t\t ... ... \n");
							imagedAreaManager.appendPlainStringAsNewLine("            ");
							imagedAreaManager.appendLaTeXStringToLastLine("\\vdots ");
						}
					}
				} else {
					if (detailed) {
						textAreaManager.appendContent("\t" + totalCounter + " : " + StringGenerator.convertToString(string) + ", NOT accept\n");
					} else if (part100 == true && totalCounter <= 100) {
						textAreaManager.appendContent("\t" + totalCounter + " : " + StringGenerator.convertToString(string) + ", NOT accept\n");
						imagedAreaManager.appendPlainStringAsNewLine("    ");
						imagedAreaManager.appendLaTeXStringToLastLine(totalCounter + " : " + StringGenerator.convertToString(string) + ", \\textrm{NOT accepted}");
						if (part100 && totalCounter == 100) {
							textAreaManager.appendContent("\t\t ... ... \n");
							imagedAreaManager.appendPlainStringAsNewLine("            ");
							imagedAreaManager.appendLaTeXStringToLastLine("\\vdots ");
						}
					}
				}
				if (generator.isLast()) break;
				generator.next();
			}
			
			textAreaManager.appendContent("\tThere are " + result + " string(s) satisfy the filter condition in the total " + totalCounter + " string(s)!\n");
			imagedAreaManager.appendPlainStringAsNewLine("    计数结果：有 ");
			imagedAreaManager.appendLaTeXStringToLastLine(""+result);
			imagedAreaManager.appendPlainStringToLastLine(" 个串满足计数条件，一共生成了 ");
			String message = "=" + totalCounter;
			if (repetitionButton.isSelected()) message = baseSet.length() + "^{" + length + "}" + message;
			else message = "P(" + baseSet.length() + ", " + length + ")" + message;
			imagedAreaManager.appendLaTeXStringToLastLine(message);
			imagedAreaManager.appendPlainStringToLastLine(" 个串！");
			
			return true;
		}
	}
}
