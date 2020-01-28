package com.chemistrylab.layer;

import java.util.*;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;
import com.chemistrylab.*;
import org.newdawn.slick.*;
import com.chemistrylab.render.*;
import com.chemistrylab.layer.component.*;

import static org.lwjgl.opengl.GL11.*;
import static com.chemistrylab.ChemistryLab.*;

public abstract class Layer {

	protected final Range range;
	protected Set<Component> comps = new HashSet<>();
	protected long lastClick = -1;
	protected Component focus = null;

	public Layer(float f, float y0, float g, float h) {
		range = new Range();
		range.x0 = f;
		range.y0 = y0;
		range.x1 = g;
		range.y1 = h;
	}

	public static final boolean checkRange(Range range, int x, int y) {
		return CommonRender.othToWinWidth(range.x0) < x && CommonRender.othToWinWidth(range.x1) > x
				&& CommonRender.othToWinHeight(range.y0) < Display.getHeight() - y
				&& CommonRender.othToWinHeight(range.y1) > Display.getHeight() - y;
	}

	public final boolean checkRange(int x, int y) {
		return CommonRender.othToWinWidth(range.x0) < x && CommonRender.othToWinWidth(range.x1) > x
				&& CommonRender.othToWinHeight(range.y0) < Display.getHeight() - y
				&& CommonRender.othToWinHeight(range.y1) > Display.getHeight() - y;
	}
	
	protected static final void doDefaultResize(Layer l){
		float ratioX = nowWidth / oldWidth;
		float ratioY = nowHeight / oldHeight;
		l.range.x0 *= ratioX;
		l.range.x1 *= ratioX;
		l.range.y0 *= ratioY;
		l.range.y1 *= ratioY;
	}

	public final Range getRange() {
		return range;
	}

	public void debugRender() {
		if (useComponent())
			compoRender();
		else
			render();
		Color.red.bind();
		glBegin(GL_LINE_LOOP);
		glVertex2f(range.x0, range.y0);
		glVertex2f(range.x1, range.y0);
		glVertex2f(range.x1, range.y1);
		glVertex2f(range.x0, range.y1);
		glEnd();
		if (useComponent()) {
			for (Component c : comps)
				c.debugRender();
		}
	}

	public final void compoRender() {
		render();
		for (Component c : comps) {
			c.render();
		}
	}

	public final boolean isClickLegal(long del) {
		boolean b = ChemistryLab.getTime() - lastClick > del;
		if (b)
			lastClick = ChemistryLab.getTime();
		return b;
	}

	public final boolean isFocus(Component c) {
		return c.equals(focus);
	}

	public abstract void render();

	public void onMouseEvent() {
		if (useComponent()) {
			for (Component c : comps) {
				if (c.checkRange(Mouse.getX(), Mouse.getY())) {
					c.onMouseEvent();
					if (Mouse.isButtonDown(0))
						focus = c;
				}
			}
		}
	}

	public void onContainerResized() {
		float ratioX = nowWidth / oldWidth;
		float ratioY = nowHeight / oldHeight;
		resizeSelf(ratioX, ratioY);
		if (useComponent())
			resizeComponents(ratioX, ratioY);
	}

	protected void resizeSelf(float ratioX, float ratioY) {
		range.x0 *= ratioX;
		range.x1 *= ratioX;
		range.y0 *= ratioY;
		range.y1 *= ratioY;
	}

	protected void resizeComponents(float ratioX, float ratioY) {
		for(Component c:comps){
			c.range.x0 *= ratioX;
			c.range.x1 *= ratioX;
			c.range.y0 *= ratioY;
			c.range.y1 *= ratioY;
		}
	}

	public void onPop() {
		if (useComponent()) {
			for (Component c : comps) {
				c.onPop();
			}
		}
	}

	public void onKeyActive() {
		if (useComponent()) {
			if (comps.contains(focus)) {
				focus.onKeyActive();
			} else
				focus = null;
		}
	}

	public boolean isMouseEventStop() {
		return true;
	}

	public boolean useComponent() {
		return false;
	}
}
