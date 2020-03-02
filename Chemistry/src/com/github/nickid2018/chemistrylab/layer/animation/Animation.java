package com.github.nickid2018.chemistrylab.layer.animation;

import com.github.nickid2018.chemistrylab.layer.*;

public abstract class Animation extends Layer {

	protected final int fp;
	private int counter = 0;

	public Animation(int fp) {
		super(0, 0, 0, 0);
		this.fp = fp;
	}

	@Override
	public final void render() {
		if (counter == fp) {
			LayerRender.popLayer(this);
			onEnd();
			return;
		}
		render(counter++);
	}

	public abstract void render(int fp);

	public void onEnd() {
	}
}
