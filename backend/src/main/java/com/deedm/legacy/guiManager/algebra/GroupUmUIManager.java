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
import com.deedm.legacy.algebra.GroupUnitModulo;
import com.deedm.legacy.guiManager.ImagedTextAreaManager;
import com.deedm.legacy.guiManager.MainSwingFrame;
import com.deedm.legacy.guiManager.PlainTextAreaManager;

/**
 * @author user
 *
 */
public class GroupUmUIManager extends JPanel {

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

	private boolean ok = false;
	private int counter = 0;

	public GroupUmUIManager(PlainTextAreaManager plainManager, ImagedTextAreaManager imageManager) {
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
 		inputPanel.setBorder(BorderFactory.createTitledBorder("输入一个大于等于3的正整数m，以展示群U(m)的信息"));

 		int inputWidth = 20;
		// 创建一个标签用于提示输入集合信息
 		JPanel tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
		JLabel promptLabel = new JLabel("正整数(m)：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+V可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('m');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		inputmField = new JTextField(inputWidth);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(inputmField);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(inputmField);
		promptLabel = new JLabel("（建议输入的正整数m不要大于30，否则展示会需要很长时间）", JLabel.LEFT);
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

		// 创建用于确定输入信息的“生成”按钮
		actButton = new JButton("随机生成(G)");
		actButton.setMnemonic('G');					// 设置快捷键
		// 设置动作命令的名称，这可用来区分用户到底按了什么按钮
		actButton.setActionCommand("generate");
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
		
		JButton exampleButton = new JButton("问题10.28(1)");
		exampleButton.setActionCommand("example10_28_1");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("问题10.28(2)");
		exampleButton.setActionCommand("example10_28_2");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("例子10.31");
		exampleButton.setActionCommand("example10_31");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("例子10.32");
		exampleButton.setActionCommand("example10_32");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("例子10.35");
		exampleButton.setActionCommand("example10_35");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		// 创建给出变量集信息和范式信息的窗格与相关组件
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 25, 0));
		infoPanel.setBorder(BorderFactory.createTitledBorder("选择要展示的信息（注意，如果群U(m)的元素超过30个，展示子群及其陪集可能需要很长时间）："));
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
				doGroupUmDisplay();
				imagedAreaManager.revalidate();
			} else if (command.equals("generate")) {
				int m = (int)(Math.random() * 29) + 1;
				setInputs(m);
				cycleBox.setSelected(true);
				powerBox.setSelected(true);
				orderBox.setSelected(true);
				subgroupBox.setSelected(true);
				cosetBox.setSelected(true);
			} else if (command.equals("cancel")) {
				ok = false;
				dialog.setVisible(false);
			} else if (command.equals("example10_28_1")) {
				setInputs(5);
				cycleBox.setSelected(true);
				powerBox.setSelected(true);
				orderBox.setSelected(false);
				subgroupBox.setSelected(false);
				cosetBox.setSelected(false);
			} else if (command.equals("example10_28_2")) {
				setInputs(7);
				cycleBox.setSelected(true);
				powerBox.setSelected(true);
				orderBox.setSelected(false);
				subgroupBox.setSelected(false);
				cosetBox.setSelected(false);
			} else if (command.equals("example10_31")) {
				setInputs(5);
				cycleBox.setSelected(false);
				powerBox.setSelected(true);
				orderBox.setSelected(true);
				subgroupBox.setSelected(false);
				cosetBox.setSelected(false);
			} else if (command.equals("example10_32")) {
				setInputs(7);
				cycleBox.setSelected(false);
				powerBox.setSelected(false);
				orderBox.setSelected(false);
				subgroupBox.setSelected(true);
				cosetBox.setSelected(false);
			} else if (command.equals("example10_35")) {
				setInputs(7);
				cycleBox.setSelected(false);
				powerBox.setSelected(false);
				orderBox.setSelected(false);
				subgroupBox.setSelected(true);
				cosetBox.setSelected(true);
			} else if (command.equals("clear")) {
				textAreaManager.clearContent();
				imagedAreaManager.clearContent();
				imagedAreaManager.revalidate();
			}
		}
		
		public boolean doGroupUmDisplay() {
			int m = 11;
			try {
				m = Integer.parseInt(inputmField.getText());
			} catch (Exception exc) {
				JOptionPane.showMessageDialog(dialog, "输入的 m = [" + m + "不是合法的整数，使用缺省值 m = 11！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				m = 11;
			}

			GroupUnitModulo UGroup = new GroupUnitModulo(m);
			Integer[] elements = UGroup.getElements();
			String elementString = GroupUnitModulo.integerArrayToString(elements);
			String operatorString = UGroup.getLaTeXStringOfOperatorTable();
			
			textAreaManager.appendContent(counter + " : Group U(" + m + ") = \\{" + elementString + "\\}\n");
			textAreaManager.appendContent("\t" + operatorString + "\n");
			imagedAreaManager.appendPlainStringAsNewLine(counter + ": 群  ");
			imagedAreaManager.appendLaTeXStringToLastLine("U(" + m + ") = \\{" + elementString + "\\}");
			imagedAreaManager.appendPlainStringAsNewLine("        ");
			imagedAreaManager.appendLaTeXStringToLastLine(operatorString);

			if (cycleBox.isSelected()) {
				if (UGroup.isCycleGroup()) {
					int[] generator = GroupUnitModulo.integerListToArray(UGroup.getGenerator());
					String message = GroupUnitModulo.integerArrayToString(generator);
					textAreaManager.appendContent("\tGroup U(" + m + ") is a cycle group, generator : " + message + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("    群 ");
					imagedAreaManager.appendLaTeXStringToLastLine("U(" + m + ")");
					imagedAreaManager.appendPlainStringToLastLine("是循环群，生成元有：");
					imagedAreaManager.appendLaTeXStringToLastLine(message);
				} else {
					textAreaManager.appendContent("\tGroup U(" + m + ") is not a cycle group!\n");
					imagedAreaManager.appendPlainStringAsNewLine("    群 ");
					imagedAreaManager.appendLaTeXStringToLastLine("U(" + m + ")");
					imagedAreaManager.appendPlainStringToLastLine("不是循环群！");
				}
			}
			
			if (powerBox.isSelected()) {
				textAreaManager.appendContent("\tThe power of elements:\n");
				imagedAreaManager.appendPlainStringAsNewLine("    群元素的各次幂（包括群元素的逆）如下：");
				for (int i = 0; i < elements.length; i++) {
					StringBuffer message = new StringBuffer();
					int inverse = UGroup.getInverse(elements[i]);
					message.append(elements[i] + "^{-1}=" + inverse + "\\quad\\quad\\quad\\quad ");
					for (int j = 1; j <= elements.length; j++) {
						int power = UGroup.power(elements[i], j);
						message.append(elements[i] + "^{" + j + "}=" + power + "\\quad\\quad\\quad\\quad ");
						if (power == UGroup.getIdentity()) break;
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
					int order = UGroup.getOrder(elements[i]);
					if (i % 10 == 0) {
						textAreaManager.appendContent("\n\t\t");
						imagedAreaManager.appendPlainStringAsNewLine("        ");
					}
					textAreaManager.appendContent("|" + elements[i] + "| = " + order + "\\quad\\quad\\quad\\quad ");
					imagedAreaManager.appendLaTeXStringToLastLine("|" + elements[i] + "| = " + order + "\\quad\\quad\\quad\\quad ");
				}
				textAreaManager.appendContent("\n");
			}

			if (subgroupBox.isSelected() || cosetBox.isSelected()) {
				textAreaManager.appendContent("\tThe non-trivial subgroups of group: ");
				imagedAreaManager.appendPlainStringAsNewLine("    群的非平凡子群如下：");
				List<List<Integer>> allSubgroups = UGroup.getAllSubgroup();
				for (List<Integer> subgroup : allSubgroups) {
					if (subgroup.size() == 1 || subgroup.size() == elements.length) continue;		// They are trivial groups!
					
					String subgroupString = GroupUnitModulo.integerListToString(subgroup);
					textAreaManager.appendContent("\t\tSub group: \\{" + subgroupString + "\\}");
					imagedAreaManager.appendPlainStringAsNewLine("        子群：");
					imagedAreaManager.appendLaTeXStringToLastLine("\\{" + subgroupString + "\\}");
					
					Integer[] array = new Integer[subgroup.size()];
					for (int i = 0; i < subgroup.size(); i++) array[i] = subgroup.get(i);
					List<Integer> generatorList = UGroup.getGenerator(array);
					if (generatorList.size() > 0) {
						String message = GroupUnitModulo.integerListToString(generatorList);
						textAreaManager.appendContent(", it is a cycle subgroup, and has generators: " + message + ".\n");
						imagedAreaManager.appendPlainStringToLastLine(" 是循环子群，有生成元：");
						imagedAreaManager.appendLaTeXStringToLastLine(message);
					} else {
						textAreaManager.appendContent(", it is not a cycle subgroup!\n");
						imagedAreaManager.appendPlainStringToLastLine(" 不是循环子群！");
					}
					BinaryOperator<Integer> subgroupOperatorTable = UGroup.getSubgroupOperatorTable(array);
					String message = subgroupOperatorTable.toLaTeXString();
					textAreaManager.appendContent("\t\t\t" + message + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("              ");
					imagedAreaManager.appendLaTeXStringToLastLine(message);
					if (cosetBox.isSelected()) {
						textAreaManager.appendContent("\t\t\tSub group: \\{" + subgroupString + "\\}, its cosets include: ");
						imagedAreaManager.appendPlainStringAsNewLine("            子群 ");
						imagedAreaManager.appendLaTeXStringToLastLine("\\{" + subgroupString + "\\}");
						imagedAreaManager.appendPlainStringToLastLine(" 的陪集包括：");
						List<int[]> allCosets = UGroup.getAllCoset(subgroup);
						for (int i = 0; i < allCosets.size(); i++) {
							int[] coset = allCosets.get(i);
							if (i % 6 == 0) {
								textAreaManager.appendContent("\n\t\t\t\t");
								imagedAreaManager.appendPlainStringAsNewLine("                ");
							}
							textAreaManager.appendContent("\\{" + GroupUnitModulo.integerArrayToString(coset) + "\\}\\quad\\qquad ");
							imagedAreaManager.appendLaTeXStringToLastLine("\\{" + GroupUnitModulo.integerArrayToString(coset) + "\\}\\quad\\qquad ");
						}
						textAreaManager.appendContent("\n");
					}
				}
			}
			
			return true;
		}
	}
}
