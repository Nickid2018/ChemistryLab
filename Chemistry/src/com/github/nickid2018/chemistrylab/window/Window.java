package com.github.nickid2018.chemistrylab.window;

import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwGetVersionString;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetCharCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCharModsCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.Version;
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

import com.github.nickid2018.chemistrylab.ChemistryLab;
import com.github.nickid2018.chemistrylab.HotKeyMap;
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
	
	// Window Handle
	public static long window;

	// Callback Start!!

	private static final GLFWErrorCallbackI errorCallback = (error, description) ->  {
		ChemistryLab.logger.error("GLFW Error: " + 
				APIUtil.apiClassTokens((field, value) ->
					0x10000 < value && value < 0x20000, null, GLFW.class).get(error)
				+"(0x" + Integer.toHexString(error) + ")-" + MemoryUtil.memUTF8(description));
	};

	private static final GLFWKeyCallbackI keyCallback = (window, key, scancode, action, mods) -> {
		HotKeyMap.activeKey(key, scancode, action, mods);
		LayerRender.postKey(key, scancode, action, mods);
	};

	private static final GLFWCharCallbackI charCallback = (window, codepoint) -> LayerRender.postCharInput(codepoint);

	private static final GLFWCharModsCallbackI charModCallback = 
			(window, codepoint, mods) -> LayerRender.postModCharInput(codepoint, mods);

	private static final GLFWMouseButtonCallbackI mouse_callback = 
			(window, button, action, mods) -> LayerRender.postMouse(button, action, mods);

	private static final GLFWCursorPosCallbackI cursorPosCallback = 
			(window, xpos, ypos) -> LayerRender.postCursorPos(xpos, ypos);

	private static final GLFWScrollCallbackI scrollCallback = 
			(window, xoffset, yoffset) -> LayerRender.postScroll(xoffset, yoffset);

	private static final GLFWFramebufferSizeCallbackI resizeCallback = (window, width, height) -> {
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
		return errorCallback;
	}

	public static GLFWKeyCallbackI getKeyCallback() {
		return keyCallback;
	}

	public static GLFWCharCallbackI getCharCallback() {
		return charCallback;
	}

	public static GLFWCharModsCallbackI getCharModCallback() {
		return charModCallback;
	}

	public static GLFWMouseButtonCallbackI getMouseCallback() {
		return mouse_callback;
	}

	public static GLFWCursorPosCallbackI getCursorPosCallback() {
		return cursorPosCallback;
	}

	public static GLFWScrollCallbackI getScrollCallback() {
		return scrollCallback;
	}

	public static GLFWFramebufferSizeCallbackI getResizeCallback() {
		return resizeCallback;
	}
	
	public static void init() {
		ChemistryLab.logger.info("LWJGL Version:" + Version.getVersion());
		if (!glfwInit()) {
			ChemistryLab.logger.error("Unable to initialize GLFW");
			return;
		}
		ChemistryLab.logger.info("GLFW Version:" + glfwGetVersionString());
		LayerRender.logger.info("Creating window...");
		glfwDefaultWindowHints();
		Window.window = glfwCreateWindow((int) Window.DREAM_WIDTH, (int) Window.DREAM_HEIGHT, "Chemistry Lab", NULL, NULL);
		if (Window.window == NULL) {
			glfwTerminate();
			ChemistryLab.logger.error("Failed to create the GLFW window");
			return;
		}
		glfwSetFramebufferSizeCallback(Window.window, Window.getResizeCallback());
		glfwSetKeyCallback(Window.window, Window.getKeyCallback());
		glfwSetCharCallback(Window.window, Window.getCharCallback());
		glfwSetCharModsCallback(Window.window, Window.getCharModCallback());
		glfwSetMouseButtonCallback(Window.window, Window.getMouseCallback());
		glfwSetCursorPosCallback(Window.window, Window.getCursorPosCallback());
		glfwSetScrollCallback(Window.window, Window.getScrollCallback());
		glfwMakeContextCurrent(Window.window);
	}

}
