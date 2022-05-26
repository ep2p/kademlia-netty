package io.ep2p.kademlia.netty.server;

import com.google.gson.reflect.TypeToken;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.netty.factory.GsonFactory;
import io.ep2p.kademlia.node.DHTKademliaNodeAPI;
import io.ep2p.kademlia.protocol.message.KademliaMessage;

import java.io.Serializable;
import java.math.BigInteger;

public class NettyKademliaMessageHandler<ID extends Number, K extends Serializable, V extends Serializable> extends AbstractKademliaMessageHandler<ID, K, V> {

    public NettyKademliaMessageHandler(DHTKademliaNodeAPI<ID, NettyConnectionInfo, K, V> dhtKademliaNodeAPI) {
        this(dhtKademliaNodeAPI, new GsonFactory.DefaultGsonFactory());
    }

    public NettyKademliaMessageHandler(DHTKademliaNodeAPI<ID, NettyConnectionInfo, K, V> dhtKademliaNodeAPI, GsonFactory gsonFactory) {
        super(gsonFactory, dhtKademliaNodeAPI);
    }

    @Override
    protected KademliaMessage<ID, NettyConnectionInfo, Serializable> toKademliaMessage(String message) {
        return this.GSON.fromJson(
                message,
                new TypeToken<KademliaMessage<BigInteger, NettyConnectionInfo, Serializable>>(){}.getType()
        );
    }
}
