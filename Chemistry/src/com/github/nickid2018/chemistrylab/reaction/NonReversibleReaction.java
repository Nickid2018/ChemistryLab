package com.github.nickid2018.chemistrylab.reaction;

import java.util.*;
import com.github.nickid2018.jmcl.*;
import com.github.nickid2018.chemistrylab.chemicals.*;

public class NonReversibleReaction extends Reaction {

	public NonReversibleReaction(Map<ChemicalResource, Integer> reacts, Map<ChemicalResource, Integer> gets, double dH,
			double dS, String k) throws MathException {
		super(reacts, gets, dH, dS, k);
	}

	@Override
	public void doReaction(ChemicalMixture mix) {

	}

}
