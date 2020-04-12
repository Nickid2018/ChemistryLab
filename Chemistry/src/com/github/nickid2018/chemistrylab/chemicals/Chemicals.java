package com.github.nickid2018.chemistrylab.chemicals;

import java.util.*;

//The map to save ChemicalResource
public class Chemicals extends TreeMap<String, ChemicalResource>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8546799773747025135L;

	private int failedPartLoad = 0;

	public int getFailedPartLoad() {
		return failedPartLoad;
	}

	public void addFailed() {
		this.failedPartLoad++;
	}

	public int atoms() {
		int r = 0;
		for (ChemicalResource cr : values()) {
			if (cr.hasAttribute("atom"))
				r++;
		}
		return r;
	}

	public int ions() {
		int r = 0;
		for (ChemicalResource cr : values()) {
			if (cr.hasAttribute("ion"))
				r++;
		}
		return r;
	}

	// Conflict!
	public ChemicalResource put(String key, ChemicalResource value, String modid) {
		return super.put(key + ":" + modid, value);
	}

	public ChemicalResource get(String key) {
		ChemicalResource ret = super.get(key);
		if (ret == null) {
			// May be not merged/overwrite
			for (String unmerged : keySet()) {
				if (unmerged.split(":")[0].equals(key)) {
					return super.get(unmerged);
				}
			}
			throw new IllegalArgumentException("Can't find chemical \'" + key + "\'");
		}
		return ret;
	}
}
