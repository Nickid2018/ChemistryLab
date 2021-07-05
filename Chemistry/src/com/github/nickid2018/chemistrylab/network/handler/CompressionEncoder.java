package com.github.nickid2018.chemistrylab.network.handler;

import java.util.zip.*;
import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.handler.codec.*;
import com.github.nickid2018.chemistrylab.network.*;

public class CompressionEncoder extends MessageToByteEncoder<ByteBuf> {

	private Deflater deflater = new Deflater();
	private int limit;
	private byte[] encodeBuffer = new byte[8192];

	public CompressionEncoder(int limit) {
		this.limit = limit;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
		int length = msg.readableBytes();
		FriendlyByteBuf buf = new FriendlyByteBuf(out);
		if (length < limit) {
			buf.writeVarInt(0);
			buf.writeBytes(msg);
		} else {
			byte[] in = new byte[length];
			msg.readBytes(in);
			buf.writeVarInt(in.length);
			deflater.setInput(in);
			deflater.finish();
			while (!deflater.finished()) {
				int len = deflater.deflate(encodeBuffer);
				buf.writeBytes(encodeBuffer, 0, len);
			}
			deflater.reset();
		}
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
}
