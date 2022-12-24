package io.ep2p.kademlia.netty.server;

import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class DefaultNettyChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final ChannelInboundHandler channelInboundHandler;

    public DefaultNettyChannelInitializer(ChannelInboundHandler channelInboundHandler) {
        this.channelInboundHandler = channelInboundHandler;
    }

    @Override
    public void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
        pipeline.addLast(this.channelInboundHandler);
    }
}
