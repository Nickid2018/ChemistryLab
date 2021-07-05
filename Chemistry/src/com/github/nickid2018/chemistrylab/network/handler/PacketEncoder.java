package com.github.nickid2018.chemistrylab.network.handler;

import java.io.*;
import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.handler.codec.*;
import com.github.nickid2018.chemistrylab.network.*;

public class PacketEncoder extends MessageToByteEncoder<NetworkPacket<?>> {

	private final NetworkSide side;

	public PacketEncoder(NetworkSide side) {
		this.side = side;
	}

	public NetworkSide getSide() {
		return side;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, NetworkPacket<?> msg, ByteBuf out) throws Exception {
		NetworkState state = ctx.channel().attr(NetworkConnection.ATTRIBUTE_STATE).get();
		if (state == null)
			throw new IllegalStateException("Network state is null!");
		int id = state.getPacketId(side, msg);
		if (id < 0)
			throw new IOException(
					"Error in encoding packet (State: " + state + ", Name: " + msg.getClass().getName() + ")");
		FriendlyByteBuf buf = new FriendlyByteBuf(out);
		buf.writeVarInt(id);
		try {
			msg.writePacket(buf);
		} catch (Exception e) {
			if (msg.skipSupportted()) {
				NetworkConnection.NETWORK_LOGGER.warn("Error in encoding packet (ID {}, State {}), skipped.", id,
						state);
				throw new SkippedPacketException(e);
			} else
				throw e;
		}
	}

}
