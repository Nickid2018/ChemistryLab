package io.github.nickid2018.chemistrylab.server.network;

import io.github.nickid2018.chemistrylab.network.NetworkConnection;
import io.github.nickid2018.chemistrylab.network.packet.listener.ServerPlayPacketListener;
import io.github.nickid2018.chemistrylab.network.packet.play.c2s.C2SChatPacket;
import io.github.nickid2018.chemistrylab.server.AbstractServer;
import io.github.nickid2018.chemistrylab.text.Text;

public class ServerPlayPacketHandler implements ServerPlayPacketListener {

    private final String userName;
    private final NetworkConnection connection;

    public ServerPlayPacketHandler(AbstractServer server, NetworkConnection connection, String userName) {
        this.userName = userName;
        this.connection = connection;
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
        connection.disconnect(text);
    }

    @Override
    public void asyncOnDisconnect(Text text) {
        NetworkConnection.NETWORK_LOGGER.info("{} lost connection: {}", userName, text.getValue());
    }

}
