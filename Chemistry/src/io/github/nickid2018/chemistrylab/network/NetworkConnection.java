package io.github.nickid2018.chemistrylab.network;

import io.github.nickid2018.chemistrylab.network.handler.*;
import io.github.nickid2018.chemistrylab.network.packet.DisconnectPacket;
import io.github.nickid2018.chemistrylab.network.packet.listener.NetworkListener;
import io.github.nickid2018.chemistrylab.text.BasicText;
import io.github.nickid2018.chemistrylab.text.Text;
import io.github.nickid2018.chemistrylab.util.LazyLoadedValue;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Queue;

public class NetworkConnection extends SimpleChannelInboundHandler<NetworkPacket<?>> {

    public static final Logger NETWORK_LOGGER = LogManager.getLogger("Network Connection Listener");

    public static final LazyLoadedValue<NioEventLoopGroup> SERVER_EVENT_GROUP = new LazyLoadedValue<>(
            () -> new NioEventLoopGroup(0,
                    (new ThreadFactoryBuilder()).setNameFormat("Server IO #%d").setDaemon(true).build()));

    public static final LazyLoadedValue<EpollEventLoopGroup> SERVER_EPOLL_EVENT_GROUP = new LazyLoadedValue<>(
            () -> new EpollEventLoopGroup(0,
                    (new ThreadFactoryBuilder()).setNameFormat("Epoll Server IO #%d").setDaemon(true).build()));

    public static final LazyLoadedValue<NioEventLoopGroup> NETWORK_WORKER_GROUP = new LazyLoadedValue<>(
            () -> new NioEventLoopGroup(0,
                    (new ThreadFactoryBuilder()).setNameFormat("Client IO #%d").setDaemon(true).build()));

    public static final LazyLoadedValue<EpollEventLoopGroup> NETWORK_EPOLL_WORKER_GROUP = new LazyLoadedValue<>(
            () -> new EpollEventLoopGroup(0,
                    (new ThreadFactoryBuilder()).setNameFormat("Epoll Client IO #%d").setDaemon(true).build()));

    public static final LazyLoadedValue<DefaultEventLoopGroup> LOCAL_WORKER_GROUP = new LazyLoadedValue<>(
            () -> new DefaultEventLoopGroup(0,
                    (new ThreadFactoryBuilder()).setNameFormat("Local Client IO #%d").setDaemon(true).build()));

    public static final AttributeKey<NetworkState> ATTRIBUTE_STATE = AttributeKey.valueOf("state");
    private final NetworkSide side;
    private final Queue<PacketHolder> packetBuffer = Queues.newSynchronousQueue();
    private Channel channel;
    private SocketAddress address;
    private int packetReceived = 0;
    private int packetSent = 0;
    private float avgPacketReceived = 0;
    private float avgPacketSent = 0;
    private int nowTickCount = 0;
    private Text disconnectReason;
    private boolean disconnected;
    private boolean errored;

    private NetworkListener listener;
    private boolean encrypted;

    public NetworkConnection(NetworkSide side) {
        this.side = side;
    }

    public static NetworkConnection connectToTcpServer(InetAddress addr, int port) {
        return connectToTcpServer(addr, port, 30);
    }

