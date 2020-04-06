package com.github.nickid2018.chemistrylab.chemicals;

import java.util.*;

public class ChemicalRegistry {

	public final Set<String> REGISTRY_ATOM = new TreeSet<>();
	public final Set<String> REGISTRY_ION = new TreeSet<>();
	public final Set<String> REGISTRY_CHEMICAL = new TreeSet<>();
	
	public void addAtom(String name) {
		REGISTRY_ATOM.add(name);
	}
	
	public void addIon(String name) {
		REGISTRY_ION.add(name);
	}
	
	public void addChemical(String name) {
		REGISTRY_CHEMICAL.add(name);
	}
}
