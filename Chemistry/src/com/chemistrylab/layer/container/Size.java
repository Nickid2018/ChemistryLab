package com.chemistrylab.layer.container;

public class Size {

	String name;
	int volume;
	int diameter;
	int height;

	@Override
	public String toString() {
		return "Size " + name + "[V=" + volume + ",d=" + diameter + ",h=" + height + "]";
	}
}
