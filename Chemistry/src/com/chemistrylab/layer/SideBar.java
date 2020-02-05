package com.chemistrylab.layer;

import org.lwjgl.input.*;
import org.newdawn.slick.*;
import com.chemistrylab.util.*;
import com.chemistrylab.layer.animation.*;
import com.chemistrylab.layer.component.*;

import static org.lwjgl.opengl.GL11.*;
import static com.chemistrylab.ChemistryLab.*;

public class SideBar extends Layer {

	public SideBar() {
		super(0, 0, 200, nowHeight);
		addComponent(new TextComponent(12, 0, 200, 64, this, I18N.getString("sidebar.language.settings"), () -> {
			if (Mouse.isButtonDown(0)) {
				LayerRender.pushLayer(new I18NLayer());
				LayerRender.popLayer(CloseBar.class);
				LayerRender.pushLayer(new SideBarClose(true));
			}
		}, 32, Color.white, true));
		addComponent(new TextComponent(12, 66, 200, 130, this, I18N.getString("sidebar.log.clear"), () -> {
			if (Mouse.isButtonDown(0))
				clearLog();
		}, 32, Color.white, true));
		addComponent(new TextComponent(12, 132, 200, 196, this, I18N.getString("sidebar.reaction.add"), () -> {
			if (Mouse.isButtonDown(0)) {
				LayerRender.pushLayer(new AddReactionLayer());
				LayerRender.popLayer(CloseBar.class);
				LayerRender.pushLayer(new SideBarClose(true));
			}
		}, 32, Color.white, true));
	}

	@Override
	public void render() {
		new Color(150, 150, 150, 75).bind();
		glBegin(GL_QUADS);
			glVertex2f(0, 0);
			glVertex2f(0, nowHeight);
			glVertex2f(200, nowHeight);
			glVertex2f(200, 0);
		glEnd();
		new Color(0, 200, 0, 150).bind();
		glBegin(GL_QUADS);
			glVertex2f(0, 0);
			glVertex2f(0, 64);
			glVertex2f(10, 64);
			glVertex2f(10, 0);
		glEnd();
		glBegin(GL_QUADS);
			glVertex2f(0, 66);
			glVertex2f(0, 130);
			glVertex2f(10, 130);
			glVertex2f(10, 66);
		glEnd();
		glBegin(GL_QUADS);
			glVertex2f(0, 132);
			glVertex2f(0, 196);
			glVertex2f(10, 196);
			glVertex2f(10, 132);
		glEnd();
	}

	@Override
	public void onContainerResized() {
		range.y1 = nowHeight;
	}

	@Override
	public boolean useComponent() {
		return true;
	}
}
