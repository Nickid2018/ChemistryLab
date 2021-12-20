package io.github.nickid2018.chemistrylab.network.packet.listener;

import io.github.nickid2018.chemistrylab.network.packet.login.s2c.S2CCompressionPacket;
import io.github.nickid2018.chemistrylab.network.packet.login.s2c.S2CEncryptionPacket;
import io.github.nickid2018.chemistrylab.network.packet.login.s2c.S2CHelloPacket;
import io.github.nickid2018.chemistrylab.network.packet.login.s2c.S2CLoginSuccessPacket;

public interface ClientLoginPacketListener extends NetworkListener {

    void handleLoginSuccess(S2CLoginSuccessPacket packet);

    void handleCompression(S2CCompressionPacket packet);

    void handleEncryption(S2CEncryptionPacket packet);

    void handleHello(S2CHelloPacket packet);
}
