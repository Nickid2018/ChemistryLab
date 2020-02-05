package com.chemistrylab.layer.animation;

import org.newdawn.slick.*;
import com.chemistrylab.layer.*;

import static org.lwjgl.opengl.GL11.*;
import static com.chemistrylab.ChemistryLab.*;

public class SideBarClose extends Animation {

	private boolean pop;

	public SideBarClose() {
		this(false);
	}

	public SideBarClose(boolean pop) {
		super(10);
		this.pop = pop;
		LayerRender.popLayer(SideBar.class);
	}

	@Override
	public void render(int fp) {
		new Color(150, 150, 150, 75).bind();
		glBegin(GL_QUADS);
			glVertex2f(0, 0);
			glVertex2f(0, nowHeight);
			glVertex2f(-fp * 20 + 200, nowHeight);
			glVertex2f(-fp * 20 + 200, 0);
		glEnd();
	}

	@Override
	public void onEnd() {
		if (pop)
			return;
		LayerRender.pushLayer(new ExpandBar());
	}
}