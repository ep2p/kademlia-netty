package io.ep2p.kademlia.netty.builder;

import io.ep2p.kademlia.netty.factory.NettyKademliaMessageHandlerFactory;

import java.io.Serializable;

public interface NettyKademliaMessageHandlerFactoryProvider {
    <K extends Serializable, V extends Serializable> NettyKademliaMessageHandlerFactory<K, V> getNettyKademliaMessageHandlerFactory(NettyKademliaDHTNodeBuilder<K, V> nettyKademliaDHTNodeBuilder);

    class DefaultNettyKademliaMessageHandlerFactoryProvider implements NettyKademliaMessageHandlerFactoryProvider {

        @Override
        public <K extends Serializable, V extends Serializable> NettyKademliaMessageHandlerFactory<K, V> getNettyKademliaMessageHandlerFactory(NettyKademliaDHTNodeBuilder<K, V> nettyKademliaDHTNodeBuilder) {
            return new NettyKademliaMessageHandlerFactory.DefaultNettyKademliaMessageHandlerFactory<>(nettyKademliaDHTNodeBuilder.getMessageSerializer());
        }
    }
}
