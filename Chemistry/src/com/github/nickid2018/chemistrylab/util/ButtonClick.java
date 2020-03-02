package com.github.nickid2018.chemistrylab.util;

@FunctionalInterface
public interface ButtonClick {

	public void click(int button, int action, int mods);
}
