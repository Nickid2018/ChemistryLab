package com.github.nickid2018.chemistrylab.network.play;

import com.github.nickid2018.chemistrylab.network.*;

public class C2SChatPacket implements NetworkPacket<ServerPlayPacketListener> {

	public String chatMessage;

	@Override
	@SideOnly(NetworkSide.CLIENT)
	public void writePacket(FriendlyByteBuf buf) throws Exception {
		buf.writeUtf(chatMessage);
	}

	@Override
	@SideOnly(NetworkSide.SERVER)
	public void readPacket(FriendlyByteBuf buf) throws Exception {
		chatMessage = buf.readUtf();
	}

	@Override
	@SideOnly(NetworkSide.SERVER)
	public void applyToListener(ServerPlayPacketListener listener) {
		listener.handleChatMessage(this);
	}

	public static C2SChatPacket createPacket(String chatMessage) {
		C2SChatPacket packet = new C2SChatPacket();
		packet.chatMessage = chatMessage;
		return packet;
	}
}
