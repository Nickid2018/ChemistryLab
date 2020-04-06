package com.github.nickid2018.chemistrylab.mod;

import com.github.nickid2018.chemistrylab.init.*;

public final class ModTextureRegistry extends TextureRegistry {

	private final String modid;

	public ModTextureRegistry(String modid) {
		super(modid);
		this.modid = modid;
	}

	public String getModid() {
		return modid;
	}

	@Override
	public TextureRegistry newRegistry(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ModTextureRegistry newModRegistry(String modid) {
		throw new UnsupportedOperationException();
	}
}
