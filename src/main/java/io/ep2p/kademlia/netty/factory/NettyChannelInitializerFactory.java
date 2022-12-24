package io.ep2p.kademlia.netty.factory;

import io.ep2p.kademlia.netty.server.DefaultNettyChannelInitializer;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;


public interface NettyChannelInitializerFactory {
    ChannelInitializer<SocketChannel> getChannelChannelInitializer(ChannelInboundHandler channelInboundHandler);

    class DefaultNettyChannelInitializerFactory implements NettyChannelInitializerFactory {

        @Override
        public ChannelInitializer<SocketChannel> getChannelChannelInitializer(ChannelInboundHandler channelInboundHandler) {
            return new DefaultNettyChannelInitializer(channelInboundHandler);
        }
    }
}
