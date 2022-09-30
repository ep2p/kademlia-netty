package io.ep2p.kademlia.netty.factory;

import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.netty.server.NettyKademliaMessageHandler;
import io.ep2p.kademlia.netty.server.filter.NettyKademliaServerFilterChain;
import io.ep2p.kademlia.node.DHTKademliaNodeAPI;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

import java.io.Serializable;
import java.math.BigInteger;

public interface KademliaMessageHandlerFactory<K extends Serializable, V extends Serializable> {
    SimpleChannelInboundHandler<FullHttpRequest> getKademliaMessageHandler(DHTKademliaNodeAPI<BigInteger, NettyConnectionInfo, K, V> dhtKademliaNodeAPI);

    class DefaultKademliaMessageHandlerFactory<K extends Serializable, V extends Serializable> implements KademliaMessageHandlerFactory<K, V> {
        private final NettyKademliaServerFilterChain<K, V> filterChain;

        public DefaultKademliaMessageHandlerFactory(NettyKademliaServerFilterChain<K, V> filterChain) {
            this.filterChain = filterChain;
        }

        @Override
        public SimpleChannelInboundHandler<FullHttpRequest> getKademliaMessageHandler(DHTKademliaNodeAPI<BigInteger, NettyConnectionInfo, K, V> dhtKademliaNodeAPI) {
            return new NettyKademliaMessageHandler<>(dhtKademliaNodeAPI, this.filterChain);
        }

        public NettyKademliaServerFilterChain<K, V> getFilterChain() {
            return filterChain;
        }
    }
}
