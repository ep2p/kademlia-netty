package io.ep2p.kademlia.netty.serialization;

import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.protocol.message.KademliaMessage;

import java.io.Serializable;
import java.math.BigInteger;


public interface MessageSerializer {

    <S extends Serializable> String serialize(KademliaMessage<BigInteger, NettyConnectionInfo, S> message);
    <S extends Serializable> KademliaMessage<BigInteger, NettyConnectionInfo, S> deserialize(String message);

}
