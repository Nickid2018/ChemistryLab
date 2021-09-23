package com.github.nickid2018.chemistrylab.network.packet.login.s2c;

import com.github.nickid2018.chemistrylab.network.FriendlyByteBuf;
import com.github.nickid2018.chemistrylab.network.NetworkPacket;
import com.github.nickid2018.chemistrylab.network.NetworkSide;
import com.github.nickid2018.chemistrylab.network.SideOnly;
import com.github.nickid2018.chemistrylab.network.packet.listener.ClientLoginPacketListener;

public class S2CHelloPacket implements NetworkPacket<ClientLoginPacketListener> {

    public String serverName;

    public static S2CHelloPacket createPacket(String serverName) {
        S2CHelloPacket packet = new S2CHelloPacket();
        packet.serverName = serverName;
        return packet;
    }

    @Override
    @SideOnly(NetworkSide.SERVER)
    public void writePacket(FriendlyByteBuf buf) {
        buf.writeUtf(serverName);
    }

    @Override
    @SideOnly(NetworkSide.CLIENT)
    public void readPacket(FriendlyByteBuf buf) {
        serverName = buf.readUtf();
    }

    @Override
    @SideOnly(NetworkSide.CLIENT)
    public void applyToListener(ClientLoginPacketListener listener) {
        listener.handleHello(this);
    }
}
