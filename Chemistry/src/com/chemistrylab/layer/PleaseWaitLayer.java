package com.chemistrylab.layer;

import org.lwjgl.input.*;
import com.chemistrylab.*;
import org.newdawn.slick.*;
import com.chemistrylab.render.*;
import com.chemistrylab.eventbus.*;

import static org.lwjgl.opengl.GL11.*;
import static com.chemistrylab.ChemistryLab.*;

public final class PleaseWaitLayer extends Layer {

	private String info;
	private String error;
	private String success;
	private final Runnable whattodo;

	private static final AnimationTexture dealing = (AnimationTexture) getTextures()
			.get("texture.guianimation.dealing");

	public PleaseWaitLayer(String info, Runnable wtd) {
		super(0, 0, nowWidth, nowHeight);
		this.info = info;
		whattodo = wtd;
		CommonRender.loadFontUNI(info);
		dealing.startToBind();
	}

	public PleaseWaitLayer start() {
		ThreadManger.invoke(() -> {
			try {
				whattodo.run();
			} catch (Throwable e) {
				Event error = ChemistryLab.THREAD_FATAL.clone();
				error.putExtra(0, e);
				error.putExtra(1, Thread.currentThread());
				EventBus.postEvent(error);
			}
			if (error == null && success == null)
				LayerRender.addEndEvent(() -> LayerRender.popLayer(PleaseWaitLayer.this));
			dealing.endToBind();
		});
		return this;
	}

	@Override
	public void render() {
		new Color(150, 150, 150, 75).bind();
		glBegin(GL_QUADS);
			glVertex2f(0, 0);
			glVertex2f(0, nowHeight);
			glVertex2f(nowWidth, nowHeight);
			glVertex2f(nowWidth, 0);
		glEnd();
		Color.white.bind();
		float center_x = nowWidth / 2;
		float center_y = nowHeight / 2;
		glBegin(GL_QUADS);
			glVertex2f(center_x - 300, center_y - 100);
			glVertex2f(center_x + 300, center_y - 100);
			glVertex2f(center_x + 300, center_y + 100);
			glVertex2f(center_x - 300, center_y + 100);
		glEnd();
		if (error != null) {
			CommonRender.drawFontUNI(error, center_x - 170, center_y - 24, Color.red);
		} else if (success != null) {
			CommonRender.drawFontUNI(success, center_x - 170, center_y - 24, Color.green);
		} else {
			CommonRender.drawTexture(dealing, center_x - 224, center_y - 24, center_x - 176, center_y + 24, 0, 0, 1, 1);
			CommonRender.drawFontUNI(info, center_x - 170, center_y - 24, Color.black);
		}
	}

	@Override
	public void onMouseEvent() {
		if ((error != null || success != null) && Mouse.isButtonDown(0) && isClickLegal(500))
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

	public void setSuccess(String info) {
		success = info;
		LayerRender.addEndEvent(() -> CommonRender.loadFontUNI(success));
		isClickLegal(1);
	}
}
