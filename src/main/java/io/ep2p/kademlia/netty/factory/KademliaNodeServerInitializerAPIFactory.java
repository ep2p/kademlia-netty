package io.ep2p.kademlia.netty.factory;

import io.ep2p.kademlia.netty.server.DefaultKademliaNodeServerInitializer;
import io.ep2p.kademlia.netty.server.KademliaNodeServerInitializer;

import java.io.Serializable;

public class KademliaNodeServerInitializerAPIFactory {
    public <K extends Serializable, V extends Serializable> KademliaNodeServerInitializer<K, V> getKademliaNodeServerInitializerAPI(){
        return new DefaultKademliaNodeServerInitializer<K, V>();
    }
}
