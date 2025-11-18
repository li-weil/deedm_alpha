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
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.deedm.legacy.graph.DefaultGraph;
import com.deedm.legacy.graph.DefaultGraphNode;
import com.deedm.legacy.graph.DoubleMatrix;
import com.deedm.legacy.graph.GraphEdge;
import com.deedm.legacy.graph.GraphNode;
import com.deedm.legacy.graph.GraphUtil;
import com.deedm.legacy.graph.WeightedGraph;
import com.deedm.legacy.graph.WeightedGraphPath;
import com.deedm.legacy.graphicAnimation.AnimationArea;
import com.deedm.legacy.guiManager.ImagedTextAreaAnimator;
import com.deedm.legacy.guiManager.ImagedTextAreaManager;
import com.deedm.legacy.guiManager.MainGUIManager;
import com.deedm.legacy.guiManager.MainSwingFrame;
import com.deedm.legacy.guiManager.PlainTextAreaManager;
import com.deedm.legacy.util.Configuration;

public class ShortestPathUIManager extends JPanel {
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
	private JTextField startField = null;
	private JCheckBox dijkstraBox = null;
	private JCheckBox detailBox = null;
	private JCheckBox pathBox = null;
	private JCheckBox pathGraphBox = null;
	private JCheckBox useMatrixBox = null;
	private JCheckBox useGraphBox = null;
	private JRadioButton undirectedButton = null;
	private JRadioButton directedButton = null;

	private boolean ok = false;
	private int counter = 0;


	public ShortestPathUIManager(PlainTextAreaManager plainManager, ImagedTextAreaManager imageManager) {
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
		startField.setText("");
		feedbackAnimator.update();
	}
	
	public void setGraphType(boolean isDirect) {
		if (isDirect == true) {
			directedButton.setSelected(true);
			undirectedButton.setSelected(false);
		} else {
			directedButton.setSelected(false);
			undirectedButton.setSelected(true);
		}
	}
	
	public boolean setInputs(String nodesString, String startString, String edgesString) {
		boolean directed = false;
		if (directedButton.isSelected()) directed = true;
		
		nodeField.setText(nodesString);
		edgeArea.setText(edgesString);
		startField.setText(startString);
		boolean success = true;
		
		List<GraphNode> nodeList = new ArrayList<GraphNode>();
		if (!nodesString.contentEquals("")) {
			success = GraphUtil.extractNodesFromFormatedString(nodesString, nodeList);
			if (success == true) {
				feedbackAnimator.appendLaTeXStringAsNewLine("V = " + GraphUtil.getNodeLaTeXString(nodeList));
				boolean foundStart = false;
				for (GraphNode node : nodeList) {
					if (node.getId().equals(startString)) {
						foundStart = true;
						break;
					}
				}
				if (foundStart == false) {
					feedbackAnimator.appendPlainStringAsNewLine("给出的顶点集不包含起始顶点！");
				} else {
					feedbackAnimator.appendPlainStringAsNewLine("起始顶点：");
					feedbackAnimator.appendLaTeXStringToLastLine(startString);
				}
			} else {
				feedbackAnimator.appendPlainStringAsNewLine("给出顶点集的字符串 " + nodesString + " 不是合法的表示顶点集的字符串！");
				feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + GraphUtil.getErrorMessage());
				return false;
			}
		}
		if (!edgesString.contentEquals("")) {
			List<GraphEdge> edgeList = new ArrayList<GraphEdge>();
			success = GraphUtil.extractWeightedEdgesFromFormatedString(nodeList, edgesString, edgeList, directed);
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
		dijkstraBox.setSelected(true);
		pathBox.setSelected(true);
		detailBox.setSelected(true);
		pathGraphBox.setSelected(true);
	}
	
	// 创建输入关系的窗格
	private JPanel createInputSetPanel() {
		// 创建用于输入公式的窗格，使用盒式布局管理
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new GridLayout(2, 2, 5, 0));
		// 设置该窗格使用的带标题的边框
 		inputPanel.setBorder(BorderFactory.createTitledBorder("请输入带权图的顶点集和边集格式化串，顶点集的格式化串形式是：{顶点id(顶点label), ..., 顶点id(顶点label)}，边集的格式化串形式是：{边label[权] = (顶点id, , 顶点id), ..., 边label[权] = (顶点id, 顶点id)}"));

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
		promptLabel = new JLabel("起始顶点(S)：", JLabel.RIGHT);
		promptLabel.setDisplayedMnemonic('V');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		startField = new JTextField(12);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(startField);
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(startField);
		
