package com.github.nickid2018.chemistrylab.reaction;

import com.github.nickid2018.chemistrylab.chemicals.*;

public class ChemicalItem {

	public final ChemicalResource resource;
	public final ChemicalState state;

	public ChemicalItem(ChemicalResource resource, ChemicalState state) {
		this.resource = resource;
		this.state = state;
	}

	public ChemicalItem(ChemicalItem item) {
		resource = item.resource;
		state = item.state;
	}

	@Override
	public int hashCode() {
		return resource.getCAS().hashCode() + state.ordinal();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ChemicalItem))
			return false;
		ChemicalItem item = (ChemicalItem) obj;
		return item.resource == resource && item.state == state;
	}
}
