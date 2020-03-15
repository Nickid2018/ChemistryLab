	package com.github.nickid2018.chemistrylab.window;

import static org.lwjgl.glfw.GLFW.GLFW_ARROW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_HAND_CURSOR;
import static org.lwjgl.glfw.GLFW.glfwCreateStandardCursor;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetMonitors;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCharCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallbackI;
import org.lwjgl.glfw.GLFWCharModsCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.MemoryUtil;

import com.github.nickid2018.chemistrylab.ChemistryLab;
import com.github.nickid2018.chemistrylab.FPS;
import com.github.nickid2018.chemistrylab.HotKeys;
import com.github.nickid2018.chemistrylab.exception.DefaultUncaughtExceptionHandler;
import com.github.nickid2018.chemistrylab.init.InitLoader;
import com.github.nickid2018.chemistrylab.layer.LayerRender;
import com.github.nickid2018.chemistrylab.render.CommonRender;
import com.github.nickid2018.chemistrylab.render.ShaderManager;
import com.github.nickid2018.chemistrylab.render.VertexDataManager;
import com.github.nickid2018.chemistrylab.util.HotKeyMap;
import com.github.nickid2018.chemistrylab.util.I18N;

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
		ChemistryLab.checkClose();
		ChemistryLab.updateFPS();
		ChemistryLab.checkGLError();
		glfwSwapBuffers(Window.window);
		ChemistryLab.updateUPS();
		glfwPollEvents();
		ChemistryLab.update();
	}

	/**
	 * Swap Window
	 * 
	 * @throws Exception
	 */
	public static void swapFullScreen() throws Exception {
		glfwSetWindowShouldClose(Window.window, true);
		glfwPollEvents();
		// Release Resource, or the program will break down!
		VertexDataManager.MANAGER.releaseResource();
		ShaderManager.MANAGER.releaseResource();
		glfwDestroyWindow(Window.window);
		// Get Monitor Info
		PointerBuffer pb = HotKeys.fullScreen ? glfwGetMonitors() : null;
		long recreate = pb == null ? NULL : pb.get(0);
		glfwDefaultWindowHints();
		oldWidth = nowWidth;
		oldHeight = nowHeight;
		if (HotKeys.fullScreen) {
			Window.window = glfwCreateWindow(CommonRender.TOOLKIT.getScreenSize().width,
					CommonRender.TOOLKIT.getScreenSize().height, I18N.getString("window.title"), recreate, NULL);
			nowWidth = CommonRender.TOOLKIT.getScreenSize().width;
			nowHeight = CommonRender.TOOLKIT.getScreenSize().height;
		} else {
			Window.window = glfwCreateWindow((int) DREAM_WIDTH, (int) DREAM_HEIGHT,
					I18N.getString("window.title"), recreate, NULL);
			nowWidth = DREAM_WIDTH;
			nowHeight = DREAM_HEIGHT;
		}
		if (Window.window == NULL) {
			glfwTerminate();
			ChemistryLab.logger.error("Failed to recreate the GLFW window");
			return;
		}
		// Callback re-bind
		glfwSetFramebufferSizeCallback(Window.window, getResizeCallback());
		glfwSetKeyCallback(Window.window, getKeyCallback());
		glfwSetCharCallback(Window.window, getCharCallback());
		glfwSetMouseButtonCallback(Window.window, getMouseCallback());
		glfwSetScrollCallback(Window.window, getScrollCallback());
		glfwMakeContextCurrent(Window.window);
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
		VertexDataManager.MANAGER.reload();
		InitLoader.getTextureLoader().reloadTexture();
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
			Window.DREAM_WIDTH = Integer.parseInt(settings.getProperty("width", "1280"));
			Window.DREAM_HEIGHT = Integer.parseInt(settings.getProperty("height", "720"));
			FPS.maxFPS = Integer.parseInt(settings.getProperty("maxfps", "100"));
			ChemistryLab.DEFAULT_LOG_FILE = settings.getProperty("logdir", "logs");
			String locale = settings.getProperty("locale", "default");
			if (locale.equals("default"))
				I18N.NOW = I18N.SYSTEM_DEFAULT;
			else
				I18N.NOW = Locale.forLanguageTag(locale);
			Window.nowWidth = Window.DREAM_WIDTH;
			Window.nowHeight = Window.DREAM_HEIGHT;
			Window.oldWidth = Window.DREAM_WIDTH;
			Window.oldHeight = Window.DREAM_HEIGHT;
		} catch (Exception e1) {
			ChemistryLab.logger.error("QAQ, the program crashed!", e1);
			// Can't make window, use AWT
			DefaultUncaughtExceptionHandler.onError(e1);
		}
	}

}
