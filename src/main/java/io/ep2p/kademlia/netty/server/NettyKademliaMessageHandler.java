package io.ep2p.kademlia.netty.server;

import com.google.gson.reflect.TypeToken;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.netty.factory.GsonFactory;
import io.ep2p.kademlia.node.DHTKademliaNodeAPI;
import io.ep2p.kademlia.protocol.message.KademliaMessage;

import java.io.Serializable;
import java.math.BigInteger;

public class NettyKademliaMessageHandler<K extends Serializable, V extends Serializable> extends AbstractKademliaMessageHandler {
    private final GsonFactory gsonFactory;

    public NettyKademliaMessageHandler(DHTKademliaNodeAPI<BigInteger, NettyConnectionInfo, K, V> dhtKademliaNodeAPI) {
        this(dhtKademliaNodeAPI, new GsonFactory.DefaultGsonFactory());
    }

    public NettyKademliaMessageHandler(DHTKademliaNodeAPI<BigInteger, NettyConnectionInfo, K, V> dhtKademliaNodeAPI, GsonFactory gsonFactory) {
        super(dhtKademliaNodeAPI);
        this.gsonFactory = gsonFactory;
    }

    @Override
    protected KademliaMessage<BigInteger, NettyConnectionInfo, Serializable> toKademliaMessage(String message) {
        return gsonFactory.gson().fromJson(
                message,
                new TypeToken<KademliaMessage<BigInteger, NettyConnectionInfo, Serializable>>() {}.getType()
        );
    }
}
