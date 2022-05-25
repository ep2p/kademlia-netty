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
import lombok.Getter;

import java.io.Serializable;
import java.math.BigInteger;


@Getter
public class KademliaNodeServer<K extends Serializable, V extends Serializable> {

    private final int port;
    private final String host;
    private final KademliaNodeServerInitializerAPIFactory kademliaNodeServerInitializerAPIFactory;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

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

    public synchronized void run(DHTKademliaNodeAPI<BigInteger, NettyConnectionInfo, K, V> dhtKademliaNodeAPI) throws InterruptedException {
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();

        KademliaNodeServerInitializer<K, V> kademliaNodeServerInitializer = kademliaNodeServerInitializerAPIFactory.getKademliaNodeServerInitializerAPI();
        kademliaNodeServerInitializer.registerKademliaNode(dhtKademliaNodeAPI);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(kademliaNodeServerInitializer)
                    .option(ChannelOption.SO_BACKLOG, 128);

            ChannelFuture bind = host != null ? bootstrap.bind(host, port) : bootstrap.bind(port);
            bind.sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public synchronized void stop(){
        if (bossGroup != null && workerGroup != null){
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
