package io.github.nickid2018.chemistrylab.network.packet.login.c2s;

import io.github.nickid2018.chemistrylab.network.FriendlyByteBuf;
import io.github.nickid2018.chemistrylab.network.NetworkPacket;
import io.github.nickid2018.chemistrylab.network.NetworkSide;
import io.github.nickid2018.chemistrylab.network.SideOnly;
import io.github.nickid2018.chemistrylab.network.packet.listener.ServerLoginPacketListener;

public class C2SLoginNamePacket implements NetworkPacket<ServerLoginPacketListener> {

    public String userName;

    public static C2SLoginNamePacket createPacket(String name) {
        C2SLoginNamePacket packet = new C2SLoginNamePacket();
        packet.userName = name;
        return packet;
    }

    @Override
    @SideOnly(NetworkSide.CLIENT)
    public void writePacket(FriendlyByteBuf buf) {
        buf.writeUtf(userName);
    }

    @Override
    @SideOnly(NetworkSide.SERVER)
    public void readPacket(FriendlyByteBuf buf) {
        userName = buf.readUtf();
    }

    @Override
    @SideOnly(NetworkSide.SERVER)
    public void applyToListener(ServerLoginPacketListener listener) {
        listener.handleName(this);
    }
}
