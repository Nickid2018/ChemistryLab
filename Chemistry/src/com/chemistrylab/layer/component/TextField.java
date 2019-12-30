package com.chemistrylab.layer.component;

import org.newdawn.slick.*;
import com.chemistrylab.layer.*;
import com.chemistrylab.render.*;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.input.Mouse;

public class TextField extends Component {

	private String pa;
	private int size;
	private int startpaint;
	private int postion = 0;
	private Color color = Color.white;

	public TextField(int x0, int y0, int x1, int y1, Layer l, int size) {
		this(x0, y0, x1, y1, l, size, "");
	}

	public TextField(int x0, int y0, int x1, int y1, Layer l, int size, String s) {
		super(x0, y0, x1, y1, l);
		pa = s;
		this.size = size;
		startpaint = 0;
	}

	public String getString() {
		return pa;
	}

	public void setString(String pa) {
		this.pa = pa;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getPostion() {
		return postion;
	}

	public void setPostion(int postion) {
		this.postion = postion;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	@Override
	public void onMouseEvent() {
		if(Mouse.isButtonDown(0)) {
			float len = Mouse.getX();
			String s = CommonRender.subTextWidth(pa.substring(startpaint), size, len);
			postion = startpaint + s.length();
		}
	}

	@Override
	public void render() {
		super.render();
		float honzsize = range.x1 - range.x0;
		float vertsize = range.y1 - range.y0;
		float hei = CommonRender.winToOthHeight(size);
		float nexty = range.y0 + vertsize / 2 - hei / 2;
		float all_length = CommonRender.calcTextWidth(pa, size);
		glLineWidth(1);
		if(all_length < honzsize) {
			CommonRender.drawFont(pa, range.x0, nexty, size, color);
			float pos = CommonRender.calcTextWidth(pa.substring(0, postion), size);
			glBegin(GL_LINE_STRIP);
				glVertex2f(pos, range.y0);
				glVertex2f(pos, range.y1);
			glEnd();
		} else {
			String topaint = CommonRender.subTextWidth(pa.substring(startpaint), size, honzsize);
			CommonRender.drawFont(topaint, range.x0, nexty, size, color);
			float pos = CommonRender.calcTextWidth(pa.substring(startpaint, postion), size);
			glBegin(GL_LINE_STRIP);
				glVertex2f(pos, range.y0);
				glVertex2f(pos, range.y1);
			glEnd();
		}
	}

}
