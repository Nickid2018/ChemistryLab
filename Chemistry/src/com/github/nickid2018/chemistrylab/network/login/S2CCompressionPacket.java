package com.github.nickid2018.chemistrylab.network.login;

import com.github.nickid2018.chemistrylab.network.*;

public class S2CCompressionPacket implements NetworkPacket<ClientLoginPacketListener> {

	public int limit;

	@Override
	@SideOnly(NetworkSide.SERVER)
	public void writePacket(FriendlyByteBuf buf) throws Exception {
		buf.writeVarInt(limit);
	}

	@Override
	@SideOnly(NetworkSide.CLIENT)
	public void readPacket(FriendlyByteBuf buf) throws Exception {
		limit = buf.readVarInt();
	}

	@Override
	@SideOnly(NetworkSide.CLIENT)
	public void applyToListener(ClientLoginPacketListener listener) {
		listener.handleCompression(this);
	}

	public static S2CCompressionPacket createPacket(int limit) {
		S2CCompressionPacket packet = new S2CCompressionPacket();
		packet.limit = limit;
		return packet;
	}
}
