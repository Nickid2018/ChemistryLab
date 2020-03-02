package com.github.nickid2018.chemistrylab.layer.animation;

import com.github.nickid2018.chemistrylab.layer.*;

public abstract class SildeLayer extends Layer {

	public SildeLayer(float f, float y0, float g, float h) {
		super(f, y0, g, h);
	}

	public final void setRange(float x0, float y0, float x1, float y1) {
		range.x0 = x0;
		range.x1 = x1;
		range.y0 = y0;
		range.y1 = y1;
	}
}
