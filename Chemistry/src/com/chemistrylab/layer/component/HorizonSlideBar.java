package com.chemistrylab.layer.component;

import java.util.*;
import com.chemistrylab.*;

import org.lwjgl.glfw.GLFW;
import org.newdawn.slick.*;
import com.chemistrylab.init.*;
import com.chemistrylab.util.*;
import com.chemistrylab.layer.*;

import static org.lwjgl.opengl.GL11.*;

public class HorizonSlideBar extends Component {

	private final int honzsize;
	private final int barheight;
	private ArrayList<Slidable> cons;
	// Warning:This is a percent.
	private float postion;
	private Range slibar = new Range();
	private Color barcolor = Color.darkGray;

	public HorizonSlideBar(float x0, float y0, float x1, float y1, Layer l, ArrayList<Slidable> cons, int honzsize,
			int barheight) {
		super(x0, y0, x1, y1, l);
		this.cons = cons;
		this.honzsize = honzsize;
		this.barheight = barheight;
		slibar.x0 = x0;
		slibar.x1 = x1;
		slibar.y0 = y1 - barheight;
		slibar.y1 = y1;
	}

	@Override
	public void debugRender() {
		float mysize = range.x1 - range.x0;
		float shouldDraw = cons.size() * honzsize;
		if (shouldDraw > mysize) {
			float percent = mysize / shouldDraw;
			if (percent > 1)
				percent = 0;
			float barlength = (percent == 0 ? 1 : percent) * mysize;
			float drawup = (mysize - barlength) * postion + range.x0;
			float drawdown = (mysize - barlength) * postion + barlength + range.x0;
			slibar.x0 = (int) drawup;
			slibar.x1 = (int) drawdown;
			// Bar
			barcolor.bind();
			glBegin(GL_QUADS);
			glVertex2f(drawup, range.y1 - barheight);
			glVertex2f(drawdown, range.y1 - barheight);
			glVertex2f(drawdown, range.y1);
			glVertex2f(drawup, range.y1);
			glEnd();
			// Inside
			int canDraws = MathHelper.fastFloor(mysize / honzsize);
			int firstnear = MathHelper.fastFloor(postion * cons.size());
			int first = firstnear - 1;
			for (int i = first < 0 ? 0 : first, count = 0; i < cons.size() && i < firstnear + canDraws; i++, count++) {
				Slidable s = cons.get(i);
				s.setNowPositon(MathHelper.fastFloor(mysize * count / canDraws + range.x0),
						MathHelper.fastFloor(mysize * count / canDraws + range.x0 + honzsize), range.y0, range.y1);
				s.debugRender();
			}
		} else {
			for (int i = 0; i < cons.size() && i < cons.size(); i++) {
				Slidable s = cons.get(i);
				s.setNowPositon(MathHelper.fastFloor(i * honzsize + range.x0),
						MathHelper.fastFloor(i * honzsize + range.x0 + honzsize), range.y0, range.y1);
				s.debugRender();
			}
		}
	}

	@Override
	public void render() {
		float mysize = range.x1 - range.x0;
		float shouldDraw = cons.size() * honzsize;
		if (shouldDraw > mysize) {
			float percent = mysize / shouldDraw;
			if (percent > 1)
				percent = 0;
			float barlength = (percent == 0 ? 1 : percent) * mysize;
			float drawup = (mysize - barlength) * postion + range.x0;
			float drawdown = (mysize - barlength) * postion + barlength + range.x0;
			slibar.x0 = (int) drawup;
			slibar.x1 = (int) drawdown;
			// Bar
			barcolor.bind();
			glBegin(GL_QUADS);
			glVertex2f(drawup, range.y1 - barheight);
			glVertex2f(drawdown, range.y1 - barheight);
			glVertex2f(drawdown, range.y1);
			glVertex2f(drawup, range.y1);
			glEnd();
			// Inside
			int canDraws = MathHelper.fastFloor(mysize / honzsize);
			int firstnear = MathHelper.fastFloor(postion * cons.size());
			int first = firstnear - 1;
			for (int i = first < 0 ? 0 : first, count = 0; i < cons.size() && i < firstnear + canDraws; i++, count++) {
				Slidable s = cons.get(i);
				s.setNowPositon(MathHelper.fastFloor(mysize * count / canDraws + range.x0),
						MathHelper.fastFloor(mysize * count / canDraws + range.x0 + honzsize), range.y0, range.y1);
				s.render();
			}
		} else {
			for (int i = 0; i < cons.size() && i < cons.size(); i++) {
				Slidable s = cons.get(i);
				s.setNowPositon(MathHelper.fastFloor(i * honzsize + range.x0),
						MathHelper.fastFloor(i * honzsize + range.x0 + honzsize), range.y0, range.y1);
				s.render();
			}
		}
	}

	private boolean focus_on = false;
	private long last_focus = -1;

	@Override
	public void onMouseEvent(int button, int action, int mods) {
		if(action != GLFW.GLFW_PRESS)
			return;
		if (!isClickLegal(10))
			return;
		if (ChemistryLab.getTime() - last_focus > 20) {
			focus_on = false;
		}
		if (checkRange(slibar, Mouse.getX(), Mouse.getY()) || focus_on) {
			if (button != 0)
				return;
			focus_on = true;
			last_focus = ChemistryLab.getTime();
			double why = Mouse.getX();
			float mysize = range.x1 - range.x0;
			float shouldDraw = cons.size() * honzsize;
			float percent = mysize / shouldDraw;
			float barlength = (percent > 1 ? 1 : percent) * mysize;
			postion = (float) ((ChemistryLab.nowHeight - why - range.x0 - barlength / 2) / (mysize - barlength));
			postion = postion > 1 ? 1 : postion;
			postion = postion < 0 ? 0 : postion;
		} else {
			for (Slidable s : cons) {
				if (s.checkRange((int) Mouse.getX(), (int) Mouse.getY())) {
					s.onMouseEvent(button, action, mods);
				}
			}
		}
	}
	
	@Override
	public void onScroll(double xoffset, double yoffset) {
		float mysize = range.y1 - range.y0;
		float shouldDraw = cons.size() * honzsize;
		float percent = mysize / shouldDraw;
		float barlength = (percent > 1 ? 1 : percent) * mysize;
		postion -= yoffset / (mysize - barlength);
		postion = postion > 1 ? 1 : postion;
		postion = postion < 0 ? 0 : postion;
	}

	public int getHorizonSize() {
		return honzsize;
	}

	public ArrayList<Slidable> getComponent() {
		return cons;
	}

	public void setComponent(ArrayList<Slidable> cons) {
		this.cons = cons;
	}

	public int getBarHeight() {
		return barheight;
	}

	public Color getBarcolor() {
		return barcolor;
	}

	public void setBarcolor(Color barcolor) {
		this.barcolor = barcolor;
	}

	public float getPostion() {
		return postion;
	}

	public void setPostion(float postion) {
		this.postion = postion;
	}
}
