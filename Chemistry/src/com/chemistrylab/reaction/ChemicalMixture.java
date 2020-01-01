package com.chemistrylab.reaction;

import java.util.*;
import com.chemistrylab.eventbus.*;
import com.chemistrylab.chemicals.*;

public class ChemicalMixture extends HashMap<ChemicalResource,Unit>{
	
	public static final Event CHEMICAL_CHANGED = Event.createNewEvent();
	public static final int CHEMICAL_ADDED = 0x0;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8142851189900240337L;
	
	private boolean shadow;
	private ReactionController contr;

	public ChemicalMixture() {
		this(false);
	}
	
	ChemicalMixture(boolean shadow){
		this.shadow = shadow;
		if(!shadow){
			contr = new ReactionController(this);
		}
	}
	
	public void copy(ChemicalMixture mix){
		clear();
		putAll(mix);
	}
	
	@Override
	public Unit put(ChemicalResource key, Unit value) {
		if (containsKey(key)) {
			return replace(key, value.add(get(key)));
		} else {
			Unit u = super.put(key, value);
			Event post = CHEMICAL_CHANGED.clone();
			post.putExtra(CHEMICAL_ADDED, key);
			EventBus.postEvent(post);
			return u;
		}
	}

	public ReactionController getController() {
		if(shadow)
			throw new RuntimeException("Shadow Mixture");
		return contr;
	}
}
