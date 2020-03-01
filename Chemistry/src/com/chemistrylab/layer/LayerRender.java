package com.chemistrylab.layer;

import java.util.*;
import org.apache.log4j.*;
import com.chemistrylab.*;
import java.util.concurrent.*;
import com.chemistrylab.util.*;
import com.chemistrylab.layer.animation.*;

public class LayerRender {

	public static final Logger logger = Logger.getLogger("Render Manager");
	private static final Stack<Layer> layers = new Stack<>();
	private static final Queue<Runnable> sr = new LinkedBlockingDeque<>();
	private static Layer focus = null;
	private static Layer cursor = null;

	public static void addRunInRender(Runnable r) {
		sr.offer(r);
	}

	public static void pushLayer(Layer layer) {
		sr.offer(() -> layers.push(layer));
	}

	public static void popLayer(Layer layer) {
		sr.offer(() -> {
			layers.remove(layer);
			layer.onPop();
		});
	}

	public static void popLayer(Class<?> clazz) {
		sr.offer(() -> {
			Layer find = null;
			for (Layer l : layers) {
				if (l.getClass().equals(clazz)) {
					find = l;
					break;
				}
			}
			if (find == null)
				return;
			layers.remove(find);
			find.onPop();
		});
	}

	public static void popLayers() {
		for (Layer l : layers) {
			l.onPop();
		}
		layers.removeAllElements();
	}

	public static void postKey(int key, int scancode, int action, int mods) {
		if (layers.contains(focus)) {
			focus.onKeyActive(key, scancode, action, mods);
		} else
			focus = null;
	}

	public static void postCharInput(int codepoint) {
		if (layers.contains(focus)) {
			focus.onCharInput(codepoint);
		} else
			focus = null;
	}

	public static void postModCharInput(int codepoint, int mods) {
		if (layers.contains(focus)) {
			focus.onModCharInput(codepoint, mods);
		} else
			focus = null;
	}

	public static void postMouse(int button, int action, int mods) {
		double x = Mouse.getX();
		double y = Mouse.getY();
		boolean top = true;
		for (int i = layers.size() - 1; i >= 0; i--) {
			Layer l = layers.elementAt(i);
			if (l.checkRange(x, y)) {
				l.onMouseEvent(button, action, mods);
				if (top && button == 0) {
					top = false;
					if (!l.equals(MessageBoard.INSTANCE) && !l.equals(focus)) {
						if (focus != null)
							focus.onFocusLost();
						focus = l;
						focus.gainFocus();
					}
				}
				if (l.isMouseEventStop())
					break;
			}
		}
	}

	public static void postScroll(double xoffset, double yoffset) {
		double x = Mouse.getX();
		double y = Mouse.getY();
		for (int i = layers.size() - 1; i >= 0; i--) {
			Layer l = layers.elementAt(i);
			if (l.checkRange(x, y)) {
				l.onScroll(xoffset, yoffset);
				return;
			}
		}
	}

	public static void postCursorPos(double xpos, double ypos) {
		boolean find = false;
		for (int i = layers.size() - 1; i >= 0; i--) {
			Layer l = layers.elementAt(i);
			if (l.checkRange(xpos, ypos) && !(l instanceof Background)) {
				l.onCursorPositionChanged(xpos, ypos);
				if (l.isMouseEventStop())
					break;
			}
			if (l != cursor) {
				if (cursor != null)
					cursor.onCursorOut();
				cursor = l;
				cursor.onCursorIn();
				find = true;
			}
		}
		if (!find && cursor != null) {
			cursor.onCursorOut();
			cursor = null;
		}
	}

	public static void windowResize() {
		for (Layer l : layers) {
			l.onContainerResized();
		}
	}

	public static void render() {
		for (Layer l : layers) {
			if (ChemistryLab.f3)
				l.debugRender();
			else if (l.useComponent())
				l.compoRender();
			else
				l.render_layer();
		}
		while (!sr.isEmpty()) {
			sr.poll().run();
		}
	}

	public static int getLayerAmount() {
		return layers.size();
	}

	public static int getAnimationAmount() {
		int a = 0;
		for (Layer l : layers) {
			if (l instanceof Animation)
				a++;
		}
		return a;
	}
}
