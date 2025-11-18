package com.deedm.legacy.graphicAnimation;

/**
 * 动画类，动画是指视图位置的连续变化，因此动画类保存其视图和动画路径信息，以及创建该动画的演示器
 * @author 周晓聪
 * @since 2007/7/15
 */
public class Animation {
	protected Animator animator = null;			// 创建动画的演示器
	protected AnimationView viewer = null;			// 动画的视图
	protected AnimationPath path = null;			// 动画路径
	
	public Animation(Animator animator, AnimationView viewer) {
		this.animator = animator;
		this.viewer = viewer;
	}
	public Animation(Animator animator) {
		this.animator = animator;
	}
	
	/**
	 * 返回创建动画的演示器
	 */
	public Animator getAnimator() {
		return animator;
	}

	/**
	 * 设置创建动画的演示器
	 */
	public void setAnimator(Animator animator) {
		this.animator = animator;
	}

	/**
	 * 返回动画的动画路径
	 */
	public AnimationPath getPath() {
		return path;
	}

	/**
	 * 设置动画的动画路径，动画路径实际上就是视图连续变化的一系列位置坐标
	 */
	public void setPath(AnimationPath path) {
		this.path = path;
	}

	/**
	 * 返回动画的视图
	 */
	public AnimationView getViewer() {
		return viewer;
	}

	/**
	 * 设置动画的视图
	 */
	public void setViewer(AnimationView viewer) {
		this.viewer = viewer;
	}
}	
