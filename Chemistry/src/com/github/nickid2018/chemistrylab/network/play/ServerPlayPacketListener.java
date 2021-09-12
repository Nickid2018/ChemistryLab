package com.github.nickid2018.chemistrylab.network.play;

import com.github.nickid2018.chemistrylab.network.NetworkListener;

public interface ServerPlayPacketListener extends NetworkListener {

    void handleChatMessage(C2SChatPacket packet);
}
