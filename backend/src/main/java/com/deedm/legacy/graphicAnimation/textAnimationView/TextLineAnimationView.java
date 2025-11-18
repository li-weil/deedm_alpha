/**
 * 
 */
package com.deedm.legacy.graphicAnimation.textAnimationView;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import com.deedm.legacy.graphicAnimation.AnimationView;

/**
 * @author user
 * 一个 TextLineAnimationView代表在混合文本（包括字符串、图片、图形等的文本）中的一行。
 */
public class TextLineAnimationView extends AnimationView {
	protected List<AnimationView> viewList = null;
	protected int spaceBetweenView = 0;
	
	public TextLineAnimationView() {
		viewList = new ArrayList<AnimationView>();
	}
	
	/**
	 * 往这一行的最后添加一个视图，视图的位置原则上由给定的参数view自己确定。最好是保证这一行的所有视图的y坐标都相同，且有合适的x坐标位置，可通过下面的getNextX()和getNextY()来设置
	 * 正要添加到这一行的视图view的坐标位置。
	 */
	public boolean appendView(AnimationView view) {
		boolean success = viewList.add(view);
		if (success == false) return false;
		
		if (viewList.size() == 1) {
			position.x = view.getPosition().x;
			position.y = view.getPosition().y;
		} else {
			// 整个行视图的x, y坐标是这一行所有视图的最小的那个x, y。注意：视图的坐标理解为整个视图的左上角位置。
			int x = view.getPosition().x;
			if (x < position.x) position.x = x;
			int y = view.getPosition().y;
			if (y < position.y) position.y = y;
		}
		return true; 
	}
	
	public void setSpaceBetweenView(int space) {
		spaceBetweenView = space;
	}
	
	public int getNextX() {
		int x = position.x;
		if (viewList.size() > 0) {
			AnimationView lastView = viewList.get(viewList.size()-1);
			x = lastView.getPosition().x;
			x += lastView.getWidth();
		}
		return x+spaceBetweenView;
	}
	
	public int getNextY() {
		int y = position.y;
		if (viewList.size() > 0) {
			AnimationView lastView = viewList.get(viewList.size()-1);
			y = lastView.getPosition().y;
		}
		return y;
	}

	@Override
	public int getHeight() {
		int minY = -1;
		int maxY = -1;
		for (int i = 0; i < viewList.size(); i++) {
			int y = viewList.get(i).getPosition().y;
			if (minY == -1 || y < minY) minY = y;
			
			y = y + viewList.get(i).getHeight();   // 注意视图位置理解为视图左上角的屏幕坐标！！
			if (maxY == -1 || y > maxY) maxY = y;
		}
		return maxY - minY;
	}

	@Override
	public int getWidth() {
		if (viewList.size() == 0) return 0;

		int size = viewList.size();
		int firstX = viewList.get(0).getPosition().x;
		int lastX = viewList.get(size-1).getPosition().x;
		int lastWidth = viewList.get(size-1).getWidth();
		return lastX - firstX + lastWidth;
	}
	
	@Override
	public void paint(Graphics gc) {
		if (viewList == null) return;
		
		for (int i = 0; i < viewList.size(); i++) viewList.get(i).paint(gc);
	}
}
