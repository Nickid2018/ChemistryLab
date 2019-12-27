package com.chemistrylab.layer.component;

import com.chemistrylab.layer.*;

import static com.chemistrylab.render.CommonRender.*;
import static org.lwjgl.opengl.GL11.*;

public class TextArea extends Component {

	private String text;
	private int size;

	public TextArea(int x0, int y0, int x1, int y1, Layer l) {
		this(x0, y0, x1, y1, l, "", 16);
	}

	public TextArea(int x0, int y0, int x1, int y1, Layer l, String text, int size) {
		super(x0, y0, x1, y1, l);
		setText(text);
		this.size = size;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public void render() {
		String[] lines = text.split("\n");
		float maxWidth = -1;
		float lasty = range.y0;
		float adddrawY = winToOthHeight(size);
		for(String s:lines) {
			float len = calcTextWidth(s, size);
			maxWidth = Math.max(maxWidth, len);
			
			lasty += adddrawY;
		}
		super.render();
	}
}
