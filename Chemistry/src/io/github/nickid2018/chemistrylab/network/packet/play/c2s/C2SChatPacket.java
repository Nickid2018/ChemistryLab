package io.github.nickid2018.chemistrylab.network.packet.play.c2s;

import io.github.nickid2018.chemistrylab.network.FriendlyByteBuf;
import io.github.nickid2018.chemistrylab.network.NetworkPacket;
import io.github.nickid2018.chemistrylab.network.NetworkSide;
import io.github.nickid2018.chemistrylab.network.SideOnly;
import io.github.nickid2018.chemistrylab.network.packet.listener.ServerPlayPacketListener;

public class C2SChatPacket implements NetworkPacket<ServerPlayPacketListener> {

    public String chatMessage;

    public static C2SChatPacket createPacket(String chatMessage) {
        C2SChatPacket packet = new C2SChatPacket();
        packet.chatMessage = chatMessage;
        return packet;
    }

    @Override
    @SideOnly(NetworkSide.CLIENT)
    public void writePacket(FriendlyByteBuf buf) {
        buf.writeUtf(chatMessage);
    }

    @Override
    @SideOnly(NetworkSide.SERVER)
    public void readPacket(FriendlyByteBuf buf) {
        chatMessage = buf.readUtf();
    }

    @Override
    @SideOnly(NetworkSide.SERVER)
    public void applyToListener(ServerPlayPacketListener listener) {
        listener.handleChatMessage(this);
    }
}
