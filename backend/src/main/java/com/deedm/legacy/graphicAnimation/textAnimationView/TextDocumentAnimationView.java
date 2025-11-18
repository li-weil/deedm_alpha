/**
 * 
 */
package com.deedm.legacy.graphicAnimation.textAnimationView;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import com.deedm.legacy.graphicAnimation.AnimationView;

/**
 * @author Zhou Xiaocong
 * @Since 2020/09/28
 *
 * 一个TextDocumentAnimationView代表一个混合文档，它由多个TextLineAnimationView构成。
 */
public class TextDocumentAnimationView extends AnimationView {
	List<TextLineAnimationView> lineList = null;
	int spaceBetweenLine = 10;
	
	public TextDocumentAnimationView() {
		lineList = new ArrayList<TextLineAnimationView>();
	}
	
	/**
	 * 往文档的最后添加一行，视图的位置原则上由给定的参数view自己确定。最好是保证这一行的所有视图的y坐标都相同，且有合适的x坐标位置，可通过下面的getLastLineX()和getLastY()来获得
	 * 当前文档中已有的最后一行的x坐标和y坐标位置，根据这些位置，以及新的想加入一行本身的高度和所设置的行间距可计算新的这一行的起始x坐标和y坐标位置。注意，整个视图的位置被理解为左上角的坐标位置。
	 */
	public boolean appendView(TextLineAnimationView view) {
		boolean success = lineList.add(view);
		if (success == false) return false;
		
		if (lineList.size() == 1) {
			position.x = view.getPosition().x;
			position.y = view.getPosition().y;
		} else {
			// 整个行视图的x, y坐标是该文档中所有行视图的最小的那个x, y。注意：视图的坐标理解为整个视图的左上角位置。
			int x = view.getPosition().x;
			if (x < position.x) position.x = x;
			int y = view.getPosition().y;
			if (y < position.y) position.y = y;
		}
		return true; 
	}
	
	public boolean isEmpty() {
		return lineList.size() == 0;
	}
	
	public int getLineNumber() {
		return lineList.size();
	}
	
	public void setSpaceBetweenLine(int space) {
		spaceBetweenLine = space;
	}
	
	public int getSpaceBetweenLine() {
		return spaceBetweenLine;
	}
	
	public TextLineAnimationView getLastLineView() {
		if (isEmpty()) return null;
		return lineList.get(lineList.size()-1);
	}
	
	public int getLastLineHeight() {
		if (isEmpty()) return 0;
		return lineList.get(lineList.size()-1).getHeight();
	}
	
	public int getLastLineX() {
		int x = position.x;
		if (lineList.size() > 0) {
			AnimationView lastView = lineList.get(lineList.size()-1);
			x = lastView.getPosition().x;
		}
		return x;
	}
	
	public int getLastLineY() {
		int y = position.y;
		if (lineList.size() > 0) {
			AnimationView lastView = lineList.get(lineList.size()-1);
			y = lastView.getPosition().y;
		}
		return y;
	}
	
	@Override
	public int getHeight() {
		if (lineList.size() == 0) return 0;
		int firstY = lineList.get(0).getPosition().y;
		int lastHeight = lineList.get(lineList.size()-1).getHeight();
		int lastY = lineList.get(lineList.size()-1).getPosition().y;
		
		return ((lastY + lastHeight) - firstY);
	}

	@Override
	public int getWidth() {
		int minX = -1;
		int maxX = -1;
		for (int i = 0; i < lineList.size(); i++) {
			int x = lineList.get(i).getPosition().x;
			if (minX == -1 || x < minX) minX = x;
			
			x = x + lineList.get(i).getWidth();   // 注意视图位置理解为视图左下角的屏幕坐标！！
			if (maxX == -1 || x > maxX) maxX = x;
		}
		return maxX - minX;
	}

	@Override
	public void paint(Graphics gc) {
		for (int i = 0; i < lineList.size(); i++) lineList.get(i).paint(gc);
	}
}
