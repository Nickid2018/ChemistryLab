package com.github.nickid2018.chemistrylab.network.login;

import com.github.nickid2018.chemistrylab.network.NetworkListener;

public interface ServerLoginPacketListener extends NetworkListener {

    void handleName(C2SLoginNamePacket packet);
}
