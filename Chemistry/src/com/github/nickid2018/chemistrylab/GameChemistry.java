package com.github.nickid2018.chemistrylab;

import com.github.mmc1234.pinkengine.*;
import com.github.nickid2018.chemistrylab.init.*;

public class GameChemistry extends PinkEngine{

	@Override
	public void create() {
		super.create();
		World loadWorld = new LoadingWorld(this);
		setWorld(loadWorld);
	}
}
