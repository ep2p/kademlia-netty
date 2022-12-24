package io.ep2p.kademlia.netty.factory;

import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.netty.server.DefaultNettyKademliaMessageHandler;
import io.ep2p.kademlia.netty.server.NettyKademliaMessageHandler;
import io.ep2p.kademlia.node.DHTKademliaNodeAPI;
import io.ep2p.kademlia.serialization.api.MessageSerializer;

import java.io.Serializable;
import java.math.BigInteger;

public interface NettyKademliaMessageHandlerFactory<K extends Serializable, V extends Serializable> {
    NettyKademliaMessageHandler getNettyKademliaMessageHandler(DHTKademliaNodeAPI<BigInteger, NettyConnectionInfo, K, V> dhtKademliaNodeAPI);

    class DefaultNettyKademliaMessageHandlerFactory<K extends Serializable, V extends Serializable> implements NettyKademliaMessageHandlerFactory<K, V> {
        private final MessageSerializer<BigInteger, NettyConnectionInfo> messageSerializer;

        public DefaultNettyKademliaMessageHandlerFactory(MessageSerializer<BigInteger, NettyConnectionInfo> messageSerializer) {
            this.messageSerializer = messageSerializer;
        }

        @Override
        public NettyKademliaMessageHandler getNettyKademliaMessageHandler(DHTKademliaNodeAPI<BigInteger, NettyConnectionInfo, K, V> dhtKademliaNodeAPI) {
            return new DefaultNettyKademliaMessageHandler<>(dhtKademliaNodeAPI, messageSerializer);
        }
    }
}
