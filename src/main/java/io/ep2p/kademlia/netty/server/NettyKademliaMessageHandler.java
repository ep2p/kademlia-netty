package io.ep2p.kademlia.netty.server;

import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.node.DHTKademliaNodeAPI;
import io.ep2p.kademlia.protocol.message.KademliaMessage;

import java.io.Serializable;
import java.math.BigInteger;

public class NettyKademliaMessageHandler<K extends Serializable, V extends Serializable> extends AbstractKademliaMessageHandler {


    public NettyKademliaMessageHandler(DHTKademliaNodeAPI<BigInteger, NettyConnectionInfo, K, V> dhtKademliaNodeAPI) {
        super(dhtKademliaNodeAPI);
    }

    @Override
    protected KademliaMessage<BigInteger, NettyConnectionInfo, Serializable> toKademliaMessage(String message) {

        return null;
    }
}
