package com.deedm.legacy.graphicAnimation;

import java.awt.*;

/**
 * 算法演示视图基类。算法演示视图是指在屏幕上现实的一组图形或文字信息，通常它是算法所处理的数据的直观
 * 展示。算法演示视图基类 AnimationView 保存视图的位置、颜色及是否可见等基本信息，并定义了一些颜色常量
 * @author Administrator
 *
 */
public abstract class AnimationView implements PainterHelper {
	// 一些颜色常量
	public static final Color lightGreen = new Color(0, 128, 64);
	public static final Color midGreen = new Color(0, 128, 128);
	public static final Color darkGreen = new Color(0, 64, 64);
	public static final Color midRed = new Color(128, 0, 0);
	public static final Color darkRed = new Color(64, 0, 0);
	public static final Color midBlue = new Color(0, 0, 128);
	public static final Color darkBlue = new Color(0, 0, 96);
	public static final Color midYellow = new Color(128, 128, 0);
	public static final Color darkYellow = new Color(64, 64, 0);
	public static final Color midOrange = new Color(255, 128, 0);
	
	protected Point position;				// 视图基本位置，通常理解为整个视图的左上角位置
	protected Color color;					// 视图基本颜色
	protected boolean visible = true;		// 是否可见
	
	public AnimationView() { position = new Point(); }
	public AnimationView(Point pos) { position = new Point(pos); }
	public AnimationView(Point pos, Color color) { 
		position = new Point(pos);
		this.color = color;
	}
	
	/**
	 * 设置视图的颜色
	 */
	public void setColor(Color color) { this.color = color; }

	public void setPosition(Point pos) {
		if (position == null) position = new Point(pos);
		else {
			position.x = pos.x;	position.y = pos.y; 
		}
	}
	
	/**
	 * 将位置移动到 pos 
	 * @param pos 目标位置
	 */
	public void moveTo(Point pos) { 
		position.x = pos.x;	position.y = pos.y; 
	}
	/**
	 * 将位置移动到 (x, y) 
	 * @param (x, y) 目标位置
	 */
	public void moveTo(int x, int y) { 
		position.x = x;  position.y = y;
	}
	
	/**
	 * 将位置向上移动 steps 个像素（减少 y 坐标）
	 */
	public void moveUp(int steps) {
		position.y = position.y - steps;
	}
	/**
	 * 将位置向下移动 steps 个像素（增加 y 坐标）
	 */
	public void moveDown(int steps) {
		position.y = position.y + steps;
	}
	/**
	 * 将位置向左移动 steps 个像素（减少 x 坐标）
	 */
	public void moveLeft(int steps) {
		position.x = position.x - steps;
	}
	/**
	 * 将位置向右移动 steps 个像素（增加 x 坐标）
	 */
	public void moveRight(int steps) {
		position.x = position.x + steps;
	}

	/**
	 * 隐藏视图，不过需要在真正重绘视图时才会产生隐藏的效果
	 *
	 */
	public void hide() { visible = false; }

	/**
	 * 使视图可见，不过需要在真正重绘视图时才会真正使视图重新可见（如果以前隐藏了的话）
	 *
	 */
	public void show() { visible = true; }
	
	/**
	 * 判断视图是否可见
	 */
	public boolean isVisible() { return visible; }
	
	/**
	 * 返回视图的颜色
	 */
	public Color getColor() { return color; }
	
	/**
	 * 返回视图的位置
	 */
	public Point getPosition() { return new Point(position); }
	
	/**
	 * 返回视图的高度估算值
	 */
	public abstract int getHeight();
	
	/**
	 * 返回视图的宽度估算值
	 */
	public abstract int getWidth();

	/**
	 * 绘制视图，由真正的视图具体实现
	 */
	public abstract void paint(Graphics gc); 
}
