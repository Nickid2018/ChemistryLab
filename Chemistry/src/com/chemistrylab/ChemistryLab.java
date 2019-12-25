package com.chemistrylab;

import java.io.*;
import org.lwjgl.*;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;
import org.apache.log4j.*;
import org.hyperic.sigar.*;
import org.newdawn.slick.*;
import com.chemistrylab.init.*;
import com.chemistrylab.layer.*;
import com.chemistrylab.render.*;
import com.chemistrylab.textures.*;
import com.chemistrylab.eventbus.*;

public class ChemistryLab {

	public static final Logger logger 						= 			Logger.getLogger("Main Looper");
	public static final int WIDTH 								= 			1050;
	public static final int HEIGHT 							= 			600;
	public static final DisplayMode defaultMode 	= 			new DisplayMode(WIDTH, HEIGHT);
	public static final DisplayMode fullScreen 			= 			Display.getDesktopDisplayMode();
	public static final String DEFAULT_LOG_FILE		=			"logs";
	
	public static boolean f3 									=			false;
	public static boolean f3_with_shift						=			false;
	public static boolean f3_with_ctrl						=			false;
	public static boolean f11 									= 			false;
	public static int maxFPS										=			60;
	
	private static boolean f11ed 								= 			false;
	
	private static DisplayMode storedDisplayMode;
	private static long lastFPS;
	private static int fps;
	private static int printFPS;

	public static void main(String[] args) {
		Thread.currentThread().setName("Render Thread");
		try {
			//Basic output
			logger.info("Chemistry Lab v1.0_INDEV");
			logger.info("Made by Nickid2018.Address https://github.com/Nickid2018/");
			logger.info("LWJGL Version:"+Sys.getVersion());

			// Initialize basic settings
			Sigar.load();
			LayerRender.logger.info("Creating window...");
			Display.setTitle("Chemistry Lab");
			Display.setDisplayMode(defaultMode);
			Display.setInitialBackground(1, 1, 1);
			Display.setVSyncEnabled(true);
			Display.setResizable(true);
			Display.create();

			// Init OpenGL
			LayerRender.logger.info("Initializing OpenGL...");
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0, WIDTH, HEIGHT, 0, 1, -1);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glViewport(0, 0, WIDTH, HEIGHT);

			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			logger.info("OpenGL initialized.Version:" + GL11.glGetString(GL11.GL_VERSION));
			
			// Init program
			InitLoader.logger.info("Start load resources.");
			InitLoader.init();

			Display.setTitle(I18N.getString("window.title"));

			lastFPS = getTime();

			// Add background layer & expand handle
			LayerRender.pushLayer(new Background());
			LayerRender.pushLayer(new ExpandBar());
			
			//Ticker start
			Ticker.init();

			// Main loop of program
			while (!Display.isCloseRequested()) {
				
				checkResize();
				
				// Input
				pollInput();

				// Program frame render
				LayerRender.render();

				// Update FPS and Window
				updateFPS();
				checkGLError();
				Display.update();
				Display.sync(maxFPS);
			}
		} catch (Throwable e) {
			logger.fatal("Program Crashed!", e);
			
			String crash = "crash-report_" + System.currentTimeMillis() / 1000 + ".csh.log";
			String stack = asStack(e);
			
			while (!Display.isCloseRequested()) {
				
				checkResize();
				clearFace();
				
				while (Keyboard.next()) {
					if (Keyboard.getEventKeyState() && Keyboard.isKeyDown(Keyboard.KEY_F11)) {
						f11 = !f11;
						logger.info("Fullscreen:" + (f11 ? "on" : "off"));
						f11ed = true;
						if (f11)
							try {
								storedDisplayMode = Display.getDisplayMode();
								Display.setDisplayModeAndFullscreen(fullScreen);
								GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
							} catch (LWJGLException e1) {
								e.printStackTrace();
							}
						else
							try {
								Display.setDisplayMode(storedDisplayMode);
								GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
							} catch (LWJGLException e1) {
								e.printStackTrace();
							}
					}
				}

				CommonRender.drawFont("Program Crashed!", WIDTH/2-CommonRender.winToOthWidth(16 * 7), 20, 32, Color.red);
				CommonRender.drawFont("The crash report has been saved in " + crash, 20, 40 + CommonRender.winToOthHeight(32), 16, Color.black);
				CommonRender.drawItaticFont("Please report this crash report to https://github.com/Nickid2018/", 20, (int) (40 + CommonRender.winToOthHeight(48)), 16, Color.blue, .32f);
				CommonRender.drawFont("Stack Trace:", 20, 40 + CommonRender.winToOthHeight(64), 16, Color.red);
				CommonRender.drawFont(stack, 20, 40 + CommonRender.winToOthHeight(80), 16, Color.yellow.darker(0.3f));
				
				// Update Window
				Display.update();
				Display.sync(maxFPS);
			}
		}

