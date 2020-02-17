package com.chemistrylab.layer;

import org.newdawn.slick.*;
import com.chemistrylab.layer.animation.*;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.glfw.GLFW;

import static com.chemistrylab.ChemistryLab.*;

public class ExpandBar extends Layer {

	public ExpandBar() {
		super(0, nowHeight / 2 - 20, 21, nowHeight / 2 + 20);
	}

	@Override
	public void render() {
		new Color(150, 150, 150, 75).bind();
		glBegin(GL_QUADS);
			glVertex2f(0, nowHeight / 2 - 20);
			glVertex2f(21, nowHeight / 2 - 20);
			glVertex2f(21, nowHeight / 2 + 20);
			glVertex2f(0, nowHeight / 2 + 20);
		glEnd();
		new Color(255, 255, 255, 128).bind();
		glBegin(GL_QUADS);
			glVertex2f(1, nowHeight / 2 - 19);
			glVertex2f(4, nowHeight / 2 - 19);
			glVertex2f(19, nowHeight / 2);
			glVertex2f(16, nowHeight / 2);
		glEnd();
		glBegin(GL_QUADS);
			glVertex2f(16, nowHeight / 2);
			glVertex2f(19, nowHeight / 2);
			glVertex2f(4, nowHeight / 2 + 19);
			glVertex2f(1, nowHeight / 2 + 19);
		glEnd();
	}

	@Override
	public void onContainerResized() {
		range.y0 = nowHeight / 2 - 20;
		range.y1 = nowHeight / 2 + 20;
	}

	@Override
	public void onMouseEvent(int button, int action, int mods) {
		if(action != GLFW.GLFW_PRESS)
			return;
		if (button != 0)
			return;
		LayerRender.popLayer(this);
		LayerRender.pushLayer(new SideBarExpand());
	}

}
