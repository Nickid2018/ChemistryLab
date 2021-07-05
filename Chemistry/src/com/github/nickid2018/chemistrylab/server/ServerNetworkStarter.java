package com.github.nickid2018.chemistrylab.server;

import java.net.*;
import java.util.*;
import io.netty.channel.*;
import javax.annotation.*;
import io.netty.bootstrap.*;
import io.netty.channel.local.*;
import io.netty.channel.epoll.*;
import io.netty.handler.timeout.*;
import io.netty.channel.socket.nio.*;
import com.google.common.collect.*;
import com.github.nickid2018.chemistrylab.util.*;
import com.github.nickid2018.chemistrylab.client.network.ClientLoginNetworkHandler;
import com.github.nickid2018.chemistrylab.crash.*;
import com.github.nickid2018.chemistrylab.network.*;
import com.github.nickid2018.chemistrylab.server.network.*;
import com.github.nickid2018.chemistrylab.network.handler.*;
import com.github.nickid2018.chemistrylab.network.login.C2SLoginNamePacket;
import com.github.nickid2018.chemistrylab.network.play.C2SChatPacket;

public class ServerNetworkStarter {

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

	private AbstractServer server;

	private final List<ChannelFuture> channels = Collections.synchronizedList(Lists.newArrayList());

	private final List<NetworkConnection> connections = Collections.synchronizedList(Lists.newArrayList());

	public ServerNetworkStarter(AbstractServer server) {
		this.server = server;
	}

	public SocketAddress startLocalServer() {
		synchronized (channels) {
			ChannelFuture future;
			channels.add(future = new ServerBootstrap().channel(LocalServerChannel.class)
					.childHandler(new ChannelInitializer<Channel>() {
						@Override
						protected void initChannel(Channel channel) throws Exception {
							try {
								channel.config().setOption(ChannelOption.TCP_NODELAY, true);
							} catch (ChannelException channelException) {
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
				protected void initChannel(Channel channel) throws Exception {
					try {
						channel.config().setOption(ChannelOption.TCP_NODELAY, Boolean.valueOf(true));
					} catch (ChannelException channelException) {
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
