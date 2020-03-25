package com.github.nickid2018.chemistrylab.reaction;

import java.util.*;
import com.google.common.eventbus.*;
import com.github.nickid2018.chemistrylab.util.*;
import com.github.nickid2018.chemistrylab.event.*;
import com.github.nickid2018.chemistrylab.chemicals.*;
import com.github.nickid2018.chemistrylab.layer.container.*;

public class ReactionController {

	private ArrayList<Reaction> reactions = new ArrayList<>();
	private ChemicalMixture shadow;
	private ChemicalMixture mix;

	public ReactionController(ChemicalMixture mix) {
		this.mix = mix;
		shadow = new ChemicalMixture(true);
		shadow.copy(mix);
		AbstractContainer.CHEMICAL_BUS.register(this);
		Ticker.TICK_EVENT_BUS.register(this);
	}

	public ChemicalMixture getChemicals() {
		return mix;
	}

	public ChemicalMixture getShadowMixture() {
		return shadow;
	}

	private boolean stopNow = false;
	private boolean lastTickOver = true;

	@Subscribe
	public void listenChemicalChange(ChemicalChangedEvent e) {
		// Stop tick-update
		stopNow = true;
		if (e.mixture != mix)
			return;
		if (e.item != null) {
			ChemicalResource chem = e.item.resource;
			// Add Reactions
			chem.foreach(r -> {
				Set<ChemicalResource> reacts = r.reacts.keySet();
				// Check Chemical
				for (ChemicalResource c : reacts) {
					if (c.equals(chem))
						continue;
					if (!mix.containsChemical(c))
						return;
				}
				reactions.add(r);
			});
		}
		// Override shadow mixture
		shadow.copy(mix);
	}

	@Subscribe
	public void onTickUpdate(TickerEvent e) {
		// Tick update
		if (!lastTickOver) {
			e.isSomeCancelled = true;
			e.cancelledUnits++;
			return;
		}
		lastTickOver = false;
		// Destination Speed = 0.04s x Environment.speed
		if (!stopNow)
			// Something, like dissolve, need to work before reaction works
			AbstractContainer.CHEMICAL_BUS.post(new ReactionWorkPre(shadow, this));
		for (Reaction r : reactions) {
			if (stopNow) {
				stopNow = false;
				shadow.copy(mix);
				break;
			}
			r.doReaction(shadow);
		}
		shadow.temperatureTransfer();
		if (stopNow) {
			// Override display mixture
			if (!shadow.equals(mix)) {
				mix.copy(shadow);
			}
			stopNow = false;
		}
		lastTickOver = true;
	}

}
