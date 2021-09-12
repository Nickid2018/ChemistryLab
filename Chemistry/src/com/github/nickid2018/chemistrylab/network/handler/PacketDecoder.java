package com.github.nickid2018.chemistrylab.network.handler;

import com.github.nickid2018.chemistrylab.network.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.IOException;
import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

    private final NetworkSide side;

    public PacketDecoder(NetworkSide side) {
        this.side = side;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() == 0)
            return;
        NetworkState state = ctx.channel().attr(NetworkConnection.ATTRIBUTE_STATE).get();
        FriendlyByteBuf buf = new FriendlyByteBuf(in);
        int id = buf.readVarInt();
        NetworkPacket<?> packet = state.createPacket(side, id);
        if (packet == null)
            throw new IOException("Bad packet (ID " + id + ", State " + state + ") - Unknown ID");
        packet.readPacket(buf);
        if (buf.readableBytes() > 0)
            throw new IOException(
                    String.format("Bad packet (ID %s, State %s, Name %s) - Unexpected %s byte(s) at the packet tail",
                            id, state, packet.getClass(), buf.readableBytes()));
        out.add(packet);
    }

}
