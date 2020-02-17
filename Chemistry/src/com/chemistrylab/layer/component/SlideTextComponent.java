package com.chemistrylab.layer.component;

import org.newdawn.slick.*;
import com.chemistrylab.util.*;
import com.chemistrylab.layer.*;

public class SlideTextComponent extends TextComponent implements Slidable {

	public SlideTextComponent(Layer l, String s, ButtonClick r, int size, Color c) {
		this(l, s, r, size, c, false);
	}

	public SlideTextComponent(Layer l, String s, ButtonClick r, int size, Color c, boolean uni) {
		super(1, 1, 1, 1, l, s, r, size, c, uni);
	}

	@Override
	public void setNowPositon(float x0, float x1, float y0, float y1) {
		range.x0 = x0;
		range.x1 = x1;
		range.y0 = y0;
		range.y1 = y1;
	}
}
