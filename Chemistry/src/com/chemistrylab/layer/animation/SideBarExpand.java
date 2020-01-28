package com.chemistrylab.layer.animation;

import org.newdawn.slick.*;
import com.chemistrylab.layer.*;

import static org.lwjgl.opengl.GL11.*;
import static com.chemistrylab.ChemistryLab.*;

public class SideBarExpand extends Animation {

	public SideBarExpand() {
		super(10);
	}

	@Override
	public void render(int fp) {
		new Color(150,150,150,75).bind();
		glBegin(GL_QUADS);
			glVertex2f(0,0);
			glVertex2f(0,nowHeight);
			glVertex2f(fp*20+20,nowHeight);
			glVertex2f(fp*20+20,0);
		glEnd();
	}
	
	@Override
	public void onEnd() {
		LayerRender.addEndEvent(()->{
			LayerRender.pushLayer(new CloseBar());
			LayerRender.pushLayer(new SideBar());
		});
	}
}
