package com.chemistrylab.layer.component;

import org.newdawn.slick.*;
import com.chemistrylab.layer.*;

import static org.lwjgl.opengl.GL11.*;
import static com.chemistrylab.render.CommonRender.*;

public class TextArea extends Component {

	private String text;
	private int size;
	private Color textc = Color.white;
	private int xpostion = 0;
	private int ypostion = 0;
	private Range slibarx = new Range();
	private Range slibary = new Range();
	private Color barcolor = Color.darkGray;;
	private int barheight;

	public TextArea(int x0, int y0, int x1, int y1, Layer l) {
		this(x0, y0, x1, y1, l, "", 16, 3);
	}

	public TextArea(int x0, int y0, int x1, int y1, Layer l, String text, int size, int barheight) {
		super(x0, y0, x1, y1, l);
		setText(text);
		this.size = size;
		this.barheight = barheight;

		slibarx.x0 = x1 - barheight;
		slibarx.x1 = x1;
		slibarx.y0 = y0;
		slibarx.y1 = y1;
		
		slibary.x0 = x0;
		slibary.x1 = x1;
		slibary.y0 = y1 - barheight;
		slibary.y1 = y1;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Color getTextColor() {
		return textc;
	}

	public void setTextColor(Color textc) {
		this.textc = textc;
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

	@Override
	public void render() {
		super.render();
		String[] lines = text.split("\n");
		float honzsize = range.x1 - range.x0;
		float vertsize = range.y1 - range.y0;
		float maxWidth = -1;
		float lasty = range.y0;
		float adddrawY = winToOthHeight(size);
		int skip = 0;
		for (String s : lines) {
			if(skip < xpostion){
				skip++;
				continue;
			}
			maxWidth = Math.max(maxWidth, s.length());
			s = s.substring(xpostion, s.length() < xpostion?xpostion:s.length());
			float len = calcTextWidth(s, size);
			if (len > range.x1 - range.x0) {
				s = subTextWidth(s, size, range.x1 - range.x0);
			}
			drawFont(s, range.x0, lasty, size, textc);
			lasty += adddrawY;
			if(lasty > vertsize){
				break;
			}
		}
		//Horzion
		float shouldDrawx = maxWidth * size;
		if (shouldDrawx > honzsize) {
			float percent = honzsize / shouldDrawx;
			if (percent > 1)
				percent = 0;
			float barlength = (percent == 0 ? 1 : percent) * honzsize;
			float drawup = (honzsize - barlength) * (xpostion / maxWidth) + range.x0;
			float drawdown = (honzsize - barlength) * (xpostion / maxWidth) + barlength + range.x0;
			slibarx.x0 = (int) drawup;
			slibarx.x1 = (int) drawdown;
			// Bar
			barcolor.bind();
			glBegin(GL_QUADS);
				glVertex2f(drawup, range.y1 - barheight);
				glVertex2f(drawdown, range.y1 - barheight);
				glVertex2f(drawdown, range.y1);
				glVertex2f(drawup, range.y1);
			glEnd();
		}
		//Vertical
		float shouldDrawy = lines.length * size;
		if (shouldDrawy > vertsize) {
			float percent = vertsize / shouldDrawy;
			if (percent > 1)
				percent = 0;
			float barlength = (percent == 0 ? 1 : percent) * vertsize;
			float drawup = (vertsize - barlength) * (ypostion / lines.length) + range.y0;
			float drawdown = (vertsize - barlength) * (ypostion / lines.length) + barlength + range.y0;
			slibary.y0 = (int) drawup;
			slibary.y1 = (int) drawdown;
			// Bar
			barcolor.bind();
			glBegin(GL_QUADS);
				glVertex2f(range.x1 - barheight, drawup);
				glVertex2f(range.x1 - barheight, drawdown);
				glVertex2f(range.x1, drawdown);
				glVertex2f(range.x1, drawup);
			glEnd();
		}
	}
}
