package com.chemistrylab.reaction;

import java.util.*;
import com.chemistrylab.chemicals.*;

public class NonReversibleReaction extends Reaction {

	public NonReversibleReaction(Map<ChemicalResource, Integer> reacts, Map<ChemicalResource, Integer> gets, double dH, double dS) {
		super(reacts, gets, dH, dS);
	}

	@Override
	public void doReaction(ChemicalMixture mix) {

	}

}
