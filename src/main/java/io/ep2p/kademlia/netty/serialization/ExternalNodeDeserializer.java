package io.ep2p.kademlia.netty.serialization;

import com.google.gson.*;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.node.Node;
import io.ep2p.kademlia.node.external.ExternalNode;
import io.ep2p.kademlia.node.external.LongExternalNode;

import java.lang.reflect.Type;


public class ExternalNodeDeserializer implements JsonDeserializer<ExternalNode<Long, NettyConnectionInfo>> {
    @Override
    public ExternalNode<Long, NettyConnectionInfo> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Long distance = jsonObject.get("distance").getAsLong();
        jsonObject.remove("distance");
        Node<Long, NettyConnectionInfo> node = null;
        if (jsonObject.has("node")){
            node = jsonDeserializationContext.deserialize(
                    jsonObject.get("node"),
                    Node.class
            );
        }else {
            node = jsonDeserializationContext.deserialize(
                    jsonObject,
                    Node.class
            );
        }
        return new LongExternalNode<>(node, distance);
    }
}
