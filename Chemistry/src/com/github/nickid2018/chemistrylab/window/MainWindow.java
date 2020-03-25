package com.github.nickid2018.chemistrylab.window;

import java.io.*;
import java.util.*;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import com.github.nickid2018.chemistrylab.*;
import com.github.nickid2018.chemistrylab.util.*;
import com.github.nickid2018.chemistrylab.init.*;
import com.github.nickid2018.chemistrylab.layer.*;
import com.github.nickid2018.chemistrylab.render.*;
import com.github.nickid2018.chemistrylab.exception.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

public class MainWindow {
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
		MainWindow.oldWidth = MainWindow.nowWidth;
		MainWindow.oldHeight = MainWindow.nowHeight;
		MainWindow.nowWidth = width;
		MainWindow.nowHeight = height;
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

	/**
	 * Clear the screen and depth buffer
	 */
	public static void clearFace() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	/**
	 * Flush Window
	 */
	public static void flush() {
		EngineChemistryLab.checkGLError();
		glfwSwapBuffers(MainWindow.window);
		glfwPollEvents();
	}

	/**
	 * Swap Window
	 * 
	 * @throws Throwable
	 */
	public static void swapFullScreen() throws Throwable {
		glfwSetWindowShouldClose(MainWindow.window, true);
		glfwPollEvents();
		// Release Resource, or the program will break down!
		glfwDestroyWindow(MainWindow.window);
		// Get Monitor Info
		PointerBuffer pb = HotKeys.fullScreen ? glfwGetMonitors() : null;
		long recreate = pb == null ? NULL : pb.get(0);
		glfwDefaultWindowHints();
		oldWidth = nowWidth;
		oldHeight = nowHeight;
		if (HotKeys.fullScreen) {
			MainWindow.window = glfwCreateWindow(CommonRender.TOOLKIT.getScreenSize().width,
					CommonRender.TOOLKIT.getScreenSize().height, I18N.getString("window.title"), recreate, NULL);
			nowWidth = CommonRender.TOOLKIT.getScreenSize().width;
			nowHeight = CommonRender.TOOLKIT.getScreenSize().height;
		} else {
			MainWindow.window = glfwCreateWindow((int) DREAM_WIDTH, (int) DREAM_HEIGHT, I18N.getString("window.title"),
					recreate, NULL);
			nowWidth = DREAM_WIDTH;
			nowHeight = DREAM_HEIGHT;
		}
		if (MainWindow.window == NULL) {
			glfwTerminate();
			ChemistryLab.logger.error("Failed to recreate the GLFW window");
			return;
		}
		// Callback re-bind
		glfwSetFramebufferSizeCallback(MainWindow.window, getResizeCallback());
		glfwSetKeyCallback(MainWindow.window, getKeyCallback());
		glfwSetCharCallback(MainWindow.window, getCharCallback());
		glfwSetMouseButtonCallback(MainWindow.window, getMouseCallback());
		glfwSetScrollCallback(MainWindow.window, getScrollCallback());
		glfwMakeContextCurrent(MainWindow.window);
		GL.createCapabilities();
		// Re-initialize OpenGL
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, nowWidth, nowHeight, 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glViewport(0, 0, (int) nowWidth, (int) nowHeight);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		// Cursors
		Cursor.ARROW_CURSOR = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
		Cursor.HAND_CURSOR = glfwCreateStandardCursor(GLFW_HAND_CURSOR);
		// Reload Resource
		TextureLoader.reloadTexture();
//		CommonRender.reloadFontUNI();
		// Send Resize
		LayerRender.windowResize();
	}

	public static void readProperties() {
		// Read window properties
		try {
			Properties settings = new Properties();
			InputStream is = new FileInputStream("config/window.properties");
			settings.load(is);
			is.close();
			MainWindow.DREAM_WIDTH = Integer.parseInt(settings.getProperty("width", "1280"));
			MainWindow.DREAM_HEIGHT = Integer.parseInt(settings.getProperty("height", "720"));
			FPS.maxFPS = Integer.parseInt(settings.getProperty("maxfps", "100"));
			LogUtils.DEFAULT_LOG_FILE = settings.getProperty("logdir", "logs");
			String locale = settings.getProperty("locale", "default");
			if (locale.equals("default"))
				I18N.NOW = I18N.SYSTEM_DEFAULT;
			else
				I18N.NOW = Locale.forLanguageTag(locale);
			MainWindow.nowWidth = MainWindow.DREAM_WIDTH;
			MainWindow.nowHeight = MainWindow.DREAM_HEIGHT;
			MainWindow.oldWidth = MainWindow.DREAM_WIDTH;
			MainWindow.oldHeight = MainWindow.DREAM_HEIGHT;
		} catch (Exception e1) {
			ChemistryLab.logger.error("QAQ, the program crashed!", e1);
			// Can't make window, use AWT
			DefaultUncaughtExceptionHandler.onError(e1);
		}
	}

}
