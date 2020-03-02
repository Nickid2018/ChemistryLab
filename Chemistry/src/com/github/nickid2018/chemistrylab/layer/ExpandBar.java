package com.github.nickid2018.chemistrylab.layer;

import org.lwjgl.glfw.*;
import org.newdawn.slick.*;

import com.github.nickid2018.chemistrylab.layer.animation.*;
import com.github.nickid2018.chemistrylab.window.Window;

import static com.github.nickid2018.chemistrylab.ChemistryLab.*;
import static org.lwjgl.opengl.GL11.*;

public class ExpandBar extends Layer {

	public ExpandBar() {
		super(0,Window. nowHeight / 2 - 20, 21, Window.nowHeight / 2 + 20);
	}

	@Override
	public void render() {
		// This block will be replaced by picture
		new Color(150, 150, 150, 75).bind();
		glBegin(GL_QUADS);
		glVertex2f(0, Window.nowHeight / 2 - 20);
		glVertex2f(21, Window.nowHeight / 2 - 20);
		glVertex2f(21, Window.nowHeight / 2 + 20);
		glVertex2f(0, Window.nowHeight / 2 + 20);
		glEnd();
		new Color(255, 255, 255, 128).bind();
		glBegin(GL_QUADS);
		glVertex2f(1, Window.nowHeight / 2 - 19);
		glVertex2f(4, Window.nowHeight / 2 - 19);
		glVertex2f(19, Window.nowHeight / 2);
		glVertex2f(16, Window.nowHeight / 2);
		glEnd();
		glBegin(GL_QUADS);
		glVertex2f(16, Window.nowHeight / 2);
		glVertex2f(19, Window.nowHeight / 2);
		glVertex2f(4, Window.nowHeight / 2 + 19);
		glVertex2f(1, Window.nowHeight / 2 + 19);
		glEnd();
	}

	@Override
	public void onContainerResized() {
		range.y0 = Window.nowHeight / 2 - 20;
		range.y1 = Window.nowHeight / 2 + 20;
	}

	@Override
	public void onMouseEvent(int button, int action, int mods) {
		if (action != GLFW.GLFW_PRESS || button != 0)
			return;
		LayerRender.popLayer(this);
		LayerRender.pushLayer(new SideBarExpand());
	}

}
