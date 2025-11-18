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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.deedm.legacy.graph.DefaultGraph;
import com.deedm.legacy.graph.DefaultGraphNode;
import com.deedm.legacy.graph.GraphEdge;
import com.deedm.legacy.graph.GraphNode;
import com.deedm.legacy.graph.GraphUtil;
import com.deedm.legacy.graph.RootedTree;
import com.deedm.legacy.graph.RootedTreeNode;
import com.deedm.legacy.graphicAnimation.AnimationArea;
import com.deedm.legacy.guiManager.ImagedTextAreaAnimator;
import com.deedm.legacy.guiManager.ImagedTextAreaManager;
import com.deedm.legacy.guiManager.MainGUIManager;
import com.deedm.legacy.guiManager.MainSwingFrame;
import com.deedm.legacy.guiManager.PlainTextAreaManager;
import com.deedm.legacy.setrelfun.Matrix;
import com.deedm.legacy.util.Configuration;

/**
 * @author user
 *
 */
public class TreeTravelUIManager extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private static final int width = Math.max(3* MainSwingFrame.screenWidth/5, 1536)*(MainSwingFrame.screenResolution/96);
	private static final int height = Math.max(2*MainSwingFrame.screenHeight/5, 576)*(MainSwingFrame.screenResolution/96);
	private ImagedTextAreaManager imagedAreaManager = null;
	private PlainTextAreaManager textAreaManager = null;
	private ImagedTextAreaAnimator feedbackAnimator = null;

	private JDialog dialog = null;
	private JButton defaultButton = null;
	private JTextField nodeField = null;
	private JTextArea edgeArea = null;
	private JCheckBox inorderBox = null;
	private JCheckBox preorderBox = null;
	private JCheckBox postorderBox = null;
	private JCheckBox useAdjacencyBox = null;
	private JCheckBox useIncidenceBox = null;
	private JCheckBox useGraphBox = null;
	private JTextField rootField = null;

	private boolean ok = false;
	private int counter = 0;


	public TreeTravelUIManager(PlainTextAreaManager plainManager, ImagedTextAreaManager imageManager) {
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
		nodeField.setText("");
		edgeArea.setText("");
		rootField.setText("");
		feedbackAnimator.update();
	}
	
	public boolean setInputs(String nodesString, String rootString, String edgesString) {
		boolean directed = false;
		
		nodeField.setText(nodesString);
		edgeArea.setText(edgesString);
		rootField.setText(rootString);
		boolean success = true;
		
		List<GraphNode> nodeList = new ArrayList<GraphNode>();
		if (!nodesString.contentEquals("")) {
			success = GraphUtil.extractNodesFromFormatedString(nodesString, nodeList);
			if (success == true) {
				feedbackAnimator.appendLaTeXStringAsNewLine("V = " + GraphUtil.getNodeLaTeXString(nodeList));
				boolean foundRoot = false;
				for (GraphNode node : nodeList) {
					if (node.getId().equals(rootString)) {
						foundRoot = true;
						break;
					}
				}
				if (foundRoot == false) {
					feedbackAnimator.appendPlainStringAsNewLine("给出的顶点集不包含根节点！");
				} else {
					feedbackAnimator.appendPlainStringAsNewLine("根节点：");
					feedbackAnimator.appendLaTeXStringToLastLine(rootString);
				}
			} else {
				feedbackAnimator.appendPlainStringAsNewLine("给出顶点集的字符串 " + nodesString + " 不是合法的表示顶点集的字符串！");
				feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + GraphUtil.getErrorMessage());
				return false;
			}
		}
		if (!edgesString.contentEquals("")) {
			List<GraphEdge> edgeList = new ArrayList<GraphEdge>();
			success = GraphUtil.extractEdgesFromFormatedString(nodeList, edgesString, edgeList, directed);
			if (success != true) {
				feedbackAnimator.appendPlainStringAsNewLine("字符串 " + edgesString + "  不是合法的表示边集字符串！");
				feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + GraphUtil.getErrorMessage());
				return false;
			} else {
				feedbackAnimator.appendLaTeXStringAsNewLine("E = " + GraphUtil.getEdgeLaTeXString(edgeList));
			}
		}
		return success;
	}
	
	public void setAllTravels() {
		inorderBox.setSelected(true);
		preorderBox.setSelected(true);
		postorderBox.setSelected(true);
	}
	
	// 创建输入关系的窗格
	private JPanel createInputSetPanel() {
		// 创建用于输入公式的窗格，使用盒式布局管理
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new GridLayout(2, 2, 5, 0));
		// 设置该窗格使用的带标题的边框
 		inputPanel.setBorder(BorderFactory.createTitledBorder("请输入树的顶点集和边集格式化串，顶点集的格式化串形式是：{顶点id(顶点label), ..., 顶点id(顶点label)}，边集的格式化串形式是：{(顶点id, 顶点id), ..., (顶点id, 顶点id)}"));

 		int inputWidth = 96;
		// 创建一个标签用于提示输入集合信息
 		JPanel tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
		JLabel promptLabel = new JLabel("顶点集(V)：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+V可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('V');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		nodeField = new JTextField(inputWidth);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(nodeField);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(nodeField);
		inputPanel.add(tempPanel);
		
		tempPanel = new JPanel();
		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 35, 15));
		// 创建一个标签用于提示输入性别信息
		promptLabel = new JLabel("根节点：", JLabel.RIGHT);
		rootField = new JTextField(12);
		tempPanel.add(promptLabel);
		tempPanel.add(rootField);
		inputPanel.add(tempPanel);

 		tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 15));
		// 创建一个标签用于提示输入集合信息
		promptLabel = new JLabel("边集(E)：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+R可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('E');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		edgeArea = new JTextArea(3, inputWidth-8);
		edgeArea.setLineWrap(true);;
		JScrollPane scroll = new JScrollPane(edgeArea);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(edgeArea);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(scroll);
		inputPanel.add(tempPanel);
		
 		tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 15));
		useAdjacencyBox = new JCheckBox("给出邻接矩阵");
		tempPanel.add(useAdjacencyBox);
		useIncidenceBox = new JCheckBox("给出关联矩阵");
		tempPanel.add(useIncidenceBox);
		useGraphBox = new JCheckBox("给出图形化表示");
		tempPanel.add(useGraphBox);
		inputPanel.add(tempPanel);

		JPanel displayPanel = new JPanel();
		displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
		// 设置该窗格使用的带标题的边框
		displayPanel.setBorder(BorderFactory.createTitledBorder("你输入的顶点集和边集如下（下面使用的是顶点的label而非id，GraphViz生成的图片中也是显示label，label中可含有运算符号，而id中不可以含有运算符号）："));
 		AnimationArea area = new AnimationArea(width, 5*height/3);
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
		JButton actButton = new JButton("遍历树(Y)");
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
		
		JButton exampleButton = new JButton("例子9.26");
		exampleButton.setActionCommand("example9_26");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("例子9.27");
		exampleButton.setActionCommand("example9_27");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		// 创建给出变量集信息和范式信息的窗格与相关组件
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 25, 0));
		infoPanel.setBorder(BorderFactory.createTitledBorder("选择进行的遍历："));
		preorderBox = new JCheckBox("前序遍历");
		infoPanel.add(preorderBox);
		inorderBox = new JCheckBox("中序遍历");
		infoPanel.add(inorderBox);
		postorderBox = new JCheckBox("后序遍历");
		infoPanel.add(postorderBox);
		
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
				doGraphTravel();
				feedbackAnimator.update();
				imagedAreaManager.revalidate();
			} else if (command.equals("cancel")) {
				ok = false;
				dialog.setVisible(false);
			} else if (command.equals("remove")) {
				clearInput();
				preorderBox.setSelected(false);
				postorderBox.setSelected(false);
				inorderBox.setSelected(false);
			} else if (command.equals("generate")) {
				feedbackAnimator.clearContent();
				generateInputs();
				feedbackAnimator.update();
			} else if (command.equals("check")) {
				feedbackAnimator.clearContent();
				boolean success = checkInputs();
				if (success != true) {
					JOptionPane.showMessageDialog(dialog, "输入的顶点集和边集格式化串不完全符合要求！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(dialog, "输入的顶点集和边集格式化串语法正确，可以进行树的遍历！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				}
				feedbackAnimator.update();
			} else if (command.equals("example9_26")) {
				feedbackAnimator.clearContent();
				String nodeString = "{a, b, c, d, e, f, g, h, i, j, k, p}";
				String edgeString = "{(a,b), (a,j), (a,h), (b,c), (b,k), (b,e), (e,d), (e,f), (h,g), (h,p), (h,i)}";
				String rootString = "a";
				setInputs(nodeString, rootString, edgeString);
				inorderBox.setSelected(true);
				preorderBox.setSelected(true);
				postorderBox.setSelected(true);
				useAdjacencyBox.setSelected(false);
				useIncidenceBox.setSelected(false);
				useGraphBox.setSelected(true);
				feedbackAnimator.update();
			} else if (command.equals("example9_27")) {
				feedbackAnimator.clearContent();
				String nodeString = "{times(*), plus(+), minus(-), divide(/), three(3), seven(7), anotherthree(3), five(5), two(2)}";
				String edgeString = "{(times,plus), (times,minus), (plus,three), (plus,divide), (divide,seven), (divide,anotherthree), (minus,five), (minus,two)}";
				String rootString = "times";
				setInputs(nodeString, rootString, edgeString);
				inorderBox.setSelected(true);
				preorderBox.setSelected(true);
				postorderBox.setSelected(true);
				useAdjacencyBox.setSelected(false);
				useIncidenceBox.setSelected(false);
				useGraphBox.setSelected(true);
				feedbackAnimator.update();
			} else if (command.equals("clear")) {
				textAreaManager.clearContent();
				imagedAreaManager.clearContent();
				imagedAreaManager.revalidate();
			}
		}
		
		public boolean doGraphTravel() {
			String nodesString = nodeField.getText();
			List<GraphNode> nodeList = new ArrayList<GraphNode>();
			boolean success = GraphUtil.extractNodesFromFormatedString(nodesString, nodeList);
			if (success != true) {
				feedbackAnimator.appendPlainStringAsNewLine("给出顶点集的字符串 " + nodesString + " 不是合法的表示顶点集的字符串！");
				feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + GraphUtil.getErrorMessage());
				return false;
			}

			feedbackAnimator.appendLaTeXStringAsNewLine("V = " + GraphUtil.getNodeLaTeXString(nodeList));
			textAreaManager.appendContent(counter + ": G = (V, E), the vertice set V = " + nodesString + "\n");
			imagedAreaManager.appendPlainStringAsNewLine(counter + ": 图 ");
			imagedAreaManager.appendLaTeXStringToLastLine("G = (V, E)");
			imagedAreaManager.appendPlainStringToLastLine("，顶点集合 ");
			imagedAreaManager.appendLaTeXStringToLastLine("V = " + GraphUtil.getNodeLaTeXString(nodeList));

			DefaultGraphNode rootNode = null;
			String rootString = rootField.getText();
			for (GraphNode node : nodeList) {
				if (node.getId().equals(rootString)) {
					rootNode = (DefaultGraphNode)node;
					break;
				}
			}
			if (rootNode == null) {
				feedbackAnimator.appendPlainStringAsNewLine("给出的顶点集不包含根节点！");
				textAreaManager.appendContent("\tThe given root node is not included in the node list!\n");
				imagedAreaManager.appendPlainStringAsNewLine("    错误：给出的顶点集不包含根节点！");
			} else {
				feedbackAnimator.appendPlainStringAsNewLine("根节点：");
				feedbackAnimator.appendLaTeXStringToLastLine(rootNode.getLabel());
				textAreaManager.appendContent("\tRoot node: " + rootNode.getLabel() + "\n");
				imagedAreaManager.appendPlainStringAsNewLine("根节点：");
				imagedAreaManager.appendLaTeXStringToLastLine(rootNode.getLabel());
			}
			
			boolean directed = false;

			String edgesString = edgeArea.getText();
			List<GraphEdge> edgeList = new ArrayList<GraphEdge>();
			success = GraphUtil.extractEdgesFromFormatedString(nodeList, edgesString, edgeList, directed);
			if (success != true) {
				textAreaManager.appendContent("\tThe string " + edgesString + " dose not give the edges of the graph legally!\n");
				imagedAreaManager.appendPlainStringAsNewLine("    字符串 " + edgesString + " 没有合法地给出图的边集！");
				imagedAreaManager.appendPlainStringAsNewLine("    错误信息： " + GraphUtil.getErrorMessage());
				feedbackAnimator.appendPlainStringAsNewLine("字符串 " + edgesString + "  不是合法的表示边集字符串！");
				return false;
			}
			feedbackAnimator.appendLaTeXStringAsNewLine("E = " + GraphUtil.getEdgeLaTeXString(edgeList));
			textAreaManager.appendContent("\tE = " + edgesString + "\n");
			imagedAreaManager.appendPlainStringAsNewLine("    边集: ");
			imagedAreaManager.appendLaTeXStringToLastLine("E = " + GraphUtil.getEdgeLaTeXString(edgeList));

			DefaultGraph graph = new DefaultGraph("InputGraph");
			graph.setNodes(nodeList);
			graph.setEdges(edgeList);

			if (useGraphBox.isSelected()) {
				String dotFileName = Configuration.dataFilePath + "InputTree.dot";
				String pngFileName = Configuration.dataFilePath + "InputTree.png";
				try {
					PrintWriter writer = new PrintWriter(dotFileName);
					graph.simplyWriteToDotFile(writer);
					success = MainGUIManager.generatePNGFile(dotFileName, pngFileName, true);
					if (success) {
						textAreaManager.appendContent("\t\tHere gives the graph G = <V, E>!\n");
						imagedAreaManager.appendPlainStringAsNewLine("    图形化表示: ");
						imagedAreaManager.appendImageFileToLastLine(pngFileName);
					} else {
						textAreaManager.appendContent("\t\tCan not generate the graph of G = <V, E>!\n");
						textAreaManager.appendContent(MainGUIManager.errorMessage);
						imagedAreaManager.appendPlainStringAsNewLine("    图形化表示: 无法生成！");
					}
				} catch (Exception e) {
					textAreaManager.appendContent("\t\tCan not generate the graph of G = <V, E>!\n");
					textAreaManager.appendContent(MainGUIManager.errorMessage);
					imagedAreaManager.appendPlainStringAsNewLine("    图形化表示: 无法生成！");
				}
			}
			if (useAdjacencyBox.isSelected() || useIncidenceBox.isSelected()) {
				textAreaManager.appendContent("\tThe matrix of the graph: \n");
				imagedAreaManager.appendPlainStringAsNewLine("    图的矩阵：");
				if (useAdjacencyBox.isSelected()) {
					Matrix matrix = graph.getAdjacencyMatrix();
					textAreaManager.appendContent("\t\tAdjacency matrix: A = " + matrix.toLaTeXString());
					imagedAreaManager.appendPlainStringToLastLine("邻接矩阵 ");
					imagedAreaManager.appendLaTeXStringToLastLine("A = " + matrix.toLaTeXString());
				}
				if (useAdjacencyBox.isSelected()) {
					Matrix matrix = graph.getIncidenceMatrix();
					textAreaManager.appendContent("\t\tIncidence matrix: I = " + matrix.toLaTeXString());
					imagedAreaManager.appendPlainStringToLastLine("  关联矩阵 ");
					imagedAreaManager.appendLaTeXStringToLastLine("I = " + matrix.toLaTeXString());
				}
			}
			
			if (!graph.isUndirectTree()) {
				feedbackAnimator.appendPlainStringAsNewLine("给出的图不是一棵无向树！");
				textAreaManager.appendContent("\tThe given graph is not a undirected tree!\n");
				imagedAreaManager.appendPlainStringAsNewLine("    给出的图不是一棵无向树！");
				return false;
			}
			
			RootedTree tree = RootedTree.getAnRootedTree(graph, rootNode, directed);
			
			if (preorderBox.isSelected()) {
				List<RootedTreeNode> travelResultList = tree.getPreorderNodeList();
				String message = createLaTeXStringForTreeNodeList(travelResultList);
				textAreaManager.appendContent("\tPre-order travel: " + message + "\n");
				imagedAreaManager.appendPlainStringAsNewLine("    前序遍历序列：");
				imagedAreaManager.appendLaTeXStringToLastLine(message);
			}
			if (inorderBox.isSelected()) {
				List<RootedTreeNode> travelResultList = tree.getInorderNodeList();
				String message = createLaTeXStringForTreeNodeList(travelResultList);
				textAreaManager.appendContent("\tIn-order travel: " + message + "\n");
				imagedAreaManager.appendPlainStringAsNewLine("    中序遍历序列：");
				imagedAreaManager.appendLaTeXStringToLastLine(message);
			}
			
			if (postorderBox.isSelected()) {
				List<RootedTreeNode> travelResultList = tree.getPostorderNodeList();
				String message = createLaTeXStringForTreeNodeList(travelResultList);
				textAreaManager.appendContent("\tPost-order travel: " + message + "\n");
				imagedAreaManager.appendPlainStringAsNewLine("    后序遍历序列：");
				imagedAreaManager.appendLaTeXStringToLastLine(message);
			}
			return true;
		}
		
		public String createLaTeXStringForTreeNodeList(List<RootedTreeNode> nodeList) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("\\langle ");
			boolean isFirst = true;
			for (RootedTreeNode node : nodeList) {
				if (isFirst) {
					buffer.append(node.getLabel());
					isFirst = false;
				} else buffer.append(", " + node.getLabel());
			}
			buffer.append("\\rangle ");
			
			return buffer.toString();
		}
		
		public boolean generateInputs() {
			int nodeNumber = (int)(Math.random() * 12) + 1;
			DefaultGraph graph = GraphUtil.randomGenerateUndirectedTree(nodeNumber, false);

			String nodesString = GraphUtil.getFormatedNodeString(graph.getNodes());
			String edgesString = GraphUtil.getFormatedEdgeString(graph.getEdges());
			String rootString = graph.getNodes().get(0).getId();
			nodeField.setText(nodesString);
			edgeArea.setText(edgesString);
			rootField.setText(rootString);
			
			inorderBox.setSelected(true);
			preorderBox.setSelected(true);
			postorderBox.setSelected(true);
			useAdjacencyBox.setSelected(false);
			useIncidenceBox.setSelected(false);
			useGraphBox.setSelected(true);
			checkInputs();		// 让生成的信息通过合法性检查后反馈在输入界面之上
			return true;
		}
		
		public boolean checkInputs() {
			boolean directed = false;
			
			String nodesString = nodeField.getText();
			String edgesString = edgeArea.getText();
			String rootString = rootField.getText();
			boolean success = true;
			
			List<GraphNode> nodeList = new ArrayList<GraphNode>();
			if (!nodesString.contentEquals("")) {
				success = GraphUtil.extractNodesFromFormatedString(nodesString, nodeList);
				if (success == true) {
					feedbackAnimator.appendLaTeXStringAsNewLine("V = " + GraphUtil.getNodeLaTeXString(nodeList));
					boolean foundRoot = false;
					for (GraphNode node : nodeList) {
						if (node.getId().equals(rootString)) {
							foundRoot = true;
							break;
						}
					}
					if (foundRoot == false) {
						feedbackAnimator.appendPlainStringAsNewLine("给出的顶点集不包含根节点！");
						success = false;
					} else {
						feedbackAnimator.appendPlainStringAsNewLine("根节点 : ");
						feedbackAnimator.appendLaTeXStringToLastLine(rootString);
					}
				} else {
					feedbackAnimator.appendPlainStringAsNewLine("给出顶点集的字符串 " + nodesString + " 不是合法的表示顶点集的字符串！");
					feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + GraphUtil.getErrorMessage());
					return false;
				}
			}
			if (!edgesString.contentEquals("")) {
				List<GraphEdge> edgeList = new ArrayList<GraphEdge>();
				success = GraphUtil.extractEdgesFromFormatedString(nodeList, edgesString, edgeList, directed);
				if (success != true) {
					feedbackAnimator.appendPlainStringAsNewLine("字符串 " + edgesString + "  不是合法的表示边集字符串！");
					feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + GraphUtil.getErrorMessage());
					return false;
				} else {
					feedbackAnimator.appendLaTeXStringAsNewLine("E = " + GraphUtil.getEdgeLaTeXString(edgeList));
				}
			}
			return success;
		}
	}
}
