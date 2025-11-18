package com.deedm.legacy.guiManager;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;

import com.deedm.legacy.guiManager.algebra.BinaryOperatorUIManager;
import com.deedm.legacy.guiManager.algebra.BoolAlgebraUIManager;
import com.deedm.legacy.guiManager.algebra.GroupPermUIManager;
import com.deedm.legacy.guiManager.algebra.GroupUmUIManager;
import com.deedm.legacy.guiManager.algebra.LatticeUIManager;
import com.deedm.legacy.guiManager.count.CombCalculatorUIManager;
import com.deedm.legacy.guiManager.count.CountFunctionUIManager;
import com.deedm.legacy.guiManager.count.CountIntegerUIManager;
import com.deedm.legacy.guiManager.count.CountEquationSolverUIManager;
import com.deedm.legacy.guiManager.count.CountStringUIManager;
import com.deedm.legacy.guiManager.count.ExprCalculatorUIManager;
import com.deedm.legacy.guiManager.count.GenCombinationUIManager;
import com.deedm.legacy.guiManager.count.GenPermutationUIManager;
import com.deedm.legacy.guiManager.count.GenRepCombUIManager;
import com.deedm.legacy.guiManager.count.RecuExpressionCalculatorUIManager;
import com.deedm.legacy.guiManager.graph.GraphTravelUIManager;
import com.deedm.legacy.guiManager.graph.HuffmanTreeUIManager;
import com.deedm.legacy.guiManager.graph.ShortestPathUIManager;
import com.deedm.legacy.guiManager.graph.SpanningTreeUIManager;
import com.deedm.legacy.guiManager.graph.SpecialGraphUIManager;
import com.deedm.legacy.guiManager.graph.TreeTravelUIManager;
import com.deedm.legacy.guiManager.logic.EquivCalculusCheckUIManager;
import com.deedm.legacy.guiManager.logic.FormulaSyntaxUIManager;
import com.deedm.legacy.guiManager.logic.FormulaTruthValueUIManager;
import com.deedm.legacy.guiManager.logic.NormalFormulaCalculationUIManager;
import com.deedm.legacy.guiManager.logic.NormalFormulaExpansionUIManager;
import com.deedm.legacy.guiManager.logic.ReasonArgumentCheckUIManager;
import com.deedm.legacy.guiManager.logic.FormulaTruthTableUIManager;
import com.deedm.legacy.guiManager.setrelfun.EquivalenceUIManager;
import com.deedm.legacy.guiManager.setrelfun.FunctionUIManager;
import com.deedm.legacy.guiManager.setrelfun.PartialOrderUIManager;
import com.deedm.legacy.guiManager.setrelfun.RelationClosureUIManager;
import com.deedm.legacy.guiManager.setrelfun.RelationOperationUIManager;
import com.deedm.legacy.guiManager.setrelfun.RelationPropertyUIManager;
import com.deedm.legacy.guiManager.setrelfun.SetExprOperationUIManager;
import com.deedm.legacy.guiManager.setrelfun.SetOperationUIManager;
import com.deedm.legacy.util.Configuration;


public class MainGUIManager {
	public static String errorMessage = null;
	
	public static void main(String[] args) {
		// 初始化主画框，调整其位置和宽度，使得显示出来的按钮更漂亮
		int widthSpace = 18;
		int heightSpace = 56;
		
		MainSwingFrame.init("《离散数学基础》（周晓聪、乔海燕编著，清华大学出版社）教材例子展示", MainSwingFrame.screenWidth-widthSpace, 
			MainSwingFrame.screenHeight-2*heightSpace, 0, 0, "windows");
		
		System.out.println("Screen width = " + MainSwingFrame.screenWidth + ", screen height = " + MainSwingFrame.screenHeight + ", and resolution = " + MainSwingFrame.screenResolution);

		Configuration.load();
		MainGUICreator demo = new MainGUICreator(MainSwingFrame.getMainFrame());
		demo.createMainGUI();
		
		// 启动主画框，并进行演示
		MainSwingFrame.start(); 
	}
	
	/**
	 * Call dot.exe to generate a graphic file with png format
	 * @param dotFileName : the file name of .dot file
	 * @param graphFileName : the file name of .png file
	 */
	public static boolean generatePNGFile(String dotFileName, String graphFileName, boolean isTree) {
		final String dotProgram = Configuration.graphVizPath + "dot.exe";
		final String graphFileFormat = "png";
		Runtime rt = Runtime.getRuntime();

		String[] args = { dotProgram, "-K"+Configuration.graphVizLayout, "-T" + graphFileFormat, dotFileName, "-o", graphFileName };
		if (isTree) args[1] = "-K" + Configuration.treeVizLayout;

		try {
			Process p = rt.exec(args);
			p.waitFor();
		} catch (InterruptedException exc) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("Error: The execution of the external program dot.exe was interrupted for arguments ");
			for (int i = 0; i < args.length; i++) {
				buffer.append(args[i] + " ");
			}
			buffer.append("\n");
			buffer.append("Exception Message: " + exc.getMessage());
			errorMessage = buffer.toString();
			return false;
		} catch (IOException exc) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("Error: Throws IOException during the execution of dot.exe for arguments ");
			for (int i = 0; i < args.length; i++) {
				buffer.append(args[i] + " ");
			}
			buffer.append("\n");
			buffer.append("Exception Message: " + exc.getMessage());
			errorMessage = buffer.toString();
			return false;
		}
		return true;
	}
}

class MainGUICreator {
	JFrame topLevelFrame = null;
	ImagedTextAreaManager imagedAreaManager = null;
	PlainTextAreaManager textAreaManager = null;
	
	FormulaSyntaxUIManager formulaSyntaxUIManager = null;
	FormulaTruthValueUIManager truthValueUIManager = null;
	FormulaTruthTableUIManager truthTableUIManager = null;
	NormalFormulaExpansionUIManager normalFormulaUIManager = null;
	NormalFormulaCalculationUIManager normalCalculateUIManager = null;
	EquivCalculusCheckUIManager calculusCheckUIManager = null;
	ReasonArgumentCheckUIManager argumentCheckUIManager = null;
	
	SetOperationUIManager setOperationUIManager = null;
	SetExprOperationUIManager setExprOperationUIManager = null;
	RelationOperationUIManager relationOperationUIManager = null;
	RelationPropertyUIManager relationPropertyUIManager = null;
	RelationClosureUIManager relationClosureUIManager = null;
	EquivalenceUIManager equivalenceUIManager = null;
	PartialOrderUIManager partialOrderUIManager = null;
	FunctionUIManager functionUIManager = null;
	
	GraphTravelUIManager graphTravelUIManager = null;
	TreeTravelUIManager treeTravelUIManager = null;
	ShortestPathUIManager shortestPathUIManager = null;
	SpanningTreeUIManager spanningTreeUIManager = null;
	HuffmanTreeUIManager huffmanTreeUIManager = null;
	SpecialGraphUIManager specialGraphUIManager = null;
	
