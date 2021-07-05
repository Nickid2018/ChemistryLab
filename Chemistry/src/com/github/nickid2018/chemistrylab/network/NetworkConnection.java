package com.github.nickid2018.chemistrylab.network;

import java.util.*;
import java.net.*;
import io.netty.util.*;
import io.netty.channel.*;
import javax.annotation.*;
import io.netty.bootstrap.*;
import io.netty.channel.nio.*;
import io.netty.channel.epoll.*;
import io.netty.channel.local.*;
import io.netty.util.concurrent.*;
import io.netty.handler.timeout.*;
import org.apache.logging.log4j.*;
import io.netty.channel.socket.nio.*;
import com.google.common.collect.*;
import com.google.common.util.concurrent.*;
import com.github.nickid2018.chemistrylab.util.*;
import io.netty.handler.timeout.TimeoutException;
import com.github.nickid2018.chemistrylab.network.handler.*;

public class NetworkConnection extends SimpleChannelInboundHandler<NetworkPacket<?>> {

	public static final Logger NETWORK_LOGGER = LogManager.getLogger("Network Handler");

	public static final LazyLoadedValue<NioEventLoopGroup> SERVER_EVENT_GROUP = new LazyLoadedValue<>(
			() -> new NioEventLoopGroup(0,
					(new ThreadFactoryBuilder()).setNameFormat("[Netty]Server IO #%d").setDaemon(true).build()));

	public static final LazyLoadedValue<EpollEventLoopGroup> SERVER_EPOLL_EVENT_GROUP = new LazyLoadedValue<>(
			() -> new EpollEventLoopGroup(0,
					(new ThreadFactoryBuilder()).setNameFormat("[Netty]Epoll Server IO #%d").setDaemon(true).build()));

	public static final LazyLoadedValue<NioEventLoopGroup> NETWORK_WORKER_GROUP = new LazyLoadedValue<>(
			() -> new NioEventLoopGroup(0,
					(new ThreadFactoryBuilder()).setNameFormat("[Netty]Client IO #%d").setDaemon(true).build()));

	public static final LazyLoadedValue<EpollEventLoopGroup> NETWORK_EPOLL_WORKER_GROUP = new LazyLoadedValue<>(
			() -> new EpollEventLoopGroup(0,
					(new ThreadFactoryBuilder()).setNameFormat("[Netty]Epoll Client IO #%d").setDaemon(true).build()));

	public static final LazyLoadedValue<DefaultEventLoopGroup> LOCAL_WORKER_GROUP = new LazyLoadedValue<>(
			() -> new DefaultEventLoopGroup(0,
					(new ThreadFactoryBuilder()).setNameFormat("[Netty]Local Client IO #%d").setDaemon(true).build()));

	public static final AttributeKey<NetworkState> ATTRIBUTE_STATE = AttributeKey.valueOf("state");

	private Channel channel;
	private SocketAddress address;
	private final NetworkSide side;
	private final Queue<PacketHolder> packetBuffer = Queues.newSynchronousQueue();

	private int packetReceived = 0;
	private int packetSent = 0;
	private float avgPacketReceived = 0;
	private float avgPacketSent = 0;
	private int nowTickCount = 0;

	private NetworkListener listener;

	public NetworkConnection(NetworkSide side) {
		this.side = side;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		channel = ctx.channel();
		address = channel.remoteAddress();
		updateState(NetworkState.LOGIN);
	}

	public void updateState(NetworkState state) {
		channel.attr(ATTRIBUTE_STATE).set(state);
		channel.config().setAutoRead(true);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (!channel.isOpen())
			return;
		if (cause instanceof SkippedPacketException) {
			NETWORK_LOGGER.debug("Skipping packet due to errors", cause.getCause());
			return;
		}
		if (cause instanceof TimeoutException) {
			NETWORK_LOGGER.debug("A client met a timeout", cause);
			return;
		}

	}

	public void sendPacket(NetworkPacket<?> packet) {
		sendPacket(packet, null);
	}

	public void sendPacket(NetworkPacket<?> packet,
			@Nullable GenericFutureListener<? extends Future<? super Void>> listener) {
		if (channel == null || !channel.isOpen()) {
			PacketHolder holder = new PacketHolder();
			holder.packet = packet;
			holder.listener = listener;
			packetBuffer.offer(holder);
		} else {
			flushQueue();
			sendPacket0(packet, listener);
		}
	}

	private void sendPacket0(NetworkPacket<?> packet,
			@Nullable GenericFutureListener<? extends Future<? super Void>> listener) {
		packetSent++;
		if (channel.eventLoop().inEventLoop()) {
			ChannelFuture channelFuture = channel.writeAndFlush(packet);
			if (listener != null)
				channelFuture.addListener(listener);
			channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
		} else {
			channel.eventLoop().execute(() -> {
				ChannelFuture channelFuture = channel.writeAndFlush(packet);
				if (listener != null)
					channelFuture.addListener(listener);
				channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
			});
		}
	}

