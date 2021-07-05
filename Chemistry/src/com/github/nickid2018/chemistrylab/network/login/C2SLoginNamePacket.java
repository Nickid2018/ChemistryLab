package com.github.nickid2018.chemistrylab.network.login;

import com.github.nickid2018.chemistrylab.network.*;

public class C2SLoginNamePacket implements NetworkPacket<ServerLoginPacketListener> {

	public String userName;

	@Override
	@SideOnly(NetworkSide.CLIENT)
	public void writePacket(FriendlyByteBuf buf) throws Exception {
		buf.writeUtf(userName);
	}

	@Override
	@SideOnly(NetworkSide.SERVER)
	public void readPacket(FriendlyByteBuf buf) throws Exception {
		userName = buf.readUtf();
	}

	@Override
	@SideOnly(NetworkSide.SERVER)
	public void applyToListener(ServerLoginPacketListener listener) {
		listener.handleName(this);
	}

	public static C2SLoginNamePacket createPacket(String name) {
		C2SLoginNamePacket packet = new C2SLoginNamePacket();
		packet.userName = name;
		return packet;
	}
}
