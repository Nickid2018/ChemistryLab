package com.chemistrylab.layer;

import com.chemistrylab.*;
import org.newdawn.slick.*;
import com.chemistrylab.render.*;
import com.chemistrylab.eventbus.*;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.input.Mouse;

import static com.chemistrylab.ChemistryLab.*;

public final class PleaseWaitLayer extends Layer {

	private String info;
	private String error;
	private final Runnable whattodo;

	private static final AnimationTexture dealing = (AnimationTexture) getTextures()
			.get("texture.guianimation.dealing");

	public PleaseWaitLayer(String info, Runnable wtd) {
		super(0, 0, WIDTH, HEIGHT);
		this.info = info;
		whattodo = wtd;
		CommonRender.loadFontUNI(info);
		dealing.startToBind();
	}

	public PleaseWaitLayer start() {
		new Thread(() -> {
			try {
				whattodo.run();
			} catch (Throwable e) {
				Event error = ChemistryLab.THREAD_FATAL.clone();
				error.putExtra(0, e);
				EventBus.postEvent(error);
			}
			if (error == null)
				LayerRender.addEndEvent(() -> LayerRender.popLayer(PleaseWaitLayer.this));
			dealing.endToBind();
		}, info).start();
		return this;
	}

	@Override
	public void render() {
		new Color(150, 150, 150, 75).bind();
		glBegin(GL_QUADS);
		glVertex2f(0, 0);
		glVertex2f(0, HEIGHT);
		glVertex2f(WIDTH, HEIGHT);
		glVertex2f(WIDTH, 0);
		glEnd();
		Color.white.bind();
		glBegin(GL_QUADS);
		glVertex2f(200, 200);
		glVertex2f(850, 200);
		glVertex2f(850, 400);
		glVertex2f(200, 400);
		glEnd();
		if (error != null) {
			CommonRender.drawFontUNI(error, 330, 276, Color.red);
		} else {
			CommonRender.drawTexture(dealing, 276, 276, 324, 324, 0, 0, 1, 1);
			CommonRender.drawFontUNI(info, 330, 276, Color.black);
		}
	}

	@Override
	public void onMouseEvent() {
		if (error != null && Mouse.isButtonDown(0) && isClickLegal(500))
			LayerRender.addEndEvent(() -> LayerRender.popLayer(PleaseWaitLayer.this));
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public void setError(String info) {
		error = info;
		LayerRender.addEndEvent(() -> CommonRender.loadFontUNI(error));
		isClickLegal(1);
	}
}
