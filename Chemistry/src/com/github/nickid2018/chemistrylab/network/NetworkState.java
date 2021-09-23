package com.github.nickid2018.chemistrylab.network;

import com.github.nickid2018.chemistrylab.network.packet.DisconnectPacket;
import com.github.nickid2018.chemistrylab.network.packet.login.c2s.C2SEncryptionPacket;
import com.github.nickid2018.chemistrylab.network.packet.login.c2s.C2SHelloPacket;
import com.github.nickid2018.chemistrylab.network.packet.login.c2s.C2SLoginNamePacket;
import com.github.nickid2018.chemistrylab.network.packet.login.s2c.S2CCompressionPacket;
import com.github.nickid2018.chemistrylab.network.packet.login.s2c.S2CEncryptionPacket;
import com.github.nickid2018.chemistrylab.network.packet.login.s2c.S2CHelloPacket;
import com.github.nickid2018.chemistrylab.network.packet.login.s2c.S2CLoginSuccessPacket;
import com.github.nickid2018.chemistrylab.network.packet.play.c2s.C2SChatPacket;
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
        // Login process packet on client
        LOGIN.addPacket(NetworkSide.SERVER, C2SLoginNamePacket.class);
        LOGIN.addPacket(NetworkSide.SERVER, C2SHelloPacket.class);
        LOGIN.addPacket(NetworkSide.SERVER, C2SEncryptionPacket.class);
        // Login process packet on server
        LOGIN.addPacket(NetworkSide.CLIENT, S2CHelloPacket.class);
        LOGIN.addPacket(NetworkSide.CLIENT, S2CEncryptionPacket.class);
        LOGIN.addPacket(NetworkSide.CLIENT, S2CCompressionPacket.class);
        LOGIN.addPacket(NetworkSide.CLIENT, S2CLoginSuccessPacket.class);
        // Play process packet on client
        PLAY.addPacket(NetworkSide.SERVER, C2SChatPacket.class);
        // Disconnection
        LOGIN.addPacket(NetworkSide.SERVER, DisconnectPacket.class);
        LOGIN.addPacket(NetworkSide.CLIENT, DisconnectPacket.class);
        PLAY.addPacket(NetworkSide.SERVER, DisconnectPacket.class);
        PLAY.addPacket(NetworkSide.CLIENT, DisconnectPacket.class);
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
                return clazz == null ? null : (NetworkPacket<?>) clazz.getDeclaredConstructor().newInstance();
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