	private void flushQueue() {
		if (channel == null || !channel.isOpen())
			return;
		synchronized (packetBuffer) {
			while (!packetBuffer.isEmpty()) {
				PacketHolder holder = packetBuffer.poll();
				sendPacket0(holder.packet, holder.listener);
			}
		}
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, NetworkPacket<?> msg) throws Exception {
		if (channel.isOpen()) {
			degerateFire(msg);
			packetReceived++;
		}
	}

	public boolean isConnected() {
		return channel != null && channel.isOpen();
	}

	public boolean isConnecting() {
		return channel == null;
	}

	public void disconnect() {
		if (channel.isOpen())
			channel.close().syncUninterruptibly();
	}

	public void setCompression(int limit) {
		if (limit >= 0) {
			if (channel.pipeline().get("decompress") instanceof CompressionDecoder)
				((CompressionDecoder) channel.pipeline().get("decompress")).setLimit(limit);
			else
				channel.pipeline().addBefore("decoder", "decompress", new CompressionDecoder(limit));
			if (channel.pipeline().get("compress") instanceof CompressionEncoder)
				((CompressionEncoder) channel.pipeline().get("compress")).setLimit(limit);
			else
				channel.pipeline().addBefore("encoder", "compress", new CompressionEncoder(limit));
		} else {
			if (channel.pipeline().get("decompress") instanceof CompressionDecoder)
				channel.pipeline().remove("decompress");
			if (channel.pipeline().get("compress") instanceof CompressionEncoder)
				channel.pipeline().remove("compress");
		}
	}

	public void tick() {
		flushQueue();
		if (channel != null)
			channel.flush();
		if (nowTickCount++ % 20 == 0) {
			avgPacketReceived = 0.75f * avgPacketReceived + 0.25f * packetReceived;
			avgPacketSent = 0.75f * avgPacketSent + 0.25f * packetSent;
			packetReceived = 0;
			packetSent = 0;
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends NetworkListener> void degerateFire(NetworkPacket<T> msg) {
		msg.applyToListener((T) listener);
	}

	public void setListener(NetworkListener listener) {
		this.listener = listener;
	}

	public SocketAddress getRemote() {
		return address;
	}

	public NetworkSide getSide() {
		return side;
	}

	public float getAvgPacketReceived() {
		return avgPacketReceived;
	}

	public float getAvgPacketSent() {
		return avgPacketSent;
	}

	public void setReadOnly() {
		channel.config().setAutoRead(false);
	}

	public boolean isLocalConnection() {
		return channel instanceof LocalChannel || channel instanceof LocalServerChannel;
	}

	public static NetworkConnection connentToTcpServer(InetAddress addr, int port) {
		return connentToTcpServer(addr, port, 30);
	}

	public static NetworkConnection connentToTcpServer(InetAddress addr, int port, int timeout) {
		NetworkConnection connection = new NetworkConnection(NetworkSide.CLIENT);
		Class<? extends Channel> clazz;
		LazyLoadedValue<?> lazyLoadedValue;
		if (Epoll.isAvailable()) {
			clazz = EpollSocketChannel.class;
			lazyLoadedValue = NetworkConnection.NETWORK_EPOLL_WORKER_GROUP;
		} else {
			clazz = NioSocketChannel.class;
			lazyLoadedValue = NetworkConnection.NETWORK_WORKER_GROUP;
		}
		new Bootstrap().group((EventLoopGroup) lazyLoadedValue.get()).handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) throws Exception {
				try {
					channel.config().setOption(ChannelOption.TCP_NODELAY, true);
				} catch (ChannelException channelException) {
				}
				channel.pipeline().addLast("timeout", new ReadTimeoutHandler(timeout))
						.addLast("decoder", new PacketDecoder(NetworkSide.CLIENT))
						.addLast("encoder", new PacketEncoder(NetworkSide.SERVER))
						.addLast("packet_handler", connection);
			}
		}).channel(clazz).connect(addr, port).syncUninterruptibly();
		return connection;
	}

	public static NetworkConnection connentToLocal(SocketAddress addr) {
		NetworkConnection connection = new NetworkConnection(NetworkSide.CLIENT);
		new Bootstrap().group(LOCAL_WORKER_GROUP.get()).handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) throws Exception {
				channel.pipeline().addLast("packet_handler", connection);
			}
		}).channel(LocalChannel.class).connect(addr).syncUninterruptibly();
		return connection;
	}

	private static class PacketHolder {

		public NetworkPacket<?> packet;
		public @Nullable GenericFutureListener<? extends Future<? super Void>> listener;
	}
}
