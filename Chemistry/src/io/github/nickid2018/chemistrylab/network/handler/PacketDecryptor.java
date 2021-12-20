package io.github.nickid2018.chemistrylab.network.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import javax.crypto.Cipher;
import java.util.List;

public class PacketDecryptor extends MessageToMessageDecoder<ByteBuf> {

    private final EncryptionUtils manager;

    public PacketDecryptor(Cipher cipher) {
        manager = new EncryptionUtils(cipher);
    }

    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        list.add(manager.decrypt(channelHandlerContext, byteBuf));
    }
}

