package com.github.nickid2018.chemistrylab.mod;

import com.github.nickid2018.chemistrylab.init.*;

public class ModPreInitEvent extends ModLifeCycleEvent {

	private final ModTextureRegistry registry;

	public ModPreInitEvent(String modid, TextureRegistry parent) {
		super(modid);
		registry = parent.newModRegistry(modid);
	}

	public ModTextureRegistry getTextureRegistry() {
		return registry;
	}
}
