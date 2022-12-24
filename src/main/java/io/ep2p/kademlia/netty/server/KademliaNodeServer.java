package io.ep2p.kademlia.netty.server;

import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.netty.factory.NettyChannelInboundHandlerFactory;
import io.ep2p.kademlia.netty.factory.NettyChannelInitializerFactory;
import io.ep2p.kademlia.netty.factory.NettyKademliaMessageHandlerFactory;
import io.ep2p.kademlia.node.DHTKademliaNodeAPI;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;


@Getter
@Slf4j
public class KademliaNodeServer<K extends Serializable, V extends Serializable> {

    private final int port;
    private final String host;
    private final NettyChannelInitializerFactory nettyChannelInitializerFactory;
    private final NettyChannelInboundHandlerFactory nettyChannelInboundHandlerFactory;
    private final NettyKademliaMessageHandlerFactory<K, V> nettyKademliaMessageHandlerFactory;
    private boolean running = false;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture bindFuture;

    public KademliaNodeServer(String host, int port, NettyChannelInitializerFactory nettyChannelInitializerFactory, NettyChannelInboundHandlerFactory nettyChannelInboundHandlerFactory, NettyKademliaMessageHandlerFactory<K, V> nettyKademliaMessageHandlerFactory) {
        this.port = port;
        this.host = host;
        this.nettyChannelInitializerFactory = nettyChannelInitializerFactory;
        this.nettyChannelInboundHandlerFactory = nettyChannelInboundHandlerFactory;
        this.nettyKademliaMessageHandlerFactory = nettyKademliaMessageHandlerFactory;
    }

    public KademliaNodeServer(int port, NettyChannelInboundHandlerFactory nettyChannelInboundHandlerFactory, NettyKademliaMessageHandlerFactory<K, V> messageHandlerFactory, NettyChannelInitializerFactory nettyChannelInitializerFactory) {
        this(null, port, nettyChannelInitializerFactory, nettyChannelInboundHandlerFactory, messageHandlerFactory);
    }

    public synchronized void run(DHTKademliaNodeAPI<BigInteger, NettyConnectionInfo, K, V> dhtKademliaNodeAPI) throws InterruptedException {
        if (this.running)
            return;

        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();

        ChannelInitializer<SocketChannel> channelChannelInitializer = this.nettyChannelInitializerFactory.getChannelChannelInitializer(
                this.nettyChannelInboundHandlerFactory.getChannelInboundHandler(
                        this.nettyKademliaMessageHandlerFactory.getNettyKademliaMessageHandler(dhtKademliaNodeAPI)
                )
        );

        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(channelChannelInitializer)
                    .option(ChannelOption.SO_BACKLOG, 1024);

            ChannelFuture bind = host != null ? bootstrap.bind(host, port) : bootstrap.bind(port);
            running = true;
            this.bindFuture = bind.sync();

        } catch (InterruptedException e) {
            log.error("Kademlia Node Server interrupted", e);
            stop();
            throw e;
        }

    }

    public synchronized void stop() throws InterruptedException {
        this.running = false;
        if (bossGroup != null && workerGroup != null){
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }
        if (bindFuture != null)
            this.bindFuture.channel().closeFuture().sync();
    }

    public synchronized void stopNow() throws InterruptedException {
        this.running = false;
        if (bossGroup != null && workerGroup != null){
            bossGroup.shutdownGracefully(0, 5, TimeUnit.SECONDS).sync();
            workerGroup.shutdownGracefully(0, 5, TimeUnit.SECONDS).sync();
        }
        if (bindFuture != null)
            try {
                this.bindFuture.channel().close().sync();
            } catch (RejectedExecutionException e){
                log.error("Error when closing channel", e);
            }
    }

}
