package com.chemistrylab.reaction;

import java.util.*;
import com.cj.jmcl.*;
import com.chemistrylab.chemicals.*;

public class ReversibleReaction extends Reaction {

	private MathStatement K;
	private String strK;

	public ReversibleReaction(Map<ChemicalResource, Integer> reacts, Map<ChemicalResource, Integer> gets, double dH,
			double dS, String K) throws MathException {
		super(reacts, gets, dH, dS);
		strK = K;
		this.K = MathStatement.format(K);
	}

	public String getStrK() {
		return strK;
	}

	@Override
	public void doReaction(ChemicalMixture mix) {

	}

}
