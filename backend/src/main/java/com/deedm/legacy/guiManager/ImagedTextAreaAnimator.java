package com.deedm.legacy.guiManager;

import java.awt.Graphics;
import java.awt.Point;

import com.deedm.legacy.graphicAnimation.AnimationArea;
import com.deedm.legacy.graphicAnimation.AnimationView;
import com.deedm.legacy.graphicAnimation.Animator;
import com.deedm.legacy.graphicAnimation.graphAnimationView.ImageFileAnimationView;
import com.deedm.legacy.graphicAnimation.textAnimationView.LaTeXStringAnimationView;
import com.deedm.legacy.graphicAnimation.textAnimationView.PlainStringAnimationView;
import com.deedm.legacy.graphicAnimation.textAnimationView.TextDocumentAnimationView;
import com.deedm.legacy.graphicAnimation.textAnimationView.TextLineAnimationView;

/**
 * @author Zhou Xiaocong
 * @since 2020/09/28
 * 管理一个可以输出字符串、图形等的区域，这个区域中的字符串的输出也是作为图片输出，不支持拷贝与粘贴。
 */
public class ImagedTextAreaAnimator extends Animator {
	protected TextDocumentAnimationView view = null;

	public ImagedTextAreaAnimator(AnimationArea area) {
		super(area);
		view = new TextDocumentAnimationView();
		view.setPosition(new Point(0, 0));
	}
	
	public boolean appendPlainStringAsNewLine(String plainString) {
		PlainStringAnimationView stringView = new PlainStringAnimationView(plainString);
		int x = view.getLastLineX();
		int y = view.getLastLineY();
		int height = view.getLastLineHeight();
		int newy = y + height;
		if (!view.isEmpty()) newy = newy + view.getSpaceBetweenLine();
		stringView.setPosition(new Point(x, newy));

		Graphics currentGC = area.getGraphics();
		stringView.calculateBounds(currentGC);

		TextLineAnimationView newLine = new TextLineAnimationView();
		newLine.appendView(stringView);
		view.appendView(newLine);
		
		return true;
	}

	public boolean appendLaTeXStringAsNewLine(String laTeXString) {
		LaTeXStringAnimationView stringView = new LaTeXStringAnimationView(laTeXString);
		int x = view.getLastLineX();
		int y = view.getLastLineY();
		int height = view.getLastLineHeight();
		int newy = y + height;
		if (!view.isEmpty()) newy = newy + view.getSpaceBetweenLine();

		stringView.createImage();
		stringView.setPosition(new Point(x, newy));

		TextLineAnimationView newLine = new TextLineAnimationView();
		newLine.appendView(stringView);
		view.appendView(newLine);
		
		return true;
	}

	public boolean appendImageFileAsNewLine(String imageFileName) {
		int x = view.getLastLineX();
		int y = view.getLastLineY();
		int height = view.getLastLineHeight();
		int newy = y + height;
		if (!view.isEmpty()) newy = newy + view.getSpaceBetweenLine();

		ImageFileAnimationView imageView = new ImageFileAnimationView(imageFileName);
		imageView.loadImage();
		imageView.setPosition(new Point(x, newy));

		TextLineAnimationView newLine = new TextLineAnimationView();
		newLine.appendView(imageView);
		view.appendView(newLine);
		
		return true;
	}
	
	public boolean appendAnimationViewAsNewLine(AnimationView newView) {
		int x = view.getLastLineX();
		int y = view.getLastLineY();
		int height = view.getLastLineHeight();
		int newy = y + height;
		if (!view.isEmpty()) newy = newy + view.getSpaceBetweenLine();
		newView.setPosition(new Point(x, newy));

		TextLineAnimationView newLine = new TextLineAnimationView();
		newLine.appendView(newView);
		view.appendView(newLine);
		
		return true;
	}
	
	
	public boolean appendPlainStringToLastLine(String plainString) {
		if (view.isEmpty()) return appendPlainStringAsNewLine(plainString);
		
		TextLineAnimationView lastLine = view.getLastLineView();
		
		PlainStringAnimationView stringView = new PlainStringAnimationView(plainString);
		int x = lastLine.getNextX();
		int y = lastLine.getNextY();
		stringView.setPosition(new Point(x, y));
		
		Graphics currentGC = area.getGraphics();
		stringView.calculateBounds(currentGC);
		lastLine.appendView(stringView);
		return true;
	}

	public boolean appendLaTeXStringToLastLine(String laTeXString) {
		if (view.isEmpty()) return appendLaTeXStringAsNewLine(laTeXString);
		
		TextLineAnimationView lastLine = view.getLastLineView();
		LaTeXStringAnimationView stringView = new LaTeXStringAnimationView(laTeXString);
		int x = lastLine.getNextX();
		int y = lastLine.getNextY();
		stringView.setPosition(new Point(x, y));

		stringView.createImage();
		lastLine.appendView(stringView);
		return true;
	}
	
	public boolean appendImageFileToLastLine(String imageFileName) {
		if (view.isEmpty()) return appendImageFileAsNewLine(imageFileName);
		
		TextLineAnimationView lastLine = view.getLastLineView();
		ImageFileAnimationView imageView = new ImageFileAnimationView(imageFileName);
		int x = lastLine.getNextX();
		int y = lastLine.getNextY();
		imageView.setPosition(new Point(x, y));

		imageView.loadImage();
		lastLine.appendView(imageView);
		return true;
	}
	
	public boolean appendAnimationViewToLastLine(AnimationView newView) {
		if (view.isEmpty()) return appendAnimationViewAsNewLine(newView);
		
		TextLineAnimationView lastLine = view.getLastLineView();
		int x = lastLine.getNextX();
		int y = lastLine.getNextY();
		newView.setPosition(new Point(x, y));

		lastLine.appendView(newView);
		return true;
	}
	
	public void clearContent() {
		view = new TextDocumentAnimationView();
		view.setPosition(new Point(0, 0));
	}
	
	public int getLastLineHeight() {
		TextLineAnimationView lastLine = view.getLastLineView();
		if (lastLine == null) return 0;
		else return lastLine.getHeight();
	}
	
	public int getLastNextLineY() {
		TextLineAnimationView lastLine = view.getLastLineView();
		if (lastLine == null) return 0;
		else return lastLine.getNextY();
	}
	
	@Override
	public int getWidth() {
		return view.getWidth();
	}
	
	@Override
	public int getHeight() {
		return view.getHeight();
	}
	
	@Override
	public void paint(Graphics gc) {
		if (!visible) return;
		view.paint(gc);
	}

}
