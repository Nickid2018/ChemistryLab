package com.github.nickid2018.chemistrylab.network;

public interface NetworkPacket<T extends NetworkListener> {

	public void writePacket(FriendlyByteBuf buf) throws Exception;

	public void readPacket(FriendlyByteBuf buf) throws Exception;

	public void applyToListener(T listener);

	public default boolean skipSupportted() {
		return false;
	}
}
