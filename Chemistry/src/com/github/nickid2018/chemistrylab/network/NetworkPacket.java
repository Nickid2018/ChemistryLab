package com.github.nickid2018.chemistrylab.network;

public interface NetworkPacket<T extends NetworkListener> {

    void writePacket(FriendlyByteBuf buf);

    void readPacket(FriendlyByteBuf buf);

    void applyToListener(T listener);

    default boolean skipSupported() {
        return false;
    }
}
