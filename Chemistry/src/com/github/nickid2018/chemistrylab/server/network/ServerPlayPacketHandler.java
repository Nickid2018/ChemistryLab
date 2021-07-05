package com.github.nickid2018.chemistrylab.server.network;

import com.github.nickid2018.chemistrylab.text.*;
import com.github.nickid2018.chemistrylab.server.*;
import com.github.nickid2018.chemistrylab.network.*;
import com.github.nickid2018.chemistrylab.network.play.*;

public class ServerPlayPacketHandler implements ServerPlayPacketListener {

	private final AbstractServer server;
	private final NetworkConnection connection;
	private String userName;

	public ServerPlayPacketHandler(AbstractServer server, NetworkConnection connection, String userName) {
		this.server = server;
		this.connection = connection;
		this.userName = userName;
	}

	@Override
	public void tick() {

	}

	@Override
	public void handleChatMessage(C2SChatPacket packet) {
		System.out.println(packet.chatMessage);
	}

	@Override
	public void onDisconnect(Text text) {
		NetworkConnection.NETWORK_LOGGER.info("{} lost connection: {}", userName, text.getValue());
	}

}
