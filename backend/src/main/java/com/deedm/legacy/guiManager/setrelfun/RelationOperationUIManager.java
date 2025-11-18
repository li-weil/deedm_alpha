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
import javax.swing.JComboBox;
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
import com.deedm.legacy.setrelfun.Set;
import com.deedm.legacy.setrelfun.SetrelfunUtil;
import com.deedm.legacy.setrelfun.Matrix;
import com.deedm.legacy.setrelfun.Relation;
import com.deedm.legacy.util.Configuration;


/**
 * @author user
 *
 */
public class RelationOperationUIManager extends JPanel {

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
	private JTextField setCField = null;
	private String[] setNameArray = {"集合A", "集合B", "集合C"};
	private JTextArea relationRArea = null;
	private JTextArea relationSArea = null;
	private JComboBox<String> relRFromSetBox = null;
	private JComboBox<String> relRToSetBox = null;
	private JComboBox<String> relSFromSetBox = null;
	private JComboBox<String> relSToSetBox = null;
	private JCheckBox intersectionBox = null;
	private JCheckBox unionBox = null;
	private JCheckBox subtractBox = null;
	private JCheckBox inverseBox = null;
	private JCheckBox compositeBox = null;
	private JCheckBox invcompBox = null;
	private JCheckBox useMatrixBox = null;
	private JCheckBox useGraphBox = null;
	private JRadioButton charTypeButton = null;
	private JRadioButton intTypeButton = null;

	private boolean ok = false;
	private int counter = 0;