		// Destory and release resources
		release();
	}

	public static Textures getTextures() {
		return InitLoader.getTextureLoader().getTextures();
	}

	/**
	 * Get the time in milliseconds
	 *
	 * @return The system time in milliseconds
	 */
	public static long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}

	/**
	 * Calculate the FPS
	 */
	public static void updateFPS() {
		if (getTime() - lastFPS > 1000) {
			printFPS = fps;
			DebugSystem.addFPSInfo(printFPS);
			fps = 0; // reset the FPS counter
			lastFPS += 1000; // add one second
		}
		if(fps%12==0)
			DebugSystem.addMemInfo(CommonRender.RUNTIME.totalMemory() - CommonRender.RUNTIME.freeMemory());
		fps++;
	}

	public static int getFPS() {
		return printFPS;
	}

	/**
	 * Clear the screen and depth buffer
	 */
	public static void clearFace() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}
	
	public static void flush(){
		checkResize();
		pollInput();
		checkGLError();
		checkClose();
	}
	
	public static void checkGLError(){
		int ret=GL11.glGetError();
		if(ret!=GL11.GL_NO_ERROR){
			String error="";
			switch (ret) {
			case GL11.GL_INVALID_ENUM:
				error="Invalid enum("+ret+")";
				break;
			case GL11.GL_INVALID_OPERATION:
				error="Invalid operation("+ret+")";
				break;
			case GL11.GL_INVALID_VALUE:
				error="Invalid value("+ret+")";
				break;
			case GL11.GL_STACK_OVERFLOW:
				error="Stack overflow("+ret+")";
				break;
			case GL11.GL_STACK_UNDERFLOW:
				error="Stack underflow("+ret+")";
				break;
			case GL11.GL_OUT_OF_MEMORY:
				error="Out of memory("+ret+")";
				break;
			}
			LayerRender.logger.error("#GL ERROR#"+error);
		}
	}

	public static void checkClose(){
		if(Display.isCloseRequested())release();
	}
	
	public static void checkResize(){
		if (Display.wasResized() || f11ed) {
			f11ed = false;
			GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
			LayerRender.windowResize();
		}
	}

	public static void pollInput() {
		// For mouse
		LayerRender.postMouse();
		// For keyboard
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState() && Keyboard.isKeyDown(Keyboard.KEY_F3)) {
				f3 = !f3;
				logger.info("Debug Mode:" + (f3 ? "on" : "off"));
				f3_with_shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
				f3_with_ctrl = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
			}
			if (Keyboard.getEventKeyState() && Keyboard.isKeyDown(Keyboard.KEY_F11)) {
				f11 = !f11;
				logger.info("Fullscreen:" + (f11 ? "on" : "off"));
				f11ed = true;
				if (f11)
					try {
						storedDisplayMode = Display.getDisplayMode();
						Display.setDisplayModeAndFullscreen(fullScreen);
						GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
					} catch (LWJGLException e) {
						e.printStackTrace();
					}
				else
					try {
						Display.setDisplayMode(storedDisplayMode);
						GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
					} catch (LWJGLException e) {
						e.printStackTrace();
					}
			}
			LayerRender.postKey();
		}
	}
	
	public static void release(){
		logger.info("Stopping!");
		long tt = getTime();
		logger.info("Releasing resources.");
		if (getTextures() != null)
			getTextures().releaseAll();
		Display.destroy();
		logger.info("Game stopped.Releasing resources used " + (ChemistryLab.getTime() - tt) + " milliseconds.");
		System.exit(0);
	}
	
	private static long clearingLogTime=getTime();
	public static void clearLog(){
		if(getTime()-clearingLogTime<1000)return;
		LayerRender.pushLayer(new PleaseWaitLayer(I18N.getString("dealing.log.clear"), ()->{
			File dir=new File(DEFAULT_LOG_FILE);
			File[] todels=dir.listFiles((FilenameFilter) (dir1, name) -> !name.equals("ChemistryLab-Log"));
			for(File del:todels){
				del.delete();
			}
			clearingLogTime=getTime();
			logger.info("Cleared Log.");
		}).start());
	}
	
	public static String asStack(Throwable e){
		StringBuilder sb=new StringBuilder(e+"\n");
		StackTraceElement[] sks = e.getStackTrace();
		for (StackTraceElement ste : sks) {
			sb.append("\tat " + ste+"\n");
		}
		Throwable t = e;
		while ((t = t.getCause()) != null) {
			sb.append("Caused by:" + t+"\n");
			StackTraceElement[] sks0 = t.getStackTrace();
			int i = 0;
			for (StackTraceElement ste : sks0) {
				StackTraceElement ate = null;
				try {
					ate = sks[sks.length - (sks0.length - i)];
				} catch (Exception e2) {
				}
				if (ste.equals(ate)) {
					sb.append("\t... " + (sks0.length - i) + " more\n");
					break;
				}
				sb.append("\tat " + ste+"\n");
				i++;
			}
		}
		Throwable[] ss = e.getSuppressed();
		for (Throwable s : ss) {
			sb.append("Suppressed:" + s+"\n");
			StackTraceElement[] sks0 = s.getStackTrace();
			int i = 0;
			for (StackTraceElement ste : sks0) {
				StackTraceElement ate = null;
				try {
					ate = sks[sks.length - (sks0.length - i)];
				} catch (Exception e2) {
				}
				if (ste.equals(ate)) {
					sb.append("\t... " + (sks0.length - i) + " more\n");
					break;
				}
				sb.append("\tat " + ste+"\n");
			}
		}
		return sb.deleteCharAt(sb.length()-1).toString();
	}

	static {
		PropertyConfigurator.configure(ChemistryLab.class.getResource("/assets/log4j.properties"));
	}
}
