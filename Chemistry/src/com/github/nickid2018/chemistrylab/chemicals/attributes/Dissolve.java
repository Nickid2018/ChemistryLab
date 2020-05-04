package com.github.nickid2018.chemistrylab.chemicals.attributes;

import java.util.*;
import com.alibaba.fastjson.*;
import com.github.nickid2018.jmcl.*;
import com.github.nickid2018.chemistrylab.reaction.*;
import com.github.nickid2018.chemistrylab.reaction.data.ChemicalItem;
import com.github.nickid2018.chemistrylab.reaction.data.ChemicalItemGetter;
import com.github.nickid2018.chemistrylab.reaction.data.ChemicalState;
import com.github.nickid2018.chemistrylab.reaction.data.Environment;
import com.github.nickid2018.chemistrylab.reaction.data.Unit;
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
	}

	public static ChemicalItem DEFAULT_SOLVENT;
	private static final Map<String, Double> args = new HashMap<>();

	/**
	 * This will be run in pre and post reaction
	 */
	@Override
	public void onAttributeRun(ChemicalMixture mixture, ReactionController controller, double rate) {
		// First: Check solvent
		// This will be changed in later version
		if (!mixture.containsKey(DEFAULT_SOLVENT))
			return;
		if (functionDissolve) {
			reaction.doReaction(mixture, rate);
		} else {
			ChemicalItem item = ChemicalItemGetter.newItem(resource, ChemicalState.AQUEOUS);
			ChemicalItem gasitem = ChemicalItemGetter.newItem(resource, ChemicalState.GAS);
			ChemicalItem liqitem = ChemicalItemGetter.newItem(resource, ChemicalState.LIQUID);
			ChemicalItem soliditem = ChemicalItemGetter.newItem(resource, ChemicalState.SOLID);
			double gas = mixture.get(gasitem).getNum();
			double liquid = mixture.get(liqitem).getNum();
			double solid = mixture.get(soliditem).getNum();
			args.put("T", Environment.getTemperature());
			double solubility = this.solubility.calc(args);
			double now = convertSolubility(mixture.getConcentration(resource));
			double minus = solubility - now;
			double dodissolve = Math.signum(minus) * physicsDissolve.calc(args) * rate / 2;
			dodissolve = Math.abs(dodissolve) - Math.abs(minus) < 0 ? dodissolve : minus;
			double endConvert = convertMolPerL(dodissolve) * mixture.get(DEFAULT_SOLVENT).getNum()
					+ mixture.getChemicalItem(resource, ChemicalState.AQUEOUS).getNum();
			mixture.replace(item, new Unit(item, Unit.UNIT_MOLE, endConvert));
			double all = gas + liquid + solid;
			gas -= dodissolve * gas / all;
			liquid -= dodissolve * liquid / all;
			solid -= dodissolve * solid / all;
			mixture.replace(gasitem, new Unit(gasitem, Unit.UNIT_MOLE, gas));
			mixture.replace(liqitem, new Unit(liqitem, Unit.UNIT_MOLE, liquid));
			mixture.replace(soliditem, new Unit(soliditem, Unit.UNIT_MOLE, solid));
			ChemicalItemGetter.free(item, gasitem, liqitem, soliditem);
		}
	}

	public double convertSolubility(double mol_l) {
		return mol_l * resource.getMess() / 10;
	}

	public double convertMolPerL(double g_100g) {
		return g_100g * 10 / resource.getMess();
	}
}
