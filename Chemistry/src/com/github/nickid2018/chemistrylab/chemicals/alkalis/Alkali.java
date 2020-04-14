package com.github.nickid2018.chemistrylab.chemicals.alkalis;

import java.util.*;
import com.alibaba.fastjson.*;
import com.github.nickid2018.chemistrylab.chemicals.*;

public class Alkali extends Chemical {

	protected Map<ChemicalResource, Integer> relas = new HashMap<>();
	protected double mess;

	public Alkali(JSONObject o, ChemicalResource r) {
		super(o, r);
		JSONArray rels = o.getJSONArray("relativeIon");
		for (Object ob : rels) {
			String s = (String) ob;
			String[] info = s.split(":", 2);
			ChemicalResource ion = ChemicalLoader.CHEMICALS.get(info[0]);
			int count = Integer.parseInt(info[1]);
			relas.put(ion, count);
			mess += ion.getMess() * count;
		}
	}

	@Override
	public void doOnRedirect() {
		super.doOnRedirect();
		List<ChemicalResource> resources = new ArrayList<>(relas.keySet());
		for (ChemicalResource resource : resources) {
			int get = relas.remove(resource);
			ChemicalResource res = resource;
			while(res.canRedirect())
				res = res.getRedirectableObject().getObject();
			relas.put(res, get);
		}
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
