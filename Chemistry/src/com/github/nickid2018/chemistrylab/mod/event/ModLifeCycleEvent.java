package com.github.nickid2018.chemistrylab.mod.event;

import org.apache.log4j.*;
import com.github.nickid2018.chemistrylab.init.*;
import com.github.nickid2018.chemistrylab.mod.*;

public abstract class ModLifeCycleEvent {

	public final ModContainer mod;
	public final LoadingWindowProgress progresses;

	public ModLifeCycleEvent(String modid,LoadingWindowProgress progresses) {
		this.mod = ModController.MODS.get(modid);
		this.progresses = progresses;
	}

	public final Logger getModLog() {
		if (!ModController.MOD_LOGGERS.containsKey(mod.getModId()))
			ModController.MOD_LOGGERS.put(mod.getModId(), Logger.getLogger(mod.getModId()));
		return ModController.MOD_LOGGERS.get(mod.getModId());
	}
}
