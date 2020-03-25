package com.github.nickid2018.chemistrylab;

import com.github.nickid2018.chemistrylab.util.TimeUtils;
import com.github.nickid2018.chemistrylab.window.*;

public class FPS {

	// FPS Limit (P.S. The FPS often over the number -_||)
	public static int maxFPS;
	// FPS and UPS
	static int fps;
	static int fpsCount;
	static int ups;
	static int upsCount;

	public static void updateFPS() {
		fpsCount++;
	}

	public static void updateUPS() {
		upsCount++;
	}

	public static int getFPS() {
		return fps;
	}

	public static int getUPS() {
		return ups;
	}

	public static void update() {
		if (TimeUtils.getTime() - Mouse.lastTime > 1000) {
			FPS.fps = FPS.fpsCount;
			FPS.fpsCount = 0;
			FPS.ups = FPS.upsCount;
			FPS.upsCount = 0;
			Mouse.lastTime = TimeUtils.getTime();
			// FPS Recording
			DebugSystem.addFPSInfo(FPS.fps);
		}
		// Memory Recording
		if (FPS.fpsCount % 20 == 0)
			DebugSystem.addMemInfo(ChemistryLab.RUNTIME.totalMemory() - ChemistryLab.RUNTIME.freeMemory());
	}
}
