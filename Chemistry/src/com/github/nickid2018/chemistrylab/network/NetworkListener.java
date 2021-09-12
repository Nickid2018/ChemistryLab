package com.github.nickid2018.chemistrylab.network;

import com.github.nickid2018.chemistrylab.text.Text;

public interface NetworkListener {

    void tick();

    void onDisconnect(Text text);
}
