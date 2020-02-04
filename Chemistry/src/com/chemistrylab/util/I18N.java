package com.chemistrylab.util;

import java.io.*;
import java.util.*;
import org.apache.log4j.*;
import com.chemistrylab.eventbus.*;

public class I18N {

	public static final Logger logger = Logger.getLogger("Internationalization Manager");
	public static final Event I18N_RELOADED = Event.createNewEvent("I18N_reloaded");

	private static final Locale SYSTEM_DEFAULT = Locale.getDefault();
	private static final Properties langStrings = new Properties();
	private static final Properties langNameStrings = new Properties();
	private static Locale NOW = SYSTEM_DEFAULT;

	public static void load() throws Exception {
		String lang = NOW.toString().toLowerCase();
		String file_name = "assets/lang/" + lang + ".lang";
		InputStream stream;
		try {
			stream = ResourceManager.getResourceAsStream(file_name, true);
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

	public static void reload(Locale loc) throws Exception {
		if (loc == NOW)
			throw new Exception("The same Locale");
		if (loc == null)
			throw new Exception("The locale is null");
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
		EventBus.postEvent(I18N_RELOADED);
	}

	public static void reload(String langName) throws Exception {
		Locale loc = new Locale(langNameStrings.getProperty(langName));
		reload(loc);
	}

	public static String[] getSurporttedLanguages() {
		Set<String> names = langNameStrings.stringPropertyNames();
		String[] ret = names.toArray(new String[names.size()]);
		Arrays.sort(ret, (o1, o2) -> {
			return o1.compareTo(o2);
		});
		return ret;
	}

	public static String getNowLanguage() {
		return NOW.toString();
	}

	public static String getString(String unlocalizedname) {
		return langStrings.getProperty(unlocalizedname, unlocalizedname);
	}

	public static String getSurporttedLanguageName(String lang) {
		return langNameStrings.getProperty(lang);
	}
}