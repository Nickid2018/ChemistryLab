package com.github.nickid2018.chemistrylab.mod.event;

import com.github.nickid2018.chemistrylab.init.*;

public class ModInitEvent extends ModLifeCycleEvent {

	public ModInitEvent(String modid, LoadingWindowProgress progresses) {
		super(modid, progresses);
	}

}
