package com.github.nickid2018.chemistrylab.network.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import javax.crypto.Cipher;

public class PacketEncryptor extends MessageToByteEncoder<ByteBuf> {

    private final EncryptionUtils manager;

    public PacketEncryptor(Cipher cipher) {
        manager = new EncryptionUtils(cipher);
    }

    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) throws Exception {
        manager.encrypt(byteBuf, byteBuf2);
    }
}

