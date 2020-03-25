package com.github.nickid2018.chemistrylab.event;

import com.github.nickid2018.chemistrylab.reaction.*;

public class ReactionWorkPre {

	public final ChemicalMixture mixture;
	public final ReactionController controller;

	public ReactionWorkPre(ChemicalMixture mix, ReactionController control) {
		mixture = mix;
		controller = control;
	}
}
