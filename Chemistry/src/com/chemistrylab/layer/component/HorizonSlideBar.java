package com.chemistrylab.layer.component;

import java.util.*;
import com.chemistrylab.*;
import com.chemistrylab.layer.*;
import org.newdawn.slick.opengl.*;

import static org.lwjgl.opengl.GL11.*;

public class HorizonSlideBar extends Component {
	
	private static final Texture bar=ChemistryLab.getTextures().get("texture.slidebar.hori");
	private final ArrayList<Slidable> cons;
	private final int honzsize;
	private final int barheight;
	//Warning:This is a percent.
	private float postion;

	public HorizonSlideBar(int x0, int y0, int x1, int y1, Layer l,ArrayList<Slidable> cons,int honzsize,int barheight) {
		super(x0, y0, x1, y1, l);
		this.cons=cons;
		this.honzsize=honzsize;
		this.barheight=barheight;
	}
	
	@Override
	public void debugRender() {
		super.debugRender();
		for(Slidable c:cons){
			c.debugRender();
		}
	}

	@Override
	public void render() {
		float shouldDraw=cons.size()*honzsize;
		float percent=(range.x1-range.x0)/shouldDraw;
		float barlength=percent*(range.x1-range.x0);
		float drawleft=(range.x1-range.x0-barlength)*postion-barlength/2;
		float drawright=(range.x1-range.x0-barlength)*postion+barlength/2;
		//Bar
		glEnable(GL_TEXTURE_2D);
		bar.bind();
		glBegin(GL_QUADS);
			glTexCoord2f(0, 0);
			glVertex2f(drawleft, range.y0-barheight);
			glTexCoord2f(1, 0);
			glVertex2f(drawright, range.y1-barheight);
			glTexCoord2f(1, 0.125f);
			glVertex2f(drawright, range.y1);
			glTexCoord2f(0, 0.125f);
			glVertex2f(drawleft, range.y0);
		glEnd();
		glDisable(GL_TEXTURE_2D);
		//Inside
	}

	public int getHorizonSize() {
		return honzsize;
	}

	public ArrayList<Slidable> getComponent() {
		return cons;
	}

	public int getBarHeight() {
		return barheight;
	}
}
