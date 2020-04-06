package com.github.nickid2018.chemistrylab.chemicals.attributes;

import java.util.*;
import com.alibaba.fastjson.*;
import com.github.nickid2018.jmcl.*;
import com.github.nickid2018.chemistrylab.reaction.*;
import com.github.nickid2018.chemistrylab.chemicals.*;

/**
 * An attribute of chemical, in a reaction process it will be called twice, so
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
			DEFAULT_SOLVENT = new ChemicalItem(ChemicalLoader.CHEMICALS.get("H2O"), ChemicalState.LIQUID);
		// Dissolve have two functions:chemistry or physics
		// Chemistry: like AgCl dissolves in water (AgCl == Ag_1p + Cl_1n)
		// or CH3COOH dissolves in water (CH3COOH == CH3COO_1n + H_1p)
		// Physics: like O2 dissolves in water
		// or CH3CH2OH dissolves in water
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
				ChemicalResource resource = ChemicalLoader.CHEMICALS.get(obj.getString("chemical"));
				int number = obj.getIntValue("number");
				gets.put(resource, number);
			}
			reaction = new ReversibleReaction(reacts, gets, dH, dS, K, k);
		} else {
			physicsDissolve = MathStatement.format(o.getString("dissolveSpeed"));
			solubility = MathStatement.format(o.getString("solubility"));
		}
		item = new ChemicalItem(res, ChemicalState.AQUEOUS);
		gasitem = new ChemicalItem(resource, ChemicalState.GAS);
		liqitem = new ChemicalItem(resource, ChemicalState.LIQUID);
		soliditem = new ChemicalItem(resource, ChemicalState.SOLID);
	}

	public static ChemicalItem DEFAULT_SOLVENT;
	private static final Map<String, Double> args = new HashMap<>();

	private ChemicalItem item;
	private ChemicalItem gasitem;
	private ChemicalItem liqitem;
	private ChemicalItem soliditem;

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
			double gas = mixture.get(gasitem).getNum();
			double liquid = mixture.get(liqitem).getNum();
			double solid = mixture.get(soliditem).getNum();
			args.put("T", Environment.getTemperature());
			double solubility = this.solubility.calc(args);
			double now = convertSolubility(mixture.getConcentration(resource));
			double minus = solubility - now;
			double dodissolve = Math.signum(minus) * physicsDissolve.calc(args) * Environment.getSpeed() / 50;
			dodissolve = Math.abs(dodissolve) - Math.abs(minus) < 0 ? dodissolve : minus;
			double endConvert = convertMolPerL(dodissolve) * mixture.get(DEFAULT_SOLVENT).getNum()
					+ mixture.getChemicalItem(resource, ChemicalState.AQUEOUS).getNum();
			mixture.replace(item, new Unit(resource, Unit.UNIT_MOLE, endConvert));
			double all = gas + liquid + solid;
			gas -= dodissolve * gas / all;
			liquid -= dodissolve * liquid / all;
			solid -= dodissolve * solid / all;
			if (gas != 0)
				mixture.replace(gasitem, new Unit(resource, Unit.UNIT_MOLE, gas));
			else
				mixture.remove(gasitem);
			if (liquid != 0)
				mixture.replace(liqitem, new Unit(resource, Unit.UNIT_MOLE, liquid));
			else
				mixture.remove(liqitem);
			if (solid != 0)
				mixture.replace(soliditem, new Unit(resource, Unit.UNIT_MOLE, solid));
			else
				mixture.remove(soliditem);
		}
	}

	public double convertSolubility(double mol_l) {
		return mol_l * resource.getMess() / 10;
	}

	public double convertMolPerL(double g_100g) {
		return g_100g * 10 / resource.getMess();
	}
}
