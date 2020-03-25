package com.github.nickid2018.chemistrylab.chemicals.attributes;

import java.util.*;
import com.alibaba.fastjson.*;
import com.github.nickid2018.jmcl.*;
import com.github.nickid2018.chemistrylab.reaction.*;
import com.github.nickid2018.chemistrylab.chemicals.*;

/**
 * An attribute of chemical, in a reaction process it will be called twice,so
 * the speed rate is half of environment speed.
 * 
 * @author Nickid2018
 *
 */
public class Dissolve extends ChemicalAttribute {

	// true for chemistry, false for physics
	private boolean functionDissolve = false;

	private ReversibleReaction reaction;

	private MathStatement physicsDissolve;
	private MathStatement solubility;

	public Dissolve(JSONObject o, ChemicalResource res) throws MathException {
		super(o, res);
		if (DEFAULT_SOLVENT == null)
			DEFAULT_SOLVENT = new ChemicalItem(ChemicalsLoader.CHEMICALS.get("H2O"), ChemicalState.LIQUID);
		// Dissolve have two functions:chemistry or physics
		// Chemistry: like AgCl dissolves in water (AgCl == Ag_1p + Cl_1n)
		// or CH3COOH dissolves in water (CH3COOH == CH3COO_1n + H_1p)
		// Physics: like O2 dissolves in water
		// or CH3CH2OH dissolve in water
		functionDissolve = o.getBoolean("isReaction");
		if (functionDissolve) {
			double dH = o.getDoubleValue("deltaH");
			double dS = o.getDoubleValue("deltaS");
			String K = o.getString("equilibrium");
			String k = o.getString("speed");
			int reactionNumber = o.getIntValue("number");
			Map<ChemicalResource, Integer> reacts = new HashMap<>();
			reacts.put(res, reactionNumber);
			JSONArray array = o.getJSONArray("ions");
			Map<ChemicalResource, Integer> gets = new HashMap<>();
			for (int i = 0; i < array.size(); i++) {
				JSONObject obj = array.getJSONObject(i);
				ChemicalResource resource = ChemicalsLoader.CHEMICALS.get(obj.getString("chemical"));
				int number = obj.getIntValue("number");
				gets.put(resource, number);
			}
			reaction = new ReversibleReaction(reacts, gets, dH, dS, K, k);
		} else {
			physicsDissolve = MathStatement.format(o.getString("dissolveSpeed"));
			solubility = MathStatement.format(o.getString("solubility"));
		}
	}

	private static ChemicalItem DEFAULT_SOLVENT;

	/**
	 * This will be run in pre and post reaction
	 */
	@Override
	public void onAttributeRun(ChemicalMixture mixture, ReactionController controller) {
		// First: Check solvent
		// This will be changed in later version
		if (!mixture.containsKey(DEFAULT_SOLVENT))
			return;
		if (functionDissolve) {
			reaction.doReaction(mixture);
		} else {
			
		}
	}

}
