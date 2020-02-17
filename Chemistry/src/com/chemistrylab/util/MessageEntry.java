package com.chemistrylab.util;

import org.newdawn.slick.*;

public class MessageEntry {

	private String text = "";
	private Color color = Color.white;
	private float shear = 0.35f;
	private ButtonClick clickEvent = (button, action, mods) -> {};
	private boolean itatic = false;
	private boolean underline = false;
	private Color underlinecolor = Color.white;

	public MessageEntry(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public MessageEntry setText(String text) {
		this.text = text;
		return this;
	}

	public Color getColor() {
		return color;
	}

	public MessageEntry setColor(Color color) {
		this.color = color;
		return this;
	}

	public boolean isItatic() {
		return itatic;
	}

	public MessageEntry setItatic(boolean itatic) {
		this.itatic = itatic;
		return this;
	}

	public float getShear() {
		return shear;
	}

	public MessageEntry setShear(float shear) {
		this.shear = shear;
		return this;
	}

	public boolean isUnderline() {
		return underline;
	}

	public MessageEntry setUnderline(boolean underline) {
		this.underline = underline;
		return this;
	}

	public Color getUnderlineColor() {
		return underlinecolor;
	}

	public MessageEntry setUnderlineColor(Color underlinecolor) {
		this.underlinecolor = underlinecolor;
		return this;
	}

	public ButtonClick getClickEvent() {
		return clickEvent;
	}

	public MessageEntry setClickEvent(ButtonClick clickEvent) {
		this.clickEvent = clickEvent;
		return this;
	}
}
