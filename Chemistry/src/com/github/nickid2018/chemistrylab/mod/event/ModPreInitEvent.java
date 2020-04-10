package com.github.nickid2018.chemistrylab.mod.event;

import com.github.nickid2018.chemistrylab.init.*;
import com.github.nickid2018.chemistrylab.mod.*;
import com.github.nickid2018.chemistrylab.chemicals.*;

public class ModPreInitEvent extends ModLifeCycleEvent {

	private final ModTextureRegistry tex_registry;
	private final ChemicalDecompilerRegistry decompile_registry;

	public ModPreInitEvent(ModContainer mod, TextureRegistry parent, LoadingWindowProgress progresses) {
		super(mod, progresses);
		tex_registry = parent.newModRegistry(mod.getModId());
		decompile_registry = ChemicalLoader.DECOMPILER_REGISTRY;
	}

	public ModTextureRegistry getTextureRegistry() {
		return tex_registry;
	}

	public ChemicalDecompilerRegistry getChemicalDecompilerRegistry() {
		return decompile_registry;
	}
}
