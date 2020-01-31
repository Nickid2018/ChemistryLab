package com.chemistrylab.chemicals.atoms;

import com.alibaba.fastjson.*;
import com.chemistrylab.chemicals.*;

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

	public double getMess() {
		return mess;
	}

	public int[] getValence() {
		return valence;
	}

	public boolean isActualMess() {
		return true;
	}
}
