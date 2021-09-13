package com.github.nickid2018.chemistrylab.network.listener;

import com.github.nickid2018.chemistrylab.network.NetworkListener;
import com.github.nickid2018.chemistrylab.network.play.c2s.C2SChatPacket;

public interface ServerPlayPacketListener extends NetworkListener {

    void handleChatMessage(C2SChatPacket packet);
}