		// 创建一个标签用于提示输入性别信息
		promptLabel = new JLabel("图的类型：", JLabel.RIGHT);
		// 创建用于将下两个广播按钮捆绑成一组的按钮组对象，同一组的广播按钮只能选中一个，
		// 不同组的广播按钮则可以同时选中不同的广播按钮
		ButtonGroup group = new ButtonGroup();
		// 创建选择性别的广播按钮
		undirectedButton = new JRadioButton("无向图");
		undirectedButton.setSelected(true);		// 缺省时选中该广播按钮
		group.add(undirectedButton);
		directedButton = new JRadioButton("有向图");
		group.add(directedButton);
		tempPanel.add(promptLabel);
		tempPanel.add(undirectedButton);			// 注意是添加广播按钮组件
		tempPanel.add(directedButton);		// 而不是添加按钮组对象
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
		useMatrixBox = new JCheckBox("给出带权图的距离矩阵");
		tempPanel.add(useMatrixBox);
		useGraphBox = new JCheckBox("给出图形化表示");
		tempPanel.add(useGraphBox);
		inputPanel.add(tempPanel);

		JPanel displayPanel = new JPanel();
		displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
		// 设置该窗格使用的带标题的边框
		displayPanel.setBorder(BorderFactory.createTitledBorder("你输入的顶点集和边集如下（下面使用的是顶点的label而非id，GraphViz生成的图片中也是显示label，label中可含有一些特殊符号，例如*,+,-等运算符号，而id中不可以含有这些符号）："));
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
		
		JButton exampleButton = new JButton("例子9.28");
		exampleButton.setActionCommand("example9_28");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		exampleButton = new JButton("例子1.12");
		exampleButton.setActionCommand("example1_12");			
		exampleButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelTwo.add(exampleButton);
		
