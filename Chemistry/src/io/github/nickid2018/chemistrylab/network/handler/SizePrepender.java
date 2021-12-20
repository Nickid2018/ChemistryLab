package io.github.nickid2018.chemistrylab.network.handler;

import io.github.nickid2018.chemistrylab.network.FriendlyByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@Sharable
public class SizePrepender extends MessageToByteEncoder<ByteBuf> {

    private static final int MAX_PREPEND_LENGTH = 3;

    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) {
        int i = byteBuf.readableBytes();
        int j = FriendlyByteBuf.getVarIntSize(i);
        if (j > MAX_PREPEND_LENGTH) {
            throw new IllegalArgumentException("unable to fit " + i + " into 3");
        } else {
            FriendlyByteBuf packetByteBuf = new FriendlyByteBuf(byteBuf2);
            packetByteBuf.ensureWritable(j + i);
            packetByteBuf.writeVarInt(i);
            packetByteBuf.writeBytes(byteBuf, byteBuf.readerIndex(), i);
        }
    }
}

