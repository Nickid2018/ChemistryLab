package com.chemistrylab.layer;

import java.util.*;
import org.lwjgl.input.*;
import org.apache.log4j.*;
import com.chemistrylab.*;
import java.util.concurrent.*;
import com.chemistrylab.layer.animation.*;

public class LayerRender {

	public static final Logger logger = Logger.getLogger("Render Manager");
	private static final Stack<Layer> layers = new Stack<>();
	private static final Queue<Runnable> sr = new LinkedBlockingDeque<>();
	private static Layer focus = null;

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
					if (!l.equals(MessageBoard.INSTANCE))
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
