package io.github.nickid2018.chemistrylab.network.packet;

import io.github.nickid2018.chemistrylab.network.FriendlyByteBuf;
import io.github.nickid2018.chemistrylab.network.NetworkPacket;
import io.github.nickid2018.chemistrylab.network.packet.listener.NetworkListener;
import io.github.nickid2018.chemistrylab.text.Text;

public class DisconnectPacket implements NetworkPacket<NetworkListener> {

    public Text reason;

    @Override
    public void writePacket(FriendlyByteBuf buf) {
        buf.writeUtf(Text.serialize(reason));
    }

    @Override
    public void readPacket(FriendlyByteBuf buf) {
        reason = Text.deserialize(buf.readUtf());
    }

    @Override
    public void applyToListener(NetworkListener listener) {
        listener.onDisconnect(reason);
    }

    public static DisconnectPacket createPacket(Text reason){
        DisconnectPacket packet = new DisconnectPacket();
        packet.reason = reason;
        return packet;
    }
}
