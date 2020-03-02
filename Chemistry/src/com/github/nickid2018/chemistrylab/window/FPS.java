package com.github.nickid2018.chemistrylab.window;

public class FPS {

	// FPS Limit (P.S. The FPS often over the number -_||)
	public static int maxFPS;
	// FPS and UPS
	public static int fps;
	public static int fpsCount;
	public static int ups;
	public static int upsCount;
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

}
