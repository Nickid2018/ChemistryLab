package io.github.nickid2018.chemistrylab.network.handler;

import io.github.nickid2018.chemistrylab.network.FriendlyByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;

import java.util.List;
import java.util.zip.Inflater;

public class CompressionDecoder extends ByteToMessageDecoder {

    private final Inflater inflater = new Inflater();
    private int limit;

    public CompressionDecoder(int limit) {
        this.limit = limit;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() != 0) {
            FriendlyByteBuf buf = new FriendlyByteBuf(in);
            int length = buf.readVarInt();
            if (length == 0)
                out.add(buf.readBytes(buf.readableBytes()));
            else {
                if (length < limit)
                    throw new DecoderException(
                            "Bad compressed packet - length of " + length + " is below server limit of " + limit);
                if (length > 2097152)
                    throw new DecoderException("Bad compressed packet - length of " + length
                            + " is beyond protocol maximum of " + 2097152);
                byte[] data = new byte[buf.readableBytes()];
                buf.readBytes(data);
                inflater.setInput(data);
                byte[] decom = new byte[length];
                inflater.inflate(decom);
                out.add(Unpooled.wrappedBuffer(decom));
                inflater.reset();
            }
        }
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
