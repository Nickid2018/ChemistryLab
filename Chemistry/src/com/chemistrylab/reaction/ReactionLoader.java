package com.chemistrylab.reaction;

import com.cj.rdt.*;
import com.cj.rdt.versions.*;

public class ReactionLoader {
	
	public static final Reactions reactions = new Reactions();

	public static final void loadReaction() throws Exception {
		RDT11.addTag(ReactionEntry.REACTION_ENTRY, ReactionEntry.INSTANCE);
		RDTObject<Reactions> obj = new RDTObject<>(reactions,
				ReactionLoader.class.getResource("/assets/models/reaction/reactions.rdt").getFile());
		obj.read();
	}

}
