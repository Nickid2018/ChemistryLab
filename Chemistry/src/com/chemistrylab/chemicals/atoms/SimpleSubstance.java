package com.chemistrylab.chemicals.atoms;

import com.alibaba.fastjson.*;
import com.chemistrylab.chemicals.*;

public class SimpleSubstance extends Chemical {

	private int count;
	private ChemicalResource sup;

	public SimpleSubstance(JSONObject o, ChemicalResource res) {
		super(o, res);
		SimpleSubstanceA.ChemicalResourceOfSS r = (SimpleSubstanceA.ChemicalResourceOfSS) res;
		count = r.getCount();
		sup = r.getSuperRes();
	}

	@Override
	public double getMess() {
		return sup.getMess() * count;
	}

	@Override
	public boolean isActualMess() {
		return true;
	}

	static {
		try {
			ChemicalsLoader.mapping.put("simple-substance",
					SimpleSubstance.class.getConstructor(JSONObject.class, ChemicalResource.class));
		} catch (NoSuchMethodException | SecurityException e) {
		}
	}
}
