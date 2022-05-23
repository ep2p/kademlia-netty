package io.ep2p.kademlia.netty.factory;

import io.ep2p.kademlia.netty.server.DefaultKademliaNodeServerInitializer;
import io.ep2p.kademlia.netty.server.KademliaNodeServerInitializerAPI;

import java.io.Serializable;

public class KademliaNodeServerInitializerAPIFactory {
    public <K extends Serializable, V extends Serializable> KademliaNodeServerInitializerAPI<K, V> getKademliaNodeServerInitializerAPI(){
        return new DefaultKademliaNodeServerInitializer<K, V>();
    }
}
