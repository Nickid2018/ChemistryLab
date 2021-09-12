package com.github.nickid2018.chemistrylab.network.login;

import com.github.nickid2018.chemistrylab.network.FriendlyByteBuf;
import com.github.nickid2018.chemistrylab.network.NetworkPacket;
import com.github.nickid2018.chemistrylab.network.NetworkSide;
import com.github.nickid2018.chemistrylab.network.SideOnly;

public class S2CLoginSuccessPacket implements NetworkPacket<ClientLoginPacketListener> {

    public String userName;

    public static S2CLoginSuccessPacket createPacket(String name) {
        S2CLoginSuccessPacket packet = new S2CLoginSuccessPacket();
        packet.userName = name;
        return packet;
    }

    @Override
    @SideOnly(NetworkSide.SERVER)
    public void writePacket(FriendlyByteBuf buf) {
        buf.writeUtf(userName);
    }

    @Override
    @SideOnly(NetworkSide.CLIENT)
    public void readPacket(FriendlyByteBuf buf) {
        userName = buf.readUtf();
    }

    @Override
    @SideOnly(NetworkSide.CLIENT)
    public void applyToListener(ClientLoginPacketListener listener) {
        listener.handleLoginSuccess(this);
    }
}
