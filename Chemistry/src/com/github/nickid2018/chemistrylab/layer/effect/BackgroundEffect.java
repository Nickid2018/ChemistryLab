package com.github.nickid2018.chemistrylab.layer.effect;

import org.newdawn.slick.*;

import com.github.nickid2018.chemistrylab.layer.component.*;
import com.github.nickid2018.chemistrylab.render.*;

//import static org.lwjgl.opengl.GL11.*;

public class BackgroundEffect implements Effect {

	private Color bg;
	private FastQuad quad;

	public BackgroundEffect(Color bg, Component c) {
		this.bg = bg;
		quad = new FastQuad(c.getRange().x0, c.getRange().y0, c.getRange().x1, c.getRange().y1, bg);
	}

	public Color getBackground() {
		return bg;
	}

	public void setBackground(Color bg) {
		this.bg = bg;
	}

	@Override
	public void effect(Component c) {
//		bg.bind();
		// glBegin(GL_QUADS);
		// glVertex2f(c.getRange().x0, c.getRange().y0);
		// glVertex2f(c.getRange().x1, c.getRange().y0);
		// glVertex2f(c.getRange().x1, c.getRange().y1);
		// glVertex2f(c.getRange().x0, c.getRange().y1);
		// glEnd();
		quad.render();
	}

}
