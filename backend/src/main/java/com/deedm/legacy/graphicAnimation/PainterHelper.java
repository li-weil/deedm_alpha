package com.deedm.legacy.graphicAnimation;

import java.awt.Graphics;

/**
 * 算法演示绘制器的帮助者，所谓帮助者就是可将自己绘制的对象，目前我们将算法演示视图和算法演示器都看作
 * 算法演示绘制器的帮助者
 *
 */
public interface PainterHelper {
	public void paint(Graphics gc);
}
