package io.ep2p.kademlia.netty.configuration;

import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.netty.server.AbstractKademliaMessageHandler;
import io.ep2p.kademlia.node.DHTKademliaNodeAPI;

import java.io.Serializable;
import java.math.BigInteger;

public abstract class KademliaMessageHandlerFactory<K extends Serializable, V extends Serializable> {
    public abstract AbstractKademliaMessageHandler getKademliaMessageHandler(DHTKademliaNodeAPI<BigInteger, NettyConnectionInfo, K, V> dhtKademliaNodeAPI);
}
