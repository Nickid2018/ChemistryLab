package com.github.nickid2018.chemistrylab.network.packet.listener;

import com.github.nickid2018.chemistrylab.network.packet.login.c2s.C2SEncryptionPacket;
import com.github.nickid2018.chemistrylab.network.packet.login.c2s.C2SHelloPacket;
import com.github.nickid2018.chemistrylab.network.packet.login.c2s.C2SLoginNamePacket;

public interface ServerLoginPacketListener extends NetworkListener {

    void handleName(C2SLoginNamePacket packet);

    void handleEncryption(C2SEncryptionPacket packet);

    void handleHello(C2SHelloPacket packet);
}