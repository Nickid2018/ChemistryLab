package com.github.nickid2018.chemistrylab.chemicals;

import java.util.*;
import com.github.nickid2018.chemistrylab.util.*;
import com.github.nickid2018.chemistrylab.resource.*;

public class ChemicalRegistry {

	public final List<Pair<String, String>> REGISTRY_ATOM = new ArrayList<>();
	public final List<Pair<String, String>> REGISTRY_ION = new ArrayList<>();
	public final List<Pair<String, String>> REGISTRY_CHEMICAL = new ArrayList<>();

	public String nowLoadMod;

	public void addAtom(String name) {
		REGISTRY_ATOM.add(new Pair<>(NameMapping.mapName(name), nowLoadMod));
	}

	public void addIon(String name) {
		REGISTRY_ION.add(new Pair<>(NameMapping.mapName(name), nowLoadMod));
	}

	public void addChemical(String name) {
		REGISTRY_CHEMICAL.add(new Pair<>(NameMapping.mapName(name), nowLoadMod));
	}

	public int getTotalSize() {
		return REGISTRY_ATOM.size() + REGISTRY_ION.size() + REGISTRY_CHEMICAL.size();
	}

	public Pair<String, String> getNowLoading(int number) {
		if (number < REGISTRY_ATOM.size())
			return REGISTRY_ATOM.get(number);
		if ((number -= REGISTRY_ATOM.size()) < REGISTRY_ION.size())
			return REGISTRY_ION.get(number);
		if ((number -= REGISTRY_ION.size()) < REGISTRY_CHEMICAL.size())
			return REGISTRY_CHEMICAL.get(number);
		return new Pair<>("Loading Finished", "Loading Finished");
	}

//	public void doInit(AssetManager manager) {
//		List<Pair<String, String>> items = new ArrayList<>();
//		items.addAll(REGISTRY_ATOM);
//		items.addAll(REGISTRY_ION);
//		items.addAll(REGISTRY_CHEMICAL);
//		for (Pair<String, String> path : items) {
//			manager.load(path.key, ChemicalResource.class, new ChemicalLoadParameters(path.value));
//		}
//	}
}
