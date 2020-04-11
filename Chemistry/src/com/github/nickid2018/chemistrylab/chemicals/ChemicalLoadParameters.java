package com.github.nickid2018.chemistrylab.chemicals;

import com.badlogic.gdx.assets.*;

public class ChemicalLoadParameters extends AssetLoaderParameters<ChemicalResource> {

	public String modid;
	
	public ChemicalLoadParameters(String modid) {
		this.modid = modid;
	}
}
