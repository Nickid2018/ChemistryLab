package com.github.nickid2018.chemistrylab.reaction;

import java.util.*;
import com.github.nickid2018.jmcl.*;
import com.github.nickid2018.chemistrylab.chemicals.*;

public abstract class Reaction {

	protected final double dH;
	protected final double dS;
	protected Map<ChemicalResource, Integer> reacts = new HashMap<>();
	protected Map<ChemicalResource, Integer> gets = new HashMap<>();

	// Constant of speed
	protected final MathStatement k;
	protected final String strk;

	public Reaction(Map<ChemicalResource, Integer> reacts, Map<ChemicalResource, Integer> gets, double dH, double dS,
			String k) throws MathException {
		this.reacts = reacts;
		this.gets = gets;
		this.dH = dH;
		this.dS = dS;
		strk = k;
		this.k = MathStatement.format(k);
	}

	public Map<ChemicalResource, Integer> getReacts() {
		return reacts;
	}

	public Map<ChemicalResource, Integer> getGets() {
		return gets;
	}

	public double getdH() {
		return dH;
	}

	public double getdS() {
		return dS;
	}

	public boolean isReactionCanWork(double temperature) {
		return dH - temperature * dS < 0;
	}

	public final int computeSign() {
		return (int) (reacts.hashCode() * gets.hashCode() - dH * dS);
	}

	public abstract void doReaction(ChemicalMixture mix, double rate);
}
