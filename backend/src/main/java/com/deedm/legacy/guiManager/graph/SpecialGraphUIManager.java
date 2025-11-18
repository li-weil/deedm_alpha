/**
 * 
 */
package com.deedm.legacy.guiManager.graph;

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

import com.deedm.legacy.graph.AbstractGraph;
import com.deedm.legacy.graph.GraphUtil;
import com.deedm.legacy.guiManager.ImagedTextAreaManager;
import com.deedm.legacy.guiManager.MainGUIManager;
import com.deedm.legacy.guiManager.MainSwingFrame;
import com.deedm.legacy.guiManager.PlainTextAreaManager;
import com.deedm.legacy.util.Configuration;

/**
 * @author user
 *
 */
public class SpecialGraphUIManager extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final int width = Math.max(2* MainSwingFrame.screenWidth/5, 1024)*(MainSwingFrame.screenResolution/96);
	private static final int height = Math.max(1*MainSwingFrame.screenHeight/6, 240)*(MainSwingFrame.screenResolution/96);
	private ImagedTextAreaManager imagedAreaManager = null;
	private PlainTextAreaManager textAreaManager = null;

	private JDialog dialog = null;
	private JButton defaultButton = null;
	private JTextField inputnField = null;
	private JTextField inputmField = null;
	private JCheckBox completeBox = null;
	private JCheckBox cycleBox = null;
	private JCheckBox wheelBox = null;
	private JCheckBox hypercubBox = null;
	private JCheckBox bigraphBox = null;

	private boolean ok = false;
	private int counter = 0;

	public SpecialGraphUIManager(PlainTextAreaManager plainManager, ImagedTextAreaManager imageManager) {
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

	public boolean setInputs(int n, int m) {
		inputnField.setText(n+"");
		inputmField.setText(m+"");
		return true;
	}
	
	public void setAllDisplay() {
		completeBox.setSelected(true);
		wheelBox.setSelected(true);
		cycleBox.setSelected(true);
		hypercubBox.setSelected(true);
		bigraphBox.setSelected(true);
	}
	
	// 创建输入关系的窗格
	private JPanel createInputSetPanel() {
		// 创建用于输入公式的窗格，使用盒式布局管理
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new GridLayout(1, 2, 5, 0));
		// 设置该窗格使用的带标题的边框
 		inputPanel.setBorder(BorderFactory.createTitledBorder("输入要展示的图的顶点数n，m（用于完全二部图）"));

 		int inputWidth = 20;
		// 创建一个标签用于提示输入集合信息
 		JPanel tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
		JLabel promptLabel = new JLabel("顶点数(n)：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+V可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('n');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		inputnField = new JTextField(inputWidth);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(inputnField);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(inputnField);
		inputPanel.add(tempPanel);

 		tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
		promptLabel = new JLabel("顶点数(m)：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+V可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('m');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		inputmField = new JTextField(inputWidth);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(inputmField);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(inputmField);
		
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

		// 创建给出变量集信息和范式信息的窗格与相关组件
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 25, 0));
		infoPanel.setBorder(BorderFactory.createTitledBorder("选择要展示的图形："));
		completeBox = new JCheckBox("完全图");
		infoPanel.add(completeBox);
		cycleBox = new JCheckBox("圈图");
		infoPanel.add(cycleBox);
		wheelBox = new JCheckBox("轮图");
		infoPanel.add(wheelBox);
		hypercubBox = new JCheckBox("超立方体");
		infoPanel.add(hypercubBox);
		bigraphBox = new JCheckBox("完全二部图");
		infoPanel.add(bigraphBox);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.add(infoPanel);
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
				doSpecialGraphDisplay();
				imagedAreaManager.revalidate();
			} else if (command.equals("cancel")) {
				ok = false;
				dialog.setVisible(false);
			} else if (command.equals("clear")) {
				textAreaManager.clearContent();
				imagedAreaManager.clearContent();
				imagedAreaManager.revalidate();
			}
		}
		
		public boolean doSpecialGraphDisplay() {
			int n = 3;
			int m = 5;
			
			try {
				n = Integer.parseInt(inputnField.getText());
				m = Integer.parseInt(inputmField.getText());
			} catch (Exception exc) {
				JOptionPane.showMessageDialog(dialog, "输入的 n = [" + n + "或 m = " + m + "不是合法的整数，使用缺省值n = 3, m = 5！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
			}
			textAreaManager.appendContent(counter + " : n = " + n + ", m = " + m);
			imagedAreaManager.appendPlainStringAsNewLine(counter + ": 展示特殊图形的顶点数  ");
			imagedAreaManager.appendLaTeXStringToLastLine("n = " + n + ", \\quad\\quad m = " + m);
			
			if (completeBox.isSelected()) {
				AbstractGraph graph = GraphUtil.createCompleteGraph(n);
				String dotFileName = Configuration.dataFilePath + "Complete.dot";
				String pngFileName = Configuration.dataFilePath + "Complete.png";
				try {
					PrintWriter writer = new PrintWriter(dotFileName);
					graph.simplyWriteToDotFile(writer);
					boolean success = MainGUIManager.generatePNGFile(dotFileName, pngFileName, false);
					if (success) {
						textAreaManager.appendContent("\t\tHere gives the complete graph K_" + n + "!\n");
						imagedAreaManager.appendPlainStringAsNewLine("        完全图 ");
						imagedAreaManager.appendLaTeXStringToLastLine("K_" + n);
						imagedAreaManager.appendPlainStringToLastLine(" ： ");
						imagedAreaManager.appendImageFileToLastLine(pngFileName);
					} else {
						textAreaManager.appendContent("\t\tCan not generate the complete graph K_" + n + "!\n");
						textAreaManager.appendContent(MainGUIManager.errorMessage);
						imagedAreaManager.appendPlainStringAsNewLine("        完全图: 无法生成！");
					}
				} catch (Exception e) {
					textAreaManager.appendContent("\t\tCan not generate the complete graph K_" + n + "!\n");
					textAreaManager.appendContent(MainGUIManager.errorMessage);
					imagedAreaManager.appendPlainStringAsNewLine("        完全图: 无法生成！");
				}
			}
			if (cycleBox.isSelected()) {
				AbstractGraph graph = GraphUtil.createCycleGraph(n);
				String dotFileName = Configuration.dataFilePath + "Cycle.dot";
				String pngFileName = Configuration.dataFilePath + "Cycle.png";
				try {
					PrintWriter writer = new PrintWriter(dotFileName);
					graph.simplyWriteToDotFile(writer);
					boolean success = MainGUIManager.generatePNGFile(dotFileName, pngFileName, false);
					if (success) {
						textAreaManager.appendContent("\t\tHere gives the cycle graph C_" + n + "!\n");
						imagedAreaManager.appendPlainStringAsNewLine("        圈图 ");
						imagedAreaManager.appendLaTeXStringToLastLine("C_" + n);
						imagedAreaManager.appendPlainStringToLastLine(" ： ");
						imagedAreaManager.appendImageFileToLastLine(pngFileName);
					} else {
						textAreaManager.appendContent("\t\tCan not generate the cycle graph C_" + n + "!\n");
						textAreaManager.appendContent(MainGUIManager.errorMessage);
						imagedAreaManager.appendPlainStringAsNewLine("        圈图: 无法生成！");
					}
				} catch (Exception e) {
					textAreaManager.appendContent("\t\tCan not generate the cycle graph C_" + n + "!\n");
					textAreaManager.appendContent(MainGUIManager.errorMessage);
					imagedAreaManager.appendPlainStringAsNewLine("        圈图: 无法生成！");
				}
			}
			if (wheelBox.isSelected()) {
				AbstractGraph graph = GraphUtil.createWheelGraph(n);
				String dotFileName = Configuration.dataFilePath + "Wheel.dot";
				String pngFileName = Configuration.dataFilePath + "Wheel.png";
				try {
					PrintWriter writer = new PrintWriter(dotFileName);
					graph.simplyWriteToDotFile(writer);
					boolean success = MainGUIManager.generatePNGFile(dotFileName, pngFileName, false);
					if (success) {
						textAreaManager.appendContent("\t\tHere gives the wheel graph C_" + n + "!\n");
						imagedAreaManager.appendPlainStringAsNewLine("        轮图 ");
						imagedAreaManager.appendLaTeXStringToLastLine("W_" + n);
						imagedAreaManager.appendPlainStringToLastLine(" ： ");
						imagedAreaManager.appendImageFileToLastLine(pngFileName);
					} else {
						textAreaManager.appendContent("\t\tCan not generate the whell graph C_" + n + "!\n");
						textAreaManager.appendContent(MainGUIManager.errorMessage);
						imagedAreaManager.appendPlainStringAsNewLine("        轮图: 无法生成！");
					}
				} catch (Exception e) {
					textAreaManager.appendContent("\t\tCan not generate the wheel graph C_" + n + "!\n");
					textAreaManager.appendContent(MainGUIManager.errorMessage);
					imagedAreaManager.appendPlainStringAsNewLine("        轮图: 无法生成！");
				}
			}
			if (hypercubBox.isSelected()) {
				AbstractGraph graph = GraphUtil.createHyperCubeGraph(n);
				String dotFileName = Configuration.dataFilePath + "Hypercube.dot";
				String pngFileName = Configuration.dataFilePath + "Hypercube.png";
				try {
					PrintWriter writer = new PrintWriter(dotFileName);
					graph.simplyWriteToDotFile(writer);
					boolean success = MainGUIManager.generatePNGFile(dotFileName, pngFileName, false);
					if (success) {
						textAreaManager.appendContent("\t\tHere gives the hyper cube Q_" + n + "!\n");
						imagedAreaManager.appendPlainStringAsNewLine("        超立方图 ");
						imagedAreaManager.appendLaTeXStringToLastLine("C_" + n);
						imagedAreaManager.appendPlainStringToLastLine(" ： ");
						imagedAreaManager.appendImageFileToLastLine(pngFileName);
					} else {
						textAreaManager.appendContent("\t\tCan not generate the hyper cube Q_" + n + "!\n");
						textAreaManager.appendContent(MainGUIManager.errorMessage);
						imagedAreaManager.appendPlainStringAsNewLine("        超立方图: 无法生成！");
					}
				} catch (Exception e) {
					textAreaManager.appendContent("\t\tCan not generate the hyper cube Q_" + n + "!\n");
					textAreaManager.appendContent(MainGUIManager.errorMessage);
					imagedAreaManager.appendPlainStringAsNewLine("        超立方图: 无法生成！");
				}
			}
			if (bigraphBox.isSelected()) {
				AbstractGraph graph = GraphUtil.createCompleteBiGraph(n, m);
				String dotFileName = Configuration.dataFilePath + "bigraph.dot";
				String pngFileName = Configuration.dataFilePath + "bigraph.png";
				try {
					PrintWriter writer = new PrintWriter(dotFileName);
					graph.simplyWriteToDotFile(writer);
					boolean success = MainGUIManager.generatePNGFile(dotFileName, pngFileName, false);
					if (success) {
						textAreaManager.appendContent("\t\tHere gives the bi-graph K_{" + n + ", " + m + "}!\n");
						imagedAreaManager.appendPlainStringAsNewLine("        完全二部图 ");
						imagedAreaManager.appendLaTeXStringToLastLine("K_{" + n + ", " + m + "}");
						imagedAreaManager.appendPlainStringToLastLine(" ： ");
						imagedAreaManager.appendImageFileToLastLine(pngFileName);
					} else {
						textAreaManager.appendContent("\t\tCan not generate the bi-graph K_{" + n + ", " + m + "}!\n");
						textAreaManager.appendContent(MainGUIManager.errorMessage);
						imagedAreaManager.appendPlainStringAsNewLine("        完全二部图: 无法生成！");
					}
				} catch (Exception e) {
					textAreaManager.appendContent("\t\tCan not generate the bi-graph K_{" + n + ", " + m + "}!\n");
					textAreaManager.appendContent(MainGUIManager.errorMessage);
					imagedAreaManager.appendPlainStringAsNewLine("        完全二部图: 无法生成！");
				}
			}
			return true;
		}
	}
}
