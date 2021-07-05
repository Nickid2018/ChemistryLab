package com.github.nickid2018.chemistrylab.client.network;

import com.github.nickid2018.chemistrylab.text.*;
import com.github.nickid2018.chemistrylab.network.*;
import com.github.nickid2018.chemistrylab.network.login.*;

public class ClientLoginNetworkHandler implements ClientLoginPacketListener {
	
	private NetworkConnection connection;
	private String userName;
	
	public ClientLoginNetworkHandler(NetworkConnection connection,String userName) {
		this.connection = connection;
		this.userName = userName;
	}

	@Override
	public void tick() {

	}

	@Override
	public void onDisconnect(Text text) {

	}

	@Override
	public void handleLoginSuccess(S2CLoginSuccessPacket packet) {
		if(!userName.equals(packet.userName)) {
			
		}
		connection.updateState(NetworkState.PLAY);
	}

	@Override
	public void handleCompression(S2CCompressionPacket packet) {
		connection.setCompression(packet.limit);
	}

}
