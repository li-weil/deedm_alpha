/**
 * 
 */
package com.deedm.legacy.graphicAnimation.textAnimationView;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

import com.deedm.legacy.graphicAnimation.AnimationView;
import com.deedm.legacy.util.Configuration;

/**
 * @author user
 * 这个视图在给定的位置将一个字符串在图形区域中显示
 */
public class PlainStringAnimationView extends AnimationView {
	protected String value = null;
	protected Rectangle2D bounds = null; 

	public PlainStringAnimationView(String plainString) {
		super();
		this.value = plainString;
	}

	public PlainStringAnimationView(String plainString, Point pos) {
		super(pos);
		this.value = plainString;
	}

	@Override
	public int getHeight() {
		if (bounds != null) return (int)bounds.getHeight();
		else return Configuration.plainStringFont.getSize();
	}

	@Override
	public int getWidth() {
		if (bounds != null) return (int)bounds.getWidth();
		else return Configuration.plainStringFont.getSize() * this.value.length();
	}

	public void calculateBounds(Graphics gc) {
		Graphics2D g2 = (Graphics2D)gc;
		if (g2 != null) {
			FontRenderContext context = g2.getFontRenderContext();
			bounds = Configuration.plainStringFont.getStringBounds(value, context);
		}
	}
	
	@Override
	public void paint(Graphics gc) {
		if (!visible) return;
		if (Configuration.plainStringForeground != null) gc.setColor(Configuration.plainStringForeground);
		gc.setFont(Configuration.plainStringFont);
		int y = position.y + Configuration.plainStringFont.getSize();
		gc.drawString(value, position.x, y);
	}
}
