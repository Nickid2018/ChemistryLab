package com.github.nickid2018.chemistrylab.init;

public class EngineTextureRegisterer {

	public static void registerGUI(TextureRegistry registry) {
		TextureRegistry guihandle = registry.newRegistry("GUI Textures", Integer.MIN_VALUE);
		guihandle.getTotalSize();
	}
}
