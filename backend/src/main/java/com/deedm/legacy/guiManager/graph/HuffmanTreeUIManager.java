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
import java.util.ArrayList;
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

import com.deedm.legacy.graph.GraphNode;
import com.deedm.legacy.graph.GraphUtil;
import com.deedm.legacy.graph.HuffmanTree;
import com.deedm.legacy.graph.RootedForest;
import com.deedm.legacy.graph.WeightedTreeNode;
import com.deedm.legacy.graphicAnimation.AnimationArea;
import com.deedm.legacy.guiManager.ImagedTextAreaAnimator;
import com.deedm.legacy.guiManager.ImagedTextAreaManager;
import com.deedm.legacy.guiManager.MainGUIManager;
import com.deedm.legacy.guiManager.MainSwingFrame;
import com.deedm.legacy.guiManager.PlainTextAreaManager;
import com.deedm.legacy.util.Configuration;

/**
 * @author user
 *
 */
public class HuffmanTreeUIManager extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final int width = Math.max(2* MainSwingFrame.screenWidth/5, 1024)*(MainSwingFrame.screenResolution/96);
	private static final int height = Math.max(1*MainSwingFrame.screenHeight/3, 480)*(MainSwingFrame.screenResolution/96);
	private ImagedTextAreaManager imagedAreaManager = null;
	private PlainTextAreaManager textAreaManager = null;
	private ImagedTextAreaAnimator feedbackAnimator = null;

	private JDialog dialog = null;
	private JButton defaultButton = null;
	private JTextField leafField = null;
	private JCheckBox huffmanBox = null;
	private JCheckBox detailBox = null;
	private JCheckBox codeBox = null;

	private boolean ok = false;
	private int counter = 0;

	public HuffmanTreeUIManager(PlainTextAreaManager plainManager, ImagedTextAreaManager imageManager) {
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
		leafField.setText("");
		feedbackAnimator.update();
	}
	
	public boolean setInputs(String leafString) {
		leafField.setText(leafString);
		boolean success = true;
		List<GraphNode> nodeList = new ArrayList<GraphNode>();
		if (!leafString.contentEquals("")) {
			success = HuffmanTree.extractWeightedLeafsFromFormatedString(leafString, nodeList);
			if (success == true) {
				feedbackAnimator.appendLaTeXStringToLastLine("L = " + HuffmanTree.getWeightedLeafLaTeXString(nodeList));
			} else {
				feedbackAnimator.appendPlainStringAsNewLine("给出带权叶子顶点集的字符串 " + leafString + " 不是合法的表示带权叶子顶点集的字符串！");
				feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + GraphUtil.getErrorMessage());
				return false;
			}
		}
		return success;
	}
	
	public void setAllTravels() {
		huffmanBox.setSelected(true);
		codeBox.setSelected(true);
		detailBox.setSelected(true);
	}
	
	// 创建输入关系的窗格
	private JPanel createInputSetPanel() {
		// 创建用于输入公式的窗格，使用盒式布局管理
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new GridLayout(1, 1, 5, 0));
		// 设置该窗格使用的带标题的边框
 		inputPanel.setBorder(BorderFactory.createTitledBorder("带权叶子顶点集的格式串，形式为：{叶子顶点id(叶子顶点label)[权], ..., 叶子顶点id(叶子顶点label)[权]}，可省略叶子顶点label及界定它们的圆括号"));

 		int inputWidth = 96;
		// 创建一个标签用于提示输入集合信息
 		JPanel tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
		JLabel promptLabel = new JLabel("带权叶子顶点集(L)：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+V可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('V');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		leafField = new JTextField(inputWidth);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(leafField);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(leafField);
		inputPanel.add(tempPanel);

		JPanel displayPanel = new JPanel();
		displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
		// 设置该窗格使用的带标题的边框
		displayPanel.setBorder(BorderFactory.createTitledBorder("你输入的顶点集和边集如下（下面使用的是顶点的label而非id，GraphViz生成的图片中也是显示label）："));
 		AnimationArea area = new AnimationArea(width, 3*height/5);
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
		JButton actButton = new JButton("开始执行(Y)");
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
		
		JButton exampleButton = new JButton("例子9.31");
		exampleButton.setActionCommand("example9_31");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		// 创建给出变量集信息和范式信息的窗格与相关组件
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 25, 0));
		infoPanel.setBorder(BorderFactory.createTitledBorder("选择进行的计算："));
		huffmanBox = new JCheckBox("执行Huffman算法");
		infoPanel.add(huffmanBox);
		detailBox = new JCheckBox("给出算法计算过程");
		infoPanel.add(detailBox);
		codeBox = new JCheckBox("给出叶子顶点的编码");
		infoPanel.add(codeBox);
		
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
				doHuffmanTree();
				feedbackAnimator.update();
				imagedAreaManager.revalidate();
			} else if (command.equals("cancel")) {
				ok = false;
				dialog.setVisible(false);
			} else if (command.equals("remove")) {
				clearInput();
				codeBox.setSelected(false);
				detailBox.setSelected(false);
				huffmanBox.setSelected(false);
			} else if (command.equals("generate")) {
				feedbackAnimator.clearContent();
				generateInputs();
				feedbackAnimator.update();
			} else if (command.equals("check")) {
				feedbackAnimator.clearContent();
				boolean success = checkInputs();
				if (success != true) {
					JOptionPane.showMessageDialog(dialog, "输入的带权叶子顶点集格式化串不完全符合要求！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(dialog, "输入的带权叶子顶点集格式化串语法正确，可以构造哈夫曼树！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				}
				feedbackAnimator.update();
			} else if (command.equals("example9_31")) {
				feedbackAnimator.clearContent();
				String nodeString = "{a[15], b[12], c[25], d[8], e[20], f[6], g[8], h[6]}";
				setInputs(nodeString);
				huffmanBox.setSelected(true);
				detailBox.setSelected(true);
				codeBox.setSelected(true);
				feedbackAnimator.update();
			} else if (command.equals("clear")) {
				textAreaManager.clearContent();
				imagedAreaManager.clearContent();
				imagedAreaManager.revalidate();
			}
		}
		
		public boolean doHuffmanTree() {
			String leafString = leafField.getText();
			List<GraphNode> nodeList = new ArrayList<GraphNode>();
			boolean success = HuffmanTree.extractWeightedLeafsFromFormatedString(leafString, nodeList);
			if (success == false) {
				feedbackAnimator.appendPlainStringAsNewLine("给出带权叶子顶点集的字符串 " + leafString + " 不是合法的表示带权叶子顶点集的字符串！");
				feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + GraphUtil.getErrorMessage());
				return false;
			}
			feedbackAnimator.appendLaTeXStringToLastLine("L = " + HuffmanTree.getWeightedLeafLaTeXString(nodeList));
			textAreaManager.appendContent(counter + ": the weighted leaf set L = " + HuffmanTree.getWeightedLeafLaTeXString(nodeList) + "\n");
			imagedAreaManager.appendPlainStringAsNewLine(counter + ": 带权叶子顶点集 ");
			imagedAreaManager.appendLaTeXStringToLastLine("L = " + HuffmanTree.getWeightedLeafLaTeXString(nodeList));

			if (!huffmanBox.isSelected()) return false;
			WeightedTreeNode[] leafs = new WeightedTreeNode[nodeList.size()];
			for (int i = 0; i < nodeList.size(); i++) leafs[i] = (WeightedTreeNode)nodeList.get(i);
			HuffmanTree huffmanTree = HuffmanTree.createHuffmanTree(leafs);
			
			if (detailBox.isSelected()) {
				HuffmanTree.HuffmanRecorder recorder = HuffmanTree.getHuffmanRecorder();
				List<HuffmanTree.HuffmanStepRecorder> stepList = recorder.getStepList();
				for (int i = 0; i < stepList.size(); i++) {
					HuffmanTree.HuffmanStepRecorder stepRecorder = stepList.get(i);
					boolean labelEdge = false;
					String prompt = "        当前得到的森林: ";
					if (i == stepList.size()-1) {
						labelEdge = true;
						prompt = "        最终的哈夫曼树: ";
					}
					int step = stepRecorder.getStep();
					String nodeString = stepRecorder.getLaTeXStringOfNodeArray();
					textAreaManager.appendContent("\t\tStep " + step + ": " + nodeString + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("        Step " + step + ": ");
					imagedAreaManager.appendLaTeXStringToLastLine(nodeString);
					RootedForest forest = stepRecorder.getRootedForest();
					String dotFileName = Configuration.dataFilePath + "Huffman" + step + ".dot";
					String pngFileName = Configuration.dataFilePath + "Huffman" + step + ".png";
					try {
						PrintWriter writer = new PrintWriter(dotFileName);
						forest.simplyWriteToDotFile(writer, false, labelEdge);
						success = MainGUIManager.generatePNGFile(dotFileName, pngFileName, true);
						if (success) {
							textAreaManager.appendContent("\t\tHere gives the graph of the current forest!\n");
							imagedAreaManager.appendPlainStringAsNewLine(prompt);
							imagedAreaManager.appendImageFileToLastLine(pngFileName);
						} else {
							textAreaManager.appendContent("\t\tCan not generate the graph of the current forest!\n");
							textAreaManager.appendContent(MainGUIManager.errorMessage);
							imagedAreaManager.appendPlainStringAsNewLine("        当前的树（森林）: 无法生成！");
						}
					} catch (Exception e) {
						textAreaManager.appendContent("\t\tCan not generate the graph of the current forest!\n");
						textAreaManager.appendContent(MainGUIManager.errorMessage);
						imagedAreaManager.appendPlainStringAsNewLine("        当前的树（森林）: 无法生成！");
					}
				}
			} else {
				String dotFileName = Configuration.dataFilePath + "Huffman.dot";
				String pngFileName = Configuration.dataFilePath + "Huffman.png";
				try {
					PrintWriter writer = new PrintWriter(dotFileName);
					huffmanTree.simplyWriteToDotFile(writer, false, true);
					success = MainGUIManager.generatePNGFile(dotFileName, pngFileName, true);
					if (success) {
						textAreaManager.appendContent("\t\tHere gives the graph of the current forest!\n");
						imagedAreaManager.appendPlainStringAsNewLine("        哈夫曼树: ");
						imagedAreaManager.appendImageFileToLastLine(pngFileName);
					} else {
						textAreaManager.appendContent("\t\tCan not generate the graph of the current forest!\n");
						textAreaManager.appendContent(MainGUIManager.errorMessage);
						imagedAreaManager.appendPlainStringAsNewLine("        哈夫曼树: 无法生成！");
					}
				} catch (Exception e) {
					textAreaManager.appendContent("\t\tCan not generate the graph of the current forest!\n");
					textAreaManager.appendContent(MainGUIManager.errorMessage);
					imagedAreaManager.appendPlainStringAsNewLine("        哈夫曼树: 无法生成！");
				}
			}
			double weight = huffmanTree.getTotalWeight();
			textAreaManager.appendContent("\tThe total weight of the constrcted huffman tree is " + weight + "\n");
			imagedAreaManager.appendPlainStringAsNewLine("    构造得到的Huffman树总权重是：" + weight);
			String message = huffmanTree.getTotalWeightCalculateLaTeXString();
			textAreaManager.appendContent("\t\t" + message + " = "+ weight + "\n");
			imagedAreaManager.appendPlainStringAsNewLine("        ");
			imagedAreaManager.appendLaTeXStringToLastLine(message + " = " + weight);
			
			if (codeBox.isSelected()) {
				String[] codes = huffmanTree.getCodeOfLeafNodes(leafs);
				textAreaManager.appendContent("\tThe codes of the leaf node are as follows: ");
				imagedAreaManager.appendPlainStringAsNewLine("    带权叶子顶点的编码如下：");
				for (int i = 0; i < codes.length; i++) {
					if (i % 8 == 0) {
						textAreaManager.appendContent("\n\t\t");
						imagedAreaManager.appendPlainStringAsNewLine("        ");
					}
					textAreaManager.appendContent(leafs[i].getLabel() + " : " + codes[i] + "\t\t");
					imagedAreaManager.appendLaTeXStringToLastLine(leafs[i].getLabel() + " : " + codes[i] + "\\quad\\quad\\quad\\quad");
				}
				textAreaManager.appendContent("\n");
			}
			return true;
		}
		
		public boolean generateInputs() {
			int nodeNumber = (int)(Math.random() * 8) + 5;
			List<GraphNode> nodeList = HuffmanTree.randomGenerateWeightedLeafNodes(nodeNumber, 100);
			
			String leafString = HuffmanTree.getWeightedLeafFormatedString(nodeList);
			leafField.setText(leafString);
			huffmanBox.setSelected(true);
			codeBox.setSelected(true);
			detailBox.setSelected(true);
			checkInputs();		// 让生成的信息通过合法性检查后反馈在输入界面之上
			return true;
		}
		
		public boolean checkInputs() {
			String leafString = leafField.getText();
			boolean success = true;
			List<GraphNode> nodeList = new ArrayList<GraphNode>();
			if (!leafString.contentEquals("")) {
				success = HuffmanTree.extractWeightedLeafsFromFormatedString(leafString, nodeList);
				if (success == true) {
					feedbackAnimator.appendLaTeXStringToLastLine("L = " + HuffmanTree.getWeightedLeafLaTeXString(nodeList));
				} else {
					feedbackAnimator.appendPlainStringAsNewLine("给出带权叶子顶点集的字符串 " + leafString + " 不是合法的表示带权叶子顶点集的字符串！");
					feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + GraphUtil.getErrorMessage());
					return false;
				}
			}
			return success;
		}
	}
}
