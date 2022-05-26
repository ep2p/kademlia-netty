package io.ep2p.kademlia.netty.factory;

import io.ep2p.kademlia.netty.server.DefaultKademliaNodeServerInitializer;
import io.ep2p.kademlia.netty.server.KademliaNodeServerInitializer;

import java.io.Serializable;

public class KademliaNodeServerInitializerAPIFactory {
    public <ID extends Number, K extends Serializable, V extends Serializable> KademliaNodeServerInitializer<ID, K, V> getKademliaNodeServerInitializerAPI(){
        return new DefaultKademliaNodeServerInitializer<ID, K, V>();
    }
}
