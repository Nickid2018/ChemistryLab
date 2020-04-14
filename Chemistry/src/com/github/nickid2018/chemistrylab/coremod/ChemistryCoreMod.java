package com.github.nickid2018.chemistrylab.coremod;

import org.apache.log4j.*;
import com.github.nickid2018.chemistrylab.mod.*;
import com.github.nickid2018.chemistrylab.resource.*;
import com.github.nickid2018.chemistrylab.chemicals.*;
import com.github.nickid2018.chemistrylab.mod.event.*;
import com.github.nickid2018.chemistrylab.chemicals.ions.*;
import com.github.nickid2018.chemistrylab.chemicals.alkalis.*;
import com.github.nickid2018.chemistrylab.chemicals.atoms.*;
import com.github.nickid2018.chemistrylab.chemicals.oxides.*;

@Mod(modid = "chemisty-core-mod", version = "1.0.0", priority = Integer.MAX_VALUE)
public class ChemistryCoreMod {

	@Instance
	public static ChemistryCoreMod instance;

	public Logger logger;

	public void onPreInit(ModPreInitEvent event) {
		logger = event.getModLog();
		ChemicalDecompilerRegistry registry = event.getChemicalDecompilerRegistry();
		registry.addChemicalDecompiler("atom/nonmetal", NonMentalAtom.class);
		registry.addChemicalDecompiler("atom/metal", MentalAtom.class);
		registry.addChemicalDecompiler("simple", SimpleSubstanceA.class);
		registry.addChemicalDecompiler("simple-subtance", SimpleSubstance.class);
		registry.addChemicalDecompiler("ion/H", IonH.class);
		registry.addChemicalDecompiler("ion/OH", IonOH.class);
		registry.addChemicalDecompiler("ion/strong", IonStrong.class);
		registry.addChemicalDecompiler("alkali/strong", AlkaliStrong.class);
		registry.addChemicalDecompiler("chemical/H2O", H2O.class);
	}

	public void onInit(ModInitEvent event) {
		// Name Mapping registering
		NameMapping.addMapName("coremod", "atom.chemical", "assets/models/chemicals/atoms/%s.json");
		NameMapping.addMapName("coremod", "ion.chemical", "assets/models/chemicals/ions/%s.json");
		NameMapping.addMapName("coremod", "oxide.chemical", "assets/models/chemicals/oxides/%s.json");
		NameMapping.addMapName("coremod", "alkali.chemical", "assets/models/chemicals/alkalis/%s.json");
		NameMapping.addMapName("coremod", "salt.chemical", "assets/models/chemicals/salts/%s.json");
		// Atoms
		ChemicalRegistry registry = event.getChemicalRegistry();
		registry.addAtom("coremod.H.atom.chemical");
		registry.addAtom("coremod.O.atom.chemical");
		registry.addAtom("coremod.Na.atom.chemical");
		// Ions
		registry.addIon("coremod.H_1p.ion.chemical");
		registry.addIon("coremod.OH_1n.ion.chemical");
		registry.addIon("coremod.Na_1p.ion.chemical");
		// Chemicals
		registry.addChemical("coremod.NaOH.alkali.chemical");
		registry.addChemical("coremod.H2O.oxide.chemical");
		logger.info("Loaded basic objects of Chemistry Lab");
	}
	
	public void onIMC(ModIMCEvent event) {
		
	}
}
