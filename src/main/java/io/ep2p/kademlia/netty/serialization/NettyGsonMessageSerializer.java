package io.ep2p.kademlia.netty.serialization;

import com.google.gson.GsonBuilder;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.serialization.gson.GsonMessageSerializer;

import java.io.Serializable;
import java.math.BigInteger;

public class NettyGsonMessageSerializer<K extends Serializable, V extends Serializable> extends GsonMessageSerializer<BigInteger, NettyConnectionInfo, K, V> {
    public NettyGsonMessageSerializer(Class<K> keyClass, Class<V> valueClass) {
        super(BigInteger.class, NettyConnectionInfo.class, keyClass, valueClass);
    }

    public NettyGsonMessageSerializer(GsonBuilder gsonBuilder) {
        super(gsonBuilder);
    }
}
