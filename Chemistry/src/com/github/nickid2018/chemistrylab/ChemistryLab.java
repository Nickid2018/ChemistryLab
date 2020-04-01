package com.github.nickid2018.chemistrylab;

import java.io.*;
import org.apache.log4j.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl.*;
import com.github.nickid2018.chemistrylab.init.*;

public class ChemistryLab {

	// Logger Of Main Thread
	public static final Logger logger = Logger.getLogger("Main Looper");

	// Memory Manager
	public static final Runtime RUNTIME = Runtime.getRuntime();

	// Version
	public static final com.github.nickid2018.chemistrylab.util.Version VERSION = com.github.nickid2018.chemistrylab.util.Version
			.fromString("1.0.0 indev");

	public static Application APPLICATION;

	public static void main(String[] args) {
		Thread.currentThread().setName("Main Thread");
		try {
			ProgramOptions.init(args);
		} catch (IOException e) {
		}

		APPLICATION = new LwjglApplication(new GameChemistry(), getConfig());
	}

	private static LwjglApplicationConfiguration getConfig() {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Chemistry Lab";
		config.resizable = true;
		config.vSyncEnabled = true;
		config.width = ProgramOptions.getWindowOptionInt("width");
		config.height = ProgramOptions.getWindowOptionInt("height");
		config.fullscreen = ProgramOptions.getWindowOptionBool("fullscreen");
		return config;
	}
}
