package com.github.nickid2018.chemistrylab.reaction;

import java.util.*;

import com.github.nickid2018.chemistrylab.chemicals.*;

public abstract class Reaction {

	protected double dH;
	protected double dS;
	protected Map<ChemicalResource, Integer> reacts = new HashMap<>();
	protected Map<ChemicalResource, Integer> gets = new HashMap<>();

	public Reaction(Map<ChemicalResource, Integer> reacts, Map<ChemicalResource, Integer> gets, double dH, double dS) {
		this.reacts = reacts;
		this.gets = gets;
		this.dH = dH;
		this.dS = dS;
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

	public final int computeSign() {
		return (int) (reacts.hashCode() * gets.hashCode() - dH * dS);
	}

	public abstract void doReaction(ChemicalMixture mix);
}
