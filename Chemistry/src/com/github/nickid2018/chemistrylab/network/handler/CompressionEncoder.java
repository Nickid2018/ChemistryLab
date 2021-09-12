package com.github.nickid2018.chemistrylab.network.handler;

import com.github.nickid2018.chemistrylab.network.FriendlyByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.zip.Deflater;

public class CompressionEncoder extends MessageToByteEncoder<ByteBuf> {

    private final Deflater deflater = new Deflater();
    private final byte[] encodeBuffer = new byte[8192];
    private int limit;

    public CompressionEncoder(int limit) {
        this.limit = limit;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) {
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
