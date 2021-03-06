package com.github.nickid2018.chemistrylab.util;

import java.io.*;
import java.util.*;
import org.apache.log4j.*;

import com.github.nickid2018.chemistrylab.init.ProgramOptions;
import com.github.nickid2018.chemistrylab.resource.*;

public class I18N {

	// Logger
	public static final Logger logger = Logger.getLogger("Internationalization Manager");

	// Locales
	private static final Properties langStrings = new Properties();
	private static final Properties langNameStrings = new Properties();

	public static final Locale SYSTEM_DEFAULT = Locale.getDefault();
	public static Locale NOW;

	// Status
	public static boolean i18nReload = false;

	// Load I18N file(s)
	public static void load() throws Exception {
		String localename = ProgramOptions.getWindowOption("locale", "default");
		NOW = localename.equalsIgnoreCase("default") ? SYSTEM_DEFAULT : Locale.forLanguageTag(localename);
		String lang = NOW.toString().toLowerCase();
		String file_name = "assets/lang/" + lang + ".lang";
		InputStream stream;
		try {
			stream = ResourceManager.getResourceAsStream(file_name, true); // Allow sequence the stream
		} catch (Exception e) {
			stream = ResourceManager.getResourceAsStream("assets/lang/en_us.lang", true);
			NOW = new Locale("en_us");
		}
		langStrings.load(new InputStreamReader(stream, "GB2312"));
		String disp_name = "assets/lang/display_names.settings";
		InputStream dispstream = ResourceManager.getResourceAsStream(disp_name, true);
		langNameStrings.load(new InputStreamReader(dispstream, "GB2312"));
		logger.info("Successfully loaded Language " + I18N.getNowLanguage() + ".");
	}

	// Reload I18N
	public static void reload(Locale loc) throws Exception {
		if (loc == NOW)
			throw new IllegalArgumentException("The same Locale");
		Objects.requireNonNull(loc, "The locale is null");
		NOW = loc;
		langStrings.clear();
		String lang = NOW.toString().toLowerCase();
		String file_name = "assets/lang/" + lang + ".lang";
		InputStream stream = ResourceManager.getResourceAsStream(file_name, true);
		InputStreamReader ir;
		try {
			ir = new InputStreamReader(stream, "GB2312");
		} catch (Exception e) {
			throw new Exception("Can't find language " + lang);
		}
		langStrings.load(ir);
		logger.info("Successfully loaded Language " + I18N.getNowLanguage() + ".");
	}

	// Reload with name
	public static void reload(String langName) throws Exception {
		Locale loc = new Locale(langNameStrings.getProperty(langName));
		reload(loc);
	}

	// Get supported languages
	public static String[] getSurporttedLanguages() {
		Set<String> names = langNameStrings.stringPropertyNames();
		String[] ret = names.toArray(new String[names.size()]);
		Arrays.sort(ret, (o1, o2) -> {
			return o1.compareTo(o2);
		});
		return ret;
	}

	// Get now language
	public static String getNowLanguage() {
		return NOW.toString();
	}

	// Format String
	public static String getString(String unlocalizedname) {
		return langStrings.getProperty(unlocalizedname, unlocalizedname);
	}

	// Get name of language
	public static String getSurporttedLanguageName(String lang) {
		return langNameStrings.getProperty(lang);
	}
}
