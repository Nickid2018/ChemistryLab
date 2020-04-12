package com.github.nickid2018.chemistrylab.mod;

import java.io.*;
import java.util.*;
import java.util.function.*;
import org.apache.log4j.*;
import com.google.common.base.*;
import com.github.mmc1234.mod.*;
import org.apache.commons.io.filefilter.*;
import com.github.nickid2018.chemistrylab.init.*;
import com.github.nickid2018.chemistrylab.util.*;
import com.github.mmc1234.pinkengine.ClassUtils;
import com.github.nickid2018.chemistrylab.chemicals.*;

public final class ModController {

	public static final List<ModContainer> MODS = new ArrayList<>();
	public static final Logger logger = Logger.getLogger("ModLoader");
	public static final String MOD_ANNOTATION = "com.github.nickid2018.chemistrylab.mod.Mod";

	public static final Map<String, Logger> MOD_LOGGERS = new HashMap<>();

	public static void findMods() {
		// ============================This code will be delete in release
		// version=========================//
		ModContainer core = new ModContainer(com.github.nickid2018.chemistrylab.coremod.ChemistryCoreMod.class);
		MODS.add(core);
		// =======================================End=====================================//
		String[] findedModJars = new File("mods").list(new SuffixFileFilter("jar"));
		if (findedModJars == null)
			return;
		for (String jar : findedModJars) {
			try {
				ClassUtils.addURL("mods/" + jar);
				for (ModInstance instance : ModLoader.load("mods/" + jar, MOD_ANNOTATION)) {
					ModContainer container = new ModContainer(instance);
					MODS.add(container);
				}
			} catch (Exception e) {
				logger.error("Can't load mod " + jar, e);
			}
		}
		MODS.sort((m1, m2) -> {
			long ret = (long) m2.getMod().priority() - (long) m1.getMod().priority();
			return (int) (ret == 0 ? m1.getModId().compareTo(m2.getModId()) : Math.signum(ret));
		});
	}

	public static void sendPreInit(TextureRegistry registry, LoadingWindowProgress progresses) {
		sendModEvents(container -> container.sendPreInit(registry, progresses), progresses);
	}

	public static void sendInit(ChemicalRegistry registry, LoadingWindowProgress progresses) {
		sendModEvents(container -> container.sendInit(registry, progresses), progresses);
	}

	public static void sendIMCEnqueue(LoadingWindowProgress progresses) {
		sendModEvents(container -> container.sendIMC(progresses), progresses);
	}

	public static void doBeforeIMCProcess() {
		// Re-order
		MODS.sort((m1, m2) -> m1.getModId().compareTo(m2.getModId()));
	}

	public static ModContainer findMod(String modid) {
		int index = CollectionUtils.binarySearch(MODS, container -> container.getModId().compareTo(modid));
		Preconditions.checkArgument(index >= 0, "Mod is not exists.");
		return MODS.get(index);
	}

	public static int index = 0;

	private static void sendModEvents(Consumer<ModContainer> runnable, LoadingWindowProgress progresses) {
		LoadingWindowProgress.ProgressEntry entry = progresses.push(MODS.size());
		for (ModContainer container : MODS) {
			entry.progress.setCurrent(index + 1);
			entry.message.getInfo().setText(container.getModId() + " (" + (index + 1) + "/" + MODS.size() + ")");
			runnable.accept(container);
			index++;
		}
		progresses.pop();
	}
}
