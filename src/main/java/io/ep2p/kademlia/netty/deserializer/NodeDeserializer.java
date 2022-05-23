package io.ep2p.kademlia.netty.deserializer;

import com.google.gson.*;
import io.ep2p.kademlia.netty.common.NettyBigIntegerExternalNode;

import java.lang.reflect.Type;

public class NodeDeserializer implements JsonDeserializer<NettyBigIntegerExternalNode> {
    @Override
    public NettyBigIntegerExternalNode deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return new Gson().fromJson(jsonElement.toString(), NettyBigIntegerExternalNode.class);
    }
}
