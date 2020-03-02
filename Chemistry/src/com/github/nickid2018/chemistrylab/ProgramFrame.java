package com.github.nickid2018.chemistrylab;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.PropertyConfigurator;
import org.hyperic.sigar.Sigar;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.renderer.Renderer;

import com.github.nickid2018.chemistrylab.eventbus.*;
import com.github.nickid2018.chemistrylab.init.InitLoader;
import com.github.nickid2018.chemistrylab.layer.*;
import com.github.nickid2018.chemistrylab.render.CommonRender;
import com.github.nickid2018.chemistrylab.render.GLRender;
import com.github.nickid2018.chemistrylab.sound.SoundSystem;
import com.github.nickid2018.chemistrylab.util.*;
import com.github.nickid2018.chemistrylab.value.MyHotKeys;
import com.github.nickid2018.chemistrylab.window.*;

public class ProgramFrame {
	
	public static String stack;
	public static String crash;
	
	public static void init() {
		// Configure Log4j
		// Warning: Do Not Use Resource Loader!
		PropertyConfigurator.configure(ChemistryLab.class.getResource("/assets/log4j.properties"));
		// Listen Event
		EventBus.registerListener((e) -> {
			try {
				if (e.equals(I18N.I18N_RELOADED))
					I18N.i18n_reload = true;
				if (e.equals(ChemistryLab.THREAD_FATAL)) {
					ChemistryLab.error = (Throwable) e.getExtra(0);
					ChemistryLab.errorthread = (Thread) e.getExtra(1);
				}
				// Read window properties
				Properties settings = new Properties();
				InputStream is = new FileInputStream("config/window.properties");
				settings.load(is);
				is.close();
				Window.DREAM_WIDTH = Integer.parseInt(settings.getProperty("width", "1280"));
				Window.DREAM_HEIGHT = Integer.parseInt(settings.getProperty("height", "720"));
				FPS.maxFPS = Integer.parseInt(settings.getProperty("maxfps", "100"));
				ChemistryLab.DEFAULT_LOG_FILE = settings.getProperty("logdir", "logs");
				String locale = settings.getProperty("locale", "default");
				I18N.NOW = locale.equals("default") ? I18N.SYSTEM_DEFAULT : Locale.forLanguageTag(locale);
				Window.nowWidth = Window.DREAM_WIDTH;
				Window.nowHeight = Window.DREAM_HEIGHT;
				Window.oldWidth = Window.DREAM_WIDTH;
				Window.oldHeight = Window.DREAM_HEIGHT;
			} catch (Exception e1) {
				ChemistryLab.logger.error("QAQ, the program crashed!", e1);
				// Can't make window, use AWT
				ProgramFrame.onError(e1);
			} catch (Throwable e1) {
				Event ev = ChemistryLab.THREAD_FATAL.clone();
				ev.putExtra(0, e1);
				ev.putExtra(1, Thread.currentThread());
				EventBus.postEvent(ev);
			} 
		});
		
		Thread.currentThread().setName("Render Thread");

		System.setProperty("org.lwjgl.librarypath", ".");
		// Slick Library must use this to adapt LWJGL3.2.3
		Renderer.setRenderer(new GLRender());
		glfwSetErrorCallback(Window.getErrorCallback());
		Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
			System.gc();
			if (Window.inited) {
				Event error = ChemistryLab.THREAD_FATAL.clone();
				error.putExtra(0, e);
				error.putExtra(1, t);
				EventBus.postEvent(error);
			} else {
				// Use AWT
				onError(e);
			}
		});
	}
	public static void updata() {
		ChemistryLab.clearFace();

		// Cover Surface
		ChemistryLab.QUAD.render();
		CommonRender.drawFont("Program Crashed!",
				Window.nowWidth / 2 - CommonRender.winToOthWidth(CommonRender.formatSize(16 * 7)), 20, 32, Color.red);
		CommonRender.drawFont("The crash report has been saved in " + crash, 20,
				40 + CommonRender.winToOthHeight(CommonRender.formatSize(32)), 16, Color.black);
		CommonRender.drawItaticFont(
				"Please report this crash report to https://github.com/Nickid2018/ChemistryLab/", 20,
				(int) (40 + CommonRender.winToOthHeight(CommonRender.formatSize(48))), 16, Color.blue, .32f);
		CommonRender.drawFont("Stack Trace:", 20, 40 + CommonRender.winToOthHeight(CommonRender.formatSize(64)),
				16, Color.red);
		CommonRender.drawFont(stack, 20, 40 + CommonRender.winToOthHeight(CommonRender.formatSize(80)), 16,
				Color.yellow.darker(0.3f));

		ChemistryLab.checkGLError();
		// Update Window
		glfwSwapBuffers(Window.window);
		glfwPollEvents();
	}
	
	public static void clean() {
		// Destory and release resources
				ChemistryLab.release();
	}
	
	public static void gameLoop() {
		
		try {
			// Basic output
			ChemistryLab.logger.info("Chemistry Lab v1.0_INDEV");
			ChemistryLab.logger.info("Made by Nickid2018.Address https://github.com/Nickid2018/");

			// Initialize basic settings
			Sigar.load();
			
			// Cursor
			Cursor.init();

			// Init OpenGL
			RenderInit.init();

			// HotKey Active
			MyHotKeys.init();

			// Init program
			Mouse.lastTime = ChemistryLab.getTime();
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
				if (I18N.i18n_reload) {
					I18N.i18n_reload = false;
					glfwSetWindowTitle(Window.window, I18N.getString("window.title"));
				}

				// Recreate Screen
				if (Window.recreateWindow) {
					Window.recreateWindow = false;
					// Recreate Window
					ChemistryLab.swapFullScreen();
				}

				// TPS and UPS time start
				long startTime = ChemistryLab.getTime();

				// Check Fatal Error
				ChemistryLab.checkErrors();

				// Clear Graph
				ChemistryLab.clearFace();

				// Program frame render
				LayerRender.render();
				FPS.updateFPS();

				ChemistryLab.checkGLError();
				// Update FPS and Window

				glfwSwapBuffers(Window.window);
				FPS.updateUPS();
				ChemistryLab.update();

				// Calculate Sleep Time
				long endTime = ChemistryLab.getTime();
				Thread.sleep(Math.max(0, startTime + targetTime - endTime));
			}
		} catch (Throwable e) {
			ChemistryLab.logger.fatal("QAQ, this program crashed!", e);
			Map<Event.CompleteComparedEvent, Integer> evsnap = EventBus.getNowActiveEvents();
			LayerRender.popLayers();

			if (ChemistryLab.errorthread == null)
				ChemistryLab.errorthread = Thread.currentThread();

			Date date = new Date();

			crash = "crash-report_"
					+ String.format("%tY%tm%td%tH%tM%tS%tL", date, date, date, date, date, date, date) + ".csh.log";
			String l = System.getProperty("line.separator");
			stack = StringUtils.asStack(e);

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
				IOUtils.write("Thread \"" + ChemistryLab.errorthread.getName() + "\"" + l, w);
				IOUtils.write(stack + l, w);
				IOUtils.write("=== T H R E A D S ===" + l, w);
				for (Map.Entry<Thread, StackTraceElement[]> en : Thread.getAllStackTraces().entrySet()) {
					IOUtils.write("Thread \"" + en.getKey().getName() + "\" State:" + en.getKey().getState() + l, w);
					IOUtils.write(StringUtils.asStack(en.getValue()) + l, w);
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
				ChemistryLab.logger.error("Write crash-report error.", e2);
			}

			// Error Screen
			while (!glfwWindowShouldClose(Window.window) && Window.inited) {
				updata();
			}
		}

	}
	
	public static void onError(Throwable t) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		Map<Event.CompleteComparedEvent, Integer> evsnap = EventBus.getNowActiveEvents();
		Date date = new Date();
		String crash = "crash-report_"
				+ String.format("%tY%tm%td%tH%tM%tS%tL", date, date, date, date, date, date, date) + ".csh.log";
		String l = System.getProperty("line.separator");
		String stack = StringUtils.asStack(t);
		File crashrep = new File("crash-reports/" + crash);
		crash = crashrep.getAbsolutePath();
		FileWriter w;
		try {
			crashrep.createNewFile();
			w = new FileWriter(crashrep);
			IOUtils.write("Program had crashed.This report is the detail of this error." + l, w);
			IOUtils.write("Time " + String.format("%tc", date) + l, w);
			IOUtils.write("=== S T A C K T R A C E ===" + l, w);
			IOUtils.write("Thread \"Render Thread\"" + l, w);
			IOUtils.write(stack + l, w);
			IOUtils.write("=== T H R E A D S ===" + l, w);
			for (Map.Entry<Thread, StackTraceElement[]> en : Thread.getAllStackTraces().entrySet()) {
				IOUtils.write("Thread \"" + en.getKey().getName() + "\" State:" + en.getKey().getState() + l, w);
				IOUtils.write(StringUtils.asStack(en.getValue()) + l, w);
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
			IOUtils.write("Operating System:" + System.getProperty("os.name") + " " + System.getProperty("os.version")
					+ " " + System.getProperty("os.arch") + l, w);
			IOUtils.write(
					"Java:" + System.getProperty("java.version") + "\tPath:" + System.getProperty("java.home") + l, w);
			IOUtils.write("Library Path:" + System.getProperty("java.library.path").replaceAll(";", l) + l, w);
			IOUtils.write("LWJGL Version:" + Version.getVersion() + l, w);
			IOUtils.write("GLFW Version: Haven't been loaded" + l, w);
			IOUtils.write("OpenGL Version: Haven't been loaded" + l, w);
			IOUtils.write("OpenAL Version: Haven't been loaded" + l, w);
			w.flush();
			w.close();
		} catch (IOException e2) {
			ChemistryLab.logger.error("Write crash-report error.", e2);
		}
		UIManager.getLookAndFeel().provideErrorFeedback(null);
		JOptionPane.showMessageDialog(null, "An error occurred!\n" + stack.replaceAll("\t", "    ")
				+ "\nThe crash report has been saved in " + crash, "Error", JOptionPane.ERROR_MESSAGE);
		System.exit(-1);
	}
}
