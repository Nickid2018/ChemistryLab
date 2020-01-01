package com.chemistrylab.layer.effect;

import org.newdawn.slick.*;
import com.chemistrylab.layer.component.*;

import static org.lwjgl.opengl.GL11.*;

public class BackgroundEffect implements Effect {

	private Color bg;

	public BackgroundEffect(Color bg) {
		this.bg = bg;
	}

	public Color getBackground() {
		return bg;
	}

	public void setBackground(Color bg) {
		this.bg = bg;
	}

	@Override
	public void effect(Component c) {
		bg.bind();
		glBegin(GL_QUADS);
			glVertex2f(c.getRange().x0, c.getRange().y0);
			glVertex2f(c.getRange().x1, c.getRange().y0);
			glVertex2f(c.getRange().x1, c.getRange().y1);
			glVertex2f(c.getRange().x0, c.getRange().y1);
		glEnd();
	}

}
