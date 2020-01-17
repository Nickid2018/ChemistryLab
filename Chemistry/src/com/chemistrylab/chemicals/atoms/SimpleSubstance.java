package com.chemistrylab.chemicals.atoms;

import com.alibaba.fastjson.*;
import com.chemistrylab.chemicals.*;

public class SimpleSubstance extends Chemical {

	private final ChemicalResource linked;
	private final int count;

	public SimpleSubstance(JSONObject o) {
		super(o);
		String link = o.getString("atom");
		linked = ChemicalsLoader.chemicals.get(link);
		count = o.getIntValue("count");
	}

	public ChemicalResource getLinked() {
		return linked;
	}

	public int getCount() {
		return count;
	}

	@Override
	public double getMess() {
		return linked.getMess() * count;
	}

}
