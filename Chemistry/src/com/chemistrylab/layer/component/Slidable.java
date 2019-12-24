package com.chemistrylab.layer.component;

public interface Slidable {

	void debugRender();
	void render();
	void setNowPositon(int x0,int x1,int y0,int y1);
	void onMouseEvent();
	boolean checkRange(int x, int y);

}
