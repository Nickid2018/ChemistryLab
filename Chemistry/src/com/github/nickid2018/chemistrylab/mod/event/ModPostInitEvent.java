package com.github.nickid2018.chemistrylab.mod.event;

import com.github.nickid2018.chemistrylab.init.*;

public class ModPostInitEvent extends ModLifeCycleEvent {

	public ModPostInitEvent(String modid, LoadingWindowProgress progresses) {
		super(modid, progresses);
	}

}
