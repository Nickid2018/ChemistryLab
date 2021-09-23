package com.github.nickid2018.chemistrylab.network.packet.login.s2c;

import com.github.nickid2018.chemistrylab.network.FriendlyByteBuf;
import com.github.nickid2018.chemistrylab.network.NetworkPacket;
import com.github.nickid2018.chemistrylab.network.NetworkSide;
import com.github.nickid2018.chemistrylab.network.SideOnly;
import com.github.nickid2018.chemistrylab.network.packet.listener.ClientLoginPacketListener;

public class S2CEncryptionPacket implements NetworkPacket<ClientLoginPacketListener> {

    public byte[] serverPublicKey;
    public byte[] nonce;

    public static S2CEncryptionPacket createPacket(byte[] serverPublicKey, byte[] nonce) {
        S2CEncryptionPacket packet = new S2CEncryptionPacket();
        packet.serverPublicKey = serverPublicKey;
        packet.nonce = nonce;
        return packet;
    }

    @Override
    @SideOnly(NetworkSide.SERVER)
    public void writePacket(FriendlyByteBuf buf) {
        buf.writeByteArray(serverPublicKey);
        buf.writeByteArray(nonce);
    }

    @Override
    @SideOnly(NetworkSide.CLIENT)
    public void readPacket(FriendlyByteBuf buf) {
        serverPublicKey = buf.readByteArray();
        nonce = buf.readByteArray();
    }

    @Override
    @SideOnly(NetworkSide.CLIENT)
    public void applyToListener(ClientLoginPacketListener listener) {
        listener.handleEncryption(this);
    }
}
