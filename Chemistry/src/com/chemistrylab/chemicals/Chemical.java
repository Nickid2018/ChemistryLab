package com.chemistrylab.chemicals;

import com.alibaba.fastjson.*;

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
