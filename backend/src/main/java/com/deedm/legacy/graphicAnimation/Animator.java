package com.deedm.legacy.graphicAnimation;

import java.awt.Graphics;

/**
 * 算法演示器的基类，维持该算法演示器绘制的内容是否可见，以及该算法演示器所使用的演示区域和动画驱动器
 * 等基本信息
 * @author Administrator
 *
 */
public abstract class Animator implements PainterHelper {
	protected boolean visible = true;						// 算法演示器绘制的内容是否可见
	protected AnimationArea area = null;					// 算法演示器使用的演示区域
	protected AnimationDriver animationDriver = null;		// 算法演示器使用的动画驱动器
	
	public Animator() { }
	public Animator(AnimationArea area) { 
		this.area = area; 
		area.setAnimator(this);
	}
	public Animator(AnimationDriver animationDriver) { 
		this.animationDriver = animationDriver; 
	}
	public Animator(AnimationArea area, AnimationDriver animationDriver) {
		this.area = area;
		this.animationDriver = animationDriver;
	}
	
	/**
	 * 返回所使用动画驱动器
	 */
	public AnimationDriver getAnimationDriver() {
		return animationDriver;
	}
	/**
	 * 设置所使用动画驱动器
	 */
	public void setAnimationDriver(AnimationDriver animationDriver) {
		this.animationDriver = animationDriver;
	}
	/**
	 * 返回所使用演示区域
	 */
	public AnimationArea getAnimatorArea() {
		return area;
	}
	/**
	 * 设置所使用的演示区域
	 * @param area
	 */
	public void setAnimationArea(AnimationArea area) {
		this.area = area;
	}

	/**
	 * 隐藏演示器所绘制的内容
	 */
	public void hide() { visible = false; }
	/**
	 * 使演示器所绘制的内容可见
	 */
	public void show() { visible = true; }

	/**
	 * 延迟 ms 毫秒
	 */
	public static void sleep(long ms) {
		if (ms <= 0) return;
		try {
			Thread.sleep(ms);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	/**
	 * 返回演示器所绘制的内容所需要的宽度和高度，注意这通常不是演示区域 area 给出的宽度和高度，而应该是演示器所管理的演示视图(AnimationView)对象所占用的宽度和高度
	 */
	public abstract int getWidth();
	/**
	 * 返回演示器所绘制的内容所需要的宽度和高度，注意这通常不是演示区域 area 给出的宽度和高度，而应该是演示器所管理的演示视图(AnimationView)对象所占用的宽度和高度
	 */
	public abstract int getHeight();

	/**
	 * 在演示区域上绘制演示器所需要绘制的内容，由具体的演示器实现
	 */
	public abstract void paint(Graphics gc);
	
	/**
	 * 如果设置了演示区域，则调用演示区域的 repaint() 方法重新绘制该区域
	 *
	 */
	public void update() {
		if (area != null) area.repaint();
	}
	
	
}
