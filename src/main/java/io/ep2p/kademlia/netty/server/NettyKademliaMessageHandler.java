package io.ep2p.kademlia.netty.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.netty.factory.GsonFactory;
import io.ep2p.kademlia.node.DHTKademliaNodeAPI;
import io.ep2p.kademlia.protocol.message.KademliaMessage;

import java.io.Serializable;
import java.math.BigInteger;

public class NettyKademliaMessageHandler<K extends Serializable, V extends Serializable> extends AbstractKademliaMessageHandler<K, V> {

    public NettyKademliaMessageHandler(DHTKademliaNodeAPI<BigInteger, NettyConnectionInfo, K, V> dhtKademliaNodeAPI) {
        this(new GsonFactory.DefaultGsonFactory<K, V>().gson(), dhtKademliaNodeAPI);
    }

    public NettyKademliaMessageHandler(Gson gson, DHTKademliaNodeAPI<BigInteger, NettyConnectionInfo, K, V> dhtKademliaNodeAPI) {
        super(gson, dhtKademliaNodeAPI);
    }

    @Override
    protected KademliaMessage<BigInteger, NettyConnectionInfo, Serializable> toKademliaMessage(String message) {
        return this.GSON.fromJson(
                message,
                new TypeToken<KademliaMessage<BigInteger, NettyConnectionInfo, Serializable>>(){}.getType()
        );
    }
}
