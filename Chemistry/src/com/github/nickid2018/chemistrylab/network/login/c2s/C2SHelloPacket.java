package com.github.nickid2018.chemistrylab.network.login.c2s;

import com.github.nickid2018.chemistrylab.network.FriendlyByteBuf;
import com.github.nickid2018.chemistrylab.network.NetworkPacket;
import com.github.nickid2018.chemistrylab.network.NetworkSide;
import com.github.nickid2018.chemistrylab.network.SideOnly;
import com.github.nickid2018.chemistrylab.network.listener.ServerLoginPacketListener;

public class C2SHelloPacket implements NetworkPacket<ServerLoginPacketListener> {

    public static C2SHelloPacket createPacket() {
        return new C2SHelloPacket();
    }

    @Override
    @SideOnly(NetworkSide.CLIENT)
    public void writePacket(FriendlyByteBuf buf) {
    }

    @Override
    @SideOnly(NetworkSide.SERVER)
    public void readPacket(FriendlyByteBuf buf) {
    }

    @Override
    @SideOnly(NetworkSide.SERVER)
    public void applyToListener(ServerLoginPacketListener listener) {
        listener.handleHello(this);
    }
}
