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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.deedm.legacy.algebra.BinaryOperator;
import com.deedm.legacy.algebra.GroupPermutation;
import com.deedm.legacy.guiManager.ImagedTextAreaManager;
import com.deedm.legacy.guiManager.MainSwingFrame;
import com.deedm.legacy.guiManager.PlainTextAreaManager;

/**
 * @author user
 *
 */
public class GroupPermUIManager extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final int width = Math.max(2* MainSwingFrame.screenWidth/5, 1024)*(MainSwingFrame.screenResolution/96);
	private static final int height = Math.max(1*MainSwingFrame.screenHeight/5, 240)*(MainSwingFrame.screenResolution/96);
	private ImagedTextAreaManager imagedAreaManager = null;
	private PlainTextAreaManager textAreaManager = null;

	private JDialog dialog = null;
	private JButton defaultButton = null;
	private JTextField inputmField = null;
	private JCheckBox cycleBox = null;
	private JCheckBox powerBox = null;
	private JCheckBox orderBox = null;
	private JCheckBox subgroupBox = null;
	private JCheckBox cosetBox = null;
	private JCheckBox normalBox = null;

	private boolean ok = false;
	private int counter = 0;

	public GroupPermUIManager(PlainTextAreaManager plainManager, ImagedTextAreaManager imageManager) {
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

	public boolean setInputs(int m) {
		inputmField.setText(m+"");
		return true;
	}
	
	public void setAllDisplay() {
		cycleBox.setSelected(true);
		powerBox.setSelected(true);
		subgroupBox.setSelected(true);
		orderBox.setSelected(true);
		cosetBox.setSelected(true);
	}
	
	// 创建输入关系的窗格
	private JPanel createInputSetPanel() {
		// 创建用于输入公式的窗格，使用盒式布局管理
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new GridLayout(1, 2, 5, 0));
		// 设置该窗格使用的带标题的边框
 		inputPanel.setBorder(BorderFactory.createTitledBorder("输入一个大于等于2小于等于5的正整数n，将展示元素为1到n的基集上双函数构成的置换群"));

 		int inputWidth = 20;
		// 创建一个标签用于提示输入集合信息
 		JPanel tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
		JLabel promptLabel = new JLabel("正整数(n)：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+V可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('n');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		inputmField = new JTextField(inputWidth);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(inputmField);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(inputmField);
		promptLabel = new JLabel("（建议输入的正整数n不要大于5，否则展示会需要很长时间且可能会耗尽内层而异常退出）", JLabel.LEFT);
		tempPanel.add(promptLabel);
		inputPanel.add(tempPanel);

		return inputPanel;
	}

	// 创建按钮及其所在的窗格
	private JPanel createButtonPanel() {
		// 创建一事件监听器，该监听器监听下述两个按钮上发生的事件
		SimpleListener listener = new SimpleListener();

		// 创建一临时性窗格，将这些按钮加入到该窗格，该窗格使用流式布局管理
		JPanel buttonPanelOne = new JPanel();
		buttonPanelOne.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 0));
		// 创建用于确定输入信息的“确定”按钮
		JButton actButton = new JButton("开始展示(Y)");
		actButton.setMnemonic('Y');					// 设置快捷键
		// 设置动作命令的名称，这可用来区分用户到底按了什么按钮
		actButton.setActionCommand("ok");			
		actButton.addActionListener(listener);		// 添加事件监听器
		buttonPanelOne.add(actButton);
		defaultButton = actButton;

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
		
		JButton exampleButton = new JButton("问题10.36");
		exampleButton.setActionCommand("example10_36");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("问题10.40");
		exampleButton.setActionCommand("example10_40");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		// 创建给出变量集信息和范式信息的窗格与相关组件
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 25, 0));
		infoPanel.setBorder(BorderFactory.createTitledBorder("选择要展示的信息（注意，如果基集元素大于等于5个，则展示子群及陪集会需要很长时间）："));
		cycleBox = new JCheckBox("是否循环群");
		infoPanel.add(cycleBox);
		powerBox = new JCheckBox("群元素的幂");
		infoPanel.add(powerBox);
		orderBox = new JCheckBox("群元素的阶");
		infoPanel.add(orderBox);
		subgroupBox = new JCheckBox("所有非平凡子群");
		infoPanel.add(subgroupBox);
		cosetBox = new JCheckBox("非平凡子群的陪集");
		infoPanel.add(cosetBox);
		normalBox = new JCheckBox("正规子群及其商群");
		infoPanel.add(normalBox);
		
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
				counter = counter + 1;
				doGroupPermDisplay();
				imagedAreaManager.revalidate();
			} else if (command.equals("cancel")) {
				ok = false;
				dialog.setVisible(false);
			} else if (command.equals("example10_36")) {
				setInputs(3);
				cycleBox.setSelected(true);
				powerBox.setSelected(true);
				orderBox.setSelected(true);
				subgroupBox.setSelected(true);
				cosetBox.setSelected(true);
				normalBox.setSelected(false);
			} else if (command.equals("example10_40")) {
				setInputs(3);
				cycleBox.setSelected(false);
				powerBox.setSelected(false);
				orderBox.setSelected(false);
				subgroupBox.setSelected(false);
				cosetBox.setSelected(true);
				normalBox.setSelected(true);
			} else if (command.equals("clear")) {
				textAreaManager.clearContent();
				imagedAreaManager.clearContent();
				imagedAreaManager.revalidate();
			}
		}
		
		public boolean doGroupPermDisplay() {
			int m = 3;
			try {
				m = Integer.parseInt(inputmField.getText());
			} catch (Exception exc) {
				JOptionPane.showMessageDialog(dialog, "输入的 m = [" + m + "不是合法的整数，使用缺省值 m = 3！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				m = 3;
			}

			GroupPermutation SGroup = null;
			GroupPermutation.Bijection[] elements = null;
			if (m == 3) {
				char[][] functionValue = {	{'1', '2', '3'}, {'2', '1', '3'}, {'3', '2', '1'}, {'1', '3', '2'}, {'2', '3', '1'}, {'3', '1', '2'} };
				elements = new GroupPermutation.Bijection[functionValue.length];
				for (int i = 1; i <= functionValue.length; i++) {
					elements[i-1] = new GroupPermutation.Bijection("f_"+i, functionValue[i-1]);
				}
				SGroup = new GroupPermutation(elements);
			} else {
				SGroup = new GroupPermutation(m);
				elements = SGroup.getElements();
			}

			String elementString = GroupPermutation.FunctionArrayToString(elements);
			String elementTable = SGroup.getLaTeXTableStringOfElements();
			String operatorString = SGroup.getLaTeXStringOfOperatorTable();
			
			textAreaManager.appendContent(counter + " : Group S(" + m + ") = \\{" + elementString + "\\}\n");
			textAreaManager.appendContent("\t" + operatorString + "\n");
			imagedAreaManager.appendPlainStringAsNewLine(counter + ": 置换群  ");
			imagedAreaManager.appendLaTeXStringToLastLine("S(" + m + ") = \\{" + elementString + "\\}");
			imagedAreaManager.appendPlainStringAsNewLine("        ");
			imagedAreaManager.appendLaTeXStringToLastLine(elementTable + "\\quad\\quad " + operatorString);

			if (cycleBox.isSelected()) {
				if (SGroup.isCycleGroup()) {
					GroupPermutation.Bijection[] generator = GroupPermutation.FunctionListToArray(SGroup.getGenerator());
					String message = GroupPermutation.FunctionArrayToString(generator);
					textAreaManager.appendContent("\tGroup S(" + m + ") is a cycle group, generator : " + message + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    置换群 ");
					imagedAreaManager.appendLaTeXStringToLastLine("S(" + m + ")");
					imagedAreaManager.appendPlainStringToLastLine("是循环群，生成元有：");
					imagedAreaManager.appendLaTeXStringToLastLine(message);
				} else {
					textAreaManager.appendContent("\tGroup S(" + m + ") is not a cycle group!\n");
					imagedAreaManager.appendPlainStringAsNewLine("    置换群 ");
					imagedAreaManager.appendLaTeXStringToLastLine("S(" + m + ")");
					imagedAreaManager.appendPlainStringToLastLine("不是循环群！");
				}
			}
			
			if (powerBox.isSelected()) {
				textAreaManager.appendContent("\tThe power of elements:\n");
				imagedAreaManager.appendPlainStringAsNewLine("    群元素的各次幂（包括群元素的逆）如下：");
				for (int i = 0; i < elements.length; i++) {
					StringBuffer message = new StringBuffer();
					GroupPermutation.Bijection inverse = SGroup.getInverse(elements[i]);
					message.append(elements[i] + "^{-1}=" + inverse + "\\quad\\quad\\quad\\quad ");
					for (int j = 1; j <= elements.length; j++) {
						GroupPermutation.Bijection power = SGroup.power(elements[i], j);
						message.append(elements[i] + "^{" + j + "}=" + power + "\\quad\\quad\\quad\\quad ");
						if (power == SGroup.getIdentity()) break;
					}
					String content = message.toString();
					textAreaManager.appendContent("\t\t" + content + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("        ");
					imagedAreaManager.appendLaTeXStringToLastLine(content);
				}
			}
			
			if (orderBox.isSelected()) {
				textAreaManager.appendContent("\tThe order of elements:");
				imagedAreaManager.appendPlainStringAsNewLine("    群元素的阶如下：");
				for (int i = 0; i < elements.length; i++) {
					int order = SGroup.getOrder(elements[i]);
					if (i % 10 == 0) {
						textAreaManager.appendContent("\n\t\t");
						imagedAreaManager.appendPlainStringAsNewLine("        ");
					}
					textAreaManager.appendContent("|" + elements[i] + "| = " + order + "\\quad\\quad\\quad\\quad ");
					imagedAreaManager.appendLaTeXStringToLastLine("|" + elements[i] + "| = " + order + "\\quad\\quad\\quad\\quad ");
				}
				textAreaManager.appendContent("\n");
			}

			if (subgroupBox.isSelected()) {
				textAreaManager.appendContent("\tThe non-trivial subgroups of group: ");
				imagedAreaManager.appendPlainStringAsNewLine("    群的非平凡子群如下：");
				List<List<GroupPermutation.Bijection>> allSubgroups = SGroup.getAllSubgroup();
				
				for (List<GroupPermutation.Bijection> subgroup : allSubgroups) {
					if (subgroup.size() == 1 || subgroup.size() == elements.length) continue;		// They are trivial groups!
					
					String subgroupString = GroupPermutation.FunctionListToString(subgroup);
					textAreaManager.appendContent("\t\tSub group: \\{" + subgroupString + "\\}");
					imagedAreaManager.appendPlainStringAsNewLine("        子群：");
					imagedAreaManager.appendLaTeXStringToLastLine("\\{" + subgroupString + "\\}");
					
					GroupPermutation.Bijection[] array = new GroupPermutation.Bijection[subgroup.size()];
					for (int i = 0; i < subgroup.size(); i++) array[i] = subgroup.get(i);
					List<GroupPermutation.Bijection> generatorList = SGroup.getGenerator(array);
					if (generatorList.size() > 0) {
						String message = GroupPermutation.FunctionListToString(generatorList);
						textAreaManager.appendContent(", it is a cycle subgroup, and has generators: " + message + ".\n");
						imagedAreaManager.appendPlainStringToLastLine(" 是循环子群，有生成元：");
						imagedAreaManager.appendLaTeXStringToLastLine(message);
					} else {
						textAreaManager.appendContent(", it is not a cycle subgroup!\n");
						imagedAreaManager.appendPlainStringToLastLine(" 不是循环子群！");
					}
					BinaryOperator<GroupPermutation.Bijection> subgroupOperatorTable = SGroup.getSubgroupOperatorTable(array);
					String message = subgroupOperatorTable.toLaTeXString();
					textAreaManager.appendContent("\t\t\t" + message + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("              ");
					imagedAreaManager.appendLaTeXStringToLastLine(message);
				}
			}

			if (cosetBox.isSelected() || normalBox.isSelected()) {
				textAreaManager.appendContent("\tThe cosets of the non-trivial subgroups of group: ");
				imagedAreaManager.appendPlainStringAsNewLine("    群的非平凡子群及其陪集如下：");
				List<List<GroupPermutation.Bijection>> allSubgroups = SGroup.getAllSubgroup();
				for (List<GroupPermutation.Bijection> subgroup : allSubgroups) {
					if (subgroup.size() == 1 || subgroup.size() == elements.length) continue;		// They are trivial groups!
					
					String subgroupString = GroupPermutation.FunctionListToString(subgroup);
					textAreaManager.appendContent("\t\tSub group: \\{" + subgroupString + "\\}, its left cosets include: ");
					imagedAreaManager.appendPlainStringAsNewLine("        子群：");
					imagedAreaManager.appendLaTeXStringToLastLine("\\{" + subgroupString + "\\}");
					imagedAreaManager.appendPlainStringToLastLine(" 的左陪集包括：");
					List<GroupPermutation.Coset> allCosets = SGroup.getAllLeftCoset(subgroup);
					for (int i = 0; i < allCosets.size(); i++) {
						GroupPermutation.Coset coset = allCosets.get(i);
						if (i % 6 == 0) {
							textAreaManager.appendContent("\n\t\t\t");
							imagedAreaManager.appendPlainStringAsNewLine("            ");
						}
						textAreaManager.appendContent(coset + "\\quad\\qquad ");
						imagedAreaManager.appendLaTeXStringToLastLine(coset + "\\quad\\qquad ");
					}
					textAreaManager.appendContent("\n");

					textAreaManager.appendContent("\t\t\tIts right cosets include: ");
					imagedAreaManager.appendPlainStringAsNewLine("            它的右陪集包括：");
					allCosets = SGroup.getAllRightCoset(subgroup);
					for (int i = 0; i < allCosets.size(); i++) {
						GroupPermutation.Coset coset = allCosets.get(i);
						if (i % 6 == 0) {
							textAreaManager.appendContent("\n\t\t\t");
							imagedAreaManager.appendPlainStringAsNewLine("            ");
						}
						textAreaManager.appendContent(coset + "\\quad\\qquad ");
						imagedAreaManager.appendLaTeXStringToLastLine(coset + "\\quad\\qquad ");
					}
					textAreaManager.appendContent("\n");
					
					if (normalBox.isSelected()) {
						textAreaManager.appendContent("\t\t\tSub group: \\{" + subgroupString + "\\}");
						imagedAreaManager.appendPlainStringAsNewLine("            子群 ");
						imagedAreaManager.appendLaTeXStringToLastLine("\\{" + subgroupString + "\\}");

						boolean isNormal = SGroup.isNormalSubgroup(subgroup);
						if (isNormal) {
							GroupPermutation.QuotientGroup quotient = SGroup.getQuotientGroup(subgroup);
							String message = quotient.getLaTeXStringOfOperatorTable();
							textAreaManager.appendContent(" is a normal subgroup， its quotient group operator table is as follows.\n");
							imagedAreaManager.appendPlainStringToLastLine(" 是正规子群，其商群的运算表如下：");
							textAreaManager.appendContent("\t\t\t\t" + message + "\n");
							imagedAreaManager.appendPlainStringAsNewLine("                ");
							imagedAreaManager.appendLaTeXStringToLastLine(message);
						} else {
							textAreaManager.appendContent(" is not a normal subgroup!\n");
							imagedAreaManager.appendPlainStringToLastLine(" 不是正规子群！");
						}
					}
				}
			}
			return true;
		}
	}
}
