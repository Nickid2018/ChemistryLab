package com.chemistrylab.reaction;

import java.util.*;
import java.util.concurrent.locks.*;
import com.chemistrylab.eventbus.*;
import com.chemistrylab.chemicals.*;

public class ChemicalMixture extends HashMap<ChemicalResource,Unit>{
	
	public static final Event CHEMICAL_CHANGED = Event.createNewEvent();
	public static final int CHEMICAL_ADDED = 0x0;
	public static final int CHEMICAL_CHANGE = 0x1;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8142851189900240337L;
	
	private boolean shadow;
	private ReactionController contr;
	private ReentrantLock lock = new ReentrantLock();

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
		lock.lock();
		clear();
		putAll(mix);
		lock.unlock();
	}
	
	@Override
	public Unit put(ChemicalResource key, Unit value) {
		checkNonListen(value);
		lock.lock();
		if (containsKey(key)) {
			Unit ret = replace(key, value.add(get(key)));
			lock.unlock();
			Event post = CHEMICAL_CHANGED.clone();
			post.putExtra(CHEMICAL_CHANGE, null);
			EventBus.postEvent(post);
			return ret;
		} else {
			Unit u = super.put(key, value);
			lock.unlock();
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
	
	static void checkNonListen(Unit u){
		if(EventBus.haveListener(u))
			throw new IllegalArgumentException("This unit isn't a non-listen unit.");
	}
}
