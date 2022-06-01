package io.ep2p.kademlia.netty.factory;

import io.ep2p.kademlia.netty.server.DefaultNettyServerInitializer;
import io.ep2p.kademlia.netty.server.NettyServerInitializer;
import lombok.Getter;

import java.io.Serializable;

public abstract class NettyServerInitializerFactory<K extends Serializable, V extends Serializable> {

    public abstract NettyServerInitializer<K, V> getKademliaNodeServerInitializerAPI();

    public static class DefaultNettyServerInitializerFactory<K extends Serializable, V extends Serializable> extends NettyServerInitializerFactory<K, V> {
        @Getter
        protected final KademliaMessageHandlerFactory<K, V> kademliaMessageHandlerFactory;

        public DefaultNettyServerInitializerFactory(KademliaMessageHandlerFactory<K, V> kademliaMessageHandlerFactory) {
            this.kademliaMessageHandlerFactory = kademliaMessageHandlerFactory;
        }

        public NettyServerInitializer<K, V> getKademliaNodeServerInitializerAPI(){
            return new DefaultNettyServerInitializer<K, V>(
                    this.kademliaMessageHandlerFactory
            );
        }
    }
}
