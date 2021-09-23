package com.github.nickid2018.chemistrylab.network.packet.listener;

import com.github.nickid2018.chemistrylab.text.Text;

public interface NetworkListener {

    void tick();

    void onDisconnect(Text text);

    void asyncOnDisconnect(Text text);
}
