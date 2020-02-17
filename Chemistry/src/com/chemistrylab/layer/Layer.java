package com.chemistrylab.layer;

import java.util.*;
import com.chemistrylab.*;
import org.newdawn.slick.*;
import java.util.concurrent.*;
import com.chemistrylab.util.*;
import com.chemistrylab.render.*;
import com.chemistrylab.layer.component.*;

import static org.lwjgl.opengl.GL11.*;
import static com.chemistrylab.ChemistryLab.*;

public abstract class Layer {

	protected final Range range;
	protected Set<Component> comps = new HashSet<>();
	private final Queue<Runnable> sr = new LinkedBlockingDeque<>();
	protected long lastClick = -1;
	protected Component focus = null;

	public Layer(float x0, float y0, float x1, float y1) {
		range = new Range();
		range.x0 = x0;
		range.y0 = y0;
		range.x1 = x1;
		range.y1 = y1;
	}

	public static final boolean checkRange(Range range, double x, double y) {
		return CommonRender.othToWinWidth(range.x0) < x && CommonRender.othToWinWidth(range.x1) > x
				&& CommonRender.othToWinHeight(range.y0) < y && CommonRender.othToWinHeight(range.y1) > y;
	}

	public final boolean checkRange(double d, double e) {
		return CommonRender.othToWinWidth(range.x0) < d && CommonRender.othToWinWidth(range.x1) > d
				&& CommonRender.othToWinHeight(range.y0) < e && CommonRender.othToWinHeight(range.y1) > e;
	}

	protected static final void doDefaultResize(Layer l) {
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
		while (!sr.isEmpty()) {
			sr.poll().run();
		}
	}

	public final void compoRender() {
		render();
		for (Component c : comps) {
			c.render();
		}
		while (!sr.isEmpty()) {
			sr.poll().run();
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

	public final void addComponent(Component c) {
		sr.offer(() -> comps.add(c));
	}

	public final void removeComponent(Component c) {
		sr.offer(() -> comps.remove(c));
	}

	public final void removeAllComponent() {
		sr.offer(() -> comps.clear());
	}

	public final void render_layer() {
		render();
		while (!sr.isEmpty()) {
			sr.poll().run();
		}
	}

	public abstract void render();

	protected void resizeSelf(float ratioX, float ratioY) {
		range.x0 *= ratioX;
		range.x1 *= ratioX;
		range.y0 *= ratioY;
		range.y1 *= ratioY;
	}

	protected void resizeComponents(float ratioX, float ratioY) {
		for (Component c : comps) {
			c.range.x0 *= ratioX;
			c.range.x1 *= ratioX;
			c.range.y0 *= ratioY;
			c.range.y1 *= ratioY;
		}
	}

	public void onContainerResized() {
		float ratioX = nowWidth / oldWidth;
		float ratioY = nowHeight / oldHeight;
		resizeSelf(ratioX, ratioY);
		if (useComponent())
			resizeComponents(ratioX, ratioY);
	}

	public void onPop() {
		if (useComponent()) {
			for (Component c : comps) {
				c.onPop();
			}
		}
	}

	public void onMouseEvent(int button, int action, int mods) {
		if (useComponent()) {
			for (Component c : comps) {
				if (c.checkRange(Mouse.getX(), Mouse.getY())) {
					c.onMouseEvent(button, action, mods);
					if (button == 0)
						focus = c;
				}
			}
		}
	}

	public void onScroll(double xoffset, double yoffset) {
		if (useComponent()) {
			for (Component c : comps) {
				if (c.checkRange(Mouse.getX(), Mouse.getY())) {
					c.onScroll(xoffset, yoffset);
					return;
				}
			}
		}
	}

	public void onKeyActive(int key, int scancode, int action, int mods) {
		if (useComponent()) {
			if (comps.contains(focus)) {
				focus.onKeyActive(key, scancode, action, mods);
			} else
				focus = null;
		}
	}

	public void onCharInput(int codepoint) {
		if (useComponent()) {
			if (comps.contains(focus)) {
				focus.onCharInput(codepoint);
			} else
				focus = null;
		}
	}

	public void onFocusLost() {
		focus = null;
	}

	public void gainFocus() {
	}

	public int preUseRange() {
		return 0;
	}

	public boolean isMouseEventStop() {
		return true;
	}

	public boolean useComponent() {
		return false;
	}
}
