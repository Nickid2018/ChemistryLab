package com.github.nickid2018.chemistrylab.reaction;

import java.util.*;
import com.google.common.eventbus.*;
import com.github.nickid2018.chemistrylab.util.*;
import com.github.nickid2018.chemistrylab.event.*;
import com.github.nickid2018.chemistrylab.chemicals.*;
import com.github.nickid2018.chemistrylab.container.*;

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

	// Run in different thread, so it should be volatile
	private volatile boolean stopNow = false;

	@Subscribe
	// Run in CONCURRENT THREAD
	public void onChemicalChange(ChemicalChangedEvent e) {
		// Stop tick-update
		stopNow = true;

		// Ensure mixture
		if (e.mixture != mix)
			return;

		// Check update reaction
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

		// Ensure flush successfully
		stopNow = true;

		// Under dream condition, the tick should be stopped once. But under worst
		// condition, it may stop twice and cause some wrong things.
	}

	@Subscribe
	// Run in TICK-COUNT-THREAD
	public void onTickUpdate(TickerEvent e) {
		// Destination Speed = e.reactionRate

		if (!isStop())
			// Something, like dissolve, needs to work before reaction works
			AbstractContainer.CHEMICAL_BUS.post(new ReactionWorkPre(shadow, this));

		// Reaction Run!
		for (Reaction r : reactions) {
			if (isStop()) {
				return;
			}
			r.doReaction(shadow, e.reactionRate);
		}

//		shadow.temperatureTransfer();

		// Copy and over
		if (!isStop() && !shadow.equals(mix)) {
			mix.copy(shadow);
		}
	}

	private boolean isStop() {
		boolean stop = stopNow;
		if (stopNow) {
			stopNow = false;
			shadow.copy(mix);
		}
		return stop;
	}
}
