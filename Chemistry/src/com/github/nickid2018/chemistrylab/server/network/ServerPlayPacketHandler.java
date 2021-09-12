package com.github.nickid2018.chemistrylab.server.network;

import com.github.nickid2018.chemistrylab.network.NetworkConnection;
import com.github.nickid2018.chemistrylab.network.play.C2SChatPacket;
import com.github.nickid2018.chemistrylab.network.play.ServerPlayPacketListener;
import com.github.nickid2018.chemistrylab.server.AbstractServer;
import com.github.nickid2018.chemistrylab.text.Text;

public class ServerPlayPacketHandler implements ServerPlayPacketListener {

    private final String userName;

    public ServerPlayPacketHandler(AbstractServer server, NetworkConnection connection, String userName) {
        this.userName = userName;
    }

    @Override
    public void tick() {

    }

    @Override
    public void handleChatMessage(C2SChatPacket packet) {
        System.out.println(packet.chatMessage);
    }

    @Override
    public void onDisconnect(Text text) {
        NetworkConnection.NETWORK_LOGGER.info("{} lost connection: {}", userName, text.getValue());
    }

}
