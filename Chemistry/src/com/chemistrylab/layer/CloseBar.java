package com.chemistrylab.layer;

import org.lwjgl.input.*;
import org.newdawn.slick.*;
import com.chemistrylab.layer.animation.*;

import static org.lwjgl.opengl.GL11.*;
import static com.chemistrylab.ChemistryLab.*;

public class CloseBar extends Layer {

	public CloseBar() {
		super(200, nowHeight / 2 - 20, 221, nowHeight / 2 + 20);
	}

	@Override
	public void render() {
		new Color(150, 150, 150, 75).bind();
		glBegin(GL_QUADS);
			glVertex2f(200, nowHeight / 2 - 20);
			glVertex2f(221, nowHeight / 2 - 20);
			glVertex2f(221, nowHeight / 2 + 20);
			glVertex2f(200, nowHeight / 2 + 20);
		glEnd();
		new Color(255, 255, 255, 128).bind();
		glBegin(GL_QUADS);
			glVertex2f(220, nowHeight / 2 - 19);
			glVertex2f(217, nowHeight / 2 - 19);
			glVertex2f(205, nowHeight / 2);
			glVertex2f(202, nowHeight / 2);
		glEnd();
		glBegin(GL_QUADS);
			glVertex2f(202, nowHeight / 2);
			glVertex2f(205, nowHeight / 2);
			glVertex2f(220, nowHeight / 2 + 19);
			glVertex2f(217, nowHeight / 2 + 19);
		glEnd();
	}

	@Override
	public void onContainerResized() {
		range.y0 = nowHeight / 2 - 20;
		range.y1 = nowHeight / 2 + 20;
	}

	@Override
	public void onMouseEvent() {
		if (!Mouse.isButtonDown(0))
			return;
		LayerRender.popLayer(this);
		LayerRender.pushLayer(new SideBarClose());
	}
}
