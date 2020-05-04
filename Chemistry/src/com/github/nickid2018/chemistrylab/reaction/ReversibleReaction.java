package com.github.nickid2018.chemistrylab.reaction;

import java.util.*;
import com.github.nickid2018.jmcl.*;
import com.github.nickid2018.chemistrylab.init.*;
import com.github.nickid2018.chemistrylab.chemicals.*;

public class ReversibleReaction extends Reaction {

	// Constant of equilibrium
	private final MathStatement K;
	private final String strK;

	public ReversibleReaction(Map<ChemicalResource, Integer> reacts, Map<ChemicalResource, Integer> gets, double dH,
			double dS, String K, String k) throws MathException {
		super(reacts, gets, dH, dS, k);
		strK = K;
		this.K = MathStatement.format(K);
	}

	public String getStrK() {
		return strK;
	}

	private static final Map<String, Double> args = new HashMap<>();

	@Override
	public void doReaction(ChemicalMixture mix, double rate) {
		if (!isReactionCanWork(mix.getTemperature()))
			return;
		// Chemical Equilibrium
		args.put("T", mix.getTemperature());
		double nowK = MathHelper.eplison(K.calc(args), 10);
		boolean computeLiq = false;
		double liquidv = 1;
		boolean computeGas = false;
		double gasv = 1;
		reacts.forEach((c, i) -> {
			
		});
	}

	public MathStatement getK() {
		return K;
	}

	public ReversibleReaction reverse() throws MathException {
		String strk = "1/(" + strK + ")";
		return new ReversibleReaction(gets, reacts, -dH, -dS, strk, this.strk);
	}
}
