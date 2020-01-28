package com.chemistrylab.chemicals;

import java.util.*;

public class Chemicals extends HashMap<String, ChemicalResource> {

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

	public ChemicalResource get(String key) {
		ChemicalResource ret = super.get(key);
		if (ret == null)
			throw new IllegalArgumentException("Can't find chemical \'" + key + "\'");
		return ret;
	}
}
