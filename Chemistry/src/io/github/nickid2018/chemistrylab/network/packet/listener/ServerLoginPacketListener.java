package io.github.nickid2018.chemistrylab.network.packet.listener;

import io.github.nickid2018.chemistrylab.network.packet.login.c2s.C2SEncryptionPacket;
import io.github.nickid2018.chemistrylab.network.packet.login.c2s.C2SHelloPacket;
import io.github.nickid2018.chemistrylab.network.packet.login.c2s.C2SLoginNamePacket;

public interface ServerLoginPacketListener extends NetworkListener {

    void handleName(C2SLoginNamePacket packet);

    void handleEncryption(C2SEncryptionPacket packet);

    void handleHello(C2SHelloPacket packet);
}
