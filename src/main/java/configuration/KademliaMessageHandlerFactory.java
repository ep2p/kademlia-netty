package configuration;

import common.NettyConnectionInfo;
import io.ep2p.kademlia.node.DHTKademliaNodeAPI;
import server.AbstractKademliaMessageHandler;

import java.io.Serializable;
import java.math.BigInteger;

public abstract class KademliaMessageHandlerFactory {
    public abstract <K extends Serializable, V extends Serializable> AbstractKademliaMessageHandler getKademliaMessageHandler(DHTKademliaNodeAPI<BigInteger, NettyConnectionInfo, K, V> dhtKademliaNodeAPI);
}
