package com.github.nickid2018.chemistrylab.layer;

import org.lwjgl.glfw.*;
import com.github.nickid2018.chemistrylab.*;
import com.github.nickid2018.chemistrylab.layer.animation.*;
import com.github.nickid2018.chemistrylab.window.MainWindow;

import static org.lwjgl.opengl.GL11.*;

public class CloseBar extends Layer {

	public CloseBar() {
		super(200, MainWindow.nowHeight / 2 - 20, 221, MainWindow.nowHeight / 2 + 20);
	}

	@Override
	public void render() {
		// This block will be replaced by picture
//		new Color(150, 150, 150, 75).bind();
		glBegin(GL_QUADS);
		glVertex2f(200, MainWindow.nowHeight / 2 - 20);
		glVertex2f(221, MainWindow.nowHeight / 2 - 20);
		glVertex2f(221, MainWindow.nowHeight / 2 + 20);
		glVertex2f(200, MainWindow.nowHeight / 2 + 20);
		glEnd();
//		new Color(255, 255, 255, 128).bind();
		glBegin(GL_QUADS);
		glVertex2f(220, MainWindow.nowHeight / 2 - 19);
		glVertex2f(217, MainWindow.nowHeight / 2 - 19);
		glVertex2f(205, MainWindow.nowHeight / 2);
		glVertex2f(202, MainWindow.nowHeight / 2);
		glEnd();
		glBegin(GL_QUADS);
		glVertex2f(202, MainWindow.nowHeight / 2);
		glVertex2f(205, MainWindow.nowHeight / 2);
		glVertex2f(220, MainWindow.nowHeight / 2 + 19);
		glVertex2f(217, MainWindow.nowHeight / 2 + 19);
		glEnd();
	}

	@Override
	public void onContainerResized() {
		range.y0 = MainWindow.nowHeight / 2 - 20;
		range.y1 = MainWindow.nowHeight / 2 + 20;
	}

	@Override
	public void onMouseEvent(int button, int action, int mods) {
		if (action != GLFW.GLFW_PRESS || button != 0)
			return;
		LayerRender.popLayer(this);
		LayerRender.pushLayer(new SideBarClose());
	}
}
