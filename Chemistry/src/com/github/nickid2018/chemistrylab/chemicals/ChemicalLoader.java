package com.github.nickid2018.chemistrylab.chemicals;

import org.apache.log4j.*;

public class ChemicalLoader {

	// Logger
	public static final Logger logger = Logger.getLogger("Chemical Loader");

	public static final ChemicalRegistry CHEMICAL_REGISTRY = new ChemicalRegistry();
	public static final ChemicalDecompilerRegistry DECOMPILER_REGISTRY = new ChemicalDecompilerRegistry();

	public static final Chemicals CHEMICALS = new Chemicals();
	public static final ChemicalConflictManager CONFLICT = new ChemicalConflictManager();

	public ChemicalResource nowChemical = null;

	public void load(String fileName, ChemicalLoadParameters parameter) {
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
}
