package io.github.nickid2018.chemistrylab.server;

import io.github.nickid2018.chemistrylab.network.NetworkEncryptionException;
import io.github.nickid2018.chemistrylab.network.NetworkEncryptionUtil;
import com.google.common.collect.Sets;

import java.security.KeyPair;
import java.util.Set;

public abstract class AbstractServer {

    private final Set<String> playerList = Sets.newConcurrentHashSet();
    private final ServerSettings settings = new ServerSettings();
    private final String name = "tester";
    private KeyPair pair;

    public AbstractServer() {
        if (settings.encrypt)
            try {
                pair = NetworkEncryptionUtil.generateServerKeyPair();
            } catch (NetworkEncryptionException e) {
                e.printStackTrace();
            }
    }

    public Set<String> getPlayerList() {
        return playerList;
    }

    public void addPlayer(String name) {
        playerList.add(name);
    }

    public void removePlayer(String name) {
        playerList.remove(name);
    }

    public ServerSettings getSettings() {
        return settings;
    }

    public String getName() {
        return name;
    }

    public KeyPair getPair() {
        return pair;
    }
}
