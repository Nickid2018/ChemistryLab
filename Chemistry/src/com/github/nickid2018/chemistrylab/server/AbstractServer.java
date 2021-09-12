package com.github.nickid2018.chemistrylab.server;

import com.google.common.collect.Sets;

import java.util.Set;

public abstract class AbstractServer {

    private final Set<String> playerList = Sets.newConcurrentHashSet();
    private final ServerSettings settings = new ServerSettings();

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
}
