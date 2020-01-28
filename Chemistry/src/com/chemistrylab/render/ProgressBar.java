package com.chemistrylab.render;

import org.newdawn.slick.*;

import static org.lwjgl.opengl.GL11.*;

public final class ProgressBar {

	private long max = 0;
	private long now = 0;
	private long mask = 0;
	private int height;
	private Color edgecolor = Color.darkGray;
	private Color fillcolor = Color.green;
	private Color maskcolor = Color.red;
	private boolean maskenabled = false;

	public ProgressBar(long max, int width) {
		this.max = max;
		this.height = width;
	}

	public long getMax() {
		return max;
	}

	public void setMax(long max) {
		this.max = max;
	}

	public long getNow() {
		return now;
	}

	public void setNow(long now) {
		this.now = now;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int width) {
		this.height = width;
	}

	public Color getEdgeColor() {
		return edgecolor;
	}

	public void setEdgeColor(Color edgecolor) {
		this.edgecolor = edgecolor;
	}

	public Color getFillColor() {
		return fillcolor;
	}

	public void setFillColor(Color fillcolor) {
		this.fillcolor = fillcolor;
	}

	public Color getMaskColor() {
		return maskcolor;
	}

	public void setMaskColor(Color maskcolor) {
		this.maskcolor = maskcolor;
	}

	public boolean isMaskEnabled() {
		return maskenabled;
	}

	public void setMaskEnabled(boolean maskenabled) {
		this.maskenabled = maskenabled;
	}

	public long getMask() {
		return mask;
	}

	public void setMask(long mask) {
		this.mask = mask;
	}

	public void render(float x, float y, float f) {
		glLineWidth(1);
		edgecolor.bind();
		glBegin(GL_LINE_LOOP);
			glVertex2f(x, y);
			glVertex2f(x + f, y);
			glVertex2f(x + f, y + height);
			glVertex2f(x, y + height);
		glEnd();
		fillcolor.bind();
		glBegin(GL_QUADS);
			glVertex2f(x, y);
			glVertex2f(x + (float) f * (float) now / (float) max, y);
			glVertex2f(x + (float) f * (float) now / (float) max, y + height - 1);
			glVertex2f(x, y + height - 1);
		glEnd();
		if (maskenabled) {
			maskcolor.bind();
			glBegin(GL_LINE_STRIP);
				glVertex2f(x + (float) f * (float) mask / (float) max, y);
				glVertex2f(x + (float) f * (float) mask / (float) max, y + height - 1);
			glEnd();
		}
	}
}