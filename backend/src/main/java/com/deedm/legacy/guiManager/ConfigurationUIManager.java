/**
 * 
 */
package com.deedm.legacy.guiManager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.deedm.legacy.graphicAnimation.AnimationArea;
import com.deedm.legacy.util.Configuration;

/**
 * @author user
 *
 */
public class ConfigurationUIManager extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private static final int width = Math.max(2* MainSwingFrame.screenWidth/5, 1024)*(MainSwingFrame.screenResolution/96);
	private static final int height = Math.max(MainSwingFrame.screenHeight/3, 480)*(MainSwingFrame.screenResolution/96);

	private JDialog dialog = null;
	private JButton defaultButton = null;
	private JTextField dataPathField = null;
	private JTextField dotPathField = null;
	private JComboBox<String> fontFaceBox = null;
	private JComboBox<String> fontStyleBox = null;
	private JComboBox<String> fontSizeBox = null;
	private JComboBox<String> laTeXSizeBox = null;

	SimpleListener listener = null;
	
	private String[] fontNames;		// 当前平台所使用的字体
	private String[] fontStyles = {"常规", "斜体", "粗体", "粗斜体"};
	private	String[] fontSizes = {"10", "12", "16", "20", "24", "32", "36", "48", "60", "72", "96"};
	private int currentFaceIndex = 0;
	private int currentStyleIndex = 0;
	private int currentSizeIndex = 0;
	private int currentLaTeXSizeIndex = 0;

	
	private ImagedTextAreaAnimator feedbackAnimator = null;
	private ImagedTextAreaManager imageManager = null;
	private String laTeXString = "(p\\rightarrow q)\\rightarrow r";
	private String message = "公式" + laTeXString + "生成的图片：";
	
	private boolean ok = false;

	public ConfigurationUIManager(ImagedTextAreaManager imageManager) {
		listener = new SimpleListener();
		this.imageManager = imageManager;
		
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
		
		displayFeedback();
		Point position = owner.getLocation();
		Dimension size = owner.getSize();
		position.x = position.x + (size.width - width)/2;
		position.y = position.y + (size.height -height)/2;
		dialog.setLocation(position);
		dialog.setTitle(title);
		dialog.setVisible(true);
		return ok;
	}
	
	// 创建输入集合的窗格
	private JPanel createInputSetPanel() {
		// 创建用于输入公式的窗格，使用盒式布局管理
		JPanel configPanel = new JPanel();
		configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));
		// 设置该窗格使用的带标题的边框
 		configPanel.setBorder(BorderFactory.createTitledBorder("确定选项（选择的字体确定或应用后会影响屏幕已有文字和以后生成的图片，但不影响已有图片）"));

 		int inputWidth = 60;
		// 创建一个标签用于提示输入集合信息
 		JPanel tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
		JLabel promptLabel = new JLabel("临时文件的保存目录(T)：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+U可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('T');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		dataPathField = new JTextField(inputWidth);
		dataPathField.setText(Configuration.dataFilePath);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(dataPathField);
		JButton button = new JButton("浏览");
		button.setActionCommand("TEMP_DIR");			
		button.addActionListener(listener);		// 添加事件监听器
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(dataPathField);
		tempPanel.add(button);
		configPanel.add(tempPanel);
		
		// 创建一个标签用于提示输入集合信息
 		tempPanel = new JPanel();
 		tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
		promptLabel = new JLabel("GraphViz的可执行文件目录(G)：", JLabel.RIGHT);
		// 设置选择该标签的快捷键，这使得用户按Alt+U可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('G');	
		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		dotPathField = new JTextField(inputWidth);
		dotPathField.setText(Configuration.graphVizPath);
		// 设置刚才创建的标签与nameField相关联（即用于提示该字段所要输入的信息）
		promptLabel.setLabelFor(dotPathField);
		button = new JButton("浏览");
		button.setActionCommand("GRAPH_VIZ");			
		button.addActionListener(listener);		// 添加事件监听器
		// 将标签与文本字段加入到临时的窗格
		tempPanel.add(promptLabel);
		tempPanel.add(dotPathField);
		tempPanel.add(button);
		configPanel.add(tempPanel);
		
		initFontInformation();
		JPanel selectPanel = new JPanel();
		selectPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 25, 15));

		// 创建输入集合元素的文本字段，缺省宽度为12, 但这不意味着只能输入12个字符
		// 创建一组合框用于选择字体
		fontFaceBox = new JComboBox<String>(fontNames);
		fontFaceBox.setSelectedIndex(currentFaceIndex);
		fontFaceBox.setActionCommand("face");
		promptLabel = new JLabel("文本字体(F)：", JLabel.LEADING);
		// 设置选择该标签的快捷键，这使得用户按Alt+U可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('F');	
		promptLabel.setLabelFor(fontFaceBox);
		tempPanel = new JPanel();
 		tempPanel.setLayout(new BoxLayout(tempPanel, BoxLayout.Y_AXIS));
		tempPanel.add(promptLabel);
		tempPanel.add(fontFaceBox);
		selectPanel.add(tempPanel);
		
		// 创建一组合框用于选择字形
		fontStyleBox = new JComboBox<String>(fontStyles);
		fontStyleBox.setSelectedIndex(currentStyleIndex);
		fontStyleBox.setActionCommand("style");
		promptLabel = new JLabel("文本字形(X)：", JLabel.LEADING);
		// 设置选择该标签的快捷键，这使得用户按Alt+U可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('X');	
		promptLabel.setLabelFor(fontStyleBox);
		tempPanel = new JPanel();
 		tempPanel.setLayout(new BoxLayout(tempPanel, BoxLayout.Y_AXIS));
		tempPanel.add(promptLabel);
		tempPanel.add(fontStyleBox);
		selectPanel.add(tempPanel);
		
		// 创建一组合框用于选择字体大小
		fontSizeBox = new JComboBox<String>(fontSizes);
		fontSizeBox.setSelectedIndex(currentSizeIndex);
		fontSizeBox.setActionCommand("size");
		promptLabel = new JLabel("文本字体大小(S)：", JLabel.LEADING);
		// 设置选择该标签的快捷键，这使得用户按Alt+U可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('S');	
		promptLabel.setLabelFor(fontSizeBox);
		tempPanel = new JPanel();
 		tempPanel.setLayout(new BoxLayout(tempPanel, BoxLayout.Y_AXIS));
		tempPanel.add(promptLabel);
		tempPanel.add(fontSizeBox);
		selectPanel.add(tempPanel);

		// 创建一组合框用于选择字体大小
		laTeXSizeBox = new JComboBox<String>(fontSizes);
		laTeXSizeBox.setSelectedIndex(currentLaTeXSizeIndex);
		laTeXSizeBox.setActionCommand("size");
		promptLabel = new JLabel("LaTeX公式图片字体大小(Z)：", JLabel.LEADING);
		// 设置选择该标签的快捷键，这使得用户按Alt+U可马上定位到输入姓名的文本字段
		promptLabel.setDisplayedMnemonic('Z');	
		promptLabel.setLabelFor(laTeXSizeBox);
		tempPanel = new JPanel();
 		tempPanel.setLayout(new BoxLayout(tempPanel, BoxLayout.Y_AXIS));
		tempPanel.add(promptLabel);
		tempPanel.add(laTeXSizeBox);
		selectPanel.add(tempPanel);
		
		configPanel.add(selectPanel);

		JPanel displayPanel = new JPanel();
		displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
		// 设置该窗格使用的带标题的边框
		displayPanel.setBorder(BorderFactory.createTitledBorder("你选择的字体效果如下："));
 		AnimationArea area = new AnimationArea(width, 2*height/5);
		feedbackAnimator = new ImagedTextAreaAnimator(area);
		displayPanel.add(area);
		
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new GridLayout(2, 1));
		inputPanel.add(configPanel);
		inputPanel.add(displayPanel);
		return inputPanel;
	}

	// 创建按钮及其所在的窗格
	private JPanel createButtonPanel() {
		// 创建一临时性窗格，将这些按钮加入到该窗格，该窗格使用流式布局管理
		JPanel buttonPanelOne = new JPanel();
		buttonPanelOne.setLayout(new FlowLayout(FlowLayout.CENTER, 45, 0));
		// 创建用于确定输入信息的“确定”按钮
		JButton actButton = new JButton("确认(Y)");
		actButton.setMnemonic('Y');					// 设置快捷键
		// 设置动作命令的名称，这可用来区分用户到底按了什么按钮
		actButton.setActionCommand("ok");			
		actButton.addActionListener(listener);		// 添加事件监听器
		buttonPanelOne.add(actButton);
		defaultButton = actButton;

		// 创建用于确定输入信息的“确定”按钮
		actButton = new JButton("应用(A)");
		actButton.setMnemonic('A');					// 设置快捷键
		// 设置动作命令的名称，这可用来区分用户到底按了什么按钮
		actButton.setActionCommand("apply");			
		actButton.addActionListener(listener);		// 添加事件监听器
		buttonPanelOne.add(actButton);

		// 创建用于放弃输入信息的“放弃”按钮
		actButton = new JButton("返回(R)");
		actButton.setMnemonic('R');				// 设置快捷键
		// 设置动作命令的名称，这可用来区分用户到底按了什么按钮
		actButton.setActionCommand("cancel");			
		actButton.addActionListener(listener);	// 添加事件监听器
		buttonPanelOne.add(actButton);
		
		return buttonPanelOne;
	}
	
	// 初始化字体名数组，并返回当前所用的字体名字在该数组中的下标
	public void initFontInformation() {
		// 取当前平台的图形环境
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment(); 
		fontNames = ge.getAvailableFontFamilyNames();
		// 取主画框所使用的字体
		String currentFontName = Configuration.plainStringFont.getName();
		for (int i = 0; i < fontNames.length; i++) {
			if (currentFontName.equals(fontNames[i])) currentFaceIndex = i;
		}
		int currentFontStyle = Configuration.plainStringFont.getStyle();
		if (currentFontStyle == Font.PLAIN) currentStyleIndex = 0;
		else if (currentFontStyle == Font.ITALIC) currentStyleIndex = 1;
		else if (currentFontStyle == Font.BOLD) currentStyleIndex = 2;
		else if (currentFontStyle == Font.BOLD+Font.ITALIC) currentStyleIndex = 3;
		int currentFontSize = Configuration.plainStringFont.getSize();
		for (int i = 0; i < fontSizes.length; i++) {
			int size = Integer.parseInt(fontSizes[i], 10);
			if (size == currentFontSize) currentSizeIndex = i;
			if (size == Configuration.laTeXImageFontSize) currentLaTeXSizeIndex = i;
		}
	}

	private void setConfiguration() {
		Configuration.dataFilePath = dataPathField.getText();
		Configuration.graphVizPath = dotPathField.getText();
		
		Font currentFont = Configuration.plainStringFont;
		String oldFontName = currentFont.getFontName();
		int oldFontStyle = currentFont.getStyle();
		int oldFontSize = currentFont.getSize();

		String fontName = (String)fontFaceBox.getSelectedItem();
		String fontSizeString = (String)fontSizeBox.getSelectedItem();
		int fontSize = Integer.parseInt(fontSizeString, 10);
		int selectIndex = fontStyleBox.getSelectedIndex();
		int fontStyle = Font.PLAIN;
		if (selectIndex == 1) fontStyle = Font.ITALIC;
		else if (selectIndex == 2) fontStyle = Font.BOLD;
		else if (selectIndex == 3) fontStyle = Font.BOLD+Font.ITALIC;

		// 设置新的字体
		if (!oldFontName.equals(fontName) || oldFontSize != fontSize || oldFontStyle != fontStyle) {
			Configuration.plainStringFont = new Font(fontName, fontStyle, fontSize);
		}
		fontSizeString = (String)laTeXSizeBox.getSelectedItem();
		Configuration.laTeXImageFontSize = Integer.parseInt(fontSizeString, 10);
		Configuration.save();
	}
	
	private void displayFeedback() {
		feedbackAnimator.clearContent();
		feedbackAnimator.appendPlainStringAsNewLine("当前选择的字体效果是：");
		feedbackAnimator.appendPlainStringAsNewLine(message);
		feedbackAnimator.appendLaTeXStringToLastLine(laTeXString);
		feedbackAnimator.update();
	}

	
	// 监听按钮的事件
	private class SimpleListener implements ActionListener {
		// 事件监听程序
		public void actionPerformed(ActionEvent evt) {
			String command = evt.getActionCommand();
			if (command.equals("ok")) {
				ok = true;
				setConfiguration();
				displayFeedback();
				imageManager.revalidate();
				dialog.setVisible(false);
			} else if (command.equals("apply")) {
				ok = true;
				setConfiguration();
				displayFeedback();
				imageManager.revalidate();
			} else if (command.equals("cancel")) {
				ok = false;
				dialog.setVisible(false);
			} else if (command.equals("TEMP_DIR")) {
				File file = new File(dataPathField.getText());
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("选择临时文件的保存目录");    
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (file != null) {
					if (file.isDirectory()) chooser.setCurrentDirectory(file);
				}
				//表示只能选择文件夹（或者叫目录类型）        
				chooser.showOpenDialog(dialog);
				if (chooser.getSelectedFile() != null) dataPathField.setText(chooser.getSelectedFile().getAbsolutePath() + File.separator);				
			} else if (command.equals("GRAPH_VIZ")) {
				File file = new File(dotPathField.getText());
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("GraphViz的可执行文件目录");    
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (file != null) {
					if (file.isDirectory()) chooser.setCurrentDirectory(file);
				}
				//表示只能选择文件夹（或者叫目录类型）        
				chooser.showOpenDialog(dialog);
				if (chooser.getSelectedFile() != null) dotPathField.setText(chooser.getSelectedFile().getAbsolutePath() + File.separator);				
			}
		}
	}
	
}
