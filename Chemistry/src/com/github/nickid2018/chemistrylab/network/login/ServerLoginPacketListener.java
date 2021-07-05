package com.github.nickid2018.chemistrylab.network.login;

import com.github.nickid2018.chemistrylab.network.*;

public interface ServerLoginPacketListener extends NetworkListener {

	public void handleName(C2SLoginNamePacket packet);
}
