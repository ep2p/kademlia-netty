package io.ep2p.kademlia.netty.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import io.ep2p.kademlia.netty.common.NettyBigIntegerExternalNode;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.node.Node;
import io.ep2p.kademlia.node.external.BigIntegerExternalNode;
import io.ep2p.kademlia.node.external.ExternalNode;

import java.lang.reflect.Type;
import java.math.BigInteger;

public class ExternalNodeDeserializer implements JsonDeserializer<ExternalNode<BigInteger, NettyConnectionInfo>> {
    @Override
    public ExternalNode<BigInteger, NettyConnectionInfo> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Node<BigInteger, NettyConnectionInfo> node = (Node<BigInteger, NettyConnectionInfo>) jsonDeserializationContext.deserialize(jsonElement, NettyBigIntegerExternalNode.class);
        BigInteger distance = jsonElement.getAsJsonObject().get("distance").getAsBigInteger();

        return new BigIntegerExternalNode<NettyConnectionInfo>(node, distance);
    }
}
