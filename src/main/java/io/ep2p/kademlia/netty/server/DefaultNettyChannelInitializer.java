package io.ep2p.kademlia.netty.server;

import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import org.jetbrains.annotations.Nullable;

public class DefaultNettyChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final ChannelInboundHandler channelInboundHandler;
    private final SslContext sslCtx;

    public DefaultNettyChannelInitializer(ChannelInboundHandler channelInboundHandler, @Nullable SslContext sslCtx) {
        this.channelInboundHandler = channelInboundHandler;
        this.sslCtx = sslCtx;
    }

    public DefaultNettyChannelInitializer(ChannelInboundHandler channelInboundHandler) {
        this(channelInboundHandler, null);
    }

    @Override
    public void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(socketChannel.alloc()));
        }
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
        pipeline.addLast(this.channelInboundHandler);
    }
}
