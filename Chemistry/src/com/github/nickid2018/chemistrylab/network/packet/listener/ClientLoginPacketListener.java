package com.github.nickid2018.chemistrylab.network.packet.listener;

import com.github.nickid2018.chemistrylab.network.packet.login.s2c.S2CCompressionPacket;
import com.github.nickid2018.chemistrylab.network.packet.login.s2c.S2CEncryptionPacket;
import com.github.nickid2018.chemistrylab.network.packet.login.s2c.S2CHelloPacket;
import com.github.nickid2018.chemistrylab.network.packet.login.s2c.S2CLoginSuccessPacket;

public interface ClientLoginPacketListener extends NetworkListener {

    void handleLoginSuccess(S2CLoginSuccessPacket packet);

    void handleCompression(S2CCompressionPacket packet);

    void handleEncryption(S2CEncryptionPacket packet);

    void handleHello(S2CHelloPacket packet);
}
