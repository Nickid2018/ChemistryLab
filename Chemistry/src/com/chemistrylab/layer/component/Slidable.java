package com.chemistrylab.layer.component;

public interface Slidable {

	void debugRender();

	void render();

	void setNowPositon(float x0, float x1, float y0, float y1);

	void onMouseEvent();

	boolean checkRange(int x, int y);

}
