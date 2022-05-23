package io.ep2p.kademlia.netty.configuration;

import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.node.DHTKademliaNodeAPI;
import io.ep2p.kademlia.netty.server.AbstractKademliaMessageHandler;

import java.io.Serializable;
import java.math.BigInteger;

public abstract class KademliaMessageHandlerFactory {
    public abstract <K extends Serializable, V extends Serializable> AbstractKademliaMessageHandler getKademliaMessageHandler(DHTKademliaNodeAPI<BigInteger, NettyConnectionInfo, K, V> dhtKademliaNodeAPI);
}
