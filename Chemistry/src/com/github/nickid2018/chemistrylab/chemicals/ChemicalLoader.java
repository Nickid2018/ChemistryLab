package com.github.nickid2018.chemistrylab.chemicals;

import com.badlogic.gdx.files.*;
import org.apache.log4j.Logger;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.assets.loaders.*;

public class ChemicalLoader extends SynchronousAssetLoader<ChemicalResource, ChemicalLoadParameters> {

	// Logger
	public static final Logger logger = Logger.getLogger("Chemical Loader");

	public static final ChemicalRegistry CHEMICAL_REGISTRY = new ChemicalRegistry();
	public static final ChemicalDecompilerRegistry DECOMPILER_REGISTRY = new ChemicalDecompilerRegistry();

	public static final Chemicals CHEMICALS = new Chemicals();

	public ChemicalLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	public ChemicalResource load(AssetManager manager, String fileName, FileHandle file,
			ChemicalLoadParameters parameter) {
		String[] sps = fileName.split("/");
		String name = sps[sps.length - 1].split("\\.")[0];
		try {
			return CHEMICALS.put(name, new ChemicalResource(fileName, name).preInit());
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, ChemicalLoadParameters parameter) {
		return null;
	}
}
