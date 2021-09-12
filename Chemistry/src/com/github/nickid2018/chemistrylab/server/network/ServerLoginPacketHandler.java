package com.github.nickid2018.chemistrylab.server.network;

import com.github.nickid2018.chemistrylab.network.NetworkConnection;
import com.github.nickid2018.chemistrylab.network.NetworkState;
import com.github.nickid2018.chemistrylab.network.login.C2SLoginNamePacket;
import com.github.nickid2018.chemistrylab.network.login.S2CCompressionPacket;
import com.github.nickid2018.chemistrylab.network.login.S2CLoginSuccessPacket;
import com.github.nickid2018.chemistrylab.network.login.ServerLoginPacketListener;
import com.github.nickid2018.chemistrylab.server.AbstractServer;
import com.github.nickid2018.chemistrylab.text.Text;

public class ServerLoginPacketHandler implements ServerLoginPacketListener {

    private final AbstractServer server;
    private final NetworkConnection connection;
    private String userName;

    public ServerLoginPacketHandler(AbstractServer server, NetworkConnection connection) {
        this.server = server;
        this.connection = connection;
    }

    @Override
    public void tick() {

    }

    @Override
    public void handleName(C2SLoginNamePacket packet) {
        server.addPlayer(packet.userName);
        this.userName = packet.userName;
        if (server.getSettings().compressLimit > 0 && !connection.isLocalConnection()) {
            int limit = server.getSettings().compressLimit;
            connection.sendPacket(S2CCompressionPacket.createPacket(limit), future -> connection.setCompression(limit));
        }
        connection.sendPacket(S2CLoginSuccessPacket.createPacket(userName));
        connection.setListener(new ServerPlayPacketHandler(server, connection, userName));
        connection.updateState(NetworkState.PLAY);
    }

    @Override
    public void onDisconnect(Text text) {
        NetworkConnection.NETWORK_LOGGER.info("{} lost connection in login process: {}", userName, text.getValue());
    }

    public NetworkConnection getConnection() {
        return connection;
    }

}
