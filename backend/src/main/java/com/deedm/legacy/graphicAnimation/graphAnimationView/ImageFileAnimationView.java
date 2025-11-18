/**
 * 
 */
package com.deedm.legacy.graphicAnimation.graphAnimationView;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.deedm.legacy.graphicAnimation.AnimationView;

/**
 * @author Zhou Xiaocong
 * @since 2020/10/12
 * 从图像文件中装入一个图像作为视图要显示的内容
 *
 */
public class ImageFileAnimationView extends AnimationView {
	protected BufferedImage image = null;
	protected String imageFileName = null;
	
	public ImageFileAnimationView(String imageFileName) {
		this.imageFileName = imageFileName;
	}

	public boolean loadImage() {
		File imageFile = new File(imageFileName);
		try {
			image = ImageIO.read(imageFile);
		} catch (IOException e) {
			e.printStackTrace();
			image = null;
			return false;
		}
		return true;
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
		gc.drawImage(image, position.x, position.y, null);
	}
}
