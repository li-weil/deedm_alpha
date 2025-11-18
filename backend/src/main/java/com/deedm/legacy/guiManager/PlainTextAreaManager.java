package com.deedm.legacy.guiManager;

import java.awt.Container;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.deedm.legacy.util.Configuration;

/**
 * @author Zhou Xiaocong
 * @Since 2020/09/28
 * 管理主界面的纯文本输出区域的工具类。这个区域输出的是纯文本，因此支持使用Ctrl+C和Ctrl+V等拷贝、粘贴其中的内容
 */
public class PlainTextAreaManager {
	protected JTextArea outputArea = null;
	
	public PlainTextAreaManager() {
	}
	
	/**
	 * 初始化该区域。该区域将放置在place，初始化后返回管理该区域的界面构件（一个支持滚动的面板）
	 */
	public JComponent initialize(Container place) {
		outputArea = new JTextArea();
		outputArea.setFont(Configuration.plainStringFont);
		outputArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(outputArea);
		
		return scrollPane;
	}
	
	public boolean appendContent(String content) {
		outputArea.append(content);
		return true;
	}
	
	public boolean appendContentAsNewLine(String content) {
		outputArea.append("\n" + content);
		return true;
	}
	
	public boolean appendContentToLastLine(String content) {
		outputArea.append(content);
		return true;
	}
	
	public boolean clearContent() {
		outputArea.setText("");
		return true;
	}

}
