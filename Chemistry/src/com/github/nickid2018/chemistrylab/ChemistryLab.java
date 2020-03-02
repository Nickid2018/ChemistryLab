package com.github.nickid2018.chemistrylab;

import java.io.*;
import java.util.*;
import java.nio.*;
import org.lwjgl.*;
import javax.swing.*;
import org.lwjgl.glfw.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.Desktop;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import org.hyperic.sigar.*;
import org.apache.log4j.*;
import org.newdawn.slick.*;

import com.github.nickid2018.chemistrylab.eventbus.*;
import com.github.nickid2018.chemistrylab.init.*;
import com.github.nickid2018.chemistrylab.layer.*;
import com.github.nickid2018.chemistrylab.reaction.*;
import com.github.nickid2018.chemistrylab.render.*;
import com.github.nickid2018.chemistrylab.sound.*;
import com.github.nickid2018.chemistrylab.util.*;
import com.github.nickid2018.chemistrylab.value.MyHotKeys;
import com.github.nickid2018.chemistrylab.window.Cursor;
import com.github.nickid2018.chemistrylab.window.FPS;
import com.github.nickid2018.chemistrylab.window.Window;

import java.lang.management.*;

import org.apache.commons.io.*;
import org.newdawn.slick.opengl.renderer.Renderer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

public class ChemistryLab {

	// Logger Of Main Thread
	public static final Logger logger = Logger.getLogger("Main Looper");
	public static String DEFAULT_LOG_FILE;

	// Memory Manager
	public static final Runtime RUNTIME = Runtime.getRuntime();
	public static final MemoryMXBean MEMORY = ManagementFactory.getMemoryMXBean();

	// Events
	public static final Event DEBUG_ON = Event.createNewEvent("Debug_On");
	public static final Event DEBUG_OFF = Event.createNewEvent("Debug_Off");
	public static final Event THREAD_FATAL = Event.createNewEvent("Fatal_Error");

	// Quad of cover
	public static FastQuad QUAD;

	public static void main(String[] args) {
		ProgramFrame.init();
		ProgramFrame.gameLoop();
		ProgramFrame.clean();
	}

	/**
	 * Get the time in milliseconds
	 *
	 * @return The system time in milliseconds
	 */
	public static long getTime() {
		return MathHelper.fastFloor(glfwGetTime() * 1000);
	}

	public static void update() {
		glfwPollEvents();
		if (getTime() - Mouse.lastTime > 1000) {
			FPS.fps = FPS.fpsCount;
			FPS.fpsCount = 0;
			FPS.ups = FPS.upsCount;
			FPS.upsCount = 0;
			Mouse.lastTime = getTime();
			// FPS Recording
			DebugSystem.addFPSInfo(FPS.fps);
		}
		// Memory Recording
		if (FPS.fpsCount % 20 == 0)
			DebugSystem.addMemInfo(ChemistryLab.RUNTIME.totalMemory() - ChemistryLab.RUNTIME.freeMemory());
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
		checkClose();
		FPS.updateFPS();
		checkGLError();
		glfwSwapBuffers(Window.window);
		FPS.updateUPS();
		glfwPollEvents();
		update();
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
		PointerBuffer pb = MyHotKeys.fullScreen ? glfwGetMonitors() : null;
		long recreate = pb == null ? NULL : pb.get(0);
		glfwDefaultWindowHints();
		Window.oldWidth = Window.nowWidth;
		Window.oldHeight = Window.nowHeight;
		if (MyHotKeys.fullScreen) {
			Window.window = glfwCreateWindow(CommonRender.TOOLKIT.getScreenSize().width,
					CommonRender.TOOLKIT.getScreenSize().height, I18N.getString("window.title"), recreate, NULL);
			Window.nowWidth = CommonRender.TOOLKIT.getScreenSize().width;
			Window.nowHeight = CommonRender.TOOLKIT.getScreenSize().height;
		} else {
			Window.window = glfwCreateWindow((int) Window.DREAM_WIDTH, (int) Window.DREAM_HEIGHT, I18N.getString("window.title"), recreate,
					NULL);
			Window.nowWidth = Window.DREAM_WIDTH;
			Window.nowHeight = Window.DREAM_HEIGHT;
		}
		if (Window.window == NULL) {
			glfwTerminate();
			logger.error("Failed to recreate the GLFW window");
			return;
		}
		// Callback re-bind
		glfwSetFramebufferSizeCallback(Window.window, Window.getResizeCallback());
		glfwSetKeyCallback(Window.window, Window.getKeyCallback());
		glfwSetCharCallback(Window.window, Window.getCharCallback());
		glfwSetMouseButtonCallback(Window.window, Window.getMouseCallback());
		glfwSetScrollCallback(Window.window, Window.getScrollCallback());
		glfwMakeContextCurrent(Window.window);
		GL.createCapabilities();
		// Re-initialize OpenGL
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, Window.nowWidth, Window.nowHeight, 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glViewport(0, 0, (int) Window.nowWidth, (int) Window.nowHeight);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		// Cursors
		Cursor.ARROW_CURSOR = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
		Cursor.HAND_CURSOR = glfwCreateStandardCursor(GLFW_HAND_CURSOR);
		// Reload Resource
		VertexDataManager.MANAGER.reload();
		InitLoader.getTextureLoader().reloadTexture();
		CommonRender.reloadFontUNI();
		// Send Resize
		LayerRender.windowResize();
	}

