package com.github.nickid2018.chemistrylab.chemicals;

import java.util.*;
import com.badlogic.gdx.assets.*;
import com.github.nickid2018.chemistrylab.resource.*;

public class ChemicalRegistry {

	public final List<String> REGISTRY_ATOM = new ArrayList<>();
	public final List<String> REGISTRY_ION = new ArrayList<>();
	public final List<String> REGISTRY_CHEMICAL = new ArrayList<>();

	public void addAtom(String name) {
		REGISTRY_ATOM.add(NameMapping.mapName(name));
	}

	public void addIon(String name) {
		REGISTRY_ION.add(NameMapping.mapName(name));
	}

	public void addChemical(String name) {
		REGISTRY_CHEMICAL.add(NameMapping.mapName(name));
	}

	public int getTotalSize() {
		return REGISTRY_ATOM.size() + REGISTRY_ION.size() + REGISTRY_CHEMICAL.size();
	}

	public String getNowLoading(int number) {
		if (number < REGISTRY_ATOM.size())
			return REGISTRY_ATOM.get(number);
		if ((number -= REGISTRY_ATOM.size()) < REGISTRY_ION.size())
			return REGISTRY_ION.get(number);
		if ((number -= REGISTRY_ION.size()) < REGISTRY_CHEMICAL.size())
			return REGISTRY_CHEMICAL.get(number);
		return "Loading Finished";
	}

	public void doInit(AssetManager manager) {
		List<String> items = new ArrayList<>();
		items.addAll(REGISTRY_ATOM);
		items.addAll(REGISTRY_ION);
		items.addAll(REGISTRY_CHEMICAL);
		for (String path : items) {
			manager.load(path, ChemicalResource.class);
		}
	}
}
