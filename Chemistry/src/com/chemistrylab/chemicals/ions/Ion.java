package com.chemistrylab.chemicals.ions;

import java.util.*;
import com.alibaba.fastjson.*;
import com.chemistrylab.chemicals.*;

public class Ion extends Chemical {
	
	protected Set<ChemicalResource> relas = new HashSet<>();
	protected double mess;

	public Ion(JSONObject o) {
		super(o);
		JSONArray rels = o.getJSONArray("relativeAtom");
		for(Object ob : rels){
			String s = (String) ob;
			ChemicalResource atom = ChemicalsLoader.chemicals.get(s);
			relas.add(atom);
			mess += atom.getMess();
		}
	}

	@Override
	public double getMess() {
		return mess;
	}
	
	public boolean isActualMess(){
		return true;
	}
}
