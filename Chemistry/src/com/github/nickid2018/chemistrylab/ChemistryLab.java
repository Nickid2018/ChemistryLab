package com.github.nickid2018.chemistrylab;

import java.io.*;
import java.util.*;
import java.nio.*;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.Desktop;
import org.lwjgl.opengl.*;
import org.hyperic.sigar.*;
import org.apache.log4j.*;
import java.lang.management.*;
import org.apache.commons.io.*;
import com.github.nickid2018.chemistrylab.init.*;
import com.github.nickid2018.chemistrylab.util.*;
import com.github.nickid2018.chemistrylab.layer.*;
import com.github.nickid2018.chemistrylab.sound.*;
import com.github.nickid2018.chemistrylab.render.*;
import com.github.nickid2018.chemistrylab.window.*;
import com.github.nickid2018.chemistrylab.reaction.*;
import com.github.nickid2018.chemistrylab.eventbus.*;
import com.github.nickid2018.chemistrylab.exception.*;

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
		Thread.currentThread().setName("Render Thread");

		System.setProperty("org.lwjgl.librarypath", ".");
		glfwSetErrorCallback(Window.getErrorCallback());
		Thread.setDefaultUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler());

		try {
			// Basic output
			logger.info("Chemistry Lab v1.0_INDEV");
			logger.info("Made by Nickid2018.Address https://github.com/Nickid2018/");
			logger.info("LWJGL Version:" + Version.getVersion());

			// Initialize basic settings
			Sigar.load();
			if (!glfwInit()) {
				logger.error("Unable to initialize GLFW");
				return;
			}
			logger.info("GLFW Version:" + glfwGetVersionString());
			LayerRender.logger.info("Creating window...");
			glfwDefaultWindowHints();
			Window.window = glfwCreateWindow((int) Window.DREAM_WIDTH, (int) Window.DREAM_HEIGHT, "Chemistry Lab", NULL,
					NULL);
			if (Window.window == NULL) {
				glfwTerminate();
				logger.error("Failed to create the GLFW window");
				return;
			}
			glfwSetFramebufferSizeCallback(Window.window, Window.getResizeCallback());
			glfwSetKeyCallback(Window.window, Window.getKeyCallback());
			glfwSetCharCallback(Window.window, Window.getCharCallback());
			glfwSetCharModsCallback(Window.window, Window.getCharModCallback());
			glfwSetMouseButtonCallback(Window.window, Window.getMouseCallback());
			glfwSetCursorPosCallback(Window.window, Window.getCursorPosiCallback());
			glfwSetScrollCallback(Window.window, Window.getScrollCallback());
			glfwMakeContextCurrent(Window.window);

			// Cursor
			Cursor.ARROW_CURSOR = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
			Cursor.HAND_CURSOR = glfwCreateStandardCursor(GLFW_HAND_CURSOR);

			// Init OpenGL
			LayerRender.logger.info("Initializing OpenGL...");
			GL.createCapabilities();
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0, Window.nowWidth, Window.nowHeight, 0, 1, -1);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glViewport(0, 0, (int) Window.nowWidth, (int) Window.nowHeight);

			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			LayerRender.logger.info("OpenGL initialized.Version:" + GL11.glGetString(GL11.GL_VERSION));

			// HotKey Active
			HotKeyMap.addHotKey(GLFW.GLFW_KEY_F3, (scancode, action, mods) -> {
				if (action != GLFW.GLFW_PRESS)
					return;
				HotKeys.f3 = !HotKeys.f3;
				logger.info("Debug Mode:" + (HotKeys.f3 ? "on" : "off"));
				if (HotKeys.f3) {
					EventBus.postEvent(DEBUG_ON);
				} else {
					EventBus.postEvent(DEBUG_OFF);
				}
				HotKeys.f3_with_shift = (mods & GLFW.GLFW_MOD_SHIFT) == GLFW.GLFW_MOD_SHIFT;
				HotKeys.f3_with_ctrl = (mods & GLFW.GLFW_MOD_CONTROL) == GLFW.GLFW_MOD_CONTROL;
			});
			HotKeyMap.addHotKey(GLFW_KEY_F2, (scancode, action, mods) -> {
				if (action != GLFW.GLFW_PRESS)
					return;
				GL11.glReadBuffer(GL11.GL_FRONT);
				int width = (int) Window.nowWidth;
				int height = (int) Window.nowHeight;
				int bpp = 4; // Assuming a 32-bit display with a byte each for
								// red, green, blue, and alpha.
				ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
				GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
				// Run in Thread Manager
				// Concurrent Operation
				ThreadManger.invoke(() -> {
					Date date = new Date();
					File file = new File("screenshot/screenshot_"
							+ String.format("%tY%tm%td%tH%tM%tS%tL", date, date, date, date, date, date, date)
							+ ".png"); // The file to save to.
					String format = "PNG";
					BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
					for (int x = 0; x < width; x++) {
						for (int y = 0; y < height; y++) {
							int i = (x + (width * y)) * bpp;
							int r = buffer.get(i) & 0xFF;
							int g = buffer.get(i + 1) & 0xFF;
							int b = buffer.get(i + 2) & 0xFF;
							image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
						}
					}
					try {
						ImageIO.write(image, format, file);
						// Yes, as you see, you are blind!
						MessageBoard.INSTANCE.addMessage(
								new Message().addMessageEntry(new MessageEntry(I18N.getString("screenshot.success")))
										.addMessageEntry(new MessageEntry(file.getAbsolutePath()).setUnderline(true)
												.setClickEvent((button, action2, mods2) -> {
													if (button == 0 && isSystemClickLegal(100)
															&& Desktop.isDesktopSupported()) {
														try {
															Desktop.getDesktop().open(file);
														} catch (Exception e) {
														}
													}
												})));
					} catch (Exception e) {
//						MessageBoard.INSTANCE.addMessage(new Message().addMessageEntry(
//								new MessageEntry(I18N.getString("screenshot.failed")).setColor(Color.red)));
					}
				});
			});
			HotKeyMap.addHotKey(GLFW_KEY_F11, (scancode, action, mods) -> {
				if (action != GLFW.GLFW_PRESS)
					return;
				// Send Recreate Request
				// P.S. Recreate cannot be run to callback
				HotKeys.fullScreen = !HotKeys.fullScreen;
				logger.info("Fullscreen:" + (HotKeys.fullScreen ? "on" : "off"));
				Window.recreateWindow = true;
			});

			// Init program
			Mouse.lastTime = getTime();
			InitLoader.logger.info("Start load resources.");
			ResourceManager.loadPacks();
			ResourceManager.logger.info("Loaded Resource Packs.");
			InitLoader.init();

			glfwSetWindowTitle(Window.window, I18N.getString("window.title"));

			// Sound System
			SoundSystem.init();

			// Ticker start
			Ticker.init();

			// Add background layer & expand handle
			LayerRender.pushLayer(new Background());
			LayerRender.pushLayer(MessageBoard.INSTANCE);
			LayerRender.pushLayer(new ExpandBar());

			Window.inited = true;

			// Test Start Please From This(For Resource Needing)

			// Test End

			// Main loop of program
			while (!glfwWindowShouldClose(Window.window)) {

				long targetTime = 1000L / FPS.maxFPS;

				// Status Changed
				if (I18N.i18nReload) {
					I18N.i18nReload = false;
					glfwSetWindowTitle(Window.window, I18N.getString("window.title"));
				}

				// Recreate Screen
				if (Window.recreateWindow) {
					Window.recreateWindow = false;
					// Recreate Window
					Window.swapFullScreen();
				}

				// TPS and UPS time start
				long startTime = getTime();

				// Check Fatal Error
				checkErrors();

				// Clear Graph
				Window.clearFace();

				// Program frame render
				LayerRender.render();
				updateFPS();

				checkGLError();
				// Update FPS and Window

				glfwSwapBuffers(Window.window);
				updateUPS();
				update();

				// Calculate Sleep Time
				long endTime = getTime();
				Thread.sleep(Math.max(0, startTime + targetTime - endTime));
			}
		} catch (Throwable e) {
			logger.fatal("QAQ, this program crashed!", e);
			Map<Event.CompleteComparedEvent, Integer> evsnap = EventBus.getNowActiveEvents();
			LayerRender.popLayers();

			if (errorthread == null)
				errorthread = Thread.currentThread();

			Date date = new Date();

			String crash = "crash-report_"
					+ String.format("%tY%tm%td%tH%tM%tS%tL", date, date, date, date, date, date, date) + ".csh.log";
			String l = System.getProperty("line.separator");
			String stack = asStack(e);

			// Write crash log

			File crashrep = new File("crash-reports/" + crash);
			crash = crashrep.getAbsolutePath();
			FileWriter w;
			try {
				crashrep.createNewFile();
				w = new FileWriter(crashrep);
				IOUtils.write("Program had crashed.This report is the detail of this error." + l, w);
				IOUtils.write("Time " + String.format("%tc", date) + l, w);
				IOUtils.write("=== S T A C K T R A C E ===" + l, w);
				IOUtils.write("Thread \"" + errorthread.getName() + "\"" + l, w);
				IOUtils.write(stack + l, w);
				IOUtils.write("=== T H R E A D S ===" + l, w);
				for (Map.Entry<Thread, StackTraceElement[]> en : Thread.getAllStackTraces().entrySet()) {
					IOUtils.write("Thread \"" + en.getKey().getName() + "\" State:" + en.getKey().getState() + l, w);
					IOUtils.write(asStack(en.getValue()) + l, w);
				}
				IOUtils.write("=== E V E N T B U S ===" + l, w);
				if (Window.inited) {
					IOUtils.write("Active Events:" + l, w);
					for (Map.Entry<Event.CompleteComparedEvent, Integer> en : evsnap.entrySet()) {
						IOUtils.write(en.getKey() + " " + en.getValue() + l, w);
					}
				} else {
					IOUtils.write("EventBus hasn't been initialized." + l, w);
				}
				IOUtils.write("=== S Y S T E M ===" + l, w);
				IOUtils.write("Operating System:" + System.getProperty("os.name") + " "
						+ System.getProperty("os.version") + " " + System.getProperty("os.arch") + l, w);
				IOUtils.write(
						"Java:" + System.getProperty("java.version") + "\tPath:" + System.getProperty("java.home") + l,
						w);
				IOUtils.write("Library Path: " + System.getProperty("java.library.path").replaceAll(";", l) + l, w);
				IOUtils.write("LWJGL Version: " + Version.getVersion() + l, w);
				IOUtils.write("GLFW Version: " + GLFW.glfwGetVersionString() + l, w);
				IOUtils.write("OpenGL Version: " + GL11.glGetString(GL11.GL_VERSION) + l, w);
				IOUtils.write(
						"OpenAL Version: " + SoundSystem.getALVersion() + " ALC " + SoundSystem.getALCVersion() + l, w);
				w.flush();
				w.close();
			} catch (IOException e2) {
				logger.error("Write crash-report error.", e2);
			}

			// Error Screen
			while (!glfwWindowShouldClose(Window.window) && Window.inited) {

				Window.clearFace();

				// Cover Surface
				QUAD.render();
//				CommonRender.drawFont("Program Crashed!",
//						Window.nowWidth / 2 - CommonRender.winToOthWidth(CommonRender.formatSize(16 * 7)), 20, 32,
//						Color.red);
//				CommonRender.drawFont("The crash report has been saved in " + crash, 20,
//						40 + CommonRender.winToOthHeight(CommonRender.formatSize(32)), 16, Color.black);
//				CommonRender.drawItaticFont(
//						"Please report this crash report to https://github.com/Nickid2018/ChemistryLab/", 20,
//						(int) (40 + CommonRender.winToOthHeight(CommonRender.formatSize(48))), 16, Color.blue, .32f);
//				CommonRender.drawFont("Stack Trace:", 20, 40 + CommonRender.winToOthHeight(CommonRender.formatSize(64)),
//						16, Color.red);
//				CommonRender.drawFont(stack, 20, 40 + CommonRender.winToOthHeight(CommonRender.formatSize(80)), 16,
//						Color.yellow.darker(0.3f));

				checkGLError();
				// Update Window
				glfwSwapBuffers(Window.window);
				glfwPollEvents();
			}
		}

		// Destory and release resources
		release();
	}

	/**
	 * Get textures
	 * 
	 * @return Textures
	 */
	public static Textures getTextures() {
		return InitLoader.getTextureLoader().getTextures();
	}

	/**
	 * Get the time in milliseconds
	 *
	 * @return The system time in milliseconds
	 */
	public static long getTime() {
		return MathHelper.fastFloor(glfwGetTime() * 1000);
	}

	public static void updateFPS() {
		FPS.fpsCount++;
	}

	public static void updateUPS() {
		FPS.upsCount++;
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

	public static int getFPS() {
		return FPS.fps;
	}

	public static int getUPS() {
		return FPS.ups;
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
	private static Throwable error;
	private static Thread errorthread;

	private static void checkErrors() throws Throwable {
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
//		if (getTextures() != null)
//			getTextures().releaseAll();
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

	// Print Throwable as String
	public static String asStack(Throwable e) {
		String l = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder(e.toString().replace("\n", l) + l);
		StackTraceElement[] sks = e.getStackTrace();
		for (StackTraceElement ste : sks) {
			sb.append("\tat " + ste + l);
		}
		Throwable t = e;
		while ((t = t.getCause()) != null) {
			sb.append("Caused by:" + t + l);
			StackTraceElement[] sks0 = t.getStackTrace();
			int i = 0;
			for (StackTraceElement ste : sks0) {
				StackTraceElement ate = null;
				try {
					ate = sks[sks.length - (sks0.length - i)];
				} catch (Exception e2) {
				}
				if (ste.equals(ate)) {
					sb.append("\t... " + (sks0.length - i) + " more" + l);
					break;
				}
				sb.append("\tat " + ste + l);
				i++;
			}
		}
		Throwable[] ss = e.getSuppressed();
		for (Throwable s : ss) {
			sb.append("Suppressed:" + s + l);
			StackTraceElement[] sks0 = s.getStackTrace();
			int i = 0;
			for (StackTraceElement ste : sks0) {
				StackTraceElement ate = null;
				try {
					ate = sks[sks.length - (sks0.length - i)];
				} catch (Exception e2) {
				}
				if (ste.equals(ate)) {
					sb.append("\t... " + (sks0.length - i) + " more" + "l");
					break;
				}
				sb.append("\tat " + ste + l);
			}
		}
		return sb.deleteCharAt(sb.length() - 1).toString();
	}

	// Stack as StackTraceElement
	public static String asStack(StackTraceElement[] es) {
		String l = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement ste : es) {
			sb.append("\tat " + ste + l);
		}
		return sb.toString();
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

	static {
		// Configure Log4j
		// Warning: Do Not Use Resource Loader!
		PropertyConfigurator.configure(ChemistryLab.class.getResource("/assets/log4j.properties"));
		// Listen Event
		EventBus.registerListener((e) -> {
			try {
				if (e.equals(I18N.I18N_RELOADED))
					I18N.i18nReload = true;
				if (e.equals(THREAD_FATAL)) {
					error = (Throwable) e.getExtra(0);
					errorthread = (Thread) e.getExtra(1);
				}
			} catch (Throwable e1) {
				Event ev = THREAD_FATAL.clone();
				ev.putExtra(0, e1);
				ev.putExtra(1, Thread.currentThread());
				EventBus.postEvent(ev);
			}
		});
		Window.readProperties();
	}
}
