/**
 * 
 */
package com.deedm.legacy.guiManager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.deedm.legacy.proplogic.FormulaBuilder;
import com.deedm.legacy.proplogic.formula.Formula;

/**
 * @author user
 *
 */
public class LaTeXFormulaInputManager extends JPanel {
	private static final long serialVersionUID = 1647932789529734950L;
	
	private int width = 800;
	private int height = 500;
	private JTextArea formulaArea = null;
	private boolean ok = false;
	private JDialog dialog = null;
	private ImagedTextAreaAnimator formulaAnimator = null;
	private JScrollPane imagedTextScrollPane = null;
	private ImagedTextArea imagedTextArea = null;
	JPanel displayPanel = null;
	String promptString = "";
	
	public LaTeXFormulaInputManager(int width, int height, JPanel buttonPanel) {
		this.width = width;
		this.height = height;
		promptString = "请输入基于LaTeX命令的公式，每行一个公式或同一行多个公式用英文分号分隔：";
		setLayout(new BorderLayout());
		JPanel formulaPanel = createFormulaPanel();
		add(formulaPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}
	
	public LaTeXFormulaInputManager(int width, int height, JPanel buttonPanel, String promptString) {
		this.width = width;
		this.height = height;
		this.promptString = promptString;
		setLayout(new BorderLayout());
		JPanel formulaPanel = createFormulaPanel();
		add(formulaPanel, BorderLayout.CENTER);
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
//			dialog.getRootPane().setDefaultButton(okButton);

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
	
	public void clearFormula() {
		formulaAnimator.clearContent();
		formulaArea.setText("");
		updateFeedback();
	}
	
	public boolean setContent(String content) {
		formulaArea.setText(content);
		return true;
	}
	
	public boolean setFormula(String laTeXString) {
		boolean success = true;
		formulaAnimator.clearContent();
		Formula formula = FormulaBuilder.buildFromLaTexFormulaString(laTeXString);
		if (formula != null) {
			formulaAnimator.appendLaTeXStringAsNewLine(laTeXString);
		} else {
			formulaAnimator.appendPlainStringAsNewLine("字符串 " + laTeXString + " 不是合法的公式！");
			success = false;
		}
		formulaArea.setText(laTeXString);
		updateFeedback();
		return success;
	}
	
	public boolean setFormulaList(String content) {
		List<String> laTeXStringList = splitLaTeXString(content);
		boolean success = true;
		formulaAnimator.clearContent();
		for (String laTeXString : laTeXStringList) {
			Formula formula = FormulaBuilder.buildFromLaTexFormulaString(laTeXString);
			if (formula != null) {
				formulaAnimator.appendLaTeXStringAsNewLine(laTeXString);
			} else {
				success = false;
			}
		}
		formulaArea.setText(content);
		updateFeedback();
		return success;
	}
	
	public boolean setFormulaList(List<String> laTeXStringList) {
		StringBuffer content = new StringBuffer();
		boolean success = true;
		formulaAnimator.clearContent();
		for (String laTeXString : laTeXStringList) {
			content.append(laTeXString + ";");
			Formula formula = FormulaBuilder.buildFromLaTexFormulaString(laTeXString);
			if (formula != null) {
				formulaAnimator.appendLaTeXStringAsNewLine(laTeXString);
			} else {
				success = false;
			}
		}
		formulaArea.setText(content.toString());
		updateFeedback();
		return success;
	}
	
	public String getContent() {
		return formulaArea.getText();
	}

	public List<String> getFormulaList() {
		String content = formulaArea.getText();
		return splitLaTeXString(content);
	}
	
	public void clearFeedback() {
		formulaAnimator.clearContent();
	}

	public void appendLaTeXStringAsNewFeedbackLine(String laTeXString) {
		formulaAnimator.appendLaTeXStringAsNewLine(laTeXString);
	}

	public void appendLaTeXStringToLastFeedbackLine(String laTeXString) {
		formulaAnimator.appendLaTeXStringToLastLine(laTeXString);
	}

	public void appendPlainStringAsNewFeedbackLine(String laTeXString) {
		formulaAnimator.appendPlainStringAsNewLine(laTeXString);
	}

	public void appendPlainStringToLastFeedbackLine(String laTeXString) {
		formulaAnimator.appendPlainStringToLastLine(laTeXString);
	}
	
	public void updateFeedback() {
		int width = formulaAnimator.getWidth();
		int height = formulaAnimator.getHeight();
		int lastHeight = formulaAnimator.getLastLineHeight();
		int viewPortHeight = imagedTextArea.getParent().getHeight();
		
		imagedTextArea.setPreferredSize(width+10, height+lastHeight+10);
		
		Rectangle viewPort = new Rectangle(0, height+lastHeight-viewPortHeight, width, viewPortHeight);
		imagedTextArea.scrollRectToVisible(viewPort);
		imagedTextScrollPane.revalidate();
		formulaAnimator.update();
	}

	public Component getDialog() {
		return dialog;
	}
	
	public void setDialogStatus(boolean isOk) {
		ok = isOk;
	}
	
	public void setDialogVisible(boolean visible) {
		dialog.setVisible(visible);
	}

	private List<String> splitLaTeXString(String content) {
		List<String> result = new ArrayList<String>();
		String[] splitedContent = content.split("[;\n\r]");
		for (int i = 0; i < splitedContent.length; i++) result.add(splitedContent[i]);

/*		
		StringBuffer buffer = new StringBuffer();
		int index = 0;
		boolean hasFormula = false;
		while (index < content.length()) {
			char ch = content.charAt(index);
			if (ch != ';' && ch != '\n' && ch != '\r') {
				buffer.append(ch);
				hasFormula = true;
			} else {
				if (hasFormula) {
					result.add(buffer.toString());
					buffer = new StringBuffer();
					hasFormula = false;
				}
			}
			index = index + 1;
		}
		if (hasFormula) result.add(buffer.toString());
*/
		return result;
	}
	
	// 创建输入公式的窗格
	private JPanel createFormulaPanel() {
		// 创建用于输入公式的窗格，使用盒式布局管理
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		// 设置该窗格使用的带标题的边框
 		inputPanel.setBorder(BorderFactory.createTitledBorder(promptString));
		// 创建用于输入个人简历的文本字段，缺省为3行300列
		formulaArea = new JTextArea(3, 300);
		formulaArea.setLineWrap(true);			// 设置在输入时自动折行
		// 将文本区域放置在一个滚动窗格中，以支持输入多行文本
		JScrollPane scroll = new JScrollPane(formulaArea);
		inputPanel.add(scroll);
		
		displayPanel = new JPanel();
		displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
		// 设置该窗格使用的带标题的边框
		displayPanel.setBorder(BorderFactory.createTitledBorder("你输入的公式如下："));
 		imagedTextArea = new ImagedTextArea(width, height/2);
		formulaAnimator = new ImagedTextAreaAnimator(imagedTextArea);
		imagedTextArea.setAnimator(formulaAnimator);
		imagedTextScrollPane = new JScrollPane(imagedTextArea);
		displayPanel.add(imagedTextScrollPane);
		
		JPanel formulaPanel = new JPanel();
		formulaPanel.setLayout(new GridLayout(2, 1));
		formulaPanel.add(inputPanel);
		formulaPanel.add(displayPanel);
		return formulaPanel;
	}

}
