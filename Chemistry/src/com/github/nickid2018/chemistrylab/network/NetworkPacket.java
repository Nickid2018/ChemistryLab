package com.github.nickid2018.chemistrylab.network;

import com.github.nickid2018.chemistrylab.network.packet.listener.NetworkListener;

public interface NetworkPacket<T extends NetworkListener> {

    void writePacket(FriendlyByteBuf buf);

    void readPacket(FriendlyByteBuf buf);

    void applyToListener(T listener);

    default boolean skipSupported() {
        return false;
    }
}
