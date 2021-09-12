package com.github.nickid2018.chemistrylab.network;

import com.github.nickid2018.chemistrylab.network.login.C2SLoginNamePacket;
import com.github.nickid2018.chemistrylab.network.login.S2CCompressionPacket;
import com.github.nickid2018.chemistrylab.network.login.S2CLoginSuccessPacket;
import com.github.nickid2018.chemistrylab.network.play.C2SChatPacket;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

import java.util.Map;

public enum NetworkState {

    /**
     *
     */
    LOGIN(0),
    /**
     *
     */
    PLAY(1);

    static {
        LOGIN.addPacket(NetworkSide.SERVER, C2SLoginNamePacket.class);
        LOGIN.addPacket(NetworkSide.CLIENT, S2CCompressionPacket.class);
        LOGIN.addPacket(NetworkSide.CLIENT, S2CLoginSuccessPacket.class);
        PLAY.addPacket(NetworkSide.SERVER, C2SChatPacket.class);
    }

    private final int id;
    private final Map<NetworkSide, BiMap<Integer, Class<? extends NetworkPacket<?>>>> packetMap = Maps
            .newEnumMap(NetworkSide.class);

    NetworkState(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getPacketId(NetworkSide side, NetworkPacket<?> packet) {
        return packetMap.get(side).inverse().getOrDefault(packet.getClass(), -1);
    }

    public NetworkPacket<?> createPacket(NetworkSide side, int id) {
        BiMap<Integer, Class<? extends NetworkPacket<?>>> map = packetMap.get(side);
        if (map != null) {
            Class<?> clazz = map.get(id);
            try {
                return clazz == null ? null : (NetworkPacket<?>) clazz.newInstance();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public void addPacket(NetworkSide side, Class<? extends NetworkPacket<?>> clazz) {
        BiMap<Integer, Class<? extends NetworkPacket<?>>> map = packetMap.computeIfAbsent(side, k -> HashBiMap.create());
        Preconditions.checkArgument(!map.containsValue(clazz));
        map.put(map.size(), clazz);
    }
}