	// Check GL Error
	public static void checkGLError() {
		int ret = GL11.glGetError();
		if (ret != GL11.GL_NO_ERROR) {
			String error = "";
			switch (ret) {
			case GL11.GL_INVALID_ENUM:
				error = "Invalid enum(" + ret + ")";
				break;
			case GL11.GL_INVALID_OPERATION:
				error = "Invalid operation(" + ret + ")";
				break;
			case GL11.GL_INVALID_VALUE:
				error = "Invalid value(" + ret + ")";
				break;
			case GL11.GL_STACK_OVERFLOW:
				error = "Stack overflow(" + ret + ")";
				break;
			case GL11.GL_STACK_UNDERFLOW:
				error = "Stack underflow(" + ret + ")";
				break;
			case GL11.GL_OUT_OF_MEMORY:
				error = "Out of memory(" + ret + ")";
				break;
			}
			LayerRender.logger.error("#GL ERROR#" + error);
		}
	}

	// Check Window Close
	public static void checkClose() {
		if (glfwWindowShouldClose(Window.window))
			release();
	}

	// Check Click Legal
	public static boolean isSystemClickLegal(long del) {
		boolean b = getTime() - Mouse.lastClick > del;
		if (b)
			Mouse.lastClick = getTime();
		return b;
	}

	// Check Fatal Error from Other Thread
	public static Throwable error;
	public static Thread errorthread;

	public static void checkErrors() throws Throwable {
		if (error != null)
			throw error;
	}

	/**
	 * Release Resource
	 */
	public static void release() {
		logger.info("Stopping!");
		if (Window.inited)
			try {
				Environment.saveSettings();
			} catch (Exception e) {
				logger.warn("Can't save the settings of Environment!", e);
			}
		try {
			ResourceManager.flushStream();
		} catch (IOException e) {
			logger.warn("Can't flush the resources of program!", e);
		}
		long tt = getTime();
		logger.info("Releasing resources.");
		if (Textures.getTextures() != null)
			Textures.getTextures().releaseAll();
		VertexDataManager.MANAGER.releaseResource();
		ShaderManager.MANAGER.releaseResource();
		glfwDestroyWindow(Window.window);
		if (Window.inited) {
			SoundSystem.stopProgram();
			EventBus.releaseEventBus();
		}
		logger.info("Program stopped.Releasing resources used " + (getTime() - tt) + " milliseconds.");
		glfwTerminate();
		if (Window.inited) {
			// Wait Sound System Run Over
			while (SoundSystem.isAlive()) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}
			}
		}

		System.exit(0);
	}

	// Clear logs
	private static long clearingLogTime = getTime();
	private static PleaseWaitLayer layer = null;

	public static void clearLog() {
		if (getTime() - clearingLogTime < 1000)
			return;
		LayerRender.pushLayer(layer = new PleaseWaitLayer(I18N.getString("dealing.log.clear"), () -> {
			layer.isClickLegal(1);
			File dir = new File(DEFAULT_LOG_FILE);
			File[] todels = dir.listFiles((FilenameFilter) (dir1, name) -> !name.equals("ChemistryLab-Log"));
			for (File del : todels) {
				del.delete();
			}
			clearingLogTime = getTime();
			logger.info("Cleared Log.");
			layer.setSuccess(I18N.getString("sidebar.log.success"));
		}).start());
	}


	/**
	 * Get the class of caller. Hack of Reflection.
	 * 
	 * @return The class of caller
	 */
	public static final Class<?> getCallerClass() {
		// Warning
		// Reflect Class Operation
		// Destination: sun.reflect.Reflection
		// Function to Reflect: getCallerClass(I)Ljava.lang.Class;
		// Function Warning: Deprecated at defined class
		// Version can work: Java 8 (Also can run in Java 6 and 7)
		try {
			Class<?> reflc = Class.forName("sun.reflect.Reflection");
			// Invoke Function Stack:
			// 3: Invoke Stack Want to know
			// 2: Invoke Stack
			// 1: This Function Stack
			// 0: java.lang.Method.invoke
			// Top: sun.reflect.Reflection.getCallerClass Function Stack (Native
			// Method Stack)
			return (Class<?>) (reflc.getMethod("getCallerClass", int.class)).invoke(reflc, 3);
		} catch (Throwable e) {
			// Actually, this function won't throw any error. (Except
			// OutOfMemoryError or StackOverflowError)
			return null;
		}
	}

	// Get Total Memory
	public static final double getTotalMemory() {
		return RUNTIME.maxMemory();
	}
}
