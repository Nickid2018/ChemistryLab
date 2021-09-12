package com.github.nickid2018.chemistrylab.network.login;

import com.github.nickid2018.chemistrylab.network.NetworkListener;

public interface ClientLoginPacketListener extends NetworkListener {

    void handleLoginSuccess(S2CLoginSuccessPacket packet);

    void handleCompression(S2CCompressionPacket packet);
}
