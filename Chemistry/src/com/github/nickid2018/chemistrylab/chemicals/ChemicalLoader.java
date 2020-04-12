package com.github.nickid2018.chemistrylab.chemicals;

import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.files.*;
import org.apache.log4j.Logger;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.assets.loaders.*;

public class ChemicalLoader extends AsynchronousAssetLoader<ChemicalResource, ChemicalLoadParameters> {

	// Logger
	public static final Logger logger = Logger.getLogger("Chemical Loader");

	public static final ChemicalRegistry CHEMICAL_REGISTRY = new ChemicalRegistry();
	public static final ChemicalDecompilerRegistry DECOMPILER_REGISTRY = new ChemicalDecompilerRegistry();

	public static final Chemicals CHEMICALS = new Chemicals();
	public static final ChemicalConflictManager CONFLICT = new ChemicalConflictManager();

	public ChemicalLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	private ChemicalResource nowChemical = null;

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, ChemicalLoadParameters parameter) {
		nowChemical = null;
		String[] sps = fileName.split("/");
		String name = sps[sps.length - 1].split("\\.")[0];
		try {
			CHEMICALS.put(name, nowChemical = new ChemicalResource(fileName, name).preInit(),
					parameter.modid);
		} catch (Exception e) {
			nowChemical = ChemicalResource.NULL;
			e.printStackTrace();
		}
	}

	@Override
	public ChemicalResource loadSync(AssetManager manager, String fileName, FileHandle file,
			ChemicalLoadParameters parameter) {
		return nowChemical;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, ChemicalLoadParameters parameter) {
		return null;
	}
}
