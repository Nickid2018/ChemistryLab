package com.chemistrylab.reaction;

import com.cj.rdt.*;
import com.cj.rdt.versions.*;
import com.chemistrylab.util.*;

public class ReactionLoader {

	public static final Reactions reactions = new Reactions();

	public static final void loadReaction() throws Exception {
		RDT11.addTag(ReactionEntry.REACTION_ENTRY, ReactionEntry.INSTANCE);
		ReadOnlyRDTObject<Reactions> obj = new ReadOnlyRDTObject<>(reactions,
				new ReadOnlyRDTFile(ResourceManager.getResourceAsStream("assets/models/reaction/reactions.rdt", true)));
		obj.read();
	}

}
