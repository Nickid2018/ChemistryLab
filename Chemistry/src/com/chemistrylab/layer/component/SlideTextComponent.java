package com.chemistrylab.layer.component;

import org.newdawn.slick.*;
import com.chemistrylab.layer.*;

public class SlideTextComponent extends TextComponent implements Slidable {
	
	public SlideTextComponent(Layer l, String s, Runnable r, int size, Color c){
		this(l,s,r,size,c,false);
	}

	public SlideTextComponent(Layer l, String s, Runnable r, int size, Color c,boolean uni) {
		super(1, 1, 1, 1, l, s, r, size, c, uni);
	}

	@Override
	public void setNowPositon(int x0, int x1, int y0, int y1) {
		range.x0=x0;
		range.x1=x1;
		range.y0=y0;
		range.y1=y1;
	}
}
