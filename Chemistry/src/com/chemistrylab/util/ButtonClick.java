package com.chemistrylab.util;

@FunctionalInterface
public interface ButtonClick {

	public void click(int button, int action, int mods);
}
