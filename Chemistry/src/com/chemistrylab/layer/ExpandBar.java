package com.chemistrylab.layer;

import org.lwjgl.input.*;
import org.newdawn.slick.*;

import com.chemistrylab.layer.animation.*;

import static com.chemistrylab.ChemistryLab.*;
import static org.lwjgl.opengl.GL11.*;

public class ExpandBar extends Layer {

	public ExpandBar() {
		super(0, 	HEIGHT/2-20, 21, HEIGHT/2+20);
	}

	@Override
	public void render() {
		new Color(150,150,150,75).bind();
		glBegin(GL_QUADS);
			glVertex2f(0, HEIGHT/2-20);
			glVertex2f(21,HEIGHT/2-20);
			glVertex2f(21,HEIGHT/2+20);
			glVertex2f(0,HEIGHT/2+20);
		glEnd();
		new Color(255,255,255,128).bind();
		glBegin(GL_QUADS);
			glVertex2f(1,HEIGHT/2-19);
			glVertex2f(4,HEIGHT/2-19);
			glVertex2f(19,HEIGHT/2);
			glVertex2f(16,HEIGHT/2);
		glEnd();
		glBegin(GL_QUADS);
			glVertex2f(16,HEIGHT/2);
			glVertex2f(19,HEIGHT/2);
			glVertex2f(4,HEIGHT/2+19);
			glVertex2f(1,HEIGHT/2+19);
		glEnd();
	}
	
	@Override
	public void onMouseEvent() {
		if(!Mouse.isButtonDown(0))return;
		LayerRender.popLayer(this);
		LayerRender.pushLayer(new SideBarExpand());
	}

}
