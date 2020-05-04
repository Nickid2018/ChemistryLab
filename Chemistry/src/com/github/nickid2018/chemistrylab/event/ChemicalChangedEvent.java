package com.github.nickid2018.chemistrylab.event;

import com.github.nickid2018.chemistrylab.reaction.*;
import com.github.nickid2018.chemistrylab.reaction.data.*;

public class ChemicalChangedEvent extends CancellableEvent {

	public ChemicalItem item;
	public ChemicalMixture mixture;

	@Override
	public void reset() {
		item = null;
		mixture = null;
	}

	@Override
	public Event set(Object... o) {
		item = (ChemicalItem) o[0];
		mixture = (ChemicalMixture) o[1];
		return this;
	}

}
