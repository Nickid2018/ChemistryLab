package com.github.nickid2018.chemistrylab.chemicals;

import com.alibaba.fastjson.*;

// DO NOT OBFUSCATE IT AND ITS SUBCLASS IN FINAL VERSION
public abstract class Chemical {

	protected ChemicalResource resource;

	public Chemical(JSONObject o, ChemicalResource res) {
		resource = res;
	}

	public ChemicalResource getResource() {
		return resource;
	}

	public void doOnRedirect() {
		while (resource.canRedirect())
			resource = resource.getRedirectableObject().getObject();
	}

	public abstract double getMess();

	public boolean isActualMess() {
		return false;
	}
}
