package com.github.nickid2018.chemistrylab.network.login;

import com.github.nickid2018.chemistrylab.network.*;

public class S2CLoginSuccessPacket implements NetworkPacket<ClientLoginPacketListener> {

	public String userName;

	@Override
	@SideOnly(NetworkSide.SERVER)
	public void writePacket(FriendlyByteBuf buf) throws Exception {
		buf.writeUtf(userName);
	}

	@Override
	@SideOnly(NetworkSide.CLIENT)
	public void readPacket(FriendlyByteBuf buf) throws Exception {
		userName = buf.readUtf();
	}

	@Override
	@SideOnly(NetworkSide.CLIENT)
	public void applyToListener(ClientLoginPacketListener listener) {
		listener.handleLoginSuccess(this);
	}

	public static S2CLoginSuccessPacket createPacket(String name) {
		S2CLoginSuccessPacket packet = new S2CLoginSuccessPacket();
		packet.userName = name;
		return packet;
	}
}
