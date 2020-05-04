package com.github.nickid2018.chemistrylab.reaction.data;

import com.github.nickid2018.chemistrylab.util.pool.*;
import com.github.nickid2018.chemistrylab.chemicals.*;

public class ChemicalItem implements Poolable {

	public ChemicalResource resource;
	public ChemicalState state;

	public ChemicalItem() {
	}

	public ChemicalItem(ChemicalItem item) {
		resource = item.resource;
		state = item.state;
	}

	public ChemicalItem(ChemicalResource resource, ChemicalState state) {
		this.resource = resource;
		this.state = state;
	}

	@Override
	public int hashCode() {
		if (resource == null || state == null)
			return -1;
		return resource.getCAS().hashCode() + state.ordinal();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ChemicalItem))
			return false;
		ChemicalItem item = (ChemicalItem) obj;
		return item.resource == resource && item.state == state;
	}

	public ChemicalItem set(ChemicalResource resource, ChemicalState state) {
		this.resource = resource;
		this.state = state;
		return this;
	}

	@Override
	public void reset() {
		resource = null;
		state = null;
	}
}
