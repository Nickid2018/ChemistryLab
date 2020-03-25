package com.github.nickid2018.chemistrylab;

import org.apache.log4j.*;
import java.lang.management.*;
import com.github.mmc1234.minigoldengine.font.*;

public class ChemistryLab {

	// Logger Of Main Thread
	public static final Logger logger = Logger.getLogger("Main Looper");

	// Memory Manager
	public static final Runtime RUNTIME = Runtime.getRuntime();
	public static final MemoryMXBean MEMORY = ManagementFactory.getMemoryMXBean();

	public static final FontRenderer fontRender = new BitmapFontRenderer("assets/textures/font/unicode_");

	public static void main(String[] args) {
		Thread.currentThread().setName("Main Thread");
		new Thread(new EngineChemistryLab()).start();

//			InitLoader.init();
//

		// Main loop of program
		// Status Changed
//				if (I18N.i18nReload) {
//					I18N.i18nReload = false;
//					glfwSetWindowTitle(MainWindow.window, I18N.getString("window.title"));
//				}

		// Recreate Screen
//				if (MainWindow.recreateWindow) {
//					MainWindow.recreateWindow = false;
//					// Recreate Window
////					MainWindow.swapFullScreen();
//				}

		// Cover Surface
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
	}

	// Get Total Memory
	public static final double getTotalMemory() {
		return RUNTIME.maxMemory();
	}
}
