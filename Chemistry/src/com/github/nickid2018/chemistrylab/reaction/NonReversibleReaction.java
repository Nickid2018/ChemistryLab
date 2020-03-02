package com.github.nickid2018.chemistrylab.reaction;

import java.util.*;

import com.github.nickid2018.chemistrylab.chemicals.*;

public class NonReversibleReaction extends Reaction {

	public NonReversibleReaction(Map<ChemicalResource, Integer> reacts, Map<ChemicalResource, Integer> gets, double dH,
			double dS) {
		super(reacts, gets, dH, dS);
	}

	@Override
	public void doReaction(ChemicalMixture mix) {

	}

}
