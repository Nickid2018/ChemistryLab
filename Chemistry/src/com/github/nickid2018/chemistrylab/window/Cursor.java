package com.github.nickid2018.chemistrylab.window;

import static org.lwjgl.glfw.GLFW.*;

public class Cursor {

	// GLFW Cursor
	public static long ARROW_CURSOR;
	public static long HAND_CURSOR;

	public static void init() {
		Cursor.ARROW_CURSOR = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
		Cursor.HAND_CURSOR = glfwCreateStandardCursor(GLFW_HAND_CURSOR);
	}
}