	CombCalculatorUIManager combCalculatorUIManager = null;
	ExprCalculatorUIManager exprCalculatorUIManager = null;
	RecuExpressionCalculatorUIManager recuExpressionCalculatorUIManager = null;
	CountStringUIManager countStringUIManager = null;
	CountIntegerUIManager countIntegerUIManager = null;
	CountEquationSolverUIManager countSolverUIManager = null;
	CountFunctionUIManager countFunctionUIManager = null;
	GenPermutationUIManager genPermutationUIManager = null;
	GenCombinationUIManager genCombinationUIManager = null;
	GenRepCombUIManager genRepCombUIManager = null;

	GroupUmUIManager groupUmUIManager = null;
	GroupPermUIManager groupPermUIManager = null;
	LatticeUIManager latticeUIManager = null;
	BoolAlgebraUIManager boolAlgebraUIManager = null;
	BinaryOperatorUIManager binaryOperatorUIManager = null;
	
	ConfigurationUIManager configManager = null;
	
	private final String TEST_COMMAND = "test";
	private final String ABOUT_COMMAND = "about";
	private final String EXIT_COMMAND = "exit";
	private final String USAGE_COMMAND = "usage";
	private final String FORMULA_SYNTAX_COMMAND = "formula_syntax";
	private final String TRUTH_VALUE_COMMAND = "truth_value";
	private final String TRUTH_TABLE_COMMAND = "truth_table";
	private final String EXPAND_NF_COMMAND = "expand_normal_formula";
	private final String CALCULATE_NF_COMMAND = "calculate_normal_formula";
	private final String CALCULUS_CHECK_COMMAND = "calculus_check_formula";
	private final String ARGUMENT_CHECK_COMMAND = "argument_check_formula";
	
	private final String SET_OPERATION_COMMAND = "set_operation";
	private final String SET_EXPR_OPERATION_COMMAND = "set_expr_operation";
	private final String RELATION_OPERATION_COMMAND = "relation_operation";
	private final String RELATION_PROPERTY_COMMAND = "relation_property";
	private final String RELATION_CLOSURE_COMMAND = "relation_closure";
	private final String EQUIVALENCE_COMMAND = "equivalence_relation";
	private final String PARTIAL_ORDER_COMMAND = "partial_order";
	private final String FUNCTION_COMMAND = "function";
	private final String GRAPH_TRAVEL_COMMAND = "graph_travel";
	private final String TREE_TRAVEL_COMMAND = "tree_travel";
	private final String SHORTEST_PATH_COMMAND = "short_path";
	private final String SPANNING_TREE_COMMAND = "spanning_tree";
	private final String HUFFMAN_TREE_COMMAND = "huffman_tree";
	private final String SPECIAL_GRAPH_COMMAND = "special_graph";
	private final String COMB_CALCULATOR_COMMAND = "comb_calculator";
	private final String EXPR_CALCULATOR_COMMAND = "expr__calculator";
	private final String RECU_EXPR_CALCULATOR_COMMAND = "recu_expr_calculator";
	private final String COUNT_STRING_COMMAND = "count_string";
	private final String COUNT_INTEGER_COMMAND = "count_integer";
	private final String COUNT_SOLVER_COMMAND = "count_solver";
	private final String COUNT_FUNCTION_COMMAND = "count_function";
	private final String GEN_PERMUTATION_COMMAND = "generate_permutation";
	private final String GEN_COMBINATION_COMMAND = "generate_combination";
	private final String GEN_REPCOMB_COMMAND = "generate_repcomb";
	private final String GROUP_UM_COMMAND = "group_Um";
	private final String GROUP_PERM_COMMAND = "group_Perm";
	private final String LATTICE_COMMAND = "lattice";
	private final String BOOLEAN_COMMAND = "boolean";
	private final String BINARY_OPERATOR_COMMAND = "binary_operator";
	private final String CLEAR_COMMAND = "clear";
	private final String CONFIG_COMMAND = "config";

	public MainGUICreator(JFrame topLevelFrame) {
		this.topLevelFrame = topLevelFrame;
	}

