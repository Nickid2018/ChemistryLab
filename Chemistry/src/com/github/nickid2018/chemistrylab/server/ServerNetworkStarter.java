package com.github.nickid2018.chemistrylab.server;

import com.github.nickid2018.chemistrylab.Bootstrap;
import com.github.nickid2018.chemistrylab.annotation.API;
import com.github.nickid2018.chemistrylab.annotation.ScriptAPI;
import com.github.nickid2018.chemistrylab.client.network.ClientLoginNetworkHandler;
import com.github.nickid2018.chemistrylab.crash.CrashReport;
import com.github.nickid2018.chemistrylab.crash.CrashReportSession;
import com.github.nickid2018.chemistrylab.crash.DetectedCrashException;
import com.github.nickid2018.chemistrylab.mod.javascript.JavaScriptModBase;
import com.github.nickid2018.chemistrylab.network.NetworkConnection;
import com.github.nickid2018.chemistrylab.network.NetworkSide;
import com.github.nickid2018.chemistrylab.network.handler.PacketDecoder;
import com.github.nickid2018.chemistrylab.network.handler.PacketEncoder;
import com.github.nickid2018.chemistrylab.network.handler.SizePrepender;
import com.github.nickid2018.chemistrylab.network.handler.SplitterHandler;
import com.github.nickid2018.chemistrylab.network.packet.DisconnectPacket;
import com.github.nickid2018.chemistrylab.network.packet.play.c2s.C2SChatPacket;
import com.github.nickid2018.chemistrylab.server.network.ServerLoginPacketHandler;
import com.github.nickid2018.chemistrylab.text.BasicText;
import com.github.nickid2018.chemistrylab.text.Text;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ServerNetworkStarter {

    public static final Logger SERVER_LOGGER = LogManager.getLogger("Server Starter");
    private final AbstractServer server;
    private final List<ChannelFuture> channels = Collections.synchronizedList(Lists.newArrayList());
    private final List<NetworkConnection> connections = Collections.synchronizedList(Lists.newArrayList());
    private boolean active;

    @API
    @ScriptAPI("serverInterface:getServerStarterInstance")
    public static ServerNetworkStarter instance;

    public ServerNetworkStarter(AbstractServer server) {
        instance = this;
        this.server = server;
    }

    public static void main(String[] args) throws Exception {
        Bootstrap.init("integrated");
        ServerNetworkStarter starter = new ServerNetworkStarter(new AbstractServer() {
        });
        JavaScriptModBase base = new JavaScriptModBase();
        base.init();
        base.evalString("""
            if (importSystemPackage("servernterface")) {
                startTCPServer(InetAddress.getLocalHost(), 25565, 30);
            } else {
                throwJavaException(new ScriptException("Cannot load serverInterface"));
            }
        """);
//		SocketAddress addr = starter.startLocalServer();
//		NetworkConnection connection = NetworkConnection.connectToLocal(addr);
        NetworkConnection connection = NetworkConnection.connectToTcpServer(InetAddress.getLocalHost(), 25565);
        String name = "hello";
        connection.setListener(new ClientLoginNetworkHandler(connection, name));
        Thread t = new Thread(()->{
            while(true) {
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                starter.tick();
                connection.tick();
            }
        });
        t.setDaemon(true);
        t.start();
        Thread.sleep(3000);
        connection.sendPacket(C2SChatPacket.createPacket("5fqrervvacrcacafd"));
        connection.sendPacket(C2SChatPacket.createPacket("c234c13xxc143csaf"));
        connection.sendPacket(C2SChatPacket.createPacket("qvt 44v2v34cadf3c"));
        starter.connections.get(0).sendPacket(
                DisconnectPacket.createPacket(BasicText.newLiteralText("hello")),
                f -> connection.disconnect(BasicText.newLiteralText("hello")));
        connection.sendPacket(C2SChatPacket.createPacket("4353523c321cqvads"));
        Thread.sleep(5000);
//        starter.stop();
        base.evalString("forceStopServer();");
    }

    public boolean isActive() {
        return active;
    }

    public SocketAddress startLocalServer() {
        synchronized (channels) {
            ChannelFuture future;
            channels.add(future = new ServerBootstrap().channel(LocalServerChannel.class)
                    .childHandler(new ChannelInitializer<>() {
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
            active = true;
            return future.channel().localAddress();
        }
    }

    @API
    @ScriptAPI("serverInterface:startTcpServer")
    public void startTcpServer(@Nullable InetAddress inetAddress, int i, int timeout) {
        synchronized (channels) {
            Class<? extends ServerChannel> clazz;
            LazyLoadedValue<?> lazyLoadedValue;
            if (Epoll.isAvailable()) {
                clazz = EpollServerSocketChannel.class;
                lazyLoadedValue = NetworkConnection.SERVER_EPOLL_EVENT_GROUP;
                SERVER_LOGGER.info("Using epoll channel type");
            } else {
                clazz = NioServerSocketChannel.class;
                lazyLoadedValue = NetworkConnection.SERVER_EVENT_GROUP;
                SERVER_LOGGER.info("Using default channel type");
            }
            channels.add(new ServerBootstrap().channel(clazz).childHandler(new ChannelInitializer<>() {
                @Override
                protected void initChannel(Channel channel) {
                    try {
                        channel.config().setOption(ChannelOption.TCP_NODELAY, Boolean.TRUE);
                    } catch (ChannelException ignored) {
                    }
                    channel.pipeline().addLast("timeout", new ReadTimeoutHandler(timeout))
                            .addLast("splitter", new SplitterHandler())
                            .addLast("decoder", new PacketDecoder(NetworkSide.SERVER))
                            .addLast("prepender", new SizePrepender())
                            .addLast("encoder", new PacketEncoder(NetworkSide.CLIENT));
                    NetworkConnection connection = new NetworkConnection(NetworkSide.SERVER);
                    connections.add(connection);
                    channel.pipeline().addLast("packet_handler", connection);
                    connection.setListener(new ServerLoginPacketHandler(server, connection));
                }
            }).group((EventLoopGroup) lazyLoadedValue.get()).localAddress(inetAddress, i).bind().syncUninterruptibly());
            active = true;
        }
    }

    @API
    @ScriptAPI("serverInterface:forceStopServer")
    public void stop() {
        active = false;
        for(ChannelFuture future : channels) {
            try {
                future.channel().close().sync();
            } catch (InterruptedException e) {
                SERVER_LOGGER.error("Interrupted whilst closing channel");
            }
        }

    }

    public void tick() {
        synchronized (connections) {
            Iterator<NetworkConnection> iterator = connections.iterator();
            while (iterator.hasNext()) {
                NetworkConnection connection = iterator.next();
                if (connection.isConnecting())
                    continue;
                if (connection.isOpen()) {
                    try {
                        connection.tick();
                    } catch (Exception exception) {
                        NetworkConnection.NETWORK_LOGGER.warn("Failed to handle packet for {}", connection.getRemote(),
                                exception);
                        if (connection.isLocalConnection()) {
                            CrashReport crash = new CrashReport("Ticking local connection", exception);
                            CrashReportSession session = new CrashReportSession("Connection Detail");
                            session.addDetailObject("Connection", connection);
                            crash.addSession(session);
                            throw new DetectedCrashException(crash);
                        }
                        Text reason = BasicText.newTranslateText("disconnect.serverError");
                        connection.sendPacket(DisconnectPacket.createPacket(reason), f -> connection.disconnect(reason));
                        connection.setReadOnly();
                    }
                    continue;
                }
                iterator.remove();
                connection.disconnect();
            }
        }
    }
}
