package com.github.nickid2018.chemistrylab.reaction;

import com.github.nickid2018.rdt.*;
import com.github.nickid2018.rdt.versions.*;
import com.github.nickid2018.chemistrylab.resource.ResourceManager;
import com.github.nickid2018.chemistrylab.util.*;

public class ReactionLoader {

	public static final Reactions reactions = new Reactions();

	public static final void loadReaction() throws Exception {
		RDT11.addTag(ReactionEntry.REACTION_ENTRY, ReactionEntry.INSTANCE);
		ReadOnlyRDTObject<Reactions> obj = new ReadOnlyRDTObject<>(reactions,
				new ReadOnlyRDTFile(ResourceManager.getResourceAsStream("assets/models/reaction/reactions.rdt", true)));
		obj.read();
	}

}
