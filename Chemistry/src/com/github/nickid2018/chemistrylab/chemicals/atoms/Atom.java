package com.github.nickid2018.chemistrylab.chemicals.atoms;

import com.alibaba.fastjson.*;
import com.github.nickid2018.chemistrylab.chemicals.*;

public class Atom extends Chemical {

	private final double mess;
	private int[] valence;

	public Atom(JSONObject o, ChemicalResource r) {
		super(o, r);
		mess = o.getDoubleValue("mess");
		JSONArray valencea = o.getJSONArray("valence");
		valence = new int[valencea.size()];
		for (int i = 0; i < valencea.size(); i++) {
			valence[i] = valencea.getInteger(i);
		}
	}

	@Override
	public double getMess() {
		return mess;
	}

	public int[] getValence() {
		return valence;
	}

	@Override
	public boolean isActualMess() {
		return true;
	}
}
