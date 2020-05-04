package com.github.nickid2018.chemistrylab.init;

import java.io.*;
import java.util.*;
import com.google.common.collect.*;

public class ProgramOptions {

	private static Properties WINDOW_PROPERTIES;
	private static Properties COMMAND_SWITCHES;

	public static void init(String[] commandArgs) throws IOException {
		// Read: window.properties
		WINDOW_PROPERTIES = new Properties();
		WINDOW_PROPERTIES.load(new FileInputStream("config/window.properties"));
		// Commad switches
		COMMAND_SWITCHES = new Properties();
		Lists.newArrayList(commandArgs).forEach(sw -> COMMAND_SWITCHES.put(sw.split(":")[0], sw.split(":", 2)[1]));
	}

	public static String getWindowOption(String key) {
		return WINDOW_PROPERTIES.getProperty(key);
	}

	public static String getWindowOption(String key, String defaultValue) {
		return WINDOW_PROPERTIES.getProperty(key, defaultValue);
	}

	public static int getWindowOptionInt(String key) {
		return Integer.parseInt(getWindowOption(key, "0"));
	}

	public static boolean getWindowOptionBool(String key) {
		return Boolean.valueOf(getWindowOption(key, "false"));
	}

	public static String getCommandSwitch(String key) {
		return COMMAND_SWITCHES.getProperty(key);
	}

	public static String getCommandSwitch(String key, String defaultValue) {
		return COMMAND_SWITCHES.getProperty(key, defaultValue);
	}

	public static int getCommandSwitchInt(String key) {
		return Integer.parseInt(getCommandSwitch(key, "0"));
	}

	public static boolean getCommandSwitchBool(String key) {
		return Boolean.valueOf(getCommandSwitch(key, "false"));
	}
}
