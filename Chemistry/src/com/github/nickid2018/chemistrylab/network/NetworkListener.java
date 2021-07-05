package com.github.nickid2018.chemistrylab.network;

import com.github.nickid2018.chemistrylab.text.*;

public interface NetworkListener {
	
	public void tick();

	public void onDisconnect(Text text);
}
