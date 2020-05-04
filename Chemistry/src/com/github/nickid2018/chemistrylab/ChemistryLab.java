package com.github.nickid2018.chemistrylab;

import java.io.*;
import com.jme3.app.*;
import org.apache.log4j.*;
import com.jme3.system.*;
import com.simsilica.lemur.*;
import com.google.common.eventbus.*;
import com.github.nickid2018.chemistrylab.init.*;
import com.github.nickid2018.chemistrylab.util.*;
import com.github.nickid2018.chemistrylab.crash.*;
import com.github.nickid2018.chemistrylab.debug.*;
import com.github.nickid2018.chemistrylab.resource.*;

public class ChemistryLab extends SimpleApplication {

	// Logger Of Main Thread
	public static final Logger logger = Logger.getLogger("Main Looper");

	// Memory Manager
	public static final Runtime RUNTIME = Runtime.getRuntime();

	// Version
	public static final Version VERSION = Version.fromString("1.0.0 indev");

	// Engine EventBus
	public static final EventBus ENGINE_EVENTBUS = new EventBus("Engine");

	// Program Status
	public static boolean INITED = false;

	public static void main(String[] args) {
		Thread.currentThread().setName("Main Thread");
		PropertyConfigurator.configure(ChemistryLab.class.getResourceAsStream("/assets/log4j.properties"));
		ResourceManager.loadPacks();
		try {
			ProgramOptions.init(args);
		} catch (IOException e) {
		}
		Thread.setDefaultUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler());
		if (ProgramOptions.getCommandSwitchBool("-commandConsole"))
			CommandLineInputer.init();
		try {
			I18N.load();
		} catch (Exception e) {
			logger.error("Cannot load I18N.", e);
		}
		ENGINE_EVENTBUS.register(new FatalErrorListener());
		ChemistryLab application = new ChemistryLab();
		application.setSettings(getDefaultSettings());
		application.setDisplayStatView(false);
		application.setShowSettings(false);
		application.setPauseOnLostFocus(false);
		application.start();
	}

	public static AppSettings getDefaultSettings() {
		AppSettings set = new AppSettings(true);
		set.setTitle("Chemistry Lab");
		set.setResizable(true);
		set.setVSync(ProgramOptions.getWindowOptionBool("vsync"));
		set.setWidth(ProgramOptions.getWindowOptionInt("width"));
		set.setHeight(ProgramOptions.getWindowOptionInt("height"));
		set.setFrequency(ProgramOptions.getWindowOptionInt("maxfps"));
		set.setFullscreen(ProgramOptions.getWindowOptionBool("fullscreen"));
		return set;
	}

	@Override
	public void simpleInitApp() {
		GuiGlobals.initialize(this);
	}
}
