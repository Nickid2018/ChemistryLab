package com.chemistrylab.layer;

import org.newdawn.slick.*;
import com.chemistrylab.render.*;

import static org.lwjgl.opengl.GL11.*;
import static com.chemistrylab.ChemistryLab.*;

public final class PleaseWaitLayer extends Layer {
	
	private String info;
	private final Runnable whattodo;
	
	private static final AnimationTexture dealing=(AnimationTexture) getTextures().get("texture.guianimation.dealing");

	public PleaseWaitLayer(String info,Runnable wtd) {
		super(0, 0, WIDTH, HEIGHT);
		this.info=info;
		whattodo=wtd;
		CommonRender.loadFontUNI(info);
		dealing.startToBind();
	}
	
	public PleaseWaitLayer start(){
		new Thread(()->{
			whattodo.run();
			LayerRender.addEndEvent(()->LayerRender.popLayer(PleaseWaitLayer.this));
			dealing.endToBind();
		},info).start();
		return this;
	}

	@Override
	public void render() {
		new Color(150, 150, 150, 75).bind();
		glBegin(GL_QUADS);
			glVertex2f(0, 0);
			glVertex2f(0,HEIGHT);
			glVertex2f(WIDTH,HEIGHT);
			glVertex2f(WIDTH,0);
		glEnd();
		Color.white.bind();
		glBegin(GL_QUADS);
			glVertex2f(200, 200);
			glVertex2f(850,200);
			glVertex2f(850,400);
			glVertex2f(200,400);
		glEnd();
		CommonRender.drawTexture(dealing, 276, 276, 324, 324, 0, 0, 1, 1);
		CommonRender.drawFontUNI(info, 330, 276, Color.black);
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

}