	public RelationOperationUIManager(PlainTextAreaManager plainManager, ImagedTextAreaManager imageManager) {
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
		setCField.setText("");
		relationRArea.setText("");;
		relationSArea.setText("");;
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
	
	public boolean setAllSets(String setAString, String setBString, String setCString) {
		boolean isIntElement = false;
		if (intTypeButton.isSelected()) isIntElement = true;
		
		setAField.setText(setAString);
		setBField.setText(setBString);
		setCField.setText(setCString);
		boolean success = true;
		if (!setAString.contentEquals("")) {
			char[] setAElements = SetrelfunUtil.extractSetElements(setAString, isIntElement);
			if (setAElements != null) {
				Set setA = new Set(setAElements);
				feedbackAnimator.appendLaTeXStringAsNewLine("A = " + setA.toLaTeXString());
			} else {
				feedbackAnimator.appendPlainStringAsNewLine("给出集合A的字符串 " + setAString + " 不是合法的表示集合的字符串！");
				feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + SetrelfunUtil.getExtractErrorMessage());
				success = false;
			}
		}
		if (!setBString.contentEquals("")) {
			char[] setBElements = SetrelfunUtil.extractSetElements(setBString, isIntElement);
			if (setBElements != null) {
				Set setB = new Set(setBElements);
				feedbackAnimator.appendLaTeXStringAsNewLine("B = " + setB.toLaTeXString());
			} else {
				feedbackAnimator.appendPlainStringAsNewLine("给出集合B的字符串 " + setBString + " 不是合法的表示集合的字符串！");
				feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + SetrelfunUtil.getExtractErrorMessage());
				success = false;
			}
		}
		if (!setCString.contentEquals("")) {
			char[] setCElements = SetrelfunUtil.extractSetElements(setCString, isIntElement);
			if (setCElements != null) {
				Set setC = new Set(setCElements);
				feedbackAnimator.appendLaTeXStringAsNewLine("C = " + setC.toLaTeXString());
			} else {
				feedbackAnimator.appendPlainStringAsNewLine("给出集合C的字符串 " + setCString + " 不是合法的表示集合的字符串！");
				feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + SetrelfunUtil.getExtractErrorMessage());
				success = false;
			}
		}
		feedbackAnimator.update();
		return success;
	}
	
	/**
	 * Note that: fromSetIndex or toSetIndex = 0, means set A, = 1, means set B, = 2, means set C 
	 */
	public boolean setRelationR(int fromSetIndex, int toSetIndex, String relRString) {
		boolean success = checkRelationString("R", fromSetIndex, toSetIndex, relRString);
		relRFromSetBox.setSelectedIndex(fromSetIndex);
		relRToSetBox.setSelectedIndex(toSetIndex);
		relationRArea.setText(relRString);
		return success;
	}
	
	/**
	 * Note that: fromSetIndex or toSetIndex = 0, means set A, = 1, means set B, = 2, means set C 
	 */
	public boolean setRelationS(int fromSetIndex, int toSetIndex, String relSString) {
		boolean success = checkRelationString("S", fromSetIndex, toSetIndex, relSString);
		relSFromSetBox.setSelectedIndex(fromSetIndex);
		relSToSetBox.setSelectedIndex(toSetIndex);
		relationSArea.setText(relSString);
		return success;
	}

	public Relation getRelationR() {
		int fromSetIndex = relRFromSetBox.getSelectedIndex();
		int toSetIndex = relRToSetBox.getSelectedIndex();
		String content = relationRArea.getText();
		
		return getRelation(fromSetIndex, toSetIndex, content);
	}
	
	public Relation getRelationS() {
		int fromSetIndex = relSFromSetBox.getSelectedIndex();
		int toSetIndex = relSToSetBox.getSelectedIndex();
		String content = relationSArea.getText();
		
		return getRelation(fromSetIndex, toSetIndex, content);
	}

	public void setAllOperators() {
		intersectionBox.setSelected(true);
		unionBox.setSelected(true);
		subtractBox.setSelected(true);
		inverseBox.setSelected(true);
		compositeBox.setSelected(true);
		invcompBox.setSelected(true);
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

	public Set getSetC() {
		boolean isIntElement = false;
		if (intTypeButton.isSelected()) isIntElement = true;
		String content = setCField.getText();
		char[] elements = SetrelfunUtil.extractSetElements(content, isIntElement);
		if (elements == null) return null;
		else return new Set(elements);
	}
	
	private boolean checkRelationString(String relationName, int fromSetIndex, int toSetIndex, String content) {
		boolean isIntElement = false;
		if (intTypeButton.isSelected()) isIntElement = true;
		
		String fromSetString = setAField.getText();;
		if (fromSetIndex == 1) fromSetString = setBField.getText();
		else if (fromSetIndex == 2) fromSetString = setCField.getText();
		char[] fromSetElements = SetrelfunUtil.extractSetElements(fromSetString, isIntElement);
		if (fromSetElements == null) {
			feedbackAnimator.appendPlainStringAsNewLine("下标 " + fromSetIndex + " 给出的关系源集合 " + fromSetString + " 不是合法的表示集合的字符串！");
			feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + SetrelfunUtil.getExtractErrorMessage());
			return false;
		}
		Set fromSet = new Set(fromSetElements);
		
		String toSetString = setAField.getText();;
		if (toSetIndex == 1) toSetString = setBField.getText();
		else if (toSetIndex == 2) toSetString = setCField.getText();
		char[] toSetElements = SetrelfunUtil.extractSetElements(toSetString, isIntElement);
		if (toSetElements == null) {
			feedbackAnimator.appendPlainStringAsNewLine("下标 " + toSetIndex + " 给出的关系目标集合 " + toSetString + " 不是合法的表示集合的字符串！");
			feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + SetrelfunUtil.getExtractErrorMessage());
			return false;
		}
		Set toSet = new Set(toSetElements);
		
		Relation relation = SetrelfunUtil.extractRelation(fromSet, toSet, content, isIntElement);
		if (relation == null) {
			feedbackAnimator.appendPlainStringAsNewLine("字符串 " + content + "  不是合法的表示关系的字符串！");
			feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + SetrelfunUtil.getExtractErrorMessage());
			return false;
		} else {
			feedbackAnimator.appendLaTeXStringAsNewLine(relationName + " = " + relation.toLaTeXString(isIntElement));
		}
		feedbackAnimator.update();
		return true;
	}
	
	private Relation getRelation(int fromSetIndex, int toSetIndex, String content) {
		boolean isIntElement = false;
		if (intTypeButton.isSelected()) isIntElement = true;
		
		String fromSetString = setAField.getText();
		if (fromSetIndex == 1) fromSetString = setBField.getText();
		else if (fromSetIndex == 2) fromSetString = setCField.getText();
		char[] fromSetElements = SetrelfunUtil.extractSetElements(fromSetString, isIntElement);
		if (fromSetElements == null) {
			feedbackAnimator.appendPlainStringAsNewLine("下标 " + fromSetIndex + " 给出的关系源集合 " + fromSetString + " 不是合法的表示集合的字符串！");
			feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + SetrelfunUtil.getExtractErrorMessage());
			return null;
		}
		Set fromSet = new Set(fromSetElements);
		
		String toSetString = setAField.getText();;
		if (toSetIndex == 1) toSetString = setBField.getText();
		else if (toSetIndex == 2) toSetString = setCField.getText();
		char[] toSetElements = SetrelfunUtil.extractSetElements(toSetString, isIntElement);
		if (toSetElements == null) {
			feedbackAnimator.appendPlainStringAsNewLine("下标 " + toSetIndex + " 给出的关系目标集合 " + toSetString + " 不是合法的表示集合的字符串！");
			feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + SetrelfunUtil.getExtractErrorMessage());
			return null;
		}
		Set toSet = new Set(fromSetElements);
		
		Relation relation = SetrelfunUtil.extractRelation(fromSet, toSet, content, isIntElement);
		if (relation == null) {
			feedbackAnimator.appendPlainStringAsNewLine("字符串 " + content + "  不是合法的表示关系的字符串！");
			feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + SetrelfunUtil.getExtractErrorMessage());
			return null;
		}
		return relation;
	}
	
	// 创建输入关系的窗格
	private JPanel createInputSetPanel() {
		// 创建用于输入公式的窗格，使用盒式布局管理
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new GridLayout(4, 2, 5, 0));
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
		
 		tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
		// 创建一个标签用于提示输入集合信息
		promptLabel = new JLabel("集合(C)：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+C可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('C');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		setCField = new JTextField(inputWidth);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(setCField);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(setCField);
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
		promptLabel = new JLabel("关系R源集：", JLabel.RIGHT);
		relRFromSetBox = new JComboBox<String>(setNameArray);
		relRFromSetBox.setSelectedIndex(0);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(relRFromSetBox);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(relRFromSetBox);

		promptLabel = new JLabel("目标集：", JLabel.RIGHT);
		relRToSetBox = new JComboBox<String>(setNameArray);
		relRToSetBox.setSelectedIndex(0);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(relRToSetBox);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(relRToSetBox);
		inputPanel.add(tempPanel);

 		tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 15));
		// 创建一个标签用于提示输入集合信息
		promptLabel = new JLabel("关系(S)：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+S可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('S');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		relationSArea = new JTextArea(2, inputWidth-10);
		relationSArea.setLineWrap(true);;
		scroll = new JScrollPane(relationSArea);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(relationSArea);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(scroll);
		inputPanel.add(tempPanel);
		
 		tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 15));
		promptLabel = new JLabel("关系S源集：", JLabel.RIGHT);
		relSFromSetBox = new JComboBox<String>(setNameArray);
		relSFromSetBox.setSelectedIndex(0);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(relSFromSetBox);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(relSFromSetBox);

		promptLabel = new JLabel("目标集：", JLabel.RIGHT);
		relSToSetBox = new JComboBox<String>(setNameArray);
		relSToSetBox.setSelectedIndex(0);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(relSToSetBox);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(relSToSetBox);
		inputPanel.add(tempPanel);

		JPanel displayPanel = new JPanel();
		displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
		// 设置该窗格使用的带标题的边框
		displayPanel.setBorder(BorderFactory.createTitledBorder("你输入的集合和关系如下："));
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
		JButton actButton = new JButton("关系运算(Y)");
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
		
		JButton exampleButton = new JButton("例子6.8");
		exampleButton.setActionCommand("example6_8");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("问题6.10");
		exampleButton.setActionCommand("problem6_10");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		// 创建给出变量集信息和范式信息的窗格与相关组件
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 0));
		infoPanel.setBorder(BorderFactory.createTitledBorder("选择要进行的关系运算以及是否给出关系的矩阵、图形表示："));
		intersectionBox = new JCheckBox("关系交");
		infoPanel.add(intersectionBox);
		unionBox = new JCheckBox("关系并");
		infoPanel.add(unionBox);
		subtractBox = new JCheckBox("关系差");
		infoPanel.add(subtractBox);
		inverseBox = new JCheckBox("关系逆");
		infoPanel.add(inverseBox);
		compositeBox = new JCheckBox("关系复合");
		infoPanel.add(compositeBox);
		invcompBox = new JCheckBox("逆关系复合");
		infoPanel.add(invcompBox);
		useMatrixBox = new JCheckBox("给出关系矩阵");
		infoPanel.add(useMatrixBox);
		useGraphBox = new JCheckBox("给出关系图");
		infoPanel.add(useGraphBox);
		
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
				doRelationOperation();
				feedbackAnimator.update();
				imagedAreaManager.revalidate();
			} else if (command.equals("cancel")) {
				ok = false;
				dialog.setVisible(false);
			} else if (command.equals("remove")) {
				clearInput();
				intersectionBox.setSelected(false);
				unionBox.setSelected(false);
				subtractBox.setSelected(false);
				inverseBox.setSelected(false);
				compositeBox.setSelected(false);
				invcompBox.setSelected(false);
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
					JOptionPane.showMessageDialog(dialog, "输入的集合与关系都符合要求，可以进行关系运算！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				}
				feedbackAnimator.update();
			} else if (command.equals("example6_8")) {
				feedbackAnimator.clearContent();
				String setAString = "{1, 2, 3, 4}";
				String setBString = "{a, b, c, d}";
				String setCString = "{x, y, z}";
				String relationR = "{<1, a>, <1, b>, <2, a>, <3, b>, <3, d>, <4, b>}";
				String relationS = "{<a, x>, <b, y>, <b, z>, <c, x>, <d, x>}";
				setElementTypeInt(false);
				setAllSets(setAString, setBString, setCString);
				setRelationR(0, 1, relationR);
				setRelationS(1, 2, relationS);
				intersectionBox.setSelected(false);
				unionBox.setSelected(false);
				subtractBox.setSelected(false);
				inverseBox.setSelected(true);
				compositeBox.setSelected(true);
				invcompBox.setSelected(true);
				feedbackAnimator.update();
			} else if (command.equals("problem6_10")) {
				feedbackAnimator.clearContent();
				String setAString = "{1, 2, 3, 4}";
				String relationR = "{<1, 1>, <1, 3>, <2, 2>, <2, 4>, <3, 1>, <3, 3>, <4, 2>, <4, 4>}";
				String relationS = "{<1, 1>, <1, 4>, <2, 2>, <3, 3>, <4, 1>, <4, 4>}";
				setElementTypeInt(false);
				setAllSets(setAString, "", "");
				setRelationR(0, 0, relationR);
				setRelationS(0, 0, relationS);
				intersectionBox.setSelected(true);
				unionBox.setSelected(true);
				subtractBox.setSelected(true);
				inverseBox.setSelected(true);
				compositeBox.setSelected(true);
				invcompBox.setSelected(true);
				feedbackAnimator.update();
			} else if (command.equals("clear")) {
				textAreaManager.clearContent();
				imagedAreaManager.clearContent();
				imagedAreaManager.revalidate();
			}
		}
		
		public boolean doRelationOperation() {
			boolean hasSetA = false;
			boolean hasSetB = false;
			boolean hasSetC = false;
			int RfromSetIndex = relRFromSetBox.getSelectedIndex();
			if (RfromSetIndex == 0) hasSetA = true;
			else if (RfromSetIndex == 1) hasSetB = true;
			else if (RfromSetIndex == 2) hasSetC = true;
			int RtoSetIndex = relRToSetBox.getSelectedIndex();
			if (RtoSetIndex == 0) hasSetA = true;
			else if (RtoSetIndex == 1) hasSetB = true;
			else if (RtoSetIndex == 2) hasSetC = true;
			int SfromSetIndex = relSFromSetBox.getSelectedIndex();
			if (SfromSetIndex == 0) hasSetA = true;
			else if (SfromSetIndex == 1) hasSetB = true;
			else if (SfromSetIndex == 2) hasSetC = true;
			int StoSetIndex = relSToSetBox.getSelectedIndex();
			if (StoSetIndex == 0) hasSetA = true;
			else if (StoSetIndex == 1) hasSetB = true;
			else if (StoSetIndex == 2) hasSetC = true;
			
			Set setA = null;
			if (hasSetA) {
				setA = getSetA();
				if (setA == null) {
					textAreaManager.appendContent("The string " + setAField.getText() + " does not give a legal set!\n");
					imagedAreaManager.appendPlainStringAsNewLine("字符串 " + setAField.getText() + " 没有给出合法的集合A！");
					feedbackAnimator.appendPlainStringAsNewLine("字符串 " + setAField.getText() + " 没有给出合法的集合A！");
					return false;
				} else feedbackAnimator.appendLaTeXStringAsNewLine("A = " + setA.toLaTeXString());
			}
			Set setB = null;
			if (hasSetB) {
				setB = getSetB();
				if (setB == null) {
					textAreaManager.appendContent("The string " + setBField.getText() + " does not give a legal set!\n");
					imagedAreaManager.appendPlainStringAsNewLine("字符串 " + setBField.getText() + " 没有给出合法的集合B！");
					feedbackAnimator.appendPlainStringAsNewLine("字符串 " + setBField.getText() + " 没有给出合法的集合B！");
					return false;
				} else feedbackAnimator.appendLaTeXStringAsNewLine("B = " + setB.toLaTeXString());
			}
			Set setC = null;
			if (hasSetC) {
				setC = getSetC();
				if (setC == null) {
					textAreaManager.appendContent("The string " + setCField.getText() + " does not give a legal set!\n");
					imagedAreaManager.appendPlainStringAsNewLine("字符串 " + setCField.getText() + " 没有给出合法的集合C！");
					feedbackAnimator.appendPlainStringAsNewLine("字符串 " + setCField.getText() + " 没有给出合法的集合C！");
					return false;
				} else feedbackAnimator.appendLaTeXStringAsNewLine("C = " + setC.toLaTeXString());
			}
			
			if (hasSetA) {
				textAreaManager.appendContent(counter + ": The set A = " + setA.toLaTeXString());
				imagedAreaManager.appendPlainStringAsNewLine(counter + ": 集合 ");
				imagedAreaManager.appendLaTeXStringToLastLine("A = " + setA.toLaTeXString());
				if (hasSetB) {
					textAreaManager.appendContent(", B = " + setB.toLaTeXString());
					imagedAreaManager.appendLaTeXStringToLastLine(", B = " + setB.toLaTeXString());
				}
				if (hasSetC) {
					textAreaManager.appendContent(", C = " + setC.toLaTeXString());
					imagedAreaManager.appendLaTeXStringToLastLine(", C = " + setC.toLaTeXString());
				}
			} else if (hasSetB) {
				textAreaManager.appendContent(counter + ": The set B = " + setB.toLaTeXString());
				imagedAreaManager.appendPlainStringAsNewLine(counter + ": 集合 ");
				imagedAreaManager.appendLaTeXStringToLastLine("B = " + setB.toLaTeXString());
				if (hasSetC) {
					textAreaManager.appendContent(", C = " + setC.toLaTeXString());
					imagedAreaManager.appendLaTeXStringToLastLine(", C = " + setC.toLaTeXString());
				}
			} else if (hasSetC) {
				textAreaManager.appendContent(counter + ": The set C = " + setC.toLaTeXString());
				imagedAreaManager.appendPlainStringAsNewLine(counter + ": 集合 ");
				imagedAreaManager.appendLaTeXStringToLastLine("C = " + setC.toLaTeXString());
			}

			boolean isIntElement = false;
			if (intTypeButton.isSelected()) isIntElement = true;
			Set RfromSet = null;
			String RfromSetName = null;
			Set RtoSet = null;
			String RtoSetName = null;
			Set SfromSet = null;
			String SfromSetName = null;
			Set StoSet = null;
			String StoSetName = null;
			if (RfromSetIndex == 0) {
				RfromSet = setA;
				RfromSetName = "A";
			} else if (RfromSetIndex == 1) {
				RfromSet = setB;
				RfromSetName = "B";
			} else if (RfromSetIndex == 2) {
				RfromSet = setC;
				RfromSetName = "C";
			}
			if (RtoSetIndex == 0) {
				RtoSet = setA;
				RtoSetName = "A";
			} else if (RtoSetIndex == 1) {
				RtoSet = setB;
				RtoSetName = "B";
			} else if (RtoSetIndex == 2) {
				RtoSet = setC;
				RtoSetName = "C";
			}
			if (SfromSetIndex == 0) {
				SfromSet = setA;
				SfromSetName = "A";
			} else if (SfromSetIndex == 1) {
				SfromSet = setB;
				SfromSetName = "B";
			} else if (SfromSetIndex == 2) {
				SfromSet = setC;
				SfromSetName = "C";
			}
			if (StoSetIndex == 0) {
				StoSet = setA;
				StoSetName = "A";
			} else if (StoSetIndex == 1) {
				StoSet = setB;
				StoSetName = "B";
			} else if (StoSetIndex == 2) {
				StoSet = setC;
				StoSetName = "C";
			}
			String content = relationRArea.getText();
			Relation relationR = SetrelfunUtil.extractRelation(RfromSet, RtoSet, content, isIntElement);
			if (relationR == null) {
				textAreaManager.appendContent("\n\tThe string " + content + " is not a legal relation for R!\n");
				imagedAreaManager.appendPlainStringAsNewLine("    字符串 " + content + " 没有合法地给出关系 R！");
				imagedAreaManager.appendPlainStringAsNewLine("    错误信息： " + SetrelfunUtil.getExtractErrorMessage());
				feedbackAnimator.appendPlainStringAsNewLine("字符串 " + content + "  不是合法的表示关系的字符串！");
				return false;
			} else {
				textAreaManager.appendContent("\n\tR = " + relationR.toLaTeXString(isIntElement) + "\n");
				imagedAreaManager.appendPlainStringAsNewLine("    关系: ");
				imagedAreaManager.appendLaTeXStringToLastLine("R = " + relationR.toLaTeXString(isIntElement) + " \\subseteq " + RfromSetName + "\\times " + RtoSetName);
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
			}
			content = relationSArea.getText();
			Relation relationS = SetrelfunUtil.extractRelation(SfromSet, StoSet, content, isIntElement);
			if (relationS == null) {
				textAreaManager.appendContent("\tThe string " + content + " is not a legal relation for S!\n");
				imagedAreaManager.appendPlainStringAsNewLine("    字符串 " + content + " 没有合法地给出关系 S！");
				imagedAreaManager.appendPlainStringAsNewLine("    错误信息： " + SetrelfunUtil.getExtractErrorMessage());
				feedbackAnimator.appendPlainStringAsNewLine("字符串 " + content + "  不是合法的表示关系的字符串！");
				return false;
			} else {
				textAreaManager.appendContent("\tS = " + relationS.toLaTeXString(isIntElement) + "\n");
				imagedAreaManager.appendPlainStringAsNewLine("    关系: ");
				imagedAreaManager.appendLaTeXStringToLastLine("S = " + relationS.toLaTeXString(isIntElement) + " \\subseteq " + SfromSetName + "\\times " + StoSetName);

				if (useMatrixBox.isSelected()) {
					Matrix matrix = relationS.getMatrix();
					textAreaManager.appendContent("\t\t" + "M_S = " + matrix.toLaTeXString() + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    关系矩阵: ");
					imagedAreaManager.appendLaTeXStringToLastLine("M_S = " + matrix.toLaTeXString());
				}
				if (useGraphBox.isSelected()) {
					String dotFileName = Configuration.dataFilePath + "RelationS.dot";
					String pngFileName = Configuration.dataFilePath + "RelationS.png";
					try {
						PrintWriter writer = new PrintWriter(dotFileName);
						AbstractGraph graph = relationS.getRelationGraph(isIntElement);
						graph.simplyWriteToDotFile(writer);
						boolean success = MainGUIManager.generatePNGFile(dotFileName, pngFileName, false);
						if (success) {
							textAreaManager.appendContent("\t\tHere gives the relation graph of the relation S!\n");
							imagedAreaManager.appendPlainStringAsNewLine("    关系图: ");
							imagedAreaManager.appendImageFileToLastLine(pngFileName);
						} else {
							textAreaManager.appendContent("\t\tCan not generate the relation graph of the relation S!\n");
							textAreaManager.appendContent(MainGUIManager.errorMessage);
							imagedAreaManager.appendPlainStringAsNewLine("    关系图: 无法生成！");
						}
					} catch (Exception e) {
						textAreaManager.appendContent("\t\tCan not generate the relation graph of the relation S!\n");
						textAreaManager.appendContent("Exception message: " + e.getMessage());
						imagedAreaManager.appendPlainStringAsNewLine("    关系图: 无法生成！");
					}
				}
				feedbackAnimator.appendLaTeXStringAsNewLine("S = " + relationS.toLaTeXString(isIntElement));
			}
			
			if (intersectionBox.isSelected()) {
				if (RfromSet == SfromSet && RtoSet == StoSet) {
					Relation result = relationR.intersection(relationS);
					textAreaManager.appendContent("\tR\\cap S = " + result.toLaTeXString() + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    关系的交: ");
					imagedAreaManager.appendLaTeXStringToLastLine("R\\cap S = " + result.toLaTeXString(isIntElement));
					if (useMatrixBox.isSelected()) {
						Matrix matrix = result.getMatrix();
						textAreaManager.appendContentToLastLine("\t\t" + "M_{R\\cap S} = " + matrix.toLaTeXString());
						imagedAreaManager.appendPlainStringAsNewLine("    关系矩阵: ");
						imagedAreaManager.appendLaTeXStringToLastLine("M_{R\\cap S} = " + matrix.toLaTeXString());
					}
					if (useGraphBox.isSelected()) {
						String dotFileName = Configuration.dataFilePath + "RelationRcapS.dot";
						String pngFileName = Configuration.dataFilePath + "RelationRcapS.png";
						try {
							PrintWriter writer = new PrintWriter(dotFileName);
							AbstractGraph graph = result.getRelationGraph(isIntElement);
							graph.simplyWriteToDotFile(writer);
							boolean success = MainGUIManager.generatePNGFile(dotFileName, pngFileName, false);
							if (success) {
								textAreaManager.appendContent("\t\tHere gives the relation graph of the relation R\\cap S!\n");
								imagedAreaManager.appendPlainStringAsNewLine("    关系图: ");
								imagedAreaManager.appendImageFileToLastLine(pngFileName);
							} else {
								textAreaManager.appendContent("\t\tCan not generate the relation graph of the relation R\\cap S!\n");
								textAreaManager.appendContent(MainGUIManager.errorMessage);
								imagedAreaManager.appendPlainStringAsNewLine("    关系图: 无法生成！");
							}
						} catch (Exception e) {
							textAreaManager.appendContent("\t\tCan not generate the relation graph of the relation R\\caps S!\n");
							textAreaManager.appendContent("Exception message: " + e.getMessage());
							imagedAreaManager.appendPlainStringAsNewLine("    关系图: 无法生成！");
						}
					}
				} else {
					textAreaManager.appendContent("\tCan not do R\\cap S!\n");
					imagedAreaManager.appendPlainStringAsNewLine("    不能执行者两个关系的交运算，它们不是相同两个集合之间的关系！ ");
				}
			}
			if (unionBox.isSelected()) {
				if (RfromSet == SfromSet && RtoSet == StoSet) {
					Relation result = relationR.union(relationS);
					textAreaManager.appendContent("\tR\\cup S = " + result.toLaTeXString() + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    关系的并: ");
					imagedAreaManager.appendLaTeXStringToLastLine("R\\cup S = " + result.toLaTeXString(isIntElement));
					if (useMatrixBox.isSelected()) {
						Matrix matrix = result.getMatrix();
						textAreaManager.appendContentToLastLine("\t\t" + "M_{R\\cup S} = " + matrix.toLaTeXString());
						imagedAreaManager.appendPlainStringAsNewLine("    关系矩阵: ");
						imagedAreaManager.appendLaTeXStringToLastLine("M_{R\\cup S} = " + matrix.toLaTeXString());
					}
					if (useGraphBox.isSelected()) {
						String dotFileName = Configuration.dataFilePath + "RelationRcupS.dot";
						String pngFileName = Configuration.dataFilePath + "RelationRcupS.png";
						try {
							PrintWriter writer = new PrintWriter(dotFileName);
							AbstractGraph graph = result.getRelationGraph(isIntElement);
							graph.simplyWriteToDotFile(writer);
							boolean success = MainGUIManager.generatePNGFile(dotFileName, pngFileName, false);
							if (success) {
								textAreaManager.appendContent("\t\tHere gives the relation graph of the relation R\\cup S!\n");
								imagedAreaManager.appendPlainStringAsNewLine("    关系图: ");
								imagedAreaManager.appendImageFileToLastLine(pngFileName);
							} else {
								textAreaManager.appendContent("\t\tCan not generate the relation graph of the relation R\\cup S!\n");
								textAreaManager.appendContent(MainGUIManager.errorMessage);
								imagedAreaManager.appendPlainStringAsNewLine("    关系图: 无法生成！");
							}
						} catch (Exception e) {
							textAreaManager.appendContent("\t\tCan not generate the relation graph of the relation R\\cup S!\n");
							textAreaManager.appendContent("Exception message: " + e.getMessage());
							imagedAreaManager.appendPlainStringAsNewLine("    关系图: 无法生成！");
						}
					}
				} else {
					textAreaManager.appendContent("\tCan not do R\\cup S!\n");
					imagedAreaManager.appendPlainStringAsNewLine("    不能执行者两个关系的并运算，它们不是相同两个集合之间的关系！ ");
				}
			}
			if (subtractBox.isSelected()) {
				if (RfromSet == SfromSet && RtoSet == StoSet) {
					Relation result = relationR.subtract(relationS);
					textAreaManager.appendContent("\tR - S = " + result.toLaTeXString() + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    关系的差: ");
					imagedAreaManager.appendLaTeXStringToLastLine("R - S = " + result.toLaTeXString(isIntElement));
					if (useMatrixBox.isSelected()) {
						Matrix matrix = result.getMatrix();
						textAreaManager.appendContentToLastLine("\t\t" + "M_{R - S} = " + matrix.toLaTeXString());
						imagedAreaManager.appendPlainStringAsNewLine("    关系矩阵: ");
						imagedAreaManager.appendLaTeXStringToLastLine("M_{R - S} = " + matrix.toLaTeXString());
					}
				} else {
					textAreaManager.appendContent("\tCan not do R - S!\n");
					imagedAreaManager.appendPlainStringAsNewLine("    不能执行者两个关系的差运算，它们不是相同两个集合之间的关系！ ");
				}
			}
			if (inverseBox.isSelected()) {
				Relation result = relationR.inverse();
				textAreaManager.appendContent("\tR^{-1}  = " + result.toLaTeXString() + "\n");
				imagedAreaManager.appendPlainStringAsNewLine("    关系R的逆: ");
				imagedAreaManager.appendLaTeXStringToLastLine("R^{-1} = " + result.toLaTeXString(isIntElement));
				if (useMatrixBox.isSelected()) {
					Matrix matrix = result.getMatrix();
					textAreaManager.appendContentToLastLine("\t\t" + "M_{R^{-1}} = " + matrix.toLaTeXString());
					imagedAreaManager.appendPlainStringAsNewLine("    关系矩阵: ");
					imagedAreaManager.appendLaTeXStringToLastLine("M_{R^{-1}} = " + matrix.toLaTeXString());
				}
				result = relationS.inverse();
				textAreaManager.appendContent("\tS^{-1}  = " + result.toLaTeXString() + "\n");
				imagedAreaManager.appendPlainStringAsNewLine("    关系S的逆: ");
				imagedAreaManager.appendLaTeXStringToLastLine("S^{-1} = " + result.toLaTeXString(isIntElement));
				if (useMatrixBox.isSelected()) {
					Matrix matrix = result.getMatrix();
					textAreaManager.appendContentToLastLine("\t\t" + "M_{S^{-1}} = " + matrix.toLaTeXString());
					imagedAreaManager.appendPlainStringAsNewLine("    关系矩阵: ");
					imagedAreaManager.appendLaTeXStringToLastLine("M_{S^{-1}} = " + matrix.toLaTeXString());
				}
			}
			if (compositeBox.isSelected()) {
				boolean hasComposite = false;
				if (RtoSet == SfromSet) {
					Relation result = relationR.composite(relationS);
					textAreaManager.appendContent("\tS\\circ R  = " + result.toLaTeXString() + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    关系R和S的复合: ");
					imagedAreaManager.appendLaTeXStringToLastLine("S\\circ R = " + result.toLaTeXString(isIntElement));
					hasComposite = true;
					if (useMatrixBox.isSelected()) {
						Matrix matrix = result.getMatrix();
						textAreaManager.appendContentToLastLine("\t\t" + "M_{S\\circ R} = " + matrix.toLaTeXString());
						imagedAreaManager.appendPlainStringAsNewLine("    关系矩阵: ");
						imagedAreaManager.appendLaTeXStringToLastLine("M_{S\\circ R} = " + matrix.toLaTeXString());
					}
					if (useGraphBox.isSelected()) {
						String dotFileName = Configuration.dataFilePath + "RelationScircR.dot";
						String pngFileName = Configuration.dataFilePath + "RelationScircR.png";
						try {
							PrintWriter writer = new PrintWriter(dotFileName);
							AbstractGraph graph = result.getRelationGraph(isIntElement);
							graph.simplyWriteToDotFile(writer);
							boolean success = MainGUIManager.generatePNGFile(dotFileName, pngFileName, false);
							if (success) {
								textAreaManager.appendContent("\t\tHere gives the relation graph of the relation S\\circ R!\n");
								imagedAreaManager.appendPlainStringAsNewLine("    关系图: ");
								imagedAreaManager.appendImageFileToLastLine(pngFileName);
							} else {
								textAreaManager.appendContent("\t\tCan not generate the relation graph of the relation S\\circ R!\n");
								textAreaManager.appendContent(MainGUIManager.errorMessage);
								imagedAreaManager.appendPlainStringAsNewLine("    关系图: 无法生成！");
							}
						} catch (Exception e) {
							textAreaManager.appendContent("\t\tCan not generate the relation graph of the relation S\\circ R!\n");
							textAreaManager.appendContent("Exception message: " + e.getMessage());
							imagedAreaManager.appendPlainStringAsNewLine("    关系图: 无法生成！");
						}
					}
				}
				if (StoSet == RfromSet) {
					Relation result = relationS.composite(relationR);
					textAreaManager.appendContent("\tR\\circ S  = " + result.toLaTeXString() + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    关系S和R的复合: ");
					imagedAreaManager.appendLaTeXStringToLastLine("R\\circ S = " + result.toLaTeXString(isIntElement));
					hasComposite = true;
					if (useMatrixBox.isSelected()) {
						Matrix matrix = result.getMatrix();
						textAreaManager.appendContentToLastLine("\t\t" + "M_{R\\circ S} = " + matrix.toLaTeXString());
						imagedAreaManager.appendPlainStringAsNewLine("    关系矩阵: ");
						imagedAreaManager.appendLaTeXStringToLastLine("M_{R\\circ S} = " + matrix.toLaTeXString());
					}
					if (useGraphBox.isSelected()) {
						String dotFileName = Configuration.dataFilePath + "RelationRcircS.dot";
						String pngFileName = Configuration.dataFilePath + "RelationRcircS.png";
						try {
							PrintWriter writer = new PrintWriter(dotFileName);
							AbstractGraph graph = result.getRelationGraph(isIntElement);
							graph.simplyWriteToDotFile(writer);
							boolean success = MainGUIManager.generatePNGFile(dotFileName, pngFileName, false);
							if (success) {
								textAreaManager.appendContent("\t\tHere gives the relation graph of the relation R\\circ S!\n");
								imagedAreaManager.appendPlainStringAsNewLine("    关系图: ");
								imagedAreaManager.appendImageFileToLastLine(pngFileName);
							} else {
								textAreaManager.appendContent("\t\tCan not generate the relation graph of the relation R\\circ S!\n");
								textAreaManager.appendContent(MainGUIManager.errorMessage);
								imagedAreaManager.appendPlainStringAsNewLine("    关系图: 无法生成！");
							}
						} catch (Exception e) {
							textAreaManager.appendContent("\t\tCan not generate the relation graph of the relation R\\circ S!\n");
							textAreaManager.appendContent("Exception message: " + e.getMessage());
							imagedAreaManager.appendPlainStringAsNewLine("    关系图: 无法生成！");
						}
					}
				}
				if (hasComposite == false) {
					textAreaManager.appendContent("\tCan not do S\\circ R or R\\circ S!\n");
					imagedAreaManager.appendPlainStringAsNewLine("    既不能做R和S的复合，也不能做S和R的复合！ ");
				}
			}
			if (invcompBox.isSelected()) {
				boolean hasComposite = false;
				if (SfromSet == RtoSet) {
					Relation invR = relationR.inverse();
					Relation invS = relationS.inverse();
					Relation result = invS.composite(invR);
					textAreaManager.appendContent("\tR^{-1}\\circ S^{-1}  = " + result.toLaTeXString() + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    关系S的逆和R的逆复合: ");
					imagedAreaManager.appendLaTeXStringToLastLine("R^{-1}\\circ S^{-1} = " + result.toLaTeXString(isIntElement));
					hasComposite = true;
					if (useMatrixBox.isSelected()) {
						Matrix matrix = result.getMatrix();
						textAreaManager.appendContentToLastLine("\t\t" + "M_{R^{-1}\\circ S^{-1}} = " + matrix.toLaTeXString());
						imagedAreaManager.appendPlainStringAsNewLine("    关系矩阵: ");
						imagedAreaManager.appendLaTeXStringToLastLine("M_{R^{-1}\\circ S^{-1}} = " + matrix.toLaTeXString());
					}
				}
				if (RfromSet == StoSet) {
					Relation invR = relationR.inverse();
					Relation invS = relationS.inverse();
					Relation result = invR.composite(invS);
					textAreaManager.appendContent("\tS^{-1}\\circ R^{-1}  = " + result.toLaTeXString() + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    关系R的逆和S的逆复合: ");
					imagedAreaManager.appendLaTeXStringToLastLine("S^{-1}\\circ R^{-1} = " + result.toLaTeXString(isIntElement));
					hasComposite = true;
					if (useMatrixBox.isSelected()) {
						Matrix matrix = result.getMatrix();
						textAreaManager.appendContentToLastLine("\t\t" + "M_{S^{-1}\\circ R^{-1}} = " + matrix.toLaTeXString());
						imagedAreaManager.appendPlainStringAsNewLine("    关系矩阵: ");
						imagedAreaManager.appendLaTeXStringToLastLine("M_{S^{-1}\\circ R^{-1}} = " + matrix.toLaTeXString());
					}
				}
				if (hasComposite == false) {
					textAreaManager.appendContent("\tCan not do R^{-1}\\circ S^{-1} or S^{-1}\\circ R^{-1}!\n");
					imagedAreaManager.appendPlainStringAsNewLine("    既不能做R的逆和S的逆的复合，也不能做S的逆和R的逆的复合！ ");
				}
			}
			return true;
		}
		
		public boolean generateInputs() {
			int RfromSetIndex = relRFromSetBox.getSelectedIndex();
			int RtoSetIndex = relRToSetBox.getSelectedIndex();
			int SfromSetIndex = relSFromSetBox.getSelectedIndex();
			int StoSetIndex = relSToSetBox.getSelectedIndex();
			Set setA = getSetA();
			if (setA == null) {
				char[] elements = {'0', '1', '2', '3', '4', '5', '6'};
				setA = new Set(elements);
				setAField.setText(setA.toString());
				intTypeButton.setSelected(false);
				charTypeButton.setSelected(true);
			}
			Set RfromSet = setA;
			Set RtoSet = setA;
			Set SfromSet = setA;
			Set StoSet = setA;

			Set setB = getSetB();
			if (setB == null) {
				if (RfromSetIndex == 1) relRFromSetBox.setSelectedIndex(0);
				if (RtoSetIndex == 1) relRToSetBox.setSelectedIndex(0);
				if (SfromSetIndex == 1) relSFromSetBox.setSelectedIndex(0);
				if (StoSetIndex == 1) relSToSetBox.setSelectedIndex(0);
			} else {
				if (RfromSetIndex == 1) RfromSet = setB;
				if (RtoSetIndex == 1) RtoSet = setB;
				if (SfromSetIndex == 1) SfromSet = setB;
				if (StoSetIndex == 1) StoSet = setB;
			}
			Set setC = getSetC();
			if (setC == null) {
				if (RfromSetIndex == 2) relRFromSetBox.setSelectedIndex(0);
				if (RtoSetIndex == 2) relRToSetBox.setSelectedIndex(0);
				if (SfromSetIndex == 2) relSFromSetBox.setSelectedIndex(0);
				if (StoSetIndex == 2) relSToSetBox.setSelectedIndex(0);
			} else {
				if (RfromSetIndex == 2) RfromSet = setC;
				if (RtoSetIndex == 2) RtoSet = setC;
				if (SfromSetIndex == 2) SfromSet = setC;
				if (StoSetIndex == 2) StoSet = setC;
			}
			Relation relationR = Relation.randomGenerate(RfromSet, RtoSet, 10);
			relationRArea.setText(relationR.toString());
			Relation relationS = Relation.randomGenerate(SfromSet, StoSet, 10);
			relationSArea.setText(relationS.toString());
			
			checkInputs();		// 让生成的信息通过合法性检查后反馈在输入界面之上
			return true;
		}
		
		public boolean checkInputs() {
			boolean hasSetA = false;
			boolean hasSetB = false;
			boolean hasSetC = false;
			int RfromSetIndex = relRFromSetBox.getSelectedIndex();
			if (RfromSetIndex == 0) hasSetA = true;
			else if (RfromSetIndex == 1) hasSetB = true;
			else if (RfromSetIndex == 2) hasSetC = true;
			int RtoSetIndex = relRToSetBox.getSelectedIndex();
			if (RtoSetIndex == 0) hasSetA = true;
			else if (RtoSetIndex == 1) hasSetB = true;
			else if (RtoSetIndex == 2) hasSetC = true;
			int SfromSetIndex = relSFromSetBox.getSelectedIndex();
			if (SfromSetIndex == 0) hasSetA = true;
			else if (SfromSetIndex == 1) hasSetB = true;
			else if (SfromSetIndex == 2) hasSetC = true;
			int StoSetIndex = relSToSetBox.getSelectedIndex();
			if (StoSetIndex == 0) hasSetA = true;
			else if (StoSetIndex == 1) hasSetB = true;
			else if (StoSetIndex == 2) hasSetC = true;
			
			Set setA = null;
			if (hasSetA) {
				setA = getSetA();
				if (setA == null) {
					feedbackAnimator.appendPlainStringAsNewLine("字符串 " + setAField.getText() + " 没有给出合法的集合A！");
					return false;
				} else feedbackAnimator.appendLaTeXStringAsNewLine("A = " + setA.toLaTeXString());
			}
			Set setB = null;
			if (hasSetB) {
				setB = getSetB();
				if (setB == null) {
					feedbackAnimator.appendPlainStringAsNewLine("字符串 " + setBField.getText() + " 没有给出合法的集合B！");
					return false;
				} else feedbackAnimator.appendLaTeXStringAsNewLine("B = " + setB.toLaTeXString());
			}
			Set setC = null;
			if (hasSetC) {
				setC = getSetC();
				if (setC == null) {
					feedbackAnimator.appendPlainStringAsNewLine("字符串 " + setCField.getText() + " 没有给出合法的全集！");
					return false;
				} else feedbackAnimator.appendLaTeXStringAsNewLine("C = " + setC.toLaTeXString());
			}
			
			boolean isIntElement = false;
			if (intTypeButton.isSelected()) isIntElement = true;
			Set RfromSet = null;
			String RfromSetName = null;
			Set RtoSet = null;
			String RtoSetName = null;
			Set SfromSet = null;
			String SfromSetName = null;
			Set StoSet = null;
			String StoSetName = null;
			if (RfromSetIndex == 0) {
				RfromSet = setA;
				RfromSetName = "A";
			} else if (RfromSetIndex == 1) {
				RfromSet = setB;
				RfromSetName = "B";
			} else if (RfromSetIndex == 2) {
				RfromSet = setC;
				RfromSetName = "C";
			}
			if (RtoSetIndex == 0) {
				RtoSet = setA;
				RtoSetName = "A";
			} else if (RtoSetIndex == 1) {
				RtoSet = setB;
				RtoSetName = "B";
			} else if (RtoSetIndex == 2) {
				RtoSet = setC;
				RtoSetName = "C";
			}
			if (SfromSetIndex == 0) {
				SfromSet = setA;
				SfromSetName = "A";
			} else if (SfromSetIndex == 1) {
				SfromSet = setB;
				SfromSetName = "B";
			} else if (SfromSetIndex == 2) {
				SfromSet = setC;
				SfromSetName = "C";
			}
			if (StoSetIndex == 0) {
				StoSet = setA;
				StoSetName = "A";
			} else if (StoSetIndex == 1) {
				StoSet = setB;
				StoSetName = "B";
			} else if (StoSetIndex == 2) {
				StoSet = setC;
				StoSetName = "C";
			}
			String content = relationRArea.getText();
			Relation relationR = SetrelfunUtil.extractRelation(RfromSet, RtoSet, content, isIntElement);
			if (relationR == null) {
				feedbackAnimator.appendPlainStringAsNewLine("字符串 " + content + "  不是合法的表示关系的字符串！");
				return false;
			} else {
				feedbackAnimator.appendLaTeXStringAsNewLine("R = " + relationR.toLaTeXString(isIntElement) + " \\subseteq " + RfromSetName + "\\times " + RtoSetName);
			}
			content = relationSArea.getText();
			Relation relationS = SetrelfunUtil.extractRelation(SfromSet, StoSet, content, isIntElement);
			if (relationS == null) {
				feedbackAnimator.appendPlainStringAsNewLine("字符串 " + content + "  不是合法的表示关系的字符串！");
				return false;
			} else {
				feedbackAnimator.appendLaTeXStringAsNewLine("S = " + relationS.toLaTeXString(isIntElement) + " \\subseteq " + SfromSetName + "\\times " + StoSetName);
			}
			return true;
		}
	}
	
}
