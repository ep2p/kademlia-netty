package io.ep2p.kademlia.netty.serialization;

import com.google.gson.*;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.netty.common.NettyExternalNode;
import io.ep2p.kademlia.node.Node;

import java.lang.reflect.Type;


public class NodeDeserializer implements JsonDeserializer<Node<Long, NettyConnectionInfo>> {
    @Override
    public Node<Long, NettyConnectionInfo> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        NettyExternalNode nettyExternalNode = new NettyExternalNode();
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        nettyExternalNode.setId(jsonDeserializationContext.deserialize(jsonObject.get("id"), Long.class));
        nettyExternalNode.setConnectionInfo(jsonDeserializationContext.deserialize(jsonObject.get("connectionInfo"), NettyConnectionInfo.class));
        return nettyExternalNode;
    }
}
