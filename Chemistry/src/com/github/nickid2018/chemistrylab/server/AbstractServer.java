package com.github.nickid2018.chemistrylab.server;

import java.util.*;
import com.google.common.collect.*;

public abstract class AbstractServer {

	private Set<String> playerList = Sets.newConcurrentHashSet();
	private ServerSettings settings = new ServerSettings();

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
