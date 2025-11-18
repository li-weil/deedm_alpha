package com.deedm.legacy.guiManager;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.deedm.legacy.graphicAnimation.AnimationArea;

public class ImagedTextArea extends AnimationArea implements Scrollable {
	private static final long serialVersionUID = 1L;

	public ImagedTextArea(int width, int height) {
		super(width, height);
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
        Dimension d = super.getPreferredSize();
        if (d == null) d = new Dimension(400,400); 

		int width = animator.getWidth();
		int height = animator.getHeight();

		if (width != 0) d.width = Math.max(d.width, width);
        if (height != 0) d.height = Math.max(d.height, height+50);
        return d;
	}

	@Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        switch(orientation) {
        case SwingConstants.VERTICAL:
        	if (direction > 0) {
        		int gap = animator.getHeight() - (visibleRect.y + visibleRect.height);
         		if (gap > 0) return Math.min(gap, visibleRect.height);
        	}
        	return visibleRect.height;
        case SwingConstants.HORIZONTAL:
            return visibleRect.width;
        default:
            throw new IllegalArgumentException("Invalid orientation: " + orientation);
        }
    }

	@Override
    public boolean getScrollableTracksViewportHeight() {
        Container parent = SwingUtilities.getUnwrappedParent(this);
        if (parent instanceof JViewport) {
            return parent.getHeight() > getPreferredSize().height;
        }
        return false;
    }

	@Override
    public boolean getScrollableTracksViewportWidth() {
        Container parent = SwingUtilities.getUnwrappedParent(this);
        if (parent instanceof JViewport) {
            return parent.getWidth() > getPreferredSize().width;
        }
        return false;
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
       switch(orientation) {
        case SwingConstants.VERTICAL:
        	if (direction > 0) {
        		int gap = animator.getHeight() - (visibleRect.y + visibleRect.height);
        		if (gap > 0) return Math.min(gap, visibleRect.height);
        	}
            return visibleRect.height / 10;
        case SwingConstants.HORIZONTAL:
            return visibleRect.width / 10;
        default:
            throw new IllegalArgumentException("Invalid orientation: " + orientation);
        }
    }
}