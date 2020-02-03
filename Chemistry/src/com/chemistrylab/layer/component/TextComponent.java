package com.chemistrylab.layer.component;

import org.newdawn.slick.*;
import com.chemistrylab.layer.*;
import com.chemistrylab.render.*;

public class TextComponent extends Component {

	protected Runnable act;
	protected String s;
	protected int size;
	protected Color t;
	protected boolean unifont;
	protected boolean aligncenter = false;

	public TextComponent(float x0, float y0, float x1, float y1, Layer l, String s, Runnable r, int size, Color c) {
		this(x0, y0, x1, y1, l, s, r, size, c, false);
	}

	public TextComponent(float x0, float y0, float x1, float y1, Layer l, String s, Runnable r, int size, Color c,
			boolean unif) {
		super(x0, y0, x1, y1, l);
		act = r;
		this.s = s;
		this.size = size;
		t = c;
		unifont = unif;
		CommonRender.loadFontUNI(s, size);
	}

	public TextComponent setAlignCenter() {
		aligncenter = true;
		return this;
	}

	public String getText() {
		return s;
	}

	public void setText(String s) {
		this.s = s;
	}
	
	@Override
	public void onContainerResized() {
	}

	@Override
	public void render() {
		super.render();
		if (!unifont) {
			float start = range.x0;
			if (aligncenter) {
				start = (range.x1 + range.x0) / 2 - CommonRender.calcTextWidth(s, size) / 2;
			}
			CommonRender.drawFont(s, start, range.y0 / 2 + range.y1 / 2 - size / 2, size, t);
		} else {
			float start = range.x0;
			if (aligncenter) {
				start = (range.x1 + range.x0) / 2 - CommonRender.getFontLengthUNI(s, size) / 2.0f;
			}
			CommonRender.drawFontUNI(s, start, range.y0 / 2 + range.y1 / 2 - CommonRender.getFontHeightUNI(s, size) / 2.0f - 5, t, size);
		}
	}

	@Override
	public void onMouseEvent() {
		act.run();
	}
}
