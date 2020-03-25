package com.github.nickid2018.chemistrylab.util;

import org.lwjgl.glfw.*;
import com.github.nickid2018.chemistrylab.init.*;
import com.github.nickid2018.chemistrylab.window.*;

public class TimeUtils {

	/**
	 * Get the time in milliseconds
	 *
	 * @return The system time in milliseconds
	 */
	public static long getTime() {
		return MathHelper.fastFloor(GLFW.glfwGetTime() * 1000);
	}

	// Check Click Legal
	public static boolean isSystemClickLegal(long del) {
		boolean b = getTime() - Mouse.lastClick > del;
		if (b)
			Mouse.lastClick = getTime();
		return b;
	}
}
