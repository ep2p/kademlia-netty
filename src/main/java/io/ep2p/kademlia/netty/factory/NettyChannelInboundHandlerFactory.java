package io.ep2p.kademlia.netty.factory;

import io.ep2p.kademlia.netty.server.NettyKademliaChannelInboundHandler;
import io.ep2p.kademlia.netty.server.NettyKademliaMessageHandler;
import io.netty.channel.ChannelInboundHandler;

public interface NettyChannelInboundHandlerFactory {
    ChannelInboundHandler getChannelInboundHandler(NettyKademliaMessageHandler nettyKademliaMessageHandler);

    class DefaultNettyChannelInboundHandlerFactory implements NettyChannelInboundHandlerFactory {

        @Override
        public ChannelInboundHandler getChannelInboundHandler(NettyKademliaMessageHandler nettyKademliaMessageHandler) {
            return new NettyKademliaChannelInboundHandler<>(nettyKademliaMessageHandler);
        }
    }

}
