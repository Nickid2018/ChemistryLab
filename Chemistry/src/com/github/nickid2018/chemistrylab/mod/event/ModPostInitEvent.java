package com.github.nickid2018.chemistrylab.mod.event;

import com.github.nickid2018.chemistrylab.init.*;
import com.github.nickid2018.chemistrylab.mod.*;

public class ModPostInitEvent extends ModLifeCycleEvent {

	public ModPostInitEvent(ModContainer mod, LoadingWindowProgress progresses) {
		super(mod, progresses);
	}

}
