package io.ep2p.kademlia.netty.factory;

import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.node.DHTKademliaNodeAPI;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

import java.io.Serializable;

public abstract class KademliaMessageHandlerFactory<ID extends Number, K extends Serializable, V extends Serializable> {
    public abstract SimpleChannelInboundHandler<FullHttpRequest> getKademliaMessageHandler(DHTKademliaNodeAPI<ID, NettyConnectionInfo, K, V> dhtKademliaNodeAPI);
}
