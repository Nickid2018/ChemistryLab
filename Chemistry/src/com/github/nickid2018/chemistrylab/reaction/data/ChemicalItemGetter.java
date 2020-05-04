package com.github.nickid2018.chemistrylab.reaction.data;

import com.github.nickid2018.chemistrylab.util.pool.*;
import com.github.nickid2018.chemistrylab.chemicals.*;

public class ChemicalItemGetter {

	public static ChemicalItem newItem(ChemicalResource resource, ChemicalState state) {
		ChemicalItem item = Pools.obtain(ChemicalItem.class);
		item.set(resource, state);
		return item;
	}

	public static void free(ChemicalItem... items) {
		for (ChemicalItem item : items)
			Pools.free(item);
	}
}
