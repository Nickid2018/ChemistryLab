package com.github.nickid2018.chemistrylab.mod;

import java.io.*;
import java.util.*;
import org.apache.log4j.*;
import com.github.mmc1234.mod.*;
import org.apache.commons.io.filefilter.*;
import com.github.nickid2018.chemistrylab.init.*;

public final class ModController {

	public static final Map<String, ModContainer> MODS = new TreeMap<>();
	public static final Logger logger = Logger.getLogger("ModLoader");
	public static final String MOD_ANNOTATION = "com.github.nickid2018.chemistrylab.mod.Mod";

	static final Map<String, Logger> MOD_LOGGERS = new HashMap<>();

	public static void findMods() {
		String[] findedModJars = new File("mods").list(new SuffixFileFilter("jar"));
		if (findedModJars == null)
			return;
		for (String jar : findedModJars) {
			try {
				for (ModInstance instance : ModLoader.load(jar, MOD_ANNOTATION)) {
					ModContainer container = new ModContainer(instance);
					MODS.put(container.getModId(), container);
				}
			} catch (IOException e) {
				logger.error("Can't load mod " + jar, e);
			}
		}
	}

	public static void sendPreInit(TextureRegistry registry) {
		MODS.values().forEach(container -> container.sendPreInit(registry));
	}
}
