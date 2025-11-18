package com.deedm.legacy.graphicAnimation.textAnimationView;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import com.deedm.legacy.graphicAnimation.AnimationView;
import com.deedm.legacy.util.Configuration;

/**
 * @author user
 * 这个视图在给定的位置将一个符合LaTeX语法的字符串转换成图片在图形区域中显示
 */
public class LaTeXStringAnimationView extends AnimationView {
	protected String value = null;
	protected BufferedImage image = null;
	
	public LaTeXStringAnimationView(String latexString) {
		super();
		value = latexString;
	}

	public LaTeXStringAnimationView(String latexString, Point pos) {
		super(pos);
		value = latexString;
	}

	public void createImage() {
		// image = LaTeXStringImageCreator.createImage(value, Configuration.laTeXImageBackground, Configuration.laTeXImageBackground, Configuration.laTeXImageFontSize);
		//暂时注释掉latex图片生成，以免编译错误
	}
	
	@Override
	public int getHeight() {
		if (image != null) return image.getHeight();
		return 0;
	}

	@Override
	public int getWidth() {
		if (image != null) return image.getWidth();
		return 0;
	}

	@Override
	public void paint(Graphics gc) {
		if (!visible) return;
		if (image == null) return;
		int y = position.y + 4;		// 我们理解的视图是整个图片的左上角的坐标，但gc.drawImage中的用的y坐标也是左上角的坐标。
		gc.drawImage(image, position.x, y, null);
	}
}
