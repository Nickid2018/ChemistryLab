package com.chemistrylab.reaction;

import java.util.*;
import com.chemistrylab.eventbus.*;

public class ReactionController implements EventBusListener {

	private ArrayList<Reaction> reactions = new ArrayList<>();
	private ChemicalMixture shadow;
	private ChemicalMixture mix;

	public ReactionController(ChemicalMixture mix) {
		this.mix = mix;
		shadow = new ChemicalMixture(true);
		shadow.copy(mix);
	}

	public void setChemicals(ChemicalMixture mix) {
		this.mix = mix;
		shadow.copy(mix);
	}

	public ChemicalMixture getChemicals() {
		return mix;
	}

	@Override
	public boolean receiveEvents(Event e) {
		return e.equals(Ticker.NEXT_TICK) && e.equals(ChemicalMixture.CHEMICAL_CHANGED);
	}

	@Override
	public void listen(Event e) {
		if (e.equals(Ticker.NEXT_TICK)) {
			for (Reaction r : reactions) {
				r.doReaction(shadow);
			}
			if (!shadow.equals(mix)) {
				mix.copy(shadow);
			}
		} else {
			
		}
	}

}