	public void createMainGUI() {
		String[] mainMenuArray = {"命题逻辑(P)      ", "集合关系函数(S)      ", "组合计数(C)      ", "图与树(G)      ", "代数结构(B)      ", "帮助(H)      "};
		int[] mainMnemonic = {KeyEvent.VK_P, KeyEvent.VK_S, KeyEvent.VK_C, KeyEvent.VK_G, KeyEvent.VK_B, KeyEvent.VK_H};

		String[] propSubMenuArray = {"分析公式的语法", "计算公式的真值", "构造公式的真值表", "Separator", "计算与公式逻辑等值的范式", "将范式扩展为主范式", "Separator", "等值演算过程检查", "验证推理有效性论证检查"};
		int[] propSubMnemonic = {0, 0, KeyEvent.VK_T, 0, KeyEvent.VK_N, 0, 0, 0, 0};  
		String[] propSubCommand = {FORMULA_SYNTAX_COMMAND, TRUTH_VALUE_COMMAND, TRUTH_TABLE_COMMAND, "", CALCULATE_NF_COMMAND, EXPAND_NF_COMMAND, "", CALCULUS_CHECK_COMMAND, ARGUMENT_CHECK_COMMAND};

		String[] setSubMenuArray = {"单个集合的运算", "集合表达式运算", "Separator", "单个关系的运算", "关系性质判断", "关系闭包的计算", "Separator", "等价关系的计算", "偏序关系的计算", "Separator", "函数性质的判断"};
		int[] setSubMnemonic = {KeyEvent.VK_O, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};  
		String[] setSubCommand = {SET_OPERATION_COMMAND, SET_EXPR_OPERATION_COMMAND, "", RELATION_OPERATION_COMMAND, RELATION_PROPERTY_COMMAND, RELATION_CLOSURE_COMMAND, "", EQUIVALENCE_COMMAND, PARTIAL_ORDER_COMMAND, "", FUNCTION_COMMAND};

		String[] countSubMenuArray = {"排列组合数计算", "组合表达式计算", "递归表达式计算", "Separator", "字符串计数", "基于整除性质的整数计数", "不定方程非负整数解计数", "不同性质的函数计数", "Separator", "排列的生成(G)", "不重复组合的生成(N)", "允许重复组合的生成(F)"};
		int[] countSubMnemonic = {0, 0, 0, 0, 0, KeyEvent.VK_F, 0, 0, 0, 0, 0, 0};  
		String[] countSubCommand = {COMB_CALCULATOR_COMMAND, EXPR_CALCULATOR_COMMAND, RECU_EXPR_CALCULATOR_COMMAND, "", COUNT_STRING_COMMAND, COUNT_INTEGER_COMMAND, COUNT_SOLVER_COMMAND, COUNT_FUNCTION_COMMAND, "", GEN_PERMUTATION_COMMAND, GEN_COMBINATION_COMMAND, GEN_REPCOMB_COMMAND, /*"", TEST_COMMAND*/};
		
		String[] graphSubMenuArray = {"图的遍历", "树的遍历", "Separator", "带权图最短路径计算", "带权图最小生成树计算", "Separator", "哈夫曼树构造", "Separator", "展示特殊的图"};
		int[] graphSubMnemonic = {0, 0, 0, KeyEvent.VK_D, KeyEvent.VK_S, 0, KeyEvent.VK_H, 0, 0};  
		String[] graphSubCommand = {GRAPH_TRAVEL_COMMAND, TREE_TRAVEL_COMMAND, "", SHORTEST_PATH_COMMAND, SPANNING_TREE_COMMAND, "", HUFFMAN_TREE_COMMAND, "", SPECIAL_GRAPH_COMMAND};

		String[] algebraSubMenuArray = {"运算性质的判断", "Separator", "群U(m)及其子群与陪集", "置换群及其子群与陪集", "Separator", "偏序关系是否格的判断", "整除与布尔代数的判断"};
		int[] algebraSubMnemonic = {KeyEvent.VK_J, 0, 0, 0, 0, 0, 0};  
		String[] algebraSubCommand = {BINARY_OPERATOR_COMMAND, "", GROUP_UM_COMMAND, GROUP_PERM_COMMAND, "", LATTICE_COMMAND, BOOLEAN_COMMAND};
		
		String[] helpSubMenuArray = {"关于", "使用说明", "Separator", "首选项", "清理屏幕", "Separator", "退出"};
		int[] helpSubMnemonic = {KeyEvent.VK_A, KeyEvent.VK_H, 0, KeyEvent.VK_P, 0, 0, KeyEvent.VK_E};  
		String[] helpSubCommand = {ABOUT_COMMAND, USAGE_COMMAND, "", CONFIG_COMMAND, CLEAR_COMMAND, "", EXIT_COMMAND};
		
		Container place = topLevelFrame.getContentPane(); 
		JSplitPane hSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		hSplitPane.setDividerLocation(place.getPreferredSize().width/2);
		place.add(hSplitPane);

		textAreaManager = new PlainTextAreaManager();
		JComponent textArea  = textAreaManager.initialize(place);
		imagedAreaManager = new ImagedTextAreaManager();
		JComponent imagedArea = imagedAreaManager.initialize(place);
		hSplitPane.setLeftComponent(imagedArea);
		hSplitPane.setRightComponent(textArea);
		hSplitPane.resetToPreferredSizes();
		
		// 创建菜单的监听器
		MenuListener menuListener = new MenuListener();
		// 创建菜单条
		JMenuBar menuBar = new JMenuBar();
		topLevelFrame.setJMenuBar(menuBar);		// 放置在顶层容器
		
		// 创建主菜单项
		int mainIndex = 0;
		JMenu menu = new JMenu(mainMenuArray[mainIndex]);
		menu.setMnemonic(mainMnemonic[mainIndex]);		// 设置字符键F为快捷键
		menuBar.add(menu);						// 加入到菜单条
		mainIndex = mainIndex + 1;
		// 创建子菜单项
		for (int subIndex = 0; subIndex < propSubMenuArray.length; subIndex++) {
			if (propSubMenuArray[subIndex].equals("Separator")) {
				menu.addSeparator();
				continue;
			}
			JMenuItem menuItem = new JMenuItem(propSubMenuArray[subIndex], null);
			if (propSubMnemonic[subIndex] != 0) {
				menuItem.setMnemonic(propSubMnemonic[subIndex]);
				menuItem.setAccelerator(KeyStroke.getKeyStroke(propSubMnemonic[subIndex], ActionEvent.CTRL_MASK));
			}
			menuItem.addActionListener(menuListener);
			menuItem.setActionCommand(propSubCommand[subIndex]);		// 设置命令为退出程序
			menu.add(menuItem);						// 加入到第一个主菜单项
		}

		// 主菜单项.
		menu = new JMenu(mainMenuArray[mainIndex]);
		menu.setMnemonic(mainMnemonic[mainIndex]);
		menuBar.add(menu);
		mainIndex = mainIndex + 1;
		// 创建子菜单项
		for (int subIndex = 0; subIndex < setSubMenuArray.length; subIndex++) {
			if (setSubMenuArray[subIndex].equals("Separator")) {
				menu.addSeparator();
				continue;
			}
			JMenuItem menuItem = new JMenuItem(setSubMenuArray[subIndex], null);
			if (setSubMnemonic[subIndex] != 0) {
				menuItem.setMnemonic(setSubMnemonic[subIndex]);
				menuItem.setAccelerator(KeyStroke.getKeyStroke(setSubMnemonic[subIndex], ActionEvent.CTRL_MASK));
			}
			menuItem.addActionListener(menuListener);
			menuItem.setActionCommand(setSubCommand[subIndex]);		// 设置命令为退出程序
			menu.add(menuItem);						// 加入到第一个主菜单项
		}

		// 主菜单项.
		menu = new JMenu(mainMenuArray[mainIndex]);
		menu.setMnemonic(mainMnemonic[mainIndex]);
		menuBar.add(menu);
		mainIndex = mainIndex + 1;
		// 创建子菜单项
		for (int subIndex = 0; subIndex < countSubMenuArray.length; subIndex++) {
			if (countSubMenuArray[subIndex].equals("Separator")) {
				menu.addSeparator();
				continue;
			}
			JMenuItem menuItem = new JMenuItem(countSubMenuArray[subIndex], null);
			if (countSubMnemonic[subIndex] != 0) {
				menuItem.setMnemonic(countSubMnemonic[subIndex]);
				menuItem.setAccelerator(KeyStroke.getKeyStroke(countSubMnemonic[subIndex], ActionEvent.CTRL_MASK));
			}
			menuItem.addActionListener(menuListener);
			menuItem.setActionCommand(countSubCommand[subIndex]);		// 设置命令为退出程序
			menu.add(menuItem);						// 加入到第一个主菜单项
		}
		
		// 主菜单项.
		menu = new JMenu(mainMenuArray[mainIndex]);
		menu.setMnemonic(mainMnemonic[mainIndex]);
		menuBar.add(menu);
		mainIndex = mainIndex + 1;
		// 创建子菜单项
		for (int subIndex = 0; subIndex < graphSubMenuArray.length; subIndex++) {
			if (graphSubMenuArray[subIndex].equals("Separator")) {
				menu.addSeparator();
				continue;
			}
			JMenuItem menuItem = new JMenuItem(graphSubMenuArray[subIndex], null);
			if (graphSubMnemonic[subIndex] != 0) {
				menuItem.setMnemonic(graphSubMnemonic[subIndex]);
				menuItem.setAccelerator(KeyStroke.getKeyStroke(graphSubMnemonic[subIndex], ActionEvent.CTRL_MASK));
			}
			menuItem.addActionListener(menuListener);
			menuItem.setActionCommand(graphSubCommand[subIndex]);		// 设置命令为退出程序
			menu.add(menuItem);						// 加入到第一个主菜单项
		}

		// 主菜单项.
		menu = new JMenu(mainMenuArray[mainIndex]);
		menu.setMnemonic(mainMnemonic[mainIndex]);
		menuBar.add(menu);
		mainIndex = mainIndex + 1;
		// 创建子菜单项
		for (int subIndex = 0; subIndex < algebraSubMenuArray.length; subIndex++) {
			if (algebraSubMenuArray[subIndex].equals("Separator")) {
				menu.addSeparator();
				continue;
			}
			JMenuItem menuItem = new JMenuItem(algebraSubMenuArray[subIndex], null);
			if (algebraSubMnemonic[subIndex] != 0) {
				menuItem.setMnemonic(algebraSubMnemonic[subIndex]);
				menuItem.setAccelerator(KeyStroke.getKeyStroke(algebraSubMnemonic[subIndex], ActionEvent.CTRL_MASK));
			}
			menuItem.addActionListener(menuListener);
			menuItem.setActionCommand(algebraSubCommand[subIndex]);		// 设置命令为退出程序
			menu.add(menuItem);						// 加入到第一个主菜单项
		}

		// 主菜单项.
		menu = new JMenu(mainMenuArray[mainIndex]);
		menu.setMnemonic(mainMnemonic[mainIndex]);
		menuBar.add(menu);
		mainIndex = mainIndex + 1;
		// 创建子菜单项
		for (int subIndex = 0; subIndex < helpSubMenuArray.length; subIndex++) {
			if (helpSubMenuArray[subIndex].equals("Separator")) {
				menu.addSeparator();
				continue;
			}
			JMenuItem menuItem = new JMenuItem(helpSubMenuArray[subIndex], null);
			if (helpSubMnemonic[subIndex] != 0) {
				menuItem.setMnemonic(helpSubMnemonic[subIndex]);
				menuItem.setAccelerator(KeyStroke.getKeyStroke(helpSubMnemonic[subIndex], ActionEvent.CTRL_MASK|ActionEvent.ALT_MASK));
			}
			menuItem.addActionListener(menuListener);
			menuItem.setActionCommand(helpSubCommand[subIndex]);		// 设置命令为退出程序
			menu.add(menuItem);						// 加入到第一个主菜单项
		}
	}

