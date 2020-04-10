package com.github.nickid2018.chemistrylab.init;

import com.github.nickid2018.chemistrylab.resource.*;

public class EngineTextureRegisterer {

	public static void registerGUI(TextureRegistry registry) {
		TextureRegistry guihandle = registry.newRegistry("GUI Textures", Integer.MIN_VALUE);
		guihandle.register(NameMapping.mapName("gui.table.texture"));
	}
}
