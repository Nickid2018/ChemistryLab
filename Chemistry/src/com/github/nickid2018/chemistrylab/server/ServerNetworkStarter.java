package com.github.nickid2018.chemistrylab.server;

import com.github.nickid2018.chemistrylab.client.network.ClientLoginNetworkHandler;
import com.github.nickid2018.chemistrylab.crash.CrashReport;
import com.github.nickid2018.chemistrylab.crash.CrashReportSession;
import com.github.nickid2018.chemistrylab.crash.DetectedCrashException;
import com.github.nickid2018.chemistrylab.network.NetworkConnection;
import com.github.nickid2018.chemistrylab.network.NetworkSide;
import com.github.nickid2018.chemistrylab.network.handler.PacketDecoder;
import com.github.nickid2018.chemistrylab.network.handler.PacketEncoder;
import com.github.nickid2018.chemistrylab.network.login.C2SLoginNamePacket;
import com.github.nickid2018.chemistrylab.network.play.C2SChatPacket;
import com.github.nickid2018.chemistrylab.server.network.ServerLoginPacketHandler;
import com.github.nickid2018.chemistrylab.util.LazyLoadedValue;
import com.google.common.collect.Lists;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import javax.annotation.Nullable;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ServerNetworkStarter {

    private final AbstractServer server;
    private final List<ChannelFuture> channels = Collections.synchronizedList(Lists.newArrayList());
    private final List<NetworkConnection> connections = Collections.synchronizedList(Lists.newArrayList());

    public ServerNetworkStarter(AbstractServer server) {
        this.server = server;
    }

    public static void main(String[] args) throws Exception {
        ServerNetworkStarter starter = new ServerNetworkStarter(new AbstractServer() {
        });
//		SocketAddress addr = starter.startLocalServer();
//		NetworkConnection connection = NetworkConnection.connentToLocal(addr);
        starter.startTcpServer(InetAddress.getLocalHost(), 25565, 30);
        NetworkConnection connection = NetworkConnection.connentToTcpServer(InetAddress.getLocalHost(), 25565);
        String name = "hello";
        connection.setListener(new ClientLoginNetworkHandler(connection, name));
        connection.sendPacket(C2SLoginNamePacket.createPacket(name));
        Thread.sleep(5000);
        connection.tick();
        starter.tick();
        connection.sendPacket(C2SChatPacket.createPacket("1145141919810"));
        Thread.sleep(5000);
    }

    public SocketAddress startLocalServer() {
        synchronized (channels) {
            ChannelFuture future;
            channels.add(future = new ServerBootstrap().channel(LocalServerChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) {
                            try {
                                channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                            } catch (ChannelException ignored) {
                            }
                            NetworkConnection connection = new NetworkConnection(NetworkSide.SERVER);
                            connections.add(connection);
                            channel.pipeline().addLast("packet_handler", connection);
                            connection.setListener(new ServerLoginPacketHandler(server, connection));
                        }
                    }).group(NetworkConnection.SERVER_EVENT_GROUP.get()).localAddress(LocalAddress.ANY).bind()
                    .syncUninterruptibly());
            return future.channel().localAddress();
        }
    }

    public void startTcpServer(@Nullable InetAddress inetAddress, int i, int timeout) {
        synchronized (channels) {
            Class<? extends ServerChannel> clazz;
            LazyLoadedValue<?> lazyLoadedValue;
            if (Epoll.isAvailable()) {
                clazz = EpollServerSocketChannel.class;
                lazyLoadedValue = NetworkConnection.SERVER_EPOLL_EVENT_GROUP;
                NetworkConnection.NETWORK_LOGGER.info("Using epoll channel type");
            } else {
                clazz = NioServerSocketChannel.class;
                lazyLoadedValue = NetworkConnection.SERVER_EVENT_GROUP;
                NetworkConnection.NETWORK_LOGGER.info("Using default channel type");
            }
            channels.add(new ServerBootstrap().channel(clazz).childHandler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel channel) {
                    try {
                        channel.config().setOption(ChannelOption.TCP_NODELAY, Boolean.TRUE);
                    } catch (ChannelException ignored) {
                    }
                    channel.pipeline().addLast("timeout", new ReadTimeoutHandler(timeout))
                            .addLast("decoder", new PacketDecoder(NetworkSide.SERVER))
                            .addLast("encoder", new PacketEncoder(NetworkSide.CLIENT));
                    NetworkConnection connection = new NetworkConnection(NetworkSide.SERVER);
                    connections.add(connection);
                    channel.pipeline().addLast("packet_handler", connection);
                    connection.setListener(new ServerLoginPacketHandler(server, connection));
                }
            }).group((EventLoopGroup) lazyLoadedValue.get()).localAddress(inetAddress, i).bind().syncUninterruptibly());
        }
    }

    public void tick() {
        synchronized (connections) {
            Iterator<NetworkConnection> iterator = connections.iterator();
            while (iterator.hasNext()) {
                NetworkConnection connection = iterator.next();
                if (connection.isConnecting())
                    continue;
                if (connection.isConnected()) {
                    try {
                        connection.tick();
                    } catch (Exception exception) {
                        if (connection.isLocalConnection()) {
                            CrashReport crash = new CrashReport("Ticking local connection", exception);
                            CrashReportSession session = new CrashReportSession("Connection Detail");
                            session.addDetail("Connection", connection);
                            crash.addSession(session);
                            throw new DetectedCrashException(crash);
                        }
                        NetworkConnection.NETWORK_LOGGER.warn("Failed to handle packet for {}", connection.getRemote(),
                                exception);
                        connection.setReadOnly();
                    }
                    continue;
                }
                iterator.remove();
            }
        }
    }
}
