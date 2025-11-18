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
import java.io.PrintWriter;

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
import com.deedm.legacy.algebra.Lattice;
import com.deedm.legacy.graph.AbstractGraph;
import com.deedm.legacy.guiManager.ImagedTextAreaManager;
import com.deedm.legacy.guiManager.MainGUIManager;
import com.deedm.legacy.guiManager.MainSwingFrame;
import com.deedm.legacy.guiManager.PlainTextAreaManager;
import com.deedm.legacy.setrelfun.PartialOrder;
import com.deedm.legacy.setrelfun.Set;
import com.deedm.legacy.util.Configuration;

/**
 * @author user
 *
 */
public class BoolAlgebraUIManager extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final int width = Math.max(2* MainSwingFrame.screenWidth/5, 1024)*(MainSwingFrame.screenResolution/96);
	private static final int height = Math.max(1*MainSwingFrame.screenHeight/5, 240)*(MainSwingFrame.screenResolution/96);
	private ImagedTextAreaManager imagedAreaManager = null;
	private PlainTextAreaManager textAreaManager = null;

	private JDialog dialog = null;
	private JButton defaultButton = null;
	private JTextField inputmField = null;
	private JCheckBox hasseBox = null;
	private JCheckBox operatorBox = null;
	private JCheckBox complementBox = null;

	private boolean ok = false;
	private int counter = 0;

	public BoolAlgebraUIManager(PlainTextAreaManager plainManager, ImagedTextAreaManager imageManager) {
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
		hasseBox.setSelected(true);
		operatorBox.setSelected(true);
		complementBox.setSelected(true);
	}
	
	// 创建输入关系的窗格
	private JPanel createInputSetPanel() {
		// 创建用于输入公式的窗格，使用盒式布局管理
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new GridLayout(1, 2, 5, 0));
		// 设置该窗格使用的带标题的边框
 		inputPanel.setBorder(BorderFactory.createTitledBorder("输入一个大于等于6的正整数m，将展示以m的正因子为基集，整除为偏序的格F(m)"));

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
		promptLabel = new JLabel("展示格F(m)（建议输入的正整数m的正因子数不要超过30个，否则展示需要的时间会太长）", JLabel.LEFT);
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
		
		JButton exampleButton = new JButton("问题10.50(1)");
		exampleButton.setActionCommand("example10_50_1");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("问题10.50(2)");
		exampleButton.setActionCommand("example10_50_2");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("问题10.50(3)");
		exampleButton.setActionCommand("example10_50_3");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);

		exampleButton = new JButton("问题10.50(4)");
		exampleButton.setActionCommand("example10_50_4");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		// 创建给出变量集信息和范式信息的窗格与相关组件
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 25, 0));
		infoPanel.setBorder(BorderFactory.createTitledBorder("选择要展示的信息（注意，如果基集元素大于等于5个，则展示子群及陪集会需要很长时间）："));
		hasseBox = new JCheckBox("给出格的哈斯图");
		infoPanel.add(hasseBox);
		operatorBox = new JCheckBox("给出格的运算表");
		infoPanel.add(operatorBox);
		complementBox = new JCheckBox("给出格的补元情况");
		infoPanel.add(complementBox);
		
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
				doBooleanAlgebraDisplay();
				imagedAreaManager.revalidate();
			} else if (command.equals("generate")) {
				int m = (int)(Math.random() * 1000) + 6;
				setInputs(m);
				hasseBox.setSelected(true);
				operatorBox.setSelected(true);
				complementBox.setSelected(true);
			} else if (command.equals("cancel")) {
				ok = false;
				dialog.setVisible(false);
			} else if (command.equals("example10_50_1")) {
				setInputs(110);
				hasseBox.setSelected(true);
				operatorBox.setSelected(true);
				complementBox.setSelected(true);
			} else if (command.equals("example10_50_2")) {
				setInputs(6);
				hasseBox.setSelected(true);
				operatorBox.setSelected(true);
				complementBox.setSelected(true);
			} else if (command.equals("example10_50_3")) {
				setInputs(12);
				hasseBox.setSelected(true);
				operatorBox.setSelected(true);
				complementBox.setSelected(true);
			} else if (command.equals("example10_50_4")) {
				setInputs(24);
				hasseBox.setSelected(true);
				operatorBox.setSelected(true);
				complementBox.setSelected(true);
			} else if (command.equals("clear")) {
				textAreaManager.clearContent();
				imagedAreaManager.clearContent();
				imagedAreaManager.revalidate();
			}
		}
		
		public boolean doBooleanAlgebraDisplay() {
			int m = 1001;
			try {
				m = Integer.parseInt(inputmField.getText());
			} catch (Exception exc) {
				JOptionPane.showMessageDialog(dialog, "输入的 m = [" + m + "不是合法的整数，使用缺省值 m = 1001！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				m = 1001;
			}

			boolean isIntElement = true;
			PartialOrder partialOrder = PartialOrder.createFactorDivisionOrder(m);
			Set setA = partialOrder.getFromSet();
			String setAString = setA.toLaTeXString(true);

			textAreaManager.appendContent(counter + ": The lattice F(" + m + ") = " + setAString + "\n");
			imagedAreaManager.appendPlainStringAsNewLine(counter + ": 以 ");
			imagedAreaManager.appendLaTeXStringToLastLine(""+m);
			imagedAreaManager.appendPlainStringToLastLine(" 的所有正因子为集合，整除关系为偏序构成的格 ");
			imagedAreaManager.appendLaTeXStringToLastLine("F(" + m + ") = " + setAString);

			if (hasseBox.isSelected()) {
				String dotFileName = Configuration.dataFilePath + "PartialOrderR.dot";
				String pngFileName = Configuration.dataFilePath + "PartialOrderR.png";
				try {
					PrintWriter writer = new PrintWriter(dotFileName);
					AbstractGraph graph = partialOrder.getHasseDigram(isIntElement);
					graph.simplyWriteToDotFile(writer);
					boolean success = MainGUIManager.generatePNGFile(dotFileName, pngFileName, false);
					if (success) {
						textAreaManager.appendContent("\t\tHere gives the Hasse diagram of the partial order R!\n");
						imagedAreaManager.appendPlainStringAsNewLine("        哈斯图: ");
						imagedAreaManager.appendImageFileToLastLine(pngFileName);
					} else {
						textAreaManager.appendContent("\t\tCan not generate the Hasse diagram of the partial order R!\n");
						textAreaManager.appendContent(MainGUIManager.errorMessage);
						imagedAreaManager.appendPlainStringAsNewLine("       哈斯图: 无法生成！");
					}
				} catch (Exception e) {
					textAreaManager.appendContent("\t\tCan not generate the Hasse diagram of the partial order R!\n");
					textAreaManager.appendContent("Exception message: " + e.getMessage());
					imagedAreaManager.appendPlainStringAsNewLine("        哈斯图: 无法生成！");
				}
			}
			
			Lattice lattice = Lattice.createLatticeFromPoset(partialOrder);
			if (operatorBox.isSelected()) {
				BinaryOperator<Character> supOperator = lattice.getSupOperator();
				String supOptableString = supOperator.toLaTeXString(true);
				BinaryOperator<Character> subOperator = lattice.getSubOperator();
				String subOptableString = subOperator.toLaTeXString(true);
				textAreaManager.appendContent("\tThe operator tables are as follows:\n");
				textAreaManager.appendContent("\t\t" + supOptableString);
				textAreaManager.appendContent("\t\t" + subOptableString);
				imagedAreaManager.appendPlainStringAsNewLine("    这个格的求上确界（即最小公倍数） ");
				imagedAreaManager.appendLaTeXStringToLastLine(supOperator.getName());
				imagedAreaManager.appendPlainStringToLastLine(" 和求下确界运算 （即最大公约数） ");
				imagedAreaManager.appendLaTeXStringToLastLine(subOperator.getName());
				imagedAreaManager.appendPlainStringToLastLine(" 的运算表如下：");
				imagedAreaManager.appendPlainStringAsNewLine("        ");
				imagedAreaManager.appendLaTeXStringToLastLine(supOptableString);
				imagedAreaManager.appendPlainStringToLastLine("    ");
				imagedAreaManager.appendLaTeXStringToLastLine(subOptableString);
			}
			
			boolean isBoolAlgebra = lattice.isBooleanAlgebra();
			char greatest = lattice.getGreatestElement();
			String greatestString = Set.getElementLabel(greatest, isIntElement);
			char least = lattice.getLeastElement();
			String leastString = Set.getElementLabel(least, isIntElement);
			textAreaManager.appendContent("\the greatest element is " + greatestString + ", and the least element is " + leastString + "!\n");
			imagedAreaManager.appendPlainStringAsNewLine("    格的最大元是 ");
			imagedAreaManager.appendLaTeXStringToLastLine(greatestString);
			imagedAreaManager.appendPlainStringToLastLine(" ，最小元是 ");
			imagedAreaManager.appendLaTeXStringToLastLine(leastString);

			if (isBoolAlgebra) {
				textAreaManager.appendContent("\tThis lattice is a boolean algebra!\n");
				imagedAreaManager.appendPlainStringAsNewLine("    这个格是布尔代数！");
			} else {
				textAreaManager.appendContent("\tThis lattice is NOT a boolean algebra!\n");
				imagedAreaManager.appendPlainStringAsNewLine("    这个格不是布尔代数！");
			}
			if (complementBox.isSelected()) {
				char[] complements = new char[setA.length()];
				for (int i = 0; i < setA.length(); i++) {
					char element = setA.get(i);
					String elementString = Set.getElementLabel(element, isIntElement);
					int complementNumber = lattice.getComplement(element, complements);
					if (complementNumber > 0) {
						String complementString = Set.getElementsLabel(complements, isIntElement, complementNumber);
						textAreaManager.appendContent("\t\tThe complements of the element " + elementString + " have " + complementString + "\n");
						imagedAreaManager.appendPlainStringAsNewLine("        元素 ");
						imagedAreaManager.appendLaTeXStringToLastLine(elementString);
						imagedAreaManager.appendPlainStringToLastLine(" 的补元是：");
						imagedAreaManager.appendLaTeXStringToLastLine(complementString);
					} else {
						textAreaManager.appendContent("\t\tThe element " + elementString + " has NOT complement element!\n");
						imagedAreaManager.appendPlainStringAsNewLine("        元素 ");
						imagedAreaManager.appendLaTeXStringToLastLine(elementString);
						imagedAreaManager.appendPlainStringToLastLine(" 没有补元！");
					}
				}
			}
			return true;
		}
	}
}
