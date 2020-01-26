package com.chemistrylab.layer;

import java.util.*;
import org.lwjgl.input.*;
import org.apache.log4j.*;
import com.chemistrylab.*;
import com.chemistrylab.layer.animation.*;

public class LayerRender {

	public static final Logger logger = Logger.getLogger("Render Manager");
	private static final Stack<Layer> layers = new Stack<>();
	private static final Set<Runnable> sr = new HashSet<>();
	private static Layer focus = null;

	public static void pushLayer(Layer layer) {
		layers.push(layer);
	}

	public static boolean popLayer(Layer layer) {
		return layers.remove(layer);
	}

	public static boolean popLayer(Class<?> clazz) {
		Layer find = null;
		for (Layer l : layers) {
			if (l.getClass().equals(clazz)) {
				find = l;
				break;
			}
		}
		boolean ret = find != null && layers.remove(find);
		find.onPop();
		return ret;
	}

	public static void popLayers() {
		for (Layer l : layers) {
			l.onPop();
		}
		layers.removeAllElements();
	}

	public static void postKey() {
		if (layers.contains(focus)) {
			focus.onKeyActive();
		} else
			focus = null;
	}

	public static void postMouse() {
		int x = Mouse.getX();
		int y = Mouse.getY();
		boolean top = true;
		for (int i = layers.size() - 1; i >= 0; i--) {
			Layer l = layers.elementAt(i);
			if (l.checkRange(x, y)) {
				l.onMouseEvent();
				if (top && Mouse.isButtonDown(0)) {
					top = false;
					focus = l;
				}
				if (l.isMouseEventStop())
					break;
			}
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
				l.render();
		}
		for (Runnable r : sr) {
			r.run();
		}
		sr.clear();
	}

	/**
	 * <p>
	 * Ensure not an {@linkplain java.util.ConcurrentModificationException}
	 * happens.
	 * </p>
	 * <p>
	 * Info:The event will be run in Render Thread(Have OpenGL), if you want to
	 * do something using too much time, please use EventBus.
	 * </p>
	 * 
	 * @param r
	 *            an event
	 */
	public static void addEndEvent(Runnable r) {
		sr.add(r);
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
