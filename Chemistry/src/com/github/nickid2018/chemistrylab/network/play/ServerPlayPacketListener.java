package com.github.nickid2018.chemistrylab.network.play;

import com.github.nickid2018.chemistrylab.network.*;

public interface ServerPlayPacketListener extends NetworkListener {

	public void handleChatMessage(C2SChatPacket packet);
}
