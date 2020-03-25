package com.github.nickid2018.chemistrylab;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.*;
import org.hyperic.sigar.*;
import com.google.common.eventbus.*;
import com.github.mmc1234.minigoldengine.*;
import com.github.nickid2018.chemistrylab.util.*;
import com.github.nickid2018.chemistrylab.init.*;
import com.github.nickid2018.chemistrylab.init.TextureLoader;
import com.github.mmc1234.minigoldengine.util.*;
import com.github.nickid2018.chemistrylab.layer.*;
import com.github.nickid2018.chemistrylab.sound.*;
import com.github.mmc1234.minigoldengine.event.*;
import com.github.nickid2018.chemistrylab.window.*;
import com.github.nickid2018.chemistrylab.reaction.*;
import com.github.mmc1234.minigoldengine.loader.*;
import com.github.mmc1234.minigoldengine.util.file.*;
import com.github.mmc1234.minigoldengine.render.*;
import com.github.mmc1234.minigoldengine.render.shader.DefaultShaders;
import com.github.mmc1234.minigoldengine.window.*;
import com.github.nickid2018.chemistrylab.exception.*;
import com.github.mmc1234.minigoldengine.util.Timer;
import com.github.mmc1234.minigoldengine.render.sprite.*;

import static org.lwjgl.glfw.GLFW.*;

public class EngineChemistryLab extends EngineBase {

	public static final Logger logger = Logger.getLogger("ChemistryLab");
	public static final EventBus EVENT_BUS = new EventBus("MiniGoldEngine-Main");

	@Override
	public void start() {
		Utils.initLog4j();
		System.setProperty("org.lwjgl.librarypath", ".");
		if (PathUtils.isJar(this.getClass())) {
			ModLoader.loadAll();
		}
		if (!GLFW.glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}
		InitLoader.logger.info("Start load resources.");
		ResourceManager.loadPacks();
		ResourceManager.logger.info("Loaded Resource Packs.");
		try {
			DefaultShaders.VS = IOUtils.toString(ResourceManager.getResourceAsStream("assets/shader/default.vsh"),
					Charset.forName("GB2312"));
			DefaultShaders.FS = IOUtils.toString(ResourceManager.getResourceAsStream("assets/shader/default.fsh"),
					Charset.forName("GB2312"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		timer = new Timer();
		Thread.setDefaultUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler());
		MainWindow.readProperties();
		PropertyConfigurator.configure(ChemistryLab.class.getResource("/assets/log4j.properties"));
		// EventBus Listener Register
		EVENT_BUS.register(new ListenerKey());
		EVENT_BUS.register(new HotKeyMap());
		window = new Window("Chemistry Lab");
		window.create((int) MainWindow.DREAM_WIDTH, (int) MainWindow.DREAM_HEIGHT, false);
		window.show();
		window.onInit();
		MainWindow.window = window.getWindow();
	}

	@Override
	public void preInit() {
		super.preInit();
		Mouse.lastTime = TimeUtils.getTime();
		SoundSystem.init();
		try {
			Sigar.load();
		} catch (SigarException e) {
			logger.error("Error when initializing Sigar", e);
		}
		// Mod PreInit Operation

		// EngineBase.EVENT_BUS.post(new EventTextureLoader());
	}

	@Override
	public void init() {
		Cursor.init();
	}

	@Override
	public void postInit() {
		LayerRender.pushLayer(new Background());
		LayerRender.pushLayer(MessageBoard.INSTANCE);
		LayerRender.pushLayer(new ExpandBar());
		MainWindow.inited = true;
		glfwSetWindowTitle(MainWindow.window, I18N.getString("window.title"));
		Ticker.init();
	}

	@Override
	public void update() {
		FPS.update();
	}

	@Override
	public void gameLoop() {
		double elapsedTime;
		double accumulator = 0f;
		double interval = 1f / this.ups;

		while (!window.isClose()) {
			elapsedTime = timer.getElapsedTime();
			accumulator += elapsedTime;

			while (accumulator >= interval) {
				update();
				accumulator -= interval;
			}

			preRender();
			render();
			FPS.updateFPS();
			renderAfter();
			FPS.updateUPS();
			sync();
		}
	}

	@Override
	public void render() {
		super.render();
		LayerRender.render();
	}

	@Override
	public void renderAfter() {
		super.renderAfter();
		try {
			FatalErrorListener.checkErrors();
		} catch (Throwable e) {
			logger.fatal("QAQ, this program crashed!", e);
			LayerRender.popLayers();
			String crash = ErrorUtils.outputCrashLog(FatalErrorListener.errorthread, FatalErrorListener.error);
			// Error Screen
		}
	}

	@Override
	public void cleanup() {
		if (MainWindow.inited)
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
		long tt = TimeUtils.getTime();
		logger.info("Releasing resources.");
		if (TextureLoader.getTextures() != null)
			TextureLoader.getTextures().releaseAll();
		if (TextureLoader.getAniTextures() != null)
			TextureLoader.getAniTextures().forEach((s, t) -> t.release());
		renderer.cleanup();
		if (MainWindow.inited) {
			SoundSystem.stopProgram();
		}
		logger.info("Program stopped.Releasing resources used " + (TimeUtils.getTime() - tt) + " milliseconds.");
		glfwTerminate();
		if (MainWindow.inited) {
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
}
