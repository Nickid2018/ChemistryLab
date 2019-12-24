package com.chemistrylab.layer.effect;

import org.newdawn.slick.*;
import com.chemistrylab.layer.*;
import com.chemistrylab.layer.component.*;

import static org.lwjgl.opengl.GL11.*;

public class LineBorderEffect implements Effect{
	
	private int width;
	private Color col;
	
	public LineBorderEffect(int width,Color col) {
		this.width=width;
		this.col=col;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public Color getColor() {
		return col;
	}

	public void setColor(Color col) {
		this.col = col;
	}

	@Override
	public void effect(Component c) {
		Range r=c.getRange();
		col.bind();
		glLineWidth(width);
		glBegin(GL_LINE_LOOP);
			glVertex2f(r.x0,r.y0);
			glVertex2f(r.x1,r.y0);
			glVertex2f(r.x1,r.y1);
			glVertex2f(r.x0,r.y1);
		glEnd();
		glLineWidth(1);
	}

}
