package com.github.nickid2018.chemistrylab.chemicals.ions;

import java.util.*;
import com.alibaba.fastjson.*;
import com.github.nickid2018.chemistrylab.chemicals.*;

public class Ion extends Chemical {

	protected Set<ChemicalResource> relas = new HashSet<>();
	protected double mess;

	public Ion(JSONObject o, ChemicalResource r) {
		super(o, r);
		JSONArray rels = o.getJSONArray("relativeAtom");
		for (Object ob : rels) {
			String s = (String) ob;
			ChemicalResource atom = ChemicalLoader.CHEMICALS.get(s);
			relas.add(atom);
			mess += atom.getMess();
		}
	}

	@Override
	public void doOnRedirect() {
		super.doOnRedirect();
		Set<ChemicalResource> copy = new HashSet<>();
		for (ChemicalResource resource : relas) {
			ChemicalResource res = resource;
			while (res.canRedirect())
				res = res.getRedirectableObject().getObject();
			copy.add(res);
		}
		relas = copy;
	}

	@Override
	public double getMess() {
		return mess;
	}

	@Override
	public boolean isActualMess() {
		return true;
	}
}
