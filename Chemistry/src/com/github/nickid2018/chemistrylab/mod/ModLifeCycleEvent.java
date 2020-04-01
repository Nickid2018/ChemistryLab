package com.github.nickid2018.chemistrylab.mod;

import org.apache.log4j.*;

public abstract class ModLifeCycleEvent {

	public final ModContainer mod;

	public ModLifeCycleEvent(String modid) {
		this.mod = ModController.MODS.get(modid);
	}

	public final Logger getModLog() {
		if (!ModController.MOD_LOGGERS.containsKey(mod.getModId()))
			ModController.MOD_LOGGERS.put(mod.getModId(), Logger.getLogger(mod.getModId()));
		return ModController.MOD_LOGGERS.get(mod.getModId());
	}
}
