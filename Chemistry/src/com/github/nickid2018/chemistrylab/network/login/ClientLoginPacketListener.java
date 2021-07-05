package com.github.nickid2018.chemistrylab.network.login;

import com.github.nickid2018.chemistrylab.network.*;

public interface ClientLoginPacketListener extends NetworkListener{
	
	public void handleLoginSuccess(S2CLoginSuccessPacket packet);

	public void handleCompression(S2CCompressionPacket packet);
}
