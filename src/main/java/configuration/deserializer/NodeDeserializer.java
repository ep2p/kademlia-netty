package configuration.deserializer;

import com.google.gson.*;
import common.NettyBigIntegerExternalNode;

import java.lang.reflect.Type;

public class NodeDeserializer implements JsonDeserializer<NettyBigIntegerExternalNode> {
    @Override
    public NettyBigIntegerExternalNode deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return new Gson().fromJson(jsonElement.toString(), NettyBigIntegerExternalNode.class);
    }
}
