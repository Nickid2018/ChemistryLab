package com.github.nickid2018.chemistrylab.layer.component;

import java.util.*;

import org.lwjgl.glfw.GLFW;
import org.newdawn.slick.*;

import com.github.nickid2018.chemistrylab.*;
import com.github.nickid2018.chemistrylab.init.*;
import com.github.nickid2018.chemistrylab.layer.*;
import com.github.nickid2018.chemistrylab.util.*;
import com.github.nickid2018.chemistrylab.window.Window;

import static org.lwjgl.opengl.GL11.*;

public class VerticalSlideBar extends Component {

	private final int vertsize;
	private final int barheight;
	private final Range slibar = new Range();
	private ArrayList<Slidable> cons;
	private Color barcolor = Color.darkGray;
	// Warning:This is a percent.
	private float postion = 0;

	public VerticalSlideBar(float x0, float y0, float x1, float y1, Layer l, ArrayList<Slidable> cons, int vertsize,
			int barheight) {
		super(x0, y0, x1, y1, l);
		this.cons = cons;
		this.vertsize = vertsize;
		this.barheight = barheight;
		slibar.x0 = x1 - barheight;
		slibar.x1 = x1;
		slibar.y0 = y0;
		slibar.y1 = y1;
	}

	@Override
	public void debugRender() {
		float mysize = range.y1 - range.y0;
		float shouldDraw = cons.size() * vertsize;
		if (shouldDraw > mysize) {
			float percent = mysize / shouldDraw;
			if (percent > 1)
				percent = 0;
			float barlength = (percent == 0 ? 1 : percent) * mysize;
			float drawup = (mysize - barlength) * postion + range.y0;
			float drawdown = (mysize - barlength) * postion + barlength + range.y0;
			slibar.y0 = (int) drawup;
			slibar.y1 = (int) drawdown;
			// Bar
			barcolor.bind();
			glBegin(GL_QUADS);
			glVertex2f(range.x1 - barheight, drawup);
			glVertex2f(range.x1 - barheight, drawdown);
			glVertex2f(range.x1, drawdown);
			glVertex2f(range.x1, drawup);
			glEnd();
			// Inside
			int canDraws = MathHelper.fastFloor(mysize / vertsize);
			int firstnear = MathHelper.fastFloor(postion * cons.size());
			int first = firstnear - 1;
			for (int i = first < 0 ? 0 : first, count = 0; i < cons.size() && i < firstnear + canDraws; i++, count++) {
				Slidable s = cons.get(i);
				s.setNowPositon(range.x0, range.x1, MathHelper.fastFloor(mysize * count / canDraws + range.y0),
						MathHelper.fastFloor(mysize * count / canDraws + range.y0 + vertsize));
				s.debugRender();
			}
		} else {
			for (int i = 0; i < cons.size() && i < cons.size(); i++) {
				Slidable s = cons.get(i);
				s.setNowPositon(range.x0, range.x1, MathHelper.fastFloor(i * vertsize + range.y0),
						MathHelper.fastFloor(i * vertsize + range.y0 + vertsize));
				s.debugRender();
			}
		}
	}

	@Override
	public void render() {
		super.render();
		float mysize = range.y1 - range.y0;
		float shouldDraw = cons.size() * vertsize;
		if (shouldDraw > mysize) {
			float percent = mysize / shouldDraw;
			if (percent > 1)
				percent = 0;
			float barlength = (percent == 0 ? 1 : percent) * mysize;
			float drawup = (mysize - barlength) * postion + range.y0;
			float drawdown = (mysize - barlength) * postion + barlength + range.y0;
			slibar.y0 = (int) drawup;
			slibar.y1 = (int) drawdown;
			// Bar
			barcolor.bind();
			glBegin(GL_QUADS);
			glVertex2f(range.x1 - barheight, drawup);
			glVertex2f(range.x1 - barheight, drawdown);
			glVertex2f(range.x1, drawdown);
			glVertex2f(range.x1, drawup);
			glEnd();
			// Inside
			int canDraws = MathHelper.fastFloor(mysize / vertsize);
			int firstnear = MathHelper.fastFloor(postion * cons.size());
			int first = firstnear - 1;
			for (int i = first < 0 ? 0 : first, count = 0; i < cons.size()
					&& i < firstnear + canDraws - 1; i++, count++) {
				Slidable s = cons.get(i);
				s.setNowPositon(range.x0, range.x1, MathHelper.fastFloor(mysize * count / canDraws + range.y0),
						MathHelper.fastFloor(mysize * count / canDraws + range.y0 + vertsize));
				s.render();
			}
		} else {
			for (int i = 0; i < cons.size() && i < cons.size(); i++) {
				Slidable s = cons.get(i);
				s.setNowPositon(range.x0, range.x1, MathHelper.fastFloor(i * vertsize + range.y0),
						MathHelper.fastFloor(i * vertsize + range.y0 + vertsize));
				s.render();
			}
		}
	}

	private boolean focus_on = false;
	private long last_focus = -1;

	@Override
	public void onMouseEvent(int button, int action, int mods) {
		if (action != GLFW.GLFW_PRESS)
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
			double why = Mouse.getY();
			float mysize = range.y1 - range.y0;
			float shouldDraw = cons.size() * vertsize;
			float percent = mysize / shouldDraw;
			float barlength = (percent > 1 ? 1 : percent) * mysize;
			postion = (float) ((Window.nowHeight - why - range.y0 - barlength / 2) / (mysize - barlength));
			postion = postion > 1 ? 1 : postion;
			postion = postion < 0 ? 0 : postion;
		} else {
			for (Slidable s : cons) {
				if (s.checkRange(Mouse.getX(), Mouse.getY())) {
					s.onMouseEvent(button, action, mods);
				}
			}
		}
	}

	@Override
	public void onScroll(double xoffset, double yoffset) {
		float mysize = range.y1 - range.y0;
		float shouldDraw = cons.size() * vertsize;
		float percent = mysize / shouldDraw;
		float barlength = (percent > 1 ? 1 : percent) * mysize;
		postion -= yoffset / (mysize - barlength);
		postion = postion > 1 ? 1 : postion;
		postion = postion < 0 ? 0 : postion;
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

	public int getVerticalSize() {
		return vertsize;
	}
}