    public static NetworkConnection connectToTcpServer(InetAddress addr, int port, int timeout) {
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
        new Bootstrap().group((EventLoopGroup) lazyLoadedValue.get()).handler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel channel) {
                try {
                    channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                } catch (ChannelException ignored) {
                }
                channel.pipeline().addLast("timeout", new ReadTimeoutHandler(timeout))
                        .addLast("splitter", new SplitterHandler())
                        .addLast("decoder", new PacketDecoder(NetworkSide.CLIENT))
                        .addLast("prepender", new SizePrepender())
                        .addLast("encoder", new PacketEncoder(NetworkSide.SERVER))
                        .addLast("packet_handler", connection);
            }
        }).channel(clazz).connect(addr, port).syncUninterruptibly();
        return connection;
    }

    public static NetworkConnection connectToLocal(SocketAddress addr) {
        NetworkConnection connection = new NetworkConnection(NetworkSide.CLIENT);
        new Bootstrap().group(LOCAL_WORKER_GROUP.get()).handler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel channel) {
                channel.pipeline().addLast("packet_handler", connection);
            }
        }).channel(LocalChannel.class).connect(addr).syncUninterruptibly();
        return connection;
    }

    public void disconnect(Text reason) {
        if(channel.isOpen()) {
            channel.close().awaitUninterruptibly();
            disconnectReason = reason;
        }
    }

    public void setDisconnectReason(Text disconnectReason) {
        this.disconnectReason = disconnectReason;
    }

    public void channelInactive(ChannelHandlerContext ctx) {
        disconnect(BasicText.newTranslateText("disconnect.endOfStream"));
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
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (!channel.isOpen())
            return;
        if (cause instanceof SkippedPacketException) {
            NETWORK_LOGGER.debug("Skipping packet due to errors", cause.getCause());
            return;
        }
        if(isOpen()) {
            if(errored) {
                NETWORK_LOGGER.error("Double fault in sending disconnect packet", cause);
                disconnect(disconnectReason);
            } else {
                errored = true;
                if (cause instanceof TimeoutException) {
                    NETWORK_LOGGER.debug("A client met a timeout", cause);
                    disconnectReason = BasicText.newTranslateText("disconnect.timeout");
                } else {
                    NETWORK_LOGGER.error("Fatal error in sending/receiving packet", cause);
                    disconnectReason = BasicText.newTranslateText("disconnect.internal", cause);
                }
                sendPacket(DisconnectPacket.createPacket(disconnectReason), f -> disconnect(disconnectReason));
                channel.config().setAutoRead(false);
            }
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
        if (!isOpen())
            return;
        synchronized (packetBuffer) {
            while (!packetBuffer.isEmpty()) {
                PacketHolder holder = packetBuffer.poll();
                sendPacket0(holder.packet, holder.listener);
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NetworkPacket<?> msg) {
        if (isOpen()) {
            delegateFire(msg);
            packetReceived++;
        }
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

    public void setupEncryption(SecretKey key) throws NetworkEncryptionException {
        channel.pipeline().addBefore("splitter", "decrypt",
                new PacketDecryptor(NetworkEncryptionUtil.cipherFromKey(Cipher.DECRYPT_MODE, key)));
        channel.pipeline().addBefore("prepender", "encrypt",
                new PacketEncryptor(NetworkEncryptionUtil.cipherFromKey(Cipher.ENCRYPT_MODE, key)));
        encrypted = true;
    }

    public boolean isEncrypted() {
        return this.encrypted;
    }

    public void tick() {
        flushQueue();
        if(listener != null)
            listener.tick();
        if(channel != null && !channel.isOpen() && !disconnected) {
            disconnected = true;
            if (listener != null)
                listener.asyncOnDisconnect(
                        disconnectReason == null ? BasicText.newTranslateText("disconnect.simple") : disconnectReason);
        }
        if (channel != null)
            channel.flush();
        if (nowTickCount++ % 20 == 0) {
            avgPacketReceived = 0.75f * avgPacketReceived + 0.25f * packetReceived;
            avgPacketSent = 0.75f * avgPacketSent + 0.25f * packetSent;
            packetReceived = 0;
            packetSent = 0;
        }
    }

    public boolean isOpen() {
        return channel != null && channel.isOpen();
    }

    @Override
    public String toString() {
        return ("NetworkConnection{side=%s, address=%s, packetReceived=%d, packetSent=%d," +
                " avgPacketReceived=%s, avgPacketSent=%s, nowTickCount=%d, encrypted=%s, local=%s}")
                .formatted(side, address, packetReceived, packetSent, avgPacketReceived, avgPacketSent,
                        nowTickCount, encrypted, isLocalConnection());
    }

    @SuppressWarnings("unchecked")
    private <T extends NetworkListener> void delegateFire(NetworkPacket<T> msg) {
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

    private static class PacketHolder {

        public NetworkPacket<?> packet;
        @Nullable
        public GenericFutureListener<? extends Future<? super Void>> listener;
    }
}
