package com.chemistrylab.layer.component;

import org.newdawn.slick.*;
import com.chemistrylab.layer.*;
import com.chemistrylab.render.*;

public class TextComponent extends Component {
	
	protected Runnable act;
	protected String s;
	protected int size;
	protected Color t;
	protected boolean unifont;

	public TextComponent(int x0, int y0, int x1, int y1, Layer l,String s,Runnable r,int size,Color c) {
		this(x0, y0, x1, y1, l,s,r,size,c,false);
	}
	
	public TextComponent(int x0, int y0, int x1, int y1, Layer l,String s,Runnable r,int size,Color c,boolean unif) {
		super(x0, y0, x1, y1, l);
		act=r;
		this.s=s;
		this.size=size;
		t=c;
		unifont=unif;
		CommonRender.loadFontUNI(s);
	}
	
	@Override
	public void onContainerResized() {
		
	}

	@Override
	public void render() {
		super.render();
		if(!unifont)
			CommonRender.drawFont(s, range.x0, range.y0/2+range.y1/2-size/2, size, t);
		else{
			CommonRender.drawFontUNI(s, range.x0, range.y0/2+range.y1/2-size/2, t);
		}
	}

	@Override
	public void onMouseEvent() {
		act.run();
	}
}
