package com.github.nickid2018.chemistrylab;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallbackI;
import org.lwjgl.glfw.GLFWCharModsCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.MemoryUtil;

import com.github.nickid2018.chemistrylab.layer.LayerRender;

public class Window {
	// Window State
	public static boolean inited = false;
	public static boolean recreateWindow = false;

	// Window Settings
	public static float DREAM_WIDTH;
	public static float DREAM_HEIGHT;
	public static float nowWidth;
	public static float nowHeight;
	public static float oldWidth;
	public static float oldHeight;

	// Callback Start!!

	private static final GLFWErrorCallbackI error_callback = (error,
			description) -> ChemistryLab.logger
					.error("GLFW Error: "
							+ APIUtil.apiClassTokens((field, value) -> 0x10000 < value && value < 0x20000, null,
									GLFW.class).get(error)
							+ "(0x" + Integer.toHexString(error) + ")-" + MemoryUtil.memUTF8(description));

	private static final GLFWKeyCallbackI key_callback = (window, key, scancode, action, mods) -> {
		HotKeyMap.activeKey(key, scancode, action, mods);
		LayerRender.postKey(key, scancode, action, mods);
	};

	private static final GLFWCharCallbackI char_callback = (window, codepoint) -> LayerRender.postCharInput(codepoint);

	private static final GLFWCharModsCallbackI char_mod_callback = (window, codepoint, mods) -> LayerRender
			.postModCharInput(codepoint, mods);

	private static final GLFWMouseButtonCallbackI mouse_callback = (window, button, action, mods) -> LayerRender
			.postMouse(button, action, mods);

	private static final GLFWCursorPosCallbackI cursor_posi_callback = (window, xpos, ypos) -> LayerRender
			.postCursorPos(xpos, ypos);

	private static final GLFWScrollCallbackI scroll_callback = (window, xoffset, yoffset) -> LayerRender
			.postScroll(xoffset, yoffset);

	private static final GLFWFramebufferSizeCallbackI resize_callback = (window, width, height) -> {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, width, height, 0, 1, -1);
		GL11.glViewport(0, 0, width, height);
		Window.oldWidth = Window.nowWidth;
		Window.oldHeight = Window.nowHeight;
		Window.nowWidth = width;
		Window.nowHeight = height;
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		LayerRender.windowResize();
	};

	public static GLFWErrorCallbackI getErrorCallback() {
		return error_callback;
	}

	public static GLFWKeyCallbackI getKeyCallback() {
		return key_callback;
	}

	public static GLFWCharCallbackI getCharCallback() {
		return char_callback;
	}

	public static GLFWCharModsCallbackI getCharModCallback() {
		return char_mod_callback;
	}

	public static GLFWMouseButtonCallbackI getMouseCallback() {
		return mouse_callback;
	}

	public static GLFWCursorPosCallbackI getCursorPosiCallback() {
		return cursor_posi_callback;
	}

	public static GLFWScrollCallbackI getScrollCallback() {
		return scroll_callback;
	}

	public static GLFWFramebufferSizeCallbackI getResizeCallback() {
		return resize_callback;
	}

}
