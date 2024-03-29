package io.github.nickid2018.chemistrylab.network.packet.login.c2s;

import io.github.nickid2018.chemistrylab.network.FriendlyByteBuf;
import io.github.nickid2018.chemistrylab.network.NetworkPacket;
import io.github.nickid2018.chemistrylab.network.NetworkSide;
import io.github.nickid2018.chemistrylab.network.SideOnly;
import io.github.nickid2018.chemistrylab.network.packet.listener.ServerLoginPacketListener;

public class C2SEncryptionPacket implements NetworkPacket<ServerLoginPacketListener> {

    public byte[] encryptedNonce;
    public byte[] encryptedKey;

    public static C2SEncryptionPacket createPacket(byte[] encryptedNonce, byte[] encryptedKey) {
        C2SEncryptionPacket packet = new C2SEncryptionPacket();
        packet.encryptedNonce = encryptedNonce;
        packet.encryptedKey = encryptedKey;
        return packet;
    }

    @Override
    @SideOnly(NetworkSide.CLIENT)
    public void writePacket(FriendlyByteBuf buf) {
        buf.writeByteArray(encryptedNonce);
        buf.writeByteArray(encryptedKey);
    }

    @Override
    @SideOnly(NetworkSide.SERVER)
    public void readPacket(FriendlyByteBuf buf) {
        encryptedNonce = buf.readByteArray();
        encryptedKey = buf.readByteArray();
    }

    @Override
    @SideOnly(NetworkSide.SERVER)
    public void applyToListener(ServerLoginPacketListener listener) {
        listener.handleEncryption(this);
    }
}
