package com.github.nickid2018.chemistrylab.util;

import java.util.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import com.github.nickid2018.chemistrylab.*;
import com.github.nickid2018.chemistrylab.render.*;
import com.github.nickid2018.chemistrylab.window.*;

public class Message implements Cloneable {

	private final long spawnTime = ChemistryLab.getTime();
	private final ArrayList<MessageEntry> entries;
	private long surviveTime;
	private long disappearTime = 5000;
	private boolean haveEvent = false;

	public Message() {
		this(15000);
	}

	public Message(int surviveTime) {
		this(surviveTime, new ArrayList<>());
	}

	public Message(long surviveTime, ArrayList<MessageEntry> en) {
		this.surviveTime = surviveTime;
		entries = en;
	}

	public Message addMessageEntry(MessageEntry en) {
		entries.add(en);
		if (en.getClickEvent() != null)
			haveEvent = true;
		return this;
	}

	public long getSpawnTime() {
		return spawnTime;
	}

	public long getSurviveTime() {
		return surviveTime;
	}

	public Message setSurviveTime(long surviveTime) {
		this.surviveTime = surviveTime;
		return this;
	}

	public long getDisappearTime() {
		return disappearTime;
	}

	public Message setDisappearTime(long disappearTime) {
		this.disappearTime = disappearTime;
		return this;
	}

	public boolean isValid() {
		return ChemistryLab.getTime() - spawnTime < surviveTime;
	}

	public void onMouseEvent(int button, int action, int mods) {
		if (action != GLFW.GLFW_PRESS)
			return;
		double x = Mouse.getX();
		int l = 0;
		for (MessageEntry en : entries) {
//			l += CommonRender.calcTextWidth(en.getText(), 16);
//			if (l > x && haveEvent) {
//				if (en.getClickEvent() != null)
//					en.getClickEvent().click(button, action, mods);
//				break;
//			}
		}
	}

	public void render(float y) {
		float x = 0;
		float lastx = 0;
		float percent = Math.min(-(ChemistryLab.getTime() - spawnTime - surviveTime) / (float) disappearTime, 1);
		for (MessageEntry en : entries) {
//			Color nowa = en.getColor();
//			Color now = new Color(nowa.r, nowa.g, nowa.b, percent * nowa.a);
//			if (en.isItatic()) {
//				x = CommonRender.drawItaticFont(en.getText(), x, y, 16, now, en.getShear());
//			} else {
//				x = CommonRender.drawFont(en.getText(), x, y, 16, now);
//			}
			if (en.isUnderline()) {
				GL11.glBegin(GL11.GL_LINE_STRIP);
				GL11.glVertex2f(lastx, y + 16);
				GL11.glVertex2f(x, y + 16);
				GL11.glEnd();
			}
			lastx = x;
		}
	}

	public void onCursorIn() {
		if (haveEvent) {
			GLFW.glfwSetCursor(Window.window, Cursor.HAND_CURSOR);
		}
	}

	public void onCursorOut() {
		GLFW.glfwSetCursor(Window.window, Cursor.ARROW_CURSOR);
	}

	@Override
	public Message clone() {
		try {
			return (Message) super.clone();
		} catch (CloneNotSupportedException e) {
			// That's impossible!!
			return null;
		}
	}
}
