package com.github.nickid2018.chemistrylab.layer.component;

public interface Slidable {

	void debugRender();

	void render();

	void setNowPositon(float x0, float x1, float y0, float y1);

	void onMouseEvent(int button, int action, int mods);

	boolean checkRange(double d, double e);

}
