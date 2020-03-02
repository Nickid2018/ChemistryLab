package com.github.nickid2018.chemistrylab.window;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import com.github.nickid2018.chemistrylab.layer.LayerRender;

public class RenderInit {
	public static void init() {
		LayerRender.logger.info("Initializing OpenGL...");
		GL.createCapabilities();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, Window.nowWidth, Window.nowHeight, 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glViewport(0, 0, (int) Window.nowWidth, (int) Window.nowHeight);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		LayerRender.logger.info("OpenGL initialized.Version:" + GL11.glGetString(GL11.GL_VERSION));
	}
}
