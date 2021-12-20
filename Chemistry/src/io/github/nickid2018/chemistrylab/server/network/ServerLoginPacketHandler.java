package io.github.nickid2018.chemistrylab.server.network;

import io.github.nickid2018.chemistrylab.network.NetworkConnection;
import io.github.nickid2018.chemistrylab.network.NetworkEncryptionException;
import io.github.nickid2018.chemistrylab.network.NetworkEncryptionUtil;
import io.github.nickid2018.chemistrylab.network.NetworkState;
import io.github.nickid2018.chemistrylab.network.packet.listener.ServerLoginPacketListener;
import io.github.nickid2018.chemistrylab.network.packet.login.c2s.C2SEncryptionPacket;
import io.github.nickid2018.chemistrylab.network.packet.login.c2s.C2SHelloPacket;
import io.github.nickid2018.chemistrylab.network.packet.login.c2s.C2SLoginNamePacket;
import io.github.nickid2018.chemistrylab.network.packet.login.s2c.S2CCompressionPacket;
import io.github.nickid2018.chemistrylab.network.packet.login.s2c.S2CEncryptionPacket;
import io.github.nickid2018.chemistrylab.network.packet.login.s2c.S2CHelloPacket;
import io.github.nickid2018.chemistrylab.network.packet.login.s2c.S2CLoginSuccessPacket;
import io.github.nickid2018.chemistrylab.server.AbstractServer;
import io.github.nickid2018.chemistrylab.text.Text;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Random;

/*
 * Login Process:
 *                                      // IF ENCRYPTION                                                                            // IF COMPRESSION
 * CLIENT  --    C2SHelloPacket     | [Generate AES Key]  →  C2SEncryptionPacket      | [Handle Server Info] → C2SLoginNamePacket  | [Start compression]    |  [Login Succeeded]
 *       connect      ↓             |        ↑                        ↓               |           ↑                    ↓           |         ↑              |          ↑
 * SERVER  --  [check encryption] → | S2CEncryptionPacket  [Verify client, get key] → |    S2CHelloPacket      [Get Client Info] → | S2CCompressionPacket → | S2CLoginSuccessPacket
 *
 * Disconnect:
 *
 * >Client
 *
 * CLIENT    --          DisconnectPacket     →     DISCONNECT
 *       Any Process            ↓                CONNECTION BREAKS
 * SERVER    --      [Fill disconnect reason]     DISPLAY REASON
 *
 * >Server
 *
 * CLIENT    --      [Fill disconnect reason]     DISPLAY REASON
 *       Any Process            ↑                CONNECTION BREAKS
 * SERVER    --          DisconnectPacket     →     DISCONNECT
 */
public class ServerLoginPacketHandler implements ServerLoginPacketListener {

    private final AbstractServer server;
    private final NetworkConnection connection;
    private final byte[] randomNonce = new byte[4];
    private String userName;

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
        if (server.getSettings().encrypt && !connection.isLocalConnection()) {
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
            SecretKey secretKey = NetworkEncryptionUtil.decryptSecretKey(server.getPair().getPrivate(), packet.encryptedKey);
            connection.setupEncryption(secretKey);
            connection.sendPacket(S2CHelloPacket.createPacket(server.getName()));
        } catch (NetworkEncryptionException e) {
            NetworkConnection.NETWORK_LOGGER.error("{} lost connection in login process : {}",
                    userName != null ? userName : connection.toString(), e.getMessage());
        }
    }

    @Override
    public void onDisconnect(Text text) {
        connection.disconnect(text);
    }

    @Override
    public void asyncOnDisconnect(Text text) {
        NetworkConnection.NETWORK_LOGGER.info("{} lost connection in login process: {}",
                userName != null ? userName : connection.toString(), text.getValue());
        if (userName != null)
            server.removePlayer(userName);
    }

    public NetworkConnection getConnection() {
        return connection;
    }

}
