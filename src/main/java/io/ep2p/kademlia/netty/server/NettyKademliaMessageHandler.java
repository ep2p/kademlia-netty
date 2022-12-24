package io.ep2p.kademlia.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public interface NettyKademliaMessageHandler {
    void handle(ChannelHandlerContext context, FullHttpRequest request, FullHttpResponse httpResponse);
}
