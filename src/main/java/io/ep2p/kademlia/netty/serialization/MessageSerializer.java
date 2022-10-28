package io.ep2p.kademlia.netty.serialization;

import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.protocol.message.KademliaMessage;

import java.io.Serializable;


public interface MessageSerializer {

    <S extends Serializable> String serialize(KademliaMessage<Long, NettyConnectionInfo, S> message);
    <S extends Serializable> KademliaMessage<Long, NettyConnectionInfo, S> deserialize(String message);

}
