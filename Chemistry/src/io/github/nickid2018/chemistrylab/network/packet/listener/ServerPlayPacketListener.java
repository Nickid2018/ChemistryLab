package io.github.nickid2018.chemistrylab.network.packet.listener;

import io.github.nickid2018.chemistrylab.network.packet.play.c2s.C2SChatPacket;

public interface ServerPlayPacketListener extends NetworkListener {

    void handleChatMessage(C2SChatPacket packet);
}
