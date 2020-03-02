package com.github.nickid2018.chemistrylab.util;

import java.nio.*;
import org.lwjgl.*;
import org.lwjgl.glfw.*;

import com.github.nickid2018.chemistrylab.*;
import com.github.nickid2018.chemistrylab.window.Window;

public class Mouse {

	private static DoubleBuffer xpos = BufferUtils.createDoubleBuffer(1);
	private static DoubleBuffer ypos = BufferUtils.createDoubleBuffer(1);
	private static double lastX = 0;
	private static double lastY = 0;

	public static double getX() {
		GLFW.glfwGetCursorPos(Window.window, xpos, ypos);
		double ret = xpos.get();
		xpos.clear();
		return ret;
	}

	public static double getY() {
		GLFW.glfwGetCursorPos(Window.window, xpos, ypos);
		double ret = ypos.get();
		ypos.clear();
		return ret;
	}

	public static double getDX() {
		double ret = getX() - lastX;
		lastX = ret + lastX;
		return ret;
	}

	public static double getDY() {
		double ret = getY() - lastY;
		lastY = ret + lastY;
		return ret;
	}

	// Mouse Click
	public static long lastTime;
	public static long lastClick = -1;
}
