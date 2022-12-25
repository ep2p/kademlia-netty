package io.ep2p.kademlia.netty.factory;

import io.ep2p.kademlia.netty.server.DefaultNettyChannelInitializer;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import org.jetbrains.annotations.Nullable;


public interface NettyChannelInitializerFactory {
    ChannelInitializer<SocketChannel> getChannelChannelInitializer(ChannelInboundHandler channelInboundHandler);

    class DefaultNettyChannelInitializerFactory implements NettyChannelInitializerFactory {

        private final SslContext sslContext;

        public DefaultNettyChannelInitializerFactory() {
            this(null);
        }

        public DefaultNettyChannelInitializerFactory(@Nullable SslContext sslContext) {
            this.sslContext = sslContext;
        }

        @Override
        public ChannelInitializer<SocketChannel> getChannelChannelInitializer(ChannelInboundHandler channelInboundHandler) {
            return new DefaultNettyChannelInitializer(channelInboundHandler, sslContext);
        }
    }
}
