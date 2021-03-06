package com.github.nickid2018.chemistrylab.reaction;

import java.util.*;
import java.util.stream.*;
import java.util.concurrent.locks.*;
import com.google.common.base.*;
import com.github.nickid2018.chemistrylab.event.*;
import com.github.nickid2018.chemistrylab.chemicals.*;
import com.github.nickid2018.chemistrylab.container.*;
import com.github.nickid2018.chemistrylab.reaction.data.*;
import com.github.nickid2018.chemistrylab.chemicals.attributes.*;

public class ChemicalMixture extends HashMap<ChemicalItem, Unit> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8142851189900240337L;

	private double temperature = Environment.getTemperature();
	private boolean shadow;
	private ReactionController contr;
	private ReentrantLock lock = new ReentrantLock();

	public ChemicalMixture() {
		this(false);
	}

	ChemicalMixture(boolean shadow) {
		this.shadow = shadow;
		if (!shadow) {
			contr = new ReactionController(this);
		}
	}

	public void copy(ChemicalMixture mix) {
		lock.lock();
		clear();
		putAll(mix);
		lock.unlock();
		temperature = mix.temperature;
	}

	@Override
	public Unit put(ChemicalItem key, Unit value) {
		Preconditions.checkArgument(!value.isListen(), "This unit isn't a non-listen unit.");
		value = new Unit(key, Unit.UNIT_MOLE, value.toMol()).setNotListen();
		ChemicalItem item;
		lock.lock();
		Unit ret;
		// value is a temporatory object
		if (containsKey(key)) {
			item = null;
			ret = get(key).add(value);
		} else {
			item = key;
			ret = value.clone();
		}
		ChemicalChangedEvent post = Event.newEvent(ChemicalChangedEvent.class, this, item);
		AbstractContainer.CHEMICAL_BUS.post(post);
		if (!post.isCancelled())
			super.put(key, ret);
		Event.free(post);
		lock.unlock();
		return ret;
	}

	@Override
	public Unit replace(ChemicalItem key, Unit value) {
		if (value.getNum() <= 0)
			return remove(key);
		return put(key, value);
	}

	public boolean containsChemical(ChemicalResource resource) {
		ChemicalState[] states = ChemicalState.values();
		for (int i = 0; i < states.length; i++) {
			if (containsKey(new ChemicalItem(resource, states[i])))
				return true;
		}
		return false;
	}

	public Set<Map.Entry<ChemicalItem, Unit>> filterByState(ChemicalState state) {
		return entrySet().stream().filter(en -> en.getKey().state == state).collect(Collectors.toSet());
	}

	public Unit get(ChemicalItem key) {
		Unit ret = super.get(key);
		return ret == null ? Unit.NULL_UNIT : ret;
	}

	public Unit getChemicalItem(ChemicalResource res, ChemicalState state) {
		return get(new ChemicalItem(res, state));
	}

	// For aq
	public double getConcentration(ChemicalResource res) {
		return get(new ChemicalItem(res, ChemicalState.AQUEOUS)).getNum()
				/ (get(Dissolve.DEFAULT_SOLVENT).getNum() * ChemicalLoader.CHEMICALS.get("H2O").getMess() / 1000);
	}

	public ReactionController getController() {
		if (shadow)
			throw new RuntimeException("Shadow Mixture");
		return contr;
	}

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

//	public 

	public void temperatureTransfer() {
		// TODO

	}
}
