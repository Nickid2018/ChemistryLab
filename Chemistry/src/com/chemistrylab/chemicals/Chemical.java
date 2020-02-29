package com.chemistrylab.chemicals;

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

	public abstract double getMess();
	
	public boolean isActualMess(){
		return false;
	}
}
