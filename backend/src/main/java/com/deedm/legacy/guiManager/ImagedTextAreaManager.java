/**
 * 
 */
package com.deedm.legacy.guiManager;

import java.awt.Container;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import com.deedm.legacy.graphicAnimation.AnimationArea;
import com.deedm.legacy.graphicAnimation.AnimationView;

/**
 * @author user
 *
 */
public class ImagedTextAreaManager {
	ImagedTextAreaAnimator animator = null;
	AnimationArea outputArea = null;
	JScrollPane scrollPane = null;
	
	public ImagedTextAreaManager() {
	}

	/**
	 * 初始化该区域。该区域将放置在place，初始化后返回管理该区域的界面构件（一个支持滚动的面板）
	 */
	protected JComponent initialize(Container place) {
		outputArea = new ImagedTextArea(0, 0);
		scrollPane = new JScrollPane();
		scrollPane.setViewportView(outputArea);

		animator = new ImagedTextAreaAnimator(outputArea);
		outputArea.setAnimator(animator);
		return scrollPane;
	}
	
	public boolean appendPlainStringAsNewLine(String plainString) {
		animator.appendPlainStringAsNewLine(plainString);
		return true;
	}
	
	public boolean appendLaTeXStringAsNewLine(String laTeXString) {
		animator.appendLaTeXStringAsNewLine(laTeXString);
		return true;
	}

	public boolean appendImageFileAsNewLine(String imageFileName) {
		animator.appendImageFileAsNewLine(imageFileName);
		return true;
	}
	
	public boolean appendAnimationViewAsNewLine(AnimationView newView) {
		animator.appendAnimationViewAsNewLine(newView);
		return true;
	}

	public boolean appendPlainStringToLastLine(String plainString) {
		animator.appendPlainStringToLastLine(plainString);
		return true;
	}
	
	public boolean appendLaTeXStringToLastLine(String laTeXString) {
		animator.appendLaTeXStringToLastLine(laTeXString);
		return true;
	}
	
	public boolean appendImageFileToLastLine(String imageFileName) {
		animator.appendImageFileToLastLine(imageFileName);
		return true;
	}

	public boolean appendAnimationViewToLastLine(AnimationView newView) {
		animator.appendAnimationViewToLastLine(newView);
		return true;
	}

	public void clearContent() {
		animator.clearContent();
	}
	
	public void revalidate() {
		int width = animator.getWidth();
		int height = animator.getHeight();
		int lastHeight = animator.getLastLineHeight();
		int viewPortHeight = outputArea.getParent().getHeight();

		outputArea.setPreferredSize(width+10, height+lastHeight+10);
		
		Rectangle viewPort = new Rectangle(0, height-viewPortHeight, width, viewPortHeight);
		outputArea.scrollRectToVisible(viewPort);
		scrollPane.revalidate();
		animator.update();
	}
}


