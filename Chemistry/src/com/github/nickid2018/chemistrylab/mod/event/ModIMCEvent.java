package com.github.nickid2018.chemistrylab.mod.event;

import com.github.nickid2018.chemistrylab.init.*;
import com.github.nickid2018.chemistrylab.mod.*;
import com.github.nickid2018.chemistrylab.mod.imc.*;

public class ModIMCEvent extends ModLifeCycleEvent {

	public ModIMCEvent(ModContainer mod, LoadingWindowProgress progresses) {
		super(mod, progresses);
	}

	public void sendIMCMessage(String to, ConflictType type, IConflictable<?> conflict) {
		ModIMCController.sendTo(mod.getModId(), to, type, conflict);
	}

	public void sendAllIMCMessage(ConflictType type, IConflictable<?> conflict) {
		ModIMCController.sendAll(mod.getModId(), type, conflict);
	}
}