		// 创建给出变量集信息和范式信息的窗格与相关组件
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 25, 0));
		infoPanel.setBorder(BorderFactory.createTitledBorder("选择进行的遍历和计算："));
		dijkstraBox = new JCheckBox("执行Dijsktra算法");
		infoPanel.add(dijkstraBox);
		detailBox = new JCheckBox("给出算法计算过程");
		infoPanel.add(detailBox);
		pathBox = new JCheckBox("给出最短路径详情");
		infoPanel.add(pathBox);
		pathGraphBox = new JCheckBox("给出最短路径图形");
		infoPanel.add(pathGraphBox);
		
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
				doShortestPath();
				feedbackAnimator.update();
				imagedAreaManager.revalidate();
			} else if (command.equals("cancel")) {
				ok = false;
				dialog.setVisible(false);
			} else if (command.equals("remove")) {
				clearInput();
				pathBox.setSelected(false);
				detailBox.setSelected(false);
				dijkstraBox.setSelected(false);
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
					JOptionPane.showMessageDialog(dialog, "输入的顶点集和边集格式化串语法正确，可以计算最短路径！", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
				}
				feedbackAnimator.update();
			} else if (command.equals("example9_28")) {
				feedbackAnimator.clearContent();
				String nodeString = "{v1, v2, v3, v4, v5, v6}";
				String startString = "v1";
				String edgeString = "{[1]=<v1,v3>, [7]=<v1,v2>, [6]=<v3,v2>, [4]=<v2,v4>, [1]=<v2,v6>, [3]=<v5,v2>, [2]=<v3,v5>, [7]=<v3,v6>, [5]=<v5,v4>, [3]=<v6,v5> }";
				setGraphType(true);
				setInputs(nodeString, startString, edgeString);
				dijkstraBox.setSelected(true);
				pathBox.setSelected(true);
				useMatrixBox.setSelected(true);
				useGraphBox.setSelected(true);
				feedbackAnimator.update();
			} else if (command.equals("example1_12")) {
				feedbackAnimator.clearContent();
				String nodeString = "{GZ, WH, HZ, HF, BJ, TJ, SY}";
				String startString = "GZ";
				String edgeString = "{[1022]=(GZ,WH), [1607]=(GZ,HZ), [807]=(WH,HZ>, [1200]=(WH,BJ), [362]=(WH,HF), [445]=(HZ,HF), [1512]=(HZ,TJ), [1109]=(HF,BJ), [971]=(HF,TJ), [137]=(BJ,TJ), [889]=(BJ,SY), [689]=(TJ,SY) }";
				setGraphType(false);
				setInputs(nodeString, startString, edgeString);
				dijkstraBox.setSelected(true);
				pathBox.setSelected(true);
				useMatrixBox.setSelected(true);
				useGraphBox.setSelected(true);
				feedbackAnimator.update();
			} else if (command.equals("clear")) {
				textAreaManager.clearContent();
				imagedAreaManager.clearContent();
				imagedAreaManager.revalidate();
			}
		}
		
		public boolean doShortestPath() {
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

			DefaultGraphNode startNode = null;
			String startString = startField.getText();
			for (GraphNode node : nodeList) {
				if (node.getId().equals(startString)) {
					startNode = (DefaultGraphNode)node;
					break;
				}
			}
			if (startNode == null) {
				feedbackAnimator.appendPlainStringAsNewLine("给出的顶点集不包含起始顶点！");
				textAreaManager.appendContent("\tThe given start node is not included in the node list!\n");
				imagedAreaManager.appendPlainStringAsNewLine("    错误：给出的顶点集不包含起始顶点！");
			} else {
				feedbackAnimator.appendPlainStringAsNewLine("起始顶点：");
				feedbackAnimator.appendLaTeXStringToLastLine(startNode.getLabel());
				textAreaManager.appendContent("\tStart node: " + startNode.getLabel() + "\n");
				imagedAreaManager.appendPlainStringAsNewLine("    起始顶点：");
				imagedAreaManager.appendLaTeXStringToLastLine(startNode.getLabel());
			}

			boolean directed = false;
			if (directedButton.isSelected()) directed = true;
			String edgesString = edgeArea.getText();
			
			List<GraphEdge> edgeList = new ArrayList<GraphEdge>();
			success = GraphUtil.extractWeightedEdgesFromFormatedString(nodeList, edgesString, edgeList, directed);
			if (success != true) {
				textAreaManager.appendContent("\tThe string " + edgesString + " dose not give the edges of the graph legally!\n");
				imagedAreaManager.appendPlainStringAsNewLine("    字符串 " + edgesString + " 没有合法地给出图的边集！");
				imagedAreaManager.appendPlainStringAsNewLine("    错误信息： " + GraphUtil.getErrorMessage());
				feedbackAnimator.appendPlainStringAsNewLine("字符串 " + edgesString + "  不是合法的表示边集字符串！");
				return false;
			}
			feedbackAnimator.appendLaTeXStringAsNewLine("E = " + GraphUtil.getWeightedEdgeLaTeXString(edgeList));
			textAreaManager.appendContent("\tE = " + edgesString + "\n");
			imagedAreaManager.appendPlainStringAsNewLine("    边集: ");
			imagedAreaManager.appendLaTeXStringToLastLine("E = " + GraphUtil.getWeightedEdgeLaTeXString(edgeList));

			WeightedGraph graph = new WeightedGraph("WeightedGraph");
			graph.setNodes(nodeList);
			graph.setEdges(edgeList);

			if (useGraphBox.isSelected()) {
				String dotFileName = Configuration.dataFilePath + "WeightedGraph.dot";
				String pngFileName = Configuration.dataFilePath + "WeightedGraph.png";
				try {
					PrintWriter writer = new PrintWriter(dotFileName);
					graph.simplyWriteToDotFile(writer);
					success = MainGUIManager.generatePNGFile(dotFileName, pngFileName, false);
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
			if (useMatrixBox.isSelected()) {
				DoubleMatrix matrix = graph.getWeightAdjacencyMatrix();
				textAreaManager.appendContent("\tThe matrix of the graph: D = " + matrix.toLaTeXString() + "\n");
				imagedAreaManager.appendPlainStringAsNewLine("    带权图的距离矩阵：");
				imagedAreaManager.appendLaTeXStringToLastLine("D = " + matrix.toLaTeXString());
			}
			
			if (dijkstraBox.isSelected()) {
				textAreaManager.appendContent("\tUse Dijkstra Algorithm to calculate the shortest pathes from the node " + startNode.getLabel() + " to all other nodes\n");
				imagedAreaManager.appendPlainStringAsNewLine("    使用Dijkstra算法计算起始顶点 ");
				imagedAreaManager.appendLaTeXStringToLastLine(startNode.getLabel());
				imagedAreaManager.appendPlainStringToLastLine(" 到所有其他顶点的最短路径 ");
				List<WeightedGraphPath> pathList = graph.dijkstra(startNode, null);
				if (detailBox.isSelected()) {
					String message = WeightedGraph.getDijkstraRecorder().toLaTeXString();
					textAreaManager.appendContent("\t\t" + message + "\n");
					imagedAreaManager.appendPlainStringAsNewLine("        ");
					imagedAreaManager.appendLaTeXStringToLastLine(message);
				}
				if (pathBox.isSelected()) {
					textAreaManager.appendContent("\tThe shortest pathes are as follows: \n");
					imagedAreaManager.appendPlainStringAsNewLine("    计算得到的最短路径与最短距离如下：");
					for (WeightedGraphPath path : pathList) {
						textAreaManager.appendContent("\t\t" + path.toLaTeXString(startNode) + "\n");
						imagedAreaManager.appendPlainStringAsNewLine("        ");
						imagedAreaManager.appendLaTeXStringToLastLine(path.toLaTeXString(startNode));
					}
				}
				if (pathGraphBox.isSelected()) {
					WeightedGraph pathGraph = new WeightedGraph("ShortestPath");
					List<GraphEdge> pathEdgeList = new ArrayList<GraphEdge>();
					for (WeightedGraphPath path : pathList) {
						for (GraphEdge edge : path.getEdges()) {
							if (!pathEdgeList.contains(edge)) pathEdgeList.add(edge);
						}
					}
					pathGraph.setNodes(nodeList);
					pathGraph.setEdges(pathEdgeList);
					String dotFileName = Configuration.dataFilePath + "ShortestPath.dot";
					String pngFileName = Configuration.dataFilePath + "ShortestPath.png";
					try {
						PrintWriter writer = new PrintWriter(dotFileName);
						pathGraph.simplyWriteToDotFile(writer);
						success = MainGUIManager.generatePNGFile(dotFileName, pngFileName, false);
						if (success) {
							textAreaManager.appendContent("\t\tHere gives the graph of the shortest pathes in the graph!\n");
							imagedAreaManager.appendPlainStringAsNewLine("    最短路径图: ");
							imagedAreaManager.appendImageFileToLastLine(pngFileName);
						} else {
							textAreaManager.appendContent("\t\tCan not generate the graph of the sortest pathes in the graph!\n");
							textAreaManager.appendContent(MainGUIManager.errorMessage);
							imagedAreaManager.appendPlainStringAsNewLine("    最短路径图: 无法生成！");
						}
					} catch (Exception e) {
						textAreaManager.appendContent("\t\tCan not generate the graph of the sortest pathes in the graph!\n");
						textAreaManager.appendContent(MainGUIManager.errorMessage);
						imagedAreaManager.appendPlainStringAsNewLine("    最短路径图: 无法生成！");
					}
				}
			}
			return true;
		}
		
		public boolean generateInputs() {
			boolean directed = false;
			if (directedButton.isSelected()) directed = true;
			int nodeNumber = (int)(Math.random() * 5) + 5;
			int edgeNumber = (int)(Math.random() * 20) + 1;
			int maxWeight = 20;
			DefaultGraph graph = GraphUtil.randomGenerateWeightedGraph(nodeNumber, edgeNumber, directed, true, maxWeight);

			String nodesString = GraphUtil.getFormatedNodeString(graph.getNodes());
			String edgesString = GraphUtil.getFormatedWeightedEdgeString(graph.getEdges());
			nodeField.setText(nodesString);
			edgeArea.setText(edgesString);
			
			dijkstraBox.setSelected(true);
			pathBox.setSelected(true);
			detailBox.setSelected(true);
			useMatrixBox.setSelected(true);
			useGraphBox.setSelected(true);
			checkInputs();		// 让生成的信息通过合法性检查后反馈在输入界面之上
			return true;
		}
		
		public boolean checkInputs() {
			boolean directed = false;
			if (directedButton.isSelected()) directed = true;
			
			String nodesString = nodeField.getText();
			String edgesString = edgeArea.getText();
			boolean success = true;
			
			List<GraphNode> nodeList = new ArrayList<GraphNode>();
			if (!nodesString.contentEquals("")) {
				success = GraphUtil.extractNodesFromFormatedString(nodesString, nodeList);
				if (success == true) {
					feedbackAnimator.appendLaTeXStringAsNewLine("V = " + GraphUtil.getNodeLaTeXString(nodeList));
				} else {
					feedbackAnimator.appendPlainStringAsNewLine("给出顶点集的字符串 " + nodesString + " 不是合法的表示顶点集的字符串！");
					feedbackAnimator.appendPlainStringAsNewLine("    错误信息： " + GraphUtil.getErrorMessage());
					return false;
				}
			}
			if (!edgesString.contentEquals("")) {
				List<GraphEdge> edgeList = new ArrayList<GraphEdge>();
				success = GraphUtil.extractWeightedEdgesFromFormatedString(nodeList, edgesString, edgeList, directed);
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
