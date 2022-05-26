package io.ep2p.kademlia.netty.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.netty.factory.GsonFactory;
import io.ep2p.kademlia.node.DHTKademliaNodeAPI;
import io.ep2p.kademlia.protocol.message.KademliaMessage;

import java.io.Serializable;
import java.math.BigInteger;

public class NettyKademliaMessageHandler<ID extends Number, K extends Serializable, V extends Serializable> extends AbstractKademliaMessageHandler<ID, K, V> {

    public NettyKademliaMessageHandler(DHTKademliaNodeAPI<ID, NettyConnectionInfo, K, V> dhtKademliaNodeAPI) {
        this(new GsonFactory.DefaultGsonFactory().gson(), dhtKademliaNodeAPI);
    }

    public NettyKademliaMessageHandler(Gson gson, DHTKademliaNodeAPI<ID, NettyConnectionInfo, K, V> dhtKademliaNodeAPI) {
        super(gson, dhtKademliaNodeAPI);
    }

    @Override
    protected KademliaMessage<ID, NettyConnectionInfo, Serializable> toKademliaMessage(String message) {
        return this.GSON.fromJson(
                message,
                new TypeToken<KademliaMessage<BigInteger, NettyConnectionInfo, Serializable>>(){}.getType()
        );
    }
}
