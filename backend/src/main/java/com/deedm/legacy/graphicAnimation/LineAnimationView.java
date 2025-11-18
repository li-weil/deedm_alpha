package com.deedm.legacy.graphicAnimation;

import java.awt.*;

/**
 * 直线视图，即在屏幕上的两点之间画一条直线
 *
 */
public class LineAnimationView extends AnimationView {
	// 直线视图的一个端点，另一个端点由父类 AnimationView 的 position 保存
	protected Point position2 = null;
	
	public LineAnimationView(Point position, Point position2, Color color) {
		super(position, color);
		this.position2 = position2;
	}
	
	/**
	 * 绘制直线视图，即以当前颜色，在两个坐标点之间绘制一条直线，目前没有考虑所谓的线型等更多绘制手段
	 */
	public void paint(Graphics gc) {
		if (!visible) return;		// 如果不可见则不绘制
		
		gc.setColor(color);
		gc.drawLine(position.x, position.y, position2.x, position2.y);
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}
}
