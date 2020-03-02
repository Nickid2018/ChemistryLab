package com.github.nickid2018.chemistrylab.layer.animation;

import static com.github.nickid2018.chemistrylab.ChemistryLab.*;

import com.github.nickid2018.chemistrylab.layer.*;
import com.github.nickid2018.chemistrylab.window.Window;

public class SlideAnimation extends Animation {

	public static final int DIRECTION_RIGHT = 0;
	public static final int DIRECTION_LEFT = 1;
	public static final int DIRECTION_DOWN = 2;
	public static final int DIRECTION_UP = 3;

	public static final int ON_END_PUSH = 0;
	public static final int ON_END_POP = 1;
	public static final int ON_END_NODO = 2;

	private int direction;
	private int onend;
	private float distance;
	private SildeLayer layer;
	private Runnable onend_event;

	public SlideAnimation(int fp, SildeLayer layer, int direction, int onend, Runnable onend_event) {
		super(fp);
		this.layer = layer;
		this.onend = onend;
		this.onend_event = onend_event;
		switch (direction) {
		case DIRECTION_RIGHT:
		case DIRECTION_LEFT:
			distance = layer.getRange().x1 - layer.getRange().x0;
			break;
		case DIRECTION_DOWN:
		case DIRECTION_UP:
			distance = layer.getRange().y1 - layer.getRange().y0;
			break;
		default:
			throw new IllegalArgumentException("direction");
		}
		this.direction = direction;
		switch (direction) {
		case DIRECTION_RIGHT:
			layer.setRange(Window.DREAM_WIDTH, layer.getRange().y0,
					Window.DREAM_WIDTH + layer.getRange().x1 - layer.getRange().x0 + distance, layer.getRange().y1);
			break;
		case DIRECTION_LEFT:
			layer.setRange(-distance, layer.getRange().y0, 0, layer.getRange().y1);
			break;
		case DIRECTION_DOWN:
			layer.setRange(layer.getRange().x0, Window.DREAM_HEIGHT, layer.getRange().x1,
					Window.DREAM_HEIGHT + layer.getRange().y1 - layer.getRange().y0 + distance);
			break;
		case DIRECTION_UP:
			layer.setRange(layer.getRange().x0, -distance, layer.getRange().x1, 0);
			break;
		default:
			throw new IllegalArgumentException("direction");
		}
		layer.render();
	}

	@Override
	public void render(int f) {
		f++;
		float step = distance / fp;
		float nowon = step * f;
		switch (direction) {
		case DIRECTION_RIGHT:
			layer.setRange(nowon - distance, layer.getRange().y0, nowon, layer.getRange().y1);
			break;
		case DIRECTION_LEFT:
			layer.setRange(Window.DREAM_WIDTH - nowon, layer.getRange().y0, Window.DREAM_WIDTH + distance - nowon,
					layer.getRange().y1);
			break;
		case DIRECTION_DOWN:
			layer.setRange(layer.getRange().x0, Window.DREAM_HEIGHT - nowon, layer.getRange().x1,
					Window.DREAM_HEIGHT + distance - nowon);
			break;
		case DIRECTION_UP:
			layer.setRange(layer.getRange().x0, nowon, layer.getRange().x1, nowon - distance);
			break;
		default:
			throw new IllegalArgumentException("direction");
		}
	}

	@Override
	public void onContainerResized() {
		if (onend == ON_END_PUSH)
			layer.onContainerResized();
	}

	@Override
	public void onEnd() {
		switch (onend) {
		case ON_END_PUSH:
			LayerRender.pushLayer(layer);
			break;
		case ON_END_POP:
			LayerRender.popLayer(layer);
			break;
		case ON_END_NODO:
			break;
		default:
			throw new IllegalArgumentException("onend");
		}
		if (onend_event != null) {
			onend_event.run();
		}
	}

}
