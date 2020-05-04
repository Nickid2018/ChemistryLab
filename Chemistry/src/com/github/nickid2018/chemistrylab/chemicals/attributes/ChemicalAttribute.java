package com.github.nickid2018.chemistrylab.chemicals.attributes;

import com.alibaba.fastjson.JSONObject;
import com.github.nickid2018.chemistrylab.reaction.*;
import com.github.nickid2018.chemistrylab.chemicals.*;

public abstract class ChemicalAttribute extends Chemical {

	public ChemicalAttribute(JSONObject o, ChemicalResource res) {
		super(o, res);
	}

	public abstract void onAttributeRun(ChemicalMixture mixture, ReactionController controller, double rate);

	@Override
	public final boolean isActualMess() {
		return false;
	}

	@Override
	public final double getMess() {
		return 0;
	}

}
