package com.github.nickid2018.chemistrylab.server.network;

import com.github.nickid2018.chemistrylab.network.NetworkConnection;
import com.github.nickid2018.chemistrylab.network.NetworkEncryptionException;
import com.github.nickid2018.chemistrylab.network.NetworkEncryptionUtil;
import com.github.nickid2018.chemistrylab.network.NetworkState;
import com.github.nickid2018.chemistrylab.network.listener.ServerLoginPacketListener;
import com.github.nickid2018.chemistrylab.network.login.c2s.C2SEncryptionPacket;
import com.github.nickid2018.chemistrylab.network.login.c2s.C2SHelloPacket;
import com.github.nickid2018.chemistrylab.network.login.c2s.C2SLoginNamePacket;
import com.github.nickid2018.chemistrylab.network.login.s2c.S2CCompressionPacket;
import com.github.nickid2018.chemistrylab.network.login.s2c.S2CEncryptionPacket;
import com.github.nickid2018.chemistrylab.network.login.s2c.S2CHelloPacket;
import com.github.nickid2018.chemistrylab.network.login.s2c.S2CLoginSuccessPacket;
import com.github.nickid2018.chemistrylab.server.AbstractServer;
import com.github.nickid2018.chemistrylab.text.Text;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Random;

public class ServerLoginPacketHandler implements ServerLoginPacketListener {

    private final AbstractServer server;
    private final NetworkConnection connection;
    private final byte[] randomNonce = new byte[4];
    private String userName;
    private SecretKey secretKey;

    public ServerLoginPacketHandler(AbstractServer server, NetworkConnection connection) {
        this.server = server;
        this.connection = connection;
        if (server.getSettings().encrypt)
            new Random().nextBytes(randomNonce);
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

    public void handleHello(C2SHelloPacket packet) {
        if (server.getSettings().encrypt) {
            connection.sendPacket(S2CEncryptionPacket.createPacket(server.getPair().getPublic().getEncoded(), randomNonce));
        } else
            connection.sendPacket(S2CHelloPacket.createPacket(server.getName()));
    }

    @Override
    public void handleEncryption(C2SEncryptionPacket packet) {
        try {
            byte[] returnNonce = NetworkEncryptionUtil.decrypt(server.getPair().getPrivate(), packet.encryptedNonce);
            if (!Arrays.equals(randomNonce, returnNonce))
                throw new NetworkEncryptionException("Invalid nonce!");
            secretKey = NetworkEncryptionUtil.decryptSecretKey(server.getPair().getPrivate(), packet.encryptedKey);
            connection.setupEncryption(secretKey);
            connection.sendPacket(S2CHelloPacket.createPacket(server.getName()));
        } catch (NetworkEncryptionException e) {
            NetworkConnection.NETWORK_LOGGER.error("{} lost connection in login process : {}", userName, e.getMessage());
        }
    }

    @Override
    public void onDisconnect(Text text) {
        NetworkConnection.NETWORK_LOGGER.info("{} lost connection in login process: {}", userName, text.getValue());
        if (userName != null)
            server.removePlayer(userName);
    }

    public NetworkConnection getConnection() {
        return connection;
    }

}
