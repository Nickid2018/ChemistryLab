package com.github.nickid2018.chemistrylab.mod.event;

import com.github.nickid2018.chemistrylab.init.*;
import com.github.nickid2018.chemistrylab.mod.*;
import com.github.nickid2018.chemistrylab.chemicals.*;

public class ModPreInitEvent extends ModLifeCycleEvent {

	private final ModTextureRegistry tex_registry;
	private final ChemicalDecompilerRegistry decompile_registry;

	public ModPreInitEvent(String modid, TextureRegistry parent) {
		super(modid);
		tex_registry = parent.newModRegistry(modid);
		decompile_registry = ChemicalLoader.DECOMPILER_REGISTRY;
	}

	public ModTextureRegistry getTextureRegistry() {
		return tex_registry;
	}

	public ChemicalDecompilerRegistry getChemicalDecompilerRegistry() {
		return decompile_registry;
	}
}
