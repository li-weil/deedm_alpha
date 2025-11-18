package com.deedm.legacy.graphicAnimation;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

/**
 * 演示区域，用于演示器在该区域绘制演示场景
 * @author 周晓聪
 * @since 2007/7/16
 */
public class AnimationArea extends JComponent {
	private static final long serialVersionUID = 1L;

	protected Animator animator = null;		// 使用演示区域的演示器
	protected int width;						// 演示区域的最佳宽度
	protected int height;						// 演示区域的最佳高度
	
	public AnimationArea(int width, int height) {
		this.width = width;  this.height = height;
	}
	public void setAnimator(Animator animator) {
		this.animator = animator;
	}
	public Animator getAnimator() {
		return animator;
	}
	
	public void setPreferredSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}
	
	/**
	 * 构建的绘制方法，调用使用演示区域的演示器的 paint() 方法完成绘制
	 */
	@Override
	protected void paintComponent(Graphics gc) {
		if (animator != null) animator.paint(gc);
	}
}
