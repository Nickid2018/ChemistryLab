package com.github.nickid2018.chemistrylab.mod.event;

import com.github.nickid2018.chemistrylab.init.*;
import com.github.nickid2018.chemistrylab.mod.*;
import com.github.nickid2018.chemistrylab.chemicals.*;

public class ModInitEvent extends ModLifeCycleEvent {

	private final ChemicalRegistry chemical_registry;

	public ModInitEvent(ModContainer mod, ChemicalRegistry registry, LoadingWindowProgress progresses) {
		super(mod, progresses);
		this.chemical_registry = registry;
	}

	public ChemicalRegistry getChemicalRegistry() {
		return chemical_registry;
	}
}
