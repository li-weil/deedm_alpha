package com.deedm.legacy.graphicAnimation;

import java.util.*;
import java.awt.*;

/**
 * 动画驱动器类，用于同步运行多个动画
 * @author 周晓聪
 * @since 2007/7/15
 *
 */
public class AnimationDriver {
	protected ArrayList<Animation> animations = new ArrayList<Animation>(2);		// 动画
	protected int delay = 2;			// 展示动画时的每一步延迟的缺省值，即 2 毫秒
	
	/**
	 * 增加要运行的动画
	 */
	public void add(Animation animation) {
		animations.add(animation);
	}
	/**
	 * 删除某个动画
	 */
	public void remove(Animation animation) {
		animations.remove(animation);
	}
	
	/**
	 * 删除属于演示器 animator 的所有动画
	 */
	public void remove(Animator animator) {
		ArrayList<Animation> temp = new ArrayList<Animation>(2);
		for (int i = 0; i < animations.size(); i++) {
			Animation animation = animations.get(i);
			if (animation.getAnimator() == animator) temp.add(animation);
		}
		animations.removeAll(temp);
	}

	/** 
	 * 清除动画驱动器中的所有动画
	 */
	public void clear() {
		animations.clear();
	}
	
	/**
	 * 设置运行动画时的每一步的延迟毫秒数
	 */
	public void setDelay(int delay) { this.delay = delay; }  

	/**
	 * 返回运行动画时的每一步的延迟毫秒数
	 */
	public int getDelay() { return delay; }
	
	/**
	 * 以轮转的方式运行动画驱动器中的所有动画
	 *
	 */
	public void run() {
		
		boolean done = false;		// 用于判断是否所有的动画都已经运行完毕
		int index = 0;				// 用于扫描动画路径
		while (!done) {
			done = true;
			// 扫描动画驱动器中的所有动画
			for (int i = 0; i < animations.size(); i++) {
				// 获得该动画的有关信息
				Animation animation = animations.get(i);
				AnimationPath path = animation.getPath();
				AnimationView viewer = animation.getViewer();
				Animator animator = animation.getAnimator();

				if (index < path.size()) {
					// 当前动画还有动画路径没有完成
					done = false;
					Point point = path.get(index);
					// 将该动画的视图移到动画路径指定的位置
					viewer.moveTo(point);
					// 使得该动画的演示器刷新屏幕
					animator.update();
				}
			}
			index = index + 1;
			// 所有动画的当前步对运行完毕之后，延迟 delay 毫秒，然后继续运行下一步
			Animator.sleep(delay);
		}
	}
}
