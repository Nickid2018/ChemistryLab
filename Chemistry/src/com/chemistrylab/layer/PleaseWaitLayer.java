package com.chemistrylab.layer;

import org.lwjgl.glfw.*;
import com.chemistrylab.*;
import org.newdawn.slick.*;
import com.chemistrylab.render.*;
import com.github.nickid2018.chemistrylab.Window;
import com.chemistrylab.eventbus.*;

import static com.chemistrylab.ChemistryLab.*;

public final class PleaseWaitLayer extends Layer {

	private String info;
	private String error;
	private String success;
	private final Runnable whattodo;

	private final AnimationTexture dealing = ((AnimationTexture) getTextures().get("texture.guianimation.dealing"))
			.clone();

	private final FastTexture tex;
	private static final FastQuad show;

	static {
		float center_x = Window.nowWidth / 2;
		float center_y = Window.nowHeight / 2;
		show = new FastQuad(center_x - 300, center_y - 100, center_x + 300, center_y + 100, Color.white);
	}

	public PleaseWaitLayer(String info, Runnable wtd) {
		super(0, 0, Window.nowWidth, Window.nowHeight);
		this.info = info;
		whattodo = wtd;
		CommonRender.loadFontUNI(info, 32);
		dealing.startToBind(this);
		float center_x = Window.nowWidth / 2;
		float center_y = Window.nowHeight / 2;
		tex = new FastTexture(center_x - 224, center_y - 24, center_x - 176, center_y + 24, 0, 0, 1, 1, dealing);
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
				LayerRender.popLayer(this);
			dealing.endToBind(this);
		});
		return this;
	}

	@Override
	public void render() {
		CommonRender.TABLE.render();
		show.render();
		float center_x = Window.nowWidth / 2;
		float center_y = Window.nowHeight / 2;
		if (error != null) {
			CommonRender.drawFontUNI(error, center_x - 170, center_y - 24, Color.red, 32);
		} else if (success != null) {
			CommonRender.drawFontUNI(success, center_x - 170, center_y - 24, Color.green, 32);
		} else {
			dealing.preBind(this);
			// CommonRender.drawTexture(dealing, center_x - 224, center_y - 24,
			// center_x - 176, center_y + 24, 0, 0, 1, 1);
			tex.render();
			CommonRender.drawFontUNI(info, center_x - 170, center_y - 24, Color.black, 32);
		}
	}

	@Override
	public void onMouseEvent(int button, int action, int mods) {
		if (action != GLFW.GLFW_PRESS)
			return;
		if ((error != null || success != null) && button == 0 && isClickLegal(500))
			LayerRender.popLayer(this);
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public void setError(String info) {
		error = info;
		LayerRender.addRunInRender(() -> CommonRender.loadFontUNI(error, 32));
		isClickLegal(1);
	}

	public void setSuccess(String info) {
		success = info;
		LayerRender.addRunInRender(() -> CommonRender.loadFontUNI(success, 32));
		isClickLegal(1);
	}
}
