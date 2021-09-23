package com.github.nickid2018.chemistrylab.network.packet.login.s2c;

import com.github.nickid2018.chemistrylab.network.FriendlyByteBuf;
import com.github.nickid2018.chemistrylab.network.NetworkPacket;
import com.github.nickid2018.chemistrylab.network.NetworkSide;
import com.github.nickid2018.chemistrylab.network.SideOnly;
import com.github.nickid2018.chemistrylab.network.packet.listener.ClientLoginPacketListener;

public class S2CCompressionPacket implements NetworkPacket<ClientLoginPacketListener> {

    public int limit;

    public static S2CCompressionPacket createPacket(int limit) {
        S2CCompressionPacket packet = new S2CCompressionPacket();
        packet.limit = limit;
        return packet;
    }

    @Override
    @SideOnly(NetworkSide.SERVER)
    public void writePacket(FriendlyByteBuf buf) {
        buf.writeVarInt(limit);
    }

    @Override
    @SideOnly(NetworkSide.CLIENT)
    public void readPacket(FriendlyByteBuf buf) {
        limit = buf.readVarInt();
    }

    @Override
    @SideOnly(NetworkSide.CLIENT)
    public void applyToListener(ClientLoginPacketListener listener) {
        listener.handleCompression(this);
    }
}
