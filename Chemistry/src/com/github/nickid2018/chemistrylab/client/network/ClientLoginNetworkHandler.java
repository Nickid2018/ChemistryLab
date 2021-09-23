package com.github.nickid2018.chemistrylab.client.network;

import com.github.nickid2018.chemistrylab.network.NetworkConnection;
import com.github.nickid2018.chemistrylab.network.NetworkEncryptionException;
import com.github.nickid2018.chemistrylab.network.NetworkEncryptionUtil;
import com.github.nickid2018.chemistrylab.network.NetworkState;
import com.github.nickid2018.chemistrylab.network.packet.listener.ClientLoginPacketListener;
import com.github.nickid2018.chemistrylab.network.packet.login.c2s.C2SEncryptionPacket;
import com.github.nickid2018.chemistrylab.network.packet.login.c2s.C2SHelloPacket;
import com.github.nickid2018.chemistrylab.network.packet.login.c2s.C2SLoginNamePacket;
import com.github.nickid2018.chemistrylab.network.packet.login.s2c.S2CCompressionPacket;
import com.github.nickid2018.chemistrylab.network.packet.login.s2c.S2CEncryptionPacket;
import com.github.nickid2018.chemistrylab.network.packet.login.s2c.S2CHelloPacket;
import com.github.nickid2018.chemistrylab.network.packet.login.s2c.S2CLoginSuccessPacket;
import com.github.nickid2018.chemistrylab.text.Text;

import javax.crypto.SecretKey;
import java.security.Key;

public class ClientLoginNetworkHandler implements ClientLoginPacketListener {

    private final NetworkConnection connection;
    private final String userName;

    public ClientLoginNetworkHandler(NetworkConnection connection, String userName) {
        this.connection = connection;
        this.userName = userName;
        connection.sendPacket(C2SHelloPacket.createPacket());
    }

    @Override
    public void tick() {

    }

    @Override
    public void onDisconnect(Text text) {
        connection.disconnect(text);
    }

    @Override
    public void asyncOnDisconnect(Text text) {
        NetworkConnection.NETWORK_LOGGER.info("{} lost connection in login process: {}", userName, text.getValue());
        // Window...
    }

    @Override
    public void handleLoginSuccess(S2CLoginSuccessPacket packet) {
        if (!userName.equals(packet.userName)) {

        }
        connection.updateState(NetworkState.PLAY);
    }

    @Override
    public void handleCompression(S2CCompressionPacket packet) {
        connection.setCompression(packet.limit);
    }

    @Override
    public void handleEncryption(S2CEncryptionPacket packet) {
        try {
            byte[] nonce = packet.nonce;
            Key serverPublicKey = NetworkEncryptionUtil.readEncodedPublicKey(packet.serverPublicKey);
            byte[] encryptedNonce = NetworkEncryptionUtil.encrypt(serverPublicKey, nonce);
            SecretKey aesKey = NetworkEncryptionUtil.generateKey();
            connection.sendPacket(C2SEncryptionPacket.createPacket(
                    encryptedNonce, NetworkEncryptionUtil.encrypt(serverPublicKey, aesKey.getEncoded())));
            connection.setupEncryption(aesKey);
        } catch (NetworkEncryptionException e) {
            NetworkConnection.NETWORK_LOGGER.fatal("Cannot deserialize key", e);
        }

    }

    @Override
    public void handleHello(S2CHelloPacket packet) {
        connection.sendPacket(C2SLoginNamePacket.createPacket(userName));
    }

}
