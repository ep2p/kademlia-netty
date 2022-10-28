package io.ep2p.kademlia.netty.serialization;

import com.google.gson.*;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.netty.common.NettyExternalNode;
import io.ep2p.kademlia.node.Node;

import java.lang.reflect.Type;
import java.math.BigInteger;

public class NodeDeserializer implements JsonDeserializer<Node<BigInteger, NettyConnectionInfo>> {
    @Override
    public Node<BigInteger, NettyConnectionInfo> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        NettyExternalNode nettyExternalNode = new NettyExternalNode();
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        nettyExternalNode.setId(jsonDeserializationContext.deserialize(jsonObject.get("id"), BigInteger.class));
        nettyExternalNode.setConnectionInfo(jsonDeserializationContext.deserialize(jsonObject.get("connectionInfo"), NettyConnectionInfo.class));
        return nettyExternalNode;
    }
}
