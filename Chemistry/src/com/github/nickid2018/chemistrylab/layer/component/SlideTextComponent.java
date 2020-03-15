package com.github.nickid2018.chemistrylab.layer.component;

import com.github.nickid2018.chemistrylab.util.*;
import com.github.nickid2018.chemistrylab.layer.*;
import com.github.nickid2018.chemistrylab.window.*;

public class SlideTextComponent extends TextComponent implements Slidable {

//	public SlideTextComponent(Layer l, String s, ButtonClick r, int size, Color c) {
//		this(l, s, r, size, c, false);
//	}
//
//	public SlideTextComponent(Layer l, String s, ButtonClick r, int size, Color c, boolean uni) {
//		super(1, 1, 1, 1, l, s, r, size, c, uni);
//	}

	public SlideTextComponent(float f, float f2, float g, float h, Layer l) {
		super(f, f2, g, h, l);
		// TODO 自动生成的构造函数存根
	}

	@Override
	public void setNowPositon(float x0, float x1, float y0, float y1) {
		range.x0 = x0;
		range.x1 = x1;
		range.y0 = y0;
		range.y1 = y1;
	}
}
