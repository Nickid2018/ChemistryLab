package com.github.nickid2018.chemistrylab.window;

import static org.lwjgl.glfw.GLFW.GLFW_ARROW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_HAND_CURSOR;
import static org.lwjgl.glfw.GLFW.glfwCreateStandardCursor;

public class Cursor {

	// GLFW Cursora
	public static long ARROW_CURSOR;
	public static long HAND_CURSOR;
	
	public static void init() {
		Cursor.ARROW_CURSOR = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
		Cursor.HAND_CURSOR = glfwCreateStandardCursor(GLFW_HAND_CURSOR);
	}

}
