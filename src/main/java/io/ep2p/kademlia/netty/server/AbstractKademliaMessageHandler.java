package io.ep2p.kademlia.netty.server;

import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.node.DHTKademliaNodeAPI;
import io.ep2p.kademlia.protocol.message.KademliaMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.Serializable;
import java.math.BigInteger;

public abstract class AbstractKademliaMessageHandler extends SimpleChannelInboundHandler<String> {
    private final DHTKademliaNodeAPI<BigInteger, NettyConnectionInfo, ?, ?> dhtKademliaNodeAPI;

    public AbstractKademliaMessageHandler(DHTKademliaNodeAPI<BigInteger, NettyConnectionInfo, ?, ?> dhtKademliaNodeAPI) {
        this.dhtKademliaNodeAPI = dhtKademliaNodeAPI;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String jsonStr) throws Exception {
        this.dhtKademliaNodeAPI.onMessage(this.toKademliaMessage(jsonStr));
    }

    protected abstract KademliaMessage<BigInteger, NettyConnectionInfo, Serializable> toKademliaMessage(String message);
}
