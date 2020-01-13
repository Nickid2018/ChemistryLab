package com.chemistrylab.reaction;

import java.util.*;
import com.chemistrylab.eventbus.*;
import com.chemistrylab.chemicals.*;

public class ReactionController implements EventBusListener {

	private ArrayList<Reaction> reactions = new ArrayList<>();
	private ChemicalMixture shadow;
	private ChemicalMixture mix;

	public ReactionController(ChemicalMixture mix) {
		this.mix = mix;
		shadow = new ChemicalMixture(true);
		shadow.copy(mix);
		EventBus.registerListener(this);
	}

	public ChemicalMixture getChemicals() {
		return mix;
	}

	@Override
	public boolean receiveEvents(Event e) {
		return e.equals(Ticker.NEXT_TICK) && e.equals(ChemicalMixture.CHEMICAL_CHANGED);
	}

	private boolean stopNow = false;
	
	@Override
	public void listen(Event e) {
		if(e.equals(ChemicalMixture.CHEMICAL_CHANGED)){
			stopNow = true;
			Object get;
			if((get = e.getExtra(ChemicalMixture.CHEMICAL_ADDED))!=null){
				ChemicalResource chem = (ChemicalResource) get;
				//Add Reactions
			}
			shadow.copy(mix);
		}else if (e.equals(Ticker.NEXT_TICK)) {
			for (Reaction r : reactions) {
				if(stopNow){
					stopNow = false;
					shadow.copy(mix);
					break;
				}
				r.doReaction(shadow);
			}
			if (!shadow.equals(mix)) {
				mix.copy(shadow);
			}
		}
	}

}