	// 监听菜单项的按下动作
	private class MenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JMenuItem source = (JMenuItem)(e.getSource());
			String command = source.getActionCommand();
			if (command.equals(ABOUT_COMMAND)) {
				// 弹出一窗口显示一些信息
				JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "\n 离散数学基础例题习题展示(Deedm)\n\n (Demonstrator of Examples in Elementary Discrete Mathematics)\n\n\n (C) 周晓聪版权所有， 2020--2030 \n\n 中山大学计算机学院、软件工程学院，广州        \n\n", "关于", 
					JOptionPane.WARNING_MESSAGE);
			} else if (command.equals(EXIT_COMMAND)) {
				System.exit(1);  // 退出整个程序
			} else if (command.equals(USAGE_COMMAND)) {
				doShowUsage();
			} else if (command.equals(FORMULA_SYNTAX_COMMAND)) {
				if (formulaSyntaxUIManager == null) {
					String laTeXString = "p\\vee(q\\wedge r)\\leftrightarrow(r\\rightarrow\\neg q)";
					formulaSyntaxUIManager = new FormulaSyntaxUIManager(textAreaManager, imagedAreaManager);
					formulaSyntaxUIManager.setFormula(laTeXString);
				}
				formulaSyntaxUIManager.showDialog(topLevelFrame, "输入要展示语法的公式");
			} else if (command.equals(TRUTH_VALUE_COMMAND)) {
				if (truthValueUIManager == null) {
					String laTeXString = "p\\vee(q\\wedge r)\\leftrightarrow(r\\rightarrow\\neg q)";
					truthValueUIManager = new FormulaTruthValueUIManager(textAreaManager, imagedAreaManager);
					truthValueUIManager.setFormula(laTeXString);
				}
				truthValueUIManager.showDialog(topLevelFrame, "输入要计算真值的公式");
			} else if (command.equals(TRUTH_TABLE_COMMAND)) {
				if (truthTableUIManager == null) {
					String laTeXString = "p\\vee(q\\wedge r)\\leftrightarrow(r\\rightarrow\\neg q)";
					truthTableUIManager = new FormulaTruthTableUIManager(textAreaManager, imagedAreaManager);
					truthTableUIManager.setFormula(laTeXString);
				}
				truthTableUIManager.showDialog(topLevelFrame, "输入要构造真值表的公式");
			} else if (command.equals(EXPAND_NF_COMMAND)) {
				if (normalFormulaUIManager == null) {
					String laTeXString = "(\\neg p\\vee q\\vee r)\n(\\neg r\\vee \\neg s)\n(q\\vee t)\n(\\neg q\\vee \\neg t)\n(\\neg p\\vee s)";
					normalFormulaUIManager = new NormalFormulaExpansionUIManager(textAreaManager, imagedAreaManager);
					normalFormulaUIManager.setVariableSet("p, q, r, s, t");
					normalFormulaUIManager.setFormulaList(laTeXString);
					normalFormulaUIManager.setToBeDFN(false);
				}
				normalFormulaUIManager.showDialog(topLevelFrame, "输入简单析取式或简单合取式");
			} else if (command.equals(CALCULATE_NF_COMMAND)) {
				if (normalCalculateUIManager == null) {
					String laTeXString = "(p\\wedge q\\rightarrow r)\n(p\\leftrightarrow r)\n(q\\vee r)\n";
					normalCalculateUIManager = new NormalFormulaCalculationUIManager(textAreaManager, imagedAreaManager);
					normalCalculateUIManager.setVariableSet("p, q, r");
					normalCalculateUIManager.setFormulaList(laTeXString);
					normalCalculateUIManager.setToBeDFN(false);
				}
				normalCalculateUIManager.showDialog(topLevelFrame, "输入一个或多个公式");
			} else if (command.equals(CALCULUS_CHECK_COMMAND)) {
				if (calculusCheckUIManager == null) {
					String laTeXString = "    & ((p\\vee q)\\rightarrow r)\\rightarrow q & \n" + 
							"\\equiv & (\\neg (p\\vee q)\\vee r)\\rightarrow q & 蕴涵等值式\n" + 
							"\\equiv & (\\neg p\\wedge\\neg q\\vee r)\\rightarrow q & 德摩尔根律\n" + 
							"\\equiv & \\neg(\\neg p\\wedge\\neg q\\vee r)\\vee q & 蕴涵等值式\n" + 
							"\\equiv & p\\vee q\\wedge r\\vee q & 德摩尔根律\n" + 
							"\\equiv & p\\vee q\\wedge r & 幂等律";
					calculusCheckUIManager = new EquivCalculusCheckUIManager(textAreaManager, imagedAreaManager);
					calculusCheckUIManager.setEquivCalculus(laTeXString);
				}
				calculusCheckUIManager.showDialog(topLevelFrame, "输入演算过程");
			} else if (command.equals(ARGUMENT_CHECK_COMMAND)) {
				if (argumentCheckUIManager == null) {
					String premises = "s\\rightarrow\\neg q, s\\vee p, \\neg p\\rightarrow q";
					String consequent = "p";
					String laTeXString = "(1) & \\neg p &  附加前提\n"+
							"(2) & s\\vee p & 前提\n"+
							"(3) & s & (1),(2)假言推理\n"+
							"(4) & s\\rightarrow \\neg q &  前提\n"+
							"(5) & \\neg q &  (3),(4)假言推理\n"+
							"(6) & \\neg p\\rightarrow q &  前提\n"+
							"(7) & p &  (5),(6)假言易位";
					argumentCheckUIManager = new ReasonArgumentCheckUIManager(textAreaManager, imagedAreaManager);
					argumentCheckUIManager.setReasoning(premises, consequent);
					argumentCheckUIManager.setArgument(laTeXString);
				}
				argumentCheckUIManager.showDialog(topLevelFrame, "输入推理及其论证");
			} else if (command.equals(SET_OPERATION_COMMAND)) {
				if (setOperationUIManager == null) {
					String setUString = "{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12}";
					String setAString = "{0, 1, 5, 8, 10, 12}";
					String setBString = "{0, 2, 5, 8, 9}";
					setOperationUIManager = new SetOperationUIManager(textAreaManager, imagedAreaManager);
					setOperationUIManager.clearFeedback();
					setOperationUIManager.setElementTypeInt(true);
					setOperationUIManager.setAllSets(setUString, setAString, setBString);
					setOperationUIManager.setAllOperators();
				}
				setOperationUIManager.showDialog(topLevelFrame, "输入参与运算的集合");
			} else if (command.equals(SET_EXPR_OPERATION_COMMAND)) {
				if (setExprOperationUIManager == null) {
					String setUString = "{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12}";
					String setAString = "{0, 1, 5, 8, 10, 12}";
					String setBString = "{0, 2, 5, 8, 9}";
					String expressionString = "\\overline{A\\cap B \\oplus A}";
					setExprOperationUIManager = new SetExprOperationUIManager(textAreaManager, imagedAreaManager);
					setExprOperationUIManager.clearFeedback();
					setExprOperationUIManager.setElementTypeInt(true);
					setExprOperationUIManager.setAllSets(setUString, setAString, setBString, expressionString);
				}
				setExprOperationUIManager.showDialog(topLevelFrame, "输入参与运算的集合");
			} else if (command.equals(RELATION_OPERATION_COMMAND)) {
				if (relationOperationUIManager == null) {
					relationOperationUIManager = new RelationOperationUIManager(textAreaManager, imagedAreaManager);
					String setAString = "{0, 1, 2, 3, 4, 5}";
					String setBString = "";
					String setCString = "";
					String relRString = "{<3, 4>, <2, 0>, <4, 4>, <3, 3>, <2, 1>, <0, 4>, <3, 2>, <0, 0>}";
					String relSString = "{<2, 2>, <3, 5>, <5, 2>, <2, 5>, <3, 2>, <2, 0>, <1, 5>, <5, 4>}";
					relationOperationUIManager.setElementTypeInt(false);
					relationOperationUIManager.setAllSets(setAString, setBString, setCString);
					relationOperationUIManager.setRelationR(0, 0, relRString);
					relationOperationUIManager.setRelationS(0, 0, relSString);
					relationOperationUIManager.setAllOperators();
				}
				relationOperationUIManager.showDialog(topLevelFrame, "输入参与运算的关系");
			} else if (command.equals(RELATION_PROPERTY_COMMAND)) {
				if (relationPropertyUIManager == null) {
					String setAString = "{0, 1, 2, 3, 4, 5}";
					String relRString = "{<0, 2>, <0, 4>, <1, 2>, <2, 3>, <3, 5>}";
					relationPropertyUIManager = new RelationPropertyUIManager(textAreaManager, imagedAreaManager);
					relationPropertyUIManager.setElementTypeInt(false);
					relationPropertyUIManager.setInputs(setAString, relRString);
					relationPropertyUIManager.setAllProperties();
				}
				relationPropertyUIManager.showDialog(topLevelFrame, "输入要判断性质的关系");
			} else if (command.equals(RELATION_CLOSURE_COMMAND)) {
				if (relationClosureUIManager == null) {
					String setAString = "{0, 1, 2, 3, 4, 5}";
					String relRString = "{<0, 2>, <0, 4>, <1, 2>, <2, 3>, <3, 5>}";
					relationClosureUIManager = new RelationClosureUIManager(textAreaManager, imagedAreaManager);
					relationClosureUIManager.setElementTypeInt(false);
					relationClosureUIManager.setInputs(setAString, relRString);
					relationClosureUIManager.setAllClosures();
				}
				relationClosureUIManager.showDialog(topLevelFrame, "输入要计算闭包的关系");
			} else if (command.equals(EQUIVALENCE_COMMAND)) {
				if (equivalenceUIManager == null) {
					equivalenceUIManager = new EquivalenceUIManager(textAreaManager, imagedAreaManager);
					String setAString = "{0, 1, 2, 3, 4, 5}";
					String relRString = "{<0, 2>, <0, 4>, <1, 2>, <2, 3>, <3, 5>}";
					equivalenceUIManager.setElementTypeInt(false);
					equivalenceUIManager.setInputs(setAString, relRString);
					equivalenceUIManager.setAllProperties();
				}
				equivalenceUIManager.showDialog(topLevelFrame, "输入要判断的等价关系");
			} else if (command.equals(PARTIAL_ORDER_COMMAND)) {
				if (partialOrderUIManager == null) {
					partialOrderUIManager = new PartialOrderUIManager(textAreaManager, imagedAreaManager);
					String setAString = "{0, 1, 2, 3, 4, 5}";
					String setSString = "{1, 3, 5}";
					String relRString = "{<0, 2>, <0, 4>, <1, 2>, <2, 3>, <3, 5>}";
					partialOrderUIManager.setElementTypeInt(false);
					partialOrderUIManager.setInputs(setAString, setSString, relRString);
					partialOrderUIManager.setElementsCalculation();
				}
				partialOrderUIManager.showDialog(topLevelFrame, "输入要判断的偏序关系");
			} else if (command.equals(FUNCTION_COMMAND)) {
				if (functionUIManager == null) {
					functionUIManager = new FunctionUIManager(textAreaManager, imagedAreaManager);
					String setAString = "{1, 2, 3, 4, 5, 6}";
					String setBString = "{a, b, c, d, e, f}";
					String relRString = "{<1, a>, <2, c>, <3, a>, <4, d>, <5, b>, <6, f>}";
					functionUIManager.setElementTypeInt(false);
					functionUIManager.setInputs(setAString, setBString, relRString);
					functionUIManager.setFunctionProperties();
				}
				functionUIManager.showDialog(topLevelFrame, "输入要判断的函数（关系）");
			} else if (command.equals(GRAPH_TRAVEL_COMMAND)) {
				if (graphTravelUIManager == null) {
					graphTravelUIManager = new GraphTravelUIManager(textAreaManager, imagedAreaManager);
					String nodesString = "{v1, v2, v3, v4, v5, v6}";
					String edgesString = "{e1=(v1,v2), e2=(v1,v3), e3=(v1,v3), e4=(v2,v3), e5=(v3,v4), e6=(v2,v4), e7=(v2,v5), e8=(v4,v5), e9=(v5,v6), e10=(v4,v6)}";
					graphTravelUIManager.setGraphType(false);
					graphTravelUIManager.setInputs(nodesString, edgesString);
					graphTravelUIManager.setAllTravels();
				}
				graphTravelUIManager.showDialog(topLevelFrame, "输入要遍历的图");
			} else if (command.equals(TREE_TRAVEL_COMMAND)) {
				if (treeTravelUIManager == null) {
					treeTravelUIManager = new TreeTravelUIManager(textAreaManager, imagedAreaManager);
					String nodesString = "{1, 2, 3, 4, 5, 6, 7, 8}";
					String rootString = "1";
					String edgesString = "{(1, 2), (1, 3), (2, 4), (2, 5), (3, 6), (3, 7), (4, 8)}";
					treeTravelUIManager.setInputs(nodesString, rootString, edgesString);
					treeTravelUIManager.setAllTravels();
				}
				treeTravelUIManager.showDialog(topLevelFrame, "输入要遍历的树");
			} else if (command.equals(SHORTEST_PATH_COMMAND)) {
				if (shortestPathUIManager == null) {
					shortestPathUIManager = new ShortestPathUIManager(textAreaManager, imagedAreaManager);
					String nodesString = "{v1, v2, v3, v4, v5, v6}";
					String startString = "v1";
					String edgesString = "{e1[2]=(v1,v2), e2[1]=(v1,v3), e3[4]=(v1,v3), e4[5]=(v2,v3), e5[2]=(v3,v4), e6[6]=(v2,v4), e7[3]=(v2,v5), e8[5]=(v4,v5), e9[1]=(v5,v6), e10[3]=(v4,v6)}";
					shortestPathUIManager.setGraphType(false);
					shortestPathUIManager.setInputs(nodesString, startString, edgesString);
					shortestPathUIManager.setAllTravels();
				}
				shortestPathUIManager.showDialog(topLevelFrame, "输入要计算的带权图");
			} else if (command.equals(SPANNING_TREE_COMMAND)) {
				if (spanningTreeUIManager == null) {
					spanningTreeUIManager = new SpanningTreeUIManager(textAreaManager, imagedAreaManager);
					String nodesString = "{v1, v2, v3, v4, v5, v6}";
					String edgesString = "{e1[2]=(v1,v2), e2[1]=(v1,v3), e3[4]=(v1,v3), e4[5]=(v2,v3), e5[2]=(v3,v4), e6[6]=(v2,v4), e7[3]=(v2,v5), e8[5]=(v4,v5), e9[1]=(v5,v6), e10[3]=(v4,v6)}";
					spanningTreeUIManager.setInputs(nodesString, edgesString);
					spanningTreeUIManager.setAllTravels();
				}
				spanningTreeUIManager.showDialog(topLevelFrame, "输入要计算的带权图");
			} else if (command.equals(HUFFMAN_TREE_COMMAND)) {
				if (huffmanTreeUIManager == null) {
					huffmanTreeUIManager = new HuffmanTreeUIManager(textAreaManager, imagedAreaManager);
					String nodesString = "{v1[10], v2[20], v3[5], v4[8], v5[15], v6[20], v7[12], v8[25]}";
					huffmanTreeUIManager.setInputs(nodesString);
					huffmanTreeUIManager.setAllTravels();
				}
				huffmanTreeUIManager.showDialog(topLevelFrame, "输入要计算的带权叶子顶点集");
			} else if (command.equals(SPECIAL_GRAPH_COMMAND)) {
				if (specialGraphUIManager == null) {
					specialGraphUIManager = new SpecialGraphUIManager(textAreaManager, imagedAreaManager);
					specialGraphUIManager.setInputs(3, 5);
					specialGraphUIManager.setAllDisplay();
				}
				specialGraphUIManager.showDialog(topLevelFrame, "输入要展示特殊图的顶点规模");
			} else if (command.equals(COMB_CALCULATOR_COMMAND)) {
				if (combCalculatorUIManager == null) {
					combCalculatorUIManager = new CombCalculatorUIManager(textAreaManager, imagedAreaManager);
					combCalculatorUIManager.setInputs(8, 5);
					combCalculatorUIManager.setAllDisplay();
				}
				combCalculatorUIManager.showDialog(topLevelFrame, "输入要进行计算的整数数值");
			} else if (command.equals(EXPR_CALCULATOR_COMMAND)) {
				if (exprCalculatorUIManager == null) {
					exprCalculatorUIManager = new ExprCalculatorUIManager(textAreaManager, imagedAreaManager);
					exprCalculatorUIManager.setInputs("2+3*4-C((2*2),2)!");
				}
				exprCalculatorUIManager.showDialog(topLevelFrame, "输入要进行计算的组合表达式");
			} else if (command.equals(RECU_EXPR_CALCULATOR_COMMAND)) {
				if (recuExpressionCalculatorUIManager == null) {
					recuExpressionCalculatorUIManager = new RecuExpressionCalculatorUIManager(textAreaManager, imagedAreaManager);
					recuExpressionCalculatorUIManager.setInputs("2*a+b", "3", "1", "2");
				}
				recuExpressionCalculatorUIManager.showDialog(topLevelFrame, "输入要进行计算的递推表达式、初始条件、递推次数");
			} else if (command.equals(COUNT_STRING_COMMAND)) {
				if (countStringUIManager == null) countStringUIManager = new CountStringUIManager(textAreaManager, imagedAreaManager);
				countStringUIManager.showDialog(topLevelFrame, "输入要进行计数的字符串信息");
			} else if (command.equals(COUNT_INTEGER_COMMAND)) {
				if (countIntegerUIManager == null) countIntegerUIManager = new CountIntegerUIManager(textAreaManager, imagedAreaManager);
				countIntegerUIManager.showDialog(topLevelFrame, "输入要进行计数的整数信息");
			} else if (command.equals(COUNT_SOLVER_COMMAND)) {
				if (countSolverUIManager == null) countSolverUIManager = new CountEquationSolverUIManager(textAreaManager, imagedAreaManager);
				countSolverUIManager.showDialog(topLevelFrame, "输入要进行计数的不定方程信息");
			} else if (command.equals(COUNT_FUNCTION_COMMAND)) {
				if (countFunctionUIManager == null) {
					countFunctionUIManager = new CountFunctionUIManager(textAreaManager, imagedAreaManager);
					String setAString = "{1, 2, 3, 4, 5, 6}";
					String setBString = "{a, b, c, d, e, f}";
					String maxDisplay = "100";
					countFunctionUIManager.setElementTypeInt(false);
					countFunctionUIManager.setInputs(setAString, setBString, maxDisplay);
					countFunctionUIManager.setFunctionProperties();
				}
				countFunctionUIManager.showDialog(topLevelFrame, "输入要计数函数的源集合和目标集合");
			} else if (command.equals(GEN_PERMUTATION_COMMAND)) {
				if (genPermutationUIManager == null) genPermutationUIManager = new GenPermutationUIManager(textAreaManager, imagedAreaManager);
				genPermutationUIManager.showDialog(topLevelFrame, "输入要生成的排列的信息");
			} else if (command.equals(GEN_COMBINATION_COMMAND)) {
				if (genCombinationUIManager == null) genCombinationUIManager = new GenCombinationUIManager(textAreaManager, imagedAreaManager);
				genCombinationUIManager.showDialog(topLevelFrame, "输入要生成的组合的信息");
			} else if (command.equals(GEN_REPCOMB_COMMAND)) {
				if (genRepCombUIManager == null) genRepCombUIManager = new GenRepCombUIManager(textAreaManager, imagedAreaManager);
				genRepCombUIManager.showDialog(topLevelFrame, "输入要生成的允许重复组合的信息");
			} else if (command.equals(GROUP_UM_COMMAND)) {
				if (groupUmUIManager == null) {
					groupUmUIManager = new GroupUmUIManager(textAreaManager, imagedAreaManager);
					groupUmUIManager.setInputs(11);
					groupUmUIManager.setAllDisplay();
				}
				groupUmUIManager.showDialog(topLevelFrame, "输入要展示的群U(m)的m值");
			} else if (command.equals(GROUP_PERM_COMMAND)) {
				if (groupPermUIManager == null) {
					groupPermUIManager = new GroupPermUIManager(textAreaManager, imagedAreaManager);
					groupPermUIManager.setInputs(3);
					groupPermUIManager.setAllDisplay();
				}
				groupPermUIManager.showDialog(topLevelFrame, "输入要展示的置换群S(m)的m值");
			} else if (command.equals(LATTICE_COMMAND)) {
				if (latticeUIManager == null) {
					latticeUIManager = new LatticeUIManager(textAreaManager, imagedAreaManager);
					String setAString = "{0, 1, 2, 3, 4, 5}";
					String relRString = "{<0, 2>, <0, 4>, <1, 2>, <2, 3>, <3, 5>}";
					latticeUIManager.setElementTypeInt(false);
					latticeUIManager.setInputs(setAString, relRString);
					latticeUIManager.setAllProperties();
					latticeUIManager.setUseHasseDiagramAsInput(true);
				}
				latticeUIManager.showDialog(topLevelFrame, "输入要判断的偏序关系");
			} else if (command.equals(BOOLEAN_COMMAND)) {
				if (boolAlgebraUIManager == null) {
					boolAlgebraUIManager = new BoolAlgebraUIManager(textAreaManager, imagedAreaManager);
					boolAlgebraUIManager.setInputs(1001);
					boolAlgebraUIManager.setAllDisplay();
				}
				boolAlgebraUIManager.showDialog(topLevelFrame, "输入要展示的格F(m)的m值");
			} else if (command.equals(CONFIG_COMMAND)) {
				if (configManager == null) configManager = new ConfigurationUIManager(imagedAreaManager);
				configManager.showDialog(topLevelFrame, "确定首选项");
			} else if (command.equals(BINARY_OPERATOR_COMMAND)) {
				if (binaryOperatorUIManager == null) {
					String setString = "{a,b,c}";
					String op1String = "{<a, a, a>, <a, b, b>, <a, c, c>, <b, a, b>, <b, b, c>, <b, c, a>, <c, a, c>, <c, b, a>, <c, c, b>}";
					String op2String = "{<a, a, a>, <a, b, b>, <a, c, c>, <b, a, b>, <b, b, a>, <b, c, c>, <c, a, c>, <c, b, c>, <c, c, c>}";
					binaryOperatorUIManager = new BinaryOperatorUIManager(textAreaManager, imagedAreaManager);
					binaryOperatorUIManager.setElementTypeInt(false);
					binaryOperatorUIManager.setInputs(setString, op1String, op2String);
					binaryOperatorUIManager.setAllProperties();
				}
				binaryOperatorUIManager.showDialog(topLevelFrame, "输入要判断性质的运算");
			} else if (command.equals(TEST_COMMAND)) {
				// 弹出一窗口显示一些信息
				JOptionPane.showMessageDialog(MainSwingFrame.getMainFrame(), "这个功能还在测试中....!", "离散数学基础例题习题展示", JOptionPane.WARNING_MESSAGE);
			} else if (command.equals(CLEAR_COMMAND)) {
				textAreaManager.clearContent();
				imagedAreaManager.clearContent();
				imagedAreaManager.revalidate();
			}
		}
		
		public void doShowUsage() {
			textAreaManager.clearContent();
			imagedAreaManager.clearContent();
			imagedAreaManager.appendPlainStringAsNewLine("软件使用简介：");
			imagedAreaManager.appendPlainStringAsNewLine("    ");
			imagedAreaManager.appendPlainStringAsNewLine("    Deedm(Demonstrator of Examples in Elementary Discrete Mathematics)是一款用于展示");
			imagedAreaManager.appendPlainStringAsNewLine("教材《离散数学基础》（周晓聪、乔海燕编著，清华大学出版社）中的计算型例题的工具。");
			imagedAreaManager.appendPlainStringAsNewLine("    Deedm主要功能包括：构造命题逻辑公式真值表、计算命题逻辑公式范式、集合关系运算、");
			imagedAreaManager.appendPlainStringAsNewLine("关系性质判断、关系闭包判断、等价关系、偏序关系展示、函数性质判断、字符串、整数、不定方");
			imagedAreaManager.appendPlainStringAsNewLine("程解和函数计数、排列、组合和不允许重复组合的生成、图和树的遍历、带权图最短距离和最小生");
			imagedAreaManager.appendPlainStringAsNewLine("成树的计算、哈夫曼树构造、代数运算性质判断、群U(m)和置换群信息展示、格的性质判断等，");
			imagedAreaManager.appendPlainStringAsNewLine("涵盖了教材中绝大多数计算型例题所需要进行的计算功能。");

			textAreaManager.appendContentAsNewLine("软件简介：");
			textAreaManager.appendContentAsNewLine("    ");
			textAreaManager.appendContentAsNewLine("    Deedm(Demonstrator of Examples in Elementary Discrete Mathematics)是一款用于展示");
			textAreaManager.appendContentAsNewLine("教材《离散数学基础》（周晓聪、乔海燕编著，清华大学出版社）中的计算型例题的工具。");
			textAreaManager.appendContentAsNewLine("    Deedm主要功能包括：构造命题逻辑公式真值表、计算命题逻辑公式范式、集合关系运算、");
			textAreaManager.appendContentAsNewLine("关系性质判断、关系闭包判断、等价关系、偏序关系展示、函数性质判断、字符串、整数、不定方");
			textAreaManager.appendContentAsNewLine("程解和函数计数、排列、组合和不允许重复组合的生成、图和树的遍历、带权图最短距离和最小生");
			textAreaManager.appendContentAsNewLine("成树的计算、哈夫曼树构造、代数运算性质判断、群U(m)和置换群信息展示、格的性质判断等，");
			textAreaManager.appendContentAsNewLine("涵盖了教材中绝大多数计算型例题所需要进行的计算功能。");

			imagedAreaManager.appendPlainStringAsNewLine("    这里以构造命题逻辑公式真值表为例，简要说明软件的用法。这项功能是主菜单“命题逻辑”");
			imagedAreaManager.appendPlainStringAsNewLine("下的一项子菜单功能，点击该子菜单，进入下面的界面输入一个或多个命题逻辑公式：");
			String fileName = Configuration.imageFilePath + "UsagePropInput.png";
			imagedAreaManager.appendPlainStringAsNewLine("   ");
			imagedAreaManager.appendImageFileToLastLine(fileName);
			imagedAreaManager.appendPlainStringAsNewLine("输入区域是使用LaTeX命令表示的命题逻辑公式，其中使用\\wedge, \\vee, \\neg, \\rightarrow和");
			imagedAreaManager.appendPlainStringAsNewLine("\\leftrightarrow分别表示");
			imagedAreaManager.appendLaTeXStringToLastLine("\\wedge, \\vee, \\neg, \\rightarrow");
			imagedAreaManager.appendPlainStringToLastLine(" 和 ");
			imagedAreaManager.appendLaTeXStringToLastLine("\\leftrightarrow");
			imagedAreaManager.appendPlainStringToLastLine("。输入多个公式时使用英文分号\';\'分隔这些公式。这个");
			imagedAreaManager.appendPlainStringAsNewLine("输入界面有一个反馈区，将上述输入的命题逻辑公式以数学符号化的图片展示出来，反馈区下面是");
			imagedAreaManager.appendPlainStringAsNewLine("按钮区，其中第一部分的按钮可给出教材的各个例子中的命题逻辑公式作为输入，第二部分的按钮");
			imagedAreaManager.appendPlainStringAsNewLine("用于执行构造真值表，或随机生成更多公式、清空输入内容、清理输出屏幕内容、检查输入合法性、");
			imagedAreaManager.appendPlainStringAsNewLine("返回主菜单等功能。");
			imagedAreaManager.appendPlainStringAsNewLine("    在输入界面点击构造真值表，将在输出屏幕区域显示构造的真值表的信息。软件的输出屏幕区");
			imagedAreaManager.appendPlainStringAsNewLine("域分为两大块，左边区域（也就是现在这里显示软件使用简介的区域）以中文+图片形式输出，例如");
			imagedAreaManager.appendPlainStringAsNewLine("上面输入的命题逻辑公式得到的输入如下：");
			fileName = Configuration.imageFilePath + "UsagePropOutput1.png";
			imagedAreaManager.appendPlainStringAsNewLine("   ");
			imagedAreaManager.appendImageFileToLastLine(fileName);
			imagedAreaManager.appendPlainStringAsNewLine("输出屏幕区域的右边区域以英文+LaTeX源代码文本的形式给出输出结果：");
			fileName = Configuration.imageFilePath + "UsagePropOutput2.png";
			imagedAreaManager.appendPlainStringAsNewLine("   ");
			imagedAreaManager.appendImageFileToLastLine(fileName);
			imagedAreaManager.appendPlainStringAsNewLine("左边的中文+图片区域不支持将其中的内容复制到其他软件，而右边的英文+LaTeX源代码文本支");
			imagedAreaManager.appendPlainStringAsNewLine("持复制其中的内容到其他软件（例如其他的LaTeX文件编译软件）。");
			imagedAreaManager.appendPlainStringAsNewLine("    实现集合运算等其他功能的子菜单选中之后弹出的输入界面与上面介绍的输入命题逻辑公式");
			imagedAreaManager.appendPlainStringAsNewLine("的界面类似，从各子菜单的输入界面给出的提示，或给出的教材例子、随机生成的例子用户应该可");
			imagedAreaManager.appendPlainStringAsNewLine("以清楚应该输入什么内容，以及输入的内容的格式。为简单起见，在输入集合、关系、运算表甚至");
			imagedAreaManager.appendPlainStringAsNewLine("图和树时，都是采用规定格式的字符串的形式输入，软件将检查输入字符串的合法性，提取其中的");
			imagedAreaManager.appendPlainStringAsNewLine("信息，转换成图片形式的集合、关系等显示在反馈区域以及输出屏幕区域。");
			imagedAreaManager.appendPlainStringAsNewLine("    另外需要说明的是，我们目前是使用GraphViz生成关系图、无向图、有向图和树等图形，");
			imagedAreaManager.appendPlainStringAsNewLine("因此为了得到这些图形，用户需要自己下载并安装GraphViz软件，并且在我们这个软件“帮助”");
			imagedAreaManager.appendPlainStringAsNewLine("主菜单下的子菜单“首选项”设置GraphViz可执行文件所在的路径，以及这个软件自动生成的一");
			imagedAreaManager.appendPlainStringAsNewLine("些临时文件存放的目录。这个“首选项”子菜单给出的输入界面如下：");
			fileName = Configuration.imageFilePath + "UsageConfig.png";
			imagedAreaManager.appendPlainStringAsNewLine("   ");
			imagedAreaManager.appendImageFileToLastLine(fileName);
			imagedAreaManager.appendPlainStringAsNewLine("    最后，谢谢大家使用这个软件，有什么问题请联系：周晓聪(isszxc@mail.sysu.edu.cn)。");
			
			
			imagedAreaManager.revalidate();
		}
	}		
}
