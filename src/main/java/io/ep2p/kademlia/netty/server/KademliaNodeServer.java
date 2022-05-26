package io.ep2p.kademlia.netty.server;

import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.netty.factory.KademliaNodeServerInitializerAPIFactory;
import io.ep2p.kademlia.node.DHTKademliaNodeAPI;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigInteger;


@Getter
public class KademliaNodeServer<K extends Serializable, V extends Serializable> {

    private final int port;
    private final String host;
    private final KademliaNodeServerInitializerAPIFactory kademliaNodeServerInitializerAPIFactory;
    private boolean running = false;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture bindFuture;

    public KademliaNodeServer(String host, int port, KademliaNodeServerInitializerAPIFactory kademliaNodeServerInitializerAPIFactory) {
        this.port = port;
        this.host = host;
        this.kademliaNodeServerInitializerAPIFactory = kademliaNodeServerInitializerAPIFactory;
    }

    public KademliaNodeServer(int port, KademliaNodeServerInitializerAPIFactory factory) {
        this(null, port, factory);
    }

    public KademliaNodeServer(int port) {
        this(null, port, new KademliaNodeServerInitializerAPIFactory());
    }

    public KademliaNodeServer(String host, int port) {
        this(host, port, new KademliaNodeServerInitializerAPIFactory());
    }

    public synchronized void run(DHTKademliaNodeAPI<BigInteger, NettyConnectionInfo, K, V> dhtKademliaNodeAPI) {
        assert !running;

        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();

        KademliaNodeServerInitializer<K, V> kademliaNodeServerInitializer = kademliaNodeServerInitializerAPIFactory.getKademliaNodeServerInitializerAPI();
        kademliaNodeServerInitializer.registerKademliaNode(dhtKademliaNodeAPI);

        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(kademliaNodeServerInitializer)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.SO_KEEPALIVE, false);

            ChannelFuture bind = host != null ? bootstrap.bind(host, port) : bootstrap.bind(port);
            this.bindFuture = bind.sync();

        } catch (InterruptedException e) {
            //todo
            e.printStackTrace();
            stop();
        }

        running = true;
    }

    public synchronized void stop(){
        if (bossGroup != null && workerGroup != null){
            try {
                bossGroup.shutdownGracefully().sync();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                workerGroup.shutdownGracefully().sync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            this.bindFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        this.running = false;

    }

}
